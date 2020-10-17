/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.us.isa.restest.searchbased.objectivefunction;

import es.us.isa.restest.searchbased.RestfulAPITestSuiteSolution;
import es.us.isa.restest.testcases.TestCase;

/**
 *
 * @author japarejo
 */
public class BalanceOfValidTestsRatio extends RestfulAPITestingObjectiveFunction {	

	public static final double DEFAULT_TARGET_RATIO = 0.9;
	
	private double targetRatio;
	
	public BalanceOfValidTestsRatio() {
		this(DEFAULT_TARGET_RATIO);		
	}		
	
	public BalanceOfValidTestsRatio (double targetRatio) {
		super(ObjectiveFunctionType.MINIMIZATION,false,true);
		this.targetRatio=targetRatio;
	}
	
    @Override
    public Double evaluate(RestfulAPITestSuiteSolution solution) {
        double validTestCases = solution.getVariables().stream().filter(tc -> !tc.getFaulty()).count();

        double ratio = Math.abs(targetRatio - (validTestCases/solution.getNumberOfVariables()));
        logEvaluation(ratio);
        return ratio;
    }

	public double getTargetRatio() {
		return targetRatio;
	}
	
    
}
