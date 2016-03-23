package com.silicolife.textmining.machinelearning.biotml.core.interfaces;

import java.io.File;
import java.util.List;
import java.util.Map;

import com.silicolife.textmining.machinelearning.biotml.core.exception.BioTMLException;

/**
 * 
 * BioTML multi model interface.
 * 
 * @since 1.0.0
 * @author Ruben Rodrigues ({@code rrodrigues@silicolife.com})
 */

public interface IBioTMLMultiModel {
	
	/**
	 * 
	 * Method to get all annotation class types from the model.
	 * 
	 * @return List of class type strings
	 */
	public List<String> getClassTypes();
	
	/**
	 * 
	 * Method to get the Information Extraction type (e.g. NER or RE).
	 * 
	 * @return IE type string.
	 */
	public String getIEType();
	
	/**
	 * 
	 * Method to get the features generation configuration.
	 * 
	 * @return Features configuration ({@link IBioTMLFeatureGeneratorConfigurator}).
	 */
	public IBioTMLFeatureGeneratorConfigurator getFeatureConfiguration();
	
	/**
	 * 
	 * Method to get the model configuration.
	 * 
	 * @return Model configuration ({@link IBioTMLModelConfigurator}).
	 */
	public List<IBioTMLModelConfigurator> getModelConfigurations();
	
	/**
	 * 
	 * Method to get the model evaluation configuration.
	 * 
	 * @return Model evaluation configuration ({@link IBioTMLModelEvaluationConfigurator}).
	 */
	public IBioTMLModelEvaluationConfigurator getModelEvaluationConfiguration();
	
	
	/**
	 * 
	 * Method to evaluate the multi-model using the initialized evaluation configurations.
	 * 
	 * @return Map of evaluations by submodel.
	 * @throws BioTMLException
	 */
	public Map<String,IBioTMLModelEvaluationResults> evaluate()  throws BioTMLException;
	
	/**
	 * 
	 * Method to train the multi-model using the initialized configurations.
	 * 
	 * @throws BioTMLException
	 */
	public void train() throws BioTMLException;
	
	/**
	 * 
	 * Method to train the multi-model using the initialized configurations and save the trained submodels during the train model (reduces the memory usage).
	 * 
	 * @param modelPathAndFilename Absolute file path to save the model.
	 * @throws BioTMLException
	 */
	public void trainAndSaveFile(String modelPathAndFilename) throws BioTMLException;
	
	/**
	 * 
	 * Method to annotate a corpus using this multi-model.
	 * 
	 * @param corpus Corpus to be annotated with the model ({@link IBioTMLCorpus}).
	 * @return Annotated corpus ({@link IBioTMLCorpus}).
	 * @throws BioTMLException
	 */
	public IBioTMLCorpus annotate(IBioTMLCorpus corpus) throws BioTMLException;
	
	/**
	 * 
	 * Method to get all submodels present in the multi-model file.
	 * 
	 * @return List of submodels ({@link IBioTMLModel}).
	 */
	public List<IBioTMLModel> getModels();
	
	/**
	 * 
	 * Method that returns a file which contains a generated readme model file. 
	 * 
	 * @return File object that contains the model readme.
	 */
	public File generateReadmeFile();

}
