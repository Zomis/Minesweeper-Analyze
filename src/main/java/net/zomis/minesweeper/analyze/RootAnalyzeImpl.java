package net.zomis.minesweeper.analyze;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

public class RootAnalyzeImpl<T> implements SolvedCallback<T>, RootAnalyze<T> {
	// IDEA: Iterator to loop through the possible solutions, without creating them -- saves memory but still wastes time
	// IDEA: getProbabilityOf on a SolutionSet object, which returns a solutionSet. To nest the calls. analyze.getProbabilityOf(...).getProbabilityOf().getProbability().
	//           this would require the solutionset to know the original combinations (double), not change it, and compare against the current combinations.
	
	// replace some of the lists in analyze code with LinkedLists. -- check speed compare.
	private final List<FieldRule<T>> rules = new ArrayList<FieldRule<T>>();
	private final List<Solution<T>> solutions = new ArrayList<Solution<T>>();
	
	private final List<FieldGroup<T>> groups = new ArrayList<FieldGroup<T>>();
	private double total;
	private boolean solved = false;
	private final List<FieldRule<T>> originalRules = new ArrayList<FieldRule<T>>();
	
	@Override
	public double getTotal() {
		return this.total;
	}
	
	private RootAnalyzeImpl(Solution<T> known) {
		for (Entry<FieldGroup<T>, Integer> sol : known.getSetGroupValues().entrySet()) {
			this.rules.add(new FieldRule<T>(null, sol.getKey(), sol.getValue()));
		}
	}
	public RootAnalyzeImpl() {}
	
	public void addRule(FieldRule<T> rule) {
		this.rules.add(rule);
	}
	
	/**
	 * Get the list of simplified rules used to perform the analyze
	 * 
	 * @return List of simplified rules
	 */
	@Override
	public List<FieldRule<T>> getRules() {
		return new ArrayList<FieldRule<T>>(this.rules);
	}
	
	@Override
	public FieldGroup<T> getGroupFor(T field) {
		for (FieldGroup<T> group : this.groups) {
			if (group.contains(field)) {
				return group;
			}
		}
		return null;
	}
	
	/**
	 * Return a random solution that satisfies all the rules
	 * 
	 * @param random Random object to perform the randomization
	 * @return A list of fields randomly selected that is guaranteed to be a solution to the constraints
	 * 
	 */
	@Override
	public List<T> randomSolution(Random random) {
		if (random == null)
			throw new IllegalArgumentException("Random object cannot be null");
		
		List<Solution<T>> solutions = new LinkedList<Solution<T>>(this.solutions);
		if (this.getTotal() == 0) throw new IllegalStateException("Analyze has 0 combinations: " + this);
		
		double rand = random.nextDouble() * this.getTotal();
		Solution<T> theSolution = null;
		
		while (rand > 0) {
			if (solutions.isEmpty()) throw new IllegalStateException("Solutions is suddenly empty.");
			theSolution = solutions.get(0);
			rand -= theSolution.nCr();
			solutions.remove(0);
		}
		
		return theSolution.getRandomSolution(random);
	}
	
	private RootAnalyzeImpl<T> solutionToNewAnalyze(Solution<T> solution, List<FieldRule<T>> extraRules) {
		Collection<FieldRule<T>> newRules = new ArrayList<FieldRule<T>>();
		for (FieldRule<T> rule : extraRules) 
			newRules.add(new FieldRule<T>(rule)); // Need to create new rules for each solution iteration, because the older ones has been simplified.
		RootAnalyzeImpl<T> newRoot = new RootAnalyzeImpl<T>(solution);
		newRoot.rules.addAll(newRules);
		return newRoot;
	}
	
	@Override
	public RootAnalyze<T> cloneAddSolve(List<FieldRule<T>> extraRules) {
		List<FieldRule<T>> newRules = this.getOriginalRules();
		newRules.addAll(extraRules);
		RootAnalyzeImpl<T> copy = new RootAnalyzeImpl<T>();
		for (FieldRule<T> rule : newRules) {
			copy.addRule(new FieldRule<T>(rule));
		}
		copy.solve();
		return copy;
	}
	
	/**
	 * Get the list of the original, non-simplified, rules
	 * 
	 * @return The original rule list  
	 */
	@Override
	public List<FieldRule<T>> getOriginalRules() {
		return this.originalRules.isEmpty() ? this.getRules() : new ArrayList<FieldRule<T>>(this.originalRules);
	}

	private double getTotalWith(List<FieldRule<T>> extraRules) {
		if (!this.solved)
			throw new IllegalStateException("Analyze is not solved");
		double total = 0;
		
		for (Solution<T> solution : this.getSolutions()) {
			RootAnalyzeImpl<T> root = this.solutionToNewAnalyze(solution, extraRules);
			root.solve();
			total += root.getTotal();
		}
		
		return total;
	}
	
	@Override
	public double getProbabilityOf(List<FieldRule<T>> extraRules) {
		if (!this.solved)
			throw new IllegalStateException("Analyze is not solved");
		return this.getTotalWith(extraRules) / this.getTotal();
	}
	
	@Override
	public List<Solution<T>> getSolutions() {
		if (!this.solved)
			throw new IllegalStateException("Analyze is not solved");
		return new ArrayList<Solution<T>>(this.solutions);
	}

	/**
	 * Separate fields into field groups. Example <code>a + b + c = 2</code> and <code>b + c + d = 1</code> becomes <code>(a) + (b + c) = 2</code> and <code>(b + c) + (d) = 1</code>. This method is called automatically when calling {@link #solve()}
	 */
	public void splitFieldRules() {
		if (rules.size() <= 1)
			return;
			
		boolean splitPerformed = true;
		int splits = (int) Math.pow(rules.size(), 5);
		while (splitPerformed) {
			splitPerformed = false;
			for (FieldRule<T> a : rules) {
//				if (a.isIsolated()) continue; // TODO: Using this code can lead to infinite loop
				for (FieldRule<T> b : rules) {
					boolean result = a.checkIntersection(b);
					
					if (result) {
						splitPerformed = true;
					}
				}
			}
			splits--;
			if (splits == 0) {
				throw new AssertionError("Infinite loop during splitting! Current rules: " + rules);
			}
		}
	}
	
	
	public void solve() {
		if (this.solved)
			throw new IllegalStateException("Analyze has already been solved");
		
		List<FieldRule<T>> original = new ArrayList<FieldRule<T>>(this.rules.size());
		for (FieldRule<T> rule : this.rules) {
			original.add(new FieldRule<T>(rule));
		}
		this.originalRules.addAll(original);
		
		this.splitFieldRules();
		
		this.total = 0;
		
		new GameAnalyze<T>(null, rules, this).solve();
		
		for (Solution<T> solution : this.solutions) {
			solution.setTotal(total);
		}
		
		if (!this.solutions.isEmpty()) {
			for (FieldGroup<T> group : this.solutions.get(0).getSetGroupValues().keySet()) {
				// All solutions should contain the same fieldgroups.
				groups.add(group);
			}
		}
		this.solved = true;
	}
	
	public static double nCr(int n, int r) {
		if (r > n || r < 0)
			return 0;
		if (r == 0 || r == n)
			return 1;
		
		double value = 1;
		
		for (int i = 0; i < r; i++) {
			value = value * (n - i) / (r - i);
		}
		
		return value;
	}

	@Override
	public List<FieldGroup<T>> getGroups() {
		if (!this.solved) {
			Set<FieldGroup<T>> agroups = new HashSet<FieldGroup<T>>();
			for (FieldRule<T> rule : this.getRules()) {
				agroups.addAll(rule.getFieldGroups());
			}
			return new ArrayList<FieldGroup<T>>(agroups);
		}
		
		List<FieldGroup<T>> grps = new ArrayList<FieldGroup<T>>(this.groups);
		Iterator<FieldGroup<T>> it = grps.iterator();
		while (it.hasNext()) {
			if (it.next().isEmpty())
				it.remove(); // remove empty fieldgroups
		}
		return grps;
	}
	@Override
	public List<T> getFields() {
		if (!this.solved) 
			throw new IllegalStateException("Analyze is not solved");
		
		List<T> allFields = new ArrayList<T>();
		for (FieldGroup<T> group : this.getGroups()) {
			allFields.addAll(group);
		}
		return allFields;
	}

	@Override
	public void solved(Solution<T> solved) {
		this.solutions.add(solved);
		this.total += solved.nCr();
	}

	@Override
	public List<T> getSolution(double solution) {
		if (Math.rint(solution) != solution)
			throw new IllegalArgumentException("solution must be an integer");
		
		if (solution < 0 || solution >= this.getTotal())
			throw new IllegalArgumentException("solution must be between 0 and total (" + this.getTotal() + ")");
		
		List<Solution<T>> solutions = new LinkedList<Solution<T>>(this.solutions);
		if (solutions.isEmpty())
			throw new IllegalStateException("Solutions is empty.");
		Solution<T> theSolution = solutions.get(0);
		while (solution > theSolution.nCr()) {
			solution -= theSolution.nCr();
			solutions.remove(0);
			theSolution = solutions.get(0);
		}
		return theSolution.getCombination(solution);
	}
	
	@Override
	public Iterable<Solution<T>> getSolutionIteration() {
		return this.solutions;
	}
}
