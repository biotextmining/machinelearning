package com.silicolife.textmining.machinelearning.biotml.core.annotator;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.silicolife.textmining.machinelearning.biotml.core.BioTMLConstants;
import com.silicolife.textmining.machinelearning.biotml.core.corpora.BioTMLCorpusImpl;
import com.silicolife.textmining.machinelearning.biotml.core.exception.BioTMLException;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLEntity;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLEvent;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLAnnotator;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLCorpus;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLModel;
import com.silicolife.textmining.machinelearning.biotml.core.mllibraries.BioTMLAlgorithm;

import cc.mallet.classify.Classifier;
import cc.mallet.fst.Transducer;

/**
 * 
 * Represents the mallet annotator to apply a mallet model and annotate a corpus.
 * 
 * @since 1.0.0
 * @version 1.1.0
 * @author Ruben Rodrigues ({@code rrodrigues@silicolife.com})
 */

public class BioTMLMalletAnnotatorImpl implements IBioTMLAnnotator{

	private IBioTMLCorpus corpus;
	private boolean stop = false;
	private BioTMLMalletREAnnotator reAnnotator;
	private BioTMLMalletNERAnnotator nerAnnotator;

	/**
	 * 
	 * Initializes the annotator with a unannotated corpus.
	 * 
	 * @param corpus - Unannotated {@link IBioTMLCorpus}.
	 */
	public BioTMLMalletAnnotatorImpl(IBioTMLCorpus corpus){
		this.corpus = corpus;
	}

	public IBioTMLCorpus getBasedBioTMCorpus() {
		return corpus;
	}

	public IBioTMLCorpus generateAnnotatedBioTMCorpus(IBioTMLModel model, int threads) throws BioTMLException{

		if(!validateModel(model))
		{
			throw new BioTMLException(5);
		}

		List<IBioTMLEntity> annotations = getBasedBioTMCorpus().getAnnotations();
		List<IBioTMLEvent> events = getBasedBioTMCorpus().getEvents();

		generateNewAnnotationsOrEvents(model, threads, annotations, events);

		if(!events.isEmpty()){
			return new BioTMLCorpusImpl(getBasedBioTMCorpus().getDocuments(), annotations, events, getBasedBioTMCorpus().toString());
		}else{
			return new BioTMLCorpusImpl(getBasedBioTMCorpus().getDocuments(), annotations, getBasedBioTMCorpus().toString());
		}
	}

	private void generateNewAnnotationsOrEvents(IBioTMLModel model, int threads, List<IBioTMLEntity> annotations,
			List<IBioTMLEvent> events) throws BioTMLException {
		
		if(model.getModelConfiguration().getIEType().equals(BioTMLConstants.ner.toString()))
		{
			Set<IBioTMLEntity> newAnnotations = executeNERAnnotation(model, threads);
			annotations.addAll(newAnnotations);
		}
		else if(model.getModelConfiguration().getIEType().equals(BioTMLConstants.re.toString()))
		{
			Set<IBioTMLEvent> newEvents = executeREAnnotation(model, threads);
			events.addAll(newEvents);
		}
		
	}

	public IBioTMLCorpus generateAnnotatedBioTMCorpus(List<IBioTMLModel> models, int threads) throws BioTMLException{

		if(!validateSubModels(models))
		{
			throw new BioTMLException(5);
		}

		List<IBioTMLEntity> annotations = getBasedBioTMCorpus().getAnnotations();
		List<IBioTMLEvent> relations = getBasedBioTMCorpus().getEvents();
		Iterator<IBioTMLModel> itModels = models.iterator();
		while(itModels.hasNext() && !stop){
			IBioTMLModel model = itModels.next();
			generateNewAnnotationsOrEvents(model, threads, annotations, relations);
		}
		
		if(!relations.isEmpty()){
			return new BioTMLCorpusImpl(getBasedBioTMCorpus().getDocuments(), annotations, relations, getBasedBioTMCorpus().toString());
		}else{
			return new BioTMLCorpusImpl(getBasedBioTMCorpus().getDocuments(), annotations, getBasedBioTMCorpus().toString());
		}
	}

	private Set<IBioTMLEvent> executeREAnnotation(IBioTMLModel model, int threads) throws BioTMLException {
		reAnnotator = new BioTMLMalletREAnnotator();
		return reAnnotator.generateEvents(getBasedBioTMCorpus(), model, threads);
	}

	private Set<IBioTMLEntity> executeNERAnnotation(IBioTMLModel model, int threads) throws BioTMLException {
		nerAnnotator = new BioTMLMalletNERAnnotator();
		return nerAnnotator.generateAnnotations(getBasedBioTMCorpus(), model, threads);
	}


	public boolean validateModel(IBioTMLModel model) {
		return model.isValid();
	}

	private boolean validateSubModels(List<IBioTMLModel> models){
		for( IBioTMLModel model : models){
			if(!validateModel(model)){
				return false;
			}
		}
		return true;
	}

	public void stopAnnotator() {
		stop = true;
		if(nerAnnotator!= null){
			nerAnnotator.stopAnnotator();
		}
		if(reAnnotator!= null){
			reAnnotator.stopAnnotator();
		}
	}

}
