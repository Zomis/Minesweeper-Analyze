package net.zomis.minesweeper.analyze;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;

public class FieldGroupSplitTest {

	private <T> FieldGroupSplit<T> split(FieldGroup<T> a, FieldGroup<T> b) {
		return FieldGroupSplit.split(a, b);
	}
	
	@Test
	public void allFieldsCommon() {
		FieldGroup<String> a = new FieldGroup<String>(Arrays.asList("a", "b", "c"));
		FieldGroup<String> b = new FieldGroup<String>(Arrays.asList("a", "b", "c"));
		FieldGroupSplit<String> split = split(a, b);
		assertTrue(split.getOnlyA().isEmpty());
		assertFalse(split.getBoth().isEmpty());
		assertSame(a, split.getBoth());
		assertTrue(split.getOnlyB().isEmpty());
		assertFalse(split.splitPerformed());
	}
	
	@Test
	public void sameFields() {
		FieldGroup<String> a = new FieldGroup<String>(Arrays.asList("a", "a", "b", "c"));
		FieldGroup<String> b = new FieldGroup<String>(Arrays.asList("a", "a", "d", "e"));
		FieldGroupSplit<String> split = split(a, b);
		assertEquals(new FieldGroup<String>(Arrays.asList("b", "c")), split.getOnlyA());
		assertEquals(new FieldGroup<String>(Arrays.asList("a", "a")), split.getBoth());
		assertEquals(new FieldGroup<String>(Arrays.asList("d", "e")), split.getOnlyB());
	}
	
	@Test
	public void split() {
		FieldGroup<String> a = new FieldGroup<String>(Arrays.asList("a", "b", "c"));
		FieldGroup<String> b = new FieldGroup<String>(Arrays.asList("b", "c", "d"));
		FieldGroupSplit<String> split = split(a, b);
		assertEquals(new FieldGroup<String>(Arrays.asList("a")), split.getOnlyA());
		assertEquals(new FieldGroup<String>(Arrays.asList("b", "c")), split.getBoth());
		assertEquals(new FieldGroup<String>(Arrays.asList("d")), split.getOnlyB());
		assertTrue(split.splitPerformed());
	}
	
	@Test
	public void disjointGroups() {
		FieldGroup<String> a = new FieldGroup<String>(Arrays.asList("a", "b", "c"));
		FieldGroup<String> b = new FieldGroup<String>(Arrays.asList("d", "e", "f"));
		FieldGroupSplit<String> split = split(a, b);
		assertNull(split);
	}
	
}
