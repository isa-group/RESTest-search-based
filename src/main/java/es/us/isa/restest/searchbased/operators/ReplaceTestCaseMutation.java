package es.us.isa.restest.searchbased.operators;

import es.us.isa.restest.searchbased.RestfulAPITestSuiteSolution;
import es.us.isa.restest.testcases.TestCase;
import es.us.isa.restest.util.RESTestException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.uma.jmetal.util.pseudorandom.PseudoRandomGenerator;

import java.util.List;

public class ReplaceTestCaseMutation extends AbstractMutationOperator {

	private static final Logger logger = LogManager.getLogger(ReplaceTestCaseMutation.class.getName());

	public ReplaceTestCaseMutation(double mutationProbability, PseudoRandomGenerator randomGenerator) {
		super(mutationProbability, randomGenerator);		
	}

	protected void doMutation(double mutationProbability, RestfulAPITestSuiteSolution solution) throws RESTestException {
		if(getRandomGenerator().nextDouble() <= mutationProbability) {
			int replacedTestCaseIndex = getRandomGenerator().nextInt(0, solution.getVariables().size()-1);
			TestCase replacedTestCase = solution.getVariable(replacedTestCaseIndex);
			TestCase insertedTestCase = solution.getProblem().createRandomTestCase();
			solution.setVariable(replacedTestCaseIndex, insertedTestCase);
			solution.removeTestResult(replacedTestCase.getId());
			solution.setTestResult(insertedTestCase.getId(), null);
			logger.info("Mutation probability fulfilled! Test case replaced in test suite.");
		}
		
	}

}
