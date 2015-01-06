package net.zomis.minesweeper.analyze;

import java.util.ArrayList;
import java.util.Collection;

/**
 * A constraint of a number of fields or {@link FieldGroup}s that should have a specific sum
 * 
 * @author Simon Forsberg
 * @param <T> Field type
 */
public class FieldRule<T> extends BoundedFieldRule<T> {
	
	/**
	 * Create a copy of an existing rule.
	 * 
	 * @param copyFrom Rule to copy
	 */
	private FieldRule(FieldRule<T> copyFrom) {
		super(copyFrom.getCause(), copyFrom.fields, copyFrom.getResult(), copyFrom.getResult());
	}
	
	/**
	 * Create a rule from a list of fields and a result (create a new FieldGroup for it)
	 * 
	 * @param cause The reason for why this rule is added (optional, may be null)
	 * @param rule Fields that this rule applies to
	 * @param result The value that should be forced for the fields
	 */
	public FieldRule(T cause, Collection<T> rule, int result) {
		super(cause, rule, result, result);
	}
	
	FieldRule(T cause, FieldGroup<T> group, int result) {
		super(cause, new ArrayList<T>(), result, result);
		this.fields.add(group);
	}
	
	public Collection<FieldGroup<T>> getFieldGroups() {
		return new ArrayList<FieldGroup<T>>(this.fields);
	}
	
	public int getFieldsCountInGroups() {
		int fieldsCounter = 0;
		for (FieldGroup<T> group : fields) {
			fieldsCounter += group.size();
		}
		return fieldsCounter;
	}
	
	public int getResult() {
		return this.getMinResult();
	}
	
	public double nCr() {
		if (this.fields.size() != 1) {
			throw new IllegalStateException("Rule has more than one group.");
		}
		return Combinatorics.nCr(this.getFieldsCountInGroups(), this.minResult);
	}

	@Override
	public int getMaxResult() {
		return getMinResult();
	}
	
	@Override
	public String toString() {
		StringBuilder rule = new StringBuilder();
		for (FieldGroup<T> field : this.fields) {
			if (rule.length() > 0) {
				rule.append(" + ");
			}
			rule.append(field.toString());
		}
		rule.append(" = ");
		rule.append(getResult());
		return rule.toString(); 
	}

	@Override
	public FieldRule<T> copy() {
		return new FieldRule<T>(this);
	}
	
}
