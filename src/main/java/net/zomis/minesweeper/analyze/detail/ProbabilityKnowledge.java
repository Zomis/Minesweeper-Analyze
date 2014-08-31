package net.zomis.minesweeper.analyze.detail;

import net.zomis.minesweeper.analyze.FieldGroup;
import net.zomis.minesweeper.analyze.GroupValues;


public interface ProbabilityKnowledge<T> {

	/**
	 * @return The field that this object has stored the probabilities for
	 */
	T getField();
	
	/**
	 * @return The {@link FieldGroup} for the field returned by {@link #getField()}
	 */
	FieldGroup<T> getFieldGroup();
	
	/**
	 * @return How many mines has already been found for this field
	 */
	int getFound();
	
	/**
	 * @return The mine probability for the {@link FieldGroup} returned by {@link #getFieldGroup()}
	 */
	double getMineProbability();
	
	/**
	 * @return {@link GroupValues} object for what neighbors the field returned by {@link #getField()} has
	 */
	GroupValues<T> getNeighbors();
	
	/**
	 * @return The array of the probabilities for what number this field has. The sum of this array + the value of {@link #getMineProbability()} will be 1.
	 */
	double[] getProbabilities();
	
}