package net.zomis.minesweeper.analyze.detail;

import java.util.Collection;

public interface NeighborFind<T> {
	Collection<T> getNeighborsFor(T field);
	boolean isFoundAndisMine(T field);
}
