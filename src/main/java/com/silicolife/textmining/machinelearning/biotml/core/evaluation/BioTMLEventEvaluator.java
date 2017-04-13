package com.silicolife.textmining.machinelearning.biotml.core.evaluation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.silicolife.textmining.machinelearning.biotml.core.evaluation.datastrucures.BioTMLMultiEvaluationImpl;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLEvaluation;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLEvent;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLMultiEvaluation;

public class BioTMLEventEvaluator extends BioTMLEvaluator<IBioTMLEvent>{

	public IBioTMLMultiEvaluation generatelEvaluationByType(Collection<IBioTMLEvent> goldEvents, Collection<IBioTMLEvent> toCompareEvents){
		Map<String, List<IBioTMLEvaluation>> multiEvaluations = new HashMap<>();
		
		Map<String, Collection<IBioTMLEvent>> goldEventsMap = getEventsByTypeMap(goldEvents);
		Map<String, Collection<IBioTMLEvent>> toCompareEventsMap = getEventsByTypeMap(toCompareEvents);
		Set<String> keyset = goldEventsMap.keySet();
		keyset.addAll(toCompareEventsMap.keySet());
		for(String key : keyset){
			if(!multiEvaluations.containsKey(key))
				multiEvaluations.put(key, new ArrayList<>());
			
			Collection<IBioTMLEvent> goldEventsOnType = new ArrayList<>();
			Collection<IBioTMLEvent> toCompareEventsOnType = new ArrayList<>();
			if(goldEventsMap.containsKey(key))
				goldEventsOnType = goldEventsMap.get(key);
			if(toCompareEventsMap.containsKey(key))
				toCompareEventsOnType = toCompareEventsMap.get(key);
			List<IBioTMLEvaluation> evaluations = multiEvaluations.get(key);
			evaluations.add(generateGeneralEvaluation(goldEventsOnType, toCompareEventsOnType));
			multiEvaluations.put(key, evaluations);
		}
		
		
		return new BioTMLMultiEvaluationImpl(multiEvaluations);
	}
	
	private Map<String, Collection<IBioTMLEvent>> getEventsByTypeMap(Collection<IBioTMLEvent> events){
		Map<String, Collection<IBioTMLEvent>> map = new HashMap<>();
		for(IBioTMLEvent event : events){
			if(!map.containsKey(event.getEventType()))
				map.put(event.getEventType(), new ArrayList<>());
			
			Collection<IBioTMLEvent> annotationStored = map.get(event.getEventType());
			annotationStored.add(event);
			map.put(event.getEventType(),annotationStored);
		}
		
		return map;
	}
	
}
