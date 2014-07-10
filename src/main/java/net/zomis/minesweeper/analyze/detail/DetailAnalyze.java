package net.zomis.minesweeper.analyze.detail;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.zomis.minesweeper.analyze.FieldGroup;
import net.zomis.minesweeper.analyze.GroupValues;
import net.zomis.minesweeper.analyze.AnalyzeResult;
import net.zomis.minesweeper.analyze.Solution;

public class DetailAnalyze<T> {
	// TODO: Separate into Analyze and analyze result. Add a method on `AnalyzeResult` to retreive a `DetailedAnalyzeResult`
	private final AnalyzeResult<T>	analyze;
	private final List<FieldGroup<T>>	groups;
	private final Map<T, FieldProxy<T>> proxies;
	private int	proxyCount;

	public DetailAnalyze(AnalyzeResult<T> analyze) {
		this.analyze = analyze;
		this.groups = analyze.getGroups();
		this.proxies = new HashMap<T, FieldProxy<T>>();
	}
	
	public Collection<FieldProxy<T>> getProxies() {
		return this.proxies.values();
	}
	
	public int getProxyCount() {
		return proxyCount;
	}

	public FieldProxy<T> getProxyFor(T field) {
		return this.proxies.get(field);
	}
	
	public Map<T, FieldProxy<T>> solveDetailed(NeighborFind<T> neighborStrategy) {
		for (FieldGroup<T> group : this.groups) {
			for (T field : group) {
				FieldProxy<T> proxy = new FieldProxy<T>(group, field);
				proxies.put(field, proxy);
			}
		}
		
		for (FieldProxy<T> fieldProxy : this.proxies.values()) {
			fieldProxy.fixNeighbors(neighborStrategy, this);
		}
		
		double ncrtotal = this.analyze.getTotal();
		Map<GroupValues<T>, FieldProxy<T>> bufferedValues = new HashMap<GroupValues<T>, FieldProxy<T>>();
		for (Entry<T, FieldProxy<T>> ee : this.proxies.entrySet()) {
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
		this.proxyCount = bufferedValues.size();
		return this.proxies;
	}
}
