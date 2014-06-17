package net.zomis.minesweeper.analyze;

import java.util.HashMap;

public class GroupValues<T> extends HashMap<FieldGroup<T>, Integer> {
	private static final long	serialVersionUID	= -107328884258597555L;
	private int bufferedHash = 0;
	
	public GroupValues(GroupValues<T> values) {
		super(values);
	}

	public GroupValues() {
		super();
	}

	@Override
	public int hashCode() {
		if (bufferedHash != 0) return this.bufferedHash;
		
		int result = super.hashCode();
		System.out.println("Hash buffered: " + result);
		this.bufferedHash = result;
		return result;
	}
	
	public int calculateHash() {
		this.bufferedHash = 0;
		return this.hashCode();
	}

}
