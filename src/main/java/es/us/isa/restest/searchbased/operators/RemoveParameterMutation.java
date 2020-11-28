/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.us.isa.restest.searchbased.operators;

import es.us.isa.restest.searchbased.RestfulAPITestSuiteSolution;
import es.us.isa.restest.specification.ParameterFeatures;
import es.us.isa.restest.testcases.TestCase;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.checkerframework.checker.units.qual.m;
import org.uma.jmetal.util.pseudorandom.PseudoRandomGenerator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static es.us.isa.restest.util.SolutionUtils.resetTestResult;
import static es.us.isa.restest.util.SolutionUtils.updateTestCaseFaultyReason;

/**
 *
 * @author japar
 */
public class RemoveParameterMutation extends AbstractMutationOperator {

    private static final Logger logger = LogManager.getLogger(RemoveParameterMutation.class.getName());

    public RemoveParameterMutation(double mutationProbability) {super(mutationProbability);}
    
    public RemoveParameterMutation(double mutationProbability, PseudoRandomGenerator randomGenerator) {
    	super(mutationProbability,randomGenerator);
	}

	@Override
    protected void doMutation(double mutationProbability, RestfulAPITestSuiteSolution solution) {
        if (getRandomGenerator().nextDouble() <= mutationProbability) {
            List<TestCase> testCases = new ArrayList<>(solution.getVariables());
            Collections.shuffle(testCases);

            for (TestCase testCase : testCases) {
                List<ParameterFeatures> presentParams = new ArrayList<>(getNonAuthParameters(getAllPresentParameters(testCase), testCase, solution));
                Collections.shuffle(presentParams);

                if (!presentParams.isEmpty()) {
                    ParameterFeatures paramToChange = presentParams.get(0);
                    doMutation(paramToChange, testCase);

                    logger.info("Mutation probability fulfilled! Parameter removed from test case.");
                    updateTestCaseFaultyReason(solution, testCase);
                    resetTestResult(testCase.getId(), solution); // The test case changed, reset test result
                    break;
                }
            }
        }
    }
    
    private void doMutation(ParameterFeatures param, TestCase testCase) {
        testCase.removeParameter(param);
    }

}
