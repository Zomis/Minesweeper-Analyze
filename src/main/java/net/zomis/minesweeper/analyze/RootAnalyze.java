package net.zomis.minesweeper.analyze;

import java.util.Collection;
import java.util.List;
import java.util.Random;

public interface RootAnalyze<Field> {
	/**
	 * Get the total number of combinations of Field placements.
	 * @return The number of combinations for the analyze. 0 if the analyze is impossible.
	 */
	double getTotal();

	List<Field> getFields();

	List<FieldRule<Field>> getRules();

	FieldGroup<Field> getGroupFor(Field field);

	List<Field> randomSolution(Random random);
	
	List<Field> getSolution(double solution);
	Iterable<Solution<Field>> getSolutionIteration();
	
	List<FieldRule<Field>> getOriginalRules();

	double getProbabilityOf(List<FieldRule<Field>> extraRules);

	Collection<Solution<Field>> getSolutions();

	List<FieldGroup<Field>> getGroups();

	RootAnalyze<Field> cloneAddSolve(List<FieldRule<Field>> extraRules);

}
