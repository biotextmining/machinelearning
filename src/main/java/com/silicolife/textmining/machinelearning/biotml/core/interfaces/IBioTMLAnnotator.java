package com.silicolife.textmining.machinelearning.biotml.core.interfaces;

import java.util.List;

import com.silicolife.textmining.machinelearning.biotml.core.exception.BioTMLException;

/**
 * 
 * Represents annotator interface.
 * 
 * @since 1.0.0
 * @author Ruben Rodrigues ({@code rrodrigues@silicolife.com})
 */

public interface IBioTMLAnnotator {
	
	/**
	 * 
	 * Method to generate annotations in one unannotated corpus using a ML model.
	 * 
	 * @param model - {@link IBioTMLModel} used to annotate the corpus.
	 * @return {@link IBioTMLCorpus} annotated.
	 * @throws {@link BioTMLException}.
	 */
	public IBioTMLCorpus generateAnnotatedBioTMCorpus(IBioTMLModel model, int threads) throws BioTMLException;
	
	/**
	 * 
	 * Method to generate annotations in one unannotated corpus using a list of ML model.
	 * 
	 * @param models - List of {@link IBioTMLModel} used to annotate the corpus.
	 * @return {@link IBioTMLCorpus} annotated.
	 * @throws {@link BioTMLException}.
	 */
	public IBioTMLCorpus generateAnnotatedBioTMCorpus(List<IBioTMLModel> models, int threads) throws BioTMLException;
	
	/**
	 * 
	 * Method to get unannotated corpus.
	 * 
	 * @return {@link IBioTMLCorpus} unannotated.
	 */
	public IBioTMLCorpus getBasedBioTMCorpus();
	
	/**
	 * 
	 * Method to validate the model compatibility with the annotation algorithm.
	 * 
	 * @param model - {@link IBioTMLModel} used to annotate the corpus.
	 * @return Boolean that validates the model.
	 */
	public boolean validateModel(IBioTMLModel model);
	
	/**
	 * 
	 * Method to kill all threads and stop the corpus annotation.
	 * 
	 */
	public void stopAnnotator();
	
}
