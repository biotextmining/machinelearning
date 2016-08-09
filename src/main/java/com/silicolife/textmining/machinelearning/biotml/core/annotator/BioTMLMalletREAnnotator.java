package com.silicolife.textmining.machinelearning.biotml.core.annotator;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.silicolife.textmining.machinelearning.biotml.core.BioTMLConstants;
import com.silicolife.textmining.machinelearning.biotml.core.annotator.processors.BioTMLMalletClassifierAnnotatorProcessor;
import com.silicolife.textmining.machinelearning.biotml.core.annotator.processors.BioTMLMalletTransducerAnnotatorProcessor;
import com.silicolife.textmining.machinelearning.biotml.core.corpora.BioTMLDocSentTokenIDs;
import com.silicolife.textmining.machinelearning.biotml.core.exception.BioTMLException;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLAnnotation;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLAnnotationsRelation;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLCorpus;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLDocument;
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

	public Set<IBioTMLAnnotationsRelation> generateRelations(IBioTMLCorpus corpus, IBioTMLModel model, int threads) throws BioTMLException{

		if(!validateModel(model))
		{
			throw new BioTMLException(5);
		}
		
		Set<IBioTMLAnnotationsRelation> relations = new HashSet<>();
		if(model.getModelConfiguration().getAlgorithmType().equals(BioTMLAlgorithms.malletcrf.toString())
				|| model.getModelConfiguration().getAlgorithmType().equals(BioTMLAlgorithms.mallethmm.toString()))
		{
			predictRelationsUsingTransducerProcessor(corpus, model, threads, relations);	
		}
		else if(model.getModelConfiguration().getAlgorithmType().equals(BioTMLAlgorithms.malletsvm.toString()))
		{
			predictRelationsUsingClassifierProcessor(corpus, model, threads, relations);
		}
		return relations;
	}

	private void predictRelationsUsingTransducerProcessor(IBioTMLCorpus corpus, IBioTMLModel model, int threads,
			Set<IBioTMLAnnotationsRelation> relations) throws BioTMLException {
		transducerProcessor = new BioTMLMalletTransducerAnnotatorProcessor(corpus, model, threads);
		InstanceList predictionMatrix = transducerProcessor.generatePredictionMatrix();
		Iterator<Instance> itSentence = predictionMatrix.iterator();
		while(itSentence.hasNext()){
			Instance sentence = itSentence.next();
			@SuppressWarnings("rawtypes")
			Sequence predictedLabels = transducerProcessor.getPredictionForInstance(sentence);
			Double predictionScore = transducerProcessor.getPredictionScoreForInstance(sentence);
			BioTMLDocSentTokenIDs ids = (BioTMLDocSentTokenIDs)sentence.getName();
			IBioTMLDocument doc = corpus.getDocumentByID(ids.getDocId());
			for(int tokenIndex=0; tokenIndex<predictedLabels.size(); tokenIndex++){
				String prediction = predictedLabels.get(tokenIndex).toString();
				if(!prediction.equals(BioTMLConstants.o.toString())){
					IBioTMLAnnotation annotation = transducerProcessor.getAnnotation(corpus, doc, ids.getSentId(),ids.getAnnotTokenStartIndex(), ids.getAnnotTokenEndIndex());
					if(annotation != null){
						transducerProcessor.addPredictedRelation(corpus, relations, doc, ids.getSentId(), tokenIndex, annotation, model.getModelConfiguration().getClassType(), prediction, predictionScore);
					}
				}
			}
		}

	}

	private void predictRelationsUsingClassifierProcessor(IBioTMLCorpus corpus, IBioTMLModel model, int threads,
			Set<IBioTMLAnnotationsRelation> relations) throws BioTMLException {
		classifierProcessor = new BioTMLMalletClassifierAnnotatorProcessor(corpus, model, threads);
		InstanceList predictionMatrix = classifierProcessor.generatePredictionMatrix();
		Iterator<Instance> itToken = predictionMatrix.iterator();
		while(itToken.hasNext()){
			Instance token = itToken.next();
			String predictedLabel = classifierProcessor.getPredictionForInstance(token);
			Double predictionScore = classifierProcessor.getPredictionScoreForInstance(token);
			BioTMLDocSentTokenIDs ids = (BioTMLDocSentTokenIDs)token.getName();
			IBioTMLDocument doc = corpus.getDocumentByID(ids.getDocId());
			if(!predictedLabel.equals(BioTMLConstants.o.toString())){
				IBioTMLAnnotation annotation = classifierProcessor.getAnnotation(corpus, doc, ids.getSentId(),ids.getAnnotTokenStartIndex(), ids.getAnnotTokenEndIndex());
				if(annotation != null){
					classifierProcessor.addPredictedRelation(corpus, relations, doc, ids.getSentId(), ids.getTokenId(), annotation, model.getModelConfiguration().getClassType(), predictedLabel, predictionScore);
				}
			}
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
