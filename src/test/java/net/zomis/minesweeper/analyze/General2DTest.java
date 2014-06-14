package net.zomis.minesweeper.analyze;

import static org.junit.Assert.*;
import net.zomis.minesweeper.analyze.factory.CharPoint;
import net.zomis.minesweeper.analyze.factory.General2DAnalyze;

import org.junit.Test;

public class General2DTest {
	
	@Test
	public void simpleMap() {
		String[] input = {
				"___x",
				"_13x",
				"__x_",
				"_xxx",
		};
		General2DAnalyze analyze = new General2DAnalyze(input);
		analyze.solve();
		
		for (FieldRule<CharPoint> ee : analyze.getRules()) {
			System.out.println(ee);
		}
		for (Solution<CharPoint> ee : analyze.getSolutions()) {
			System.out.println(ee);
		}
		
		
		assertEquals(3*6 + 4*3*4, analyze.getTotal(), 0.000001);
	}

	@Test
	public void time() {
		for (int i = 0; i < 1000; i++) {
			rolfl1();
		}
		long time = System.nanoTime();
		General2DAnalyze analyze = rolfl1();
		long timeEnd = System.nanoTime();
		System.out.println((timeEnd - time) / 1000000.0);
		System.out.println(analyze);
	}
	
	String[] input = {
			"___x",
			"_2_x",
			"_xx_",
			"__2_",
			"x___",
	};
	public General2DAnalyze rolfl1() {
		General2DAnalyze analyze = new General2DAnalyze(input);
		analyze.solve();
		return analyze;
	}

}
