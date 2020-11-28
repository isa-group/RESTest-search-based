package es.us.isa.restest.searchbased.constraints;

import es.us.isa.restest.searchbased.RestfulAPITestSuiteSolution;

public interface OptimizationConstraint {	
	public double evaluate(RestfulAPITestSuiteSolution solution);
}
