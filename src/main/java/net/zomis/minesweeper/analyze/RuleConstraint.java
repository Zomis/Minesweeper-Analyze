package net.zomis.minesweeper.analyze;

import net.zomis.minesweeper.analyze.listener.RuleListener;

import java.util.List;


public interface RuleConstraint<T> {
	/**
	 * Apply various values to this rule to potentially simplify it and learn something new
	 * 
	 * @param knownValues Known values that can be removed and cleaned up from this rule to simplify it
	 * @param listener
	 * @return A {@link SimplifyResult} corresponding to how successful the simplification was
	 */
	SimplifyResult simplify(GroupValues<T> knownValues, RuleListener<T> listener);

	/**
	 * Create a copy of this rule, for trial-and-error purposes
	 * 
	 * @return A copy of this rule in its current state
	 */
	RuleConstraint<T> copy();
	
	/**
	 * Determine whether or not this rule is finished and thus can be removed from the list of rules
	 * 
	 * @return True if this rule is successfully finished, false otherwise
	 */
	boolean isEmpty();

	/**
	 * Find the best field group to branch on
	 * 
	 * @return The best {@link FieldGroup} to branch on, or null if this rule does not have a preference about how to branch
	 */
	FieldGroup<T> getSmallestFieldGroup();

	/**
	 * @return An indication on what caused this rule to be created
	 */
	T getCause();
	
	/**
	 * @return Direct access to the {@link FieldGroup}s in this rule, in order to split them
	 */
	List<FieldGroup<T>> fieldGroups(); 
}
