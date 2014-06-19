package net.zomis.minesweeper.analyze;

import static org.junit.Assert.*;

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
