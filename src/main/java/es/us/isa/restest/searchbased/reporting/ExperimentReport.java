package es.us.isa.restest.searchbased.reporting;

import org.javatuples.Septet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ExperimentReport {
    private String id;

    // Helper variables (not exported):
    private int currentIteration;
    private String stoppingCriterion;
    private Double currentStoppingCriterionState;
    private Double stoppingCriterionMax;
    private int currentSolutionIndex;

    // Experiment configuration:
    private int minTestSuiteSize;
    private int maxTestSuiteSize;
    private int populationSize;
    private double[] mutationProbabilities;
    private double crossoverProbability;
    private String[] objectiveFunctions;
    private String terminationCriterion;

    // Regarding the whole execution:
    private long time;
    private long executedRequests;
    // Fitness values:
    // iteration, stoppingCriterion, stoppingCriterionState, stoppingCriterionMax, solutionIndex, objectiveFunction, value
    private List<Septet<Integer, String, Double, Double, Integer, String, Double>> fitnessValues;

    // Regarding the final test suite generated:
    private double apiCoverage;
    private int testSuiteSize;
    private int nominalTestCases;
    private int faultyTestCases;
    private int faultyTestCasesDueToParameters;
    private int faultyTestCasesDueToDependencies;
    private int successfulTestResults;
    private int failedTestResults;
    private int failedTestResults5XX;
    private int failedTestResultsNominal4XX;
    private int failedTestResultsFaultyParameters2XX;
    private int failedTestResultsFaultyDependencies2XX;
    private int failedTestResultsSwagger;
    private int differentFailures; // Based on response body

    // *** Helper methods to save fitness values ***

    public void setCurrentIteration(int currentIteration) {
        this.currentIteration = currentIteration;
    }

    public void incrementCurrentIteration() {
        this.currentIteration++;
    }

    public void setStoppingCriterion(String stoppingCriterion) {
        this.stoppingCriterion = stoppingCriterion;
    }

    public void setCurrentStoppingCriterionState(Double currentStoppingCriterionState) {
        this.currentStoppingCriterionState = currentStoppingCriterionState;
    }

    public void setStoppingCriterionMax(Double stoppingCriterionMax) {
        this.stoppingCriterionMax = stoppingCriterionMax;
    }

    public void setCurrentSolutionIndex(int currentSolutionIndex) {
        this.currentSolutionIndex = currentSolutionIndex;
    }

    public void incrementCurrentSolutionIndex() {
        this.currentSolutionIndex++;
    }

    public void addFitnessValue(String objectiveFunction, double value) {
        fitnessValues.add(Septet.with(
                currentIteration,
                stoppingCriterion,
                currentStoppingCriterionState,
                stoppingCriterionMax,
                currentSolutionIndex,
                objectiveFunction,
                value
        ));
    }

    // *********************************************

    public ExperimentReport(String id) {
        this.id = id;
        this.currentIteration = 1;
        this.fitnessValues = new ArrayList<>();
    }

    public ExperimentReport withMinTestSuiteSize(int minTestSuiteSize) {
        this.minTestSuiteSize = minTestSuiteSize;
        return this;
    }

    public ExperimentReport withMaxTestSuiteSize(int maxTestSuiteSize) {
        this.maxTestSuiteSize = maxTestSuiteSize;
        return this;
    }

    public ExperimentReport withPopulationSize(int populationSize) {
        this.populationSize = populationSize;
        return this;
    }

    public ExperimentReport withMutationProbabilities(double[] mutationProbabilities) {
        this.mutationProbabilities = mutationProbabilities;
        return this;
    }

    public ExperimentReport withCrossoverProbabilities(double crossoverProbability) {
        this.crossoverProbability = crossoverProbability;
        return this;
    }

    public ExperimentReport withObjectiveFunctions(String[] objectiveFunctions) {
        this.objectiveFunctions = objectiveFunctions;
        return this;
    }

    public ExperimentReport withTerminationCriterion(String terminationCriterion) {
        this.terminationCriterion = terminationCriterion;
        return this;
    }

    public ExperimentReport withTime(long time) {
        this.time = time;
        return this;
    }

    public ExperimentReport withExecutedRequests(long executedRequests) {
        this.executedRequests = executedRequests;
        return this;
    }

    public ExperimentReport withApiCoverage(double apiCoverage) {
        this.apiCoverage = apiCoverage;
        return this;
    }

    public ExperimentReport withTestSuiteSize(int testSuiteSize) {
        this.testSuiteSize = testSuiteSize;
        return this;
    }

    public ExperimentReport withNominalTestCases(int nominalTestCases) {
        this.nominalTestCases = nominalTestCases;
        return this;
    }

    public ExperimentReport withFaultyTestCases(int faultyTestCases) {
        this.faultyTestCases = faultyTestCases;
        return this;
    }

    public ExperimentReport withFaultyTestCasesDueToParameters(int faultyTestCasesDueToParameters) {
        this.faultyTestCasesDueToParameters = faultyTestCasesDueToParameters;
        return this;
    }

    public ExperimentReport withFaultyTestCasesDueToDependencies(int faultyTestCasesDueToDependencies) {
        this.faultyTestCasesDueToDependencies = faultyTestCasesDueToDependencies;
        return this;
    }

    public ExperimentReport withSuccessfulTestResults(int successfulTestResults) {
        this.successfulTestResults = successfulTestResults;
        return this;
    }

    public ExperimentReport withFailedTestResults(int failedTestResults) {
        this.failedTestResults = failedTestResults;
        return this;
    }

    public ExperimentReport withFailedTestResults5XX(int failedTestResults5XX) {
        this.failedTestResults5XX = failedTestResults5XX;
        return this;
    }

    public ExperimentReport withFailedTestResultsNominal4XX(int failedTestResultsNominal4XX) {
        this.failedTestResultsNominal4XX = failedTestResultsNominal4XX;
        return this;
    }

    public ExperimentReport withFailedTestResultsFaultyParameters2XX(int failedTestResultsFaultyParameters2XX) {
        this.failedTestResultsFaultyParameters2XX = failedTestResultsFaultyParameters2XX;
        return this;
    }

    public ExperimentReport withFailedTestResultsFaultyDependencies2XX(int failedTestResultsFaultyDependencies2XX) {
        this.failedTestResultsFaultyDependencies2XX = failedTestResultsFaultyDependencies2XX;
        return this;
    }

    public ExperimentReport withFailedTestResultsSwagger(int failedTestResultsSwagger) {
        this.failedTestResultsSwagger = failedTestResultsSwagger;
        return this;
    }

    public ExperimentReport withDifferentFailures(int differentFailures) {
        this.differentFailures = differentFailures;
        return this;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getMinTestSuiteSize() {
        return minTestSuiteSize;
    }

    public void setMinTestSuiteSize(int minTestSuiteSize) {
        this.minTestSuiteSize = minTestSuiteSize;
    }

    public int getMaxTestSuiteSize() {
        return maxTestSuiteSize;
    }

    public void setMaxTestSuiteSize(int maxTestSuiteSize) {
        this.maxTestSuiteSize = maxTestSuiteSize;
    }

    public int getPopulationSize() {
        return populationSize;
    }

    public void setPopulationSize(int populationSize) {
        this.populationSize = populationSize;
    }

    public double[] getMutationProbabilities() {
        return mutationProbabilities;
    }

    public void setMutationProbabilities(double[] mutationProbabilities) {
        this.mutationProbabilities = mutationProbabilities;
    }

    public double getCrossoverProbability() {
        return crossoverProbability;
    }

    public void setCrossoverProbability(double crossoverProbability) {
        this.crossoverProbability = crossoverProbability;
    }

    public String[] getObjectiveFunctions() {
        return objectiveFunctions;
    }

    public void setObjectiveFunctions(String[] objectiveFunctions) {
        this.objectiveFunctions = objectiveFunctions;
    }

    public String getTerminationCriterion() {
        return terminationCriterion;
    }

    public void setTerminationCriterion(String terminationCriterion) {
        this.terminationCriterion = terminationCriterion;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getExecutedRequests() {
        return executedRequests;
    }

    public void setExecutedRequests(long executedRequests) {
        this.executedRequests = executedRequests;
    }

    public double getApiCoverage() {
        return apiCoverage;
    }

    public void setApiCoverage(double apiCoverage) {
        this.apiCoverage = apiCoverage;
    }

    public int getTestSuiteSize() {
        return testSuiteSize;
    }

    public void setTestSuiteSize(int testSuiteSize) {
        this.testSuiteSize = testSuiteSize;
    }

    public int getNominalTestCases() {
        return nominalTestCases;
    }

    public void setNominalTestCases(int nominalTestCases) {
        this.nominalTestCases = nominalTestCases;
    }

    public int getFaultyTestCases() {
        return faultyTestCases;
    }

    public void setFaultyTestCases(int faultyTestCases) {
        this.faultyTestCases = faultyTestCases;
    }

    public int getFaultyTestCasesDueToParameters() {
        return faultyTestCasesDueToParameters;
    }

    public void setFaultyTestCasesDueToParameters(int faultyTestCasesDueToParameters) {
        this.faultyTestCasesDueToParameters = faultyTestCasesDueToParameters;
    }

    public int getFaultyTestCasesDueToDependencies() {
        return faultyTestCasesDueToDependencies;
    }

    public void setFaultyTestCasesDueToDependencies(int faultyTestCasesDueToDependencies) {
        this.faultyTestCasesDueToDependencies = faultyTestCasesDueToDependencies;
    }

    public int getSuccessfulTestResults() {
        return successfulTestResults;
    }

    public void setSuccessfulTestResults(int successfulTestResults) {
        this.successfulTestResults = successfulTestResults;
    }

    public int getFailedTestResults() {
        return failedTestResults;
    }

    public void setFailedTestResults(int failedTestResults) {
        this.failedTestResults = failedTestResults;
    }

    public int getFailedTestResults5XX() {
        return failedTestResults5XX;
    }

    public void setFailedTestResults5XX(int failedTestResults5XX) {
        this.failedTestResults5XX = failedTestResults5XX;
    }

    public int getFailedTestResultsNominal4XX() {
        return failedTestResultsNominal4XX;
    }

    public void setFailedTestResultsNominal4XX(int failedTestResultsNominal4XX) {
        this.failedTestResultsNominal4XX = failedTestResultsNominal4XX;
    }

    public int getFailedTestResultsFaultyParameters2XX() {
        return failedTestResultsFaultyParameters2XX;
    }

    public void setFailedTestResultsFaultyParameters2XX(int failedTestResultsFaultyParameters2XX) {
        this.failedTestResultsFaultyParameters2XX = failedTestResultsFaultyParameters2XX;
    }

    public int getFailedTestResultsFaultyDependencies2XX() {
        return failedTestResultsFaultyDependencies2XX;
    }

    public void setFailedTestResultsFaultyDependencies2XX(int failedTestResultsFaultyDependencies2XX) {
        this.failedTestResultsFaultyDependencies2XX = failedTestResultsFaultyDependencies2XX;
    }

    public int getFailedTestResultsSwagger() {
        return failedTestResultsSwagger;
    }

    public void setFailedTestResultsSwagger(int failedTestResultsSwagger) {
        this.failedTestResultsSwagger = failedTestResultsSwagger;
    }

    public int getDifferentFailures() {
        return differentFailures;
    }

    public void setDifferentFailures(int differentFailures) {
        this.differentFailures = differentFailures;
    }

    public static String getGeneralStatsCsvHeader() {
        return "id,minTestSuiteSize,maxTestSuiteSize,populationSize,mutationProbabilities,crossoverProbabilities,objectiveFunctions,terminationCriterion,time,executedRequests,apiCoverage,testSuiteSize,nominalTestCases,faultyTestCases,faultyTestCasesDueToParameters,faultyTestCasesDueToDependencies,successfulTestResults,failedTestResults,failedTestResults5XX,failedTestResultsNominal4XX,failedTestResultsFaultyParameters2XX,failedTestResultsFaultyDependencies2XX,failedTestResultsSwagger,differentFailures";
    }

    public static String getObjFuncStatsCsvHeader() {
        return "iteration,stoppingCriterion,stoppingCriterionState,stoppingCriterionMax,solutionIndex,objectiveFunction,value";
    }

    public String getGeneralStatsCsvRow() {
        return id
                + "," + minTestSuiteSize
                + "," + maxTestSuiteSize
                + "," + populationSize
                + "," + Arrays.toString(mutationProbabilities).replace(',', ';')
                + "," + crossoverProbability
                + "," + Arrays.toString(objectiveFunctions).replace(',', ';')
                + "," + terminationCriterion
                + "," + time
                + "," + executedRequests
                + "," + apiCoverage
                + "," + testSuiteSize
                + "," + nominalTestCases
                + "," + faultyTestCases
                + "," + faultyTestCasesDueToParameters
                + "," + faultyTestCasesDueToDependencies
                + "," + successfulTestResults
                + "," + failedTestResults
                + "," + failedTestResults5XX
                + "," + failedTestResultsNominal4XX
                + "," + failedTestResultsFaultyParameters2XX
                + "," + failedTestResultsFaultyDependencies2XX
                + "," + failedTestResultsSwagger
                + "," + differentFailures;
    }

    public String getObjFuncStatsCsvRows() {
        return fitnessValues.stream().map(fitnessValueField ->
                fitnessValueField.toList().stream().map(e -> e.toString().replace(',', ';')).collect(Collectors.joining(","))
        ).collect(Collectors.joining("\n"));
    }
}
