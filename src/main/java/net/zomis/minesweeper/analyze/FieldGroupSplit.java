package net.zomis.minesweeper.analyze;

import java.util.Collections;

public class FieldGroupSplit<T> {

	private final FieldGroup<T>	onlyA;
	private final FieldGroup<T>	both;
	private final FieldGroup<T>	onlyB;

	public FieldGroupSplit(FieldGroup<T> onlyA, FieldGroup<T> both, FieldGroup<T> onlyB) {
		this.onlyA = onlyA;
		this.onlyB = onlyB;
		this.both = both;
	}

	public FieldGroup<T> getBoth() {
		return both;
	}
	
	public FieldGroup<T> getOnlyA() {
		return onlyA;
	}
	
	public FieldGroup<T> getOnlyB() {
		return onlyB;
	}
	
	public boolean splitPerformed() {
		return !onlyA.isEmpty() || !onlyB.isEmpty();
	}
	
	@Override
	public String toString() {
		return "FieldGroupSplit:" + onlyA + " -- " + both + " -- " + onlyB;
	}

	public static <T> FieldGroupSplit<T> split(FieldGroup<T> a, FieldGroup<T> b) {
		if (a == b)
			return null;
		
		if (Collections.disjoint(a, b)) 
			return null; // Return if the groups have no fields in common
		
		FieldGroup<T> both = new FieldGroup<T>(a);
		FieldGroup<T> onlyA = new FieldGroup<T>(a);
		FieldGroup<T> onlyB = new FieldGroup<T>(b);
		both.retainAll(b);
		onlyA.removeAll(both);
		onlyB.removeAll(both);

		// Check if ALL fields are in common
		if (onlyA.isEmpty() && onlyB.isEmpty()) {
			// If this is called in a loop an inf-loop can occur if we don't do this because we're creating a NEW object all the time to hold them both.
			// We should reuse one of the existing ones and go back to using == above.
			both = a;
		}
		
		return new FieldGroupSplit<T>(onlyA, both, onlyB);
	}

}
