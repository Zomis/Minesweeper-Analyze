package net.zomis.minesweeper.analyze.listener;

import net.zomis.minesweeper.analyze.FieldGroup;
import net.zomis.minesweeper.analyze.GameAnalyze;

public interface RuleListener<T> {

    void onValueSet(FieldGroup<T> group, int value);

}
