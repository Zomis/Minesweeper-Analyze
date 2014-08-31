package net.zomis.minesweeper.analyze.detail;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map.Entry;

import net.zomis.minesweeper.analyze.Combinatorics;
import net.zomis.minesweeper.analyze.FieldGroup;
import net.zomis.minesweeper.analyze.GroupValues;
import net.zomis.minesweeper.analyze.RuntimeTimeoutException;
import net.zomis.minesweeper.analyze.Solution;

public class FieldProxy<T> implements ProbabilityKnowledge<T> {
	
	private static int minK(int N, int K, int n) {
		// If all fields in group are neighbors to this field then all mines must be neighbors to this field as well
		return (N == K) ? n : 0;
	}
	
	private double[] detailedCombinations;
	private double[] detailedProbabilities;
	private final T field;
	private int	found;
	private final FieldGroup<T> group;
	private final GroupValues<T> neighbors;
	
	public FieldProxy(FieldGroup<T> group, T field) {
		this.field = field;
		this.neighbors = new GroupValues<T>();
		this.group = group;
		this.found = 0;
	}
	
	void addSolution(Solution<T> solution) {
		solution = solution.copyWithoutNCRData();
		
		recursiveRemove(solution, 1, 0);
	}
	
	void copyFromOther(FieldProxy<T> copyFrom, double analyzeTotal) {
		for (int i = 0; i < this.detailedCombinations.length - this.found; i++) {
			if (copyFrom.detailedCombinations.length <= i + copyFrom.found) break;
			this.detailedCombinations[i + this.found] = copyFrom.detailedCombinations[i + copyFrom.found];
		}
		
		this.finalCalculation(analyzeTotal);
	}
	
	void finalCalculation(double analyzeTotal) {
		this.detailedProbabilities = new double[this.detailedCombinations.length];
		for (int i = 0; i < this.detailedProbabilities.length; i++) {
			this.detailedProbabilities[i] = this.detailedCombinations[i] / analyzeTotal;
		}
	}
	
	void fixNeighbors(NeighborFind<T> neighborStrategy, ProxyProvider<T> proxyProvider) {
		Collection<T> realNeighbors = neighborStrategy.getNeighborsFor(field);
		this.detailedCombinations = new double[realNeighbors.size() + 1];
		for (T neighbor : realNeighbors) {
			if (neighborStrategy.isFoundAndisMine(neighbor)) {
				this.found++;
				continue; // A found mine is not, and should not be, in a fieldproxy
			}
			
			FieldProxy<T> proxy = proxyProvider.getProxyFor(neighbor);
			if (proxy == null) {
				continue;
			}
			
			FieldGroup<T> neighborGroup = proxy.group;
			if (neighborGroup != null) {
				// Ignore zero-probability neighborGroups
				if (neighborGroup.getProbability() == 0) {
					continue;
				}
				
				Integer getValue = neighbors.get(neighborGroup);
				
				if (getValue == null) {
					neighbors.put(neighborGroup, 1);
				}
				else neighbors.put(neighborGroup, getValue + 1);
			}
		}
		
	}
	
	@Override
	public T getField() {
		return this.field;
	}
	
	@Override
	public FieldGroup<T> getFieldGroup() {
		return this.group;
	}
	
	@Override
	public int getFound() {
		return this.found;
	}
	
	@Override
	public double getMineProbability() {
		return this.group.getProbability();
	}

	@Override
	public GroupValues<T> getNeighbors() {
		return this.neighbors;
	}

	@Override
	public double[] getProbabilities() {
		return this.detailedProbabilities;
	}

	private void recursiveRemove(Solution<T> solution, double combinations, int mines) {
		if (Thread.interrupted()) {
    		throw new RuntimeTimeoutException();
		}
		
		GroupValues<T> remaining = solution.getSetGroupValues();
		if (remaining.isEmpty()) { // or if combinations equals zero ?
			this.detailedCombinations[mines + this.found] += combinations;
			return;
		}
		
		Entry<FieldGroup<T>, Integer> ee = remaining.entrySet().iterator().next();
		FieldGroup<T> group = ee.getKey();
		
		
		int N = ee.getKey().size();
		int n = ee.getValue();
		Integer K = this.neighbors.get(group);
		remaining.remove(group);
		solution = Solution.createSolution(remaining);
		
		if (this.group == group) N--;
		
		if (K == null) {
			// This field does not have any neighbors to that group.
			recursiveRemove(solution, combinations * Combinatorics.nCr(N, n), mines);
		}
		else {
			int maxLoop = Math.min(K, n);
			for (int k = minK(N, K, n); k <= maxLoop; k++) {
				double thisCombinations = Combinatorics.NNKK(N, n, K, k);
				recursiveRemove(solution, combinations * thisCombinations, mines + k);
			}
		}
	}
	
	@Override
	public String toString() {
		return "Proxy(" + this.field.toString() + ")"
				+ "\n neighbors: " + this.neighbors.toString()
				+ "\n group: " + this.group.toString()
				+ "\n Mine prob " + this.group.getProbability() + " Numbers: " + Arrays.toString(this.detailedProbabilities);
	}
}
