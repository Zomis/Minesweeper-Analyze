package net.zomis.minesweeper.analyze;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;

public class FieldGroupSplitTest {

	@Test
	public void split() {
		FieldGroup<String> a = new FieldGroup<String>(Arrays.asList("a", "b", "c"));
		FieldGroup<String> b = new FieldGroup<String>(Arrays.asList("b", "c", "d"));
		FieldGroupSplit<String> split = FieldGroupSplit.split(a, b);
		assertEquals(1, split.getOnlyA().size());
		assertEquals(2, split.getBoth().size());
		assertEquals(1, split.getOnlyB().size());
		assertTrue(split.splitPerformed());
	}
	
}
