package com.silicolife.textmining.machinelearning.biotml.core.nlp.clearnlp.tokenizer;

import java.io.BufferedReader;
import java.util.List;

import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLToken;
import com.silicolife.textmining.machinelearning.biotml.core.nlp.clearnlp.StringBooleanPair;

/**
 * 
 * Modified ClearNLP AbstractTokenizer to accept offsets and return {@link IBioTMLToken}.
 * 
 * @since 1.0.0
 * @author Ruben Rodrigues ({@code rrodrigues@silicolife.com})
 */

abstract public class AbstractTokenizer
{
	protected boolean b_twit   = false;
	protected boolean b_userId = true;
	
	public void setTwit(boolean isTwit)
	{
		b_twit = isTwit;
	}
	
	public void setUserID(boolean isUserID)
	{
		b_userId = isUserID;
	}
	
	/**
	 * Convert a string into a list of boolean pairs to be processed in getTokenList.
	 * @param str string to retrieve tokens list.
	 * @return a list of tokens from the string.
	 */

	abstract public List<StringBooleanPair> getTokenList(String str);
	
	/**
	 * Tokenize a string.
	 * @param str string to retrieve tokens list.
	 * @return a list of tokens from string.
	 */
	abstract public List<IBioTMLToken> getTokens(String str);
	
	/**
	 * Tokenize the text from the specific reader.
	 * @param fin the reader to retrieve tokens list.
	 * @return a list of tokens from the specific reader.
	 */
	abstract public List<IBioTMLToken> getTokens(BufferedReader fin);
}

