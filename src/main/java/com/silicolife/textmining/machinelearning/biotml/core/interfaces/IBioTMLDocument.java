package com.silicolife.textmining.machinelearning.biotml.core.interfaces;

import java.io.Serializable;
import java.util.List;

import com.silicolife.textmining.machinelearning.biotml.core.exception.BioTMLException;

/**
 * 
 * BioTML document interface.
 * 
 * @since 1.0.0
 * @version 1.0.1
 * @author Ruben Rodrigues ({@code rrodrigues@silicolife.com})
 */

public interface IBioTMLDocument extends Serializable {
	
	/**
	 * 
	 * Method to return the document ID.
	 * 
	 * @return Document ID.
	 */
	public long getID();
	
	/**
	 * 
	 * Method to return the document title or name.
	 * 
	 * @return Title string.
	 */
	public String getTitle();
	
	
	/**
	 * 
	 * Method to return the document external ID.
	 * 
	 * @return Document external ID.
	 */
	public String getExternalID();
	
	/**
	 * 
	 * Method to get all document sentences.
	 * 
	 * @return List of {@link IBioTMLSentence}.
	 */
	public List<IBioTMLSentence> getSentences();
	
	/**
	 * 
	 * Method to get a {@link IBioTMLSentence} by sentence index in document.
	 * 
	 * @param index - Intenger index in sentence list.
	 * @return {@link IBioTMLSentence}.
	 */
	public IBioTMLSentence getSentence(int index);
	
	/**
	 * 
	 * Method to get a list of {@link IBioTMLSentence} that contains the inserted tokens.
	 * 
	 * @param tokens - List of {@link IBioTMLToken}.
	 * @return List of {@link IBioTMLSentence}.
	 */
	
	public List<IBioTMLSentence> getSentencesOfTokens(List<IBioTMLToken> tokens);
	
	/**
	 * 
	 * Method to get a {@link IBioTMLToken} by token offsets.
	 * 
	 * @param startOffset - Start token offset.
	 * @param endOffset - End token offset.
	 * @return {@link IBioTMLToken}.
	 * @throws {@link BioTMLException}.
	 */
	public IBioTMLToken getToken(long startOffset, long endOffset) throws BioTMLException;
	
	/**
	 * 
	 * Method to get a list of {@link IBioTMLToken} by start and end of tokens offsets.
	 * 
	 * @param startOffset - Start token offset.
	 * @param endOffset - End token offset.
	 * @return {@link IBioTMLToken}.
	 * @throws {@link BioTMLException}.
	 */
	public List<IBioTMLToken> getTokens(long startOffset, long endOffset) throws BioTMLException;
	
	/**
	 * 
	 * Method to get the document text stream.
	 * 
	 * @return Document text string.
	 */
	public String toString();
	
	
}
