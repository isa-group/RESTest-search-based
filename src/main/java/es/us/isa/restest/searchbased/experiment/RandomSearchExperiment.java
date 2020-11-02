package es.us.isa.restest.searchbased.experiment;

import es.us.isa.restest.searchbased.RestfulAPITestSuiteGenerationProblem;
import es.us.isa.restest.searchbased.RestfulAPITestSuiteSolution;
import es.us.isa.restest.searchbased.SearchBasedTestSuiteGenerator;
import es.us.isa.restest.searchbased.objectivefunction.InputCoverage;
import es.us.isa.restest.searchbased.objectivefunction.RestfulAPITestingObjectiveFunction;
import es.us.isa.restest.searchbased.objectivefunction.SuiteSize;
import es.us.isa.restest.searchbased.terminationcriteria.MaxEvaluations;
import es.us.isa.restest.specification.OpenAPISpecification;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.multiobjective.randomsearch.RandomSearch;
import org.uma.jmetal.util.experiment.util.ExperimentAlgorithm;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class RandomSearchExperiment {

	
	public static void main(String[] args) {
		// Experiment configuration
		long seed=1979;
		int populationSize=100;
		int maxEvaluations=10000;
		int independentRuns=2;
		
		// Problem configuration 
		String OAISpecPath = "src/test/resources/Bikewise/swagger.yaml";		    // Path to OAS specification file
	    String confPath = "src/test/resources/Bikewise/fullConf.yaml";		    // Path to test configuration file
	    String experimentName = "bikewise";                                      // Experiment name
	    String targetDir = "src/generation/java/searchbased";	// Directory where tests will be generated.
		String testClassName = "BikewiseTest"; // Name of the class where tests will be written.
	    String resourcePath ="/v2/incidents";
	    String method ="GET";
	    int minTestSuiteSize=2;
	    int maxTestSuiteSize=10;
		double[] mutationProbabilities = {
				0.1, // AddTestCaseMutation
				0.1, // RemoveTestCaseMutation
				0.1, // ReplaceTestCaseMutation
				0.1, // AddParameterMutation
				0.1, // RemoveParameterMutation
				0.1  // RandomParameterValueMutation
		};
		double crossoverProbability = 0.1; // SinglePointTestSuiteCrossover
	    List<RestfulAPITestingObjectiveFunction> objectiveFunctions= Arrays.asList(
				new InputCoverage(),
	    		new SuiteSize()
	    		);
	    SearchBasedTestSuiteGenerator generator=new SearchBasedTestSuiteGenerator(
				new OpenAPISpecification(OAISpecPath),
                confPath,
                experimentName,
                objectiveFunctions,
                targetDir,
                seed,
                minTestSuiteSize,
                maxTestSuiteSize,                
                populationSize,
				mutationProbabilities,
				crossoverProbability,
                new MaxEvaluations(maxEvaluations),
				null);
	    List<RestfulAPITestSuiteGenerationProblem> problems = Arrays.asList();
	    List<ExperimentAlgorithm<RestfulAPITestSuiteSolution, List<RestfulAPITestSuiteSolution>>> algorithms = null;
	    Algorithm<List<RestfulAPITestSuiteSolution>> randomSearch=new RandomSearch(generator.getProblems().get(0).getProblem(),maxEvaluations);
	    generator.setAlgorithms(Arrays.asList(new ExperimentAlgorithm<RestfulAPITestSuiteSolution, List<RestfulAPITestSuiteSolution>>(randomSearch,generator.getProblems().get(0),0)));
	    try {
			generator.run();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
