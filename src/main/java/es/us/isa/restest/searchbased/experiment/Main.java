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
import es.us.isa.restest.searchbased.operators.*;
import es.us.isa.restest.searchbased.terminationcriteria.MaxEvaluations;
import es.us.isa.restest.searchbased.terminationcriteria.MaxExecutedRequests;
import es.us.isa.restest.searchbased.terminationcriteria.TerminationCriterion;
import es.us.isa.restest.specification.OpenAPISpecification;
import es.us.isa.restest.testcases.writers.IWriter;
import es.us.isa.restest.testcases.writers.RESTAssuredWriter;
import es.us.isa.restest.util.PropertyManager;
import es.us.isa.restest.util.Timer;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.commons.lang3.RandomUtils;

import java.io.File;
import java.io.IOException;
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
    private static int minTestSuiteSize = 10;
    private static int maxTestSuiteSize = 10;
    private static int populationSize = 10; // Population size for the evolutionary algorithm
    private static int maxEvaluations = 500;
    private static int maxExecutedRequests=100;
    private static double[] mutationProbabilities = {
            0.1, // AddTestCaseMutation
            0.1, // RemoveTestCaseMutation
            0.1, // ReplaceTestCaseMutation
            0.1, // AddParameterMutation
            0.1, // RemoveParameterMutation
            0.1  // RandomParameterValueMutation
    };
    private static double[] crossoverProbabilities = {
            0.1, // UniformTestCaseCrossover
            0.1  // SinglePointTestSuiteCrossover
    };
    // Objective functions: ORDER IS IMPORTANT!!! First one will be used to determine the "best" test suite
    private static List<RestfulAPITestingObjectiveFunction> objectiveFunctions = Lists.newArrayList(
            new Diversity(SimilarityMeter.METRIC.LEVENSHTEIN, Diversity.ELEMENT.INPUT)
//                new SuiteSize()
    );

    // Termination criterion
    private static TerminationCriterion terminationCriterion = or(
            new MaxEvaluations(maxEvaluations),
            new MaxExecutedRequests(maxExecutedRequests)
    );

    // API parameters
    private static OpenAPISpecification spec;
    private static String OAISpecPath = "src/test/resources/languagetool/swagger.json"; // Path to OAS specification file
    private static String confPath = "src/test/resources/languagetool/testConf.yaml"; // Path to test configuration file
    private static String experimentName = "languagetool_prelim" + "_" + RandomStringUtils.randomAlphanumeric(10); // Experiment name
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
                crossoverProbabilities,
                terminationCriterion,
                runner
        );

        try {
            generator.run();
            Timer.stopCounting(ALL);
            generateTimeReport();

            logger.info("Results saved to folder {}", experimentName);
        } catch (IOException ex) {
            logger.error(ex);
        }

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