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
import org.uma.jmetal.util.pseudorandom.PseudoRandomGenerator;

import java.util.ArrayList;
import java.util.Collection;
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

	boolean removePathParameters;
	boolean removeSecurityParameters;
	
    public RemoveParameterMutation(double mutationProbability, PseudoRandomGenerator randomGenerator) {
        this(mutationProbability, randomGenerator,false,false);
    }
    
    public RemoveParameterMutation(double mutationProbability, PseudoRandomGenerator randomGenerator, boolean removePathParameters) {
		this(mutationProbability,randomGenerator,removePathParameters,false);
    }
    
    public RemoveParameterMutation(double mutationProbability, PseudoRandomGenerator randomGenerator, boolean removePathParameters,boolean removeSecurityParameters) {
    	super(mutationProbability,randomGenerator);
		this.removePathParameters=removePathParameters;
		this.removeSecurityParameters=removeSecurityParameters;
	}

	@Override
    protected void doMutation(double mutationProbability, RestfulAPITestSuiteSolution solution) {
        if (getRandomGenerator().nextDouble() <= mutationProbability) {
            int maxTestCasesToMutate = (int) (getRandomGenerator().nextDouble() * maxMutationsRatio * solution.getNumberOfVariables());
            List<TestCase> testCases = new ArrayList<>(solution.getVariables());
            Collections.shuffle(testCases);
            TestCase testCase;

            for (int index=0; index < maxTestCasesToMutate; index++) {
                testCase = testCases.get(index);
                List<ParameterFeatures> presentParams = new ArrayList<>(getAllPresentParameters(testCase));
                Collections.shuffle(presentParams);

                if (!presentParams.isEmpty()) {
                    ParameterFeatures paramToChange = presentParams.get(0);
                    doMutation(paramToChange, testCase);

                    logger.info("Mutation probability fulfilled! Parameter removed from test case.");
                    updateTestCaseFaultyReason(solution, testCase);
                    resetTestResult(testCase.getId(), solution); // The test case changed, reset test result
                }
            }
        }
    }
    
    private void doMutation(ParameterFeatures param, TestCase testCase) {
        testCase.removeParameter(param);
    }
    
    @Override
    protected Collection<ParameterFeatures> getAllPresentParameters(TestCase testCase) {
    	return getAllPresentParameters(testCase, removePathParameters,removeSecurityParameters);
    }
    
}
