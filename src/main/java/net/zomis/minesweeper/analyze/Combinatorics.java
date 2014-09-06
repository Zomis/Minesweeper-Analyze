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
	
	// TODO: Consider using `BigInteger` for picking combinations
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

	private static void zIndexCombinations(int[] result, double num, int size, int elements) {
		int resultIndex = 0;
		int nextNumber = 1;
		while (size > 0) {
			double ncr = nCr(elements - 1, size - 1);
			if (ncr <= 0.0) {
				throw new IllegalArgumentException("No such possible combination.");
			}
			if (num <= ncr) {
				result[resultIndex] = nextNumber;
				elements--;
				size--;
				nextNumber++;
				resultIndex++;
			}
			else {
				num -= ncr;
				elements--;
				nextNumber++;
			}
		}
		
		/* 
		 * 1 2 3 4
		 * 1 2, 1 3, 1 4, 2 3, 2 4, 3 4
		 * 
		 * 12345
		 * 12 13 14 15
		 * 23 24 25
		 * 34 35
		 * 45
		 * 
		 * 123 124 125
		 * 234 235
		 * 345
		 * 
		 * 12345678
		 * 1234 1235 1236 1237 1238
		 * 1245 1246 1247 1248
		 * 1256 1257 1258				1+2 chosen, 15 combinations (6 nCr 2)
		 * 1267 1268
		 * 1278
		 * 
		 * 1345 1346 1347 1348
		 * 1356 1357 1358
		 * 1367 1368					1+3 chosen, 10 combinations (5 nCr 2)
		 * 1378
		 * 
		 * 1456 1457 1458
		 * 1467 1468					1+4 chosen, 6 combinations (4 nCr 2)
		 * 1478
		 * 
		 * 1567 1568					1+5 chosen, 3 combinations (3 nCr 2)
		 * 1578
		 * 
		 * 1678							1+6 chosen, 1 combinations (2 nCr 2)
		 * 17--
		 * 18--
		 * 
		 * combination 20: GOAL 1356
		 * 8 nCr 4 = 70    (elements ncr size)
		 * 7 nCr 4 = 35
		 * num 20 < ncr 35
		 * elements--, size--
		 * First number is 1
		 * 
		 * 7 nCr 3 = 35
		 * 6 nCr 3 = 20
		 * num 20 < ncr 20
		 * Second number is not 2
		 * elements--, nextNumber++
		 * num -= 5 nCr 3				num -= 6 nCr 2, num = 5
		 * num 10 < ncr 20
		 * Second number is 3
		 * elements--, size--
		 * 
		 * 5 nCr 2 = 10
		 * num 10 < ncr 10
		 * Third number is not 4
		 * elements--, nextNumber++ (elements = 4, nextNumber = 5, size = 2)
		 * num -= 4 nCr 2
		 * num 4 < ncr 10
		 * Third number is 5
		 * elements--, size--
		 * 
		 * (elements = 3, nextNumber = 6, size = 1)
		 * num 4 < ncr 3
		 * 
		 */
	}
	
	public static int[] zIndexCombinations(double x, int size, int elements) {
		if (Math.floor(x) != Math.ceil(x)) {
			throw new IllegalArgumentException("x must be a whole number");
		}
		if (x <= 0.0) {
			throw new IllegalArgumentException("x must be positive");
		}
		
		int[] result = new int[size];
		zIndexCombinations(result, x, size, elements);
		return result;
	}

}
