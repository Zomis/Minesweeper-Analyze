package net.zomis.minesweeper.analyze;

public interface SolvedCallback<T> {
	void solved(Solution<T> solved);
}
