package com.silicolife.textmining.machinelearning.biotml.core.evaluation;

import java.util.Map;

import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLEvaluation;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLModelEvaluationResults;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLMultiEvaluation;

/**
 * 
 * Represents model evaluation results.
 * 
 * @since 1.0.0
 * @author Ruben Rodrigues ({@code rrodrigues@silicolife.com})
 */

public class BioTMLModelEvaluationResultsImpl implements IBioTMLModelEvaluationResults{
	
	private Map<String, IBioTMLMultiEvaluation> evaluationResults;

	/**
	 * 
	 * Initializes the model evaluation results with a map of type of evaluation as key and score results as value.
	 * 
	 * @param evaluationResults - Map of evaluation type with {@link IBioTMLEvaluation} scores.
	 */
	
	public BioTMLModelEvaluationResultsImpl(Map<String, IBioTMLMultiEvaluation> evaluationResults){
		this.evaluationResults = evaluationResults;
	}

	public Map<String, IBioTMLMultiEvaluation> getResults() {
		return evaluationResults;
	}

	public String printResults() {
		String print = "\tModel Evaluation Results:\n\n";
		for(String res: getResults().keySet()){
			print = print +"\t" +res + "\n";
			IBioTMLMultiEvaluation result = getResults().get(res);
			print = print + "\tPrecision: " + String.format("%.2f", result.getMeanPrecision()*100) + "%\n\t" + "Recall: " + String.format("%.2f", result.getMeanRecall()*100) + "%\n\tF1-Score: " + String.format("%.2f", result.getMeanFscore()*100) + "%\n\n";
		}
		return print;
	}

	@Override
	public String toString() {
		return printResults();
	}

}
