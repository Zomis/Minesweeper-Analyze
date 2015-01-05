package net.zomis.minesweeper.analyze;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class IterateOnce<T> implements Iterator<T> {

	private boolean iterated = false;
	private final T list;
	
	public IterateOnce(T list) {
		this.list = list;
	}
	
	@Override
	public boolean hasNext() {
		return !iterated;
	}

	@Override
	public T next() {
		if (!hasNext()) {
			throw new NoSuchElementException();
		}
		iterated = true;
		return list;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

}
