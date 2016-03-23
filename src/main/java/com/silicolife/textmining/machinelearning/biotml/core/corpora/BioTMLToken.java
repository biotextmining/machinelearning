package com.silicolife.textmining.machinelearning.biotml.core.corpora;

import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLToken;

/**
 * 
 * Represents a Token.
 * 
 * @since 1.0.0
 * @author Ruben Rodrigues ({@code rrodrigues@silicolife.com})
 */

public class BioTMLToken implements IBioTMLToken{

	private static final long serialVersionUID = 1L;
	private String token;
	private long startOffset;
	private long endOffset;
	
	/**
	 * 
	 * Initializes a token.
	 * 
	 * @param token - String that represents the token in text stream.
	 * @param startOffset - Staring token offset in text stream.
	 * @param endOffset - Ending token offset in text stream.
	 */

	public BioTMLToken( String token, long startOffset, long endOffset){
		this.token = token;
		this.startOffset = startOffset;
		this.endOffset = endOffset;
	}

	public String getToken() {
		return token;
	}

	public long getStartOffset() {
		return startOffset;
	}

	public long getEndOffset() {
		return endOffset;
	}
	
	public String toString() {
		return token;
	}

	public int compareTo(IBioTMLToken o) {
		if(	getToken().equals(o.getToken())
			&& getStartOffset()==o.getStartOffset()
			&& getEndOffset()==o.getEndOffset()){
			return 0;
		}
		if( getStartOffset()<o.getStartOffset() 
			|| getEndOffset()<o.getEndOffset()){
			return -1;
		}
		return 1;
	}
}
