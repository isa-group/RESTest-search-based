package es.us.isa.restest.searchbased.constraints;

import es.us.isa.restest.searchbased.RestfulAPITestSuiteSolution;

public abstract class OptimizationConstraint {	
	
	public abstract double evaluate(RestfulAPITestSuiteSolution solution);
	
	public boolean isMeet(RestfulAPITestSuiteSolution solution) {
		return evaluate(solution)>=0.0;
	}
}
