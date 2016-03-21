package net.zomis.minesweeper.analyze.listener;

import net.zomis.minesweeper.analyze.FieldGroup;
import net.zomis.minesweeper.analyze.GameAnalyze;

public interface SolveListener<T> {

    void onValueSet(GameAnalyze<T> analyze, FieldGroup<T> group, int value);

}
