package net.zomis.alberi;

import net.zomis.minesweeper.analyze.FieldGroup;
import net.zomis.minesweeper.analyze.FieldRule;
import net.zomis.minesweeper.analyze.listener.Analyze;
import net.zomis.minesweeper.analyze.listener.SolveListener;

import java.util.ArrayList;
import java.util.List;

public class AlberiListener implements SolveListener<String> {

    @Override
    public void onValueSet(Analyze<String> analyze,
           FieldGroup<String> group, int value) {
        System.out.println("onValueSet for depth " + analyze.getDepth() + ": " + group + " = " + value);
        int radix = Character.MAX_RADIX;
        if (value > 0) {
            for (String str : group) {
                int x = Integer.parseInt(String.valueOf(str.charAt(0)), radix);
                int y = Integer.parseInt(String.valueOf(str.charAt(1)), radix);
                List<String> fields = new ArrayList<String>();
                for (int xx = x - 1; xx <= x + 1; xx++) {
                    for (int yy = y - 1; yy <= y + 1; yy++) {
                        if (xx == x && yy == y) {
                            continue;
                        }
                        String field = AlberiTest.str(xx, yy);
                        fields.add(field);
                    }
                }
                FieldRule<String> rule = new FieldRule<String>(str, fields, 0);
                analyze.addRule(rule);
                System.out.println("Adding rule dynamically: " + rule + " to depth " + analyze.getDepth());
            }
        }
    }

}
