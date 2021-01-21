package es.us.isa.restest.searchbased.experiment;

import java.io.Serializable;
import java.util.List;

import org.uma.jmetal.lab.experiment.util.ExperimentAlgorithm;
import org.uma.jmetal.lab.experiment.util.ExperimentProblem;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;

import es.us.isa.restest.searchbased.SearchBasedTestSuiteGenerator;
import es.us.isa.restest.searchbased.algorithms.SearchBasedAlgorithm;
import es.us.isa.restest.searchbased.constraints.ObjectiveFunctionConstraint;
import es.us.isa.restest.searchbased.constraints.OptimizationConstraint;
import es.us.isa.restest.searchbased.objectivefunction.RestfulAPITestingObjectiveFunction;
import es.us.isa.restest.searchbased.reporting.ExperimentReport;
import es.us.isa.restest.searchbased.terminationcriteria.MaxEvaluations;
import es.us.isa.restest.searchbased.terminationcriteria.TerminationCriterion;
import es.us.isa.restest.specification.OpenAPISpecification;

import java.util.ArrayList;
//import javax.annotation.Generated;
import java.util.Collections;

/**
 * This class represents the configuration of an algorithm in the context of an
 * experiment
 * 
 * @author japarejo
 *
 */

public class ExperimentalConfiguration extends ExperimentAlgorithm implements Serializable {
	int id;
	// Experiment data:
	String experimentName;	
	String targetDir; // Directory where tests will be generated.
	String packageName; // Package name
	String testClassName; // Name of the class where tests will be written.
	long seed;
	// Problem data:
	String problemTag;
	String openApiSpecPath;
	String configFilePath;
	int minTestSuiteSize;
	int maxTestSuiteSize;
	List<RestfulAPITestingObjectiveFunction> objectiveFunctions;
	List<OptimizationConstraint> constraints;
	// Algorithm data:
	SearchBasedAlgorithm algorithm;

	// @Generated("SparkTools")
	private ExperimentalConfiguration(DefaultAlgorithmBuilder builder) {
		super(null,builder.algorithmTag, null,1);
		this.problemTag = builder.problemTag;
		this.experimentName = builder.experimentName;
		this.openApiSpecPath = builder.openApiSpecPath;
		this.configFilePath = builder.configFilePath;
		this.targetDir = builder.targetDir;
		this.packageName = builder.packageName;
		this.testClassName = builder.testClassName;
		this.seed = builder.seed;
		this.objectiveFunctions = builder.objectiveFunctions;
		this.constraints = builder.constraints;
		this.minTestSuiteSize = builder.minTestSuiteSize;
		this.maxTestSuiteSize = builder.maxTestSuiteSize;		
		
		this.algorithm = SearchBasedTestSuiteGenerator.
				createDefaultAlgorithm(seed, 
						builder.populationSize, 
						builder.mutationProbabilities, 
						builder.crossoverProbability,
						SearchBasedTestSuiteGenerator.buildProblem(
								new OpenAPISpecification(openApiSpecPath), 
								configFilePath, 
								objectiveFunctions, 
								targetDir, 
								minTestSuiteSize, 
								maxTestSuiteSize), 
						builder.terminationCriterion);
	}

	public ExperimentalConfiguration(Builder builder) {
		super(null,builder.algorithmTag, null,1);
		this.problemTag = builder.problemTag;
		this.experimentName = builder.experimentName;
		this.openApiSpecPath = builder.openApiSpecPath;
		this.configFilePath = builder.configFilePath;
		this.targetDir = builder.targetDir;
		this.packageName = builder.packageName;
		this.testClassName = builder.testClassName;
		this.seed = builder.seed;
		this.objectiveFunctions = builder.objectiveFunctions;
		this.minTestSuiteSize = builder.minTestSuiteSize;
		this.maxTestSuiteSize = builder.maxTestSuiteSize;
		this.algorithm = builder.algorithm;
	}
	
	public int getRunId() {
	    return this.id;
	  }

	/**
	 * Creates builder to build {@link ExperimentalConfiguration}.
	 * 
	 * @return created builder
	 */
	// @Generated("SparkTools")
	public static DefaultAlgorithmBuilder defaultAlgorithmBuilder() {
		return new DefaultAlgorithmBuilder();
	}
	
	public static Builder builder() {
		return new Builder();
	}

	/**
	 * Builder to build {@link ExperimentalConfiguration}.
	 */
	// @Generated("SparkTools")
	public static final class DefaultAlgorithmBuilder {
		private String algorithmTag;
		private String problemTag;
		private String experimentName;
		private String openApiSpecPath;
		private String configFilePath;
		private String targetDir;
		private String packageName;
		private String testClassName;
		private long seed;
		private int minTestSuiteSize;
		private int maxTestSuiteSize;
		private int populationSize;
		private double[] mutationProbabilities;
		private double crossoverProbability;
		private List<RestfulAPITestingObjectiveFunction> objectiveFunctions = Collections.emptyList();
		private List<OptimizationConstraint> constraints= new ArrayList<OptimizationConstraint>();
		private TerminationCriterion terminationCriterion;

		private DefaultAlgorithmBuilder() {
		}

		public DefaultAlgorithmBuilder withOpenApiSpecPath(String openApiSpecPath) {
			this.openApiSpecPath = openApiSpecPath;
			return this;
		}

		public DefaultAlgorithmBuilder withExperimentName(String experimentName) {
			this.experimentName = experimentName;
			return this;
		}

		public DefaultAlgorithmBuilder withTargetDir(String targetDir) {
			this.targetDir = targetDir;
			return this;
		}

		public DefaultAlgorithmBuilder withConfigFilePath(String configFilePath) {
			this.configFilePath = configFilePath;
			return this;
		}

		public DefaultAlgorithmBuilder withPackageName(String packageName) {
			this.packageName = packageName;
			return this;
		}

		public DefaultAlgorithmBuilder withTestClassName(String testClassName) {
			this.testClassName = testClassName;
			return this;
		}

		public DefaultAlgorithmBuilder withSeed(long seed) {
			this.seed = seed;
			return this;
		}

		public DefaultAlgorithmBuilder withMinTestSuiteSize(int minTestSuiteSize) {
			this.minTestSuiteSize = minTestSuiteSize;
			return this;
		}

		public DefaultAlgorithmBuilder withMaxTestSuiteSize(int maxTestSuiteSize) {
			this.maxTestSuiteSize = maxTestSuiteSize;
			return this;
		}

		public DefaultAlgorithmBuilder withPopulationSize(int populationSize) {
			this.populationSize = populationSize;
			return this;
		}

		public DefaultAlgorithmBuilder withMutationProbabilities(double [] mutationProbabilities) {
			this.mutationProbabilities = mutationProbabilities;
			return this;
		}

		public DefaultAlgorithmBuilder withCrossoverProbability(double probability) {
			this.crossoverProbability = probability;
			return this;
		}

		public DefaultAlgorithmBuilder withObjectiveFunctions(
				List<RestfulAPITestingObjectiveFunction> objectiveFunctions) {
			this.objectiveFunctions = objectiveFunctions;
			return this;
		}

		public DefaultAlgorithmBuilder withTerminationCriterion(TerminationCriterion terminationCriterion) {
			this.terminationCriterion = terminationCriterion;
			return this;
		}
		
		public DefaultAlgorithmBuilder withConstraints(List<OptimizationConstraint> constraints) {
			this.constraints=constraints;
			return this;
		}
		
		public DefaultAlgorithmBuilder withProblemTag(String problemTag) {
			this.problemTag=problemTag;
			return this;
		}

		public ExperimentalConfiguration build() {
			return new ExperimentalConfiguration(this);
		}
	}

	public static final class Builder {
		public String algorithmTag;
		private String experimentTag;
		private String problemTag;
		private String experimentName;
		private String openApiSpecPath;
		private String configFilePath;
		private String targetDir;
		private String packageName;
		private String testClassName;
		private List<RestfulAPITestingObjectiveFunction> objectiveFunctions = Collections.emptyList();
		private long seed;
		private int minTestSuiteSize;
		private int maxTestSuiteSize;
		private SearchBasedAlgorithm algorithm;
		
		private Builder() {
		}

		public Builder withOpenApiSpecPath(String openApiSpecPath) {
			this.openApiSpecPath = openApiSpecPath;
			return this;
		}

		public Builder withExperimentName(String experimentName) {
			this.experimentName = experimentName;
			return this;
		}

		public Builder withTargetDir(String targetDir) {
			this.targetDir = targetDir;
			return this;
		}

		public Builder withConfigFilePath(String configFilePath) {
			this.configFilePath = configFilePath;
			return this;
		}

		public Builder withPackageName(String packageName) {
			this.packageName = packageName;
			return this;
		}

		public Builder withTestClassName(String testClassName) {
			this.testClassName = testClassName;
			return this;
		}

		public Builder withSeed(long seed) {
			this.seed = seed;
			return this;
		}

		public Builder withMinTestSuiteSize(int minTestSuiteSize) {
			this.minTestSuiteSize = minTestSuiteSize;
			return this;
		}

		public Builder withMaxTestSuiteSize(int maxTestSuiteSize) {
			this.maxTestSuiteSize = maxTestSuiteSize;
			return this;
		}

		public Builder withAlgorithm(SearchBasedAlgorithm algorithm) {
			this.algorithm = algorithm;
			return this;
		}
		
		public Builder withObjectiveFunctions(
				List<RestfulAPITestingObjectiveFunction> objectiveFunctions) {
			this.objectiveFunctions = objectiveFunctions;
			return this;
		}
		
		public ExperimentalConfiguration build() {
			return new ExperimentalConfiguration(this);
		}
	}

	public int getId() {
		return id;
	}

	public String getExperimentName() {
		return experimentName;
	}
	

	public String getTargetDir() {
		return targetDir;
	}

	public String getPackageName() {
		return packageName;
	}

	public String getTestClassName() {
		return testClassName;
	}

	public long getSeed() {
		return seed;
	}

	public int getMinTestSuiteSize() {
		return minTestSuiteSize;
	}

	public int getMaxTestSuiteSize() {
		return maxTestSuiteSize;
	}

	public String getOpenApiSpecPath() {
		return openApiSpecPath;
	}

	public String getConfigFilePath() {
		return configFilePath;
	}
	
	public List<RestfulAPITestingObjectiveFunction> getObjectiveFunctions() {
		return objectiveFunctions;
	}

	public SearchBasedAlgorithm getAlgorithm() {
		return algorithm;
	}
	
	public ExperimentReport generateExperimentReport() {
		ExperimentReport experimentReport = new ExperimentReport(experimentName);
		experimentReport.setStoppingCriterion(algorithm.getTerminationCriterion().toString());
		experimentReport.setCurrentStoppingCriterionState(0d);
		if (algorithm.getTerminationCriterion() instanceof MaxEvaluations)
			experimentReport.setStoppingCriterionMax(((MaxEvaluations) algorithm.getTerminationCriterion()).getStoppingCriterionMax());
		experimentReport.setCurrentSolutionIndex(0);
		objectiveFunctions.forEach(objFunc -> objFunc.setExperimentReport(experimentReport));
		// terminationCriterion.setExperimentReport(experimentReport);
		return experimentReport;
	}		

}
