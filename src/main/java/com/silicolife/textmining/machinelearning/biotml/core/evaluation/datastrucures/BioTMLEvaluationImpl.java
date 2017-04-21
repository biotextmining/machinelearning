package com.silicolife.textmining.machinelearning.biotml.core.evaluation.datastrucures;

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
	


	public BioTMLEvaluationImpl(IBioTMLConfusionMatrix<?> confusionMatrix){
		this.confusionMatrix = confusionMatrix;
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
	public double getPrecisionOfLabel(String label) {
		return calculatePrecisionOfLabel(label);
	}

	@Override
	public double getRecallOfLabel(String label) {
		return calculateRecall(label);
	}

	@Override
	public double getFscoreOfLabel(String label) {
		return calculateFScore(getPrecisionOfLabel(label), getRecallOfLabel(label));
	}

	@Override
	public String getEvaluationDescription() {
		return evaluationDescription;
	}
	
	@Override
	public String toString() {
		return "BioTMLEvaluationImpl [evaluationDescription=" + evaluationDescription + "]";
	}

	
	private double calculatePrecisionOfLabel(String label){
		double tp = (double)getConfusionMatrix().getTruePositivesOfLabel(label).size();
		double fp = (double)getConfusionMatrix().getFalsePositivesOfLabel(label).size();
		if(tp == 0)
			return 0;
		return tp/(tp+fp);
	}
	
	
	private double calculateRecall(String label) {
		double tp = (double)getConfusionMatrix().getTruePositivesOfLabel(label).size();
		double fn = (double)getConfusionMatrix().getFalseNegativesOfLabel(label).size();
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