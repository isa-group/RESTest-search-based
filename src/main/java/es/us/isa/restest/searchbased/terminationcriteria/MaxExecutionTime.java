package es.us.isa.restest.searchbased.terminationcriteria;

import es.us.isa.restest.searchbased.algorithms.SearchBasedAlgorithm;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MaxExecutionTime extends AbstractTerminationCriterion{

	private Long start;
	private long duration;
	
	public MaxExecutionTime(long duration, TimeUnit unit) {
		super();
		this.duration=Math.abs(duration)*unit.multiplier;
		this.start=null;
	}

	public String toString() {
		return getClass().getSimpleName() + " - " + duration;
	}

	public enum TimeUnit {
		MILLISECONDS(1),
		SECONDS(1000),
		MINUTES(60000),
		HOURS(3600000);		 
		private long multiplier;
		TimeUnit(long multiplier){this.multiplier = multiplier;}
	}

	@Override
	public Double getStoppingCriterionState(SearchBasedAlgorithm t) {
		if(start==null) {
			start=System.currentTimeMillis();
		}
		long now = System.currentTimeMillis();
		return (double) (now - start);
	}

	@Override
	public Double getStoppingCriterionMax() {
		return (double) duration;
	}

	@Override
	public void reset() {
		start = null;
	}
}
