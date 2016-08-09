package com.silicolife.textmining.machinelearning.biotml.core.annotator;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.silicolife.textmining.machinelearning.biotml.core.BioTMLConstants;
import com.silicolife.textmining.machinelearning.biotml.core.corpora.BioTMLCorpus;
import com.silicolife.textmining.machinelearning.biotml.core.exception.BioTMLException;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLAnnotation;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLAnnotationsRelation;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLAnnotator;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLCorpus;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLModel;
import com.silicolife.textmining.machinelearning.biotml.core.mllibraries.BioTMLAlgorithms;

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

public class BioTMLMalletAnnotator implements IBioTMLAnnotator{

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
	public BioTMLMalletAnnotator(IBioTMLCorpus corpus){
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

		List<IBioTMLAnnotation> annotations = getBasedBioTMCorpus().getAnnotations();
		List<IBioTMLAnnotationsRelation> relations = getBasedBioTMCorpus().getRelations();

		generateNewAnnotationsOrRelations(model, threads, annotations, relations);

		if(!relations.isEmpty()){
			return new BioTMLCorpus(getBasedBioTMCorpus().getDocuments(), annotations, relations, getBasedBioTMCorpus().toString());
		}else{
			return new BioTMLCorpus(getBasedBioTMCorpus().getDocuments(), annotations, getBasedBioTMCorpus().toString());
		}
	}

	private void generateNewAnnotationsOrRelations(IBioTMLModel model, int threads, List<IBioTMLAnnotation> annotations,
			List<IBioTMLAnnotationsRelation> relations) throws BioTMLException {
		
		if(model.getModelConfiguration().getIEType().equals(BioTMLConstants.ner.toString()))
		{
			Set<IBioTMLAnnotation> newAnnotations = executeNERAnnotation(model, threads);
			annotations.addAll(newAnnotations);
		}
		else if(model.getModelConfiguration().getIEType().equals(BioTMLConstants.re.toString()))
		{
			Set<IBioTMLAnnotationsRelation> newRelations = executeREAnnotation(model, threads);
			relations.addAll(newRelations);
		}
		
	}

	public IBioTMLCorpus generateAnnotatedBioTMCorpus(List<IBioTMLModel> models, int threads) throws BioTMLException{

		if(!validateSubModels(models))
		{
			throw new BioTMLException(5);
		}

		List<IBioTMLAnnotation> annotations = getBasedBioTMCorpus().getAnnotations();
		List<IBioTMLAnnotationsRelation> relations = getBasedBioTMCorpus().getRelations();
		Iterator<IBioTMLModel> itModels = models.iterator();
		while(itModels.hasNext() && !stop){
			IBioTMLModel model = itModels.next();
			generateNewAnnotationsOrRelations(model, threads, annotations, relations);
		}
		
		if(!relations.isEmpty()){
			return new BioTMLCorpus(getBasedBioTMCorpus().getDocuments(), annotations, relations, getBasedBioTMCorpus().toString());
		}else{
			return new BioTMLCorpus(getBasedBioTMCorpus().getDocuments(), annotations, getBasedBioTMCorpus().toString());
		}
	}

	private Set<IBioTMLAnnotationsRelation> executeREAnnotation(IBioTMLModel model, int threads) throws BioTMLException {
		reAnnotator = new BioTMLMalletREAnnotator();
		return reAnnotator.generateRelations(getBasedBioTMCorpus(), model, threads);
	}

	private Set<IBioTMLAnnotation> executeNERAnnotation(IBioTMLModel model, int threads) throws BioTMLException {
		nerAnnotator = new BioTMLMalletNERAnnotator();
		return nerAnnotator.generateAnnotations(getBasedBioTMCorpus(), model, threads);
	}


	public boolean validateModel(IBioTMLModel model) {
		
		if(model.getModelConfiguration().getIEType().equals(BioTMLConstants.ner.toString())
				|| model.getModelConfiguration().getIEType().equals(BioTMLConstants.re.toString())){
			
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
