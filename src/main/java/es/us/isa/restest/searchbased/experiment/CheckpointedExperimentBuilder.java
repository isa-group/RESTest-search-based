package es.us.isa.restest.searchbased.experiment;

import java.util.ArrayList;
import java.util.List;

import org.uma.jmetal.qualityindicator.impl.GenericIndicator;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.experiment.Experiment;
import org.uma.jmetal.util.experiment.ExperimentBuilder;
import org.uma.jmetal.util.experiment.util.ExperimentAlgorithm;
import org.uma.jmetal.util.experiment.util.ExperimentProblem;

public class CheckpointedExperimentBuilder<S extends Solution<?>, Result extends List<S>> {
	private final String experimentName;
	private List<ExperimentAlgorithm<S, Result>> algorithmList;
	private List<ExperimentProblem<S>> problemList;
	private String referenceFrontDirectory;
	private String experimentBaseDirectory;
	private String outputParetoFrontFileName;
	private String outputParetoSetFileName;
	private int independentRuns;
	private List<GenericIndicator<S>> indicatorList;
	private int numberOfCores;
	private int checkpointFrequency;

	public CheckpointedExperimentBuilder(String experimentName) {
		this.experimentName = experimentName;
		this.independentRuns = 1;
		this.numberOfCores = 1;
		this.referenceFrontDirectory = null;
		this.checkpointFrequency = Integer.MAX_VALUE; // No checkpointing
	}

	public CheckpointedExperimentBuilder<S, Result> setAlgorithmList(
			List<ExperimentAlgorithm<S, Result>> algorithmList) {
		this.algorithmList = new ArrayList<>(algorithmList);

		return this;
	}

	public CheckpointedExperimentBuilder<S, Result> setProblemList(List<ExperimentProblem<S>> problemList) {
		this.problemList = problemList;

		return this;
	}

	public CheckpointedExperimentBuilder<S, Result> setExperimentBaseDirectory(String experimentBaseDirectory) {
		this.experimentBaseDirectory = experimentBaseDirectory + "/" + experimentName;

		return this;
	}

	public CheckpointedExperimentBuilder<S, Result> setReferenceFrontDirectory(String referenceFrontDirectory) {
		this.referenceFrontDirectory = referenceFrontDirectory;

		return this;
	}

	public CheckpointedExperimentBuilder<S, Result> setIndicatorList(List<GenericIndicator<S>> indicatorList) {
		this.indicatorList = indicatorList;

		return this;
	}

	public CheckpointedExperimentBuilder<S, Result> setOutputParetoFrontFileName(String outputParetoFrontFileName) {
		this.outputParetoFrontFileName = outputParetoFrontFileName;

		return this;
	}

	public CheckpointedExperimentBuilder<S, Result> setOutputParetoSetFileName(String outputParetoSetFileName) {
		this.outputParetoSetFileName = outputParetoSetFileName;

		return this;
	}

	public CheckpointedExperimentBuilder<S, Result> setIndependentRuns(int independentRuns) {
		this.independentRuns = independentRuns;

		return this;
	}

	public CheckpointedExperimentBuilder<S, Result> setNumberOfCores(int numberOfCores) {
		this.numberOfCores = numberOfCores;

		return this;
	}

	public CheckpointedExperiment<S, Result> build() {
		return new CheckpointedExperiment<S, Result>(this);
	}

	/* Getters */
	public String getExperimentName() {
		return experimentName;
	}

	public List<ExperimentAlgorithm<S, Result>> getAlgorithmList() {
		return algorithmList;
	}

	public List<ExperimentProblem<S>> getProblemList() {
		return problemList;
	}

	public String getExperimentBaseDirectory() {
		return experimentBaseDirectory;
	}

	public String getOutputParetoFrontFileName() {
		return outputParetoFrontFileName;
	}

	public String getOutputParetoSetFileName() {
		return outputParetoSetFileName;
	}

	public int getIndependentRuns() {
		return independentRuns;
	}

	public int getNumberOfCores() {
		return numberOfCores;
	}

	public String getReferenceFrontDirectory() {
		return referenceFrontDirectory;
	}

	public List<GenericIndicator<S>> getIndicatorList() {
		return indicatorList;
	}

	public int getCheckpointFrequency() {
		return checkpointFrequency;
	}
	
	public ExperimentBuilder<S,Result> getExperimentBuilder(){
		return new ExperimentBuilder<S, Result>(this.experimentName)
				.setExperimentBaseDirectory(this.experimentBaseDirectory)
				.setAlgorithmList(this.algorithmList)
				.setProblemList(this.problemList)
				.setReferenceFrontDirectory(this.referenceFrontDirectory)		
				.setOutputParetoFrontFileName(this.outputParetoFrontFileName)
				.setOutputParetoSetFileName(this.outputParetoSetFileName)
				.setIndependentRuns(this.independentRuns)
				.setIndicatorList(this.indicatorList)
				.setNumberOfCores(this.numberOfCores);							
	}
}
