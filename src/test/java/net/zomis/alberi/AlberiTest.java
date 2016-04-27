package net.zomis.alberi;

import net.zomis.minesweeper.analyze.*;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertEquals;

public class AlberiTest {

    @Test
    public void simpleMap() {
        String map =
                "1112\n" +
                "1322\n" +
                "3342\n" +
                "3444";
        AnalyzeFactory<String> analyze = alberi(map, 1);
        for (RuleConstraint<String> rule : analyze.getRules()) {
            System.out.println(rule);
        }
        AnalyzeResult<String> result = analyze.solve();
        System.out.println(result.getSolutions());

        assertSolution(result, "10", "02", "23", "31");
    }

    @Test
    public void doubleTrees() {
        String map =
                "12223333\n" +
                "11214343\n" +
                "51114444\n" +
                "55554444\n" +
                "55554444\n" +
                "56657777\n" +
                "66667877\n" +
                "88888888";
        AnalyzeFactory<String> analyze = alberi(map, 2);
        AnalyzeResult<String> result = analyze.solve();

        for (FieldGroup<String> group : result.getGroups()) {
            if (group.getProbability() > 0) {
                System.out.println(group);
            }
        }

        assertSolution(result, "45", "47", "04", "06", "51", "53",
            "10", "12", "65", "67", "24", "26",
            "71", "73", "30", "32");
    }

    public static <T> void assertSolution(AnalyzeResult<T> result, T... trueFields) {
        Set<FieldGroup<T>> allGroups = new HashSet<FieldGroup<T>>(result.getGroups());
        Set<FieldGroup<T>> trueGroups = new HashSet<FieldGroup<T>>();
        for (T field : trueFields) {
            FieldGroup<T> group = result.getGroupFor(field);
            assertEquals("Unexpected probability for " + group, 1.0, group.getProbability(), 0.0001);
            trueGroups.add(group);
        }

        Set<FieldGroup<T>> falseGroups = new HashSet<FieldGroup<T>>(allGroups);
        falseGroups.removeAll(trueGroups);
        for (FieldGroup<T> group : falseGroups) {
            assertEquals("Unexpected probability for " + group, 0.0, group.getProbability(), 0.0001);
            trueGroups.add(group);
        }
    }

    @Test
    public void someMap() {
        String map =
                "1122233\n" +
                "1222223\n" +
                "4255667\n" +
                "4455667\n" +
                "4445557\n" +
                "4455577\n" +
                "4455577";
        AnalyzeFactory<String> analyze = alberi(map, 1);
        AnalyzeResult<String> result = analyze.solve();
        System.out.println(result.getSolutions());

        assertSolution(result, "12", "00", "61", "24", "36", "43", "55");
    }

    public AnalyzeFactory<String> alberi(String map, int count) {
        AnalyzeFactory<String> factory = new AnalyzeFactory<String>();
        String[] lines = map.split("\\n");
        Map<Character, List<String>> groups = new HashMap<Character, List<String>>();
        for (int y = 0; y < lines.length; y++) {
            String line = lines[y];
            if (line.length() != lines.length) {
                throw new IllegalArgumentException("Map format incorrect: " + map + " expected " +
                        lines.length + " columns in row " + y + " but was " + line.length());
            }
            List<String> row = new ArrayList<String>();
            for (int x = 0; x < line.length(); x++) {
                char ch = line.charAt(x);
                addToGroup(groups, ch, x, y);
                row.add(str(x, y));
            }
            factory.addRule(new FieldRule<String>(String.valueOf("row " + y), row, count));
        }

        for (int x = 0; x < lines.length; x++) {
            List<String> col = new ArrayList<String>();
            for (int y = 0; y < lines.length; y++) {
                col.add(str(x, y));
            }
            factory.addRule(new FieldRule<String>(String.valueOf("col " + x), col, count));
        }

        for (Map.Entry<Character, List<String>> ee : groups.entrySet()) {
            factory.addRule(new FieldRule<String>(String.valueOf(ee.getKey()), ee.getValue(), count));
        }

        factory.setListener(new AlberiListener());

        return factory;
    }

    public static String str(int x, int y) {
        int radix = Character.MAX_RADIX;
        return Integer.toString(x, radix) + Integer.toString(y, radix);
    }

    private void addToGroup(Map<Character, List<String>> groups, char ch, int x, int y) {
        List<String> list = groups.get(ch);
        if (list == null) {
            list = new ArrayList<String>();
            groups.put(ch, list);
        }

        list.add(str(x, y));
    }

}
