package com.silicolife.textmining.machinelearning.biotml.core.annotator;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.silicolife.textmining.machinelearning.biotml.core.BioTMLConstants;
import com.silicolife.textmining.machinelearning.biotml.core.annotator.processors.BioTMLMalletClassifierAnnotatorProcessor;
import com.silicolife.textmining.machinelearning.biotml.core.annotator.processors.BioTMLMalletTransducerAnnotatorProcessor;
import com.silicolife.textmining.machinelearning.biotml.core.corpora.BioTMLAssociationImpl;
import com.silicolife.textmining.machinelearning.biotml.core.corpora.BioTMLEventImpl;
import com.silicolife.textmining.machinelearning.biotml.core.corpora.otherdatastructures.BioTMLDocSentIDs;
import com.silicolife.textmining.machinelearning.biotml.core.exception.BioTMLException;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLAnnotation;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLAssociation;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLCorpus;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLDocument;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLEvent;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLModel;
import com.silicolife.textmining.machinelearning.biotml.core.mllibraries.BioTMLAlgorithms;

import cc.mallet.classify.Classifier;
import cc.mallet.fst.Transducer;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import cc.mallet.types.Sequence;

/**
 * 
 * Represents the mallet RE annotator to apply a mallet model and annotate a corpus.
 * 
 * @since 1.1.0
 * @version 1.1.0
 * @author Ruben Rodrigues ({@code rrodrigues@silicolife.com})
 */

public class BioTMLMalletREAnnotator {

	private BioTMLMalletTransducerAnnotatorProcessor transducerProcessor;
	private BioTMLMalletClassifierAnnotatorProcessor classifierProcessor;

	public BioTMLMalletREAnnotator(){

	}

	public Set<IBioTMLEvent> generateEvents(IBioTMLCorpus corpus, IBioTMLModel model, int threads) throws BioTMLException{

		if(!validateModel(model))
		{
			throw new BioTMLException(5);
		}
		
		Set<IBioTMLEvent> events = new HashSet<>();
		if(model.getModelConfiguration().getAlgorithmType().equals(BioTMLAlgorithms.malletcrf.toString())
				|| model.getModelConfiguration().getAlgorithmType().equals(BioTMLAlgorithms.mallethmm.toString()))
		{
			predictEventsUsingTransducerProcessor(corpus, model, threads, events);	
		}
		else if(model.getModelConfiguration().getAlgorithmType().equals(BioTMLAlgorithms.malletsvm.toString()))
		{
			predictEventsUsingClassifierProcessor(corpus, model, threads, events);
		}
		return events;
	}

	private void predictEventsUsingTransducerProcessor(IBioTMLCorpus corpus, IBioTMLModel model, int threads,
			Set<IBioTMLEvent> events) throws BioTMLException {
		transducerProcessor = new BioTMLMalletTransducerAnnotatorProcessor(corpus, model, threads);
		InstanceList predictionMatrix = transducerProcessor.generatePredictionMatrix();
		Iterator<Instance> itInstance = predictionMatrix.iterator();
		while(itInstance.hasNext()){
			Instance instance = itInstance.next();
			@SuppressWarnings("rawtypes")
			Sequence predictedLabels = transducerProcessor.getPredictionForInstance(instance);
			Double predictionScore = transducerProcessor.getPredictionScoreForInstance(instance);
			BioTMLDocSentIDs ids = (BioTMLDocSentIDs)instance.getName();
			IBioTMLDocument doc = corpus.getDocumentByID(ids.getDocId());
			for(int tokenIndexOrAssociationIndex=0; tokenIndexOrAssociationIndex<predictedLabels.size(); tokenIndexOrAssociationIndex++){
				String prediction = predictedLabels.get(tokenIndexOrAssociationIndex).toString();
				if(!prediction.equals(BioTMLConstants.o.toString()) && ids.getAnnotTokenRelationStartIndex() != -1 && ids.getAnnotTokenRelationEndIndex() != -1){
					IBioTMLAnnotation annotationOrClue = transducerProcessor.getAnnotation(corpus, doc, ids.getSentId(),ids.getAnnotTokenRelationStartIndex(), ids.getAnnotTokenRelationEndIndex());
					if(annotationOrClue != null){
						transducerProcessor.addPredictedEvent(corpus, events, doc, ids.getSentId(), tokenIndexOrAssociationIndex, annotationOrClue, ids.isOnlyAnnotations(), model.getModelConfiguration().getClassType(), model.getModelConfiguration().getREMethodology(), prediction, predictionScore);
					}
				}else if(!prediction.equals(BioTMLConstants.o.toString()) && !ids.getAssociations().isEmpty()){
					IBioTMLAssociation association = ids.getAssociations().get(tokenIndexOrAssociationIndex);
					transducerProcessor.addEvent(events, association, model.getModelConfiguration().getClassType(), predictionScore);
				}
			}
		}

	}

	private void predictEventsUsingClassifierProcessor(IBioTMLCorpus corpus, IBioTMLModel model, int threads,
			Set<IBioTMLEvent> events) throws BioTMLException {
		classifierProcessor = new BioTMLMalletClassifierAnnotatorProcessor(corpus, model, threads);
		InstanceList predictionMatrix = classifierProcessor.generatePredictionMatrix();
		Iterator<Instance> itInstance = predictionMatrix.iterator();
		while(itInstance.hasNext()){
			Instance instance = itInstance.next();
			String predictedLabel = classifierProcessor.getPredictionForInstance(instance);
			Double predictionScore = classifierProcessor.getPredictionScoreForInstance(instance);
			BioTMLDocSentIDs ids = (BioTMLDocSentIDs)instance.getName();
			IBioTMLDocument doc = corpus.getDocumentByID(ids.getDocId());
			if(!predictedLabel.equals(BioTMLConstants.o.toString()) && ids.getAnnotTokenRelationStartIndex() != -1 && ids.getAnnotTokenRelationEndIndex() != -1){
				IBioTMLAnnotation trigger = classifierProcessor.getAnnotation(corpus, doc, ids.getSentId(),ids.getAnnotTokenRelationStartIndex(), ids.getAnnotTokenRelationEndIndex());
				if(trigger != null){
					classifierProcessor.addPredictedEvent(corpus, events, doc, ids.getSentId(), ids.getTokenId(), trigger, ids.isOnlyAnnotations(), model.getModelConfiguration().getClassType(), model.getModelConfiguration().getREMethodology(), predictedLabel, predictionScore);
				}
			}else if(!predictedLabel.equals(BioTMLConstants.o.toString()) && ids.getAssociation() != null)
				classifierProcessor.addEvent(events, ids.getAssociation(), model.getModelConfiguration().getClassType(), predictionScore);
		}
	}
	
	public boolean validateModel(IBioTMLModel model) {
		
		if(model.getModelConfiguration().getIEType().equals(BioTMLConstants.re.toString())){
			
			if (model.getModel() instanceof Transducer){
				if(	model.getModelConfiguration().getAlgorithmType().equals(BioTMLAlgorithms.malletcrf.toString())
						|| model.getModelConfiguration().getAlgorithmType().equals(BioTMLAlgorithms.mallethmm.toString())){
					return true;
				}
			}
			
			if (model.getModel() instanceof Classifier && model.getModelConfiguration().getAlgorithmType().equals(BioTMLAlgorithms.malletsvm.toString())){
				return true;
			}
			
		}

		return false;
	}

	public void stopAnnotator() {
		if(transducerProcessor != null){
			transducerProcessor.stopProcessor();
		}
		if(classifierProcessor != null){
			classifierProcessor.stopProcessor();
		}
	}


}
