package net.zomis.minesweeper.analyze;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class FieldRule<T> {
	
	private final List<FieldGroup<T>> fields;
	/**
	 * Modified in result and can therefore not be final.
	 */
	private int result = 0;
	private final T cause;
	
	public int getResult() {
		return this.result;
	}
	
	public T getCause() {
		return this.cause;
	}
	
	/**
	 * Create a rule from a list of fields and a result (create a new FieldGroup for it)
	 * 
	 * @param cause The reason for why this rule is added (may be null)
	 * @param rule Fields that this rule applies to
	 * @param result The value that should be forced for the fields
	 */
	public FieldRule(T cause, Collection<T> rule, int result) {
		this.fields = new ArrayList<FieldGroup<T>>();
		this.fields.add(new FieldGroup<T>(rule));
		this.result = result;
		this.cause = cause;
	}
	
	FieldRule(T cause, FieldGroup<T> group, int result) {
		this.cause = cause;
		this.fields = new ArrayList<FieldGroup<T>>();
		this.fields.add(group);
		this.result = result;
	}
	
	/**
	 * Create a copy of an existing rule.
	 * 
	 * @param copyFrom Rule to copy
	 */
	public FieldRule(FieldRule<T> copyFrom) {
		this.fields = new ArrayList<FieldGroup<T>>(copyFrom.fields); // Deep copy? Probably not. FieldGroup don't change much.
		this.result = copyFrom.result;
		this.cause = copyFrom.cause;
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
		rule.append(result);
		return rule.toString(); 
	}
	
	boolean isIsolated() {
		for (FieldGroup<T> fg : this.fields)
			if (fg.size() != 1)
				return false;
		return true;
	}
	
	public SimplifyResult simplify(Map<FieldGroup<T>, Integer> knownValues) {
		if (this.isEmpty()) {
			return SimplifyResult.NO_EFFECT;
		}
		
		Iterator<FieldGroup<T>> it = fields.iterator();
		while (it.hasNext()) {
			FieldGroup<T> field = it.next();
			Integer known = knownValues.get(field);
			if (known != null) {
				it.remove();
				result -= known;
			}
		}
		
		if (result < 0) {
			return SimplifyResult.FAILED_NEGATIVE_RESULT;
		}
		
		if (result > getFieldsCountInGroups()) {
			return SimplifyResult.FAILED_TOO_BIG_RESULT;
		}
		
		if (fields.size() == 1) {
			knownValues.put(fields.get(0), result);
			fields.clear();
			result = 0;
			return SimplifyResult.SIMPLIFIED;
		}
		
		if (result == 0) {
			for (FieldGroup<T> field : fields) {
				knownValues.put(field, 0);
			}
			fields.clear();
			result = 0;
			return SimplifyResult.SIMPLIFIED;
		}
		
		if (getFieldsCountInGroups() == result) {
			for (FieldGroup<T> field : fields) {
				knownValues.put(field, result * field.size() / getFieldsCountInGroups());
			}
			return SimplifyResult.SIMPLIFIED;
		}
		return SimplifyResult.NO_EFFECT;
	}
	
	public int getFieldsCountInGroups() {
		int fieldsCounter = 0;
		for (FieldGroup<T> fg : fields) {
			fieldsCounter += fg.size();
		}
		return fieldsCounter;
	}
	
	List<FieldGroup<T>> checkIntersection(FieldRule<T> rule) {
		if (rule == this)
			return null;
		
//		boolean splitDone = false;
		List<FieldGroup<T>> fieldsCopy = new ArrayList<FieldGroup<T>>(fields);
		List<FieldGroup<T>> ruleFieldsCopy = new ArrayList<FieldGroup<T>>(rule.fields);
		
//		RootAnalyze.log("checkIntersect A: " + this);
//		RootAnalyze.log("checkIntersect B: " + rule);
		
		for (FieldGroup<T> groupA : fieldsCopy) {
			for (FieldGroup<T> groupB : ruleFieldsCopy) {
				if (groupA == groupB)
					continue;
				
				List<FieldGroup<T>> splitResult = groupA.splitCheck(groupB);
				if (splitResult == null) continue; // nothing to split
				
				FieldGroup<T> both = splitResult.get(1);
				FieldGroup<T> onlyA = splitResult.get(0);
				FieldGroup<T> onlyB = splitResult.get(2);
				
//				RootAnalyze.log("Intersection Found! FieldGroups both: " + both);
//				RootAnalyze.log("Intersection Found! FieldGroups onlyA: " + onlyA);
//				RootAnalyze.log("Intersection Found! FieldGroups onlyB: " + onlyB);
				
				this.fields.remove(groupA);
				if (!onlyA.isEmpty()) this.fields.add(onlyA);
				if (!both.isEmpty()) this.fields.add(both);
				
				rule.fields.remove(groupB);
				if (!both.isEmpty()) rule.fields.add(both);
				if (!onlyB.isEmpty()) rule.fields.add(onlyB);
//				splitDone = true;
				return splitResult;
			}
		}
		
		return null;
	}
	
	public boolean isEmpty () {
		return fields.isEmpty() && result == 0;
	}

	public FieldGroup<T> getSmallestFieldGroup() {
		if (this.fields.isEmpty()) return null;
		
		FieldGroup<T> ret = this.fields.get(0);
		for (FieldGroup<T> group : this.fields) {
			if (group.size() < ret.size()) ret = group;
		}
		return ret;
	}

	public Collection<FieldGroup<T>> getFieldGroups() {
		return new ArrayList<FieldGroup<T>>(this.fields);
	}

	public double nCr() {
		if (this.fields.size() != 1) throw new IllegalStateException("Rule has more than one group.");
		return RootAnalyzeImpl.nCr(this.getFieldsCountInGroups(), this.result);
	}
}
