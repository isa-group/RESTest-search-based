package es.us.isa.restest.searchbased.operators;

import es.us.isa.restest.searchbased.AbstractSearchBasedTest;
import es.us.isa.restest.searchbased.RestfulAPITestSuiteGenerationProblem;
import es.us.isa.restest.searchbased.RestfulAPITestSuiteSolution;
import es.us.isa.restest.testcases.TestCase;
import io.qameta.allure.junit4.DisplayName;
import org.junit.Test;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ParameterAdditionMutationTest extends AbstractSearchBasedTest {
	
	@Test
	@DisplayName("With a mutation probability of 0 there is no changes due to mutation")
	public void executeWithNoProbabilityTest() {
		for(RestfulAPITestSuiteGenerationProblem problem:createTestProblems()){		
			// Arrangment | fixture
			RestfulAPITestSuiteSolution solution= problem.createSolution();
			RestfulAPITestSuiteSolution expectedResult=solution.copy(); 
		
			AddParameterMutation operator=new  AddParameterMutation(0.0, JMetalRandom.getInstance().getRandomGenerator());
		
			// Act (SUT invocation)		
			RestfulAPITestSuiteSolution result=operator.execute(solution.copy());
		
			// Assert: There is no changes due to the mutation:
			assertEquals(result, expectedResult);
		}
	}
	
	@Test
	@DisplayName("With a mutation probability of 1 only one test has one additional parameter")
	public void executeWithFullProbabilityTest() {
	
		List<RestfulAPITestSuiteGenerationProblem> problems=createTestProblems();		
		for(RestfulAPITestSuiteGenerationProblem problem:problems) {
			// Arrangement:
			RestfulAPITestSuiteSolution solution= problem.createSolution();		 		
			AddParameterMutation operator=new  AddParameterMutation(1.0, JMetalRandom.getInstance().getRandomGenerator());
		
			// Act (SUT invocation)		
			RestfulAPITestSuiteSolution result=operator.execute(solution.copy());
		
			// Assert: There is one additional parameters on one testcase
			TestCase originalTestCase;
			TestCase mutatedTestCase;
			int totalParams = 0;
			int totalParamsAfterMutation = 0;
			for(int i=0;i<result.getVariables().size();i++) {
				originalTestCase=solution.getVariable(i);
				mutatedTestCase=result.getVariable(i);
				totalParams += originalTestCase.getQueryParameters().entrySet().size();
				totalParamsAfterMutation += mutatedTestCase.getQueryParameters().entrySet().size();
				// Query parameters:
				assertTrue(mutatedTestCase.getQueryParameters().entrySet().containsAll(originalTestCase.getQueryParameters().entrySet()));
				assertTrue(mutatedTestCase.getQueryParameters().entrySet().size()<=originalTestCase.getQueryParameters().entrySet().size()+1);

				// TODO: Header, formData and body parameters (BikeWise have none)
			}

			assertEquals(totalParams+1, totalParamsAfterMutation);
		}
	}
}
