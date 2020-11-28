package es.us.isa.restest.searchbased.operators;

import es.us.isa.restest.searchbased.RestfulAPITestSuiteSolution;
import es.us.isa.restest.testcases.TestCase;
import es.us.isa.restest.util.RESTestException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.uma.jmetal.util.pseudorandom.PseudoRandomGenerator;

public class AddTestCaseMutation extends AbstractMutationOperator {

	private static final long serialVersionUID = -5748485902107048811L;

	private static final Logger logger = LogManager.getLogger(AddTestCaseMutation.class.getName());

	public AddTestCaseMutation(double mutationProbability) {super(mutationProbability);}
	
	public AddTestCaseMutation(double mutationProbability, PseudoRandomGenerator randomGenerator) {
		super(mutationProbability, randomGenerator);
	}

	@Override
	protected void doMutation(double mutationProbability, RestfulAPITestSuiteSolution solution) throws RESTestException {
		// If we are solving a fixed suite size problem, we perform no mutation 
		if(solution.getProblem().getFixedTestSuiteSize()!=null)
			return;
		// If the size of the suite is not maximal
		if(solution.getVariables().size()<solution.getProblem().getMaxTestSuiteSize() && getRandomGenerator().nextDouble() <= mutationProbability) {
			// We add one random test case to the suite:
			TestCase newTestCase = solution.getProblem().createRandomTestCase();
			solution.addVariable(newTestCase);
			solution.setTestResult(newTestCase.getId(), null);
			logger.info("Mutation probability fulfilled! Test case added to test suite.");
		}
	}
		

}
