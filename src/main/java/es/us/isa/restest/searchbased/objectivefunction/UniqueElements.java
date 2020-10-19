package es.us.isa.restest.searchbased.objectivefunction;

import es.us.isa.restest.searchbased.RestfulAPITestSuiteSolution;
import es.us.isa.restest.testcases.TestCase;
import es.us.isa.restest.testcases.TestResult;
import org.javatuples.Pair;
import org.javatuples.Triplet;
import org.javatuples.Quartet;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This function maximizes the total number of different elements in the
 * test suite. Three types of elements are possible:<br>
 *  *     1.- Inputs (HTTP requests).<br>
 *  *     2.- Outputs (HTTP responses).<br>
 *  *     3.- Failures (based on the response body).<br>
 *
 * Two different modes are possible:<br>
 *     1.- Count all elements that are unique.<br>
 *     2.- Count elements that are different enough from the others
 *         (similarityThreshold).<br>
 *
 * @author Alberto Martin-Lopez
 */
public class UniqueElements extends RestfulAPITestingObjectiveFunction {

    private Element elementType;
    private double similarityThreshold = 1; // Used to compare how different elements are. The lower, the more diverse elements will be
    private SimilarityMeter similarityMeter = null;
    private boolean normalize; // UniqueElements measured in [0,1], to avoid bias due to test suite size

    // Mode 1
    public UniqueElements(Element element, boolean normalize) {
        super(ObjectiveFunctionType.MAXIMIZATION,element != Element.INPUT,element == Element.FAILURE);
        elementType = element;
        this.normalize = normalize;
    }

    // Mode 2
    public UniqueElements(Element element, SimilarityMeter.METRIC similarityMetric, double similarityThreshold, boolean normalize) {
        super(ObjectiveFunctionType.MAXIMIZATION,element != Element.INPUT,element == Element.FAILURE);
        similarityMeter = new SimilarityMeter(similarityMetric);
        assert(similarityThreshold < 1);
        this.similarityThreshold = similarityThreshold;
        elementType = element;
        this.normalize = normalize;
    }

    @Override
    public Double evaluate(RestfulAPITestSuiteSolution solution) {
        double numOfElements = 0;
        switch (elementType) {
            case FAILURE:
                numOfElements = getFailures(solution).size();
                break;
            case INPUT:
                numOfElements = getInputs(solution).size();
                break;
            case OUTPUT:
                numOfElements = getOutputs(solution).size();
                break;
            default:
                throw new IllegalArgumentException("The element type " + elementType + " is not supported");
        }
        if (normalize)
            numOfElements /= solution.getNumberOfVariables();

        logEvaluation(numOfElements);
        return numOfElements;
    }

    public List<Triplet<String, String, String>> getFailures(RestfulAPITestSuiteSolution solution) {
        List<Triplet<String, String, String>> failures = new ArrayList<>(); // operationId, statusCode, responseBody
        for (TestCase testCase: solution.getVariables()) {
            TestResult testResult = solution.getTestResult(testCase.getId());
            if (testResult.getPassed() != null && !testResult.getPassed()) {
                Triplet<String, String, String> failure = Triplet.with(testCase.getOperationId(), testResult.getStatusCode(), testResult.getResponseBody());
                if ((similarityThreshold == 1 && !failures.contains(failure))   // Mode 1: If similarity is disabled, just add all failures whose body is unique
                        ||                                                      // OR
                    (similarityThreshold < 1 &&                                 // Mode 2: if similarity is enabled
                        failures.stream().noneMatch(f ->
                        (f.getValue0().equals(failure.getValue0()) &&               // add failures in a new operation with a new status code
                        f.getValue1().equals(failure.getValue1()))
                            &&                                                      // OR in same operation/status code, but different enough
                        similarityMeter.apply(f.getValue2(), failure.getValue2()) > similarityThreshold)))
                    failures.add(failure);
            }
        }

        return failures;
    }

    public List<Pair<String, TestCase>> getInputs(RestfulAPITestSuiteSolution solution) {
        List<Pair<String, TestCase>> inputs = new ArrayList<>();
        for (TestCase testCase: solution.getVariables()) {
            Pair<String, TestCase> input = Pair.with(testCase.getFlatRepresentation(), testCase);
            // Mode 1: If similarity is disabled, just add all inputs that are unique:
            if ((similarityThreshold == 1 && !inputs.stream().map(Pair::getValue0).collect(Collectors.toList()).contains(input.getValue0()))
                    ||                                                      // OR
                (similarityThreshold < 1 &&                                 // Mode 2: if similarity is enabled
                    inputs.stream().noneMatch(i ->
                    (i.getValue1().getOperationId().equals(input.getValue1().getOperationId())) // add inputs in a new operation
                        &&                                                      // OR in same operation, but different enough
                    similarityMeter.apply(i.getValue0(), input.getValue0()) > similarityThreshold)))
                inputs.add(input);
        }

        return inputs;
    }

    private List<Quartet<String, String, String, String>> getOutputs(RestfulAPITestSuiteSolution solution) {
        List<Quartet<String, String, String, String>> outputs = new ArrayList<>(); // operationId, statusCode, contentType responseBody
        for (TestCase testCase: solution.getVariables()) {
            TestResult testResult = solution.getTestResult(testCase.getId());
            Quartet<String, String, String, String> output = Quartet.with(testCase.getOperationId(), testResult.getStatusCode(), testResult.getOutputFormat(), testResult.getResponseBody());
            if ((similarityThreshold == 1 && !outputs.contains(output))   // Mode 1: If similarity is disabled, just add all outputs that are unique
                    ||                                                      // OR
                (similarityThreshold < 1 &&                                 // Mode 2: if similarity is enabled
                    outputs.stream().noneMatch(o ->
                    (o.getValue0().equals(output.getValue0()) &&               // add outputs in a new operation with a new status code
                    o.getValue1().equals(output.getValue1()))
                        &&                                                      // OR in same operation/status code, but different enough
                    similarityMeter.apply(o.getValue2(), output.getValue2()) > similarityThreshold)))
                outputs.add(output);
        }

        return outputs;
    }

    public String toString() {
        return getClass().getSimpleName() + " - "
                + similarityMeter.getSimilarityMetric() + ", "
                + similarityThreshold + ", "
                + elementType + ", "
                + normalize;
    }

}
