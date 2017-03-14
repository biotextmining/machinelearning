package com.silicolife.textmining.machinelearning.biotml.core.interfaces;

import java.io.Serializable;

import com.silicolife.textmining.machinelearning.biotml.core.exception.BioTMLException;

/**
 * 
 * BioTML relation annotation interface.
 * 
 * @since 1.0.0
 * @author Ruben Rodrigues ({@code rrodrigues@silicolife.com})
 */

public interface IBioTMLEvent extends Serializable {
	
	@SuppressWarnings("rawtypes")
	public IBioTMLAssociation getAssociation();
	
	public IBioTMLAnnotation getTrigger();
	
	public IBioTMLAnnotation getEntity();
	
	/**
	 * 
	 * Method to get the document ID associated with the relation annotation.
	 * 
	 * @return Document ID.
	 */
	public long getDocID();
	
	/**
	 * 
	 * Method to get the Event type.
	 * 
	 * @return Event type string.
	 */
	public String getEventType();
	
	/**
	 * 
	 * Method to get the associated prediction score given to relation.
	 * If is not defined, the score is 1.0 (range from 0.0 to 1.0). 
	 * 
	 * @return Relation prediction score.
	 */
	public double getScore();
	
	/**
	 * 
	 * Method to find a {@link IBioTMLAnnotation} in the relation.
	 * 
	 * @param annot Annotation to find {@link IBioTMLAnnotation}.
	 * @return Boolean if is or not present in this relation.
	 */
	public boolean findAnnotationInEvent(IBioTMLAnnotation annot);
	
	/**
	 * 
	 * Method to find a {@link IBioTMLAnnotation} in the relation using the annotation offsets.
	 * 
	 * @param startOffset Annotation start offset.
	 * @param endOffset Annotation end offset.
	 * @return {@link IBioTMLAnnotation} found.
	 * @throws BioTMLException if is not found.
	 */
	public IBioTMLAnnotation getAnnotationInEventByOffsets(long startOffset, long endOffset) throws BioTMLException;

}
