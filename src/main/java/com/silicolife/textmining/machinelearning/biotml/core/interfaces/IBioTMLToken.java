package com.silicolife.textmining.machinelearning.biotml.core.interfaces;

import java.io.Serializable;

/**
 * 
 * BioTML Token interface.
 * 
 * @since 1.0.0
 * @author Ruben Rodrigues ({@code rrodrigues@silicolife.com})
 */

public interface IBioTMLToken extends Serializable, Comparable<IBioTMLToken>{

	/**
	 * 
	 * Method to return the token string.
	 * 
	 * @return Token string.
	 */
	public String getToken();

	/**
	 * 
	 * Method to return the start token offset.
	 * 
	 * @return Start token offset.
	 */
	public long getStartOffset();
	
	/**
	 * 
	 * Method to return the end token offset.
	 * 
	 * @return End token offset.
	 */
	public long getEndOffset();
	
}
