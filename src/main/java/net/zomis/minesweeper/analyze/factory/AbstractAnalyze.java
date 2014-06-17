package net.zomis.minesweeper.analyze.factory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.zomis.minesweeper.analyze.FieldRule;
import net.zomis.minesweeper.analyze.RootAnalyzeImpl;

public abstract class AbstractAnalyze<F> extends RootAnalyzeImpl<F> {
	
	public AbstractAnalyze() {
	}
	
	protected abstract List<F> getAllPoints();
	
	protected final void createRules(List<F> points) {
		Set<F> knownNonMines = new HashSet<F>();
		int remaining = getRemainingMinesCount();
		if (remaining != -1) {
			this.addRule(new FieldRule<F>(null, getAllUnclickedFields(), remaining));
		}
		
		for (F field : points) {
			if (!fieldHasRule(field))
				continue;

			FieldRule<F> newRule = internalRuleFromField(field, knownNonMines);
			if (newRule != null) {
				this.addRule(newRule);
			}
		}
		if (!knownNonMines.isEmpty())
			this.addRule(new FieldRule<F>(null, knownNonMines, 0));
	}
	
	/**
	 * Determines if the specified field is/has a rule that should be added to the constraints
	 * 
	 * @param field Field that is being checked
	 * @return True if the field has a rule that should be applied, false otherwise
	 */
	protected abstract boolean fieldHasRule(F field);
	
	protected abstract int getRemainingMinesCount();
	
	protected abstract List<F> getAllUnclickedFields();
	
	private FieldRule<F> internalRuleFromField(F field, Set<F> knownNonMines) {
		List<F> ruleParams = new ArrayList<F>();
		int foundNeighbors = 0;
		int fieldValue = getFieldValue(field);
		for (F neighbor : getNeighbors(field)) {
			if (isDiscoveredMine(neighbor)) 
				foundNeighbors++;
			else if (!isClicked(neighbor))
				ruleParams.add(neighbor);
		}
		
		if (fieldValue - foundNeighbors == 0) {
			if (knownNonMines != null) {
				for (F mf : ruleParams) {
					if (!knownNonMines.contains(mf)) {
						knownNonMines.add(mf);
					}
				}
			}
			return null;
		}
		else return new FieldRule<F>(field, ruleParams, fieldValue - foundNeighbors);
	}
	
	protected abstract boolean isDiscoveredMine(F neighbor);

	protected abstract int getFieldValue(F field);
	
	protected abstract List<F> getNeighbors(F field);
	
	protected abstract boolean isClicked(F neighbor);
	
}
