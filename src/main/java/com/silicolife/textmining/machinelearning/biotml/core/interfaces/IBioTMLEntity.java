package com.silicolife.textmining.machinelearning.biotml.core.interfaces;

import java.io.Serializable;

/**
 * 
 * BioTML annotation interface.
 * 
 * @since 1.0.0
 * @version 1.0.2
 * @author Ruben Rodrigues ({@code rrodrigues@silicolife.com})
 */

public interface IBioTMLEntity extends IBioTMLAnnotation, Comparable<IBioTMLEntity>, Serializable {
	
	/**
	 * 
	 * Method to get the document ID associated with the annotation.
	 * 
	 * @return Document ID.
	 */
	public long getDocID();

	/**
	 * 
	 * Method to get the annotation offsets pair.
	 * 
	 * @return {@link IBioTMLOffsetsPair}
	 */
	public IBioTMLOffsetsPair getAnnotationOffsets();
	
	/**
	 * 
	 * Method to get the annotation start offset.
	 * 
	 * @return Start annotation offset.
	 */
	public long getStartOffset();
	
	/**
	 * 
	 * Method to get the annotation end offset.
	 * 
	 * @return End annotation offset.
	 */
	public long getEndOffset();

}
