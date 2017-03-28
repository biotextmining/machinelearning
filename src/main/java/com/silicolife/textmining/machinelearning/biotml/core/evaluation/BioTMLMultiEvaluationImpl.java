package com.silicolife.textmining.machinelearning.biotml.core.evaluation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLConfusionMatrix;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLEvaluation;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLMultiEvaluation;

/**
 * 
 * Represents multi model evaluation scores.
 * 
 * @since 1.0.0
 * @author Ruben Rodrigues ({@code rrodrigues@silicolife.com})
 */

public class BioTMLMultiEvaluationImpl implements IBioTMLMultiEvaluation{
	
	private static final long serialVersionUID = 1L;
	private Map<String, List<IBioTMLEvaluation>> multiEvaluations;
	private Map<String, Double> averangePrecision;
	private Map<String, Double> averangeRecall;
	private Map<String, Double> averangeFscore;

	/**
	 * 
	 *  Initializes the multi model evaluation scores with a set of evaluations from a cross-evaluation process.
	 * 
	 * @param multiEvaluations - Set of {@link IBioTMLEvaluation}.
	 */
	
	public BioTMLMultiEvaluationImpl(Map<String, List<IBioTMLEvaluation>> multiEvaluations)
	{
		this.multiEvaluations = multiEvaluations;
		this.calculateScores();
	}
	
	public Map<String, List<IBioTMLEvaluation>> getMultiEvaluations(){
		return multiEvaluations;
	}

	@SuppressWarnings("rawtypes")
	private void calculateScores() {
		
		averangePrecision = new HashMap<>();
		averangeRecall = new HashMap<>();
		averangeFscore = new HashMap<>();
		
		for(String labelString : getMultiEvaluations().keySet()){
			List<IBioTMLConfusionMatrix> confusionMatrixes = new ArrayList<>();
			for(IBioTMLEvaluation evaluation : getMultiEvaluations().get(labelString))
				confusionMatrixes.add(evaluation.getConfusionMatrix());
			
			IBioTMLConfusionMatrix<Integer> confusionMatrixSizes = getConfusionMatrixSizesOfConfusionMatrixes(confusionMatrixes);
			
			double avgprecision = calculateAverangePrecision(confusionMatrixSizes);
			averangePrecision.put(labelString, avgprecision);
			double avgrecall = calculateAverangeRecall(confusionMatrixSizes);
			averangeRecall.put(labelString, avgrecall);
			double avgFscore = calculateAverangeFScore(avgprecision, avgrecall);
			averangeFscore.put(labelString, avgFscore);
			
		}

	}
	
	public Map<String, Double> getAverangePrecision(){
		return averangePrecision;
	}

	public Map<String, Double> getAverangeRecall() {
		return averangeRecall;
	}

	public Map<String, Double> getAverangeFscore() {
		return averangeFscore;
	}
	
	@SuppressWarnings("rawtypes")
	private IBioTMLConfusionMatrix<Integer> getConfusionMatrixSizesOfConfusionMatrixes(List<IBioTMLConfusionMatrix> confusionMatrixes){
		IBioTMLConfusionMatrix<Integer> sumConfusionMatrix = new BioTMLConfusionMatrixImpl<>();
		for(IBioTMLConfusionMatrix confusionMatrix : confusionMatrixes){
			sumConfusionMatrix.addTruePositive(confusionMatrix.getTruePositives().size());
			sumConfusionMatrix.addFalsePositive(confusionMatrix.getFalsePositives().size());
			sumConfusionMatrix.addTrueNegative(confusionMatrix.getTrueNegatives().size());
			sumConfusionMatrix.addFalseNegative(confusionMatrix.getFalseNegatives().size());
		}
		return sumConfusionMatrix;
	}
	
	private Integer sumList(List<Integer> listIntegerToSum){
		Integer sum = 0;
		for(Integer tosum : listIntegerToSum)
			sum = sum + tosum;
		return sum;
	}
	
	
	private double calculateAverangePrecision(IBioTMLConfusionMatrix<Integer> confusionMatrixSizes){
		double tp = (double)sumList(confusionMatrixSizes.getTruePositives());
		double fp = (double)sumList(confusionMatrixSizes.getFalsePositives());
		if(tp == 0)
			return 0;
		return tp/(tp+fp);
	}
	
	
	private double calculateAverangeRecall(IBioTMLConfusionMatrix<Integer> confusionMatrix) {
		double tp = (double)sumList(confusionMatrix.getTruePositives());
		double fn = (double)sumList(confusionMatrix.getFalseNegatives());
		if(tp == 0)
			return 0;
		return tp/(tp+fn);
	}
	
	private double calculateAverangeFScore(double averangeprecision, double averangeRecall) {
		double dividend = averangeprecision * averangeRecall;
		if(dividend == 0)
			return 0;
		return 2*(dividend/(averangeprecision + averangeRecall));
	}
}