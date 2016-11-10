package com.silicolife.textmining.machinelearning.biotml.core.interfaces;

import com.silicolife.textmining.machinelearning.biotml.core.exception.BioTMLException;
import com.silicolife.textmining.machinelearning.biotml.core.nlp.BioTMLNLPSystemsEnum;

/**
 * 
 * BioTML corpus reader interface.
 * 
 * @since 1.0.0
 * @author Ruben Rodrigues ({@code rrodrigues@silicolife.com})
 */

public interface IBioTMLCorpusReader {
	
	/**
	 * 
	 * Method to read the corpus from a folder.
	 * 
	 * @param corpusDirFolder Folder absolute path string.
	 * @param nlpSystem Selected {@link BioTMLNLPSystemsEnum} to tokenize.
	 * @return {@link IBioTMLCorpus} loaded.
	 * @throws BioTMLException
	 */
	public IBioTMLCorpus readBioTMLCorpusFromDirFolder(String corpusDirFolder, String nlpSystem) throws BioTMLException;
	
	/**
	 * 
	 * Method to read the document from a folder.
	 * 
	 * @param documentFile Document absolute path string.
	 * @param nlpSystem Selected {@link BioTMLNLPSystemsEnum} to tokenize.
	 * @return {@link IBioTMLCorpus} loaded.
	 * @throws BioTMLException
	 */
	public IBioTMLCorpus readBioTMLCorpusFromBioCFiles(String documentFile, String nlpSystem) throws BioTMLException;
	
	/**
	 * 
	 * Method to read the corpus from Biocreative Files.
	 * 
	 * @param documentFile  Biocreative documents file absolute path string.
	 * @param annotationsFile Biocreative annotations file absolute path string.
	 * @param nlpSystem Selected {@link BioTMLNLPSystemsEnum} to tokenize.
	 * @return {@link IBioTMLCorpus} loaded.
	 * @throws BioTMLException
	 */
	public IBioTMLCorpus readBioTMLCorpusFromBioCFiles(String documentFile, String annotationsFile,  String nlpSystem) throws BioTMLException;
	
	/**
	 * 
	 * Method to read a gz serialized corpus file. 
	 * 
	 * @param filename Corpus absolute file path string.
	 * @return {@link IBioTMLCorpus} loaded.
	 * @throws BioTMLException
	 */
	public IBioTMLCorpus readBioTMLCorpusFromFile(String filename) throws BioTMLException;

}
