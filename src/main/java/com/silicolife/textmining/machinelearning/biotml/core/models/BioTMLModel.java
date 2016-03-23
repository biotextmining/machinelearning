package com.silicolife.textmining.machinelearning.biotml.core.models;

import com.silicolife.textmining.machinelearning.biotml.core.evaluation.BioTMLModelEvaluationConfigurator;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLFeatureGeneratorConfigurator;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLModel;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLModelConfigurator;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLModelEvaluationConfigurator;


/**
 * 
 * Model class that is extended for other models like CRF, MEMM, HMM, SVM, etc.
 * 
 * @since 1.0.0
 * @author Ruben Rodrigues ({@code rrodrigues@silicolife.com})
 */

public abstract class BioTMLModel implements IBioTMLModel{

	private IBioTMLFeatureGeneratorConfigurator featureConfiguration;
	protected IBioTMLModelConfigurator modelConfiguration;
	private IBioTMLModelEvaluationConfigurator modelEvaluationConfiguration;

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
		this.modelEvaluationConfiguration = new BioTMLModelEvaluationConfigurator();
	}

	/**
	 * 
	 *  Initializes a model with evaluation configuration.
	 * 
	 * @param featureConfiguration - A feature generation configurator {@link IBioTMLFeatureGeneratorConfigurator}.
	 * @param modelConfiguration - A model configurator {@link IBioTMLModelConfigurator}.
	 * @param modelEvaluationConfiguration - A model evaluation configurator {@link IBioTMLModelEvaluationConfigurator}.
	 */
	public BioTMLModel(	IBioTMLFeatureGeneratorConfigurator featureConfiguration, 
					IBioTMLModelConfigurator modelConfiguration,
					IBioTMLModelEvaluationConfigurator modelEvaluationConfiguration){
		this.featureConfiguration = featureConfiguration;
		this.modelConfiguration = modelConfiguration; 
		this.modelEvaluationConfiguration = modelEvaluationConfiguration;
	}

	public IBioTMLFeatureGeneratorConfigurator getFeatureConfiguration(){
		return featureConfiguration;
	}

	public IBioTMLModelConfigurator getModelConfiguration(){
		return modelConfiguration;
	}

	public IBioTMLModelEvaluationConfigurator getModelEvaluationConfiguration(){
		return modelEvaluationConfiguration;
	}

}
