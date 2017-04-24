package com.silicolife.textmining.machinelearning.biotml.core.annotator;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.silicolife.textmining.machinelearning.biotml.core.BioTMLConstants;
import com.silicolife.textmining.machinelearning.biotml.core.annotator.processors.BioTMLMalletClassifierAnnotatorProcessor;
import com.silicolife.textmining.machinelearning.biotml.core.annotator.processors.BioTMLMalletTransducerAnnotatorProcessor;
import com.silicolife.textmining.machinelearning.biotml.core.corpora.otherdatastructures.BioTMLDocSentIDs;
import com.silicolife.textmining.machinelearning.biotml.core.exception.BioTMLException;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLEntity;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLCorpus;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLDocument;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLModel;
import com.silicolife.textmining.machinelearning.biotml.core.mllibraries.BioTMLAlgorithm;

import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import cc.mallet.types.Sequence;

/**
 * 
 * Represents the mallet NER annotator to apply a mallet model and annotate a corpus.
 * 
 * @since 1.1.0
 * @version 1.1.0
 * @author Ruben Rodrigues ({@code rrodrigues@silicolife.com})
 */

public class BioTMLMalletNERAnnotator {
	
	private BioTMLMalletTransducerAnnotatorProcessor transducerProcessor;
	private BioTMLMalletClassifierAnnotatorProcessor classifierProcessor;

	public BioTMLMalletNERAnnotator(){
		
	}
	
	private BioTMLMalletTransducerAnnotatorProcessor getTransducerProcessor() {
		return transducerProcessor;
	}

	private BioTMLMalletClassifierAnnotatorProcessor getClassifierProcessor() {
		return classifierProcessor;
	}

	public Set<IBioTMLEntity> generateEntities(IBioTMLCorpus corpus, IBioTMLModel model, int threads) throws BioTMLException{
		
		if(!validateModel(model))
			throw new BioTMLException(5);
		
		Set<IBioTMLEntity> annotations = new HashSet<>();
		if(model.getModelConfiguration().getAlgorithmType().equals(BioTMLAlgorithm.malletcrf)
				|| model.getModelConfiguration().getAlgorithmType().equals(BioTMLAlgorithm.mallethmm))
		{
			predictAnnotationsUsingTransducerProcessor(corpus, model, threads, annotations);	
		}
		else if(model.getModelConfiguration().getAlgorithmType().equals(BioTMLAlgorithm.malletsvm)
				|| model.getModelConfiguration().getAlgorithmType().equals(BioTMLAlgorithm.malletnaivebayes)
				|| model.getModelConfiguration().getAlgorithmType().equals(BioTMLAlgorithm.malletdecisiontree)
				|| model.getModelConfiguration().getAlgorithmType().equals(BioTMLAlgorithm.malletmaxent)
				|| model.getModelConfiguration().getAlgorithmType().equals(BioTMLAlgorithm.malletc45))
		{
			predictAnnotationsUsingClassifierProcessor(corpus, model, threads, annotations);
		}
		return annotations;
	}

	private void predictAnnotationsUsingTransducerProcessor(IBioTMLCorpus corpus, IBioTMLModel model, int threads,
			Set<IBioTMLEntity> annotations) throws BioTMLException {
		
		transducerProcessor = new BioTMLMalletTransducerAnnotatorProcessor(corpus, model, threads);
		InstanceList predictionMatrix = getTransducerProcessor().generatePredictionMatrix();
		Iterator<Instance> itSentence = predictionMatrix.iterator();
		while(itSentence.hasNext()){
			Instance sentence = itSentence.next();
			@SuppressWarnings("rawtypes")
			Sequence predictedLabels = getTransducerProcessor().getPredictionForInstance(sentence);
			Double predictionScore = getTransducerProcessor().getPredictionScoreForInstance(sentence);
			BioTMLDocSentIDs ids = (BioTMLDocSentIDs)sentence.getName();
			IBioTMLDocument doc = corpus.getDocumentByID(ids.getDocId());
			for(int tokenIndex=0; tokenIndex < predictedLabels.size(); tokenIndex++){
				getTransducerProcessor().addPredictedAnnotation(annotations, doc, ids.getSentId(), tokenIndex, 
						model.getModelConfiguration().getClassType(), predictedLabels.get(tokenIndex).toString(), predictionScore);
			}
		}
	}

	private void predictAnnotationsUsingClassifierProcessor(IBioTMLCorpus corpus, 
			IBioTMLModel model, int threads, Set<IBioTMLEntity> annotations) throws BioTMLException {
		
		classifierProcessor = new BioTMLMalletClassifierAnnotatorProcessor(corpus, model, threads);
		InstanceList predictionMatrix = getClassifierProcessor().generatePredictionMatrix();
		Iterator<Instance> itToken = predictionMatrix.iterator();
		while(itToken.hasNext()){
			Instance token = itToken.next();
			String predictedLabel = getClassifierProcessor().getPredictionForInstance(token);
			Double predictionScore = getClassifierProcessor().getPredictionScoreForInstance(token);
			BioTMLDocSentIDs ids = (BioTMLDocSentIDs)token.getName();
			IBioTMLDocument doc = corpus.getDocumentByID(ids.getDocId());
			getClassifierProcessor().addPredictedAnnotation(annotations, doc, ids.getSentId(), ids.getTokenId(), 
					model.getModelConfiguration().getClassType(), predictedLabel, predictionScore);
		}
	}
	
	public boolean validateModel(IBioTMLModel model) {
		if(model.getModelConfiguration().getIEType().equals(BioTMLConstants.ner.toString()))
			return model.isValid() && model.isTrained();
		return false;
	}
	
	public void stopAnnotator() {
		if(getTransducerProcessor() != null)
			getTransducerProcessor().stopProcessor();
		
		if(getClassifierProcessor() != null)
			getClassifierProcessor().stopProcessor();
		
	}

}
