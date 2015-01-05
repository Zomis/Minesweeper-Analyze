package net.zomis.minesweeper.analyze;

import java.util.List;

public interface RuleConstraint<T> extends Iterable<List<FieldGroup<T>>> {

	SimplifyResult simplify(GroupValues<T> knownValues);

	FieldRule<T> copy();
	
	boolean isEmpty();

	FieldGroup<T> getSmallestFieldGroup();

	boolean checkIntersection(RuleConstraint<T> rule);
}
