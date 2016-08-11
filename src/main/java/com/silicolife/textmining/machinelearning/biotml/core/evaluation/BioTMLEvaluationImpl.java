package com.silicolife.textmining.machinelearning.biotml.core.evaluation;

import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLEvaluation;

/**
 * 
 * Represents model evaluation scores.
 * 
 * @since 1.0.0
 * @author Ruben Rodrigues ({@code rrodrigues@silicolife.com})
 */

public class BioTMLEvaluationImpl implements IBioTMLEvaluation{
	
	private float precision;
	private float recall;
	private float fscore;
	private String evaluationDescription;
	
	/**
	 * 
	 * Initializes the scores of model evaluation.
	 * 
	 * @param precision - Precision score.
	 * @param recall - Recall score.
	 * @param fscore - F-score.
	 */
	
	public BioTMLEvaluationImpl(float precision, float recall, float fscore){
		this.precision = precision;
		this.recall = recall;
		this.fscore = fscore;
		this.evaluationDescription = new String();
	}
	
	public BioTMLEvaluationImpl(float precision, float recall, float fscore, String evaluationDescription){
		this(precision, recall, fscore);
		this.evaluationDescription = evaluationDescription;
	}

	@Override
	public float getPrecision() {
		return precision;
	}

	@Override
	public float getRecall() {
		return recall;
	}

	@Override
	public float getFscore() {
		return fscore;
	}

	@Override
	public String getEvaluationDescription() {
		return evaluationDescription;
	}

	@Override
	public String toString() {
		return "BioTMLEvaluation [precision=" + precision + ", recall=" + recall + ", fscore=" + fscore
				+ ", evaluationDescription=" + evaluationDescription + "]";
	}
	
	
}