package com.silicolife.textmining.machinelearning.biotml.core.evaluation;

import java.util.Collection;

import com.silicolife.textmining.machinelearning.biotml.core.evaluation.datastrucures.BioTMLConfusionMatrixImpl;
import com.silicolife.textmining.machinelearning.biotml.core.evaluation.datastrucures.BioTMLEvaluationImpl;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLConfusionMatrix;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLEvaluation;

public abstract class BioTMLEvaluator<C> {

	
	public IBioTMLConfusionMatrix<C> generateConfusionMatrix(Collection<C> goldInstances, Collection<C> toCompareInstances){
		IBioTMLConfusionMatrix<C> confusionMatrix = new BioTMLConfusionMatrixImpl<>();
		for(C toCompareAnnotation : toCompareInstances){
			if(goldInstances.contains(toCompareAnnotation)){
				confusionMatrix.addTruePositive(toCompareAnnotation);
				goldInstances.remove(toCompareAnnotation);
			}else{
				confusionMatrix.addFalsePositive(toCompareAnnotation);
			}
		}
		confusionMatrix.addAllFalseNegatives(goldInstances);
		return confusionMatrix;
	}
	
	public IBioTMLEvaluation generateGeneralEvaluation(Collection<C> goldAnnotations, Collection<C> toCompareAnnotations){
		IBioTMLConfusionMatrix<C> confusionMatrix = generateConfusionMatrix(goldAnnotations, toCompareAnnotations);
		return new BioTMLEvaluationImpl(confusionMatrix);
	}
	
}
