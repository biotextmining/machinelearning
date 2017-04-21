package com.silicolife.textmining.machinelearning.biotml.core.interfaces;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 
 * BioTML multi evaluation results interface.
 * 
 * @since 1.0.0
 * @author Ruben Rodrigues ({@code rrodrigues@silicolife.com})
 */

public interface IBioTMLMultiEvaluation extends Serializable{
	
	/**
	 * 
	 * Method to get a set of evaluations from a cross-evaluation process.
	 * 
	 * @return List of {@link IBioTMLEvaluation}.
	 */
	public Map<String, List<IBioTMLEvaluation>> getMultiEvaluations();

}