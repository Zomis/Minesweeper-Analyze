package net.zomis.minesweeper.analyze.detail;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.zomis.minesweeper.analyze.FieldGroup;
import net.zomis.minesweeper.analyze.GroupValues;
import net.zomis.minesweeper.analyze.RootAnalyze;
import net.zomis.minesweeper.analyze.Solution;

public class DetailAnalyze<Field> implements IFieldProxyProvider<Field> {
	public static interface NeighborFind<Field> {
		Collection<Field> getNeighborsFor(Field field);
		boolean isFoundAndisMine(Field field);
	}
	private final List<FieldGroup<Field>>	groups;
	private final Map<Field, FieldProxy<Field>> proxies;
	private final RootAnalyze<Field>	analyze;
	private int	proxyCount;

	public Collection<FieldProxy<Field>> getProxies() {
		return this.proxies.values();
	}
	
	public DetailAnalyze(RootAnalyze<Field> analyze) {
		this.analyze = analyze;
		this.groups = analyze.getGroups();
		this.proxies = new HashMap<Field, FieldProxy<Field>>();
	}
	
	public Map<Field, FieldProxy<Field>> solveDetailed(NeighborFind<Field> neighborStrategy) {
		// Total of max 256 iterations here
		for (FieldGroup<Field> grp : this.groups) {
			for (Field f : grp) {
				FieldProxy<Field> proxy = new FieldProxy<Field>(grp, f);
				proxies.put(f, proxy);
			}
		}
		
		// 256 iterations
		for (FieldProxy<Field> ff : this.proxies.values()) {
			ff.fixNeighbors(neighborStrategy, this);
		}
		
		// 256 iterations
//		int i = 0;
//		int total = this.proxies.size();
		double ncrtotal = this.analyze.getTotal();
		Map<GroupValues<Field>, FieldProxy<Field>> bufferedValues = new HashMap<GroupValues<Field>, FieldProxy<Field>>();
		for (Entry<Field, FieldProxy<Field>> ee : this.proxies.entrySet()) {
//			i++;
			// A hell lot of iterations...
			FieldProxy<Field> mappedValue = bufferedValues.get(ee.getValue().getNeighbors());
			if (mappedValue != null && mappedValue.getFieldGroup() == ee.getValue().getFieldGroup()) {
				ee.getValue().copyFromOther(mappedValue, ncrtotal);
//				Zomis.echo("Cheat Known: " + i + " / " + total + ": " + ee.getValue());
			}
			else {
				for (Solution<Field> solution : analyze.getSolutionIteration()) {
					ee.getValue().addSolution(solution);
				}
				ee.getValue().finalCalculation(ncrtotal);
				bufferedValues.put(ee.getValue().getNeighbors(), ee.getValue());
//				Zomis.echo("Known: " + i + " / " + total + ": " + ee.getValue());
			}
		}
		this.proxyCount = bufferedValues.size();
		return this.proxies;
	}

	@Override
	public FieldProxy<Field> getProxyFor(Field field) {
		return this.proxies.get(field);
	}
	public int getProxyCount() {
		return proxyCount;
	}
}
