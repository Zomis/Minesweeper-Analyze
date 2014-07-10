package net.zomis.minesweeper.analyze;

import java.util.ArrayList;
import java.util.Collection;

/**
 * A group of fields that have common rules
 * 
 * @author Simon Forsberg
 * @param <T> The field type
 */
public class FieldGroup<T> extends ArrayList<T> {
	// TODO: Use composition over inheritance. Perhaps switch to using `HashSet` (even though it will be less flexible)
	private static final long serialVersionUID = 4172065050118874050L;
	
	private double probability = 0;
	private int solutionsKnown = 0;
	
	public FieldGroup(Collection<T> fields) {
		super(fields);
	}
	FieldGroup(int size) {
		super(size);
	}
	
	public double getProbability() {
		return this.probability;
	}
	
	public int getSolutionsKnown() {
		return this.solutionsKnown;
	}

	void informAboutSolution(int minesForGroup, Solution<T> solution, double total) {
		if (minesForGroup == 0) {
			return;
		}
		this.probability = this.probability + solution.nCr() / total * minesForGroup / this.size();
		this.solutionsKnown++;
	}
	
	public String toString() {
		if (this.size() > 8) {
			return "(" + this.size() + " FIELDS)";
		}
		
		StringBuilder str = new StringBuilder();
		for (T field : this) {
			if (str.length() > 0) {
				str.append(" + ");
			}
			str.append(field);
		}
		return "(" + str.toString() + ")";
	}
	
}
