package net.zomis.minesweeper.analyze;

import java.util.List;
import java.util.Random;

public interface RootAnalyze<T> {
	/**
	 * Get the total number of combinations of Field placements.
	 * @return The number of combinations for the analyze. 0 if the analyze is impossible.
	 */
	double getTotal();

	List<T> getFields();

	List<FieldRule<T>> getRules();

	FieldGroup<T> getGroupFor(T field);

	List<T> randomSolution(Random random);
	
	List<T> getSolution(double solution);
	Iterable<Solution<T>> getSolutionIteration();
	
	List<FieldRule<T>> getOriginalRules();

	double getProbabilityOf(List<FieldRule<T>> extraRules);

	List<Solution<T>> getSolutions();

	List<FieldGroup<T>> getGroups();

	RootAnalyze<T> cloneAddSolve(List<FieldRule<T>> extraRules);

}
