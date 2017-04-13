package com.silicolife.textmining.machinelearning.biotml.core.evaluation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.silicolife.textmining.machinelearning.biotml.core.evaluation.datastrucures.BioTMLMultiEvaluationImpl;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLAnnotation;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLEvaluation;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLMultiEvaluation;

public class BioTMLAnnotationEvaluator extends BioTMLEvaluator<IBioTMLAnnotation>{

	public BioTMLAnnotationEvaluator(){
		
	}
	
	public IBioTMLMultiEvaluation generatelEvaluationByType(Collection<IBioTMLAnnotation> goldAnnotations, Collection<IBioTMLAnnotation> toCompareAnnotations){
		Map<String, List<IBioTMLEvaluation>> multiEvaluations = new HashMap<>();
		
		Map<String, Collection<IBioTMLAnnotation>> goldAnnotationsMap = getAnnotationsByTypeMap(goldAnnotations);
		Map<String, Collection<IBioTMLAnnotation>> toCompareAnnotationsMap = getAnnotationsByTypeMap(toCompareAnnotations);
		Set<String> keyset = goldAnnotationsMap.keySet();
		keyset.addAll(toCompareAnnotationsMap.keySet());
		for(String key : keyset){
			if(!multiEvaluations.containsKey(key))
				multiEvaluations.put(key, new ArrayList<>());
			
			Collection<IBioTMLAnnotation> goldAnnotationsOnType = new ArrayList<>();
			Collection<IBioTMLAnnotation> toCompareAnnotationsOnType = new ArrayList<>();
			if(goldAnnotationsMap.containsKey(key))
				goldAnnotationsOnType = goldAnnotationsMap.get(key);
			if(toCompareAnnotationsMap.containsKey(key))
				toCompareAnnotationsOnType = toCompareAnnotationsMap.get(key);
			List<IBioTMLEvaluation> evaluations = multiEvaluations.get(key);
			evaluations.add(generateGeneralEvaluation(goldAnnotationsOnType, toCompareAnnotationsOnType));
			multiEvaluations.put(key, evaluations);
		}
		
		
		return new BioTMLMultiEvaluationImpl(multiEvaluations);
	}
	
	private Map<String, Collection<IBioTMLAnnotation>> getAnnotationsByTypeMap(Collection<IBioTMLAnnotation> annotations){
		Map<String, Collection<IBioTMLAnnotation>> map = new HashMap<>();
		for(IBioTMLAnnotation annotation : annotations){
			if(!map.containsKey(annotation.getAnnotType()))
				map.put(annotation.getAnnotType(), new ArrayList<>());
			
			Collection<IBioTMLAnnotation> annotationStored = map.get(annotation.getAnnotType());
			annotationStored.add(annotation);
			map.put(annotation.getAnnotType(),annotationStored);
		}
		
		return map;
	}
}
