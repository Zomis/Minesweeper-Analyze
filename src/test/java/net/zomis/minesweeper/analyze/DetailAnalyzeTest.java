package net.zomis.minesweeper.analyze;

import static org.junit.Assert.*;
import net.zomis.minesweeper.analyze.detail.DetailedResults;
import net.zomis.minesweeper.analyze.detail.FieldProxy;
import net.zomis.minesweeper.analyze.detail.ProbabilityKnowledge;
import net.zomis.minesweeper.analyze.factory.CharPoint;
import net.zomis.minesweeper.analyze.factory.General2DAnalyze;

import org.junit.Test;

public class DetailAnalyzeTest {

	@Test
	public void sf() {
		String[] input = {
				"___",// aaa   abc 
				"_x_",// bcd   ded
				"2_3",// 2c3   2f3
				"_xx",// bcd   ded
				"xx_" // aaa   abc
		};
		General2DAnalyze analyze = new General2DAnalyze(input);
		AnalyzeResult<CharPoint> results = analyze.solve();
		
		DetailedResults<CharPoint> detail = results.analyzeDetailed(analyze);
		
		assertEquals(6, detail.getProxyCount());
		
		for (ProbabilityKnowledge<CharPoint> ee : detail.getProxies()) {
			System.out.println(ee);
			System.out.println();
		}
		
		FieldProxy<CharPoint> field12 = detail.getProxyFor(analyze.getPoint(1, 2));
		FieldProxy<CharPoint> field20 = detail.getProxyFor(analyze.getPoint(2, 0));
		FieldProxy<CharPoint> field03 = detail.getProxyFor(analyze.getPoint(0, 3));
		assertArrayEquals(new double[]{0.047619047619047616, 0.30158730158730157, 0.2857142857142857, 0.07936507936507936}, field20.getProbabilities(), 0.0000001);
		assertArrayEquals(new double[]{0.031746031746031744, 0.2698412698412698, 0.38095238095238093, 0.15873015873015872, 0.015873015873015872, 0.0}, field03.getProbabilities(), 0.0000001);
		assertArrayEquals(new double[]{0.0, 0.0, 0.0, 0.23809523809523808, 0.19047619047619047, 0.0, 0.0, 0.0, 0.0}, field12.getProbabilities(), 0.0000001);
		
	}
	
}
