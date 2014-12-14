package net.zomis.minesweeper.analyze;

import net.zomis.minesweeper.analyze.detail.DetailedResults;
import net.zomis.minesweeper.analyze.detail.ProbabilityKnowledge;
import net.zomis.minesweeper.analyze.factory.CharPoint;
import net.zomis.minesweeper.analyze.factory.General2DAnalyze;

import org.junit.Test;

import com.janosgyerik.microbench.api.BenchmarkRunner;
import com.janosgyerik.microbench.api.annotation.MeasureTime;

public class SpeedTest {
	
	private AnalyzeResult<String> result;
	
	@MeasureTime(warmUpIterations = 0, iterations = 1)
	public void complexMap() throws Exception {
		String[] input = {
				"________________",
				"_3x______3x_x3__",
				"_xx_____x_x_xx__",
				"___3x__3x_x3____",
				"____x_x_________",
				"_____3__________",
				"______x_________",
				"______x3___3x___",
				"_x______x_x_x___",
				"_3x______3______",
				"__x_______x_x___",
				"___3__x3x__3____",
				"_xx_x_x_____x___",
				"_3x_x3______x3__",
				"____________x___",
				"xxxxxxxxxxxxxxx_"
		};
		General2DAnalyze analyze = new General2DAnalyze(input);
		AnalyzeResult<CharPoint> results = analyze.solve();
		DetailedResults<CharPoint> detail = results.analyzeDetailed(analyze);
		System.out.println(detail);
	}
	
//	@MeasureTime(warmUpIterations = 0, iterations = 1)
	public void simpler() throws Exception {
		String[] input = {
				"________________",
				"_3x______3x_x3__",
				"_xx_____x_x_xx__",
				"___3x__3x_x3____",
				"____x_x_________",
				"_____3__________",
				"______x_________",
				"______x____3x___",
				"_x______x_x_x___",
				"_3x_____________",
				"__x_______x_x___",
				"___3__x_x_______",
				"_xx_x_x_____x___",
				"_3x_x3______x___",
				"____________x___",
				"xxxxxxxxxxxxxxx_"
		};
		General2DAnalyze analyze = new General2DAnalyze(input);
		AnalyzeResult<CharPoint> results = analyze.solve();
		DetailedResults<CharPoint> detail = results.analyzeDetailed(analyze);
		System.out.println(detail);
		
		for (ProbabilityKnowledge<CharPoint> ee : detail.getProxies()) {
			System.out.println(ee);
			System.out.println();
		}

	}
	
	@Test
	public void testName() throws Exception {
		new BenchmarkRunner(this).run();
	}
	
}
