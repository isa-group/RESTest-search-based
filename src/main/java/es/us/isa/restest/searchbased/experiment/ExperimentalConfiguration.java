package es.us.isa.restest.searchbased.experiment;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import es.us.isa.restest.searchbased.objectivefunction.RestfulAPITestingObjectiveFunction;
import es.us.isa.restest.searchbased.operators.AbstractCrossoverOperator;
import es.us.isa.restest.searchbased.operators.AbstractMutationOperator;
import es.us.isa.restest.searchbased.reporting.ExperimentReport;
import es.us.isa.restest.searchbased.terminationcriteria.MaxEvaluations;
import es.us.isa.restest.searchbased.terminationcriteria.TerminationCriterion;
//import javax.annotation.Generated;
import java.util.Collections;

/**
 * This class represents the configuration of an algorithm in the context of an experiment
 * 
 * @author japarejo
 *
 */


public class ExperimentalConfiguration implements Serializable {
	int id;
	String experimentName;
	String openApiSpecPath;
	String configFilePath;
    String targetDir; // Directory where tests will be generated.
    String packageName; // Package name
    String testClassName; // Name of the class where tests will be written.
    long seed;
	
	
	int minTestSuiteSize; 
    int maxTestSuiteSize;
    int populationSize;		 // Population size for the evolutionary algorithm    
    Map<AbstractMutationOperator,Double> mutationProbabilities; // These will include usually:
    	// AddTestCaseMutation
    	// RemoveTestCaseMutation
    	// ReplaceTestCaseMutation
    	// AddParameterMutation
    	// RemoveParameterMutation
    	// RandomParameterValueMutation
    Map<AbstractCrossoverOperator,Double> crossoverProbabilities; 
    List<RestfulAPITestingObjectiveFunction> objectiveFunctions;
    TerminationCriterion terminationCriterion;


	//@Generated("SparkTools")
	private ExperimentalConfiguration(Builder builder) {
		this.experimentName = builder.experimentName;
		this.openApiSpecPath = builder.openApiSpecPath;
		this.configFilePath = builder.configFilePath;
		this.targetDir = builder.targetDir;
		this.packageName = builder.packageName;
		this.testClassName = builder.testClassName;
		this.seed = builder.seed;
		this.minTestSuiteSize = builder.minTestSuiteSize;
		this.maxTestSuiteSize = builder.maxTestSuiteSize;
		this.populationSize = builder.populationSize;
		this.mutationProbabilities = builder.mutationProbabilities;
		this.crossoverProbabilities = builder.crossoverProbabilities;
		this.objectiveFunctions = builder.objectiveFunctions;
		this.terminationCriterion = builder.terminationCriterion;
	}
	
    
    /**
	 * Creates builder to build {@link ExperimentalConfiguration}.
	 * @return created builder
	 */
	//@Generated("SparkTools")
	public static Builder builder() {
		return new Builder();
	}
	/**
	 * Builder to build {@link ExperimentalConfiguration}.
	 */
	//@Generated("SparkTools")
	public static final class Builder {
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
		private Map<AbstractMutationOperator, Double> mutationProbabilities = Collections.emptyMap();
		private Map<AbstractCrossoverOperator, Double> crossoverProbabilities = Collections.emptyMap();
		private List<RestfulAPITestingObjectiveFunction> objectiveFunctions = Collections.emptyList();
		private TerminationCriterion terminationCriterion;

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

		public Builder withPopulationSize(int populationSize) {
			this.populationSize = populationSize;
			return this;
		}

		public Builder withMutationProbabilities(Map<AbstractMutationOperator, Double> mutationProbabilities) {
			this.mutationProbabilities = mutationProbabilities;
			return this;
		}

		public Builder withCrossoverProbabilities(Map<AbstractCrossoverOperator, Double> crossoverProbabilities) {
			this.crossoverProbabilities = crossoverProbabilities;
			return this;
		}

		public Builder withObjectiveFunctions(List<RestfulAPITestingObjectiveFunction> objectiveFunctions) {
			this.objectiveFunctions = objectiveFunctions;
			return this;
		}

		public Builder withTerminationCriterion(TerminationCriterion terminationCriterion) {
			this.terminationCriterion = terminationCriterion;
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


	public int getPopulationSize() {
		return populationSize;
	}


	public Map<AbstractMutationOperator, Double> getMutationProbabilities() {
		return mutationProbabilities;
	}


	public Map<AbstractCrossoverOperator, Double> getCrossoverProbabilities() {
		return crossoverProbabilities;
	}


	public List<RestfulAPITestingObjectiveFunction> getObjectiveFunctions() {
		return objectiveFunctions;
	}


	public TerminationCriterion getTerminationCriterion() {
		return terminationCriterion;
	}
    
	public String getOpenApiSpecPath() {
		return openApiSpecPath;
	}
	
	public String getConfigFilePath() {
		return configFilePath;
	}


	public ExperimentReport generateExperimentReport() {
		ExperimentReport experimentReport=new ExperimentReport(experimentName);
        experimentReport.setStoppingCriterion(terminationCriterion.toString());
        experimentReport.setCurrentStoppingCriterionState(0d);
        if(terminationCriterion instanceof MaxEvaluations)
        	experimentReport.setStoppingCriterionMax(((MaxEvaluations)terminationCriterion).getStoppingCriterionMax());
        experimentReport.setCurrentSolutionIndex(0);
        objectiveFunctions.forEach(objFunc -> objFunc.setExperimentReport(experimentReport));
        //terminationCriterion.setExperimentReport(experimentReport);
        return experimentReport;
	}
    
    
}
