package es.us.isa.restest.searchbased.objectivefunction;

import es.us.isa.restest.searchbased.RestfulAPITestSuiteSolution;
import es.us.isa.restest.testcases.TestCase;
import es.us.isa.restest.testcases.TestResult;
import org.javatuples.Triplet;

import java.util.List;

/**
 * This function maximizes the globalDiversity of a specific property of several
 * elements. Three modes are possible:<br>
 *     1.- Diversity among inputs (HTTP requests).<br>
 *     2.- Diversity among outputs (HTTP responses).<br>
 *     3.- Diversity among failures (based on the response body).<br>
 *
 * globalDiversity is computed as follows: 1) the number of unique pairs
 * operationId-statusCode is counted, and this number is added to globalDiversity;
 * 2) for those pairs whose operationId is the same, diversity (1 - similarity) is
 * computed and added to globalDiversity; 3) globalDiversity is divided by the total
 * number of elements.
 *
 * @author Alberto Martin-Lopez
 */
public class Diversity extends RestfulAPITestingObjectiveFunction {

    private SimilarityMeter similarityMeter;
    private Element elementType;
    private boolean normalize; // Diversity measured in [0,1], to avoid bias due to test suite size

    public Diversity(SimilarityMeter.METRIC similarityMetric, Element element, boolean normalize) {
        super(ObjectiveFunctionType.MAXIMIZATION,element != Element.INPUT,element == Element.FAILURE);
        elementType = element;
        similarityMeter = new SimilarityMeter(similarityMetric);
        this.normalize = normalize;
    }

    @Override
    public Double evaluate(RestfulAPITestSuiteSolution solution) {
        double globalDiversity = 0;
        switch (elementType){
            case FAILURE:
                List<Triplet<String, String, String>> failures = new UniqueElements(Element.FAILURE, false).getFailures(solution); // operationId, statusCode, responseBody
//                globalDiversity += failures.stream().map(f -> Pair.with(f.getValue0(), f.getValue1())).distinct().count(); // Unique operationId-statusCodes count +1 each
                for (int i=0; i < failures.size(); i++) {
                    Triplet<String, String, String> failure_i = failures.get(i);
                    for (int j=i+1; j < failures.size(); j++) {
                        Triplet<String, String, String> failure_j = failures.get(j);
                        if (failure_i.getValue0().equals(failure_j.getValue0()) && failure_i.getValue1().equals(failure_j.getValue1()))
                            globalDiversity += 1 - similarityMeter.apply(failure_i.getValue2(), failure_j.getValue2());
                        else
                            globalDiversity += 1; // Distinct pairs operationId-statusCode count +1 each
                    }
                }
                break;
            case INPUT:
//                globalDiversity += solution.getVariables().stream().map(TestCase::getOperationId).distinct().count();
                for (int i=0; i < solution.getVariables().size(); i++) {
                    TestCase testCase_i = solution.getVariables().get(i);
                    for (int j=i+1; j < solution.getVariables().size(); j++) {
                        TestCase testCase_j = solution.getVariables().get(j);
                        if (testCase_i.getOperationId().equals(testCase_j.getOperationId()))
                            globalDiversity += 1 - similarityMeter.apply(testCase_i.getFlatRepresentation(), testCase_j.getFlatRepresentation());
                        else
                            globalDiversity += 1; // Distinct operations count +1 each
                    }
                }
                break;
            case OUTPUT:
//                globalDiversity += solution.getVariables().stream().map(TestCase::getOperationId).distinct().count();
                for (int i=0; i < solution.getTestResults().size(); i++) {
                    TestCase testCase_i = solution.getVariables().get(i);
                    TestResult testResult_i = solution.getTestResult(testCase_i.getId());
                    for (int j=i+1; j < solution.getTestResults().size(); j++) {
                        TestCase testCase_j = solution.getVariables().get(j);
                        TestResult testResult_j = solution.getTestResult(testCase_j.getId());
                        if (testCase_i.getOperationId().equals(testCase_j.getOperationId()) && testResult_i.getStatusCode().equals(testResult_j.getStatusCode()))
                            globalDiversity += 1 - similarityMeter.apply(testResult_i.getFlatRepresentation(), testResult_j.getFlatRepresentation());
                        else
                            globalDiversity += 1; // Distinct pairs operationId-statusCode count +1 each
                    }
                }
                break;
            default:
                throw new IllegalArgumentException("The element type " + elementType + " is not supported");
        }
        if (normalize)
            globalDiversity /= ((double) (solution.getNumberOfVariables() * (solution.getNumberOfVariables() - 1)) / 2);

        logEvaluation(globalDiversity);
        return globalDiversity;
    }
}
