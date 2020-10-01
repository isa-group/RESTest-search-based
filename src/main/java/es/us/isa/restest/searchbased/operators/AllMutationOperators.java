package es.us.isa.restest.searchbased.operators;

import es.us.isa.restest.searchbased.RestfulAPITestSuiteSolution;

import java.util.List;

public class AllMutationOperators extends AbstractMutationOperator {

	List<? extends AbstractMutationOperator> operators;
	
	public AllMutationOperators(List<? extends AbstractMutationOperator> operators) {
		super(1, null);
		this.operators=operators;
	}

	@Override
	protected void doMutation(double mutationProbability, RestfulAPITestSuiteSolution solution) {
		for(AbstractMutationOperator operator:operators) {
			operator.execute(solution);
		}		
	}

}
