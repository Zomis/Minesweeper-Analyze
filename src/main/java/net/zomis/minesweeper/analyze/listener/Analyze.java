package net.zomis.minesweeper.analyze.listener;

import net.zomis.minesweeper.analyze.GroupValues;
import net.zomis.minesweeper.analyze.RuleConstraint;

public interface Analyze<T> {

    int getDepth();
    void addRule(RuleConstraint<T> stringFieldRule);
    GroupValues<T> getKnownValues();

}
