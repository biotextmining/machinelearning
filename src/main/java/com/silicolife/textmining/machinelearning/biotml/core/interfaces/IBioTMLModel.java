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
	 * Method to perform the evaluation regarding the evaluation configurations inputed.
	 * 
	 * @param corpus {@link IBioTMLCorpus} to evaluate the model.
	 * @param configuration {@link IBioTMLModelEvaluationConfigurator} model evaluation configuration
	 * @return {link IBioTMLModelEvaluationResults}.
	 * @throws BioTMLException
	 */
	public IBioTMLModelEvaluationResults evaluate(IBioTMLCorpus corpus, IBioTMLModelEvaluationConfigurator configuration)  throws BioTMLException;
	
	/**
	 * 
	 * Method to train a model.
	 * 
	 * @param corpus {@link IBioTMLCorpus} to train the model.
	 * @throws BioTMLException
	 */
	public void train(IBioTMLCorpus corpus) throws BioTMLException;
	
	public IBioTMLCorpus predict(IBioTMLCorpus corpus) throws BioTMLException;
	
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
	
	/**
	 * 
	 * Method to return if the model is trained.
	 * 
	 * @return Boolean
	 */
	public boolean isTrained();
	
	/**
	 * 
	 * Method to verify if the configurations used on the model are valid.
	 * 
	 * @return Boolean
	 */
	public boolean isValid();

}
