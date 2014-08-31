package net.zomis.minesweeper.analyze.detail;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import net.zomis.minesweeper.analyze.AnalyzeResult;

public class DetailedResultsImpl<T> implements DetailedResults<T> {

	private final AnalyzeResult<T> analyze;
	private final Map<T, ProbabilityKnowledge<T>> proxies;
	private final int proxyCount;

	public DetailedResultsImpl(AnalyzeResult<T> analyze, Map<T, FieldProxy<T>> proxies, int proxyCount) {
		this.analyze = analyze;
		this.proxies = Collections.unmodifiableMap(new HashMap<T, ProbabilityKnowledge<T>>(proxies));
		this.proxyCount = proxyCount;
	}
	
	@Override
	public Collection<ProbabilityKnowledge<T>> getProxies() {
		return Collections.unmodifiableCollection(proxies.values());
	}
	
	@Override
	public int getProxyCount() {
		return proxyCount;
	}

	@Override
	public ProbabilityKnowledge<T> getProxyFor(T field) {
		return proxies.get(field);
	}
	
	@Override
	public AnalyzeResult<T> getAnalyze() {
		return analyze;
	}
	
	@Override
	public Map<T, ProbabilityKnowledge<T>> getProxyMap() {
		return Collections.unmodifiableMap(proxies);
	}
}
