package net.zomis.minesweeper.analyze.bounded;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.zomis.minesweeper.analyze.AnalyzeFactory;
import net.zomis.minesweeper.analyze.AnalyzeResult;
import net.zomis.minesweeper.analyze.BoundedFieldRule;
import net.zomis.minesweeper.analyze.FieldRule;
import net.zomis.minesweeper.analyze.Solution;

import org.junit.Test;

public class NQueens {

	@Test
	public void rules() throws Exception {
		AnalyzeFactory<Integer> queens = createQueens(3);
		assertEquals(16, queens.getRules().size());
	}
	
	@Test
	public void fourQueens() throws Exception {
		AnalyzeFactory<Integer> queens = createQueens(4);
		System.out.println(queens);
		for (FieldRule<Integer> rule : queens.getRules()) {
			System.out.println(rule);
		}
		assertEquals(22, queens.getRules().size());
		
		AnalyzeResult<Integer> solve = queens.solve();
		
		for (Solution<Integer> sol : solve.getSolutions()) {
			System.out.println(sol);
		}
		assertEquals(2, solve.getTotal(), 0.1);
	}
	
	@Test
	public void eightQueens() throws Exception {
		AnalyzeFactory<Integer> queens = createQueens(8);
		AnalyzeResult<Integer> solutions = queens.solve();
		assertEquals(92, solutions.getTotal(), 0.001);
	}

	private AnalyzeFactory<Integer> createQueens(int size) {
		AnalyzeFactory<Integer> analyze = new AnalyzeFactory<Integer>();
		
		for (int x = 0; x < size; x++) {
			// Diagonal from top to bottom-right
			analyze.addRule(new BoundedFieldRule<Integer>(0, createDiagonal(x, 0, size, 1, 1), 0, 1));
			if (x != 0) {
				// Diagonals from left to bottom-right
				analyze.addRule(new BoundedFieldRule<Integer>(0, createDiagonal(0, x, size, 1, 1), 0, 1));
			}
			
			// Diagonals from top to left-bottom
			analyze.addRule(new BoundedFieldRule<Integer>(0, createDiagonal(x, 0, size, -1, 1), 0, 1));
			if (x != 0) {
				// Diagonals from right to left-bottom
				analyze.addRule(new BoundedFieldRule<Integer>(0, createDiagonal(size - 1, x, size, -1, 1), 0, 1));
			}
		}
		
		for (int x = 0; x < size; x++) {
			List<Integer> columnFields = new ArrayList<Integer>();
			List<Integer> rowFields = new ArrayList<Integer>();
			for (int y = 0; y < size; y++) {
				columnFields.add(pos(x, y, size));
				rowFields.add(pos(y, x, size));
			}
			analyze.addRule(new FieldRule<Integer>(0, columnFields, 1));
			analyze.addRule(new FieldRule<Integer>(0, rowFields, 1));
		}
		
		return analyze;
	}

	private Collection<Integer> createDiagonal(int x, int y, int size, int offsetX, int offsetY) {
		List<Integer> fields = new ArrayList<Integer>();
		while (x < size && y < size && x >= 0 && y >= 0) {
			fields.add(pos(x, y, size));
			y += offsetY;
			x += offsetX;
		}
		return fields;
	}

	private Integer pos(int x, int y, int size) {
		return y * size + x;
	}
	
}
