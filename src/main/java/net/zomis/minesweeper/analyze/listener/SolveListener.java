package net.zomis.minesweeper.analyze.listener;

import net.zomis.minesweeper.analyze.FieldGroup;

public interface SolveListener<T> {

    void onValueSet(Analyze<T> analyze, FieldGroup<T> group, int value);

}
