package es.us.isa.restest.searchbased.constraints;

import java.util.List;

import es.us.isa.restest.searchbased.RestfulAPITestSuiteGenerationProblem;
import es.us.isa.restest.searchbased.RestfulAPITestSuiteSolution;
import es.us.isa.restest.searchbased.objectivefunction.RestfulAPITestingObjectiveFunction;
import es.us.isa.restest.searchbased.objectivefunction.RestfulAPITestingObjectiveFunction.ObjectiveFunctionType;
import es.us.isa.restest.searchbased.objectivefunction.ValidTestsRatio;

public class ValidTestRatioConstraint implements OptimizationConstraint {

	private double targetValue;
	private ValidTestsRatio validTestRatioObjFunc=new ValidTestsRatio(ObjectiveFunctionType.MAXIMIZATION);
	
	public ValidTestRatioConstraint(double targetValue) {
		this.targetValue=targetValue;
	}
	
	@Override
	public double evaluate(RestfulAPITestSuiteSolution solution) {
		double result=0;
		RestfulAPITestSuiteGenerationProblem problem=solution.getProblem();
		int validTestRatioObjFuncIndex=findValidTestRatioObjFuncIndex(problem.getObjectiveFunctions());
		if(validTestRatioObjFuncIndex>=0)
			result=solution.getObjective(validTestRatioObjFuncIndex)-targetValue;
		else
			result=validTestRatioObjFunc.evaluate(solution)-targetValue;
		return result;
	}

	private int findValidTestRatioObjFuncIndex(List<RestfulAPITestingObjectiveFunction> objectiveFunctions) {
		int index=-1;
		int candidateIndex=0;
		for(RestfulAPITestingObjectiveFunction objFunc:objectiveFunctions) {
			if(objFunc instanceof ValidTestsRatio) {
				index=candidateIndex;
				break;
			}
			candidateIndex++;
		}
		return index;
	}

}
