package es.us.isa.restest.util;

import es.us.isa.restest.searchbased.RestfulAPITestSuiteSolution;
import es.us.isa.restest.testcases.TestCase;
import es.us.isa.restest.util.OASAPIValidator;
import org.apache.commons.text.StringEscapeUtils;

import java.util.List;

import static es.us.isa.restest.testcases.TestCase.checkFulfillsDependencies;

/**
 * This class contains common utilities to all mutation operators
 */
public class SolutionUtils {

    public static void updateTestCaseFaultyReason(RestfulAPITestSuiteSolution solution, TestCase testCase) {
        if (testCase.getEnableOracles()) {
            List<String> faultyReasons = testCase.getValidationErrors(OASAPIValidator.getValidator(solution.getProblem().getApiUnderTest()));
            if (!faultyReasons.isEmpty()) {
                testCase.setFaultyReason("individual_parameter_constraint according to OAS validator: " + StringEscapeUtils.escapeJava((String.join(" / ", faultyReasons))));
                testCase.setFaulty(true);
            } else if (!checkFulfillsDependencies(testCase, solution.getProblem().getTestCaseGenerators().get(testCase.getOperationId()).getIdlReasoner())) {
                testCase.setFaultyReason("inter_parameter_dependency");
                testCase.setFulfillsDependencies(false);
                testCase.setFaulty(true);
            } else {
                testCase.setFaultyReason("none");
                testCase.setFulfillsDependencies(true);
                testCase.setFaulty(false);
            }
        }
    }

    public static void resetTestResult(String testCaseId, RestfulAPITestSuiteSolution solution) {
        solution.setTestResult(testCaseId, null);
    }
}
