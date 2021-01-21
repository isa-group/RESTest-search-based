package es.us.isa.restest.searchbased.experiment;

import java.util.List;

import org.uma.jmetal.lab.experiment.Experiment;
import org.uma.jmetal.solution.Solution;

public class CheckpointedExperiment<S  extends Solution<?>,Result  extends List<S>> extends Experiment<S, Result> 
{
	int checkpointFrequency;

	public CheckpointedExperiment(CheckpointedExperimentBuilder<S, Result> builder) {
		super(builder.getExperimentBuilder());			
		this.checkpointFrequency = builder.getCheckpointFrequency();
	}

	
	public int getCheckpointFrequency() {
		return checkpointFrequency;
	}
	
	
	
	
	

}
