package es.us.isa.restest.runners;

import es.us.isa.restest.generators.AbstractTestCaseGenerator;
import es.us.isa.restest.reporting.AllureReportManager;
import es.us.isa.restest.reporting.StatsReportManager;
import es.us.isa.restest.testcases.TestCase;
import es.us.isa.restest.testcases.writers.IWriter;
import es.us.isa.restest.util.ClassLoader;
import es.us.isa.restest.util.Timer;
import io.qameta.allure.AllureLifecycle;
import io.qameta.allure.FileSystemResultsWriter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;

import java.nio.file.Paths;
import java.util.Collection;

import static es.us.isa.restest.util.Timer.TestStep.TEST_SUITE_EXECUTION;
import static es.us.isa.restest.util.Timer.TestStep.TEST_SUITE_GENERATION;

/**
 * This class extends the basic RESTestRunner to include a needed method for the
 * search-based functionality.
 * @author Alberto Martin-Lopez
 *
 */
public class SearchBasedRunner extends RESTestRunner {

	private static final Logger logger = LogManager.getLogger(SearchBasedRunner.class.getName());

	public SearchBasedRunner(String testClassName, String targetDir, String packageName, AbstractTestCaseGenerator generator, IWriter writer, AllureReportManager reportManager, StatsReportManager statsReportManager) {
		super(testClassName, targetDir, packageName, generator, writer, reportManager, statsReportManager);
	}

	protected void testExecution(Class<?> testClass)  {

		JUnitCore junit = new JUnitCore();
		//junit.addListener(new TextListener(System.out));
		FileSystemResultsWriter allureWriter = new FileSystemResultsWriter(Paths.get(allureReportManager.getResultsDir()));
		AllureLifecycle allureLifecycle = new AllureLifecycle(allureWriter);
		junit.addListener(new io.qameta.allure.junit4.AllureJunit4(allureLifecycle));
		Timer.startCounting(TEST_SUITE_EXECUTION);
		Result result = junit.run(testClass);
		Timer.stopCounting(TEST_SUITE_EXECUTION);
		int successfulTests = result.getRunCount() - result.getFailureCount() - result.getIgnoreCount();
		logger.info("{} tests run in {} seconds. Successful: {}, Failures: {}, Ignored: {}", result.getRunCount(), result.getRunTime()/1000, successfulTests, result.getFailureCount(), result.getIgnoreCount());

	}

	public void run(Collection<TestCase> testCases) {
		// Pass test cases to the statistic report manager (CSV writing, coverage)
		statsReportManager.setTestCases(testCases);

		// Write test cases
		String filePath = targetDir + "/" + testClassName + ".java";
		logger.info("Writing {} test cases to test class {}", testCases.size(), filePath);
		writer.write(testCases);

		// Test execution
		logger.info("Running tests");
		System.setProperty("allure.results.directory", allureReportManager.getResultsDirPath());
		testExecution(getTestClass());

		// Report generation
		generateReports();
	}
}
