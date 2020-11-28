/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.us.isa.restest.searchbased.operators;

import es.us.isa.restest.inputs.ITestDataGenerator;
import es.us.isa.restest.searchbased.RestfulAPITestSuiteSolution;
import es.us.isa.restest.specification.ParameterFeatures;
import es.us.isa.restest.testcases.TestCase;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.javatuples.Pair;
import org.uma.jmetal.util.pseudorandom.PseudoRandomGenerator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static es.us.isa.restest.util.SolutionUtils.resetTestResult;
import static es.us.isa.restest.util.SolutionUtils.updateTestCaseFaultyReason;

/**
 *
 * This mutation operation changes the value associated to a parameter
 * previously present in the test case. If the test case does not have any
 * parameter set, the operator will add one randomly.
 *
 * @author japarejo
 */
public class RandomParameterValueMutation extends AbstractMutationOperator {

    private static final Logger logger = LogManager.getLogger(RandomParameterValueMutation.class.getName());

    public RandomParameterValueMutation(double mutationProbability) {super(mutationProbability);}
    
    public RandomParameterValueMutation(double mutationProbability, PseudoRandomGenerator randomGenerator) {
        super(mutationProbability, randomGenerator);
    }

    @Override
    protected void doMutation(double probability, RestfulAPITestSuiteSolution solution) {
        if (getRandomGenerator().nextDouble() <= probability) {
            List<TestCase> testCases = new ArrayList<>(solution.getVariables());
            Collections.shuffle(testCases);

            for (TestCase testCase : testCases) {
                List<ParameterFeatures> presentParams = new ArrayList<>(getNonAuthParameters(getAllPresentParameters(testCase, true), testCase, solution));
                Collections.shuffle(presentParams);

                if (!presentParams.isEmpty()) {
                    ParameterFeatures paramToChange = presentParams.get(0);
                    doMutation(paramToChange, testCase, solution);

                    logger.info("Mutation probability fulfilled! Parameter value changed in test case.");
                    updateTestCaseFaultyReason(solution, testCase);
                    resetTestResult(testCase.getId(), solution); // The test case changed, reset test result
                    break;
                }
            }
        }
    }

    private void doMutation(ParameterFeatures paramFeatures, TestCase testCase, RestfulAPITestSuiteSolution solution) {
        ITestDataGenerator generator = solution.getProblem().getTestCaseGenerators().get(testCase.getOperationId()).getGenerators().get(Pair.with(paramFeatures.getName(), paramFeatures.getIn()));
        testCase.addParameter(paramFeatures, generator.nextValueAsString());
    }
}
