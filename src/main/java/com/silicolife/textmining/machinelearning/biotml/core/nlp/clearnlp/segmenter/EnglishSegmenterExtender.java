package com.silicolife.textmining.machinelearning.biotml.core.nlp.clearnlp.segmenter;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import com.clearnlp.segmentation.EnglishSegmenter;
import com.silicolife.textmining.machinelearning.biotml.core.corpora.BioTMLDocument;
import com.silicolife.textmining.machinelearning.biotml.core.corpora.BioTMLSentence;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLDocument;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLSentence;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLToken;
import com.silicolife.textmining.machinelearning.biotml.core.nlp.clearnlp.tokenizer.AbstractTokenizer;

/**
 * 
 * Modified ClearNLP EnglishSegmenterExtender to return {@link IBioTMLDocument}.
 * 
 * @since 1.0.0
 * @author Ruben Rodrigues ({@code rrodrigues@silicolife.com})
 */

public class EnglishSegmenterExtender extends AbstractSegmenter{
	
	/** Patterns of terminal punctuation. */
	protected final Pattern  P_TERMINAL_PUNCTUATION = Pattern.compile("^(\\.|\\?|\\!)+$");
	protected final String[] L_BRACKETS = {"\"","(","{","["};
	protected final String[] R_BRACKETS = {"\"",")","}","]"};
	
	public EnglishSegmenterExtender(AbstractTokenizer tokenizer) {
		super(tokenizer);
	}
	
	public List<IBioTMLDocument> getSentencesFromDocuments(List<String> documentTexts)
	{
		List<IBioTMLDocument> documents = new ArrayList<IBioTMLDocument>();
		long docID = 0;
		for( String doc : documentTexts){
			documents.add(new BioTMLDocument(docID, String.valueOf(docID), getSentences(doc)));
			docID++;	
		}
		return documents;
	}
	

	
	/** Called by {@link EnglishSegmenter#getSentencesRaw(BufferedReader)}. */
	private void countBrackets(String str, int[] brackets)
	{
		if (str.equals("\""))
			brackets[0] += (brackets[0] == 0) ? 1 : -1;
		else
		{
			int i, size = brackets.length;
			
			for (i=1; i<size; i++)
			{
				if      (str.equals(L_BRACKETS[i]))
					brackets[i]++;
				else if (str.equals(R_BRACKETS[i]))
					brackets[i]--; 
			}
		}
	}
	
	/** Called by {@link EnglishSegmenter#getSentences(List<String> docInString)}. */
	private boolean isFollowedByBracket(String str, int[] brackets)
	{
		int i, size = R_BRACKETS.length;
		
		for (i=0; i<size; i++)
		{
			if (brackets[i] > 0 && str.equals(R_BRACKETS[i]))
				return true;
		}
		
		return false;
	}
	public List<IBioTMLSentence> getSentences(String document){
		List<IBioTMLSentence> sentences = new ArrayList<IBioTMLSentence>();
		List<IBioTMLToken> tokens = g_tokenizer.getTokens(document);
		int[] brackets = new int[R_BRACKETS.length];
		int bIdx, i, size = tokens.size();
		boolean isTerminal = false;
		String curr;
		
		for (i=0, bIdx=0; i<size; i++)
		{
			curr = tokens.get(i).getToken();
			countBrackets(curr, brackets);
			
			if (isTerminal || P_TERMINAL_PUNCTUATION.matcher(curr).find())
			{
				if (i+1 < size && isFollowedByBracket(tokens.get(i+1).getToken(), brackets))
				{
					isTerminal = true;
				} else{
					List<IBioTMLToken> sentence = new ArrayList<IBioTMLToken>(tokens.subList(bIdx, bIdx = i+1));
					long startOff = sentence.get(0).getStartOffset();
					long endoff = sentence.get(sentence.size()-1).getEndOffset();
					sentences.add(new BioTMLSentence(sentence, document.substring((int)startOff, (int)endoff)));
					isTerminal = false;
				}
			}
		}
		if (bIdx < size && !isTerminal){
			List<IBioTMLToken> sentence = new ArrayList<IBioTMLToken>(tokens.subList(bIdx, size));
			long startOff = sentence.get(0).getStartOffset();
			long endoff = sentence.get(sentence.size()-1).getEndOffset();
			sentences.add(new BioTMLSentence(sentence, document.substring((int)startOff, (int)endoff)));
		}
		return sentences;
	}
	

	public List<IBioTMLSentence> getSentences(BufferedReader fin)
	{
		List<IBioTMLSentence> sentences = new ArrayList<IBioTMLSentence>();
		List<IBioTMLToken> tokens = g_tokenizer.getTokens(fin);
		StringBuilder tmp = new StringBuilder();
		String line;
		try {
			while ((line = fin.readLine()) != null)
				tmp.append(line);
		} catch (IOException e) { e.printStackTrace(); }
		String document = tmp.toString();
		int[] brackets = new int[R_BRACKETS.length];
		int bIdx, i, size = tokens.size();
		boolean isTerminal = false;
		String curr;
		
		for (i=0, bIdx=0; i<size; i++)
		{
			curr = tokens.get(i).getToken();
			countBrackets(curr, brackets);
			
			if (isTerminal || P_TERMINAL_PUNCTUATION.matcher(curr).find())
			{
				if (i+1 < size && isFollowedByBracket(tokens.get(i+1).getToken(), brackets))
				{
					isTerminal = true;
				} else{
					List<IBioTMLToken> sentence = new ArrayList<IBioTMLToken>(tokens.subList(bIdx, bIdx = i+1));
					long startOff = sentence.get(0).getStartOffset();
					long endoff = sentence.get(sentence.size()-1).getEndOffset();
					sentences.add(new BioTMLSentence(sentence, document.substring((int)startOff, (int)endoff)));
					isTerminal = false;
				}
			}
		}
		
		if (bIdx < size && !isTerminal){
			List<IBioTMLToken> sentence = new ArrayList<IBioTMLToken>(tokens.subList(bIdx, size));
			long startOff = sentence.get(0).getStartOffset();
			long endoff = sentence.get(sentence.size()-1).getEndOffset();
			sentences.add(new BioTMLSentence(sentence, document.substring((int)startOff, (int)endoff)));
		}

		return sentences;
	}
}
