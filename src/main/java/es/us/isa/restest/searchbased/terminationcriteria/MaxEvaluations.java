package es.us.isa.restest.searchbased.terminationcriteria;

import es.us.isa.restest.searchbased.algorithms.SearchBasedAlgorithm;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MaxEvaluations extends AbstractTerminationCriterion {

	private int maxEvaluations;

	public MaxEvaluations(int maxEvaluations) {
		super();
		this.maxEvaluations=maxEvaluations;
	}

	@Override
	public Double getStoppingCriterionState(SearchBasedAlgorithm t) {
		return (double) t.getEvaluations();
	}

	@Override
	public Double getStoppingCriterionMax() {
		return (double) maxEvaluations;
	}

	public String toString() {
		return getClass().getSimpleName() + " - " + maxEvaluations;
	}

}
