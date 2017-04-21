package com.silicolife.textmining.machinelearning.biotml.core.evaluation.datastrucures;

import java.util.List;
import java.util.Map;

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

	/**
	 * 
	 *  Initializes the multi model evaluation scores with a set of evaluations from a cross-evaluation process.
	 * 
	 * @param multiEvaluations - Set of {@link IBioTMLEvaluation}.
	 */
	
	public BioTMLMultiEvaluationImpl(Map<String, List<IBioTMLEvaluation>> multiEvaluations){
		this.multiEvaluations = multiEvaluations;
	}
	
	public Map<String, List<IBioTMLEvaluation>> getMultiEvaluations(){
		return multiEvaluations;
	}

	@Override
	public String toString() {	
		StringBuilder sb = new StringBuilder();
		
		for(String key : getMultiEvaluations().keySet()){
			sb.append(key+"\n");
			List<IBioTMLEvaluation> evaluations = getMultiEvaluations().get(key);
			for(IBioTMLEvaluation evaluation : evaluations){
				sb.append(evaluation.getEvaluationDescription()+"\n");
				sb.append(evaluation.getConfusionMatrix()+"\n");
			}
		}
		
		return sb.toString();
	}
	
	

}