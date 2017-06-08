package com.silicolife.textmining.machinelearning.biotml.core.annotator;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.silicolife.textmining.machinelearning.biotml.core.BioTMLConstants;
import com.silicolife.textmining.machinelearning.biotml.core.annotator.processors.BioTMLMalletClassifierAnnotatorProcessor;
import com.silicolife.textmining.machinelearning.biotml.core.annotator.processors.BioTMLMalletTransducerAnnotatorProcessor;
import com.silicolife.textmining.machinelearning.biotml.core.exception.BioTMLException;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLAssociation;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLCorpus;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLEvent;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLModel;
import com.silicolife.textmining.machinelearning.biotml.core.mllibraries.BioTMLAlgorithm;

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
		if(model.getModelConfiguration().getAlgorithmType().equals(BioTMLAlgorithm.malletcrf)
				|| model.getModelConfiguration().getAlgorithmType().equals(BioTMLAlgorithm.mallethmm))
		{
			predictEventsUsingTransducerProcessor(corpus, model, threads, events);	
		}
		else if(model.getModelConfiguration().getAlgorithmType().equals(BioTMLAlgorithm.malletsvm)
				|| model.getModelConfiguration().getAlgorithmType().equals(BioTMLAlgorithm.malletnaivebayes)
				|| model.getModelConfiguration().getAlgorithmType().equals(BioTMLAlgorithm.malletdecisiontree)
				|| model.getModelConfiguration().getAlgorithmType().equals(BioTMLAlgorithm.malletmaxent)
				|| model.getModelConfiguration().getAlgorithmType().equals(BioTMLAlgorithm.malletc45))
		{
			predictEventsUsingClassifierProcessor(corpus, model, threads, events);
		}
		return events;
	}

	@SuppressWarnings("rawtypes")
	private void predictEventsUsingTransducerProcessor(IBioTMLCorpus corpus, IBioTMLModel model, int threads,
			Set<IBioTMLEvent> events) throws BioTMLException {
		transducerProcessor = new BioTMLMalletTransducerAnnotatorProcessor(corpus, model, threads);
		InstanceList predictionMatrix = transducerProcessor.generatePredictionMatrix();
		Iterator<Instance> itInstance = predictionMatrix.iterator();
		while(itInstance.hasNext()){
			Instance instance = itInstance.next();
			Sequence predictedLabels = transducerProcessor.getPredictionForInstance(instance);
			Double predictionScore = transducerProcessor.getPredictionScoreForInstance(instance);
			@SuppressWarnings("unchecked")
			List<IBioTMLAssociation> associations = (List<IBioTMLAssociation>)instance.getName();
			for(int associationIndex=0; associationIndex<predictedLabels.size(); associationIndex++){
				IBioTMLAssociation association = associations.get(associationIndex);
				String prediction = predictedLabels.get(associationIndex).toString();
				if(!prediction.equals(BioTMLConstants.o.toString())){
					transducerProcessor.addEvent(events, association, model.getModelConfiguration().getClassType(), predictionScore);
				}
			}
		}

	}

	@SuppressWarnings("rawtypes")
	private void predictEventsUsingClassifierProcessor(IBioTMLCorpus corpus, IBioTMLModel model, int threads,
			Set<IBioTMLEvent> events) throws BioTMLException {
		classifierProcessor = new BioTMLMalletClassifierAnnotatorProcessor(corpus, model, threads);
		InstanceList predictionMatrix = classifierProcessor.generatePredictionMatrix();
		Iterator<Instance> itInstance = predictionMatrix.iterator();
		while(itInstance.hasNext()){
			Instance instance = itInstance.next();
			String predictedLabel = classifierProcessor.getPredictionForInstance(instance);
			Double predictionScore = classifierProcessor.getPredictionScoreForInstance(instance);
			IBioTMLAssociation association = (IBioTMLAssociation)instance.getName();
			if(!predictedLabel.equals(BioTMLConstants.o.toString()))
				classifierProcessor.addEvent(events, association, model.getModelConfiguration().getClassType(), predictionScore);
		}
	}
	
	public boolean validateModel(IBioTMLModel model) {
		if(model.getModelConfiguration().getIEType().equals(BioTMLConstants.re.toString()))
			return model.isValid() && model.isTrained();
		return false;
	}

	public void stopAnnotator() {
		if(transducerProcessor != null)
			transducerProcessor.stopProcessor();
		
		if(classifierProcessor != null)
			classifierProcessor.stopProcessor();
		
	}


}
