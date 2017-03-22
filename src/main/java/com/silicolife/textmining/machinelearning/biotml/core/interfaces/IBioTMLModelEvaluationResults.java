package com.silicolife.textmining.machinelearning.biotml.core.interfaces;

import java.util.Map;

/**
 * 
 * BioTML model evaluation results interface.
 * 
 * @since 1.0.0
 * @author Ruben Rodrigues ({@code rrodrigues@silicolife.com})
 */

public interface IBioTMLModelEvaluationResults {
	
	/**
	 * 
	 * Method to get a map of type of evaluation as key and score results as value.
	 * 
	 * @return Map of evaluation type with {@link IBioTMLEvaluation} scores.
	 */
	public Map<String, IBioTMLMultiEvaluation> getResults();
	
	/**
	 * 
	 * Convert the map into a string of results.
	 * 
	 * @return String of results.
	 */
	public String printResults();
	
}
