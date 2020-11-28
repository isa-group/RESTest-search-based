package es.us.isa.restest.searchbased.operators;

import es.us.isa.restest.searchbased.RestfulAPITestSuiteSolution;
import org.uma.jmetal.util.pseudorandom.PseudoRandomGenerator;

import java.util.Map;

public class OneOfMutationOperators extends AbstractMutationOperator {

	private static final long serialVersionUID = 926411558807012601L;
	
	Map<? extends AbstractMutationOperator,Double> operators;
	Double total;
	
	public OneOfMutationOperators(double mutationProbability, PseudoRandomGenerator randomGenerator,Map<? extends AbstractMutationOperator,Double> operators) {
		super(mutationProbability, randomGenerator);
		this.operators=operators;
		this.total=0.0;
		for(Double value:operators.values())
			this.total+=value;
	}

	@Override
	protected void doMutation(double mutationProbability, RestfulAPITestSuiteSolution solution) {
		double value=this.getRandomGenerator().nextDouble(0.0, total);
		double threshold=0;
		for(Map.Entry<? extends AbstractMutationOperator,Double> entry:operators.entrySet()) {
			threshold+=entry.getValue();
			if(threshold>=value) {
				entry.getKey().execute(solution);
			}
		}
	}

}
