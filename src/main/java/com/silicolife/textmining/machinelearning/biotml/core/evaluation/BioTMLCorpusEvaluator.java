package com.silicolife.textmining.machinelearning.biotml.core.evaluation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.silicolife.textmining.machinelearning.biotml.core.exception.BioTMLException;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLAnnotation;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLConfusionMatrix;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLCorpus;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLDocument;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLEvaluation;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLEvent;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLMultiEvaluation;

public class BioTMLCorpusEvaluator {

	public BioTMLCorpusEvaluator(){

	}

	public IBioTMLMultiEvaluation evaluateNERForSameExternalIDs(IBioTMLCorpus goldStandard, IBioTMLCorpus toCompare){
		Set<IBioTMLAnnotation> onlyToCompareAnnotations = new HashSet<>();
		Set<IBioTMLAnnotation> onlyGoldStandardAnnotations = new HashSet<>();
		Set<IBioTMLAnnotation> inBothCorpusAnnotations = new HashSet<>();
		fillNERAnnotationSets(goldStandard, toCompare, onlyToCompareAnnotations, onlyGoldStandardAnnotations, inBothCorpusAnnotations);

		Map<String, List<IBioTMLAnnotation>> onlyToCompareMap = countByAnnotationType(onlyToCompareAnnotations);
		Map<String, List<IBioTMLAnnotation>> onlyGoldStandardMap = countByAnnotationType(onlyGoldStandardAnnotations);
		Map<String, List<IBioTMLAnnotation>> inBothCorpusMap = countByAnnotationType(inBothCorpusAnnotations);
		Set<String> keySet = new HashSet<>();
//		keySet.addAll(onlyToCompareMap.keySet());
		keySet.addAll(onlyGoldStandardMap.keySet());
		keySet.addAll(inBothCorpusMap.keySet());

		Map<String, List<IBioTMLEvaluation>> evaluations = new HashMap<>();
		for(String annotType : keySet){
			IBioTMLConfusionMatrix<IBioTMLAnnotation> confusionMatrix = new BioTMLConfusionMatrixImpl<>();
			List<IBioTMLAnnotation> onlyToCompareSize = new ArrayList<>();
			List<IBioTMLAnnotation> onlyGoldStandardSize = new ArrayList<>();
			List<IBioTMLAnnotation> inBothCorpusSize = new ArrayList<>();
			if(onlyToCompareMap.containsKey(annotType)){
				onlyToCompareSize = onlyToCompareMap.get(annotType);
			}
			if(onlyGoldStandardMap.containsKey(annotType)){
				onlyGoldStandardSize = onlyGoldStandardMap.get(annotType);
			}
			if(inBothCorpusMap.containsKey(annotType)){
				inBothCorpusSize = inBothCorpusMap.get(annotType);
			}
			confusionMatrix.addAllTruePositives(inBothCorpusSize);
			confusionMatrix.addAllFalsePositives(onlyToCompareSize);
			confusionMatrix.addAllFalseNegatives(onlyGoldStandardSize);
			
			if(!evaluations.containsKey(annotType))
				evaluations.put(annotType, new ArrayList<IBioTMLEvaluation>());
			List<IBioTMLEvaluation> eval = evaluations.get(annotType);
			eval.add(new BioTMLEvaluationImpl(confusionMatrix));
			evaluations.put(annotType, eval);
		}

		return new BioTMLMultiEvaluationImpl(evaluations);
	}

	public IBioTMLMultiEvaluation evaluateREForSameExternalIDs(IBioTMLCorpus goldStandard, IBioTMLCorpus toCompare){
		Set<IBioTMLEvent> onlyToCompareRelations = new HashSet<>();
		Set<IBioTMLEvent> onlyGoldStandardRelations = new HashSet<>();
		Set<IBioTMLEvent> inBothCorpusRelations = new HashSet<>();
		fillREAnnotationSets(goldStandard, toCompare, onlyToCompareRelations, onlyGoldStandardRelations, inBothCorpusRelations);
		
		Map<String, List<IBioTMLEvent>> onlyToCompareMap = countByEventType(onlyToCompareRelations);
		Map<String, List<IBioTMLEvent>> onlyGoldStandardMap = countByEventType(onlyGoldStandardRelations);
		Map<String, List<IBioTMLEvent>> inBothCorpusMap = countByEventType(inBothCorpusRelations);
		
		Set<String> keySet = new HashSet<>();
//		keySet.addAll(onlyToCompareMap.keySet());
		keySet.addAll(onlyGoldStandardMap.keySet());
		keySet.addAll(inBothCorpusMap.keySet());

		Map<String, List<IBioTMLEvaluation>> evaluations = new HashMap<>();
		for(String annotType : keySet){
			IBioTMLConfusionMatrix<IBioTMLEvent> confusionMatrix = new BioTMLConfusionMatrixImpl<>();
			List<IBioTMLEvent> onlyToCompareSize = new ArrayList<>();
			List<IBioTMLEvent> onlyGoldStandardSize = new ArrayList<>();
			List<IBioTMLEvent> inBothCorpusSize = new ArrayList<>();
			if(onlyToCompareMap.containsKey(annotType)){
				onlyToCompareSize = onlyToCompareMap.get(annotType);
			}
			if(onlyGoldStandardMap.containsKey(annotType)){
				onlyGoldStandardSize = onlyGoldStandardMap.get(annotType);
			}
			if(inBothCorpusMap.containsKey(annotType)){
				inBothCorpusSize = inBothCorpusMap.get(annotType);
			}
			confusionMatrix.addAllTruePositives(inBothCorpusSize);
			confusionMatrix.addAllFalsePositives(onlyToCompareSize);
			confusionMatrix.addAllFalseNegatives(onlyGoldStandardSize);
			
			if(!evaluations.containsKey(annotType))
				evaluations.put(annotType, new ArrayList<IBioTMLEvaluation>());
			List<IBioTMLEvaluation> eval = evaluations.get(annotType);
			eval.add(new BioTMLEvaluationImpl(confusionMatrix));
			evaluations.put(annotType, eval);
		}
		return new BioTMLMultiEvaluationImpl(evaluations);
	}

	private void fillREAnnotationSets(IBioTMLCorpus goldStandard, IBioTMLCorpus toCompare,
			Set<IBioTMLEvent> onlyToCompareRelations,
			Set<IBioTMLEvent> onlyGoldStandardRelations,
			Set<IBioTMLEvent> inBothCorpusRelations) {
		for( IBioTMLDocument goldDoc : goldStandard.getDocuments()){
			IBioTMLDocument toCompareDoc = null;
			String externalID = goldDoc.getExternalID();
			try {
				toCompareDoc = toCompare.getDocumentByExternalID(externalID);
			} catch (BioTMLException e) {};
			if(toCompareDoc != null){
				Set<IBioTMLEvent> goldRels = goldStandard.getDocAnnotationEvents(goldDoc.getID());
				Set<IBioTMLEvent> toCompareRels =  toCompare.getDocAnnotationEvents(toCompareDoc.getID());
				for(IBioTMLEvent goldRel : goldRels){
					boolean found = findGoldRelationInToCompareDocument(inBothCorpusRelations,toCompareRels, goldRel);
					if(!found){
						onlyGoldStandardRelations.add(goldRel);
					}
				}
				onlyToCompareRelations.addAll(toCompareRels);
			}
		}
	}

	private Map<String, List<IBioTMLAnnotation>> countByAnnotationType(Set<IBioTMLAnnotation> annotations){
		Map<String, List<IBioTMLAnnotation>> map = new HashMap<>();
		for(IBioTMLAnnotation annotation :annotations){
			if(!map.containsKey(annotation.getAnnotType())){
				map.put(annotation.getAnnotType(), new ArrayList<IBioTMLAnnotation>());
			}
			List<IBioTMLAnnotation> countedAnnotations = map.get(annotation.getAnnotType());
			countedAnnotations.add(annotation);
			map.put(annotation.getAnnotType(), countedAnnotations);
		}
		return map;
	}
	
	private Map<String, List<IBioTMLEvent>> countByEventType(Set<IBioTMLEvent> events){
		Map<String, List<IBioTMLEvent>> map = new HashMap<>();
		for(IBioTMLEvent event : events){
			if(!map.containsKey(event.getEventType())){
				map.put(event.getEventType(), new ArrayList<IBioTMLEvent>());
			}
			List<IBioTMLEvent> countedevents = map.get(event.getEventType());
			countedevents.add(event);
			map.put(event.getEventType(), countedevents);
		}
		return map;
	}

	private void fillNERAnnotationSets(IBioTMLCorpus goldStandard, IBioTMLCorpus toCompare,
			Set<IBioTMLAnnotation> onlyToCompareAnnotations, Set<IBioTMLAnnotation> onlyGoldStandardAnnotations,
			Set<IBioTMLAnnotation> inBothCorpusAnnotations) {
		for( IBioTMLDocument goldDoc : goldStandard.getDocuments()){
			IBioTMLDocument toCompareDoc = null;
			String externalID = goldDoc.getExternalID();
			try {
				toCompareDoc = toCompare.getDocumentByExternalID(externalID);
			} catch (BioTMLException e) {};
			if(toCompareDoc != null){
				List<IBioTMLAnnotation> goldAnnots = goldStandard.getDocAnnotations(goldDoc.getID());
				List<IBioTMLAnnotation> toCompareAnnots = toCompare.getDocAnnotations(toCompareDoc.getID());
				for(IBioTMLAnnotation goldAnnot : goldAnnots){
					boolean found = findGoldAnnotationInToCompareDocument(inBothCorpusAnnotations, toCompareAnnots, goldAnnot);
					if(!found){
						onlyGoldStandardAnnotations.add(goldAnnot);
					}
				}
				onlyToCompareAnnotations.addAll(toCompareAnnots);
			}
		}
	}

	private boolean findGoldRelationInToCompareDocument(Set<IBioTMLEvent> inBothCorpusRelations,
			Set<IBioTMLEvent> toCompareRelations, IBioTMLEvent goldRel){
		for(IBioTMLEvent toCompareRelation : toCompareRelations){
			if(goldRel.equals(toCompareRelation)){
				inBothCorpusRelations.add(goldRel);
				toCompareRelations.remove(toCompareRelation);
				return true;
			}
		}
		return false;
	}

	private boolean findGoldAnnotationInToCompareDocument(Set<IBioTMLAnnotation> inBothCorpusAnnotations,
			List<IBioTMLAnnotation> toCompareAnnots, IBioTMLAnnotation goldAnnot) {
		boolean found = false;
		int i = 0;
		while(i<toCompareAnnots.size() && !found){
			IBioTMLAnnotation toCompareAnnot = toCompareAnnots.get(i);
			if(goldAnnot.getAnnotationOffsets().equals(toCompareAnnot.getAnnotationOffsets())){
				if(goldAnnot.getAnnotType().equals(toCompareAnnot.getAnnotType())){
					found = true;
					inBothCorpusAnnotations.add(goldAnnot);
					toCompareAnnots.remove(i);
				}
			}
			i++;
		}
		return found;
	}

}
