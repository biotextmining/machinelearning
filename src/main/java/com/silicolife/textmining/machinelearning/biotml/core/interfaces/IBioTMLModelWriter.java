package com.silicolife.textmining.machinelearning.biotml.core.interfaces;

import java.io.File;
import java.util.List;

import com.silicolife.textmining.machinelearning.biotml.core.exception.BioTMLException;

/**
 * 
 * BioTML model writer interface.
 * 
 * @since 1.0.0
 * @author Ruben Rodrigues ({@code rrodrigues@silicolife.com})
 */

public interface IBioTMLModelWriter {

	/**
	 * 
	 * Method to write a model to a file.
	 * 
	 * @param model Trained model that will be saved the GZ file.
	 * @throws BioTMLException
	 */
	public void writeGZModelFile(IBioTMLModel model) throws BioTMLException; 
	
	/**
	 * 
	 * Method to write a model to a file zip.
	 * 
	 * @param submodels List of models that will be saved in zip file.
	 * @param readmeFile Readme file to be added to zip file.
	 * @throws BioTMLException
	 */
	public void writeZIPModelFile(List<IBioTMLModel> submodels, File readmeFile) throws BioTMLException;
	
	/**
	 * 
	 * Method to write a model to a temporary file and return the path string. 
	 * 
	 * @param model Trained model that will be saved in temporary GZ file.
	 * @return Model path string.
	 */
	public String saveGZModelForMultiModel(IBioTMLModel model) throws BioTMLException;
	
	/**
	 * 
	 * Method to save the temporary model files into a zip file.
	 * 
	 * @param modelFilesPaths Model path file strings.
	 * @param readmeFile Readme file to be added to zip file.
	 * @throws BioTMLException
	 */
	public void writeZIPModelFilesSaved(List<String> modelFilesPaths, File readmeFile) throws BioTMLException;
	
	/**
	 * 
	 * @param multiModel Trained multi model
	 * @throws BioTMLException
	 */
	public void writeMultiModel(IBioTMLMultiModel multiModel) throws BioTMLException;
}
