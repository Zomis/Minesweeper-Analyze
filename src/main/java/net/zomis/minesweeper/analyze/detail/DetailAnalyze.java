package net.zomis.minesweeper.analyze.detail;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.zomis.minesweeper.analyze.AnalyzeResult;
import net.zomis.minesweeper.analyze.FieldGroup;
import net.zomis.minesweeper.analyze.GroupValues;
import net.zomis.minesweeper.analyze.Solution;

public class DetailAnalyze<T> {
	public static <T> DetailedResults<T> solveDetailed(AnalyzeResult<T> analyze, NeighborFind<T> neighborStrategy) {
		final Map<T, FieldProxy<T>> proxies = new HashMap<T, FieldProxy<T>>();
		
		for (FieldGroup<T> group : analyze.getGroups()) {
			for (T field : group) {
				FieldProxy<T> proxy = new FieldProxy<T>(group, field);
				proxies.put(field, proxy);
			}
		}
		
		for (FieldProxy<T> fieldProxy : proxies.values()) {
			fieldProxy.fixNeighbors(neighborStrategy, new ProxyProvider<T>() {
				@Override
				public FieldProxy<T> getProxyFor(T field) {
					return proxies.get(field);
				}
			});
		}
		
		double ncrtotal = analyze.getTotal();
		Map<GroupValues<T>, FieldProxy<T>> bufferedValues = new HashMap<GroupValues<T>, FieldProxy<T>>();
		for (Entry<T, FieldProxy<T>> ee : proxies.entrySet()) {
			FieldProxy<T> mappedValue = bufferedValues.get(ee.getValue().getNeighbors());
			if (mappedValue != null && mappedValue.getFieldGroup() == ee.getValue().getFieldGroup()) {
				ee.getValue().copyFromOther(mappedValue, ncrtotal);
			}
			else {
				for (Solution<T> solution : analyze.getSolutionIteration()) {
					ee.getValue().addSolution(solution);
				}
				ee.getValue().finalCalculation(ncrtotal);
				bufferedValues.put(ee.getValue().getNeighbors(), ee.getValue());
			}
		}
		
		int proxyCount = bufferedValues.size();
		
		return new DetailedResultsImpl<T>(analyze, proxies, proxyCount);
	}
}
