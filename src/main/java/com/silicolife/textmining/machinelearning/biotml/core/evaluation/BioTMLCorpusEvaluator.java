package com.silicolife.textmining.machinelearning.biotml.core.evaluation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.silicolife.textmining.machinelearning.biotml.core.exception.BioTMLException;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLAnnotation;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLAnnotationsRelation;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLCorpus;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLDocument;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLEvaluation;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLMultiEvaluation;

public class BioTMLCorpusEvaluator {

	public BioTMLCorpusEvaluator(){

	}

	public IBioTMLMultiEvaluation evaluateNERForSameExternalIDs(IBioTMLCorpus goldStandard, IBioTMLCorpus toCompare){
		Set<IBioTMLAnnotation> onlyToCompareAnnotations = new HashSet<>();
		Set<IBioTMLAnnotation> onlyGoldStandardAnnotations = new HashSet<>();
		Set<IBioTMLAnnotation> inBothCorpusAnnotations = new HashSet<>();
		fillNERAnnotationSets(goldStandard, toCompare, onlyToCompareAnnotations, onlyGoldStandardAnnotations, inBothCorpusAnnotations);

		Map<String, Integer> onlyToCompareMap = countByAnnotationType(onlyToCompareAnnotations);
		Map<String, Integer> onlyGoldStandardMap = countByAnnotationType(onlyGoldStandardAnnotations);
		Map<String, Integer> inBothCorpusMap = countByAnnotationType(inBothCorpusAnnotations);
		Set<String> keySet = new HashSet<>();
		keySet.addAll(onlyToCompareMap.keySet());
		keySet.addAll(onlyGoldStandardMap.keySet());
		keySet.addAll(inBothCorpusMap.keySet());

		Set<IBioTMLEvaluation> evaluations = calculateEvaluationsByAnnotType(onlyToCompareMap, onlyGoldStandardMap, inBothCorpusMap, keySet);

		return new BioTMLMultiEvaluation(evaluations);
	}

	public IBioTMLMultiEvaluation evaluateREForSameExternalIDs(IBioTMLCorpus goldStandard, IBioTMLCorpus toCompare){
		Set<IBioTMLAnnotationsRelation> onlyToCompareRelations = new HashSet<>();
		Set<IBioTMLAnnotationsRelation> onlyGoldStandardRelations = new HashSet<>();
		Set<IBioTMLAnnotationsRelation> inBothCorpusRelations = new HashSet<>();
		fillREAnnotationSets(goldStandard, toCompare, onlyToCompareRelations, onlyGoldStandardRelations, inBothCorpusRelations);
		
		Map<String, Integer> onlyToCompareMap = countByRelationType(onlyToCompareRelations);
		Map<String, Integer> onlyGoldStandardMap = countByRelationType(onlyGoldStandardRelations);
		Map<String, Integer> inBothCorpusMap = countByRelationType(inBothCorpusRelations);
		
		Set<String> keySet = new HashSet<>();
		keySet.addAll(onlyToCompareMap.keySet());
		keySet.addAll(onlyGoldStandardMap.keySet());
		keySet.addAll(inBothCorpusMap.keySet());

		Set<IBioTMLEvaluation> evaluations = calculateEvaluationsByAnnotType(onlyToCompareMap, onlyGoldStandardMap, inBothCorpusMap, keySet);;
		return new BioTMLMultiEvaluation(evaluations);
	}

	private void fillREAnnotationSets(IBioTMLCorpus goldStandard, IBioTMLCorpus toCompare,
			Set<IBioTMLAnnotationsRelation> onlyToCompareRelations,
			Set<IBioTMLAnnotationsRelation> onlyGoldStandardRelations,
			Set<IBioTMLAnnotationsRelation> inBothCorpusRelations) {
		for( IBioTMLDocument goldDoc : goldStandard.getDocuments()){
			IBioTMLDocument toCompareDoc = null;
			String externalID = goldDoc.getExternalID();
			try {
				toCompareDoc = toCompare.getDocumentByExternalID(externalID);
			} catch (BioTMLException e) {};
			if(toCompareDoc != null){
				Set<IBioTMLAnnotationsRelation> goldRels = goldStandard.getDocAnnotationRelations(goldDoc.getID());
				List<IBioTMLAnnotationsRelation> toCompareRels =  new ArrayList<>(toCompare.getDocAnnotationRelations(toCompareDoc.getID()));
				for(IBioTMLAnnotationsRelation goldRel : goldRels){
					boolean found = findGoldRelationInToCompareDocument(inBothCorpusRelations,toCompareRels, goldRel);
					if(!found){
						onlyGoldStandardRelations.add(goldRel);
					}
				}
				onlyToCompareRelations.addAll(toCompareRels);
			}
		}
	}


	private Set<IBioTMLEvaluation> calculateEvaluationsByAnnotType(Map<String, Integer> onlyToCompareMap,
			Map<String, Integer> onlyGoldStandardMap, Map<String, Integer> inBothCorpusMap, Set<String> keySet) {
		Set<IBioTMLEvaluation> evaluationsPerAnnotType = new HashSet<>();
		for(String annotType : keySet){
			int onlyToCompareSize = 0;
			int onlyGoldStandardSize = 0;
			int inBothCorpusSize = 0;
			if(onlyToCompareMap.containsKey(annotType)){
				onlyToCompareSize = onlyToCompareMap.get(annotType);
			}
			if(onlyGoldStandardMap.containsKey(annotType)){
				onlyGoldStandardSize = onlyGoldStandardMap.get(annotType);
			}
			if(inBothCorpusMap.containsKey(annotType)){
				inBothCorpusSize = inBothCorpusMap.get(annotType);
			}
			float precision = calculatePrecision(onlyToCompareSize, inBothCorpusSize);
			float recall = calculateRecall(onlyGoldStandardSize, inBothCorpusSize);
			float fscore = calculateFScore(onlyToCompareSize, onlyGoldStandardSize, inBothCorpusSize);
			evaluationsPerAnnotType.add(new BioTMLEvaluation(precision, recall, fscore, annotType));
		}
		return evaluationsPerAnnotType;
	}

	private Map<String, Integer> countByAnnotationType(Set<IBioTMLAnnotation> annotations){
		Map<String, Integer> map = new HashMap<>();
		for(IBioTMLAnnotation annotation :annotations){
			if(!map.containsKey(annotation.getAnnotType())){
				map.put(annotation.getAnnotType(), 0);
			}
			Integer count = map.get(annotation.getAnnotType());
			map.put(annotation.getAnnotType(), count+1);
		}
		return map;
	}
	
	private Map<String, Integer> countByRelationType(Set<IBioTMLAnnotationsRelation> relations){
		Map<String, Integer> map = new HashMap<>();
		for(IBioTMLAnnotationsRelation relation : relations){
			if(!map.containsKey(relation.getRelationType())){
				map.put(relation.getRelationType(), 0);
			}
			Integer count = map.get(relation.getRelationType());
			map.put(relation.getRelationType(), count+1);
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

	private boolean findGoldRelationInToCompareDocument(Set<IBioTMLAnnotationsRelation> inBothCorpusRelations,
			List<IBioTMLAnnotationsRelation> toCompareRelations, IBioTMLAnnotationsRelation goldRel){
		boolean found = false;
		int i = 0;
		while(i<toCompareRelations.size() && !found){
			IBioTMLAnnotationsRelation toCompareRelation = toCompareRelations.get(i);
			if(goldRel.haveTheSameOffsetsAndAnnotationTypes(toCompareRelation)){
				found = true;
				inBothCorpusRelations.add(goldRel);
				toCompareRelations.remove(i);
			}
			i++;
		}
		return found;
	}

	private boolean findGoldAnnotationInToCompareDocument(Set<IBioTMLAnnotation> inBothCorpusAnnotations,
			List<IBioTMLAnnotation> toCompareAnnots, IBioTMLAnnotation goldAnnot) {
		boolean found = false;
		int i = 0;
		while(i<toCompareAnnots.size() && !found){
			IBioTMLAnnotation toCompareAnnot = toCompareAnnots.get(i);
			if(goldAnnot.haveTheSameOffsets(toCompareAnnot)){
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

	private float calculatePrecision( int onlyToCompareAnnotSize, int inBothCorpusAnnotSize){
		if ( (inBothCorpusAnnotSize + onlyToCompareAnnotSize) == 0) {
			return 0;
		}
		return (float)inBothCorpusAnnotSize/(float)(inBothCorpusAnnotSize + onlyToCompareAnnotSize);
	}

	private float calculateRecall(  int onlyGoldStandardAnnotSize, int inBothCorpusAnnotSize){
		if ((inBothCorpusAnnotSize + onlyGoldStandardAnnotSize) == 0){
			return 0;
		}
		return (float)inBothCorpusAnnotSize/(float)(inBothCorpusAnnotSize + onlyGoldStandardAnnotSize);
	}

	private float calculateFScore(int onlyToCompareAnnotSize,  int onlyGoldStandardAnnotSize, int inBothCorpusAnnotSize){
		if (calculatePrecision(onlyToCompareAnnotSize, inBothCorpusAnnotSize) + calculateRecall(onlyGoldStandardAnnotSize, inBothCorpusAnnotSize) == 0){
			return 0;
		}
		return 2 * (calculatePrecision(onlyToCompareAnnotSize, inBothCorpusAnnotSize) * calculateRecall(onlyGoldStandardAnnotSize, inBothCorpusAnnotSize) ) / (calculatePrecision(onlyToCompareAnnotSize, inBothCorpusAnnotSize) + calculateRecall(onlyGoldStandardAnnotSize, inBothCorpusAnnotSize) );
	}
}
