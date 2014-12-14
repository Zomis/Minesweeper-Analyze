package net.zomis.minesweeper.analyze.detail;

import java.util.Collection;
import java.util.Map;

import net.zomis.minesweeper.analyze.AnalyzeResult;

/**
 * Interface for retreiving more detailed probabilities, for example 'What is the probability for a 4 on field x?'
 * 
 * @author Simon Forsberg
 *
 * @param <T> The field type
 */
public interface DetailedResults<T> {

	Collection<ProbabilityKnowledge<T>> getProxies();

	/**
	 * Get the number of unique proxies that was required for the calculation. As some can be re-used, this will always be lesser than or equal to <code>getProxyMap().size()</code>
	 * 
	 * @return The number of unique proxies
	 */
	int getProxyCount();

	/**
	 * Get the detailed probabilities for a field
	 * 
	 * @param field The field to get the information for
	 * @return An object containing detailed probability information for the chosen field
	 */
	ProbabilityKnowledge<T> getProxyFor(T field);

	/**
	 * Get the underlying analyze that these detailed results was based on
	 * 
	 * @return {@link AnalyzeResult} object that is the source of this analyze
	 */
	AnalyzeResult<T> getAnalyze();

	/**
	 * @return The map of all probability datas
	 */
	Map<T, ProbabilityKnowledge<T>> getProxyMap();

}
