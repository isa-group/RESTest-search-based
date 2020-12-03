package es.us.isa.restest.searchbased.constraints;

import org.junit.Test;

import es.us.isa.restest.searchbased.AbstractSearchBasedTest;
import es.us.isa.restest.searchbased.RestfulAPITestSuiteGenerationProblem;
import es.us.isa.restest.searchbased.RestfulAPITestSuiteSolution;
import es.us.isa.restest.searchbased.objectivefunction.SuiteSize;
import io.qameta.allure.junit4.DisplayName;

import static es.us.isa.restest.searchbased.constraints.ObjectiveFunctionConstraint.ConstraintType.*;
import static org.junit.Assert.*;

public class ObjectiveFunctionContraintTest extends AbstractSearchBasedTest {

	@Test
	@DisplayName("For a constraint with a minimum suite size of 3.0, "
			+ "a solution with 4.0 tests provides a evaluation value of -3.0,"
			+ "thus the constraint is met")
	public void testMaximumSizeValueEvaluation() {
		ObjectiveFunctionConstraint constraint=ObjectiveFunctionConstraint.
				the(MINIMUM).
				valueOf(new SuiteSize()).
				is(3.0);
		double expectedValue=1.0;
		for(RestfulAPITestSuiteGenerationProblem problem:createTestProblems()) {
			problem.getOptimizationConstraints().add(constraint);			
			RestfulAPITestSuiteSolution solution=problem.createSolution();
			problem.evaluate(solution);
			assertEquals(expectedValue, constraint.evaluate(solution),0.001);
			assertTrue(constraint.isMeet(solution));
		}
		
		
	}
	
	@Test
	@DisplayName("For a constraint with a maximum suite size of 1.0, "
			+ "a solution with 4.0 tests provides a evaluation value of 1.0,"
			+ "thus the constraint is not met")
	public void testMinimumSizeValueEvalution() {
		ObjectiveFunctionConstraint constraint=ObjectiveFunctionConstraint.
				the(MAXIMUM).
				valueOf(new SuiteSize()).
				is(1.0);
		double expectedValue=-3.0;
		for(RestfulAPITestSuiteGenerationProblem problem:createTestProblems()) {
			problem.getOptimizationConstraints().add(constraint);			
			RestfulAPITestSuiteSolution solution=problem.createSolution();
			problem.evaluate(solution);
			assertEquals(expectedValue, constraint.evaluate(solution),0.001);
			assertFalse(constraint.isMeet(solution));
		}
		
		
	}
}
