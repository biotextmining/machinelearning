package com.silicolife.textmining.machinelearning.biotml.core.interfaces;

import com.silicolife.textmining.machinelearning.biotml.core.exception.BioTMLException;

/**
 * 
 * BioTML corpus writer interface.
 * 
 * @since 1.0.0
 * @author Ruben Rodrigues ({@code rrodrigues@silicolife.com})
 */

public interface IBioTMLCorpusWriter {
	
	/**
	 * 
	 * Method to write the {@link IBioTMLCorpus} into a gz file.
	 * 
	 * @param filenamepath Absolute gz file path.
	 * @throws BioTMLException
	 */
	public void writeGZBioTMLCorpusFile(String filenamepath) throws BioTMLException;
	
	/**
	 * 
	 * Method to write the {@link IBioTMLCorpus} into a gz file discarding annotations.
	 * 
	 * @param filenamepath Absolute gz file path.
	 * @throws BioTMLException
	 */
	public void writeGZBioTMLCorpusFileWithoutAnnotations(String filenamepath) throws BioTMLException;
	
	/**
	 * 
	 * Method to write the {@link IBioTMLCorpus} into three equal splitted parts by document (training, development and evaluation).
	 * 
	 * @param dirnamepath Absolute directory path to store the three gz files.
	 * @throws BioTMLException
	 */
	public void writeBioTMLCorpusFileSplitedForML(String dirnamepath) throws BioTMLException;

}
