package net.zomis.minesweeper.analyze;

import net.zomis.minesweeper.analyze.listener.Analyze;
import net.zomis.minesweeper.analyze.listener.RuleListener;
import net.zomis.minesweeper.analyze.listener.SolveListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GameAnalyze<T> implements Analyze<T> {

	private final GroupValues<T> knownValues;
	private final List<RuleConstraint<T>> rules;
	private final int depth;
    private final SolveListener<T> listener;
    private final InterruptCheck interruptCheck;

    GameAnalyze(InterruptCheck interruptCheck, GroupValues<T> knownValues, List<RuleConstraint<T>> unsolvedRules,
                int depth, SolveListener<T> listener) {
        this.interruptCheck = interruptCheck;
		this.knownValues = knownValues == null ? new GroupValues<T>() : new GroupValues<T>(knownValues);
		this.rules = unsolvedRules;
        this.depth = depth;
		this.listener = listener;
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
        final List<RuleConstraint<T>> addedRules = new ArrayList<RuleConstraint<T>>();
        final Analyze<T> analyze = new Analyze<T>() {
            @Override
            public int getDepth() {
                return GameAnalyze.this.getDepth();
            }

            @Override
            public void addRule(RuleConstraint<T> rule) {
                addedRules.add(rule);
            }

            @Override
            public GroupValues<T> getKnownValues() {
                return GameAnalyze.this.getKnownValues();
            }
        };
        RuleListener<T> ruleListener = new RuleListener<T>() {
            @Override
            public void onValueSet(FieldGroup<T> group, int value) {
                listener.onValueSet(analyze, group, value);
            }
        };
        while (simplifyPerformed) {
			simplifyPerformed = false;
			Iterator<RuleConstraint<T>> it = rules.iterator();
			while (it.hasNext()) {
				RuleConstraint<T> ruleSimplify = it.next();
				SimplifyResult simplifyResult = ruleSimplify.simplify(knownValues, ruleListener);
				if (simplifyResult == SimplifyResult.SIMPLIFIED) {
					simplifyPerformed = true;
				}
				else if (simplifyResult.isFailure()) {
					return false;
				}
				if (ruleSimplify.isEmpty()) {
					it.remove();
				}
			}
            if (!addedRules.isEmpty()) {
                this.rules.addAll(addedRules);
                // as rules have been added, we need to split into field groups again
                AnalyzeFactory.splitFieldRules(this.rules);
                addedRules.clear();
                simplifyPerformed = true;
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
		if (interruptCheck.isInterrupted()) {
    		throw new RuntimeTimeoutException();
		}
		
		if (this.rules.isEmpty()) {
			return 0;
        }

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

            GameAnalyze<T> copy = new GameAnalyze<T>(interruptCheck, mapCopy, rulesCopy, depth + 1, this.listener);
            int rulesCountBefore = copy.rules.size();
            listener.onValueSet(copy, chosenGroup, i);
            int rulesCountAfter = copy.rules.size();
            if (rulesCountBefore != rulesCountAfter) {
                // onValueSet has added rules
                AnalyzeFactory.splitFieldRules(copy.rules);
            }
			total += copy.solve(solutions);
		}
		return total;
	}

	private FieldGroup<T> getSmallestFieldGroup() {
		for (RuleConstraint<T> rule : rules) {
            // TODO: this implementation seem to rely on a small field group existing in the first rule,
            // this is technically not necessary, but is how I have implemented it in Minesweeper Flags Extreme
			FieldGroup<T> smallest = rule.getSmallestFieldGroup();
			if (smallest != null) {
				return smallest;
			}
		}
		return null;
	}

    public int getDepth() {
        return depth;
    }

    @Override
    public GroupValues<T> getKnownValues() {
        return new GroupValues<T>(this.knownValues);
    }

    @Override
    public void addRule(RuleConstraint<T> ruleConstraint) {
        this.rules.add(ruleConstraint);
    }

}
