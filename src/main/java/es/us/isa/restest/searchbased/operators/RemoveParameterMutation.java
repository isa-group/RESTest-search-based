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

import java.util.Collection;

import static es.us.isa.restest.searchbased.operators.Utils.resetTestResult;
import static es.us.isa.restest.searchbased.operators.Utils.updateTestCaseFaultyReason;

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
        for (TestCase testCase : solution.getVariables()) {
            mutationApplied = false;
            for (ParameterFeatures param : getAllPresentParameters(testCase)) {
                if (getRandomGenerator().nextDouble() <= mutationProbability) {
                    doMutation(param, testCase);
                    if (!mutationApplied) mutationApplied = true;
                }
            }

            if (mutationApplied) {
                logger.info("Mutation probability fulfilled! Parameter removed from test case.");
                updateTestCaseFaultyReason(solution, testCase);
                resetTestResult(testCase.getId(), solution); // The test case changed, reset test result
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
