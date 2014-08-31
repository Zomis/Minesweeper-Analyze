package net.zomis.minesweeper.analyze.detail;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import net.zomis.minesweeper.analyze.AnalyzeResult;

public class DetailedResultsImpl<T> implements DetailedResults<T> {

	private final AnalyzeResult<T> analyze;
	private final Map<T, FieldProxy<T>> proxies;
	private final int proxyCount;

	public DetailedResultsImpl(AnalyzeResult<T> analyze, Map<T, FieldProxy<T>> proxies, int proxyCount) {
		this.analyze = analyze;
		this.proxies = proxies;
		this.proxyCount = proxyCount;
	}
	
	@Override
	public Collection<FieldProxy<T>> getProxies() {
		return Collections.unmodifiableCollection(proxies.values());
	}
	
	@Override
	public int getProxyCount() {
		return proxyCount;
	}

	@Override
	public FieldProxy<T> getProxyFor(T field) {
		return proxies.get(field);
	}
	
	@Override
	public AnalyzeResult<T> getAnalyze() {
		return analyze;
	}
	
	@Override
	public Map<T, FieldProxy<T>> getProxyMap() {
		return Collections.unmodifiableMap(proxies);
	}
}
