package net.zomis.minesweeper.analyze;

import java.util.List;


public interface RuleConstraint<T> {
	SimplifyResult simplify(GroupValues<T> knownValues);

	RuleConstraint<T> copy();
	
	boolean isEmpty();

	FieldGroup<T> getSmallestFieldGroup();

	T getCause();
	
	List<FieldGroup<T>> fieldGroups(); 
}
