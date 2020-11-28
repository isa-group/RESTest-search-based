package es.us.isa.restest.searchbased.algorithms;

import es.us.isa.restest.searchbased.RestfulAPITestSuiteGenerationProblem;
import es.us.isa.restest.searchbased.RestfulAPITestSuiteSolution;
import es.us.isa.restest.searchbased.terminationcriteria.TerminationCriterion;

import org.uma.jmetal.algorithm.Algorithm;

import java.io.Serializable;
import java.util.List;

public interface SearchBasedAlgorithm extends Algorithm<List<RestfulAPITestSuiteSolution>>, Serializable {
	public long getEvaluations();
	public RestfulAPITestSuiteGenerationProblem getProblem();
	public List<RestfulAPITestSuiteSolution> currentSolutions();
	public TerminationCriterion getTerminationCriterion();
}
