package com.silicolife.textmining.machinelearning.biotml.core.evaluation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.silicolife.textmining.machinelearning.biotml.core.BioTMLConstants;
import com.silicolife.textmining.machinelearning.biotml.core.evaluation.datastrucures.BioTMLConfusionMatrixImpl;
import com.silicolife.textmining.machinelearning.biotml.core.evaluation.datastrucures.BioTMLEvaluationImpl;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLAnnotation;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLAssociation;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLConfusionMatrix;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLEntity;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLEvaluation;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLEvent;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLOffsetsPair;

public class BioTMLEvaluator<C extends IBioTMLAnnotation> {

	public BioTMLEvaluator(){
		
	}
	
	public IBioTMLEvaluation generateGeneralEvaluation(Collection<C> goldAnnotations, Collection<C> toCompareAnnotations){
		IBioTMLConfusionMatrix<C> confusionMatrix = generateConfusionMatrix(goldAnnotations, toCompareAnnotations);
		return new BioTMLEvaluationImpl(confusionMatrix);
	}
	
	public IBioTMLConfusionMatrix<C> generateConfusionMatrix(Collection<C> goldInstances, Collection<C> toCompareInstances){
		IBioTMLConfusionMatrix<C> confusionMatrix = new BioTMLConfusionMatrixImpl<>();
		
		//get instances common in both lists
		List<C> commonInstances = new ArrayList<>(goldInstances);
		commonInstances.retainAll(toCompareInstances);
		
		for(C commonInstance : commonInstances)
			confusionMatrix.addPrediction(commonInstance, commonInstance.getAnnotationType(), commonInstance.getAnnotationType());
		
		//get only in gold annotation instances
		List<C> onlyInGoldInstances = new ArrayList<>(goldInstances);
		onlyInGoldInstances.removeAll(commonInstances);
		
		//get only in to compare annotation instances
		List<C> onlyInToCompareInstances = new ArrayList<>(toCompareInstances);
		onlyInToCompareInstances.removeAll(commonInstances);
		
		for(C onlytoCompareInstance : onlyInToCompareInstances){
			C goldInstance = getGoldInstanceThatOverlapToCompare(onlyInGoldInstances, onlytoCompareInstance);
			if(goldInstance != null)
				confusionMatrix.addPrediction(onlytoCompareInstance, onlytoCompareInstance.getAnnotationType(), goldInstance.getAnnotationType());
			else
				confusionMatrix.addPrediction(onlytoCompareInstance, onlytoCompareInstance.getAnnotationType(), BioTMLConstants.o.toString());
		}
		
		for(C onlyInGoldInstance : onlyInGoldInstances)
			confusionMatrix.addPrediction(onlyInGoldInstance, BioTMLConstants.o.toString(), onlyInGoldInstance.getAnnotationType());
		
		return confusionMatrix;
	}
	
	private C getGoldInstanceThatOverlapToCompare(Collection<C> onlyInGoldInstances, C onlytoCompareInstance){
		Iterator<C> onlyInGoldit = onlyInGoldInstances.iterator();
		C foundGoldInstance = null;
		while(onlyInGoldit.hasNext() && foundGoldInstance == null){
			C onlyInGoldInstance = onlyInGoldit.next();
			if(overlapInstancesWithDifferentAnnotTypes(onlyInGoldInstance, onlytoCompareInstance))
				foundGoldInstance = onlyInGoldInstance;
			if(foundGoldInstance != null)
				onlyInGoldit.remove();
		}
		return foundGoldInstance;
	}
	
	private boolean overlapInstancesWithDifferentAnnotTypes(C goldInstance, C toCompareInstance){
		if(goldInstance instanceof IBioTMLEntity && toCompareInstance instanceof IBioTMLEntity){
			
			IBioTMLOffsetsPair goldInstanceOffset = ((IBioTMLEntity)goldInstance).getAnnotationOffsets();
			IBioTMLOffsetsPair toCompareInstanceOffset = ((IBioTMLEntity)toCompareInstance).getAnnotationOffsets();
			
			return goldInstanceOffset.equals(toCompareInstanceOffset);
			
		}else if(goldInstance instanceof IBioTMLEvent && toCompareInstance instanceof IBioTMLEvent){
			
			IBioTMLAssociation<?,?> goldInstanceAssociation = ((IBioTMLEvent)goldInstance).getAssociation();
			IBioTMLAssociation<?,?> toCompareInstanceAssociation = ((IBioTMLEvent)toCompareInstance).getAssociation();
			
			return goldInstanceAssociation.equals(toCompareInstanceAssociation);
		}
		return false;
	}

	
}
