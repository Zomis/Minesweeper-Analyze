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
	
	/**
	 * Calculates the combinations for the hypergeometric probability distribution, that is, it does not divide by <code>N nCr n</code>.
	 * <p>Example: There are 6 fields, 3 of them contains a mine. You have the possibility to take 2 of these at once.<br>
	 * <code>NNKK(6, 3, 2, 0)</code> will return the <b>number of combinations</b> where 0 of the 2 you are taking contains a mine.</p>
	 * 
	 * @see <a href="http://en.wikipedia.org/wiki/Hypergeometric_distribution">Hypergeometric Distribution on Wikipedia</a>
	 * 
	 * @param N All elements
	 * @param n All elements containing what we are looking for
	 * @param K How many elements are we looking in
	 * @param k How many elements we are looking in that contains what we are looking for
	 * @return The number of combinations of <code>k</code> interesting elements in <code>K</code> areas when there are <code>n</code> interesting elements in <code>N</code> areas.
	 */
	public static double NNKK(int N, int n, int K, int k) {
		return nCr(K, k) * nCr(N - K, n - k);
		// Does not do the last part:	/ RootAnalyze.nCr(N, n)
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
	
	/**
	 * Calculates the Binomial Coefficient
	 * 
	 * @see <a href="http://en.wikipedia.org/wiki/Binomial_coefficient">Binomial Coefficient on Wikipedia</a>
	 * 
	 * @param n number of elements you have
	 * @param r number of elements you want to pick
	 * @return number of combinations when you have <code>n</code> elements and want <code>r</code> of them
	 */
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

	/**
	 * Calculates the Binomial Coefficient
	 * 
	 * @see <a href="http://en.wikipedia.org/wiki/Binomial_coefficient">Binomial Coefficient on Wikipedia</a>
	 * 
	 * @param n number of elements you have
	 * @param r number of elements you want to pick
	 * @return number of combinations when you have <code>n</code> elements and want <code>r</code> of them
	 */
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

	/**
	 * You have x elements and want to pick a specific combination them which will contain y elements.
	 * 
	 * <p>For example, you have 5 elements and want 3 of them. There are 10 combinations for this. The exact combinations can be ordered as:
	 * <code>012, 013, 014, 023, 024, 034, 123, 124, 134, 234</code>. Combination number 4 is then 023, 
	 * so <code>specificCombination(5, 3, BigInteger.valueOf(4))</code> will return the array <code>{ 0, 2, 3 }</code></p>
	 * 
	 * @param elements number of elements you have
	 * @param size number of elements you want to pick
	 * @param combinationNumber the combination number you want to pick. <code>1 <= combinationNumber <= nCr(elements, size)</code>
	 * @return the specific elements that you picked, each element is <code>0 <= value < elements</code>
	 * @throws IllegalArgumentException if combinationNumber is out of range
	 * @throws IllegalArgumentException if elements or size is negative
	 */
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
		BigInteger ncr = nCrBigInt(remainingElements - 1, remainingSize - 1);
		
		while (remainingSize > 0) {
			if (ncr.signum() == 0) {
				throw new IllegalArgumentException("Combination out of range: " + combinationNumber + " with " + elements + " elements and size " + size);
			}
			if (combination.compareTo(ncr) <= 0) {
				result[resultIndex] = nextNumber;
				if (remainingElements > 1) {
					ncr = ncr.multiply(BigInteger.valueOf(remainingSize - 1)).divide(BigInteger.valueOf(remainingElements - 1));
				}
				remainingSize--;
				resultIndex++;
			}
			else {
				combination = combination.subtract(ncr);
				ncr = ncr.multiply(BigInteger.valueOf((remainingElements - 1) - (remainingSize - 1))).divide(BigInteger.valueOf(remainingElements - 1));
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
	
	/**
	 * You have x elements and want to pick a specific combination them which will contain y elements.
	 * 
	 * <p>For example, you have 5 elements and want 3 of them. There are 10 combinations for this. The exact combinations can be ordered as:
	 * <code>012, 013, 014, 023, 024, 034, 123, 124, 134, 234</code>. Combination number 4 is then 023, 
	 * so <code>specificCombination(5, 3, 4)</code> will return the array <code>{ 0, 2, 3 }</code></p>
	 * 
	 * @param elements number of elements you have
	 * @param size number of elements you want to pick
	 * @param combinationNumber the combination number you want to pick. <code>1 <= combinationNumber <= nCr(elements, size)</code>
	 * @return the specific elements that you picked, each element is <code>0 <= value < elements</code>
	 * @throws IllegalArgumentException if combinationNumber is out of range
	 * @throws IllegalArgumentException if elements or size is negative
	 */
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
