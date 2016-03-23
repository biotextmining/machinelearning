package com.silicolife.textmining.machinelearning.biotml.core.evaluation;

import java.util.Map;

import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLEvaluation;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLModelEvaluationResults;

/**
 * 
 * Represents model evaluation results.
 * 
 * @since 1.0.0
 * @author Ruben Rodrigues ({@code rrodrigues@silicolife.com})
 */

public class BioTMLModelEvaluationResults implements IBioTMLModelEvaluationResults{
	
	private Map<String, IBioTMLEvaluation> evaluationResults;

	/**
	 * 
	 * Initializes the model evaluation results with a map of type of evaluation as key and score results as value.
	 * 
	 * @param evaluationResults - Map of evaluation type with {@link IBioTMLEvaluation} scores.
	 */
	
	public BioTMLModelEvaluationResults(Map<String, IBioTMLEvaluation> evaluationResults){
		this.evaluationResults = evaluationResults;
	}

	public Map<String, IBioTMLEvaluation> getResults() {
		return evaluationResults;
	}

	public String printResults() {
		String print = "\tModel Evaluation Results:\n\n";
		for(String res: getResults().keySet()){
			print = print +"\t" +res + "\n";
			IBioTMLEvaluation result = getResults().get(res);
			print = print + "\tPrecision: " + String.format("%.2f", result.getPrecision()*100) + "%\n\t" + "Recall: " + String.format("%.2f", result.getRecall()*100) + "%\n\tF1-Score: " + String.format("%.2f", result.getFscore()*100) + "%\n\n";
		}
		return print;
	}

}
