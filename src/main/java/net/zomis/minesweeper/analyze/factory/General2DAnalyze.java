package net.zomis.minesweeper.analyze.factory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class General2DAnalyze extends AbstractAnalyze<CharPoint> {

	public static final char[] UNCLICKED = { '_', '?' };
	public static final char HIDDEN_MINE = 'x';
	public static final char KNOWN_MINE = '!';
	public static final char BLOCKED = '#';
	
	
	private static final int[][]	DEFAULT_NEIGHBORS	= new int[][]{
		{ -1, -1 }, { 0, -1 }, {  1, -1 }, 
		{ -1,  0 },			   {  1,  0 },
		{ -1,  1 }, { 0,  1 }, {  1,  1 } };
	
	private final int[][]	neighbors;
	private final CharPoint[][] points;
	private final int width;
	private final int height;
	private final int hiddenMines;

	public General2DAnalyze(String[] map) {
		this(map, 0);
	}
	
	public General2DAnalyze(String[] map, int hiddenMines) {
		this(map, hiddenMines, DEFAULT_NEIGHBORS);
	}
	
	public General2DAnalyze(String[] map, int hiddenMines, int[][] neighbors) {
		for (int[] neighbor : neighbors) {
			if (neighbor.length != 2)
				throw new IllegalArgumentException("Neighbor array must be an array of int[2] (x, y) pair. Unexpected length " + neighbor.length + " for " + Arrays.toString(neighbor));
		}
		this.width = map[0].length();
		this.height = map.length;
		this.points = new CharPoint[width][height];
		
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				char ch = map[y].charAt(x);
				points[x][y] = new CharPoint(x, y, ch);
				if (ch == HIDDEN_MINE) {
					hiddenMines++;
				}
			}
		}
		
		this.hiddenMines = hiddenMines;
		this.neighbors = neighbors;
		this.createRules(getAllPoints());
	}

	@Override
	protected List<CharPoint> getAllPoints() {
		List<CharPoint> point = new ArrayList<CharPoint>();
		for (CharPoint[] ps : points) {
			for (CharPoint p : ps) {
				point.add(p);
			}
		}
		return point;
	}

	@Override
	protected boolean fieldHasRule(CharPoint field) {
		return !isBlocked(field) && isClicked(field) && !isDiscoveredMine(field);
	}

	private boolean isBlocked(CharPoint field) {
		return field.getValue() == BLOCKED;
	}

	@Override
	protected int getRemainingMinesCount() {
		return hiddenMines;
	}

	@Override
	protected List<CharPoint> getAllUnclickedFields() {
		List<CharPoint> point = new ArrayList<CharPoint>();
		for (CharPoint[] ps : points) {
			for (CharPoint p : ps) {
				if (!isClicked(p))
					point.add(p);
			}
		}
		return point;
	}

	@Override
	protected boolean isDiscoveredMine(CharPoint neighbor) {
		return neighbor.getValue() == KNOWN_MINE;
	}

	@Override
	protected int getFieldValue(CharPoint field) {
		return Character.digit(field.getValue(), 10);
	}

	@Override
	protected List<CharPoint> getNeighbors(CharPoint field) {
		List<CharPoint> neighbors = new ArrayList<CharPoint>(this.neighbors.length);
		int x = field.getX();
		int y = field.getY();
		for (int xx = x - 1; xx <= x + 1; xx++) {
			for (int yy = y - 1; yy <= y + 1; yy++) {
				if (xx == x && yy == y)
					continue;
				if (xx < 0 || yy < 0)
					continue;
				if (xx >= width || yy >= height)
					continue;
				neighbors.add(this.points[xx][yy]);
			}
		}
		
		return neighbors;
	}

	@Override
	protected boolean isClicked(CharPoint neighbor) {
		if (isBlocked(neighbor))
			return true;
		return !isUnclickedChar(neighbor.getValue()) && neighbor.getValue() != HIDDEN_MINE;
	}

	private boolean isUnclickedChar(char value) {
		return isInArray(value, UNCLICKED);
	}

	private boolean isInArray(char value, char[] array) {
		for (char ch : array) {
			if (ch == value)
				return true;
		}
		return false;
	}

	public CharPoint getPoint(int x, int y) {
		return this.points[x][y];
	}
	
}
