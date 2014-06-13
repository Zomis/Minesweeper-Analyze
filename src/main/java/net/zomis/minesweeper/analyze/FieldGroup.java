package net.zomis.minesweeper.analyze;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class FieldGroup<Field> extends ArrayList<Field> {
	private static final long serialVersionUID = 4172065050118874050L;
	
	private double probability = 0;
	private int solutionsKnown = 0;
	
	void informAboutSolution(Integer rValue, Solution<Field> solution, double total) {
		if (rValue == 0) return;
		this.probability = this.probability + solution.nCr() / total * rValue / this.size();
		this.solutionsKnown++;
	}
	
	public int getSolutionsKnown() {
		return this.solutionsKnown;
	}
	
	public double getProbability() {
		return this.probability;
	}

	public FieldGroup(Collection<Field> fields) {
		super(fields);
	}
	
	public String toString() {
		if (this.size() > 8) {
			if (this.probability == 0) 
				return "(" + this.size() + " NO_MINES)";
//			return "(" + this.size() + " DEEP SEA: " + this.probability + ")";
			return "(" + this.size() + " DEEP SEA)";
		}
		
		StringBuilder str = new StringBuilder();
		for (Field field : this) {
			if (str.length() > 0)
				str.append(" + ");
			str.append(field);// field.getCoordinate();
		}
		
		return
//				Integer.toString(this.hashCode(), Character.MAX_RADIX) + 
				"(" + str.toString() + ")";
	}
	
	
	List<FieldGroup<Field>> splitCheck(FieldGroup<Field> group) {
		if (this == group) return null;
		
		if (Collections.disjoint(this, group)) return null; // Return if the groups have no fields in common
		
		FieldGroup<Field> both = new FieldGroup<Field>(this);
		FieldGroup<Field> onlyA = new FieldGroup<Field>(this);
		FieldGroup<Field> onlyB = new FieldGroup<Field>(group);
		both.retainAll(group);
		onlyA.removeAll(both);
		onlyB.removeAll(both);

		List<FieldGroup<Field>> a = new ArrayList<FieldGroup<Field>>(3);
		a.add(onlyA);
		if (onlyA.isEmpty() && onlyB.isEmpty()) {
			// inf-loop occoured because we're creating a NEW object all the time to hold them both. We should reuse one of the existing ones and go back to using == above.
			a.add(this);
		}
		else a.add(both);
		a.add(onlyB);
		
		return a;
	}
	void resetTotal() {
		this.probability = 0;
		this.solutionsKnown = 0;
	}

}
