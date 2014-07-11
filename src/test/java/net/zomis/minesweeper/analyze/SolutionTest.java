package net.zomis.minesweeper.analyze;

import static org.junit.Assert.*;

import java.util.Random;

import net.zomis.minesweeper.analyze.factory.CharPoint;
import net.zomis.minesweeper.analyze.factory.General2DAnalyze;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class SolutionTest {

	private String[] input = new String[]{
		"_x__",
		"_13x",
		"___x",
		"_xxx",
	};
	
	private AnalyzeResult<CharPoint> analyze;
	private General2DAnalyze general2d;
	
	@Rule
	public ExpectedException expected = ExpectedException.none();
	
	@Before
	public void setup() {
		general2d = new General2DAnalyze(input);
		analyze = general2d.solve();
	}
	
	@Test
	public void solutionCount() {
		assertEquals(2, analyze.getSolutions().size());
	}
	
	@Test
	public void getCombinationMustBeIntegerValue() {
		Solution<CharPoint> solution = findWithCombinations(4 * 3 * 4);
		expected.expect(IllegalArgumentException.class);
		solution.getCombination(0.5);
	}
	
	@Test
	public void firstSolution() {
		Solution<CharPoint> solution = findWithCombinations(4 * 3 * 4);
		assertEquals(0, (int) solution.getSetGroupValues().get(analyze.getGroupFor(general2d.getPoint(0, 0))));
		assertEquals(1, (int) solution.getSetGroupValues().get(analyze.getGroupFor(general2d.getPoint(1, 0))));
		assertEquals(2, (int) solution.getSetGroupValues().get(analyze.getGroupFor(general2d.getPoint(3, 0))));
		assertEquals(3, (int) solution.getSetGroupValues().get(analyze.getGroupFor(general2d.getPoint(0, 3))));
		assertEquals(1 * 4 * 3 * 4, solution.getCombinations(), 0.0001);
		assertEquals(48.0 / 66.0, solution.getProbability(), 0.0001);
		
		assertEquals(6, solution.getCombination(0).size());
		assertEquals(6, solution.getRandomSolution(new Random(1L)).size());
	}
	
	private Solution<CharPoint> findWithCombinations(int i) {
		for (Solution<CharPoint> sol : analyze.getSolutions()) {
			if (sol.getCombinations() == i) {
				return sol;
			}
		}
		throw new NullPointerException("No solution found with " + i + " combinations");
	}

	@Test
	public void secondSolution() {
		Solution<CharPoint> solution = findWithCombinations(6 * 3);
		assertEquals(1, (int) solution.getSetGroupValues().get(analyze.getGroupFor(general2d.getPoint(0, 0))));
		assertEquals(0, (int) solution.getSetGroupValues().get(analyze.getGroupFor(general2d.getPoint(1, 0))));
		assertEquals(3, (int) solution.getSetGroupValues().get(analyze.getGroupFor(general2d.getPoint(3, 0))));
		assertEquals(2, (int) solution.getSetGroupValues().get(analyze.getGroupFor(general2d.getPoint(0, 3))));
		assertEquals(3 * 1 * 1 * 6, solution.getCombinations(), 0.0001);
		assertEquals(18.0 / 66.0, solution.getProbability(), 0.0001);
	}
	
}
