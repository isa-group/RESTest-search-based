package es.us.isa.restest.searchbased.terminationcriteria;

import es.us.isa.restest.searchbased.algorithms.SearchBasedAlgorithm;
import es.us.isa.restest.searchbased.reporting.ExperimentReport;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static es.us.isa.restest.util.PropertyManager.readProperty;

public abstract class AbstractTerminationCriterion implements TerminationCriterion {

    private ExperimentReport experimentReport;
    private final Logger logger;

    protected AbstractTerminationCriterion() {
        logger = LogManager.getLogger(this.getClass().getName());
    }

    public Double getStoppingCriterionState(SearchBasedAlgorithm t) {
        throw new UnsupportedOperationException("Operation not supported in TerminationCriterion " + getClass().getSimpleName());
    }

    public Double getStoppingCriterionMax() {
        throw new UnsupportedOperationException("Operation not supported in TerminationCriterion " + getClass().getSimpleName());
    }

    @Override
    public boolean test(SearchBasedAlgorithm t) {
        double stoppingCriterionState = getStoppingCriterionState(t);
        double stoppingCriterionMax = getStoppingCriterionMax();

        updateReportIndexes(stoppingCriterionState);
        logEvaluation(stoppingCriterionState, stoppingCriterionMax);
        return stoppingCriterionState >= stoppingCriterionMax;
    }

    public void logEvaluation(Double stoppingCriterionState, Double stoppingCriterionMax) {
        logger.info("Stopping criterion state: {} / {}", stoppingCriterionState, stoppingCriterionMax);
    }

    public void setExperimentReport(ExperimentReport experimentReport) {
        this.experimentReport = experimentReport;
    }

    public void updateReportIndexes(Double stoppingCriterionState) {
        if (Boolean.parseBoolean(readProperty("search.stats.enabled")) && experimentReport != null) {
            experimentReport.incrementCurrentIteration();
            experimentReport.setCurrentStoppingCriterionState(stoppingCriterionState);
            experimentReport.setCurrentSolutionIndex(0);
        }
    }

    /**
     * Some termination criteria may need to be reset before they are used again, e.g.,
     * the MaxExecutionTime must reset its 'start' property.
     */
    public void reset() {}
}
