package net.zomis.minesweeper.analyze.detail;

import net.zomis.minesweeper.analyze.FieldGroup;
import net.zomis.minesweeper.analyze.GroupValues;


public interface ProbabilityKnowledge<Field> {

	Field getField();
	FieldGroup<Field> getFieldGroup();
	int getFound();
	double getMineProbability();
	GroupValues<Field> getNeighbors();
	double[] getProbabilities();
}
