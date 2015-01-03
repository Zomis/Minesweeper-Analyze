package net.zomis.minesweeper.analyze;

public interface RuleConstraint<T> {

	SimplifyResult simplify(GroupValues<T> knownValues);

	FieldRule<T> copy();
}
