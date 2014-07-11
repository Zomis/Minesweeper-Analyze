package net.zomis.minesweeper.analyze.factory;

import static org.junit.Assert.*;

import java.util.List;

import net.zomis.minesweeper.analyze.AnalyzeResult;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class General2DAnalyzeTest {

	private final String input[] = new String[]{
		"#01!#",
		"0011#",
		"00___",
		"00_2_",
		"001!_",
	};
	
	@Rule
	public ExpectedException rule = ExpectedException.none();
	
	@Test
	public void invalidChar() {
		rule.expect(IllegalArgumentException.class);
		new General2DAnalyze(new String[]{ "X" });
	}
	
	@Test
	public void validChars() {
		new General2DAnalyze(new String[]{ "3" });
		new General2DAnalyze(new String[]{ "#" });
		new General2DAnalyze(new String[]{ "x" });
		new General2DAnalyze(new String[]{ "!" });
		new General2DAnalyze(new String[]{ "_" });
		new General2DAnalyze(new String[]{ "?" });
	}
	
	@Test
	public void testInput() {
		General2DAnalyze gen2d = new General2DAnalyze(input, -1);
		AnalyzeResult<CharPoint> analyze = gen2d.solve();
		
		assertEquals(2, analyze.getTotal(), 0.00001);
		assertEquals(-1, gen2d.getRemainingMinesCount());
		assertEquals(2, analyze.getGroups().size());
		assertEquals(2, analyze.getOriginalRules().size());
		
		System.out.println(analyze.getRules());
		
		assertEquals(0, analyze.getRules().size());
	}
	
	@Test
	public void wrongNeighborCount() {
		rule.expect(IllegalArgumentException.class);
		new General2DAnalyze(input, 4, new int[][]{ { 1, 2, 3 } });
	}
	
	@Test
	public void blockedFieldsAreClicked() {
		General2DAnalyze analyze = new General2DAnalyze(input);
		assertTrue(analyze.isClicked(new CharPoint(4, 2, '#')));
		assertFalse(analyze.isClicked(new CharPoint(4, 2, '_')));
	}
	
	@Test
	public void knownMineTest() {
		General2DAnalyze analyze = new General2DAnalyze(input);
		assertTrue(analyze.isDiscoveredMine(new CharPoint(4, 2, '!')));
		assertFalse(analyze.isDiscoveredMine(new CharPoint(4, 2, '#')));
		assertFalse(analyze.isDiscoveredMine(new CharPoint(4, 2, '4')));
		assertFalse(analyze.isDiscoveredMine(new CharPoint(4, 2, '_')));
	}
	
	@Test
	public void fieldHasRuleTest() {
		General2DAnalyze analyze = new General2DAnalyze(input);
		assertFalse(analyze.fieldHasRule(new CharPoint(4, 2, '_')));
		assertFalse(analyze.fieldHasRule(new CharPoint(4, 2, '#')));
		assertFalse(analyze.fieldHasRule(new CharPoint(4, 2, '!')));
		assertFalse(analyze.fieldHasRule(new CharPoint(4, 2, 'x')));
		assertFalse(analyze.fieldHasRule(new CharPoint(4, 2, '?')));
	}
	
	@Test
	public void neighborsTest() {
		General2DAnalyze analyze = new General2DAnalyze(input);
		List<CharPoint> neighbors = analyze.getNeighbors(analyze.getPoint(0, 0));
		assertEquals(3, neighbors.size());
		assertTrue(neighbors.contains(analyze.getPoint(0, 1)));
		assertTrue(neighbors.contains(analyze.getPoint(1, 0)));
		assertTrue(neighbors.contains(analyze.getPoint(1, 1)));
		assertFalse(neighbors.contains(analyze.getPoint(2, 2)));
		
		neighbors = analyze.getNeighbors(analyze.getPoint(4, 4));
		assertEquals(3, neighbors.size());
		assertTrue(neighbors.contains(analyze.getPoint(3, 4)));
		assertTrue(neighbors.contains(analyze.getPoint(4, 3)));
		assertTrue(neighbors.contains(analyze.getPoint(3, 3)));
		assertFalse(neighbors.contains(analyze.getPoint(2, 2)));
		
	}
	
}
