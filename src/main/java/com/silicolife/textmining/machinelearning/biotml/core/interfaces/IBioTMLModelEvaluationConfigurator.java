package com.silicolife.textmining.machinelearning.biotml.core.interfaces;

/**
 * 
 * BioTML model evaluation configurator interface.
 * 
 * @since 1.0.0
 * @author Ruben Rodrigues ({@code rrodrigues@silicolife.com})
 */

public interface IBioTMLModelEvaluationConfigurator {
	
	/**
	 * 
	 * Method to define the number of folds to use cross-validation based in documents as data from corpus.
	 * 
	 */
	public void setCrossValidationByCorpusDoc(int folds);
	
	/**
	 * 
	 * Method to verify if the cross-validation based in documents is activated.
	 * 
	 * @return Cross-Validation activation boolean.
	 */
	public boolean isUseCrossValidationByDocuments();
	
	/**
	 * 
	 * Method to get the number of folds defined for cross-validation based in documents.
	 * 
	 * @return Number of folds for CV based in documents.
	 */
	public int getCVFoldsByDocuments();
	
	/**
	 * 
	 * Method to define the number of folds to use cross-validation based in sentences as data from corpus.
	 * 
	 */
	public void setCrossValidationByCorpusSent(int folds);
	
	/**
	 * 
	 * Method to verify if the cross-validation based in sentences is activated.
	 * 
	 * @return Cross-Validation activation boolean.
	 */
	public boolean isUseCrossValidationBySentences();
	
	/**
	 * 
	 * Method to get the number of folds defined for cross-validation based in sentences.
	 * 
	 * @return Number of folds for CV based in sentences.
	 */
	public int getCVFoldsBySentences();

}