/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.us.isa.restest.searchbased.experiment;


import static es.us.isa.restest.util.FileManager.createDir;
import static es.us.isa.restest.util.FileManager.deleteDir;
import static es.us.isa.restest.util.PropertyManager.readProperty;
import static es.us.isa.restest.util.Timer.TestStep.ALL;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.apache.commons.collections4.map.HashedMap;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.uma.jmetal.operator.MutationOperator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;

import es.us.isa.restest.coverage.CoverageGatherer;
import es.us.isa.restest.coverage.CoverageMeter;
import es.us.isa.restest.reporting.AllureReportManager;
import es.us.isa.restest.reporting.StatsReportManager;
import es.us.isa.restest.runners.SearchBasedRunner;
import es.us.isa.restest.searchbased.SearchBasedTestSuiteGenerator;
import es.us.isa.restest.searchbased.objectivefunction.BalanceOfValidTestsRatio;
import es.us.isa.restest.searchbased.objectivefunction.Coverage;
import es.us.isa.restest.searchbased.objectivefunction.Diversity;
import es.us.isa.restest.searchbased.objectivefunction.Element;
import es.us.isa.restest.searchbased.objectivefunction.RestfulAPITestingObjectiveFunction;
import es.us.isa.restest.searchbased.objectivefunction.SimilarityMeter;
import es.us.isa.restest.searchbased.objectivefunction.UniqueElements;
import es.us.isa.restest.searchbased.operators.AbstractCrossoverOperator;
import es.us.isa.restest.searchbased.operators.AbstractMutationOperator;
import es.us.isa.restest.searchbased.operators.AddParameterMutation;
import es.us.isa.restest.searchbased.operators.AddTestCaseMutation;
import es.us.isa.restest.searchbased.operators.RandomParameterValueMutation;
import es.us.isa.restest.searchbased.operators.RemoveParameterMutation;
import es.us.isa.restest.searchbased.operators.RemoveTestCaseMutation;
import es.us.isa.restest.searchbased.operators.ReplaceTestCaseMutation;
import es.us.isa.restest.searchbased.operators.SinglePointTestSuiteCrossover;
import es.us.isa.restest.searchbased.reporting.ExperimentReport;
import es.us.isa.restest.searchbased.terminationcriteria.MaxEvaluations;
import es.us.isa.restest.searchbased.terminationcriteria.MaxExecutedRequests;
import es.us.isa.restest.searchbased.terminationcriteria.MaxExecutionTime;
import es.us.isa.restest.searchbased.terminationcriteria.AbstractTerminationCriterion;
import es.us.isa.restest.specification.OpenAPISpecification;
import es.us.isa.restest.testcases.TestCase;
import es.us.isa.restest.testcases.TestResult;
import es.us.isa.restest.testcases.writers.IWriter;
import es.us.isa.restest.testcases.writers.RESTAssuredWriter;
import es.us.isa.restest.util.CSVManager;
import es.us.isa.restest.util.PropertyManager;
import es.us.isa.restest.util.TestManager;
import es.us.isa.restest.util.Timer;

/**
 *
 * @author Alberto Martin-Lopez
 */
public class MultipleExperiments {

	private static final Logger logger = LogManager.getLogger(MultipleExperiments.class.getName());
	
	private static int checkpointFrequency=10; // We will save a checkpoint each X individual runs
	
    // Final report to be generated:
    private static String reportPath;
	private static List<ExperimentReport> experimentReports = new ArrayList<>();
    

    // Experiment parameters
    private static int[] minTestSuiteSizeArray = {100, 200, 500}; // These sizes can be justified with EvoMaster
    private static int[] maxTestSuiteSizeArray = {100, 200, 500};
    private static int[] populationSizeArray = {10, 20}; // Population size for the evolutionary algorithm
//    private static int[] maxEvaluationsArray = {1000, 5000, 10000};
//    private static int[] maxExecutedRequestsArray= {1000, 5000, 10000};
    
    private static AbstractMutationOperator[] operatorsOrder={new AddTestCaseMutation(0),
    													 new RemoveTestCaseMutation(0),
    													 new ReplaceTestCaseMutation(0),
    													 new AddParameterMutation(0),
    													 new RemoveParameterMutation(0),
    													 new RandomParameterValueMutation(0)};
    private static double[][] mutationProbabilitiesArray = {
            {
                    0.01, // AddTestCaseMutation
                    0.01, // RemoveTestCaseMutation
                    0.01, // ReplaceTestCaseMutation
                    0.01, // AddParameterMutation
                    0.01, // RemoveParameterMutation
                    0.01  // RandomParameterValueMutation
            },
            {0.05, 0.05, 0.05, 0.05, 0.05, 0.05},
            {0.1, 0.1, 0.1, 0.1, 0.1, 0.1}
    };
    private static double[] crossoverProbabilityArray = {0.5, 0.75, 0.9}; // SinglePointTestSuiteCrossover

    // Objective functions: ORDER IS IMPORTANT!!! First one will be used to determine the "best" test suite
    private static List<List<RestfulAPITestingObjectiveFunction>> objectiveFunctionsArray = Lists.newArrayList(
            Lists.newArrayList(new Coverage()),
            Lists.newArrayList(new Diversity(SimilarityMeter.METRIC.LEVENSHTEIN, Element.INPUT, false)),
            Lists.newArrayList(new Diversity(SimilarityMeter.METRIC.LEVENSHTEIN, Element.OUTPUT, false)),
            Lists.newArrayList(new UniqueElements(Element.FAILURE, false)),
            Lists.newArrayList(
                    new BalanceOfValidTestsRatio(0.8),
                    new Diversity(SimilarityMeter.METRIC.LEVENSHTEIN, Element.INPUT, false)
            ),
            Lists.newArrayList(
                    new Diversity(SimilarityMeter.METRIC.LEVENSHTEIN, Element.FAILURE, false),
                    new Coverage()
            ),
            Lists.newArrayList(
                    new UniqueElements(Element.FAILURE, false),
                    new Diversity(SimilarityMeter.METRIC.LEVENSHTEIN, Element.INPUT, false)
            )
    );
    // Termination criterion
    private static AbstractTerminationCriterion[] terminationCriterionArray = {
            new MaxEvaluations(1000),
            new MaxEvaluations(5000),
            new MaxEvaluations(10000),
            new MaxExecutedRequests(1000),
            new MaxExecutedRequests(2000),
            new MaxExecutedRequests(5000),
            new MaxExecutionTime(15, MaxExecutionTime.TimeUnit.MINUTES),
            new MaxExecutionTime(1, MaxExecutionTime.TimeUnit.HOURS),
            new MaxExecutionTime(2, MaxExecutionTime.TimeUnit.HOURS),
    };

    // API parameters
    private static OpenAPISpecification spec;
    private static String OAISpecPath = "src/test/resources/Bikewise/swagger.json"; // Path to OAS specification file
    private static String confPath = "src/test/resources/Bikewise/fullConf.yaml"; // Path to test configuration file
    private static String experimentBaseName = "bikewise" + "_";
    private static List<String> experimentNames = new ArrayList<>();

    private static int minTestSuiteSize;
    private static int maxTestSuiteSize;
    private static int populationSize;
//    private static int maxEvaluations;
//    private static int maxExecutedRequests;
    private static double[] mutationProbabilities;
    private static double crossoverProbability;
    private static List<RestfulAPITestingObjectiveFunction> objectiveFunctions;
    private static AbstractTerminationCriterion terminationCriterion;

    private static String experimentName;
    private static String targetDir; // Directory where tests will be generated.
    private static String packageName; // Package name
    private static String testClassName; // Name of the class where tests will be written.
    private static long seed;

    

    public static void main(String[] args) {
        // Create CSV file with stats of all experiments
        reportPath = readProperty("search.stats.dir")
                + "/" + experimentBaseName + RandomStringUtils.randomAlphanumeric(10)
                + "_" + readProperty("search.stats.general.file");
        createDir(readProperty("search.stats.dir"));
//        String objFuncPath = searchStatsDirectory + "/" + readProperty("search.stats.objfunc.file");
        CSVManager.createCSVwithHeader(reportPath, ExperimentReport.getGeneralStatsCsvHeader());
//        CSVManager.createCSVwithHeader(objFuncPath, ExperimentReport.getObjFuncStatsCsvHeader());
    	// Setup:
    	int execution=0;
    	List<ExperimentalConfiguration> experimentalConfigurations=generateExperimentalConfigurations();
    	List<ExperimentalConfiguration> executedConfigurations=new ArrayList();
    	Collections.shuffle(experimentalConfigurations);    	
    	// Execution:
    	for(ExperimentalConfiguration experimentalConfiguration:experimentalConfigurations) {
    		run(experimentalConfiguration);
    		executedConfigurations.add(experimentalConfiguration);
    		if(execution%checkpointFrequency==0) {
    			generateCheckpoint(experimentalConfigurations,executedConfigurations);
    		}
    		execution++;
    	}
    	

        logger.info("A total of {} experiment folders have been generated:\n{}", experimentNames.size(), String.join("\n", experimentNames));        
        exportExperimentReports(reportPath);

        logger.info("Generated experiment report in the following path: {}", reportPath);
    }

    /**
     * Checkpoining usin a JSON file with the experimental configurations to be executed.
     */
    private static void generateCheckpoint(List<ExperimentalConfiguration> experimentalConfigurations,
		List<ExperimentalConfiguration> executedConfigurations) {
    	ObjectMapper JSONmapper=new ObjectMapper();
    	String checkpointPath ="target/test-data/checkpoint.json";
		List<ExperimentalConfiguration> expConfsToSave=experimentalConfigurations.stream()
				.filter((expConf) -> !executedConfigurations.contains(expConf))
				.collect(Collectors.toList());
		try {
			JSONmapper.writeValue(new File(checkpointPath), expConfsToSave);
		} catch (IOException e) {
			logger.error("Unable to save checkpoint!:"+e.getLocalizedMessage());
			e.printStackTrace();
		}
		
	}

	private static void run(ExperimentalConfiguration config) {
        Timer.resetCounters();
        Timer.startCounting(ALL);
        
        createDir(config.getTargetDir());

        spec = new OpenAPISpecification(config.getOpenApiSpecPath());

        // RESTest runner
        IWriter writer = createWriter();                                    // Test case writer
        AllureReportManager reportManager = createAllureReportManager();    // Allure test case reporter
        StatsReportManager statsReportManager = createStatsReportManager(); // Stats reporter
        SearchBasedRunner runner = new SearchBasedRunner(config.getTestClassName(), config.getTargetDir(), config.getPackageName(), null, (RESTAssuredWriter) writer, reportManager, statsReportManager);

     // Create experiment report and set it up
        ExperimentReport experimentReport = config.generateExperimentReport();        
                
        SearchBasedTestSuiteGenerator generator = new SearchBasedTestSuiteGenerator(                 
                config.getExperimentName(),                
                config.getTargetDir(),
                config.getSeed(),
                config.getAlgorithm().getProblem(),
                config.getAlgorithm(),
                runner,
                experimentReport
        );

        try {
            generator.run();
            
            Timer.stopCounting(ALL);
            generateTimeReport();
            logger.info("Results saved to folder {}", experimentName);
            
            
         // Update file containing stats from all experiments
            updateExperimentReport(experimentReport, generator, statsReportManager);
            CSVManager.writeCSVRow(reportPath, experimentReport.getGeneralStatsCsvRow());

            // Create file containing objFunc stats from this specific experiment
            String objFuncDir = readProperty("search.stats.dir") + "/" + experimentName;
            createDir(objFuncDir);
            String objFuncPath = objFuncDir + "/" + readProperty("search.stats.objfunc.file");
            CSVManager.createCSVwithHeader(objFuncPath, ExperimentReport.getObjFuncStatsCsvHeader());
            CSVManager.writeCSVRow(objFuncPath, experimentReport.getObjFuncStatsCsvRows());
        } catch (IOException ex) {
            logger.error(ex);
        }		
	}

	

	private static double[] toProbArray(Map<AbstractMutationOperator, Double> mutationProbabilities) {		
		double []result=new double[operatorsOrder.length];
		for(int i=0;i<operatorsOrder.length;i++) {
			result[i]=mutationProbabilities.get(operatorsOrder[i]);		
		}
		return result;
	}

	private static List<ExperimentalConfiguration> generateExperimentalConfigurations() {
        List<ExperimentalConfiguration> configurations=new ArrayList<>();
		for (int i : minTestSuiteSizeArray) {
            minTestSuiteSize = i;
            for (int j : maxTestSuiteSizeArray) {
                maxTestSuiteSize = j;
                if (minTestSuiteSize == maxTestSuiteSize) {
                    for (int k : populationSizeArray) {
                        populationSize = k;
                        for (double[] l : mutationProbabilitiesArray) {
                            mutationProbabilities = l;
                            for (double m : crossoverProbabilityArray) {
                                crossoverProbability = m;
                                for (List<RestfulAPITestingObjectiveFunction> n : objectiveFunctionsArray) {
                                    objectiveFunctions = n;
                                    for (AbstractTerminationCriterion o : terminationCriterionArray) {
                                        terminationCriterion = o;
                                        if (!(terminationCriterion instanceof MaxExecutedRequests &&
/*
                                                objectiveFunctions.stream().noneMatch(RestfulAPITestingObjectiveFunction::isRequiresTestExecution))) {

                                            // API parameters
                                            experimentName = experimentBaseName + RandomStringUtils.randomAlphanumeric(10); // Experiment name
                                            experimentNames.add(experimentName);
                                            targetDir = "src/generation/java/" + experimentName; // Directory where tests will be generated.
                                            packageName = experimentName;							// Package name
                                            testClassName = experimentName.substring(0,1).toUpperCase() + experimentName.substring(1); // Name of the class where tests will be written.                                           

                                            seed = RandomUtils.nextLong();

                                            Timer.resetCounters();
                                            Timer.startCounting(ALL);

                                            createDir(targetDir);

                                            spec = new OpenAPISpecification(OAISpecPath);

                                            // RESTest runner
                                            IWriter writer = createWriter();                                    // Test case writer
                                            AllureReportManager reportManager = createAllureReportManager();    // Allure test case reporter
                                            StatsReportManager statsReportManager = createStatsReportManager(); // Stats reporter
                                            SearchBasedRunner runner = new SearchBasedRunner(testClassName, targetDir, packageName, null, (RESTAssuredWriter) writer, reportManager, statsReportManager);

                                            SearchBasedTestSuiteGenerator generator = new SearchBasedTestSuiteGenerator(
                                                    spec,
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
                                                    terminationCriterion,
                                                    runner,
                                                    experimentReport
                                            );

                                            try {
                                                generator.run();
                                                Timer.stopCounting(ALL);
                                                generateTimeReport();
                                                logger.info("Results saved to folder {}", experimentName);

                                                // Update file containing stats from all experiments
                                                updateExperimentReport(experimentReport, generator, statsReportManager);
                                                CSVManager.writeCSVRow(reportPath, experimentReport.getGeneralStatsCsvRow());

                                                // Create file containing objFunc stats from this specific experiment
                                                String objFuncDir = readProperty("search.stats.dir") + "/" + experimentName;
                                                createDir(objFuncDir);
                                                String objFuncPath = objFuncDir + "/" + readProperty("search.stats.objfunc.file");
                                                CSVManager.createCSVwithHeader(objFuncPath, ExperimentReport.getObjFuncStatsCsvHeader());
                                                CSVManager.writeCSVRow(objFuncPath, experimentReport.getObjFuncStatsCsvRows());
                                            } catch (IOException ex) {
                                                logger.error(ex);
                                            }

*/
                                        		objectiveFunctions.stream().noneMatch(RestfulAPITestingObjectiveFunction::isRequiresTestExecution))) {
                                        	ExperimentalConfiguration config=ExperimentalConfiguration.defaultAlgorithmBuilder()                                        			
                                        			/// General experiment parameters:                                        		
                                        			.withExperimentName(experimentName)
                                        			.withTargetDir(targetDir)
                                        			.withSeed(seed)
                                        			// Problem parameters:                                        			
                                        			.withMinTestSuiteSize(minTestSuiteSize)
                                        			.withMaxTestSuiteSize(maxTestSuiteSize)                                        			
                                        			// Algorithms parameters:
                                        			.withPopulationSize(populationSize)
                                        			.withMutationProbabilities(mutationProbabilities)
                                        			.withCrossoverProbability(crossoverProbability)
                                        			.withTerminationCriterion(terminationCriterion)
                                        			.build();
                                        										
                                        	configurations.add(config);								
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
		return configurations;
	}		

	private static void exportExperimentReports(String path) {
        CSVManager.createCSVwithHeader(path, ExperimentReport.getGeneralStatsCsvHeader());
        experimentReports.forEach(e -> CSVManager.writeCSVRow(path, e.getGeneralStatsCsvRow()));
    }
	
    private static void updateExperimentReport(ExperimentReport experimentReport, SearchBasedTestSuiteGenerator generator, StatsReportManager statsReportManager) throws IOException {
        List<TestResult> testResults = TestManager.getTestResults(statsReportManager.getTestDataDir() + "/test-results.csv");

        experimentReport
                .withMinTestSuiteSize(minTestSuiteSize)
                .withMaxTestSuiteSize(maxTestSuiteSize)
                .withPopulationSize(populationSize)
                .withMutationProbabilities(mutationProbabilities)
                .withCrossoverProbabilities(crossoverProbability)
                .withObjectiveFunctions(objectiveFunctions.stream().map(of -> of.toString()).toArray(String[]::new))
                .withTerminationCriterion(terminationCriterion.toString())
                .withTime(Timer.getCounters().get(ALL.getName()).get(0))
                .withExecutedRequests(generator.getProblem().getTestCasesExecuted() + generator.getBestSolution().getNumberOfVariables())
                .withApiCoverage(statsReportManager.getCoverageMeter().getTotalCoverage())
                .withTestSuiteSize(generator.getBestSolution().getNumberOfVariables())
                .withNominalTestCases((int)generator.getBestSolution().getVariables().stream().filter(tc -> !tc.getFaulty()).count())
                .withFaultyTestCases((int)generator.getBestSolution().getVariables().stream().filter(TestCase::getFaulty).count())
                .withFaultyTestCasesDueToParameters((int)generator.getBestSolution().getVariables().stream().filter(tc -> tc.getFaultyReason().contains("individual_parameter_constraint")).count())
                .withFaultyTestCasesDueToDependencies((int)generator.getBestSolution().getVariables().stream().filter(tc -> tc.getFaultyReason().contains("inter_parameter_dependency")).count())
                .withSuccessfulTestResults((int)testResults.stream().filter(TestResult::getPassed).count())
                .withFailedTestResults((int)testResults.stream().filter(tr -> !tr.getPassed()).count())
                .withFailedTestResults5XX((int)testResults.stream().filter(tr -> Integer.parseInt(tr.getStatusCode()) >= 500).count())
                .withFailedTestResultsNominal4XX((int)testResults.stream().filter(tr -> tr.getFailReason().contains("input was correct")).count())
                .withFailedTestResultsFaultyParameters2XX((int)testResults.stream().filter(tr -> tr.getFailReason().contains("individual_parameter_constraint")).count())
                .withFailedTestResultsFaultyDependencies2XX((int)testResults.stream().filter(tr -> tr.getFailReason().contains("inter_parameter_dependency")).count())
                .withFailedTestResultsSwagger((int)testResults.stream().filter(tr -> tr.getFailReason().contains("Swagger validation")).count())
                .withDifferentFailures((int)testResults.stream().filter(tr -> !tr.getPassed()).map(TestResult::getResponseBody).distinct().count());
    }

    // Create a writer for RESTAssured
    private static IWriter createWriter() {
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
    private static AllureReportManager createAllureReportManager() {
        String allureResultsDir = PropertyManager.readProperty("allure.results.dir") + "/" + experimentName;
        String allureReportDir = PropertyManager.readProperty("allure.report.dir") + "/" + experimentName;

        // Delete previous results (if any)
        deleteDir(allureResultsDir);
        deleteDir(allureReportDir);

        AllureReportManager arm = new AllureReportManager(allureResultsDir, allureReportDir);
        arm.setHistoryTrend(true);
        return arm;
    }

    private static StatsReportManager createStatsReportManager() {
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

    private static void generateTimeReport() {
        ObjectMapper mapper = new ObjectMapper();
        String timePath = readProperty("data.tests.dir") + "/" + experimentName + "/" + readProperty("data.tests.time");
        try {
            mapper.writeValue(new File(timePath), Timer.getCounters());
        } catch (IOException e) {
            logger.error("The time report cannot be generated. Stack trace:");
            logger.error(e.getMessage());
        }
        logger.info("Time report generated.");
    }
}