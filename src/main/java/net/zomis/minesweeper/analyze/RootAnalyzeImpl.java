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

public class RootAnalyzeImpl<Field> implements SolvedCallback<Field>, RootAnalyze<Field> {
	// IDEA: Iterator to loop through the possible solutions, without creating them -- saves memory but still wastes time
	// IDEA: getProbabilityOf on a SolutionSet object, which returns a solutionSet. To nest the calls. analyze.getProbabilityOf(...).getProbabilityOf().getProbability().
	//           this would require the solutionset to know the original combinations (double), not change it, and compare against the current combinations.
	
	// replace some of the lists in analyze code with LinkedLists. -- check speed compare.
	private final List<FieldRule<Field>> rules = new ArrayList<FieldRule<Field>>();
	private final List<Solution<Field>> solutions = new ArrayList<Solution<Field>>();
	
	private final List<FieldGroup<Field>> groups = new ArrayList<FieldGroup<Field>>();
	private double total;
	private boolean solved = false;
	private final List<FieldRule<Field>> originalRules = new ArrayList<FieldRule<Field>>();
	
	@Override
	public double getTotal() {
		return this.total;
	}
	
	private RootAnalyzeImpl(Solution<Field> known) {
		for (Entry<FieldGroup<Field>, Integer> sol : known.getSetGroupValues().entrySet()) {
			this.rules.add(new FieldRule<Field>(null, sol.getKey(), sol.getValue()));
		}
	}
	public RootAnalyzeImpl() {}
	
	public void addRule(FieldRule<Field> rule) {
		this.rules.add(rule);
	}
	
	/**
	 * Get the list of simplified rules used to perform the analyze
	 * 
	 * @return List of simplified rules
	 */
	@Override
	public List<FieldRule<Field>> getRules() {
		return new ArrayList<FieldRule<Field>>(this.rules);
	}
	
	@Override
	public FieldGroup<Field> getGroupFor(Field field) {
		for (FieldGroup<Field> group : this.groups)
		if (group.contains(field)) {
			return group;
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
	public List<Field> randomSolution(Random random) {
		if (random == null)
			throw new IllegalArgumentException("Random object cannot be null");
		
		List<Solution<Field>> solutions = new LinkedList<Solution<Field>>(this.solutions);
		if (this.getTotal() == 0) throw new IllegalStateException("Analyze has 0 combinations: " + this);
		
		double rand = random.nextDouble() * this.getTotal();
		Solution<Field> theSolution = null;
		
		while (rand > 0) {
			if (solutions.isEmpty()) throw new IllegalStateException("Solutions is suddenly empty.");
			theSolution = solutions.get(0);
			rand -= theSolution.nCr();
			solutions.remove(0);
		}
		
		return theSolution.getRandomSolution(random);
	}
	
	private RootAnalyzeImpl<Field> solutionToNewAnalyze(Solution<Field> solution, List<FieldRule<Field>> extraRules) {
		Collection<FieldRule<Field>> newRules = new ArrayList<FieldRule<Field>>();
		for (FieldRule<Field> rule : extraRules) 
			newRules.add(new FieldRule<Field>(rule)); // Need to create new rules for each solution iteration, because the older ones has been simplified.
		RootAnalyzeImpl<Field> newRoot = new RootAnalyzeImpl<Field>(solution);
		newRoot.rules.addAll(newRules);
		return newRoot;
	}
	
	@Override
	public RootAnalyze<Field> cloneAddSolve(List<FieldRule<Field>> extraRules) {
		List<FieldRule<Field>> newRules = this.getOriginalRules();
		newRules.addAll(extraRules);
		RootAnalyzeImpl<Field> copy = new RootAnalyzeImpl<Field>();
		for (FieldRule<Field> rule : newRules) {
			copy.addRule(new FieldRule<Field>(rule));
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
	public List<FieldRule<Field>> getOriginalRules() {
		return this.originalRules.isEmpty() ? this.getRules() : new ArrayList<FieldRule<Field>>(this.originalRules);
	}

	private double getTotalWith(List<FieldRule<Field>> extraRules) {
		if (!this.solved)
			throw new IllegalStateException("Analyze is not solved");
		double total = 0;
		
		for (Solution<Field> solution : this.getSolutions()) {
			RootAnalyzeImpl<Field> root = this.solutionToNewAnalyze(solution, extraRules);
			root.solve();
			total += root.getTotal();
		}
		
		return total;
	}
	
	@Override
	public double getProbabilityOf(List<FieldRule<Field>> extraRules) {
		return this.getTotalWith(extraRules) / this.getTotal();
	}
	
	@Override
	public Collection<Solution<Field>> getSolutions() {
		if (!this.solved)
			throw new IllegalStateException("Analyze is not solved");
		return new ArrayList<Solution<Field>>(this.solutions);
	}

	/**
	 * Separate fields into field groups. Example <code>a + b + c = 2</code> and <code>b + c + d = 1</code> becomes <code>(a) + (b + c) = 2</code> and <code>(b + c) + (d) = 1</code>. This method is called automatically when calling {@link #solve()}
	 */
	public void splitFieldRules() {
		boolean splitPerformed = true;
		int splits = (int) Math.pow(rules.size(), 5);
		while (splitPerformed) {
			splitPerformed = false;
			for (FieldRule<Field> a : rules) {
//				if (a.isIsolated()) continue; // TODO: Using this code can lead to infinite loop
				for (FieldRule<Field> b : rules) {
					List<FieldGroup<Field>> result = a.checkIntersection(b);
					
					if (result != null) {
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
		
		List<FieldRule<Field>> original = new ArrayList<FieldRule<Field>>(this.rules.size());
		for (FieldRule<Field> rule : this.rules) {
			original.add(new FieldRule<Field>(rule));
		}
		this.originalRules.addAll(original);
		
		this.splitFieldRules();
		
		this.total = 0;
		
		new GameAnalyze<Field>(null, rules, this).solve();
		
		for (Solution<Field> solution : this.solutions) {
			solution.setTotal(total);
		}
		
		if (!this.solutions.isEmpty()) {
			for (FieldGroup<Field> group : this.solutions.get(0).getSetGroupValues().keySet()) {
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
	public List<FieldGroup<Field>> getGroups() {
		if (!this.solved) {
			Set<FieldGroup<Field>> agroups = new HashSet<FieldGroup<Field>>();
			for (FieldRule<Field> rule : this.getRules()) {
				agroups.addAll(rule.getFieldGroups());
			}
			return new ArrayList<FieldGroup<Field>>(agroups);
		}
		
		List<FieldGroup<Field>> grps = new ArrayList<FieldGroup<Field>>(this.groups);
		Iterator<FieldGroup<Field>> it = grps.iterator();
		while (it.hasNext()) {
			if (it.next().isEmpty()) it.remove(); // remove empty fieldgroups
		}
		return grps;
	}
	@Override
	public List<Field> getFields() {
		if (!this.solved) 
			throw new IllegalStateException("Analyze is not solved");
		
		List<Field> allFields = new ArrayList<Field>();
		for (FieldGroup<Field> group : this.getGroups()) {
			allFields.addAll(group);
		}
		return allFields;
	}

	@Override
	public void solved(Solution<Field> solved) {
		this.solutions.add(solved);
		this.total += solved.nCr();
	}

	@Override
	public List<Field> getSolution(double solution) {
		if (Math.rint(solution) != solution)
			throw new IllegalArgumentException("solution must be an integer");
		
		if (solution < 0 || solution >= this.getTotal())
			throw new IllegalArgumentException("solution must be between 0 and total (" + this.getTotal() + ")");
		
		List<Solution<Field>> solutions = new LinkedList<Solution<Field>>(this.solutions);
		if (solutions.isEmpty())
			throw new IllegalStateException("Solutions is empty.");
		Solution<Field> theSolution = solutions.get(0);
		while (solution > theSolution.nCr()) {
			solution -= theSolution.nCr();
			solutions.remove(0);
			theSolution = solutions.get(0);
		}
		return theSolution.getCombination(solution);
	}
	
	@Override
	public String toString() {
		return this.getRules().toString() + " original: " + this.getOriginalRules().toString();
	}

	@Override
	public Iterable<Solution<Field>> getSolutionIteration() {
		return this.solutions;
	}
}
