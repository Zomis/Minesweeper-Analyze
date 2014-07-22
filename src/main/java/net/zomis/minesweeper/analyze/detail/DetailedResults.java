package net.zomis.minesweeper.analyze.detail;

import java.util.Collection;
import java.util.Map;

import net.zomis.minesweeper.analyze.AnalyzeResult;

public interface DetailedResults<T> {

	Collection<FieldProxy<T>> getProxies();

	int getProxyCount();

	FieldProxy<T> getProxyFor(T field);

	AnalyzeResult<T> getAnalyze();

	Map<T, FieldProxy<T>> getProxyMap();

}
