package net.zomis.minesweeper.analyze;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;

public class AnalyzeTest { // implements NeighborFind<String> {

	private static final double	EPSILON	= 0.000000001;

	@Test
	public void aWholeLotOfConnectedThreesComplexMap() {
		RootAnalyzeImpl<String> root = new RootAnalyzeImpl<String>();
		List<String> openSea = addBoard(16, 16);
		root.addRule(placeThreeAt(1, 1, openSea));
		root.addRule(placeThreeAt(9, 1, openSea));
		root.addRule(placeThreeAt(13, 1, openSea));
		
		root.addRule(placeThreeAt(3, 3, openSea));
		root.addRule(placeThreeAt(7, 3, openSea));
		root.addRule(placeThreeAt(11, 3, openSea));
		
		root.addRule(placeThreeAt(5, 5, openSea));
		
		root.addRule(placeThreeAt(7, 7, openSea));
		root.addRule(placeThreeAt(11, 7, openSea));
		
		root.addRule(placeThreeAt(1, 9, openSea));
		root.addRule(placeThreeAt(9, 9, openSea));
		
		root.addRule(placeThreeAt(3, 11, openSea));
		root.addRule(placeThreeAt(7, 11, openSea));
		root.addRule(placeThreeAt(11, 11, openSea));
		
		root.addRule(placeThreeAt(1, 13, openSea));
		root.addRule(placeThreeAt(5, 13, openSea));
		root.addRule(placeThreeAt(13, 13, openSea));
		
		assertEquals(256 - 17, openSea.size());
		root.addRule(new FieldRule<String>("global", openSea, 51));
		
		long time = System.nanoTime();
		root.solve();
		long timeEnd = System.nanoTime();
		for (FieldGroup<String> ee : root.getGroups())
			System.out.println(ee + ": " + ee.getProbability());

		// 17 '3's, 16 connections between them, 1 group for the "open sea"
		assertEquals(17 + 1, root.getOriginalRules().size());
		assertEquals(17 + 16 + 1, root.getGroups().size());
		
		// Alright, I gotta admit... I have not calculated these numbers by hand to make sure they're correct!
		assertEquals(61440, root.getSolutions().size(), EPSILON);
		System.out.println(root.getSolutions().iterator().next());
		assertEquals(8.268912597471693e36, root.getTotal(), 1e25);
		assertEquals(0.08875309776194928, root.getGroupFor("ff").getProbability(), EPSILON);
		assertEquals(0.2707636258317718 , root.getGroupFor("23").getProbability(), EPSILON);
		assertEquals(0.26833148025906883, root.getGroupFor("80").getProbability(), EPSILON);
		long timeElapsedNanos = timeEnd - time;
		System.out.println("Solve took " + timeElapsedNanos / 1000000.0);
	}
	
	private String pos(int x, int y) {
		return Integer.toString(x, 16) + Integer.toString(y, 16);
	}
	
	private FieldRule<String> placeThreeAt(int x, int y, List<String> openSea) {
		List<String> fields = new ArrayList<String>();
		for (int xx = x - 1; xx <= x + 1; xx++) {
			for (int yy = y - 1; yy <= y + 1; yy++) {
				if (xx != x || yy != y) {
					fields.add(pos(xx, yy));
				}
			}
		}
		assertEquals(8, fields.size());
		openSea.remove(pos(x, y));
		
		return new FieldRule<String>(pos(x, y), fields, 3);
	}

	private List<String> addBoard(int width, int height) {
		List<String> pos = new ArrayList<String>(width * height);
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < width; y++) {
				pos.add(pos(x, y));
			}
		}
		return pos;
	}

	@Test
	public void pattern_14_withSameCharacters() {
//		abbc
//		a14c
//		abbc
//		dddd
		RootAnalyzeImpl<String> root = new RootAnalyzeImpl<String>();
		root.addRule(new FieldRule<String>("global", Arrays.asList("a", "b", "b", "c", "a", "c", "a", "b", "b", "c", "d", "d", "d", "d"), 6));
		root.addRule(new FieldRule<String>("1", Arrays.asList("a", "b", "b", "a", "a", "b", "b"), 1));
		root.addRule(new FieldRule<String>("4", Arrays.asList("b", "b", "c", "c", "b", "b", "c"), 4));
		root.solve();
		
		assertEquals(3, root.getOriginalRules().size());
		assertEquals(4, root.getGroups().size());
		assertEquals(1, root.getSolutions().size());
		
		assertEquals(3, root.getGroupFor("a").size(), EPSILON);
		assertEquals(4, root.getGroupFor("b").size(), EPSILON);
		assertEquals(3, root.getGroupFor("c").size(), EPSILON);
		assertEquals(4, root.getGroupFor("d").size(), EPSILON);
		
		assertEquals(0.0 , root.getGroupFor("a").getProbability(), EPSILON);
		assertEquals(0.25, root.getGroupFor("b").getProbability(), EPSILON);
		assertEquals(1.0 , root.getGroupFor("c").getProbability(), EPSILON);
		assertEquals(0.5 , root.getGroupFor("d").getProbability(), EPSILON);
		
		assertEquals(1, root.getSolutions().size());
		Solution<String> solution = root.getSolutions().iterator().next();
		GroupValues<String> values = solution.getSetGroupValues();
		assertEquals(0, (int) values.get(root.getGroupFor("a")));
		assertEquals(1, (int) values.get(root.getGroupFor("b")));
		assertEquals(3, (int) values.get(root.getGroupFor("c")));
		assertEquals(2, (int) values.get(root.getGroupFor("d")));
		
		assertEquals(4 * 6, root.getTotal(), EPSILON); // 4 for 'b', 1 for 'a' (no mines in a), 6 for 'd'
	}
	
	@Test
	public void pattern_13() {
//		abcd
//		e13f
//		ghij
//		klmn
		// Total of 6 mines
		
		/* Possible solutions:
		 * b+c+h+i = 0:			1
		 * 	a+e+g = 1			3
		 * 	d+f+j = 3			1
		 * k+l+m+n = 2			6
		 * 
		 * b+c+h+i = 1:			4
		 * 	a+e+g = 0			1
		 * 	d+f+j = 2			3
		 * k+l+m+n = 3			4
		 **/
		
		RootAnalyzeImpl<String> root = new RootAnalyzeImpl<String>();
		root.addRule(new FieldRule<String>("global", Arrays.asList("a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n"), 6));
		root.addRule(new FieldRule<String>("1", Arrays.asList("a", "b", "c", "e", "g", "h", "i"), 1));
		root.addRule(new FieldRule<String>("3", Arrays.asList("b", "c", "d", "f", "h", "i", "j"), 3));
		root.solve();
		
		assertEquals(3, root.getOriginalRules().size());
		assertEquals(4, root.getGroups().size());
		assertEquals(2, root.getSolutions().size()); // b+c+h+i can either be 0 or 1.
		
		assertEquals(Arrays.asList("a", "e", "g"), root.getGroupFor("a"));
		assertEquals(Arrays.asList("b", "c", "h", "i"), root.getGroupFor("b"));
		assertEquals(Arrays.asList("d", "f", "j"), root.getGroupFor("d"));
		assertEquals(Arrays.asList("k", "l", "m", "n"), root.getGroupFor("k"));

		assertEquals(2, root.getSolutions().size());
		assertEquals(0.7575757575757576, root.getGroupFor("d").getProbability(), EPSILON);
		assertEquals(0.6818181818181818, root.getGroupFor("k").getProbability(), EPSILON);
		assertEquals(0.0909090909090909, root.getGroupFor("a").getProbability(), EPSILON);
		assertEquals(0.1818181818181818, root.getGroupFor("b").getProbability(), EPSILON);
		
		Iterator<Solution<String>> solutions = root.getSolutions().iterator();
		Solution<String> solution = solutions.next();
		GroupValues<String> values = solution.getSetGroupValues();
		// Solution 1
		assertEquals(0, (int) values.get(root.getGroupFor("a")));
		assertEquals(1, (int) values.get(root.getGroupFor("b")));
		assertEquals(2, (int) values.get(root.getGroupFor("d")));
		assertEquals(3, (int) values.get(root.getGroupFor("k")));
		
		// Solution 2
		values = solutions.next().getSetGroupValues();
		assertEquals(1, (int) values.get(root.getGroupFor("a")));
		assertEquals(0, (int) values.get(root.getGroupFor("b")));
		assertEquals(3, (int) values.get(root.getGroupFor("d")));
		assertEquals(2, (int) values.get(root.getGroupFor("k")));
		
		assertEquals(3*6 + 4*3*4, root.getTotal(), EPSILON);
	}
	
	@Test
	public void equals() {
		RootAnalyzeImpl<String> analyze = new RootAnalyzeImpl<String>();
		analyze.addRule(new FieldRule<String>("sea", fields("ab"), 1));
		analyze.addRule(new FieldRule<String>("sea", Arrays.asList(new String("a"), "c"), 1));
		analyze.solve();
		
		System.out.println("---------");
		for (FieldRule<String> grp : analyze.getOriginalRules())
			System.out.println("Rule: " + grp);
		for (FieldGroup<String> grp : analyze.getGroups())
			System.out.println(grp + " = " + grp.getProbability());
		System.out.println("---------");
		
	}
	
	private Collection<String> fields(String string) {
		List<String> str = new ArrayList<String>();
		for (int i = 0; i < string.length(); i++)
			str.add(String.valueOf(string.charAt(i)));
		assertEquals(string.length(), str.size());
		return str;
	}

	private void createRule(RootAnalyzeImpl<String> root, String string) {
		String[] equalSplit = string.split(" = ");
		String ruleFields = equalSplit[0];
		ruleFields = ruleFields.substring(1, ruleFields.length() - 1);
		int ruleValue = Integer.parseInt(equalSplit[1]);
		String[] fields = ruleFields.split(" + ");
		FieldRule<String> rule = new FieldRule<String>("RuleStr", Arrays.asList(fields), ruleValue);
		root.addRule(rule);
		System.out.println(rule);
		assertEquals(string, rule.toString());
	}
	
}
