package com.silicolife.textmining.machinelearning.biotml.core.interfaces;

import java.io.Serializable;
import java.util.Set;

import com.silicolife.textmining.machinelearning.biotml.core.exception.BioTMLException;

/**
 * 
 * BioTML relation annotation interface.
 * 
 * @since 1.0.0
 * @author Ruben Rodrigues ({@code rrodrigues@silicolife.com})
 */

public interface IBioTMLEvent extends IBioTMLAnnotation, Serializable {
	
	@SuppressWarnings("rawtypes")
	public IBioTMLAssociation getAssociation();
	
	public IBioTMLEntity getTrigger();
	
	public IBioTMLEntity getEntity();
	
	/**
	 * 
	 * Method to get the document ID associated with the relation annotation.
	 * 
	 * @return Document ID.
	 */
	public long getDocID();
	
	/**
	 * 
	 * Method to find a {@link IBioTMLEntity} in the relation.
	 * 
	 * @param annot Annotation to find {@link IBioTMLEntity}.
	 * @return Boolean if is or not present in this relation.
	 */
	public boolean findAnnotationInEvent(IBioTMLEntity annot);
	
	/**
	 * 
	 * Method to find a {@link IBioTMLEntity} in the relation using the annotation offsets.
	 * 
	 * @param startOffset Annotation start offset.
	 * @param endOffset Annotation end offset.
	 * @return {@link IBioTMLEntity} found.
	 * @throws BioTMLException if is not found.
	 */
	public IBioTMLEntity getAnnotationInEventByOffsets(long startOffset, long endOffset) throws BioTMLException;
	
	public Set<IBioTMLEntity> getAllAnnotationsFromEvent();

}
