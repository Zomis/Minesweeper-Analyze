package net.zomis.minesweeper.analyze;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

public class Solution<T> {
	private final GroupValues<T> setGroupValues;
	private double nCrValue;
	private double mapTotal;
	
	private Solution(GroupValues<T> values) {
		this.setGroupValues = values;
	}
	
	public static <T> Solution<T> solutionFactorySkipNCRCalc(GroupValues<T> values) {
		return new Solution<T>(values);
	}
	public static <T> Solution<T> solutionFactory(GroupValues<T> values) {
		return solutionFactorySkipNCRCalc(values).nCrPerform();
	}
	
	public Solution<T> copyWithoutNCRData() {
		return new Solution<T>(this.setGroupValues);
	}
	
	private Solution<T> nCrPerform() {
		double result = 1;
		for (Entry<FieldGroup<T>, Integer> ee : this.setGroupValues.entrySet()) {
			result = result * RootAnalyzeImpl.nCr(ee.getKey().size(), ee.getValue());
		}
		this.nCrValue = result;
		return this;
	}

	public double nCr() {
		return this.nCrValue;
	}
	
	public double getProbability(double nCrTotal) {
		return this.nCr() / nCrTotal;
	}

	public GroupValues<T> getSetGroupValues() {
		return new GroupValues<T>(setGroupValues);
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
		str.append(this.nCrValue + " combinations (" + this.getChance() + ")");
		return str.toString();
	}

	public double getChance() {
		if (this.mapTotal == 0) return -1;
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
	
	private List<T> combination(List<Entry<FieldGroup<T>, Integer>> grpValues, double combination) {
		if (grpValues.isEmpty()) return new LinkedList<T>();
		
		grpValues = new LinkedList<Entry<FieldGroup<T>, Integer>>(grpValues);
		Entry<FieldGroup<T>, Integer> first = grpValues.remove(0);
		double remaining = 1;
		for (Entry<FieldGroup<T>, Integer> fr : grpValues) remaining = remaining * nCr(fr);
		
		double fncr = nCr(first);
		
		if (combination >= remaining * fncr)
			return null;// TODO: throw new IllegalArgumentException("Not enough combinations. " + combinationNumber + " max is " + (remaining*first.nCr()));
		
		double combo = combination % fncr;
		List<T> list = ncrcomb(combo, first.getValue(), first.getKey());
		if (!grpValues.isEmpty()) {
			List<T> recursive = combination(grpValues, Math.floor(combination / fncr));
			if (recursive == null) {
				return null;
			}
			list.addAll(recursive);
		}
		
		return list;		
	}
	
	private static <T> double nCr(Entry<FieldGroup<T>, Integer> rule) {
		return RootAnalyzeImpl.nCr(rule.getKey().size(), rule.getValue());
	}

	public List<T> getCombination(double combinationIndex) {
		return combination(new LinkedList<Map.Entry<FieldGroup<T>,Integer>>(this.setGroupValues.entrySet()), combinationIndex);
	}
	
	public static List<Integer> ncrcombo2(double x, int size, int elements){
		if ((size < 0) || (size > elements)) return null;
		if (size == 0) {
			if (x == 0) return new LinkedList<Integer>();
			else return null;
		}
		else if (size == elements) {
			List<Integer> a = new LinkedList<Integer>();
			for (int i = 0; i < elements; i++) a.add(i);
			return a;
		}
		else if (x < RootAnalyzeImpl.nCr(elements - 1, size)) {
			return ncrcombo2(x, size, elements - 1);
		}
		else {
			List<Integer> o = ncrcombo2(x - RootAnalyzeImpl.nCr(elements - 1, size), size - 1, elements - 1);
			if (o != null) o.add(elements - 1);
			return o;
		}
	}
	public static <T> List<T> ncrcomb(double x, int size, List<T> elementList) {
		if (Math.rint(x) != x) throw new IllegalArgumentException("x is not an integer " + x);
		
		List<Integer> a = ncrcombo2(x, size, elementList.size());
		if (a == null) return null;
		
		List<T> b = new LinkedList<T>();
		for (Integer i : a) b.add(elementList.get(i));
		return b;
	}

	public static <Field> List<Field> ncrCombo(List<FieldRule<Field>> rules, double combinationNumber) {
		if (rules.isEmpty()) return new LinkedList<Field>();
		
		rules = new LinkedList<FieldRule<Field>>(rules);
		FieldRule<Field> first = rules.remove(0);
		double remaining = 1;
		for (FieldRule<Field> fr : rules) remaining = remaining * fr.nCr();
		
		if (combinationNumber >= remaining * first.nCr()) return null;// throw new IllegalArgumentException("Not enough combinations. " + combinationNumber + " max is " + (remaining*first.nCr()));
		
		double combo = combinationNumber % first.nCr();
		List<Field> list = ncrcomb(combo, first.getResult(), first.getFieldGroups().iterator().next());
		if (!rules.isEmpty()) {
			List<Field> recursive = ncrCombo(rules, Math.floor(combinationNumber / first.nCr()));
			if (recursive == null) {
				return null;
			}
			list.addAll(recursive);
		}
		
		return list;
	}

	
	
}
