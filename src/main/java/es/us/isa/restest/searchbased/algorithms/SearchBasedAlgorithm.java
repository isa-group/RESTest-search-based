package es.us.isa.restest.searchbased.algorithms;

import es.us.isa.restest.searchbased.RestfulAPITestSuiteGenerationProblem;
import es.us.isa.restest.searchbased.RestfulAPITestSuiteSolution;
import org.uma.jmetal.algorithm.Algorithm;

import java.util.List;

public interface SearchBasedAlgorithm extends Algorithm<List<RestfulAPITestSuiteSolution>>{
	public long getEvaluations();
	public RestfulAPITestSuiteGenerationProblem getProblem();
	public List<RestfulAPITestSuiteSolution> currentSolutions();
}
