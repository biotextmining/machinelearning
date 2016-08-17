package com.silicolife.textmining.machinelearning.biotml.core.corpora;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.silicolife.textmining.machinelearning.biotml.core.exception.BioTMLException;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLCorpus;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLDocument;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLSentence;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLToken;

/**
 * 
 * Represents a document.
 * 
 * @since 1.0.0
 * @version 1.0.1
 * @author Ruben Rodrigues ({@code rrodrigues@silicolife.com})
 */

public class BioTMLDocumentImpl implements IBioTMLDocument{

	private static final long serialVersionUID = 1L;
	private long id;
	private String title;
	private String externalID;
	private List<IBioTMLSentence> sentences;


	/**
	 * 
	 * Initializes a document to be stored in one corpus.
	 * 
	 * @param id - Document ID in {@link IBioTMLCorpus}.
	 * @param name - Document title or name.
	 * @param sentences - List of {@link IBioTMLSentence} that stores all tokens and offsets of document raw text.
	 */

	public BioTMLDocumentImpl(long id, String title, List<IBioTMLSentence> sentences){
		this.id = id;
		this.title = title;
		this.externalID = new String();
		this.sentences = sentences;
	}

	/**
	 * 
	 * Initializes a document to be stored in one corpus.
	 * 
	 * @param id - Document ID in {@link IBioTMLCorpus}.
	 * @param title - Document title or name.
	 * @param externalID - Document external ID (e.g. PUBMED or doi).
	 * @param sentences - List of {@link IBioTMLSentence} that stores all tokens and offsets of document raw text.
	 */

	public BioTMLDocumentImpl(long id, String title, String externalID, List<IBioTMLSentence> sentences){
		this(id, title, sentences);
		this.externalID = externalID;
	}


	public long getID(){
		return id;
	}

	public String getTitle(){
		return title;
	}

	public String getExternalID(){
		return externalID;
	}

	public List<IBioTMLSentence> getSentences(){
		return sentences;
	}

	public IBioTMLSentence getSentence(int index){
		return getSentences().get(index);
	}

	public List<IBioTMLSentence> getSentencesOfTokens(List<IBioTMLToken> tokens) {
		List<IBioTMLSentence> sentences = new ArrayList<IBioTMLSentence>();
		if(!tokens.isEmpty()){
			Collections.sort(tokens);
			Iterator<IBioTMLSentence> itSent = getSentences().iterator();
			Iterator<IBioTMLToken> itToken = tokens.iterator();
			boolean nextSentence = false;
			IBioTMLToken token = null;
			while(itSent.hasNext() && itToken.hasNext()){
				IBioTMLSentence sentence = itSent.next();
				if(!nextSentence){
					token = itToken.next();
				}
				if(sentence.getSentenceOffsetsPair().containsInside(token.getTokenOffsetsPair())){
					sentences.add(sentence);
					nextSentence = false;
					while(itToken.hasNext() && !nextSentence){
						token = itToken.next();
						if(!sentence.getSentenceOffsetsPair().containsInside(token.getTokenOffsetsPair())){
							nextSentence = true;
						}
					}
				}else{
					nextSentence = true;
				}
			}
		}
		return sentences;
	}

	public IBioTMLToken getToken(long startOffset, long endOffset) throws BioTMLException{
		Iterator<IBioTMLSentence> itSent = getSentences().iterator();
		while(itSent.hasNext()){
			IBioTMLSentence sentence = itSent.next();
			if(sentence.getSentenceOffsetsPair().containsInside(startOffset, endOffset)){
				return sentence.getTokenbyOffsets(startOffset, endOffset);
			}
		}
		throw new BioTMLException(1);
	}

	public List<IBioTMLToken> getTokens(long startOffset, long endOffset) throws BioTMLException {
		Iterator<IBioTMLSentence> itSent = getSentences().iterator();
		while(itSent.hasNext()){
			IBioTMLSentence sentence = itSent.next();
			if(sentence.getSentenceOffsetsPair().containsInside(startOffset, endOffset)){
				return sentence.getTokensbyOffsets(startOffset, endOffset);
			}
		}
		throw new BioTMLException(2);
	}

	public String toString(){
		String text = new String();
		IBioTMLSentence sentencebefore = null;
		for(IBioTMLSentence sentence : getSentences()){
			if( sentencebefore == null){
				text = sentence.toString();
			} else{
				long numspaces =  sentence.getStartSentenceOffset() - sentencebefore.getEndSentenceOffset();
				char[] chars = new char[(int) numspaces];
				Arrays.fill(chars, ' ');
				String spacement = new String(chars);
				text = text + spacement + sentence.toString();

			}
			sentencebefore = sentence;
		}
		return text;
	}

	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	@Override
	public boolean equals(Object document) {
		if (document instanceof IBioTMLDocument) {
			IBioTMLDocument otherDocument = (IBioTMLDocument) document;
			if (toString().equals(otherDocument.toString())) {
				return true;
			}
		}
		return false;
	}

}
