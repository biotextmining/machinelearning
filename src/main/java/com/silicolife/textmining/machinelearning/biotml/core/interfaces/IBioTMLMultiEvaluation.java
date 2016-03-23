package com.silicolife.textmining.machinelearning.biotml.core.interfaces;

import java.util.Set;

/**
 * 
 * BioTML multi evaluation results interface.
 * 
 * @since 1.0.0
 * @author Ruben Rodrigues ({@code rrodrigues@silicolife.com})
 */

public interface IBioTMLMultiEvaluation {
	
	/**
	 * 
	 * Method to get a set of evaluations from a cross-evaluation process.
	 * 
	 * @return Set of {@link IBioTMLEvaluation}.
	 */
	public Set<IBioTMLEvaluation> getMultiEvaluations();
	
	/**
	 * 
	 * Method to get the precision mean from all evaluations. 
	 * 
	 * @return Mean precision.
	 */
	public float getMeanPrecision();
	
	/**
	 * 
	 * Method to get the recall mean from all evaluations. 
	 * 
	 * @return Mean recall.
	 */
	public float getMeanRecall();
	
	/**
	 * 
	 * Method to get the f-score mean from all evaluations. 
	 * 
	 * @return Mean f-score.
	 */
	public float getMeanFscore();

}