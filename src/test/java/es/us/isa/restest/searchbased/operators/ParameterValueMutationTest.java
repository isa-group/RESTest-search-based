package es.us.isa.restest.searchbased.operators;

import es.us.isa.restest.searchbased.AbstractSearchBasedTest;
import es.us.isa.restest.searchbased.RestfulAPITestSuiteGenerationProblem;
import es.us.isa.restest.searchbased.RestfulAPITestSuiteSolution;
import es.us.isa.restest.testcases.TestCase;
import io.qameta.allure.junit4.DisplayName;
import org.junit.Test;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ParameterValueMutationTest extends AbstractSearchBasedTest {
	
	@Test
	@DisplayName("With a mutation probability of 0 there is no changes due to mutation")
	public void executeWithNoProbabilityTest() {
		for(RestfulAPITestSuiteGenerationProblem problem:createTestProblems()){		
			// Arrangment | fixture
			RestfulAPITestSuiteSolution solution= problem.createSolution();
			RestfulAPITestSuiteSolution expectedResult=solution.copy(); 
		
			RandomParameterValueMutation operator=new  RandomParameterValueMutation(0.0, JMetalRandom.getInstance().getRandomGenerator());
		
			// Act (SUT invocation)		
			RestfulAPITestSuiteSolution result=operator.execute(solution.copy());
		
			// Assert: There is no changes due to the mutation:
			assertEquals(result, expectedResult);
		}
	}
	
	@Test
	@DisplayName("With a mutation probability of 1 only one test has a changed parameter")
	public void executeWithFullProbabilityTest() {
	
		List<RestfulAPITestSuiteGenerationProblem> problems=createTestProblems();		
		for(RestfulAPITestSuiteGenerationProblem problem:problems) {
			// Arrangement:
			RestfulAPITestSuiteSolution solution= problem.createSolution();
			RandomParameterValueMutation operator=new  RandomParameterValueMutation(1.0, JMetalRandom.getInstance().getRandomGenerator());
		
			// Act (SUT invocation)		
			RestfulAPITestSuiteSolution result=operator.execute(solution.copy());
		
			// Assert: There is one changed parameter on one testcase
			TestCase originalTestCase;
			TestCase mutatedTestCase;
			int sameParameterValues = 0;
			int totalParameters = 0;
			for(int i=0;i<result.getVariables().size();i++) {
				originalTestCase=solution.getVariable(i);
				mutatedTestCase=result.getVariable(i);
				totalParameters += originalTestCase.getQueryParameters().entrySet().size();
				for (Map.Entry<String, String> param: originalTestCase.getQueryParameters().entrySet()) {
					if (mutatedTestCase.getQueryParameters().get(param.getKey()).equals(originalTestCase.getQueryParameters().get(param.getKey())))
							sameParameterValues++;
				}
				// Query parameters:
				assertTrue(mutatedTestCase.getQueryParameters().entrySet().size()<=originalTestCase.getQueryParameters().entrySet().size());

				// TODO: Header, formData and body parameters (BikeWise have none)
			}
			assertEquals(sameParameterValues+1, totalParameters);
		}
	}
}
