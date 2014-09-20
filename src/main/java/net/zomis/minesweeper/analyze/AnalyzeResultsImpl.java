package net.zomis.minesweeper.analyze;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import net.zomis.minesweeper.analyze.detail.DetailAnalyze;
import net.zomis.minesweeper.analyze.detail.DetailedResults;
import net.zomis.minesweeper.analyze.detail.NeighborFind;

public class AnalyzeResultsImpl<T> implements AnalyzeResult<T> {
	private final List<FieldGroup<T>> groups;
	private final List<FieldRule<T>> rules;
	private final List<FieldRule<T>> originalRules;
	private final List<Solution<T>> solutions;
	
	private double total;
	
	@Override
	public double getTotal() {
		return this.total;
	}
	
	public AnalyzeResultsImpl(List<FieldRule<T>> original,
			List<FieldRule<T>> rules, List<FieldGroup<T>> groups,
			List<Solution<T>> solutions, double total) {
		this.originalRules = original;
		this.rules = rules;
		this.groups = groups;
		this.solutions = solutions;
		this.total = total;
		
	}

	/**
	 * Get the list of simplified rules used to perform the analyze
	 * 
	 * @return List of simplified rules
	 */
	@Override
	public List<FieldRule<T>> getRules() {
		return new ArrayList<FieldRule<T>>(this.rules);
	}
	
	@Override
	public FieldGroup<T> getGroupFor(T field) {
		for (FieldGroup<T> group : this.groups) {
			if (group.contains(field)) {
				return group;
			}
		}
		return null;
	}
	
	/**
	 * Return a random solution that satisfies all the rules
	 * 
	 * @param random Random object to perform the randomization
	 * @return A list of fields randomly selected that is guaranteed to be a solution to the constraints
	 * 
	 */
	@Override
	public List<T> randomSolution(Random random) {
		if (random == null) {
			throw new IllegalArgumentException("Random object cannot be null");
		}
		
		List<Solution<T>> solutions = new LinkedList<Solution<T>>(this.solutions);
		if (this.getTotal() == 0) {
			throw new IllegalStateException("Analyze has 0 combinations: " + this);
		}
		
		double rand = random.nextDouble() * this.getTotal();
		Solution<T> theSolution = null;
		
		while (rand > 0) {
			if (solutions.isEmpty()) {
				throw new IllegalStateException("Solutions is suddenly empty. (This should not happen)");
			}
			theSolution = solutions.get(0);
			rand -= theSolution.nCr();
			solutions.remove(0);
		}
		
		return theSolution.getRandomSolution(random);
	}
	
	private AnalyzeFactory<T> solutionToNewAnalyze(Solution<T> solution, List<FieldRule<T>> extraRules) {
		List<FieldRule<T>> newRules = new ArrayList<FieldRule<T>>();
		for (FieldRule<T> rule : extraRules) { 
			// Create new rules, because the older ones may have been simplified already.
			newRules.add(new FieldRule<T>(rule));
		}
		AnalyzeFactory<T> newRoot = new AnalyzeFactory<T>(solution, newRules);
		return newRoot;
	}
	
	@Override
	public AnalyzeResult<T> cloneAddSolve(List<FieldRule<T>> extraRules) {
		List<FieldRule<T>> newRules = this.getOriginalRules();
		newRules.addAll(extraRules);
		AnalyzeFactory<T> copy = new AnalyzeFactory<T>();
		for (FieldRule<T> rule : newRules) {
			copy.addRule(new FieldRule<T>(rule));
		}
		return copy.solve();
	}
	
	/**
	 * Get the list of the original, non-simplified, rules
	 * 
	 * @return The original rule list  
	 */
	@Override
	public List<FieldRule<T>> getOriginalRules() {
		return this.originalRules.isEmpty() ? this.getRules() : new ArrayList<FieldRule<T>>(this.originalRules);
	}

	private double getTotalWith(List<FieldRule<T>> extraRules) {
		double total = 0;
		
		for (Solution<T> solution : this.getSolutions()) {
			AnalyzeResult<T> root = this.solutionToNewAnalyze(solution, extraRules).solve();
			total += root.getTotal();
		}
		
		return total;
	}
	
	@Override
	public double getProbabilityOf(List<FieldRule<T>> extraRules) {
		return this.getTotalWith(extraRules) / this.getTotal();
	}
	
	@Override
	public List<Solution<T>> getSolutions() {
		return new ArrayList<Solution<T>>(this.solutions);
	}

	
	
	@Override
	public List<FieldGroup<T>> getGroups() {
		
		List<FieldGroup<T>> grps = new ArrayList<FieldGroup<T>>(this.groups);
		Iterator<FieldGroup<T>> it = grps.iterator();
		while (it.hasNext()) {
			// remove empty fieldgroups
			if (it.next().isEmpty()) {
				it.remove(); 
			}
		}
		return grps;
	}
	@Override
	public List<T> getFields() {
		List<T> allFields = new ArrayList<T>();
		for (FieldGroup<T> group : this.getGroups()) {
			allFields.addAll(group);
		}
		return allFields;
	}

	@Override
	public List<T> getSolution(double solution) {
		if (Math.rint(solution) != solution || solution < 0 || solution >= this.getTotal()) {
			throw new IllegalArgumentException("solution must be an integer between 0 and total (" + this.getTotal() + ")");
		}
		if (solutions.isEmpty()) {
			throw new IllegalStateException("There are no solutions.");
		}
		
		List<Solution<T>> solutions = new ArrayList<Solution<T>>(this.solutions);
		Solution<T> theSolution = solutions.get(0);
		while (solution > theSolution.nCr()) {
			solution -= theSolution.nCr();
			solutions.remove(0);
			theSolution = solutions.get(0);
		}
		return theSolution.getCombination(solution);
	}
	
	@Override
	public Iterable<Solution<T>> getSolutionIteration() {
		return this.solutions;
	}

	@Override
	public DetailedResults<T> analyzeDetailed(NeighborFind<T> neighborStrategy) {
		return DetailAnalyze.solveDetailed(this, neighborStrategy);
	}
	
}
