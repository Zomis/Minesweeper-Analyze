package net.zomis.minesweeper.analyze;

import java.util.ArrayList;
import java.util.Collection;

public class FieldGroup<T> extends ArrayList<T> {
	private static final long serialVersionUID = 4172065050118874050L;
	
	private double probability = 0;
	private int solutionsKnown = 0;
	
	void informAboutSolution(int rValue, Solution<T> solution, double total) {
		if (rValue == 0)
			return;
		this.probability = this.probability + solution.nCr() / total * rValue / this.size();
		this.solutionsKnown++;
	}
	
	public int getSolutionsKnown() {
		return this.solutionsKnown;
	}
	
	public double getProbability() {
		return this.probability;
	}

	public FieldGroup(Collection<T> fields) {
		super(fields);
	}
	
	public String toString() {
		if (this.size() > 8) {
			return "(" + this.size() + " FIELDS)";
		}
		
		StringBuilder str = new StringBuilder();
		for (T field : this) {
			if (str.length() > 0)
				str.append(" + ");
			str.append(field);
		}
		return "(" + str.toString() + ")";
	}
	
}
