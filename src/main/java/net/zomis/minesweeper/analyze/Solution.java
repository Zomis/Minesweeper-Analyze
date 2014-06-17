package net.zomis.minesweeper.analyze;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

public class Solution<T> {
	public static <T> Solution<T> createSolution(GroupValues<T> values) {
		return new Solution<T>(values).nCrPerform();
	}
	
	private static <T> double nCr(Entry<FieldGroup<T>, Integer> rule) {
		return Combinatorics.nCr(rule.getKey().size(), rule.getValue());
	}
	
	private double mapTotal;
	
	private double nCrValue;

	private final GroupValues<T> setGroupValues;
	
	private Solution(GroupValues<T> values) {
		this.setGroupValues = values;
	}

	private List<T> combination(List<Entry<FieldGroup<T>, Integer>> grpValues, double combination) {
		if (grpValues.isEmpty()) {
			return new LinkedList<T>();
		}
		
		grpValues = new LinkedList<Entry<FieldGroup<T>, Integer>>(grpValues);
		Entry<FieldGroup<T>, Integer> first = grpValues.remove(0);
		double remaining = 1;
		for (Entry<FieldGroup<T>, Integer> fr : grpValues) {
			remaining = remaining * nCr(fr);
		}
		
		double fncr = nCr(first);
		
		if (combination >= remaining * fncr) {
			throw new IllegalArgumentException("Not enough combinations. " + combination + " max is " + (remaining * fncr));
		}
		
		double combo = combination % fncr;
		List<T> list = Combinatorics.ncrcomb(combo, first.getValue(), first.getKey());
		if (!grpValues.isEmpty()) {
			List<T> recursive = combination(grpValues, Math.floor(combination / fncr));
			if (recursive == null) {
				return null;
			}
			list.addAll(recursive);
		}
		
		return list;		
	}
	
	public Solution<T> copyWithoutNCRData() {
		return new Solution<T>(this.setGroupValues);
	}

	public List<T> getCombination(double combinationIndex) {
		return combination(new LinkedList<Map.Entry<FieldGroup<T>,Integer>>(this.setGroupValues.entrySet()), combinationIndex);
	}
	
	public double getCombinations() {
		return this.nCrValue;
	}
	
	public double getProbability() {
		if (this.mapTotal == 0)
			throw new IllegalStateException("The total number of solutions on map is unknown");
		return this.nCrValue / this.mapTotal;
	}
	
	public List<T> getRandomSolution(Random random) {
		List<T> result = new ArrayList<T>();
		
		for (Entry<FieldGroup<T>, Integer> ee : this.setGroupValues.entrySet()) {
			List<T> group = new ArrayList<T>(ee.getKey());
			Collections.shuffle(group, random);
			
			for (int i = 0; i < ee.getValue(); i++) {
				result.add(group.remove(0));
			}
		}
		
		return result;
	}
	
	public GroupValues<T> getSetGroupValues() {
		return new GroupValues<T>(setGroupValues);
	}

	public double nCr() {
		return this.nCrValue;
	}
	
	private Solution<T> nCrPerform() {
		double result = 1;
		for (Entry<FieldGroup<T>, Integer> ee : this.setGroupValues.entrySet()) {
			result = result * Combinatorics.nCr(ee.getKey().size(), ee.getValue());
		}
		this.nCrValue = result;
		return this;
	}
	void setTotal(double total) {
		this.mapTotal = total;
		for (Entry<FieldGroup<T>, Integer> ee : this.setGroupValues.entrySet()) {
			ee.getKey().informAboutSolution(ee.getValue(), this, total);
		}
	}

	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();
		for (Entry<FieldGroup<T>, Integer> ee : this.setGroupValues.entrySet()) {
			str.append(ee.getKey() + " = " + ee.getValue() + ", ");
		}
		str.append(this.nCrValue + " combinations (" + this.getProbability() + ")");
		return str.toString();
	}

	
	
}
