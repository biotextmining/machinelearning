package com.silicolife.textmining.machinelearning.biotml.core.annotator.processors;

import com.silicolife.textmining.machinelearning.biotml.core.exception.BioTMLException;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLCorpus;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLModel;
import com.silicolife.textmining.machinelearning.biotml.core.mllibraries.mallet.BioTMLCorpusToInstanceMallet;

import cc.mallet.classify.Classification;
import cc.mallet.classify.Classifier;
import cc.mallet.pipe.Pipe;
import cc.mallet.types.Alphabet;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import cc.mallet.types.Labeling;

/**
 * 
 * Represents the mallet classifier annotator processor, it contains methods for mallet classifier models used in annotators.
 *  
 * @since 1.1.0
 * @version 1.1.0
 * @author Ruben Rodrigues ({@code rrodrigues@silicolife.com})
 */

public class BioTMLMalletClassifierAnnotatorProcessor extends BioTMLMalletAnnotatorProcessor{
	
	private IBioTMLCorpus corpus;
	private IBioTMLModel bioTMLModel;
	private Classifier classifierModel; 
	private int threads;
	private BioTMLCorpusToInstanceMallet malletCorpus;

	public BioTMLMalletClassifierAnnotatorProcessor(IBioTMLCorpus corpus, IBioTMLModel bioTMLModel, int threads){
		this.corpus = corpus;
		this.bioTMLModel = bioTMLModel;
		this.threads = threads;
		this.classifierModel = (Classifier) bioTMLModel.getModel();
	}

	private IBioTMLCorpus getCorpus() {
		return corpus;
	}

	private IBioTMLModel getBioTMLModel() {
		return bioTMLModel;
	}
	
	private Classifier getClassifierModel() {
		return classifierModel;
	}
	
	private int getThreads(){
		return threads;
	}

	public InstanceList generatePredictionMatrix() throws BioTMLException{
		malletCorpus = new BioTMLCorpusToInstanceMallet(getCorpus(), 
				getBioTMLModel().getModelConfiguration().getClassType(), getBioTMLModel().getModelConfiguration().getIEType());
		Pipe classificationPipe = getClassifierModel().getInstancePipe();
		return malletCorpus.exportToMalletFeatures(classificationPipe, getThreads(), getBioTMLModel().getFeatureConfiguration());
	}
	
	public String getPredictionForInstance(Instance instanceToPredict){
		Classification prediction = getClassifierModel().classify(instanceToPredict);
		Labeling labeling = prediction.getLabeling();
		return labeling.getLabelAtRank(0).toString();
	}
	
	public Double getPredictionScoreForInstance(Instance instanceToPredict){
		Classification prediction = getClassifierModel().classify(instanceToPredict);
		Labeling labeling = prediction.getLabeling();
		return labeling.getValueAtRank(0);
	}
	
	public void stopProcessor() {
		if(malletCorpus!= null){
			malletCorpus.stopAllFeatureThreads();
		}
		Alphabet.cleanAllAphabetsFromMemory();
		Pipe.cleanAllPipesFromMemory();
	}

}
