package net.zomis.minesweeper.analyze.factory;


public class CharPoint {

	private final int x;
	private final int y;
	private final char value;

	public CharPoint(int x, int y, char value) {
		this.x = x;
		this.y = y;
		this.value = value;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
	
    @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + x;
		result = prime * result + y;
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof CharPoint))
			return false;
		CharPoint other = (CharPoint) obj;
		return x == other.getX() && y == other.getY() && other.value == value;
	}
	
    @Override
    public String toString() {
        return "(" + x + ", " + y + " '" + value + "')";
    }
    
    public char getValue() {
		return value;
	}
}
