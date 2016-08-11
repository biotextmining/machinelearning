package com.silicolife.textmining.machinelearning.biotml.core.interfaces;

import com.silicolife.textmining.machinelearning.biotml.core.exception.BioTMLException;

import cc.mallet.pipe.Pipe;
import cc.mallet.types.InstanceList;

/**
 * 
 * Corpus ({@link IBioTMLCorpus}) converter into Mallet Instances interface.
 * 
 * @since 1.0.0
 * @author Ruben Rodrigues ({@code rrodrigues@silicolife.com})
 */

public interface IBioTMLCorpusToInstanceMallet {
	
	/**
	 * 
	 * Method to get the corpus to be converted.
	 * 
	 * @return corups {@link IBioTMLCorpus}.
	 */
	public IBioTMLCorpus getCorpusToConvert();
	
	/**
	 * 
	 * Method to get the annotation type to be considered as entity or relation class in ML training.
	 * 
	 * @return Annotation type string.
	 */
	public String getConsideredAnnotationType();
	
	/**
	 * 
	 * Method to get the information extraction annotation type.
	 * 
	 * @return String that represents the IE type (e.g. NER or RE).
	 */
	public String getIEAnnotationType();
	
	
	/**
	 * 
	 * @param p Mallet Pipe with classes for features incorporation in instance list.
	 * @param threads Number of threads used in the system.
	 * @param configuration Features configuration to generate the ML features.
	 * @return Mallet InstanceList.
	 * @throws BioTMLException.
	 */
	public InstanceList exportToMalletFeatures(Pipe p, int threads, IBioTMLFeatureGeneratorConfigurator configuration) throws BioTMLException;
	
	/**
	 * 
	 * Method that stops all threads that generate the features. It's used to interrupt the process.
	 * 
	 */
	public void stopAllFeatureThreads();
	

}