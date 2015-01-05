package net.zomis.minesweeper.analyze;

import java.util.Collection;
import java.util.Iterator;

/**
 * A constraint of a number of fields or {@link FieldGroup}s that should have a sum within a specific range
 * 
 * @author Simon Forsberg
 * @param <T> Field type
 */
public class BoundedFieldRule<T> extends FieldRule<T> {
	
	protected int maxResult = 0;
	protected int minResult = 0;
	
	/**
	 * Create a copy of an existing rule.
	 * 
	 * @param copyFrom Rule to copy
	 */
	private BoundedFieldRule(BoundedFieldRule<T> copyFrom) {
		super(copyFrom.getCause(), copyFrom.fields);
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
		super(cause, rule, min);
		this.minResult = min;
		this.maxResult = max;
	}
	
	@Override
	@Deprecated
	public double nCr() {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public boolean isEmpty() {
		return fields.isEmpty() && minResult >= 0;
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
				result -= known;
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
		
		// (a + b) + (c + d) <= 0 would give the value 0 to all field groups and simplify things
		if (maxResult == 0) {
			for (FieldGroup<T> field : fields) {
				knownValues.put(field, 0);
			}
			SimplifyResult simplifyResult = fields.isEmpty() ? SimplifyResult.NO_EFFECT : SimplifyResult.SIMPLIFIED;
			fields.clear();
			result = 0;
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
			result = 0;
			minResult = 0;
			maxResult = 0;
			return simplifyResult;
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
	public FieldRule<T> copy() {
		return new BoundedFieldRule<T>(this);
	}
}
