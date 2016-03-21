package net.zomis.minesweeper.analyze;

@Deprecated
public interface SolvedCallback<T> {
	void solved(Solution<T> solved);
}
