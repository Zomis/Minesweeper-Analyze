package net.zomis.minesweeper.analyze;

import java.util.List;
import java.util.Random;

import net.zomis.minesweeper.analyze.detail.DetailedResults;
import net.zomis.minesweeper.analyze.detail.NeighborFind;

public interface AnalyzeResult<T> {
	AnalyzeResult<T> cloneAddSolve(List<FieldRule<T>> extraRules);
	
	List<T> getFields();
	
	FieldGroup<T> getGroupFor(T field);
	
	List<FieldGroup<T>> getGroups();
	
	List<FieldRule<T>> getOriginalRules();
	
	double getProbabilityOf(List<FieldRule<T>> extraRules);
	
	List<FieldRule<T>> getRules();
	
	List<T> getSolution(double solution);

	Iterable<Solution<T>> getSolutionIteration();

	List<Solution<T>> getSolutions();

	/**
	 * Get the total number of combinations of Field placements.
	 * @return The number of combinations for the analyze. 0 if the analyze is impossible.
	 */
	double getTotal();

	List<T> randomSolution(Random random);

	DetailedResults<T> analyzeDetailed(NeighborFind<T> neighborStrategy);

}
