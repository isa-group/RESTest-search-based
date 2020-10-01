/**
 *
 */
package es.us.isa.restest.searchbased;

import com.google.common.collect.Lists;
import es.us.isa.restest.configuration.TestConfigurationIO;
import es.us.isa.restest.configuration.pojos.TestConfigurationObject;
import es.us.isa.restest.runners.RESTestRunner;
import es.us.isa.restest.runners.SearchBasedRunner;
import es.us.isa.restest.searchbased.algorithms.NSGAII;
import es.us.isa.restest.searchbased.objectivefunction.RestfulAPITestingObjectiveFunction;
import es.us.isa.restest.searchbased.operators.*;
import es.us.isa.restest.searchbased.terminationcriteria.MaxEvaluations;
import es.us.isa.restest.searchbased.terminationcriteria.TerminationCriterion;
import es.us.isa.restest.specification.OpenAPISpecification;
import es.us.isa.restest.testcases.TestCase;
import es.us.isa.restest.util.CURLCommandGenerator;
import es.us.isa.restest.util.Timer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.operator.impl.selection.BinaryTournamentSelection;
import org.uma.jmetal.qualityindicator.impl.*;
import org.uma.jmetal.qualityindicator.impl.hypervolume.PISAHypervolume;
import org.uma.jmetal.util.AlgorithmRunner;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.comparator.RankingAndCrowdingDistanceComparator;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;
import org.uma.jmetal.util.experiment.Experiment;
import org.uma.jmetal.util.experiment.ExperimentBuilder;
import org.uma.jmetal.util.experiment.component.*;
import org.uma.jmetal.util.experiment.util.ExperimentAlgorithm;
import org.uma.jmetal.util.experiment.util.ExperimentProblem;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.pseudorandom.impl.MersenneTwisterGenerator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static es.us.isa.restest.util.Timer.TestStep.TEST_SUITE_GENERATION;

public class SearchBasedTestSuiteGenerator {

    private static final Logger logger = LogManager.getLogger(SearchBasedTestSuiteGenerator.class.getName());

    SearchBasedRunner restestRunner;

    // Configuration   
    Integer nsga2PopulationSize = 10;
    Long seed = 1979L;
    TerminationCriterion tc;
    
    // Members:
    RestfulAPITestSuiteGenerationProblem problem;
    private final List<ExperimentProblem<RestfulAPITestSuiteSolution>> problems;
    List<ExperimentAlgorithm<RestfulAPITestSuiteSolution, List<RestfulAPITestSuiteSolution>>> algorithms;
    ExperimentBuilder<RestfulAPITestSuiteSolution, List<RestfulAPITestSuiteSolution>> experimentBuilder;
    
    public SearchBasedTestSuiteGenerator(OpenAPISpecification spec, String configFilePath, String experimentName, List<RestfulAPITestingObjectiveFunction> objectiveFunctions, String targetPath, long seed, int populationSize,TerminationCriterion tc, SearchBasedRunner runner) {
    	this(spec, configFilePath, experimentName, objectiveFunctions,targetPath, seed,null,populationSize,tc, runner);
    }
    public SearchBasedTestSuiteGenerator(OpenAPISpecification spec, String configFilePath, String experimentName, List<RestfulAPITestingObjectiveFunction> objectiveFunctions,String targetPath, long seed, Integer fixedTestSuiteSize, int populationSize,TerminationCriterion tc, SearchBasedRunner runner) {
        this(experimentName,targetPath,seed,buildProblem(spec, configFilePath, objectiveFunctions, targetPath,fixedTestSuiteSize),populationSize,tc, runner);
    }
    
    public SearchBasedTestSuiteGenerator(OpenAPISpecification spec, String configFilePath, String experimentName, List<RestfulAPITestingObjectiveFunction> objectiveFunctions,String targetPath, long seed, Integer minTestSuiteSize,Integer maxTestSuiteSize, int populationSize,TerminationCriterion tc, SearchBasedRunner runner) {
    	this(experimentName,targetPath,seed,buildProblem(spec, configFilePath, objectiveFunctions, targetPath,minTestSuiteSize,maxTestSuiteSize),populationSize,tc, runner);
    }
    
    public SearchBasedTestSuiteGenerator(String experimentName, String targetPath, long seed, RestfulAPITestSuiteGenerationProblem problem, int populationSize,TerminationCriterion tc, SearchBasedRunner runner) {
    	this(experimentName,targetPath,seed,Lists.newArrayList(problem),populationSize,tc, runner);
    }
    
    public SearchBasedTestSuiteGenerator(String experimentName, String targetPath, long seed, List<RestfulAPITestSuiteGenerationProblem> myproblems, int populationSize,TerminationCriterion tc, SearchBasedRunner runner) {
    	this(experimentName,targetPath,seed,myproblems,configureDefaultAlgorithms(seed,populationSize,myproblems,tc), runner);
        this.tc=tc;
    	setPopulationSize(populationSize);
    }
    
    public SearchBasedTestSuiteGenerator(String experimentName, String targetPath, long seed, List<RestfulAPITestSuiteGenerationProblem> myproblems,List<ExperimentAlgorithm<RestfulAPITestSuiteSolution, List<RestfulAPITestSuiteSolution>>> algorithms, SearchBasedRunner runner) {
    	logger.info("Creating search-based experiment");
    	this.restestRunner = runner;
        this.seed=seed;
        JMetalRandom.getInstance().setSeed(seed);
        this.problem=myproblems.get(0);
        this.problems = new ArrayList<>();
        for(RestfulAPITestSuiteGenerationProblem p:myproblems)
        	this.problems.add(new ExperimentProblem<>(p));        
        this.algorithms = algorithms;
         
        experimentBuilder = new ExperimentBuilder<RestfulAPITestSuiteSolution, List<RestfulAPITestSuiteSolution>>(experimentName)
                .setExperimentBaseDirectory(targetPath)
        		.setAlgorithmList(algorithms)
                .setProblemList(problems)
                .setOutputParetoFrontFileName("FUN")
                .setOutputParetoSetFileName("VAR")
                .setReferenceFrontDirectory(targetPath+"/pareto_fronts")
                .setIndicatorList(indicators())
                .setNumberOfCores(1)
                .setIndependentRuns(1);

    }

    
    public static Algorithm<List<RestfulAPITestSuiteSolution>> createDefaultAlgorithm(long seed, int populationSize, int maxEvaluations, RestfulAPITestSuiteGenerationProblem problem){
    	Algorithm<List<RestfulAPITestSuiteSolution>> result=createDefaultAlgorithm(seed, populationSize, problem,new MaxEvaluations(maxEvaluations));
    	return result;
    }
    
    
    public static Algorithm<List<RestfulAPITestSuiteSolution>> createDefaultAlgorithm(long seed, int populationSize, RestfulAPITestSuiteGenerationProblem problem,TerminationCriterion tc){
    	MersenneTwisterGenerator generator=new MersenneTwisterGenerator(seed);
    	Algorithm<List<RestfulAPITestSuiteSolution>> result=null;
    	AllMutationOperators mutation=new AllMutationOperators(Lists.newArrayList(
        		new AddTestCaseMutation(0.1,generator),
        		new RemoveTestCaseMutation(0.1,generator),
                new ReplaceTestCaseMutation(0.1,generator),
    			new AddParameterMutation(0.01,generator),
        		new RemoveParameterMutation(0.01,generator),
        		new RandomParameterValueMutation(0.01,generator),
        		new ResourceChangeMutation(0.01,generator)
        ));
    	AllCrossoverOperators crossover=new AllCrossoverOperators(Lists.newArrayList(
    	        new UniformTestCaseCrossover(0.01),
                new SinglePointTestSuiteCrossover(0.1)
        ));
    	
    	 SelectionOperator<List<RestfulAPITestSuiteSolution>, RestfulAPITestSuiteSolution> selectionOperator= 
    			 new BinaryTournamentSelection<RestfulAPITestSuiteSolution>(new RankingAndCrowdingDistanceComparator<RestfulAPITestSuiteSolution>()) ;;
    	 SolutionListEvaluator<RestfulAPITestSuiteSolution> evaluator=new SequentialSolutionListEvaluator<RestfulAPITestSuiteSolution>();    	     	 
    	 
    	result = new NSGAII(
        		problem, populationSize, populationSize, populationSize,
                crossover,
        		mutation, 
        		selectionOperator, evaluator,
        		tc);			
    	return result;
    }
    
    private static List<ExperimentAlgorithm<RestfulAPITestSuiteSolution, List<RestfulAPITestSuiteSolution>>> configureDefaultAlgorithms(long seed, int populationSize,List<RestfulAPITestSuiteGenerationProblem> myproblems,TerminationCriterion tc) {        	 
    	List<ExperimentAlgorithm<RestfulAPITestSuiteSolution, List<RestfulAPITestSuiteSolution>>> result = new ArrayList<>();
        Algorithm<List<RestfulAPITestSuiteSolution>> algorithm = null; 
        
        ExperimentAlgorithm<RestfulAPITestSuiteSolution, List<RestfulAPITestSuiteSolution>> expAlg=null;
        int runId=0;
        for (RestfulAPITestSuiteGenerationProblem problem : myproblems) {            
        	algorithm=createDefaultAlgorithm(seed, populationSize,problem,tc);
            expAlg=new ExperimentAlgorithm<RestfulAPITestSuiteSolution, List<RestfulAPITestSuiteSolution>>(algorithm, new ExperimentProblem<>(problem), runId);           
            result.add(expAlg);
            runId++;
        }
        return result;
    }    
    
    public static RestfulAPITestSuiteGenerationProblem buildProblem(OpenAPISpecification spec, String configFilePath, List<RestfulAPITestingObjectiveFunction> objFuncs, String targetPath, Integer minTestSuiteSize, Integer maxTestSuiteSize) {
        TestConfigurationObject configuration = TestConfigurationIO.loadConfiguration(configFilePath, spec);
        return new RestfulAPITestSuiteGenerationProblem(spec, configuration, objFuncs, JMetalRandom.getInstance().getRandomGenerator(),minTestSuiteSize,maxTestSuiteSize);
    }
    
    public static RestfulAPITestSuiteGenerationProblem buildProblem(OpenAPISpecification spec, String configFilePath, List<RestfulAPITestingObjectiveFunction> objFuncs, String targetPath, Integer fixedTestSuiteSize) {
        TestConfigurationObject configuration = TestConfigurationIO.loadConfiguration(configFilePath, spec);
        return new RestfulAPITestSuiteGenerationProblem(spec, configuration, objFuncs, JMetalRandom.getInstance().getRandomGenerator(),fixedTestSuiteSize);
    }

    public void run() throws IOException {
    	JMetalLogger.logger.info("Generating testSuites for: " + problem.getName() + " using as objectives :"+ problem.getObjectiveFunctions() );
    	JMetalLogger.logger.info("Starting the execution of: " + algorithms.get(0).getAlgorithm().getClass().getSimpleName());
        Timer.startCounting(TEST_SUITE_GENERATION);
    	 AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor((Algorithm<?>) algorithms.get(0).getAlgorithm())
    		        .execute() ;
        Timer.stopCounting(TEST_SUITE_GENERATION);
    	 long computingTime = algorithmRunner.getComputingTime() ;

    	 List<RestfulAPITestSuiteSolution> suites=algorithms.get(0).getAlgorithm().getResult();

    	 JMetalLogger.logger.info("Total execution time: " + computingTime + "ms");
    	 JMetalLogger.logger.info("The algorithm generated "+suites.size()+" test suites.");
    	 JMetalLogger.logger.info("Objectives values have been written to file FUN.tsv");
    	 JMetalLogger.logger.info("Variables values have been written to file VAR.tsv");
    	 int index=1;
         RestfulAPITestSuiteSolution bestSolution = suites.get(0);
    	 for(RestfulAPITestSuiteSolution suite:suites) {
    		 JMetalLogger.logger.info("TestSuite "+index);
    		 for(int i=0;i<suite.getNumberOfObjectives();i++) {
    			 JMetalLogger.logger.info("    Objective "+suite.getProblem().getObjectiveFunctions().get(i).getClass().getSimpleName()+": " + suite.getObjective(i)) ;
    		 }
    		 int i=1;
    		 for(TestCase testCase:suite.getVariables()) {
    			 JMetalLogger.logger.info("    Solution "+i+": " + CURLCommandGenerator.generate("",testCase)) ;
    			 i++;
    		 }
    		 index++;
    		 // Update best solution according to best value of preferred objective function (first one in the array):
             if (suite.getObjective(0) < bestSolution.getObjective(0))
                 bestSolution = suite;
    	 }

    	 // Execute best test suite with RESTestRunner
        restestRunner.run(bestSolution.getVariables());
    }

	public void runExperiment(int independentRuns, int numberOfCores) throws IOException {

        Experiment<RestfulAPITestSuiteSolution, 
        			List<RestfulAPITestSuiteSolution>> experiment = experimentBuilder
        															.setIndependentRuns(independentRuns)
        															.setNumberOfCores(numberOfCores)
        															.build();
        JMetalLogger.logger.info("Generating testSuites for: " + problems.size() + " API(s) using as objectives :"+ problem.getObjectiveFunctions() );
    	JMetalLogger.logger.info("Up to "+algorithms.size()+" algorithm run(s) will be executed using " + numberOfCores + " processor core(s)");
        ExecuteAlgorithms executeAlgorithms = new ExecuteAlgorithms<>(experiment);
        long start=System.currentTimeMillis();
        executeAlgorithms.run();
        long end=System.currentTimeMillis();
        JMetalLogger.logger.info("Algorithm(s) execution finished in "+(end-start)+" ms.");
        
        JMetalLogger.logger.info("Generating reference pareto fronts for the problems...");
        GenerateReferenceParetoFront paretoFrontGenerator=new GenerateReferenceParetoFront(experiment);
        paretoFrontGenerator.run();
        JMetalLogger.logger.info("Done!");
        
        JMetalLogger.logger.info("Computing Multi-objective quality indicators...");
        ComputeQualityIndicators computeIndicators = new ComputeQualityIndicators<>(experiment);
        computeIndicators.run();
        JMetalLogger.logger.info("Done!");
        
        JMetalLogger.logger.info("Generating latex tables with statistics...");
        GenerateLatexTablesWithStatistics latexTablesGenerator = new GenerateLatexTablesWithStatistics(experiment);
        latexTablesGenerator.run();
        JMetalLogger.logger.info("Done!");

        JMetalLogger.logger.info("Generating additional R scripts...");
        GenerateWilcoxonTestTablesWithR rWicoxonTestTablesGeneartor = new GenerateWilcoxonTestTablesWithR<>(experiment);
        rWicoxonTestTablesGeneartor.run();
        
        JMetalLogger.logger.info("Wilcoxon test script done!");
        GenerateFriedmanTestTables rFriedmanTestTablesGenerator = new GenerateFriedmanTestTables<>(experiment);
        rFriedmanTestTablesGenerator.run();
        
        JMetalLogger.logger.info("Friedman test script done!");
        GenerateBoxplotsWithR rBoxplotGenerator;
        
        JMetalLogger.logger.info("Boxplot generator script done!");
        rBoxplotGenerator = new GenerateBoxplotsWithR<>(experiment);
        rBoxplotGenerator.setRows(2);
        rBoxplotGenerator.setColumns(2);
        rBoxplotGenerator.run();
        JMetalLogger.logger.info("Experiment execution and post-processing finished!");
    }



    private List<GenericIndicator<RestfulAPITestSuiteSolution>> indicators() {
        List<GenericIndicator<RestfulAPITestSuiteSolution>> result = new ArrayList<>();
        result.add(new Epsilon<>());
        result.add(new Spread<>());
        result.add(new GenerationalDistance<>());
        result.add(new PISAHypervolume<>());
        result.add(new InvertedGenerationalDistance<>());
        result.add(new InvertedGenerationalDistancePlus<>());
        return result;
    }

    public ExperimentBuilder<RestfulAPITestSuiteSolution, List<RestfulAPITestSuiteSolution>> getExperimentBuilder() {
        return experimentBuilder;
    }

    public int getPopulationSize(){
    	return nsga2PopulationSize; 
    }
    
    private void setPopulationSize(int popSize) {
    	if(popSize>0)
    		this.nsga2PopulationSize=popSize;
    	else
    		throw new IllegalArgumentException("Population size should be positive! (argument value was:"+popSize+")");
    }
    
    public long getSeed() {
    	return seed;
    }
    
    public void setSeed(long seed) {
    	this.seed = seed;
    }       
    
    public void setAlgorithms(
			List<ExperimentAlgorithm<RestfulAPITestSuiteSolution, List<RestfulAPITestSuiteSolution>>> algorithms) {
		this.algorithms = algorithms;
		this.experimentBuilder.setAlgorithmList(algorithms);
	}
    
    
    public List<ExperimentProblem<RestfulAPITestSuiteSolution>> getProblems() {
		return problems;
	}
    
}
