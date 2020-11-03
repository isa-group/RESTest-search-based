/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.us.isa.restest.searchbased.operators;

import com.google.common.collect.Lists;
import es.us.isa.restest.searchbased.RestfulAPITestSuiteSolution;
import es.us.isa.restest.specification.ParameterFeatures;
import es.us.isa.restest.testcases.TestCase;
import es.us.isa.restest.util.RESTestException;
import org.javatuples.Pair;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.util.pseudorandom.PseudoRandomGenerator;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 * @author japar
 */
public abstract class AbstractMutationOperator implements MutationOperator<RestfulAPITestSuiteSolution> {

    private double mutationProbability;
    private PseudoRandomGenerator randomGenerator;

    public AbstractMutationOperator(double mutationProbability, PseudoRandomGenerator randomGenerator) {
        this.mutationProbability = mutationProbability;
        this.randomGenerator = randomGenerator;
    }

    /* Getter */
    public double getMutationProbability() {
        return mutationProbability;
    }

    /* Setters */
    public void setMutationProbability(double mutationProbability) {
        this.mutationProbability = mutationProbability;
    }

    public PseudoRandomGenerator getRandomGenerator() {
        return randomGenerator;
    }

    @Override
    public RestfulAPITestSuiteSolution execute(RestfulAPITestSuiteSolution solution) {
        assert (solution != null);
        try {
            doMutation(getMutationProbability(), solution);
        } catch (RESTestException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return solution;
    }

    protected Collection<ParameterFeatures> getAllPresentParameters(TestCase testCase) {
    	return getAllPresentParameters(testCase, false);
    }
    
    protected Collection<ParameterFeatures> getAllPresentParameters(TestCase testCase,boolean includePathParameters) {
        Set<ParameterFeatures> parameters = new HashSet<>();
        if(includePathParameters) {
        	for (String pathParam : testCase.getPathParameters().keySet())
        		parameters.add(new ParameterFeatures(pathParam, "path", null));
        }
        for (String queryParam : testCase.getQueryParameters().keySet())
            parameters.add(new ParameterFeatures(queryParam, "query", null));
        for (String headerParam : testCase.getHeaderParameters().keySet())
            parameters.add(new ParameterFeatures(headerParam, "header", null));
        for (String formParam : testCase.getFormParameters().keySet())
            parameters.add(new ParameterFeatures(formParam, "formData", null));
        // TODO: Support body parameter mutation:
        /*if(testCase.getBodyParameter()!=null) {            
            parameterNames.add("body");
        }*/
        return parameters;
    }

    protected Collection<ParameterFeatures> getNonAuthParameters(Collection<ParameterFeatures> paramFeatures, TestCase testCase, RestfulAPITestSuiteSolution solution) {
        // Discard auth params (workaround: those that don't have associated test data generators)
        return paramFeatures.stream()
                .filter(param -> solution.getProblem().getTestCaseGenerators().get(testCase.getOperationId())
                        .getGenerators().containsKey(Pair.with(param.getName(), param.getIn())))
                .collect(Collectors.toList());
    }

	protected abstract void doMutation(double mutationProbability, RestfulAPITestSuiteSolution solution) throws RESTestException;
}
