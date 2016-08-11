package com.silicolife.textmining.machinelearning.biotml.core.interfaces;

import java.io.Serializable;
import java.util.List;

import com.silicolife.textmining.machinelearning.biotml.core.exception.BioTMLException;

/**
 * 
 * BioTML sentence interface.
 * 
 * @since 1.0.0
 * @author Ruben Rodrigues ({@code rrodrigues@silicolife.com})
 */

public interface IBioTMLSentence extends Serializable {
	
	/**
	 * 
	 * Method to return a list of tokens.
	 * 
	 * @return List of {@link IBioTMLToken}. 
	 */
	public List<IBioTMLToken> getTokens();
	
	/**
	 * 
	 * Method to return a list of tokens strings.
	 * 
	 * @return Token string list.
	 */
	public List<String> getTokenStrings();
	
	/**
	 * 
	 * Method to get a {@link IBioTMLToken} by token index in sentence.
	 * 
	 * @param index - Integer index in tokens list.
	 * @return {@link IBioTMLToken}.
	 */
	public IBioTMLToken getToken(int index);
	
	/**
	 * 
	 * Method to get a BioTMLToken by token offsets.
	 * 
	 * @param startOffset - Start token offset in document text stream.
	 * @param endOffset - End token offset in document text stream.
	 * @return {@link IBioTMLToken}.
	 * @throws {@link BioTMLException}.
	 */
	public IBioTMLToken getTokenbyOffsets(long startOffset, long endOffset) throws BioTMLException;
	
	/**
	 * 
	 * Method to get a list of {@link IBioTMLToken} by token offsets.
	 * 
	 * @param startOffset - Start token offset in document text stream.
	 * @param endOffset - End token offset in document text stream.
	 * @return {@link IBioTMLToken}.
	 * @throws {@link BioTMLException}.
	 */
	public List<IBioTMLToken> getTokensbyOffsets(long startOffset, long endOffset) throws BioTMLException;
	
	/**
	 * 
	 * Method to get a array of token index in sentence by token offsets.
	 * 
	 * @param startOffset - Start token offset in document text stream.
	 * @param endOffset - End token offset in document text stream.
	 * @return Array of token index in sentence.
	 * @throws {@link BioTMLException}.
	 */
	public List<Integer> getTokenIndexsbyOffsets(long startOffset, long endOffset) throws BioTMLException;
	
	/**
	 * 
	 * Method to get the number of tokens present in the sentence.
	 * 
	 * @return Number of tokens.
	 */
	public int getTokenSize();
	
	/**
	 * 
	 * Method to get the sentence offsets pair.
	 * 
	 * @return {@link IBioTMLOffsetsPair}}
	 */
	public IBioTMLOffsetsPair getSentenceOffsetsPair();
	
	/**
	 * 
	 * Method to get the start sentence offset.
	 * 
	 * @return Start token offset.
	 */
	public long getStartSentenceOffset();
	
	/**
	 * 
	 * Method to get the end sentence offset.
	 * 
	 * @return End token offset.
	 */
	public long getEndSentenceOffset();
	
	/**
	 * 
	 * Method to get the sentence string.
	 * 
	 * @return Sentence string.
	 */
	public String toString();
}
