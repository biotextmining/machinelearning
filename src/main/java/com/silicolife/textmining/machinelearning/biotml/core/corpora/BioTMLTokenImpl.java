package com.silicolife.textmining.machinelearning.biotml.core.corpora;

import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLOffsetsPair;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLToken;

/**
 * 
 * Represents a Token.
 * 
 * @since 1.0.2
 * @author Ruben Rodrigues ({@code rrodrigues@silicolife.com})
 */

public class BioTMLTokenImpl implements IBioTMLToken{

	private static final long serialVersionUID = 1L;
	private String token;
	private IBioTMLOffsetsPair tokenOffsetsPair;
	
	/**
	 * 
	 * Initializes a token.
	 * 
	 * @param token - String that represents the token in text stream.
	 * @param startOffset - Staring token offset in text stream.
	 * @param endOffset - Ending token offset in text stream.
	 */

	public BioTMLTokenImpl( String token, long startOffset, long endOffset){
		this.token = token;
		this.tokenOffsetsPair = new BioTMLOffsetsPairImpl(startOffset, endOffset);
	}

	public String getToken() {
		return token;
	}

	public IBioTMLOffsetsPair getTokenOffsetsPair() {
		return tokenOffsetsPair;
	}

	public long getStartOffset() {
		return getTokenOffsetsPair().getStartOffset();
	}

	public long getEndOffset() {
		return getTokenOffsetsPair().getEndOffset();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((token == null) ? 0 : token.hashCode());
		result = prime * result + ((tokenOffsetsPair == null) ? 0 : tokenOffsetsPair.hashCode());
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
		BioTMLTokenImpl other = (BioTMLTokenImpl) obj;
		if (token == null) {
			if (other.token != null)
				return false;
		} else if (!token.equals(other.token))
			return false;
		if (tokenOffsetsPair == null) {
			if (other.tokenOffsetsPair != null)
				return false;
		} else if (!tokenOffsetsPair.equals(other.tokenOffsetsPair))
			return false;
		return true;
	}

	public int compareTo(IBioTMLToken o) {
		
		if(getToken().equals(o.getToken()) && getTokenOffsetsPair().equals(o.getTokenOffsetsPair()))
			return 0;
	
		return getTokenOffsetsPair().compareTo(o.getTokenOffsetsPair());
	}
	
	public String toString() {
		return token;
	}
}
