package es.us.isa.restest.searchbased.experiment;

import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;

public class DummyProblem implements Problem<Solution<?>> {

	String problemName;
	
	@Override
	public int getNumberOfVariables() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getNumberOfObjectives() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getNumberOfConstraints() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getName() {
		return problemName;
	}

	@Override
	public void evaluate(Solution<?> solution) {
		// TODO Auto-generated method stub

	}

	@Override
	public Solution<?> createSolution() {
		// TODO Auto-generated method stub
		return null;
	}

}
