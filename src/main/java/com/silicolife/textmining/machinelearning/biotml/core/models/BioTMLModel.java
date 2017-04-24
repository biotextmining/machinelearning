package com.silicolife.textmining.machinelearning.biotml.core.models;

import java.util.ArrayList;
import java.util.Set;

import com.silicolife.textmining.machinelearning.biotml.core.BioTMLConstants;
import com.silicolife.textmining.machinelearning.biotml.core.annotator.BioTMLMalletNERAnnotator;
import com.silicolife.textmining.machinelearning.biotml.core.annotator.BioTMLMalletREAnnotator;
import com.silicolife.textmining.machinelearning.biotml.core.corpora.BioTMLCorpusImpl;
import com.silicolife.textmining.machinelearning.biotml.core.exception.BioTMLException;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLCorpus;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLEntity;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLEvent;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLFeatureGeneratorConfigurator;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLModel;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLModelConfigurator;


/**
 * 
 * Model class that is extended for other models like CRF, MEMM, HMM, SVM, etc.
 * 
 * @since 1.0.0
 * @author Ruben Rodrigues ({@code rrodrigues@silicolife.com})
 */

public abstract class BioTMLModel implements IBioTMLModel{

	private static final long serialVersionUID = 1L;
	protected IBioTMLFeatureGeneratorConfigurator featureConfiguration;
	protected IBioTMLModelConfigurator modelConfiguration;

	/**
	 * 
	 * Initializes a empty model.
	 * 
	 */
	public BioTMLModel(){}

	/**
	 * 
	 * Initializes a model without evaluation configuration.
	 * 
	 * @param featureConfiguration - A feature generation configurator {@link IBioTMLFeatureGeneratorConfigurator}.
	 * @param modelConfiguration - A model configurator {@link IBioTMLModelConfigurator}.
	 */
	public BioTMLModel(	IBioTMLFeatureGeneratorConfigurator featureConfiguration, 
					IBioTMLModelConfigurator modelConfiguration){
		this.featureConfiguration = featureConfiguration;
		this.modelConfiguration = modelConfiguration;
	}

	public IBioTMLFeatureGeneratorConfigurator getFeatureConfiguration(){
		return featureConfiguration;
	}

	public IBioTMLModelConfigurator getModelConfiguration(){
		return modelConfiguration;
	}
	
	@Override
	public IBioTMLCorpus predict(IBioTMLCorpus corpus) throws BioTMLException {
		if(!isTrained())
			throw new BioTMLException("The model is not trained!");
		if(getModelConfiguration().getIEType().equals(BioTMLConstants.ner.toString())){
			BioTMLMalletNERAnnotator annotator = new BioTMLMalletNERAnnotator();
			Set<IBioTMLEntity> annotations = annotator.generateEntities(corpus, this, getModelConfiguration().getNumThreads());
			return new BioTMLCorpusImpl(corpus.getDocuments(), new ArrayList<>(annotations), "Corpus with predicted annotations");
		}else if(getModelConfiguration().getIEType().equals(BioTMLConstants.re.toString())){
			BioTMLMalletREAnnotator annotator = new BioTMLMalletREAnnotator();
			Set<IBioTMLEvent> events = annotator.generateEvents(corpus, this, getModelConfiguration().getNumThreads());
			return new BioTMLCorpusImpl(corpus.getDocuments(), corpus.getEntities(), new ArrayList<>(events), "Corpus with predicted events");
		}
		return null;
	}

}
