package net.zomis.minesweeper.analyze;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

/**
 * Class for creating {@link AnalyzeResult}s
 * 
 * @author Simon Forsberg
 *
 * @param <T> The type of field to do analyze on
 */
public class AnalyzeFactory<T> {
	private final List<RuleConstraint<T>> rules = new ArrayList<RuleConstraint<T>>();
	
	AnalyzeFactory(Solution<T> known, List<RuleConstraint<T>> rules) {
		for (Entry<FieldGroup<T>, Integer> sol : known.getSetGroupValues().entrySet()) {
			this.rules.add(new FieldRule<T>(null, sol.getKey(), sol.getValue()));
		}
		this.rules.addAll(rules);
	}
	
	/**
	 * Create a new, empty analyze factory
	 */
	public AnalyzeFactory() {
	}
	
	/**
	 * Solve this analyze
	 * 
	 * @return An {@link AnalyzeResult} object for the result of the analyze.
	 */
	public AnalyzeResult<T> solve() {
		List<RuleConstraint<T>> original = new ArrayList<RuleConstraint<T>>(this.rules.size());
		for (RuleConstraint<T> rule : this.rules) {
			original.add(rule.copy());
		}
		
		List<RuleConstraint<T>> inProgress = new ArrayList<RuleConstraint<T>>(this.rules.size());
		for (RuleConstraint<T> rule : this.rules) {
			inProgress.add(rule.copy());
		}
		
		final List<Solution<T>> solutions = new ArrayList<Solution<T>>();
		
		this.splitFieldRules(inProgress);
		
		double total = new GameAnalyze<T>(null, inProgress, null).solve(solutions);
		
		for (Solution<T> solution : solutions) {
			solution.setTotal(total);
		}
		
		List<FieldGroup<T>> groups = new ArrayList<FieldGroup<T>>();
		if (!solutions.isEmpty()) {
			for (FieldGroup<T> group : solutions.get(0).getSetGroupValues().keySet()) {
				// All solutions should contain the same fieldgroups.
				groups.add(group);
			}
		}
		AnalyzeResultsImpl<T> result = new AnalyzeResultsImpl<T>(original, inProgress, groups, solutions, total);
		return result;
	}
	
	/**
	 * Separate fields into field groups. Example <code>a + b + c = 2</code> and <code>b + c + d = 1</code> becomes <code>(a) + (b + c) = 2</code> and <code>(b + c) + (d) = 1</code>. This method is called automatically when calling {@link #solve()}
	 * @param rules List of rules to split
	 */
	public void splitFieldRules(List<RuleConstraint<T>> rules) {
		if (rules.size() <= 1)
			return;
			
		boolean splitPerformed = true;
		while (splitPerformed) {
			splitPerformed = false;
			for (RuleConstraint<T> a : rules) {
				for (RuleConstraint<T> b : rules) {
					boolean result = a.checkIntersection(b);
					
					if (result) {
						splitPerformed = true;
					}
				}
			}
		}
	}
	
	/**
	 * Split the current field rules that has been added to this object
	 */
	public void splitFieldRules() {
		this.splitFieldRules(rules);
	}
	
	/**
	 * Add a new rule constraint that needs to be respected in all solutions
	 * 
	 * @param rule {@link FieldRule} to add
	 */
	public void addRule(FieldRule<T> rule) {
		this.rules.add(rule);
	}
	
	/**
	 * Get the rules that has been added to this analyze
	 * 
	 * @return List of {@link FieldRule}s that has been added
	 */
	public List<RuleConstraint<T>> getRules() {
		return new ArrayList<RuleConstraint<T>>(this.rules);
	}
}
