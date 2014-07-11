package net.zomis.minesweeper.analyze;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.zomis.minesweeper.analyze.factory.CharPoint;
import net.zomis.minesweeper.analyze.factory.General2DAnalyze;

import org.junit.Test;

public class RootAnalyzeImplTest {

	@Test
	public void noGroups() {
		AnalyzeResult<CharPoint> analyze = new General2DAnalyze(new String[]{ "#" }, 1).solve();
		
		assertNull(analyze.getGroupFor(new CharPoint(0, 0, 'x')));
		assertEquals(0, analyze.getGroups().size());
	}
	
	@Test
	public void test() {
		General2DAnalyze analyze = new General2DAnalyze(new String[]{
			"___",
			"_1_",
			"_x_"
		});
		AnalyzeResult<CharPoint> solution = analyze.solve();
		List<FieldRule<CharPoint>> extraRules = new ArrayList<FieldRule<CharPoint>>();
		extraRules.add(new FieldRule<CharPoint>(null, Arrays.asList(analyze.getPoint(0, 0), analyze.getPoint(1, 0)), 1));
		assertEquals(0.25, solution.getProbabilityOf(extraRules), 0.0001);
		assertFalse(false);
	}
	
	
}
