package net.zomis.minesweeper.analyze;

import java.util.HashMap;

public class GroupValues<Field> extends HashMap<FieldGroup<Field>, Integer> {
	// TODO: Override hashcode and equals so that it is possible to buffer NnK values
	
	public GroupValues(GroupValues<Field> values) {
		super(values);
	}

	public GroupValues() {
		super();
	}

	private static final long	serialVersionUID	= -107328884258597555L;
	
	
	private int bufferedHash = 0;
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
