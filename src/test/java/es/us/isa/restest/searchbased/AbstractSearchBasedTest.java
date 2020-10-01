package es.us.isa.restest.searchbased;

import es.us.isa.restest.searchbased.objectivefunction.SuiteSize;
import es.us.isa.restest.specification.OpenAPISpecification;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class AbstractSearchBasedTest {
	// Paths to OAS specification files
	List<String> OAISpecPaths = Arrays.asList("src/test/resources/Bikewise/swagger.yaml");
	// Path to test configuration files
	List<String> confPaths = Arrays.asList("src/test/resources/Bikewise/fullConf.yaml");
	String targetDir ="src/generation/java/searchbasedtests";
	// Fixed test suite size
	protected int testSuiteSize = 4;
	public List<RestfulAPITestSuiteGenerationProblem> createTestProblems(){
		List<RestfulAPITestSuiteGenerationProblem> problems=new ArrayList<>();
		for(int i=0;i<OAISpecPaths.size();i++)
		{
			problems.add(
					SearchBasedTestSuiteGenerator.
						buildProblem(new OpenAPISpecification(OAISpecPaths.get(i)),
							confPaths.get(i),
							Arrays.asList(new SuiteSize()),
							targetDir, testSuiteSize, testSuiteSize)
					);
		}
		return problems;
	}

}
