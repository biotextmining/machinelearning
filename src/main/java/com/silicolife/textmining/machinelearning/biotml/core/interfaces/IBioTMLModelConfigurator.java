package com.silicolife.textmining.machinelearning.biotml.core.interfaces;

import java.io.Serializable;

import com.silicolife.textmining.machinelearning.biotml.core.BioTMLModelLabelType;
import com.silicolife.textmining.machinelearning.biotml.core.mllibraries.BioTMLAlgorithm;

import libsvm.svm_parameter;

/**
 * 
 * Represents model configurator interface.
 * 
 * @since 1.0.0
 * @author Ruben Rodrigues ({@code rrodrigues@silicolife.com})
 */

public interface IBioTMLModelConfigurator extends Serializable{
	
	/**
	 * 
	 * Method to get the model entity or relation class type.
	 * 
	 * @return String that represents the model entity or relation class type.
	 */
	public String getClassType();
	
	/**
	 * 
	 * Method to get the information extraction type that the model is trained.
	 * 
	 * @return String that represents the model IE type.
	 */
	public String getIEType();
	
	
	/**
	 * 
	 * Method to get the number of threads.
	 * 
	 * @return Number of threads.
	 */
	public int getNumThreads();
	
	/**
	 * 
	 * Method to get the model algorithm type.
	 * 
	 * @return Algorithm type string.
	 */
	public BioTMLAlgorithm getAlgorithmType();

	/**
	 * 
	 * Method to get the SVM parameters.
	 * 
	 * @return A LibSVM object that represents the SVM parameters.
	 */
	public svm_parameter getSVMParameters();
	
	public IBioTMLREMethodologyConfiguration getREMethodology();
	
	/**
	 * 
	 * Method to get the NLP system to tokenize the text.
	 * 
	 * @return Tokenization NLP system.
	 */
	public String getUsedNLPSystem();
	
	/**
	 * 
	 * Method to set the number of threads.
	 * 
	 */
	public void setNumThreads(int numThreads);
	
	/**
	 * 
	 * Method to set the SVM parameters.
	 * 
	 */
	public void setSVMParameters(svm_parameter svmparams);
	
	/**
	 * 
	 * Method to set the model algorithm type.
	 * 
	 */
	public void setAlgorithmType(BioTMLAlgorithm algorithm);
	
	/**
	 * 
	 * Method to the NLP system to tokenize the text.
	 * 
	 */
	public void setUsedNLPSystem(String nlpSystem);
	
	public void setREMethodology(IBioTMLREMethodologyConfiguration reMethodology);
	
	public IBioTMLMalletTransducerConfiguration getTransducerConfiguration();
	
	public void setTransducerConfiguration(IBioTMLMalletTransducerConfiguration transducerConfiguration);
	
	public BioTMLModelLabelType getModelLabelType();

	public void setModelLabelType(BioTMLModelLabelType modelLabelType);
}
