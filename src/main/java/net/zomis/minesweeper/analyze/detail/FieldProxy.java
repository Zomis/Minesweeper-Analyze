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
		recursiveRemove(solution.copyWithoutNCRData(), 1, 0);
	}
	
	/**
	 * This field has the same values as another field, copy the values.
	 * 
	 * @param copyFrom {@link FieldProxy} to copy from
	 * @param analyzeTotal Total number of combinations
	 */
	void copyFromOther(FieldProxy<T> copyFrom, double analyzeTotal) {
		System.arraycopy(copyFrom.detailedCombinations, copyFrom.found, this.detailedCombinations, this.found, Math.min(this.detailedCombinations.length - this.found, copyFrom.detailedCombinations.length - copyFrom.found));
		
		this.finalCalculation(analyzeTotal);
	}
	
	/**
	 * Calculate final probabilities from the combinations information
	 * 
	 * @param analyzeTotal Total number of combinations
	 */
	void finalCalculation(double analyzeTotal) {
		this.detailedProbabilities = new double[this.detailedCombinations.length];
		for (int i = 0; i < this.detailedProbabilities.length; i++) {
			this.detailedProbabilities[i] = this.detailedCombinations[i] / analyzeTotal;
		}
	}
	
	/**
	 * Setup the neighbors for this field
	 * 
	 * @param neighborStrategy {@link NeighborFind} strategy
	 * @param proxyProvider Interface to get the related proxies
	 */
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
				
				// Increase the number of neighbors
				Integer currentNeighborAmount = neighbors.get(neighborGroup);
				if (currentNeighborAmount == null) {
					neighbors.put(neighborGroup, 1);
				}
				else neighbors.put(neighborGroup, currentNeighborAmount + 1);
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
		
		// Check if there are more field groups with values
		GroupValues<T> remaining = solution.getSetGroupValues();
		if (remaining.isEmpty()) {
			// TODO: or if combinations equals zero ?
			this.detailedCombinations[mines + this.found] += combinations;
			return;
		}
		
		// Get the first assignment
		Entry<FieldGroup<T>, Integer> fieldGroupAssignment = remaining.entrySet().iterator().next();
		FieldGroup<T> group = fieldGroupAssignment.getKey();
		remaining.remove(group);
		solution = Solution.createSolution(remaining);
		
		// Setup values for the hypergeometric distribution calculation. See http://en.wikipedia.org/wiki/Hypergeometric_distribution
		int N = group.size();
		int n = fieldGroupAssignment.getValue();
		Integer K = this.neighbors.get(group);
		if (this.group == group) {
			N--; // Always exclude self becuase you can't be neighbor to yourself
		}
		
		if (K == null) {
			// This field does not have any neighbors to that group.
			recursiveRemove(solution, combinations * Combinatorics.nCr(N, n), mines);
			return;
		}
		
		// Calculate the values and then calculate for the next group
		int maxLoop = Math.min(K, n);
		for (int k = minK(N, K, n); k <= maxLoop; k++) {
			double thisCombinations = Combinatorics.NNKK(N, n, K, k);
			recursiveRemove(solution, combinations * thisCombinations, mines + k);
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
