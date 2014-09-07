package net.zomis.minesweeper.analyze;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * Static methods for combinatorics
 * 
 * @author Simon Forsberg
 */
public class Combinatorics {
	private Combinatorics() { }
	
	public static double NNKK(int N, int n, int K, int k) {
		return nCr(K, k) * nCr(N - K, n - k); //	/ RootAnalyze.nCr(N, n)
	}
	
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
	
	@Deprecated
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

	public static BigInteger nCrBigInt(int n, int r) {
		if (r > n || r < 0) {
			return BigInteger.ZERO;
		}
		if (r == 0 || r == n) {
			return BigInteger.ONE;
		}
		if (r > n / 2) {
			// As Pascal's triangle is horizontally symmetric, use that property to reduce the for-loop below
			r = n - r;
		}
		
		BigInteger value = BigInteger.ONE;
		
		for (int i = 0; i < r; i++) {
			value = value.multiply(BigInteger.valueOf(n - i)).divide(BigInteger.valueOf(i + 1));
		}
		
		return value;
	}

	public static int[] specificCombination(final int elements, final int size, final BigInteger combinationNumber) {
		if (combinationNumber.signum() != 1) {
			throw new IllegalArgumentException("Combination must be positive");
		}
		if (elements < 0 || size < 0) {
			throw new IllegalArgumentException("Elements and size cannot be negative");
		}
		
		int[] result = new int[size];
		
		int resultIndex = 0;
		int nextNumber = 0;
		BigInteger combination = combinationNumber;
		int remainingSize = size;
		int remainingElements = elements;
		
		while (remainingSize > 0) {
			BigInteger ncr = nCrBigInt(remainingElements - 1, remainingSize - 1);
			if (ncr.signum() == 0) {
				throw new IllegalArgumentException("Combination out of range: " + combinationNumber + " with " + elements + " elements and size " + size);
			}
			if (combination.compareTo(ncr) <= 0) {
				result[resultIndex] = nextNumber;
				remainingSize--;
				resultIndex++;
			}
			else {
				combination = combination.subtract(ncr);
			}
			remainingElements--;
			nextNumber++;
		}
		
		return result;
	}

	private static void specificCombination(int[] result, double combination, int elements, int size) {
		int resultIndex = 0;
		int nextNumber = 0;
		
		while (size > 0) {
			double ncr = nCr(elements - 1, size - 1);
			if (combination <= ncr) {
				result[resultIndex] = nextNumber;
				elements--;
				size--;
				nextNumber++;
				resultIndex++;
			}
			else {
				combination -= ncr;
				elements--;
				nextNumber++;
			}
		}
	}
	
	public static int[] specificCombination(int elements, int size, double combination) {
		if (Math.floor(combination) != Math.ceil(combination)) {
			throw new IllegalArgumentException("Combination must be a whole number");
		}
		if (combination <= 0.0) {
			throw new IllegalArgumentException("Combination must be positive");
		}

		int[] result = new int[size];
		specificCombination(result, combination, elements, size);
		return result;
	}

}
