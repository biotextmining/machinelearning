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
	
	/**
	 * 
	 * Method to get the precision Averange from all evaluations. 
	 * 
	 * @return Mean precision.
	 */
	public Map<String, Double> getAverangePrecision();
	
	/**
	 * 
	 * Method to get the recall Averange from all evaluations. 
	 * 
	 * @return Mean recall.
	 */
	public Map<String, Double> getAverangeRecall();
	
	/**
	 * 
	 * Method to get the f-score Averange from all evaluations. 
	 * 
	 * @return Mean f-score.
	 */
	public Map<String, Double> getAverangeFscore();

}