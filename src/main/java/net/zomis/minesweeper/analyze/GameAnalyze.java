package net.zomis.minesweeper.analyze;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GameAnalyze<T> {

	@Deprecated
	private final SolvedCallback<T> callback;
	private final GroupValues<T> knownValues;
	private final List<RuleConstraint<T>> rules;
	
	GameAnalyze(GroupValues<T> knownValues, List<RuleConstraint<T>> unsolvedRules, SolvedCallback<T> callback) {
		this.knownValues = knownValues == null ? new GroupValues<T>() : new GroupValues<T>(knownValues);
		this.rules = unsolvedRules;
		this.callback = callback;
	}
	
	private void removeEmptyRules() {
		Iterator<RuleConstraint<T>> it = rules.iterator();
		while (it.hasNext()) {
			if (it.next().isEmpty())
				it.remove();
		}
	}
	
	private boolean simplifyRules() {
		boolean simplifyPerformed = true;
		while (simplifyPerformed) {
			simplifyPerformed = false;
			Iterator<RuleConstraint<T>> it = rules.iterator();
			while (it.hasNext()) {
				RuleConstraint<T> ruleSimplify = it.next();
				SimplifyResult simplifyResult = ruleSimplify.simplify(knownValues);
				if (simplifyResult == SimplifyResult.SIMPLIFIED) {
					simplifyPerformed = true;
					if (ruleSimplify.isEmpty()) {
						it.remove();
					}
				}
				else if (simplifyResult.isFailure()) {
					return false;
				}
			}
		}
		return true;
	}
	
	double solve(List<Solution<T>> solutions) {
		if (!this.simplifyRules()) {
			return 0;
		}
		
		this.removeEmptyRules();
		double total = this.solveRules(solutions);
		
		if (this.rules.isEmpty()) {
			Solution<T> solved = Solution.createSolution(this.knownValues);
			solutions.add(solved);
			total += solved.nCr();
		}
		return total;
	}
	
	private double solveRules(List<Solution<T>> solutions) {
		if (Thread.interrupted()) {
    		throw new RuntimeTimeoutException();
		}
		
		if (this.rules.isEmpty())
			return 0;
		
		FieldGroup<T> chosenGroup = getSmallestFieldGroup();
		if (chosenGroup == null) {
			throw new IllegalStateException("Chosen group is null: " + this.rules);
		}
		int groupSize = chosenGroup.size();
		
		if (groupSize == 0) {
			throw new IllegalStateException("Chosen group is empty. " + chosenGroup);
		}
		double total = 0;
		for (int i = 0; i <= groupSize; i++) {
			GroupValues<T> mapCopy = new GroupValues<T>(this.knownValues);
			mapCopy.put(chosenGroup, i);
			
			List<RuleConstraint<T>> rulesCopy = new ArrayList<RuleConstraint<T>>(); // deep copy!
			for (RuleConstraint<T> rule : this.rules) {
				rulesCopy.add(rule.copy());
			}

			total += new GameAnalyze<T>(mapCopy, rulesCopy, this.callback).solve(solutions);
		}
		return total;
	}

	private FieldGroup<T> getSmallestFieldGroup() {
		for (RuleConstraint<T> rule : rules) {
			FieldGroup<T> smallest = rule.getSmallestFieldGroup();
			if (smallest != null) {
				return smallest;
			}
		}
		return null;
	}
	
}
