package com.silicolife.textmining.machinelearning.biotml.core.corpora;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.silicolife.textmining.machinelearning.biotml.core.exception.BioTMLException;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLOffsetsPair;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLSentence;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLToken;


/**
 * 
 * Represents a Sentence.
 * 
 * @since 1.0.0
 * @author Ruben Rodrigues ({@code rrodrigues@silicolife.com})
 */

public class BioTMLSentenceImpl implements IBioTMLSentence{
	
	private static final long serialVersionUID = 1L;
	private List<IBioTMLToken> sentence;
	private IBioTMLOffsetsPair sentenceOffsetsPair;
	private String source;
	/**
	 * 
	 * Initializes a sentence with a list of tokens.
	 * 
	 * @param sentence - List of {@link IBioTMLToken} that must be sorted.
	 * @param source - Sentence source string.
	 */
	public BioTMLSentenceImpl(List<IBioTMLToken> sentence, String source){
		this.sentence = sentence;
		this.sentenceOffsetsPair = new BioTMLOffsetsPairImpl(sentence.get(0).getStartOffset(), sentence.get(sentence.size()-1).getEndOffset());
		this.source = source;
	}

	public List<IBioTMLToken> getTokens() {
		return sentence;
	}
	
	public IBioTMLOffsetsPair getSentenceOffsetsPair() {
		return sentenceOffsetsPair;
	}

	public List<String> getTokenStrings(){
		List<String> listTokens = new ArrayList<String>();
		for( IBioTMLToken token: getTokens()){
			listTokens.add(token.getToken());
		}
		return listTokens;
	}
	
	public IBioTMLToken getTokenbyOffsets(long start, long end) throws BioTMLException{
		if(getSentenceOffsetsPair().containsInside(start, end)){
			Iterator<IBioTMLToken> intToken = getTokens().iterator();
			boolean majorThanStart = false;
			while(intToken.hasNext() && !majorThanStart){
				IBioTMLToken token = intToken.next();
				if(start<token.getStartOffset()){
					majorThanStart = true;
				}else if(token.getTokenOffsetsPair().offsetsEquals(start, end)){
					return token;
				}
			}
		}
		throw new BioTMLException(3);
	}
	
	public List<IBioTMLToken> getTokensbyOffsets(long start, long end) throws BioTMLException {
		List<IBioTMLToken> resultList = new ArrayList<IBioTMLToken>();
		if(getSentenceOffsetsPair().containsInside(start, end)){
			Iterator<IBioTMLToken> intToken = getTokens().iterator();
			boolean majorThanStart = false;
			while(intToken.hasNext() && !majorThanStart){
				IBioTMLToken token = intToken.next();
				if(end<token.getStartOffset()){
					majorThanStart = true;
				}else if(token.getTokenOffsetsPair().offsetsOverlap(start, end)){
					resultList.add(token);
				}
			}
		}
		if(resultList.size()!=0)
			return resultList;
		throw new BioTMLException(4);
	}
	
	public List<Integer> getTokenIndexsbyOffsets(long start, long end) throws BioTMLException{
		List<Integer> tokenIndexs = new ArrayList<Integer>();
		if(getSentenceOffsetsPair().containsInside(start, end)){
			Iterator<IBioTMLToken> intToken = getTokens().iterator();
			boolean majorThanStart = false;
			int idx = 0;
			while(intToken.hasNext() && !majorThanStart){
				IBioTMLToken token = intToken.next();
				if(token.getStartOffset()>end){
					majorThanStart = true;
				}else if(token.getTokenOffsetsPair().offsetsOverlap(start, end)){
					tokenIndexs.add(idx);
				}
				idx++;
			}
			return tokenIndexs;
		}
		throw new BioTMLException(4);
	}
	
	public IBioTMLToken getToken(int index){
		return getTokens().get(index);
	}

	public int getTokenSize(){
		return getTokens().size();
	}
	
	public long getStartSentenceOffset(){
		return getSentenceOffsetsPair().getStartOffset();
	}
	
	public long getEndSentenceOffset(){
		return getSentenceOffsetsPair().getEndOffset();
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((sentence == null) ? 0 : sentence.hashCode());
		result = prime * result + ((sentenceOffsetsPair == null) ? 0 : sentenceOffsetsPair.hashCode());
		result = prime * result + ((source == null) ? 0 : source.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BioTMLSentenceImpl other = (BioTMLSentenceImpl) obj;
		if (sentence == null) {
			if (other.sentence != null)
				return false;
		} else if (!sentence.equals(other.sentence))
			return false;
		if (sentenceOffsetsPair == null) {
			if (other.sentenceOffsetsPair != null)
				return false;
		} else if (!sentenceOffsetsPair.equals(other.sentenceOffsetsPair))
			return false;
		if (source == null) {
			if (other.source != null)
				return false;
		} else if (!source.equals(other.source))
			return false;
		return true;
	}

	public String toString() {
		return source;
	}
	
}
