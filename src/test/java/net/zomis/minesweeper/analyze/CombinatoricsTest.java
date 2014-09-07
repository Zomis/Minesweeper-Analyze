package net.zomis.minesweeper.analyze;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;



public class CombinatoricsTest {

	@Test
	public void nCr() {
		assertEquals(6, Combinatorics.nCr(4, 2), 0.0001);
		assertEquals(2.034388346356e54, Combinatorics.nCr(256, 51), 1e44);
	}
	
	@Test
	public void ncrAndNcrCombinations() {
		int elements = 4;
		int size = 2;
		
		Integer[][] expected = new Integer[][] {
			new Integer[]{ 0, 1 }, new Integer[]{ 0, 2 }, new Integer[]{ 1, 2 },  
			new Integer[]{ 0, 3 }, new Integer[]{ 1, 3 }, new Integer[]{ 2, 3 }, 
		};
		
		
		double ncr = Combinatorics.nCr(elements, size);
		for (int i = 0; i < ncr; i++) {
			List<Integer> list = Combinatorics.indexCombinations(i, size, elements);
			assertArrayEquals("failed on " + i, expected[i], list.toArray(new Integer[list.size()]));
		}
	}
	
	@Test
	public void specificCombinationVeryBig() {
		int[] result = Combinatorics.specificCombination(256, 51, BigInteger.valueOf(Long.MAX_VALUE - 42));
		assertArrayEquals(new int[]{ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25,26,27, 28, 29, 30, 31, 32, 33,
				34, 35, 36, 37, 38, 52, 73, 94, 99, 132, 163, 169, 179, 190, 214, 227, 230 }, result);
	}
	
	@Test
	public void specificCombinations() {
		assertArrayEquals(new int[]{ 0, 1, 2, 3 }, Combinatorics.specificCombination(8, 4, BigInteger.ONE));
		assertArrayEquals(new int[]{ 0, 1, 2, 4 }, Combinatorics.specificCombination(8, 4, BigInteger.valueOf(2)));
		assertArrayEquals(new int[]{ 0, 2, 4, 5 }, Combinatorics.specificCombination(8, 4, BigInteger.valueOf(20)));
		
		assertArrayEquals(new int[]{ 1, 4, 6 }, Combinatorics.specificCombination(7, 3, BigInteger.valueOf(24)));
		assertArrayEquals(new int[]{ 1, 5, 6 }, Combinatorics.specificCombination(7, 3, BigInteger.valueOf(25)));
		assertArrayEquals(new int[]{ }, Combinatorics.specificCombination(7, 0, BigInteger.ONE));
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void specificCombinationOutOfRange() {
		Combinatorics.specificCombination(7, 3, BigInteger.valueOf(36));
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void specificCombinationZeroOrLess() {
		Combinatorics.specificCombination(7, 3, BigInteger.ZERO);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void specificCombinationWithNegativeSize() {
		Combinatorics.specificCombination(7, -1, BigInteger.ONE);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void specificCombinationWithTooBigSize() {
		Combinatorics.specificCombination(7, 8, BigInteger.ONE);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void specificCombinationWithNegativeElements() {
		Combinatorics.specificCombination(-1, 3, BigInteger.ONE);
	}
	
	@Test
	public void nCrBigInt() {
		assertEquals(BigInteger.valueOf(28), Combinatorics.nCrBigInt(8, 2));
		assertEquals(BigInteger.valueOf(28), Combinatorics.nCrBigInt(8, 6));
		assertEquals(BigInteger.valueOf(70), Combinatorics.nCrBigInt(8, 4));
		assertEquals(BigInteger.valueOf(56), Combinatorics.nCrBigInt(8, 3));
		assertEquals(BigInteger.valueOf(35), Combinatorics.nCrBigInt(7, 3));
		assertEquals(BigInteger.ZERO, Combinatorics.nCrBigInt(1, -1));
		assertEquals(BigInteger.ZERO, Combinatorics.nCrBigInt(0, 1));
		
		for (int i = 0; i < 100; i++) {
			assertEquals(BigInteger.ONE, Combinatorics.nCrBigInt(i, 0));
			assertEquals(BigInteger.ONE, Combinatorics.nCrBigInt(i, i));
		}
	}
	
	@Test
	public void pickCombinationsFromList() {
		List<FieldRule<Character>> rules = new LinkedList<FieldRule<Character>>();
		rules.add(new FieldRule<Character>(null, Arrays.asList('a', 'b'), 1));
		rules.add(new FieldRule<Character>(null, Arrays.asList('c', 'd', 'e'), 2));
		rules.add(new FieldRule<Character>(null, Arrays.asList('f', 'g'), 0));
		
		int ncr = 1;
		for (FieldRule<Character> fr : rules) {
			ncr *= fr.nCr();
		}
		
		assertEquals(6, ncr);
		assertEquals(Arrays.asList('a', 'c', 'd'), Combinatorics.multiListCombination(rules, 0));
		assertEquals(Arrays.asList('b', 'c', 'd'), Combinatorics.multiListCombination(rules, 1));
		assertEquals(Arrays.asList('a', 'c', 'e'), Combinatorics.multiListCombination(rules, 2));
		assertEquals(Arrays.asList('b', 'c', 'e'), Combinatorics.multiListCombination(rules, 3));
		assertEquals(Arrays.asList('a', 'd', 'e'), Combinatorics.multiListCombination(rules, 4));
		assertEquals(Arrays.asList('b', 'd', 'e'), Combinatorics.multiListCombination(rules, 5));
	}
}
