package net.zomis.minesweeper.analyze.detail;

import java.util.Collection;

/**
 * Interface strategy for performing a {@link DetailAnalyze}
 * 
 * @author Simon Forsberg
 *
 * @param <T> The field type
 */
public interface NeighborFind<T> {
	/**
	 * Retrieve the neighbors for a specific field.
	 * 
	 * @param field Field to retrieve the neighbors for
	 * 
	 * @return A {@link Collection} of the neighbors that the specified field has
	 */
	Collection<T> getNeighborsFor(T field);
	
	/**
	 * Determine if a field is a found mine or not
	 * 
	 * @param field Field to check
	 * 
	 * @return True if the field is a found mine, false otherwise
	 */
	boolean isFoundAndisMine(T field);
}
