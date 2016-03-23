package com.silicolife.textmining.machinelearning.biotml.core.evaluation;

import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLEvaluation;

/**
 * 
 * Represents model evaluation scores.
 * 
 * @since 1.0.0
 * @author Ruben Rodrigues ({@code rrodrigues@silicolife.com})
 */

public class BioTMLEvaluation implements IBioTMLEvaluation{
	
	private float precision;
	private float recall;
	private float fscore;
	
	/**
	 * 
	 * Initializes the scores of model evaluation.
	 * 
	 * @param precision - Precision score.
	 * @param recall - Recall score.
	 * @param fscore - F-score.
	 */
	
	public BioTMLEvaluation(float precision, float recall, float fscore)
	{
		this.precision = precision;
		this.recall = recall;
		this.fscore = fscore;
	}

	public float getPrecision() {
		return precision;
	}

	public float getRecall() {
		return recall;
	}

	public float getFscore() {
		return fscore;
	}
}