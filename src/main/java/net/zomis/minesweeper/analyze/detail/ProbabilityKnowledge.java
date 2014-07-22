package net.zomis.minesweeper.analyze.detail;

import net.zomis.minesweeper.analyze.FieldGroup;
import net.zomis.minesweeper.analyze.GroupValues;


public interface ProbabilityKnowledge<T> {

	T getField();
	FieldGroup<T> getFieldGroup();
	int getFound();
	double getMineProbability();
	GroupValues<T> getNeighbors();
	double[] getProbabilities();
	
}