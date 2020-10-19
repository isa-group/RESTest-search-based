/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.us.isa.restest.searchbased.experiment;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import es.us.isa.restest.coverage.CoverageGatherer;
import es.us.isa.restest.coverage.CoverageMeter;
import es.us.isa.restest.reporting.AllureReportManager;
import es.us.isa.restest.reporting.StatsReportManager;
import es.us.isa.restest.runners.SearchBasedRunner;
import es.us.isa.restest.searchbased.SearchBasedTestSuiteGenerator;
import es.us.isa.restest.searchbased.objectivefunction.*;
import es.us.isa.restest.searchbased.terminationcriteria.MaxEvaluations;
import es.us.isa.restest.searchbased.terminationcriteria.MaxExecutedRequests;
import es.us.isa.restest.searchbased.terminationcriteria.MaxExecutionTime;
import es.us.isa.restest.searchbased.terminationcriteria.TerminationCriterion;
import es.us.isa.restest.specification.OpenAPISpecification;
import es.us.isa.restest.testcases.writers.IWriter;
import es.us.isa.restest.testcases.writers.RESTAssuredWriter;
import es.us.isa.restest.util.PropertyManager;
import es.us.isa.restest.util.Timer;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static es.us.isa.restest.util.FileManager.createDir;
import static es.us.isa.restest.util.FileManager.deleteDir;
import static es.us.isa.restest.util.PropertyManager.readProperty;
import static es.us.isa.restest.util.Timer.TestStep.ALL;

/**
 *
 * @author Alberto Martin-Lopez
 */
public class MultipleExperiments {

    // Experiment parameters
    private static int[] minTestSuiteSizeArray = {100, 200, 500}; // These sizes can be justified with EvoMaster
    private static int[] maxTestSuiteSizeArray = {100, 200, 500};
    private static int[] populationSizeArray = {10, 20}; // Population size for the evolutionary algorithm
//    private static int[] maxEvaluationsArray = {1000, 5000, 10000};
//    private static int[] maxExecutedRequestsArray= {1000, 5000, 10000};
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
    private static double[][] crossoverProbabilitiesArray = {
            {
                    0.01, // UniformTestCaseCrossover
                    0.5  // SinglePointTestSuiteCrossover
            },
            {0.05, 0.75},
            {0.1, 0.9}
    };
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
    private static TerminationCriterion[] terminationCriterionArray = {
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
    private static String OAISpecPath = "src/test/resources/Yelp/swagger.yaml"; // Path to OAS specification file
    private static String confPath = "src/test/resources/Yelp/testConf.yaml"; // Path to test configuration file
    private static String experimentBaseName = "yelp_prelim" + "_";
    private static List<String> experimentNames = new ArrayList<>();

    private static int minTestSuiteSize;
    private static int maxTestSuiteSize;
    private static int populationSize;
//    private static int maxEvaluations;
//    private static int maxExecutedRequests;
    private static double[] mutationProbabilities;
    private static double[] crossoverProbabilities;
    private static List<RestfulAPITestingObjectiveFunction> objectiveFunctions;
    private static TerminationCriterion terminationCriterion;

    private static String experimentName;
    private static String targetDir; // Directory where tests will be generated.
    private static String packageName; // Package name
    private static String testClassName; // Name of the class where tests will be written.
    private static long seed;

    private static final Logger logger = LogManager.getLogger(MultipleExperiments.class.getName());

    public static void main(String[] args) {
        for (int i : minTestSuiteSizeArray) {
            minTestSuiteSize = i;
            for (int j : maxTestSuiteSizeArray) {
                maxTestSuiteSize = j;
                if (minTestSuiteSize == maxTestSuiteSize) {
                    for (int k : populationSizeArray) {
                        populationSize = k;
                        for (double[] l : mutationProbabilitiesArray) {
                            mutationProbabilities = l;
                            for (double[] m : crossoverProbabilitiesArray) {
                                crossoverProbabilities = m;
                                for (List<RestfulAPITestingObjectiveFunction> n : objectiveFunctionsArray) {
                                    objectiveFunctions = n;
                                    for (TerminationCriterion o : terminationCriterionArray) {
                                        terminationCriterion = o;
                                        if (!(terminationCriterion instanceof MaxExecutedRequests &&
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
                                                    crossoverProbabilities,
                                                    terminationCriterion,
                                                    runner
                                            );

                                            try {
                                                generator.run();
                                                Timer.stopCounting(ALL);
                                                generateTimeReport();
                                                logger.info("Results saved to folder {}", experimentName);
                                                generateExperimentDocument(statsReportManager.getTestDataDir() + "/" + experimentName + ".txt");
                                            } catch (IOException ex) {
                                                logger.error(ex);
                                            }

                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }


        logger.info("A total of {} experiment folders have been generated:\n{}", experimentNames.size(), String.join("\n", experimentNames));
    }

    private static void generateExperimentDocument(String path) throws IOException {
        String content =
                "experimentName: " + experimentName + "\n" +
                "minTestSuiteSize: " + minTestSuiteSize + "\n" +
                "maxTestSuiteSize: " + maxTestSuiteSize + "\n" +
                "populationSize: " + populationSize + "\n" +
                "mutationProbabilities: " + Arrays.toString(mutationProbabilities) + "\n" +
                "crossoverProbabilities: " + Arrays.toString(crossoverProbabilities) + "\n" +
                "objectiveFunctions: \n";

        for (RestfulAPITestingObjectiveFunction objFunc: objectiveFunctions)
            content += " - " + objFunc.toString() + "\n";

        content += "terminationCriterion: " + terminationCriterion.toString();

        BufferedWriter writer = new BufferedWriter(new FileWriter(path));
        writer.write(content);
        writer.close();
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