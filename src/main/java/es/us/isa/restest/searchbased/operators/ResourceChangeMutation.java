package es.us.isa.restest.searchbased.operators;

import es.us.isa.restest.searchbased.RestfulAPITestSuiteSolution;
import es.us.isa.restest.testcases.TestCase;
import org.uma.jmetal.util.pseudorandom.PseudoRandomGenerator;

import static es.us.isa.restest.util.SolutionUtils.resetTestResult;

public class ResourceChangeMutation extends AbstractMutationOperator {

	private static final long serialVersionUID = -8129547472315561106L;

	public ResourceChangeMutation(double mutationProbability) {
		super(mutationProbability);
	}

	public ResourceChangeMutation(double mutationProbability, PseudoRandomGenerator randomGenerator) {
		super(mutationProbability, randomGenerator);
	}

	@Override
	protected void doMutation(double mutationProbability, RestfulAPITestSuiteSolution solution) {
		for (TestCase testCase : solution.getVariables()) {
			if (getRandomGenerator().nextDouble() <= mutationProbability) {
				doMutation(testCase, solution);
				resetTestResult(testCase.getId(), solution); // The test case changed, reset test result
			}
		}

	}

	private void doMutation(TestCase testCase, RestfulAPITestSuiteSolution solution) {

	}

}