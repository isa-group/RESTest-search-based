package es.us.isa.restest.searchbased.experiment;

import static es.us.isa.restest.util.FileManager.createDir;
import static es.us.isa.restest.util.FileManager.deleteDir;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import es.us.isa.restest.util.AllureAuthManager;
import org.uma.jmetal.lab.experiment.util.ExperimentAlgorithm;

import es.us.isa.restest.coverage.CoverageGatherer;
import es.us.isa.restest.coverage.CoverageMeter;
import es.us.isa.restest.reporting.AllureReportManager;
import es.us.isa.restest.reporting.StatsReportManager;
import es.us.isa.restest.runners.SearchBasedRunner;
import es.us.isa.restest.searchbased.RestfulAPITestSuiteGenerationProblem;
import es.us.isa.restest.searchbased.RestfulAPITestSuiteSolution;
import es.us.isa.restest.searchbased.SearchBasedTestSuiteGenerator;
import es.us.isa.restest.searchbased.constraints.ObjectiveFunctionConstraint;
import es.us.isa.restest.searchbased.constraints.OptimizationConstraint;
import es.us.isa.restest.searchbased.objectivefunction.InputCoverage;
import es.us.isa.restest.searchbased.objectivefunction.RestfulAPITestingObjectiveFunction;
import es.us.isa.restest.searchbased.objectivefunction.RestfulAPITestingObjectiveFunction.ObjectiveFunctionType;
import es.us.isa.restest.searchbased.objectivefunction.SuiteSize;
import es.us.isa.restest.searchbased.objectivefunction.ValidTestsRatio;
import es.us.isa.restest.searchbased.terminationcriteria.MaxEvaluations;
import es.us.isa.restest.specification.OpenAPISpecification;
import es.us.isa.restest.testcases.writers.IWriter;
import es.us.isa.restest.testcases.writers.RESTAssuredWriter;
import es.us.isa.restest.util.PropertyManager;
import static es.us.isa.restest.searchbased.constraints.ObjectiveFunctionConstraint.ConstraintType.*;
/**
 * Experiment with bikewise api with a ValidTestRatio constraint.
 * 
 * 
 * @author japarejo
 *
 */
public class ConstrainedExperiment {
	
	
	// Experiment configuration:
	long seed=1979;
	private String experimentName = "bikewiseWithValidTestRatioConstraint";                                      // Experiment name
	private String targetDir = "src/generation/java/searchbased";	// Directory where tests will be generated.
	// RESTest runner configuration:
	private String packageName = experimentName;							// Package name
    private String testClassName = experimentName.substring(0,1).toUpperCase() + experimentName.substring(1); // Name of the class where tests will be written.
	
	//Problem configuration 
	private String OAISpecPath = "src/test/resources/Bikewise/swagger.yaml";		    // Path to OAS specification file
    private String confPath = "src/test/resources/Bikewise/fullConf.yaml";		    // Path to test configuration file       
    private String resourcePath ="/v2/incidents";
    private String method ="GET";
    private int minTestSuiteSize=2;
    private int maxTestSuiteSize=10;
    // 			Constraint:
    private double minimumValidTestRatio=4;
	// Algorithm Configuration:
    private int populationSize=10;
    private double[] mutationProbabilities = {
			0.1, // AddTestCaseMutation
			0.1, // RemoveTestCaseMutation
			0.1, // ReplaceTestCaseMutation
			0.1, // AddParameterMutation
			0.1, // RemoveParameterMutation
			0.1  // RandomParameterValueMutation
	};  
	private double crossoverProbability = 0.1; // SinglePointTestSuiteCrossover
	private int maxEvaluations=50000;
    
    
	
    public static void main(String[] args) {
		new ConstrainedExperiment().run();
	}
            
    public void run() {
    	// Objective functions:
    	List<RestfulAPITestingObjectiveFunction> objectiveFunctions= Arrays.asList(
				new InputCoverage(),
	    		new SuiteSize()
	    		);
    	// Constraints:
    	List<OptimizationConstraint> constraints=Arrays.asList(    	
    			ObjectiveFunctionConstraint.
    				the(MINIMUM).
    				valueOf(new 
    					ValidTestsRatio()).
    				is(	minimumValidTestRatio)    			
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
				null,
                null);
	    // We add the constraint:
	    generator.getProblem().getOptimizationConstraints().addAll(constraints);
	    
	    createDir(targetDir);
	    // RESTest Runner setup:
        IWriter writer = createWriter(generator.getProblem().getApiUnderTest());                                    // Test case writer
        AllureReportManager reportManager = createAllureReportManager();    // Allure test case reporter
        StatsReportManager statsReportManager = createStatsReportManager(generator.getProblem().getApiUnderTest()); // Stats reporter
        SearchBasedRunner runner = new SearchBasedRunner(testClassName, targetDir, packageName, null, (RESTAssuredWriter) writer, reportManager, statsReportManager);
	    generator.setRestestRunner(runner);
	    List<RestfulAPITestSuiteSolution> solutions=null;
	    try {
			solutions=generator.run();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    ValidTestsRatio vtrObjFunc=new ValidTestsRatio(ObjectiveFunctionType.MAXIMIZATION);
	    System.out.println("The algorithm generated "+solutions.size()+" test suites.");
	    int i=1;
	    for(RestfulAPITestSuiteSolution solution:solutions) {
	    	System.out.println("The valid test ratio of the "+i+"# suite is "+vtrObjFunc.evaluate(solution)+" (size "+solution.getVariables().size()+")");
	    	System.out.println("	Obj. Func. values:"+solution.getObjectives());
	    	System.out.println("	Constraint values:"+solution.getConstraints());
	    	i++;	    	
	    }
    }
    
    // Create a writer for RESTAssured
    private IWriter createWriter(OpenAPISpecification spec) {
        String basePath = spec.getSpecification().getServers().get(0).getUrl();
        RESTAssuredWriter writer = new RESTAssuredWriter(OAISpecPath, targetDir, testClassName, packageName, basePath);
        writer.setLogging(true);
        writer.setAllureReport(true);
        writer.setEnableStats(true);
        writer.setEnableOutputCoverage(true);
        writer.setAPIName(experimentName);
        return writer;
    }

    // Create an Allure report manager
    private AllureReportManager createAllureReportManager() {
        String allureResultsDir = PropertyManager.readProperty("allure.results.dir") + "/" + experimentName;
        String allureReportDir = PropertyManager.readProperty("allure.report.dir") + "/" + experimentName;

        // Delete previous results (if any)
        deleteDir(allureResultsDir);
        deleteDir(allureReportDir);

        //Find auth property names (if any)
        List<String> authProperties = AllureAuthManager.findAuthProperties(new OpenAPISpecification(OAISpecPath), confPath);

        AllureReportManager arm = new AllureReportManager(allureResultsDir, allureReportDir, authProperties);        arm.setHistoryTrend(true);
        return arm;
    }

    private StatsReportManager createStatsReportManager(OpenAPISpecification spec) {
        String testDataDir = PropertyManager.readProperty("data.tests.dir") + "/" + experimentName;
        String coverageDataDir = PropertyManager.readProperty("data.coverage.dir") + "/" + experimentName;

        // Delete previous results (if any)
        deleteDir(testDataDir);
        deleteDir(coverageDataDir);

        // Recreate directories
        createDir(testDataDir);
        createDir(coverageDataDir);

        return new StatsReportManager(testDataDir, coverageDataDir, true, true, true, new CoverageMeter(new CoverageGatherer(spec)));
    }

 // Create target dir if it does not exist
    public Boolean createDir(String targetDir) {
        if (targetDir.charAt(targetDir.length()-1) != '/')
            targetDir += "/";
        File dir = new File(targetDir);
        return dir.mkdirs();
    }
}
