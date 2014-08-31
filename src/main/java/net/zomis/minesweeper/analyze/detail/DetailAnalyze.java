package net.zomis.minesweeper.analyze.detail;

import java.util.HashMap;
import java.util.Map;

import net.zomis.minesweeper.analyze.AnalyzeResult;
import net.zomis.minesweeper.analyze.FieldGroup;
import net.zomis.minesweeper.analyze.GroupValues;
import net.zomis.minesweeper.analyze.Solution;

public class DetailAnalyze<T> {
	public static <T> DetailedResults<T> solveDetailed(AnalyzeResult<T> analyze, NeighborFind<T> neighborStrategy) {
		// Initialize FieldProxies
		final Map<T, FieldProxy<T>> proxies = new HashMap<T, FieldProxy<T>>();
		for (FieldGroup<T> group : analyze.getGroups()) {
			for (T field : group) {
				FieldProxy<T> proxy = new FieldProxy<T>(group, field);
				proxies.put(field, proxy);
			}
		}
		
		// Setup proxy provider
		ProxyProvider<T> provider = new ProxyProvider<T>() {
			@Override
			public FieldProxy<T> getProxyFor(T field) {
				return proxies.get(field);
			}
		};
		
		// Setup neighbors for proxies
		for (FieldProxy<T> fieldProxy : proxies.values()) {
			fieldProxy.fixNeighbors(neighborStrategy, provider);
		}
		
		double totalCombinations = analyze.getTotal();
		Map<GroupValues<T>, FieldProxy<T>> bufferedValues = new HashMap<GroupValues<T>, FieldProxy<T>>();
		for (FieldProxy<T> proxy : proxies.values()) {
			FieldProxy<T> bufferedValue = bufferedValues.get(proxy.getNeighbors());
			if (bufferedValue != null && bufferedValue.getFieldGroup() == proxy.getFieldGroup()) {
				proxy.copyFromOther(bufferedValue, totalCombinations);
				continue;
			}
			
			// Setup the probabilities for this field proxy
			for (Solution<T> solution : analyze.getSolutionIteration()) {
				proxy.addSolution(solution);
			}
			proxy.finalCalculation(totalCombinations);
			bufferedValues.put(proxy.getNeighbors(), proxy);
		}
		
		int proxyCount = bufferedValues.size();
		
		return new DetailedResultsImpl<T>(analyze, proxies, proxyCount);
	}
}
