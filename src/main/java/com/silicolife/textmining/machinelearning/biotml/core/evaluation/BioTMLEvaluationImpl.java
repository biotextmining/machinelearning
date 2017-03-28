package com.silicolife.textmining.machinelearning.biotml.core.evaluation;

import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLConfusionMatrix;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLEvaluation;

/**
 * 
 * Represents model evaluation scores.
 * 
 * @since 1.0.0
 * @author Ruben Rodrigues ({@code rrodrigues@silicolife.com})
 */

public class BioTMLEvaluationImpl implements IBioTMLEvaluation{
	
	private static final long serialVersionUID = 1L;
	private IBioTMLConfusionMatrix<?> confusionMatrix;
	private String evaluationDescription;
	private double precision;
	private double recall;
	private double fscore;
	
	
	/**
	 * 
	 * Initializes the scores of model evaluation.
	 * 
	 * @param precision - Precision score.
	 * @param recall - Recall score.
	 * @param fscore - F-score.
	 */
	
	public BioTMLEvaluationImpl(IBioTMLConfusionMatrix<?> confusionMatrix){
		this.confusionMatrix = confusionMatrix;
		this.precision = calculatePrecision(confusionMatrix);
		this.recall = calculateRecall(confusionMatrix);
		this.fscore = calculateFScore(precision, recall);
		this.evaluationDescription = new String();
	}

	public BioTMLEvaluationImpl(IBioTMLConfusionMatrix<?> confusionMatrix, String evaluationDescription){
		this(confusionMatrix);
		this.evaluationDescription = evaluationDescription;
	}
	
	@Override
	public IBioTMLConfusionMatrix<?> getConfusionMatrix() {
		return confusionMatrix;
	}

	@Override
	public double getPrecision() {
		return precision;
	}

	@Override
	public double getRecall() {
		return recall;
	}

	@Override
	public double getFscore() {
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
	
	private double calculatePrecision(IBioTMLConfusionMatrix<?> confusionMatrix){
		double tp = (double)confusionMatrix.getTruePositives().size();
		double fp = (double)confusionMatrix.getFalsePositives().size();
		if(tp == 0)
			return 0;
		return tp/(tp+fp);
	}
	
	
	private double calculateRecall(IBioTMLConfusionMatrix<?> confusionMatrix) {
		double tp = (double)confusionMatrix.getTruePositives().size();
		double fn = (double)confusionMatrix.getFalseNegatives().size();
		if(tp == 0)
			return 0;
		return tp/(tp+fn);
	}
	
	private double calculateFScore(double precision, double recall) {
		double dividend = precision * recall;
		if(dividend == 0)
			return 0;
		return 2*(dividend/(precision + recall));
	}
	
}