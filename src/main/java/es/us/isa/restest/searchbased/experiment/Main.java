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
import es.us.isa.restest.searchbased.terminationcriteria.TerminationCriterion;
import es.us.isa.restest.specification.OpenAPISpecification;
import es.us.isa.restest.testcases.writers.IWriter;
import es.us.isa.restest.testcases.writers.RESTAssuredWriter;
import es.us.isa.restest.util.AllureAuthManager;
import es.us.isa.restest.util.PropertyManager;
import es.us.isa.restest.util.Timer;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.commons.lang3.RandomUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static es.us.isa.restest.searchbased.terminationcriteria.Or.or;
import static es.us.isa.restest.util.FileManager.createDir;
import static es.us.isa.restest.util.FileManager.deleteDir;
import static es.us.isa.restest.util.PropertyManager.readProperty;
import static es.us.isa.restest.util.Timer.TestStep.ALL;

/**
 *
 * @author japar
 */
public class Main {

    // Experiment parameters
    private static int minTestSuiteSize = 5;
    private static int maxTestSuiteSize = 5;
    private static int populationSize = 10; // Population size for the evolutionary algorithm
    private static int maxEvaluations = 250;
    private static int maxExecutedRequests=100;
    private static double[] mutationProbabilities = {
            0.05, // AddTestCaseMutation
            0.05, // RemoveTestCaseMutation
            0.05, // ReplaceTestCaseMutation
            0.05, // AddParameterMutation
            0.05, // RemoveParameterMutation
            0.05  // RandomParameterValueMutation
    };
    private static double crossoverProbability = 0.8; // SinglePointTestSuiteCrossover
    // Objective functions: ORDER IS IMPORTANT!!! First one will be used to determine the "best" test suite
    private static List<RestfulAPITestingObjectiveFunction> objectiveFunctions = Lists.newArrayList(
            new BalanceOfValidTestsRatio(0.7),
            new Diversity(SimilarityMeter.METRIC.LEVENSHTEIN, Element.INPUT, false)
//                new SuiteSize()
    );

    // Termination criterion
    private static TerminationCriterion terminationCriterion =
            new MaxEvaluations(maxEvaluations);
//            or(
//                new MaxEvaluations(maxEvaluations),
//                new MaxExecutedRequests(maxExecutedRequests)
//            );

    // API parameters
    private static OpenAPISpecification spec;
    private static String OAISpecPath = "src/test/resources/Yelp/swagger.yaml"; // Path to OAS specification file
    private static String confPath = "src/test/resources/Yelp/testConf.yaml"; // Path to test configuration file
    private static String experimentName = "yelp_prelim" + "_" + RandomStringUtils.randomAlphanumeric(10); // Experiment name
    private static String targetDir = "src/generation/java/" + experimentName; // Directory where tests will be generated.
    private static String packageName = experimentName;							// Package name
    private static String testClassName = experimentName.substring(0,1).toUpperCase() + experimentName.substring(1); // Name of the class where tests will be written.
    private static long seed = RandomUtils.nextLong();

    private static final Logger logger = LogManager.getLogger(Main.class.getName());

    public static void main(String[] args) {
        Timer.startCounting(ALL);

        createDir(targetDir);

        spec = new OpenAPISpecification(OAISpecPath);

        // RESTest runner
        IWriter writer = createWriter();                                    // Test case writer
        AllureReportManager reportManager = createAllureReportManager();    // Allure test case reporter
        StatsReportManager statsReportManager = createStatsReportManager(); // Stats reporter
        SearchBasedRunner runner = new SearchBasedRunner(testClassName, targetDir, packageName, null, (RESTAssuredWriter) writer, reportManager, statsReportManager);

        SearchBasedTestSuiteGenerator generator=new SearchBasedTestSuiteGenerator(
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
                null
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

    private static void generateExperimentDocument(String path) throws IOException {
        String content =
                "experimentName: " + experimentName + "\n" +
                "minTestSuiteSize: " + minTestSuiteSize + "\n" +
                "maxTestSuiteSize: " + maxTestSuiteSize + "\n" +
                "populationSize: " + populationSize + "\n" +
                "maxEvaluations: " + maxEvaluations + "\n" +
                "maxExecutedRequests: " + maxExecutedRequests + "\n" +
                "mutationProbabilities: " + Arrays.toString(mutationProbabilities) + "\n" +
                "crossoverProbabilities: " + crossoverProbability + "\n" +
                "objectiveFunctions: \n";

        for (RestfulAPITestingObjectiveFunction objFunc: objectiveFunctions)
            content += " - " + objFunc.toString() + "\n";

        content += "terminationCriterion: " + terminationCriterion.getClass().getSimpleName();

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

        //Find auth property names (if any)
        List<String> authProperties = AllureAuthManager.findAuthProperties(spec, confPath);

        AllureReportManager arm = new AllureReportManager(allureResultsDir, allureReportDir, authProperties);
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