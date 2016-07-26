package com.silicolife.textmining.machinelearning.biotml.core.evaluation;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLEvaluation;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLMultiEvaluation;

/**
 * 
 * Represents multi model evaluation scores.
 * 
 * @since 1.0.0
 * @author Ruben Rodrigues ({@code rrodrigues@silicolife.com})
 */

public class BioTMLMultiEvaluation implements IBioTMLMultiEvaluation{
	
	private Set<IBioTMLEvaluation> multiEvaluations;
	private float meanPrecision;
	private float meanRecall;
	private float meanFscore;

	/**
	 * 
	 *  Initializes the multi model evaluation scores with a set of evaluations from a cross-evaluation process.
	 * 
	 * @param multiEvaluations - Set of {@link IBioTMLEvaluation}.
	 */
	
	public BioTMLMultiEvaluation(Set<IBioTMLEvaluation> multiEvaluations)
	{
		this.multiEvaluations = multiEvaluations;
		this.calculateScores();
	}
	
	public Set<IBioTMLEvaluation> getMultiEvaluations(){
		return multiEvaluations;
	}

	private void calculateScores() {
		Set<Float> precisions = new HashSet<Float>();
		Set<Float> recalls = new HashSet<Float>();
		Set<Float> f1s = new HashSet<Float>();
		Iterator<IBioTMLEvaluation> evalIt = getMultiEvaluations().iterator();
		while(evalIt.hasNext()){
			IBioTMLEvaluation eval = evalIt.next();
			precisions.add(eval.getPrecision());
			recalls.add(eval.getRecall());
			f1s.add(eval.getFscore());
		}
		this.meanPrecision = calculateMean(precisions);
		this.meanRecall = calculateMean(recalls);
		this.meanFscore = calculateMean(f1s);
	}
	
	private float calculateMean(Set<Float> setValues){
		Iterator<Float> valIt = setValues.iterator();
		Float total = null;
		while(valIt.hasNext()){
			if(total == null)
				total = valIt.next();
			else
				total = total + valIt.next();
		}
		if(total == null){
			return 0;
		}
		return total/(float)setValues.size();
	}
	
	public float getMeanPrecision(){
		return meanPrecision;
	}

	public float getMeanRecall() {
		return meanRecall;
	}

	public float getMeanFscore() {
		return meanFscore;
	}
}