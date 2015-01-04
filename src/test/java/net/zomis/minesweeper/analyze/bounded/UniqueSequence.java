package net.zomis.minesweeper.analyze.bounded;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map.Entry;
import java.util.Set;

import net.zomis.minesweeper.analyze.FieldGroup;
import net.zomis.minesweeper.analyze.FieldRule;
import net.zomis.minesweeper.analyze.GroupValues;
import net.zomis.minesweeper.analyze.SimplifyResult;

public class UniqueSequence<T> extends FieldRule<T> {

	private final List<List<T>> list;

	public UniqueSequence(T cause, List<List<T>> fields) {
		super(cause, new ArrayList<T>(), 0);
		this.list = fields;
	}
	
	@Override
	public boolean isEmpty(GroupValues<T> knownValues) {
		return list.isEmpty();
	}
	
	@Override
	public FieldGroup<T> getSmallestFieldGroup() {
		return null;
	}

	@Override
	public SimplifyResult simplify(GroupValues<T> knownValues) {
		// TODO: Listener for when something is added to knownValues? To be able to detect directly if rule is still OK or not. Likely speed-up.
		Set<Set<Integer>> setValues = new HashSet<Set<Integer>>();
		for (List<T> row : list) {
			SimplifyResult result = addSet(setValues, row, knownValues);
			if (result.isFailure()) {
				return SimplifyResult.FAILED_TOO_BIG_RESULT;
			}
		}
		
		for (List<T> i : list) {
			for (T t : i) {
				Integer value = getSetValue(t, knownValues);
				if (value == null) {
					return SimplifyResult.NO_EFFECT;
				}
			}
		}
		
		list.clear();
		return SimplifyResult.NO_EFFECT;
	}
	
	@Override
	public UniqueSequence<T> copy() {
		return new UniqueSequence<T>(getCause(), list);
	}

	private SimplifyResult addSet(Set<Set<Integer>> setValues, List<T> row, GroupValues<T> knownValues) {
		
		// loop through row, for each check if it has a value of 1 in the `knownValues`,
		// convert the *index*es of the positions to a Set and add to setValues
		ListIterator<T> it = row.listIterator();
		Set<Integer> indexSet = new HashSet<Integer>();
		while (it.hasNext()) {
			int index = it.nextIndex();
			T pos = it.next();
			Integer setValue = getSetValue(pos, knownValues);
			if (setValue == null) {
				return SimplifyResult.NO_EFFECT;
			}
			if (setValue == 1) {
				indexSet.add(index);
			}
		}
		boolean addOK = setValues.add(indexSet);
		return addOK ? SimplifyResult.SIMPLIFIED : SimplifyResult.FAILED_NEGATIVE_RESULT;
	}

	private Integer getSetValue(T pos, GroupValues<T> knownValues) {
		for (Entry<FieldGroup<T>, Integer> ee : knownValues.entrySet()) {
			if (ee.getKey().contains(pos)) {
				return ee.getValue() / ee.getKey().size();
			}
		}
		return null;
	}

}
