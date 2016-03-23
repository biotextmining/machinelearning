package com.silicolife.textmining.machinelearning.biotml.core.interfaces;

/**
 * 
 * BioTML evaluation interface.
 * 
 * @since 1.0.0
 * @author Ruben Rodrigues ({@code rrodrigues@silicolife.com})
 */

public interface IBioTMLEvaluation {
	
	/**
	 * 
	 * Method to get the precision score associated with the evaluation.
	 * 
	 * @return Precision score.
	 */
	public float getPrecision();
	
	/**
	 * 
	 * Method to get the recall score associated with the evaluation.
	 * 
	 * @return Recall score.
	 */
	public float getRecall();
	
	/**
	 * 
	 * Method to get the f-score score associated with the evaluation.
	 * 
	 * @return F-score.
	 */
	public float getFscore();
	
}