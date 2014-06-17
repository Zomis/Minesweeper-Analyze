package net.zomis.minesweeper.analyze;

import static org.junit.Assert.*;

import org.junit.Test;

import net.zomis.minesweeper.analyze.factory.CharPoint;

public class CharPointTest {

	@Test
	public void notEquals() {
		assertFalse(new CharPoint(5, 4, ' ').equals("A string"));
		assertFalse(new CharPoint(5, 4, ' ').equals(new CharPoint(5, 4, 'x')));
		assertFalse(new CharPoint(5, 3, ' ').equals(new CharPoint(5, 4, ' ')));
		assertFalse(new CharPoint(3, 4, ' ').equals(new CharPoint(5, 4, ' ')));
	}
	
	@Test
	public void equals() {
		assertTrue(new CharPoint(5, 4, ' ').equals(new CharPoint(5, 4, ' ')));
	}
	
	
}
