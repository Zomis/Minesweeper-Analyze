package net.zomis.minesweeper.analyze;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GameAnalyze<T> {

	private final List<FieldRule<T>> rules;
	private final GroupValues<T> knownValues;
	private final SolvedCallback<T> callback;
	
	GameAnalyze(GroupValues<T> knownValues, List<FieldRule<T>> unsolvedRules, SolvedCallback<T> callback) {
		this.knownValues = knownValues == null ? new GroupValues<T>() : new GroupValues<T>(knownValues);
		this.rules = unsolvedRules;
		this.callback = callback;
	}
	
	void solve() {
		if (Thread.interrupted())
    		throw new RuntimeTimeoutException();
		
		if (!this.simplifyRules()) {
			return;
		}
		this.removeEmptyRules();
		this.solveRules();
		if (this.rules.isEmpty()) {
			this.addSolutions();
		}
	}
	
	private void removeEmptyRules() {
		Iterator<FieldRule<T>> it = rules.iterator();
		while (it.hasNext()) {
			if (it.next().isEmpty())
				it.remove();
		}
	}
	
	private void solveRules() {
		if (this.rules.isEmpty())
			return;
		
		FieldGroup<T> chosenGroup = this.rules.get(0).getSmallestFieldGroup();
		if (chosenGroup == null) {
			throw new IllegalStateException("Chosen group is null.");
		}
		if (chosenGroup.size() == 0) {
			throw new IllegalStateException("Chosen group is empty. " + chosenGroup);
		}
		
		for (int i = 0; i <= chosenGroup.size(); i++) {
			GroupValues<T> mapCopy = new GroupValues<T>(this.knownValues);
			mapCopy.put(chosenGroup, i);
			
			List<FieldRule<T>> rulesCopy = new ArrayList<FieldRule<T>>(); // deep copy!
			for (FieldRule<T> rule : this.rules) {
				rulesCopy.add(new FieldRule<T>(rule));
			}
			
			if (rulesCopy.remove(chosenGroup))
				System.out.println("remove has effect: "); // TODO: WTF!? This never has any effect.
			
			new GameAnalyze<T>(mapCopy, rulesCopy, this.callback).solve();
		}
	}
	
	private void addSolutions() {
		if (this.callback != null) 
			this.callback.solved(Solution.solutionFactory(this.knownValues));
	}

	private boolean simplifyRules() {
		boolean simplifyPerformed = true;
		while (simplifyPerformed) {
			simplifyPerformed = false;
			for (FieldRule<T> ruleSimplify : rules) {
				SimplifyResult simplifyResult = ruleSimplify.simplify(knownValues);
				if (simplifyResult == SimplifyResult.SIMPLIFIED) {
					simplifyPerformed = true;
				}
				else if (simplifyResult.isFailure()) {
					return false;
				}
			}
		}
		return true;
	}
	
}
