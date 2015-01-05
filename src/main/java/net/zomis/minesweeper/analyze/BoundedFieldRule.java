package net.zomis.minesweeper.analyze;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * A constraint of a number of fields or {@link FieldGroup}s that should have a sum within a specific range
 * 
 * @author Simon Forsberg
 * @param <T> Field type
 */
public class BoundedFieldRule<T> implements RuleConstraint<T> {
	
	private final T cause;
	protected final List<FieldGroup<T>> fields;
	protected int maxResult = 0;
	protected int minResult = 0;
	
	/**
	 * Create a copy of an existing rule.
	 * 
	 * @param copyFrom Rule to copy
	 */
	private BoundedFieldRule(BoundedFieldRule<T> copyFrom) {
		this.cause = copyFrom.cause;
		this.fields = new ArrayList<FieldGroup<T>>(copyFrom.fields);
		this.minResult = copyFrom.minResult;
		this.maxResult = copyFrom.maxResult;
	}
	
	/**
	 * Create a rule from a list of fields and a result (create a new FieldGroup for it)
	 * 
	 * @param cause The reason for why this rule is added (optional, may be null)
	 * @param rule Fields that this rule applies to
	 * @param result The value that should be forced for the fields
	 */
	public BoundedFieldRule(T cause, Collection<T> rule, int min, int max) {
		this.cause = cause;
		this.fields = new ArrayList<FieldGroup<T>>();
		this.fields.add(new FieldGroup<T>(rule));
		this.minResult = min;
		this.maxResult = max;
	}
	
	public BoundedFieldRule(T cause, List<FieldGroup<T>> fields, int min, int max) {
		this.cause = cause;
		this.fields = new ArrayList<FieldGroup<T>>(fields);
		this.minResult = min;
		this.maxResult = max;
	}

	@Override
	public boolean isEmpty() {
		return fields.isEmpty() && minResult <= 0 && maxResult >= 0;
	}

	@Override
	public SimplifyResult simplify(GroupValues<T> knownValues) {
		if (this.isEmpty()) {
			return SimplifyResult.NO_EFFECT;
		}
		
		Iterator<FieldGroup<T>> it = fields.iterator();
		// a + b <= 1 ---- a = 1 ---> b <= 0 ---> b = 0
		int totalCount = 0;
		while (it.hasNext()) {
			FieldGroup<T> group = it.next();
			Integer known = knownValues.get(group);
			if (known != null) {
				it.remove();
				minResult -= known;
				maxResult -= known;
			}
			else totalCount += group.size();
		}
		
		// a + b < 0 is not a valid rule
		if (maxResult < 0) {
			return SimplifyResult.FAILED_NEGATIVE_RESULT;
		}
		
		// a + b > 2 is not a valid rule.
		if (minResult > totalCount) {
			return SimplifyResult.FAILED_TOO_BIG_RESULT;
		}
		
		// (a + b) = 1 or (a + b) = 0 would give a value to the (a + b) group and simplify things.
		if (fields.size() == 1 && minResult == maxResult) {
			knownValues.put(fields.get(0), minResult);
			fields.clear();
			minResult = 0;
			return SimplifyResult.SIMPLIFIED;
		}
		
		// (a + b) + (c + d) <= 0 would give the value 0 to all field groups and simplify things
		if (maxResult == 0) {
			for (FieldGroup<T> field : fields) {
				knownValues.put(field, 0);
			}
			SimplifyResult simplifyResult = fields.isEmpty() ? SimplifyResult.NO_EFFECT : SimplifyResult.SIMPLIFIED;
			fields.clear();
			minResult = 0;
			maxResult = 0;
			return simplifyResult;
		}
		
		// (a + b) + (c + d) = 4 would give the value {Group.SIZE} to all Groups.
		if (totalCount == minResult) {
			for (FieldGroup<T> field : fields) {
				knownValues.put(field, minResult * field.size() / totalCount);
			}
			SimplifyResult simplifyResult = fields.isEmpty() ? SimplifyResult.NO_EFFECT : SimplifyResult.SIMPLIFIED;
			fields.clear();
			minResult = 0;
			maxResult = 0;
			return simplifyResult;
		}
		
		if (minResult <= 0 && maxResult >= totalCount) {
			// Rule is effectively useless
			fields.clear();
			minResult = 0;
			maxResult = 0;
		}
		
		return SimplifyResult.NO_EFFECT;
	}

	@Override
	public String toString() {
		StringBuilder rule = new StringBuilder();
		rule.append(minResult);
		rule.append(" <= ");
		boolean fieldAdded = false;
		for (FieldGroup<T> field : this.fields) {
			if (fieldAdded) {
				rule.append(" + ");
			}
			fieldAdded = true;
			rule.append(field.toString());
		}
		rule.append(" <= ");
		rule.append(maxResult);
		return rule.toString(); 
	}
	
	@Override
	public BoundedFieldRule<T> copy() {
		return new BoundedFieldRule<T>(this);
	}

	@Override
	public Iterator<List<FieldGroup<T>>> iterator() {
		return new IterateOnce<List<FieldGroup<T>>>(this.fields);
	}

	@Override
	public FieldGroup<T> getSmallestFieldGroup() {
		if (this.fields.isEmpty()) {
			return null;
		}
		
		FieldGroup<T> result = this.fields.get(0);
		for (FieldGroup<T> group : this.fields) {
			int size = group.size();
			if (size == 1) {
				return group;
			}
			if (size < result.size()) {
				result = group;
			}
		}
		return result;
	}

	@Override
	public T getCause() {
		return cause;
	}

	public int getMinResult() {
		return minResult;
	}
	
	public int getMaxResult() {
		return maxResult;
	}
	
}
