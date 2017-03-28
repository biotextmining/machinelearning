package com.silicolife.textmining.machinelearning.biotml.core.interfaces;

import java.io.Serializable;

/**
 * 
 * BioTML evaluation interface.
 * 
 * @since 1.0.0
 * @author Ruben Rodrigues ({@code rrodrigues@silicolife.com})
 */

public interface IBioTMLEvaluation extends Serializable{
	
	public IBioTMLConfusionMatrix<?> getConfusionMatrix();
	
	/**
	 * 
	 * Method to get the precision score associated with the evaluation.
	 * 
	 * @return Precision score.
	 */
	public double getPrecision();
	
	/**
	 * 
	 * Method to get the recall score associated with the evaluation.
	 * 
	 * @return Recall score.
	 */
	public double getRecall();
	
	/**
	 * 
	 * Method to get the f-score score associated with the evaluation.
	 * 
	 * @return F-score.
	 */
	public double getFscore();
	
	
	public String getEvaluationDescription();
	
}