package com.silicolife.textmining.machinelearning.biotml.core.interfaces;

import com.silicolife.textmining.machinelearning.biotml.core.exception.BioTMLException;

/**
 * 
 * BioTML Model interface.
 * 
 * @since 1.0.0
 * @author Ruben Rodrigues ({@code rrodrigues@silicolife.com})
 */

public interface IBioTMLModel {
	
	/**
	 * 
	 * Method to get features generator configuration.
	 * 
	 * @return {@link IBioTMLFeatureGeneratorConfigurator}.
	 */
	public IBioTMLFeatureGeneratorConfigurator getFeatureConfiguration();
	
	/**
	 * 
	 * Method to get the model configuration.
	 * 
	 * @return {@link IBioTMLModelConfigurator}.
	 */
	public IBioTMLModelConfigurator getModelConfiguration();
	
	/**
	 * 
	 * Method to get the model evaluation configuration.
	 * 
	 * @return {@link IBioTMLModelEvaluationConfigurator}.
	 */
	public IBioTMLModelEvaluationConfigurator getModelEvaluationConfiguration();
	
	/**
	 * 
	 * Method to perform the evaluation regarding the evaluation configurations inputed.
	 * 
	 * @return {link IBioTMLModelEvaluationResults}.
	 * @throws BioTMLException
	 */
	public IBioTMLModelEvaluationResults evaluate()  throws BioTMLException;
	
	/**
	 * 
	 * Method to train a model.
	 * 
	 * @throws BioTMLException
	 */
	public void train() throws BioTMLException;
	
	/**
	 * 
	 * Method to clean the model Alphabet.
	 * 
	 */
	public void cleanAlphabetMemory();
	
	/**
	 * 
	 * Method to clean the model Pipe.
	 * 
	 */
	public void cleanPipeMemory();
	
	/**
	 * 
	 * Method to return the model.
	 * 
	 * @return Object that represents the model and could be casted into CRF, HMM, SVM, etc.
	 */
	public Object getModel();

}
