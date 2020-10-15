package es.us.isa.restest.searchbased.operators;

import es.us.isa.restest.searchbased.AbstractSearchBasedTest;
import es.us.isa.restest.searchbased.RestfulAPITestSuiteGenerationProblem;
import es.us.isa.restest.searchbased.RestfulAPITestSuiteSolution;
import es.us.isa.restest.testcases.TestCase;
import io.qameta.allure.junit4.DisplayName;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

public class UniformTestCaseCrossoverTest extends AbstractSearchBasedTest {

	@Test
	@DisplayName("With a crossover probability of 0 there is no changes due to crossover")
	public void executeTest() {
		for(RestfulAPITestSuiteGenerationProblem problem:createTestProblems()) {
			// Arrange | Fixture:
			RestfulAPITestSuiteSolution parent1=problem.createSolution();
			RestfulAPITestSuiteSolution parent2=problem.createSolution();
			List<RestfulAPITestSuiteSolution> parents=Arrays.asList(parent1,parent2);
			UniformTestCaseCrossover sut=new UniformTestCaseCrossover(0.0);
			// Act (SUT invocation):
			List<RestfulAPITestSuiteSolution> offspring=sut.execute(parents);			
			// Assert:
			assertEquals(parent1, offspring.get(0));
			assertEquals(parent2, offspring.get(1));
		}
	}
		
	@Test
	@DisplayName("With a crossover probability of 1, one test case has changed its query parameters with another")
	public void executeTest2() {
		for(RestfulAPITestSuiteGenerationProblem problem:createTestProblems()) {
			// Arrange | Fixture:
			RestfulAPITestSuiteSolution parent1;
			RestfulAPITestSuiteSolution parent2;
			do { // Create two test suites that contain the same type of operation
				parent1 = problem.createSolution();
				parent2 = problem.createSolution();

				parent1.setVariables(parent1.getVariables().stream().filter(tc -> tc.getOperationId().equals("GET--version-incidents--id---format-")).collect(Collectors.toList()));
				parent2.setVariables(parent2.getVariables().stream().filter(tc -> tc.getOperationId().equals("GET--version-incidents--id---format-")).collect(Collectors.toList()));

			} while (parent1.getVariables().isEmpty() || parent2.getVariables().isEmpty());

			List<RestfulAPITestSuiteSolution> parents= Arrays.asList(parent1,parent2);
			UniformTestCaseCrossover sut=new UniformTestCaseCrossover(1.0);
			// Act (SUT invocation):
			List<RestfulAPITestSuiteSolution> offspring=sut.execute(parents);			
			// Assert:
			int sameTestCases = 0;
			for (TestCase tc: offspring.get(0).getVariables()) {
				if (parent1.getVariables().contains(tc))
					sameTestCases++;
			}
			assertEquals(sameTestCases+1, parent1.getNumberOfVariables());
		}
	}
	
}
