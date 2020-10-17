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
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.util.pseudorandom.PseudoRandomGenerator;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author japar
 */
public abstract class AbstractMutationOperator implements MutationOperator<RestfulAPITestSuiteSolution> {

    private double mutationProbability;
    private PseudoRandomGenerator randomGenerator;
    protected boolean mutationApplied;
    protected double maxMutationsRatio; // Max percentage of elements to mutate in the test suite
    public List<String> securityParamNames=Lists.newArrayList("Authorization","Application-Authorization","auth-token","access_token"); 

    public AbstractMutationOperator(double mutationProbability, PseudoRandomGenerator randomGenerator) {
        this.mutationProbability = mutationProbability;
        this.randomGenerator = randomGenerator;
        this.maxMutationsRatio = 0.1;
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
    	return getAllPresentParameters(testCase, false,false);
    }
    
    protected Collection<ParameterFeatures> getAllPresentParameters(TestCase testCase,boolean includePathParameters, boolean includeSecurityParameters) {
        Set<ParameterFeatures> parameters = new HashSet<>();
        if(includePathParameters) {
        	for (String pathParam : testCase.getPathParameters().keySet())
        		parameters.add(new ParameterFeatures(pathParam, "path", null));
        }
        for (String queryParam : testCase.getQueryParameters().keySet()) {
        	if(includeSecurityParameters || !isSecurityParameter(queryParam) )
        		parameters.add(new ParameterFeatures(queryParam, "query", null));
        }
        for (String headerParam : testCase.getHeaderParameters().keySet())
        	if(includeSecurityParameters || !isSecurityParameter(headerParam) ) 
        		parameters.add(new ParameterFeatures(headerParam, "header", null));
        for (String formParam : testCase.getFormParameters().keySet())
        	if(includeSecurityParameters || !isSecurityParameter(formParam) )
        		parameters.add(new ParameterFeatures(formParam, "formData", null));
        // TODO: Support body parameter mutation:
        /*if(testCase.getBodyParameter()!=null) {            
            parameterNames.add("body");
        }*/
        return parameters;
    }

    private boolean isSecurityParameter(String paramName) {
		return securityParamNames.contains(paramName);
	}

	protected abstract void doMutation(double mutationProbability, RestfulAPITestSuiteSolution solution) throws RESTestException;
}
