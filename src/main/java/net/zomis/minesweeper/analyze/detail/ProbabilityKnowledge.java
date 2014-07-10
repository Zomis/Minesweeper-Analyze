package net.zomis.minesweeper.analyze.detail;

import net.zomis.minesweeper.analyze.FieldGroup;
import net.zomis.minesweeper.analyze.GroupValues;


public interface ProbabilityKnowledge<Field> {

	double[] getProbabilities();
	int getFound();
	Field getField();
	double getMineProbability();
	FieldGroup<Field> getFieldGroup();
	GroupValues<Field> getNeighbors();
}
