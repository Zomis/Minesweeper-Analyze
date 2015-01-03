package net.zomis.minesweeper.analyze.bounded;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import net.zomis.minesweeper.analyze.AnalyzeFactory;
import net.zomis.minesweeper.analyze.AnalyzeResult;
import net.zomis.minesweeper.analyze.BoundedFieldRule;
import net.zomis.minesweeper.analyze.FieldRule;
import net.zomis.minesweeper.analyze.Solution;

import org.junit.Test;

public class Binero {
	
	static void readLine(AnalyzeFactory<Integer> puzzle, int y, String line) {
		for (int x = 0; x < line.length(); x++) {
			char ch = line.charAt(x);
			if (ch == '1') {
				puzzle.addRule(positionValue(x, y, 1, line.length()));
			}
			else if (ch == '0') {
				puzzle.addRule(positionValue(x, y, 0, line.length()));
			}
		}
	}

	private static FieldRule<Integer> positionValue(int x, int y, int i, int size) {
		return new FieldRule<Integer>(pos(x, y, size), Arrays.asList(pos(x, y, size)), i);
	}

	static int readFromFile(InputStream file, AnalyzeFactory<Integer> analyze) throws IOException {
		BufferedReader bis = new BufferedReader(new InputStreamReader(file));
		String line = bis.readLine();
		int size = line.length();
		readLine(analyze, 0, line);
		for (int y = 1; y < size; y++) {
			line = bis.readLine();
			readLine(analyze, y, line);
		}
		bis.close();
		return size;
	}
	
	public static AnalyzeFactory<Integer> binero(InputStream file) throws IOException {
		AnalyzeFactory<Integer> fact = new AnalyzeFactory<Integer>();
		int length = readFromFile(file, fact);
		setupBinero(fact, length);
		return fact;
	}
	
	private static void setupBinero(AnalyzeFactory<Integer> fact, int size) {
		for (int x = 0; x < size; x++) {
			fact.addRule(new FieldRule<Integer>(null, createDiagonal(0, x, size, 1, 0), size / 2));
			fact.addRule(new FieldRule<Integer>(null, createDiagonal(x, 0, size, 0, 1), size / 2));
			
			sliding(fact, 0, x, size, 1, 0, 3);
			sliding(fact, x, 0, size, 0, 1, 3);
		}
		
	}

	private static void sliding(AnalyzeFactory<Integer> puzzle, int x, int y, int size, int offsetX, int offsetY, int count) {
		LinkedList<Integer> fields = new LinkedList<Integer>();
		while (x < size && y < size && x >= 0 && y >= 0) {
			fields.addLast(pos(x, y, size));
			x += offsetX;
			y += offsetY;
			if (fields.size() >= 3) {
				puzzle.addRule(new BoundedFieldRule<Integer>(null, new ArrayList<Integer>(fields), 1, 2));
				fields.removeFirst();
			}
		}
	}

	private static Collection<Integer> createDiagonal(int x, int y, int size, int offsetX, int offsetY) {
		List<Integer> fields = new ArrayList<Integer>();
		while (x < size && y < size && x >= 0 && y >= 0) {
			fields.add(pos(x, y, size));
			y += offsetY;
			x += offsetX;
		}
		return fields;
	}

	@Test
	public void hard() throws Exception {
		AnalyzeFactory<Integer> puzzle = binero(getClass().getResourceAsStream("simple"));
		AnalyzeResult<Integer> solved = puzzle.solve();
		System.out.println(solved.getTotal());
		for (Solution<Integer> ee : solved.getSolutions()) {
			System.out.println(ee);
		}
	}
	
	@Test
	public void veryHard() throws Exception {
		AnalyzeFactory<Integer> puzzle = binero(getClass().getResourceAsStream("veryhard"));
		AnalyzeResult<Integer> solved = puzzle.solve();
		System.out.println(solved.getTotal());
		for (Solution<Integer> ee : solved.getSolutions()) {
			System.out.println(ee);
		}
	}
	
	private static Integer pos(int x, int y, int size) {
		return y * size + x;
	}
}
