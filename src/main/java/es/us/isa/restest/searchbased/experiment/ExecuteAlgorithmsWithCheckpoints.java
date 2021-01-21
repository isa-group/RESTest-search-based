package es.us.isa.restest.searchbased.experiment;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.uma.jmetal.lab.experiment.component.ExperimentComponent;
import org.uma.jmetal.lab.experiment.util.ExperimentAlgorithm;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.JMetalLogger;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;

public class ExecuteAlgorithmsWithCheckpoints<S extends Solution<?>, Result extends List<S>> implements ExperimentComponent {

	private static final Logger logger = LogManager.getLogger(ExecuteAlgorithmsWithCheckpoints.class.getName());
	
	private CheckpointedExperiment<S, Result> experiment;
		
	@SuppressWarnings("unchecked")
	public static <S extends Solution<?>, Result extends List<S>> CheckpointedExperiment<S,Result> loadExperimentFromCheckpoint(String checkpointFile) {
		CheckpointedExperiment<S,Result> experiment=null;
		ObjectMapper JSONmapper=new ObjectMapper();	
		try {			
			experiment=(CheckpointedExperiment<S, Result>) JSONmapper.readValue(checkpointFile, CheckpointedExperiment.class);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return experiment;
		
	}
	
	
	public ExecuteAlgorithmsWithCheckpoints(CheckpointedExperiment<S, Result> configuration, int checkpointFrequency) {
		this.experiment=configuration;		
	}	
	
	public CheckpointedExperiment<S, Result> getExperiment() {
		return experiment;
	}
	
	 @Override
	  public void run() {
	    JMetalLogger.logger.info("ExecuteAlgorithms: Preparing output directory");
	    prepareOutputDirectory();

	    System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism",
	            "" + experiment.getNumberOfCores());
	    
	    List<List<ExperimentAlgorithm<S, Result>>> partitions=Lists.partition(experiment.getAlgorithmList(), experiment.getNumberOfCores());
	    Queue<ExperimentAlgorithm<S, Result>> executedAlgorithms=new ConcurrentLinkedQueue<>();
	    int checkpointingIndex=0;
	    for(List<ExperimentAlgorithm<S, Result>> chunk:partitions) {	    	
	        chunk.parallelStream()
	            .forEach(algorithm -> algorithm.runAlgorithm(experiment));
	        executedAlgorithms.addAll(chunk);
	        if(executedAlgorithms.size()/experiment.getCheckpointFrequency()>checkpointingIndex) {
	        	saveCheckPoint(experiment.getAlgorithmList(),executedAlgorithms);
	        	checkpointingIndex=executedAlgorithms.size()/experiment.getCheckpointFrequency();
	        }
	    }
	  }
	
	
	 private void saveCheckPoint(List<ExperimentAlgorithm<S, Result>> algorithmList,
	     Queue<ExperimentAlgorithm<S, Result>> executedAlgorithms) {
		 ObjectMapper JSONmapper=new ObjectMapper();
	    	String checkpointPath ="target/test-data/checkpoint.json";
			List<ExperimentAlgorithm<S, Result>> expConfsToSave=algorithmList.stream()
					.filter((expConf) -> !executedAlgorithms.contains(expConf))
					.collect(Collectors.toList());
			try {
				JSONmapper.writeValue(new File(checkpointPath), expConfsToSave);
			} catch (IOException e) {
				logger.error("Unable to save checkpoint!:"+e.getLocalizedMessage());
				e.printStackTrace();
			}
		
	}

	private void prepareOutputDirectory() {
		    if (experimentDirectoryDoesNotExist()) {
		      createExperimentDirectory();
		    }
		  }

		  private boolean experimentDirectoryDoesNotExist() {
		    boolean result;
		    File experimentDirectory;

		    experimentDirectory = new File(experiment.getExperimentBaseDirectory());
		    if (experimentDirectory.exists() && experimentDirectory.isDirectory()) {
		      result = false;
		    } else {
		      result = true;
		    }

		    return result;
		  }

		  private void createExperimentDirectory() {
		    File experimentDirectory;
		    experimentDirectory = new File(experiment.getExperimentBaseDirectory());

		    if (experimentDirectory.exists()) {
		      experimentDirectory.delete();
		    }

		    boolean result;
		    result = new File(experiment.getExperimentBaseDirectory()).mkdirs();
		    if (!result) {
		      throw new JMetalException("Error creating experiment directory: " +
		              experiment.getExperimentBaseDirectory());
		    }
		  }
	

}
