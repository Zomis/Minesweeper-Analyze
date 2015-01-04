package net.zomis.minesweeper.analyze;

public interface RuleConstraint<T> {

	SimplifyResult simplify(GroupValues<T> knownValues);

	FieldRule<T> copy();
	
	boolean isEmpty();

	FieldGroup<T> getSmallestFieldGroup();

	boolean checkIntersection(RuleConstraint<T> rule);
}
