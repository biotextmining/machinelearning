package com.silicolife.textmining.machinelearning.biotml.core.annotator;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.silicolife.textmining.machinelearning.biotml.core.BioTMLConstants;
import com.silicolife.textmining.machinelearning.biotml.core.annotator.processors.BioTMLMalletClassifierAnnotatorProcessor;
import com.silicolife.textmining.machinelearning.biotml.core.annotator.processors.BioTMLMalletTransducerAnnotatorProcessor;
import com.silicolife.textmining.machinelearning.biotml.core.corpora.otherdatastructures.BioTMLDocSentIDs;
import com.silicolife.textmining.machinelearning.biotml.core.exception.BioTMLException;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLAnnotation;
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

	public Set<IBioTMLEvent> generateRelations(IBioTMLCorpus corpus, IBioTMLModel model, int threads) throws BioTMLException{

		if(!validateModel(model))
		{
			throw new BioTMLException(5);
		}
		
		Set<IBioTMLEvent> relations = new HashSet<>();
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
			Set<IBioTMLEvent> relations) throws BioTMLException {
		transducerProcessor = new BioTMLMalletTransducerAnnotatorProcessor(corpus, model, threads);
		InstanceList predictionMatrix = transducerProcessor.generatePredictionMatrix();
		Iterator<Instance> itSentence = predictionMatrix.iterator();
		while(itSentence.hasNext()){
			Instance sentence = itSentence.next();
			@SuppressWarnings("rawtypes")
			Sequence predictedLabels = transducerProcessor.getPredictionForInstance(sentence);
			Double predictionScore = transducerProcessor.getPredictionScoreForInstance(sentence);
			BioTMLDocSentIDs ids = (BioTMLDocSentIDs)sentence.getName();
			IBioTMLDocument doc = corpus.getDocumentByID(ids.getDocId());
			for(int tokenIndexOrAnnotationIndex=0; tokenIndexOrAnnotationIndex<predictedLabels.size(); tokenIndexOrAnnotationIndex++){
				String prediction = predictedLabels.get(tokenIndexOrAnnotationIndex).toString();
				if(!prediction.equals(BioTMLConstants.o.toString()) && ids.getAnnotTokenRelationStartIndex() != -1 && ids.getAnnotTokenRelationEndIndex() != -1){
					IBioTMLAnnotation annotationOrClue = transducerProcessor.getAnnotation(corpus, doc, ids.getSentId(),ids.getAnnotTokenRelationStartIndex(), ids.getAnnotTokenRelationEndIndex());
					if(annotationOrClue != null){
						transducerProcessor.addPredictedRelation(corpus, relations, doc, ids.getSentId(), tokenIndexOrAnnotationIndex, annotationOrClue, ids.isOnlyAnnotations(), model.getModelConfiguration().getClassType(), model.getModelConfiguration().getREMethodology(), prediction, predictionScore);
					}
				}
			}
		}

	}

	private void predictRelationsUsingClassifierProcessor(IBioTMLCorpus corpus, IBioTMLModel model, int threads,
			Set<IBioTMLEvent> relations) throws BioTMLException {
		classifierProcessor = new BioTMLMalletClassifierAnnotatorProcessor(corpus, model, threads);
		InstanceList predictionMatrix = classifierProcessor.generatePredictionMatrix();
		Iterator<Instance> itToken = predictionMatrix.iterator();
		while(itToken.hasNext()){
			Instance token = itToken.next();
			String predictedLabel = classifierProcessor.getPredictionForInstance(token);
			Double predictionScore = classifierProcessor.getPredictionScoreForInstance(token);
			BioTMLDocSentIDs ids = (BioTMLDocSentIDs)token.getName();
			IBioTMLDocument doc = corpus.getDocumentByID(ids.getDocId());
			if(!predictedLabel.equals(BioTMLConstants.o.toString()) && ids.getAnnotTokenRelationStartIndex() != -1 && ids.getAnnotTokenRelationEndIndex() != -1){
				IBioTMLAnnotation trigger = classifierProcessor.getAnnotation(corpus, doc, ids.getSentId(),ids.getAnnotTokenRelationStartIndex(), ids.getAnnotTokenRelationEndIndex());
				if(trigger != null){
					classifierProcessor.addPredictedRelation(corpus, relations, doc, ids.getSentId(), ids.getTokenId(), trigger, ids.isOnlyAnnotations(), model.getModelConfiguration().getClassType(), model.getModelConfiguration().getREMethodology(), predictedLabel, predictionScore);
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
