package es.us.isa.restest.searchbased.terminationcriteria;

import es.us.isa.restest.searchbased.algorithms.SearchBasedAlgorithm;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MaxExecutedRequests extends AbstractTerminationCriterion {

	private long maxRequestsToBeExecuted;

	public MaxExecutedRequests(long maxRequestsToBeExecuted) {
		super();
		this.maxRequestsToBeExecuted=maxRequestsToBeExecuted;
	}

	@Override
	public Double getStoppingCriterionState(SearchBasedAlgorithm t) {
		return (double) t.getProblem().getTestCasesExecuted();
	}

	@Override
	public Double getStoppingCriterionMax() {
		return (double) maxRequestsToBeExecuted;
	}

	public String toString() {
		return getClass().getSimpleName() + " - " + maxRequestsToBeExecuted;
	}

}
