/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.us.isa.restest.searchbased.operators;

import es.us.isa.restest.configuration.pojos.TestParameter;
import es.us.isa.restest.inputs.ITestDataGenerator;
import es.us.isa.restest.searchbased.RestfulAPITestSuiteSolution;
import es.us.isa.restest.specification.ParameterFeatures;
import es.us.isa.restest.testcases.TestCase;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.javatuples.Pair;
import org.uma.jmetal.util.pseudorandom.PseudoRandomGenerator;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static es.us.isa.restest.searchbased.operators.Utils.resetTestResult;
import static es.us.isa.restest.searchbased.operators.Utils.updateTestCaseFaultyReason;

/**
 *
 * @author japar
 */
public class AddParameterMutation extends AbstractMutationOperator {

    private static final Logger logger = LogManager.getLogger(AddParameterMutation.class.getName());

    public AddParameterMutation(double mutationProbability, PseudoRandomGenerator randomGenerator) {
        super(mutationProbability, randomGenerator);
    }
        
    @Override
    protected void doMutation(double mutationProbability, RestfulAPITestSuiteSolution solution) {
        for (TestCase testCase : solution.getVariables()) {
            mutationApplied = false;
            for (ParameterFeatures paramFeatures : getNonPresentParameters(testCase,solution)) {
                if (getRandomGenerator().nextDouble() <= mutationProbability) {                    
                    doMutation(paramFeatures, testCase, solution);
                    if (!mutationApplied) mutationApplied = true;
                }
            }
            if (mutationApplied) {
                logger.info("Mutation probability fulfilled! Parameter added to test case.");
                updateTestCaseFaultyReason(solution, testCase);
                resetTestResult(testCase.getId(), solution); // The test case changed, reset test result
            }
        }
    }

    protected Collection<ParameterFeatures> getNonPresentParameters(TestCase testCase, RestfulAPITestSuiteSolution solution) {
    	es.us.isa.restest.configuration.pojos.Operation operation = solution.getProblem().getOperationsUnderTest().get(testCase.getOperationId());

        Collection<ParameterFeatures> presentParams=getAllPresentParameters(testCase);
        Set<ParameterFeatures> result=new HashSet<>();
        for (TestParameter param: operation.getTestParameters()) {
            ParameterFeatures paramFeatures = new ParameterFeatures(param.getName(), param.getIn(), null);
            if (!presentParams.contains(paramFeatures))
                result.add(paramFeatures);
        }
        return result;
    }

    private void doMutation(ParameterFeatures paramFeatures, TestCase testCase, RestfulAPITestSuiteSolution solution) {
        ITestDataGenerator generator = solution.getProblem().getTestCaseGenerators().get(testCase.getOperationId()).getGenerators().get(Pair.with(paramFeatures.getName(), paramFeatures.getIn()));
        testCase.addParameter(paramFeatures, generator.nextValueAsString());
    }
    
}
