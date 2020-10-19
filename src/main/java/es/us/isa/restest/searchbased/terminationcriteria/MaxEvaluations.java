package es.us.isa.restest.searchbased.terminationcriteria;

import es.us.isa.restest.searchbased.algorithms.SearchBasedAlgorithm;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MaxEvaluations implements TerminationCriterion {

	private int maxEvaluations;
	private static final Logger logger = LogManager.getLogger(MaxEvaluations.class.getName());
	
	public MaxEvaluations(int maxEvaluations) {
		this.maxEvaluations=maxEvaluations;
	}
	
	@Override
	public boolean test(SearchBasedAlgorithm t) {
		logger.info("Stopping criterion state: " + t.getEvaluations() + " / " + maxEvaluations);
		return t.getEvaluations()>=maxEvaluations;
	}

	public String toString() {
		return getClass().getSimpleName() + " - " + maxEvaluations;
	}

}
