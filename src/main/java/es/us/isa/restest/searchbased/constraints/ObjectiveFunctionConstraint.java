package es.us.isa.restest.searchbased.constraints;

import java.security.InvalidParameterException;
import java.util.List;

import es.us.isa.restest.searchbased.RestfulAPITestSuiteGenerationProblem;
import es.us.isa.restest.searchbased.RestfulAPITestSuiteSolution;
import es.us.isa.restest.searchbased.objectivefunction.RestfulAPITestingObjectiveFunction;
import es.us.isa.restest.searchbased.objectivefunction.ValidTestsRatio;


public class ObjectiveFunctionConstraint extends OptimizationConstraint {	
	private RestfulAPITestingObjectiveFunction objFunc;
	
	private double targetValue;
	
	private ConstraintType type;
	
	public ObjectiveFunctionConstraint(ConstraintType type2, RestfulAPITestingObjectiveFunction objFunc2,
			double targetValue2) {
		this.type=type2;
		this.objFunc=objFunc2;
		this.targetValue=targetValue2;
	}
	
	public static ObjectiveFunctionConstraintBuilder the(ConstraintType type) {
		return new ObjectiveFunctionConstraintBuilder(type);
	}

	@Override
	public double evaluate(RestfulAPITestSuiteSolution solution) {
		double result=0;
		RestfulAPITestSuiteGenerationProblem problem=solution.getProblem();
		int objFuncIndex=findObjFuncIndex(problem.getObjectiveFunctions());
		if(objFuncIndex>=0)
			result=meetingDistance(solution.getObjective(objFuncIndex));
		else
			result=meetingDistance(objFunc.evaluate(solution));
		return result;
	}
		
	
	
	
	private double meetingDistance(double objective) {
		double result=objective-targetValue;
		if(type.equals(ConstraintType.MAXIMUM))
			result=targetValue-objective;
		return result;
	}

	@Override
	public String toString() {
		
		return objFunc.toString()+type+targetValue;
	}
	
	private int findObjFuncIndex(List<RestfulAPITestingObjectiveFunction> objectiveFunctions) {
		int index=-1;
		int candidateIndex=0;
		for(RestfulAPITestingObjectiveFunction candidateObjFunc:objectiveFunctions) {
			if(candidateObjFunc.getClass().isAssignableFrom(objFunc.getClass())) {
				index=candidateIndex;
				break;
			}
			candidateIndex++;
		}
		return index;
	}
	
	public enum ConstraintType{MINIMUM,MAXIMUM;
	
		@Override
		public String toString() {
			if(this.equals(MINIMUM)){
				return ">=";
			}else {			
				return "<=";
			}
		}
	
	}
	
	public static final  class ObjectiveFunctionConstraintBuilder {
		private RestfulAPITestingObjectiveFunction objFunc;
		
		private double targetValue;
		
		private ConstraintType type;		
		
		private ObjectiveFunctionConstraintBuilder(ConstraintType type2) {
			this.type=type2;
		}
		
		
		public ObjectiveFunctionConstraintBuilder valueOf(RestfulAPITestingObjectiveFunction obj) {
			this.objFunc=obj;
			return this;
		}

		public ObjectiveFunctionConstraint is(double targetValue) {
			this.targetValue=targetValue;
			if(objFunc==null) {
				throw new InvalidParameterException("You must specify a objective function for the constraint");
			}
			return new ObjectiveFunctionConstraint(type,objFunc,targetValue);
		}
	}	
	
		
	
}
