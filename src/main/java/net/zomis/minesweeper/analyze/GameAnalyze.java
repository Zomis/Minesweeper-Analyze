package net.zomis.minesweeper.analyze;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GameAnalyze<Field> {

	private final List<FieldRule<Field>> rules;
	private final GroupValues<Field> knownValues;
	
	private final SolvedCallback<Field> callback;
	
	GameAnalyze(GroupValues<Field> knownValues, List<FieldRule<Field>> unsolvedRules, SolvedCallback<Field> callback) {
		this.knownValues = knownValues == null ? new GroupValues<Field>() : new GroupValues<Field>(knownValues);
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
		this.addSolutions();
	}
	
	private void removeEmptyRules() {
		Iterator<FieldRule<Field>> it = rules.iterator();
		while (it.hasNext()) {
			if (it.next().isEmpty())
				it.remove();
		}
	}
	
	private void solveRules() {
		if (this.rules.isEmpty())
			return;
		
		FieldGroup<Field> chosenGroup = this.rules.get(0).getSmallestFieldGroup();
		if (chosenGroup == null) {
			throw new IllegalStateException("Chosen group is null."); // return;
		}
		if (chosenGroup.size() == 0)
			throw new IllegalStateException("Chosen group has no size. " + chosenGroup.toString());
		
		for (int i = 0; i <= chosenGroup.size(); i++) {
			GroupValues<Field> mapCopy = new GroupValues<Field>(this.knownValues);
			mapCopy.put(chosenGroup, i);
			
			List<FieldRule<Field>> rulesCopy = new ArrayList<FieldRule<Field>>(); // deep copy!
			for (FieldRule<Field> rule : this.rules) {
				rulesCopy.add(new FieldRule<Field>(rule));
			}
			
			if (rulesCopy.remove(chosenGroup))
				System.out.println("remove has effect: "); // TODO: WTF!? This never has any effect.
			
			new GameAnalyze<Field>(mapCopy, rulesCopy, this.callback).solve();
		}
		
		
		// TODO: Remove comments.
		// find out which FieldGroup is used in the most rules, or which FieldRule has the least solutions
		// it does NOT seem to be a good idea to start solving the "big group"
		
		// if there is nothing to solve, then create a Solution object for the knownValues in this rule -- the List of Solutions should be stored in the "root" GameAnalyze,
		// and should be accessible from all sub-GameAnalyze objects.
		
		// when a FieldGroup has been chosen:
		/* for i = 0 to number of fields in FieldGroup do
		 *   copy knownValues
		 *   set value of chosen FieldGroup to i in copy
		 *   create a new GameAnalyze object with the copy, and a DEEP-copy of all the fieldRules (also copy the fieldRules themselves, not only the references!)
		 * 		simplifyRules in the new GameAnalyze object
		 * end
		 * 
		 * */
		
	}
	
	private void addSolutions() {
		/* when a GameAnalyze has been solved:
		 * calculate the total number of nCr possibilities for all the solutions (important number!)
		 * for each FieldGroup and for each Solution, check the probability for the FieldGroup in that solution.
		 */
		if (this.rules.isEmpty()) {
			if (this.callback != null) 
				this.callback.solved(Solution.solutionFactory(this.knownValues));
		}
	}

	private boolean simplifyRules() {
		boolean simplifyPerformed = true;
		while (simplifyPerformed) {
			simplifyPerformed = false;
			for (FieldRule<Field> ruleSimplify : rules) {
				SimplifyResult simplifyResult = ruleSimplify.simplify(knownValues);
				if (simplifyResult == SimplifyResult.SIMPLIFIED) {
					simplifyPerformed = true;
				}
				else if (simplifyResult != SimplifyResult.NO_EFFECT) {
					return false;
				}

			}
		}
		return true;
	}
	
}
