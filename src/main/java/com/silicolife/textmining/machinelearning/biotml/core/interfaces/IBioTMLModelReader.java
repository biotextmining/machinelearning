package com.silicolife.textmining.machinelearning.biotml.core.interfaces;

import java.util.List;

import com.silicolife.textmining.machinelearning.biotml.core.exception.BioTMLException;

/**
 * 
 * BioTML model reader interface.
 * 
 * @since 1.0.0
 * @author Ruben Rodrigues ({@code rrodrigues@silicolife.com})
 */

public interface IBioTMLModelReader {
	
	/**
	 * 
	 * Method that validates if is a BioTML model or not. 
	 * 
	 * @param modelFileName Model absolute path string.
	 * @return Boolean that says if is or not compatible.
	 */
	public boolean validateModel(String modelFileName);
	
	/**
	 * 
	 * Method to get the model Information Extraction Type (e.g NER or RE).
	 * 
	 * @param modelFileName Model absolute path string.
	 * @return IE type (e.g NER or RE).
	 */
	public String getModelType(String modelFileName);
	
	/**
	 * 
	 * Method to load a trained model from a file.
	 * 
	 * @param file - GZ trained model absolute path file to be opened.
	 * @return {@link IBioTMLModel} loaded from file.
	 * @throws BioTMLException
	 */
	public IBioTMLModel loadModelFromGZFile(String modelFileName) throws BioTMLException;
	
	/**
	 * 
	 * Method to load a trained model from a file.
	 * 
	 * @param file - Zip trained model absolute path file to be opened.
	 * @return List of {@link IBioTMLModel} loaded from file.
	 * @throws BioTMLException
	 */
	public List<IBioTMLModel> loadModelFromZipFile(String modelFileName) throws BioTMLException;
	
	/**
	 * 
	 * Method to load configurations and informations from model file.
	 * 
	 * @param file - GZ trained model absolute path file.
	 * @return {@link IBioTMLModel} with configurations and informations only.
	 * @throws BioTMLException
	 */
	public IBioTMLModel loadConfigurationsModelFromGZFile(String modelFileName) throws BioTMLException;
	
	/**
	 * 
	 * Method to load configurations and informations from model file.
	 * 
	 * @param file - Zip trained model absolute path file.
	 * @return List of{@link IBioTMLModel} with configurations and informations only.
	 * @throws BioTMLException
	 */
	public List<IBioTMLModel> loadConfigurationsModelFromZipFile(String modelFileName) throws BioTMLException;
	
	/**
	 * 
	 * Method to unZip a Zip model file and return a list of submodels absolute temporary path strings.
	 * 
	 * @param modelFileName - Zip trained model absolute path file.
	 * @return List of submodels absolute temporary path strings.
	 * @throws BioTMLException
	 */
	public List<String> loadSubmodelsToStringFromZipFile(String modelFileName) throws BioTMLException;
}
