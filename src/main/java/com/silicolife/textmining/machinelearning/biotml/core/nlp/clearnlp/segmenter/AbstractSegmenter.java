package com.silicolife.textmining.machinelearning.biotml.core.nlp.clearnlp.segmenter;

import java.io.BufferedReader;
import java.util.List;

import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLSentence;
import com.silicolife.textmining.machinelearning.biotml.core.nlp.clearnlp.tokenizer.AbstractTokenizer;

/**
 * 
 * Modified ClearNLP AbstractSegmenter to return sentences as {@link IBioTMLSentence}.
 * 
 * @since 1.0.0
 * @author Ruben Rodrigues ({@code rrodrigues@silicolife.com})
 */

abstract public class AbstractSegmenter
{
	protected AbstractTokenizer g_tokenizer;
	
	public AbstractSegmenter(AbstractTokenizer tokenizer)
	{
		g_tokenizer = tokenizer;
	}
	
	/**
	 * Segments a string.
	 * @param str string to retrieve sentences list.
	 * @return a list of sentences, which are arrays of tokens, from string.
	 */
	abstract public List<IBioTMLSentence> getSentences(String document);
	
	/**
	 * Segments the text from the specific reader.
	 * @param fin the reader to retrieve sentences list.
	 * @return a list of sentences, which are arrays of tokens, from the specific reader.
	 */
	abstract public List<IBioTMLSentence> getSentences(BufferedReader fin);

}
