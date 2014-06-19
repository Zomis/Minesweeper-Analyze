package net.zomis.minesweeper.analyze;

import java.util.ArrayList;
import java.util.List;

/**
 * Static methods for combinatorics
 * 
 * @author Simon Forsberg
 */
public class Combinatorics {
	private Combinatorics() { }
	
	public static <T> List<T> listCombination(double combination, int size, List<T> elementList) {
		if (Math.rint(combination) != combination) {
			throw new IllegalArgumentException("x is not an integer " + combination);
		}
		
		List<Integer> a = indexCombinations(combination, size, elementList.size());
		if (a == null) {
			return null;
		}
		
		List<T> b = new ArrayList<T>(a.size());
		for (Integer i : a) {
			b.add(elementList.get((int) i));
		}
		return b;
	}
	
	public static <T> List<T> multiListCombination(List<FieldRule<T>> rules, double combinationNumber) {
		if (rules.isEmpty()) { 
			return new ArrayList<T>();
		}
		
		rules = new ArrayList<FieldRule<T>>(rules);
		FieldRule<T> first = rules.remove(0);
		double remaining = 1;
		for (FieldRule<T> fr : rules) {
			remaining = remaining * fr.nCr();
		}
		
		if (combinationNumber >= remaining * first.nCr()) { 
			throw new IllegalArgumentException("Not enough combinations. " + combinationNumber + " max is " + (remaining*first.nCr()));
		}
		
		double combo = combinationNumber % first.nCr();
		List<T> list = listCombination(combo, first.getResult(), first.getFieldGroups().iterator().next());
		if (!rules.isEmpty()) {
			List<T> recursive = multiListCombination(rules, Math.floor(combinationNumber / first.nCr()));
			if (recursive == null) {
				return null;
			}
			list.addAll(recursive);
		}
		
		return list;
	}
	
	public static List<Integer> indexCombinations(double x, int size, int elements) {
		if (size < 0 || size > elements) {
			return null;
		}
		if (size == 0) {
			return x == 0 ? new ArrayList<Integer>(size) : null;
		}
		
		if (size == elements) {
			List<Integer> a = new ArrayList<Integer>(size);
			for (int i = 0; i < elements; i++) {
				a.add(i);
			}
			return a;
		}
		if (x < nCr(elements - 1, size)) {
			return indexCombinations(x, size, elements - 1);
		}
		
		List<Integer> o = indexCombinations(x - nCr(elements - 1, size), size - 1, elements - 1);
		if (o != null) {
			o.add(elements - 1);
		}
		return o;
	}
	
	public static double nCr(int n, int r) {
		if (r > n || r < 0) {
			return 0;
		}
		if (r == 0 || r == n) {
			return 1;
		}
		
		double value = 1;
		
		for (int i = 0; i < r; i++) {
			value = value * (n - i) / (r - i);
		}
		
		return value;
	}


}
