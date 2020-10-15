package es.us.isa.restest.searchbased.operators;

import es.us.isa.restest.searchbased.RestfulAPITestSuiteSolution;
import es.us.isa.restest.testcases.TestCase;
import es.us.isa.restest.testcases.TestResult;
import org.apache.commons.lang3.RandomUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.uma.jmetal.util.pseudorandom.BoundedRandomGenerator;
import org.uma.jmetal.util.pseudorandom.RandomGenerator;

import java.util.*;

import static es.us.isa.restest.searchbased.operators.Utils.resetTestResult;
import static es.us.isa.restest.searchbased.operators.Utils.updateTestCaseFaultyReason;

public class UniformTestCaseCrossover extends AbstractCrossoverOperator {

	private static final Logger logger = LogManager.getLogger(UniformTestCaseCrossover.class.getName());

	    private boolean mutationApplied;

	    public UniformTestCaseCrossover(double crossoverProbability) {
	        super(crossoverProbability);
	    }

	    public UniformTestCaseCrossover(double crossoverProbability, RandomGenerator<Double> randomGenerator) {
	        super(crossoverProbability, randomGenerator);
	    }

	    public UniformTestCaseCrossover(
				double crossoverProbability,
				RandomGenerator<Double> crossoverRandomGenerator,
				BoundedRandomGenerator<Integer> pointRandomGenerator) {
	        super(crossoverProbability, crossoverRandomGenerator, pointRandomGenerator);
	    }

	@Override
	protected List<RestfulAPITestSuiteSolution> doCrossover(double probability,
			RestfulAPITestSuiteSolution parent1,
			RestfulAPITestSuiteSolution parent2) {
		List<RestfulAPITestSuiteSolution> offspring = new ArrayList<>(2);
        RestfulAPITestSuiteSolution offspring1=(RestfulAPITestSuiteSolution) parent1.copy();
        RestfulAPITestSuiteSolution offspring2=(RestfulAPITestSuiteSolution) parent2.copy();
        offspring.add(offspring1);
        offspring.add(offspring2);
    
      // 1. We choose randomly the cases for the parameter crossover:
    	int parent1TestCaseIndex=pointRandomGenerator.getRandomValue(0, parent1.getVariables().size()-1);
    	int parent2TestCaseIndex=pointRandomGenerator.getRandomValue(0, parent2.getVariables().size()-1);
    	TestCase testCase1=offspring1.getVariable(parent1TestCaseIndex);
    	TestCase testCase2=offspring2.getVariable(parent2TestCaseIndex);
    	// Crossover is applied only between testcases of the same operation: 
    	if(testCase1.getOperationId().equals(testCase2.getOperationId())) {
			// 2. 3. Apply the crossover:
			mutationApplied = false;
			doCrossover(probability, testCase1, testCase2);
			if (mutationApplied) {
				logger.info("Crossover probability fulfilled! Two test CASES have been crossed over.");
				updateTestCaseFaultyReason(parent1, testCase1);
				updateTestCaseFaultyReason(parent2, testCase2);
				resetTestResult(testCase1.getId(), offspring1); // The test case changed, reset test result
				resetTestResult(testCase2.getId(), offspring2); // The test case changed, reset test result
			}
		}

    	return offspring;

	}

	private void doCrossover(double probability, TestCase testCase1, TestCase testCase2) {
		if(crossoverRandomGenerator.getRandomValue() < probability) {
			List<String> possibleCrossovers = Arrays.asList("query", "header", "form", "path", "body");
			Collections.shuffle(possibleCrossovers);

			int index = 0;
			while (index < possibleCrossovers.size() && !mutationApplied) {
				switch (possibleCrossovers.get(index)) {
					case "query":
						if (!testCase1.getQueryParameters().isEmpty() || !testCase2.getQueryParameters().isEmpty()) {
							doQueryCrossover(probability, testCase1, testCase2);
							mutationApplied = true;
						}
						break;
					case "header":
						if (!testCase1.getHeaderParameters().isEmpty() || !testCase2.getHeaderParameters().isEmpty()) {
							doHeadersCrossover(probability, testCase1, testCase2);
							mutationApplied = true;
						}
						break;
					case "form":
						if (!testCase1.getFormParameters().isEmpty() || !testCase2.getFormParameters().isEmpty()) {
							doFormCrossover(probability, testCase1, testCase2);
							mutationApplied = true;
						}
						break;
					case "path":
						if (!testCase1.getPathParameters().isEmpty() || !testCase2.getPathParameters().isEmpty()) {
							doPathCrossover(probability, testCase1, testCase2);
							mutationApplied = true;
						}
						break;
					case "body":
						if ((testCase1.getBodyParameter() != null && !"".equals(testCase1.getBodyParameter())) || (testCase2.getBodyParameter() != null && !"".equals(testCase2.getBodyParameter()))) {
							doBodyCrossover(probability, testCase1, testCase2);
							mutationApplied = true;
						}
						break;
					default:
				}
				index++;
			}
		}
	}

	private void doFormCrossover(double probability, TestCase testCase1, TestCase testCase2) {		
		doCrossover(probability,testCase1.getFormParameters(),testCase2.getFormParameters());
	}

	private void doBodyCrossover(double probability, TestCase testCase1, TestCase testCase2) {
		String body1=testCase1.getBodyParameter();
		testCase1.setBodyParameter(testCase2.getBodyParameter());
		testCase2.setBodyParameter(body1);
	}

	private void doQueryCrossover(double probability, TestCase testCase1, TestCase testCase2) {
		doCrossover(probability,testCase1.getQueryParameters(),testCase2.getQueryParameters());
	}

	private void doCrossover(double probability,Map<String,String> parameters1,Map<String,String> parameters2) {
		// 1. Get the total number of params
		int totalNumberOfVars= Math.min(parameters1.size(),parameters2.size());

		// 2. Calculate the point to make the crossover
		int crossoverPoint = pointRandomGenerator.getRandomValue(0, totalNumberOfVars - 1);

		// 3. Apply the crossover to the parameters;
		for(int i=0;i<=crossoverPoint;i++)
			doCrossover(parameters1.keySet().toArray(new String[0])[i], parameters1, parameters2);
	}
	
	private void doCrossover(String param, Map<String, String> parameters1, Map<String, String> parameters2) {
		String value=parameters1.get(param);
		if(parameters2.containsKey(param)) 
			parameters1.put(param,parameters2.get(param));
		else
			parameters1.remove(param);
		parameters2.put(param, value);
	}

	private void doPathCrossover(double probability, TestCase testCase1, TestCase testCase2) {
		doCrossover(probability,testCase1.getPathParameters(),testCase2.getPathParameters());		
	}

	private void doHeadersCrossover(double probability, TestCase testCase1, TestCase testCase2) {
		doCrossover(probability,testCase1.getHeaderParameters(),testCase2.getHeaderParameters());
		
	}

}
