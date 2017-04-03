package com.silicolife.textmining.machinelearning.biotml.core.evaluation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.silicolife.textmining.machinelearning.biotml.core.BioTMLConstants;
import com.silicolife.textmining.machinelearning.biotml.core.evaluation.datastrucures.BioTMLEvaluationImpl;
import com.silicolife.textmining.machinelearning.biotml.core.evaluation.datastrucures.BioTMLMultiEvaluationImpl;
import com.silicolife.textmining.machinelearning.biotml.core.evaluation.utils.BioTMLCrossValidationCorpusIterator;
import com.silicolife.textmining.machinelearning.biotml.core.exception.BioTMLException;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLAnnotation;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLAssociation;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLConfusionMatrix;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLCorpus;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLCrossValidationFold;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLEvaluation;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLEvent;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLModel;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLModelEvaluationConfigurator;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLMultiEvaluation;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLOffsetsPair;

public class BioTMLModelsCrossValidationCorpusEvaluator {
	
	private Collection<IBioTMLModel> models;
	private IBioTMLModelEvaluationConfigurator configuration;

	public BioTMLModelsCrossValidationCorpusEvaluator(Collection<IBioTMLModel> models, IBioTMLModelEvaluationConfigurator configuration){
		this.models = models;
		this.configuration = configuration;
	}
	
	private Collection<IBioTMLModel> getModels() {
		return models;
	}

	private IBioTMLModelEvaluationConfigurator getConfiguration() {
		return configuration;
	}

	public IBioTMLMultiEvaluation evaluate(IBioTMLCorpus corpus) throws BioTMLException{
		Map<String, List<IBioTMLEvaluation>> evaluations = new HashMap<>();
		Iterator<IBioTMLCrossValidationFold<IBioTMLCorpus>> itCross = new BioTMLCrossValidationCorpusIterator(corpus, getConfiguration().getCVFoldsByDocuments());
		int foldCount = 1;
		while(itCross.hasNext()){
			IBioTMLCrossValidationFold<IBioTMLCorpus> fold = itCross.next();	    
			processFold(evaluations, foldCount, fold);
			foldCount++;
		}
		return new BioTMLMultiEvaluationImpl(evaluations);
	}

	private void processFold(Map<String, List<IBioTMLEvaluation>> evaluations, int foldCount, IBioTMLCrossValidationFold<IBioTMLCorpus> fold)
			throws BioTMLException {
		IBioTMLCorpus trainingData = fold.getTrainingDataset();
		IBioTMLCorpus testingData = fold.getTestingDataset();
		Map<IBioTMLOffsetsPair, IBioTMLAnnotation> predictedAnnotations = new HashMap<>();
		Map<IBioTMLAssociation<?, ?>, IBioTMLEvent> predictedEvents = new HashMap<>();
		Set<String> nerClassTypes = new HashSet<>();
		Set<String> reEventTypes = new HashSet<>();
		for(IBioTMLModel model : getModels()){
			model.train(trainingData);
			IBioTMLCorpus predictedCorpus = model.predict(testingData);
			
			if(model.getModelConfiguration().getIEType().equals(BioTMLConstants.ner.toString())){
				nerClassTypes.add(model.getModelConfiguration().getClassType());
				addPredictedAnnotationsToMap(predictedAnnotations, predictedCorpus);
			}else if(model.getModelConfiguration().getIEType().equals(BioTMLConstants.re.toString())){
				reEventTypes.add(model.getModelConfiguration().getClassType());
				addPredictedEventsToMap(predictedEvents, predictedCorpus);
			}
				
		}
		
		if(!evaluations.containsKey(BioTMLConstants.ner.toString()))
			evaluations.put(BioTMLConstants.ner.toString(), new ArrayList<>());
		List<IBioTMLEvaluation> nerEvaluations = evaluations.get(BioTMLConstants.ner.toString());
		nerEvaluations.add(getNEREvaluation(foldCount, testingData, predictedAnnotations, nerClassTypes));
		evaluations.put(BioTMLConstants.ner.toString(), nerEvaluations);
		
		if(!evaluations.containsKey(BioTMLConstants.re.toString()))
			evaluations.put(BioTMLConstants.re.toString(), new ArrayList<>());
		List<IBioTMLEvaluation> reEvaluations = evaluations.get(BioTMLConstants.re.toString());
		reEvaluations.add(getREEvaluation(foldCount, testingData, predictedEvents, reEventTypes));
		evaluations.put(BioTMLConstants.re.toString(), reEvaluations);
	}

	private IBioTMLEvaluation getREEvaluation(int foldCount, IBioTMLCorpus testingData, Map<IBioTMLAssociation<?, ?>, IBioTMLEvent> predictedEvents, Set<String> reEventTypes) {
		Collection<IBioTMLEvent> goldEvents = testingData.getEventsByEventTypes(reEventTypes);
		Collection<IBioTMLEvent> toCompareEvents = predictedEvents.values();
		BioTMLEventEvaluator eventsEvaluator = new BioTMLEventEvaluator();
		IBioTMLConfusionMatrix<IBioTMLEvent> confusionMatrix = eventsEvaluator.generateConfusionMatrix(goldEvents, toCompareEvents);
		return new BioTMLEvaluationImpl(confusionMatrix, "Multi model event evaluation on fold: "+foldCount);
	}

	private IBioTMLEvaluation getNEREvaluation(int foldCount, IBioTMLCorpus testingData, Map<IBioTMLOffsetsPair, IBioTMLAnnotation> predictedAnnotations, Set<String> nerClassTypes) {
		Collection<IBioTMLAnnotation> goldAnnotations = testingData.getAnnotationsByAnnotationTypes(nerClassTypes);
		Collection<IBioTMLAnnotation> toCompareAnnotations = predictedAnnotations.values();
		BioTMLAnnotationEvaluator annotationsEvaluator = new BioTMLAnnotationEvaluator();
		IBioTMLConfusionMatrix<IBioTMLAnnotation> confusionMatrix = annotationsEvaluator.generateConfusionMatrix(goldAnnotations, toCompareAnnotations);
		return new BioTMLEvaluationImpl(confusionMatrix, "Multi model annotation evaluation on fold: "+foldCount);
	}

	private void addPredictedEventsToMap(Map<IBioTMLAssociation<?, ?>, IBioTMLEvent> predictedEvents,
			IBioTMLCorpus predictedCorpus) {
		List<IBioTMLEvent> events = predictedCorpus.getEvents();
		for(IBioTMLEvent event : events){
			if(!predictedEvents.containsKey(event.getAssociation()))
				predictedEvents.put(event.getAssociation(), event);
			else{
				IBioTMLEvent storedEvent = predictedEvents.get(event.getAssociation());
				if(storedEvent.getScore()<event.getScore())
					predictedEvents.put(event.getAssociation(), event);
			}
		}
	}

	private void addPredictedAnnotationsToMap(Map<IBioTMLOffsetsPair, IBioTMLAnnotation> predictedAnnotations, IBioTMLCorpus predictedCorpus) {
		List<IBioTMLAnnotation> annotations = predictedCorpus.getAnnotations();
		for(IBioTMLAnnotation annotation : annotations){
			
			if(!predictedAnnotations.containsKey(annotation.getAnnotationOffsets()))
				predictedAnnotations.put(annotation.getAnnotationOffsets(), annotation);
			else{
				IBioTMLAnnotation storedAnnotation = predictedAnnotations.get(annotation.getAnnotationOffsets());
				if(storedAnnotation.getScore()<annotation.getScore())
					predictedAnnotations.put(annotation.getAnnotationOffsets(), annotation);
			}
			
		}
	}

}
