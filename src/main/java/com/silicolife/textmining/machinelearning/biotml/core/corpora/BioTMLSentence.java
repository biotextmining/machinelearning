package com.silicolife.textmining.machinelearning.biotml.core.corpora;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.silicolife.textmining.machinelearning.biotml.core.exception.BioTMLException;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLSentence;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLToken;


/**
 * 
 * Represents a Sentence.
 * 
 * @since 1.0.0
 * @author Ruben Rodrigues ({@code rrodrigues@silicolife.com})
 */

public class BioTMLSentence implements IBioTMLSentence{
	
	private static final long serialVersionUID = 1L;
	private List<IBioTMLToken> sentence;
	private String source;
	/**
	 * 
	 * Initializes a sentence with a list of tokens.
	 * 
	 * @param sentence - List of {@link IBioTMLToken}.
	 * @param source - Sentence source string.
	 */
	public BioTMLSentence(List<IBioTMLToken> sentence, String source){
		this.sentence = sentence;
		this.source = source;
	}

	public List<IBioTMLToken> getTokens() {
		return sentence;
	}
	
	public List<String> getTokenStrings(){
		List<String> listTokens = new ArrayList<String>();
		for( IBioTMLToken token: getTokens()){
			listTokens.add(token.getToken());
		}
		return listTokens;
	}
	
	public IBioTMLToken getTokenbyOffsets(long start, long end) throws BioTMLException{
		if(getStartSentenceOffset() <= start && getEndSentenceOffset()>= end){
			Iterator<IBioTMLToken> intToken = getTokens().iterator();
			boolean majorThanStart = false;
			while(intToken.hasNext() && !majorThanStart){
				IBioTMLToken token = intToken.next();
				if(start<token.getStartOffset()){
					majorThanStart = true;
				}
				if((start == token.getStartOffset()) && (end == token.getEndOffset())){
					return token;
				}
			}
		}
		throw new BioTMLException(3);
	}
	
	public List<IBioTMLToken> getTokensbyOffsets(long start, long end) throws BioTMLException {
		List<IBioTMLToken> resultList = new ArrayList<IBioTMLToken>();
		if(getStartSentenceOffset() <= start && getEndSentenceOffset()>= end){
			Iterator<IBioTMLToken> intToken = getTokens().iterator();
			boolean majorThanStart = false;
			while(intToken.hasNext() && !majorThanStart){
				IBioTMLToken token = intToken.next();
				if(end<token.getStartOffset()){
					majorThanStart = true;
				}else if((start<=token.getStartOffset())&&(end>=token.getEndOffset())){
					resultList.add(token);
				}
			}
		}
		if(resultList.size()!=0)
			return resultList;
		throw new BioTMLException(4);
	}
	
	public List<Integer> getTokenIndexsbyOffsets(long startOffset, long endOffset) throws BioTMLException{
		List<Integer> tokenIndexs = new ArrayList<Integer>();
		if(getStartSentenceOffset() <= startOffset && getEndSentenceOffset()>= endOffset){
			Iterator<IBioTMLToken> intToken = getTokens().iterator();
			boolean majorThanStart = false;
			int idx = 0;
			while(intToken.hasNext() && !majorThanStart){
				IBioTMLToken token = intToken.next();
				if(token.getStartOffset()>endOffset){
					majorThanStart = true;
				}else if(token.getStartOffset()<=startOffset && startOffset < token.getEndOffset()){
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
		return getTokens().get(0).getStartOffset();
	}
	
	public long getEndSentenceOffset(){
		return getTokens().get(getTokens().size()-1).getEndOffset();
	}
	
	public String toString() {
		return source;
	}
	
}
