package com.silicolife.textmining.machinelearning.biotml.core.interfaces;

import java.io.Serializable;

/**
 * 
 * BioTML annotation interface.
 * 
 * @since 1.0.0
 * @version 1.0.1
 * @author Ruben Rodrigues ({@code rrodrigues@silicolife.com})
 */

public interface IBioTMLAnnotation extends Comparable<IBioTMLAnnotation>, Serializable {
	
	/**
	 * 
	 * Method to get the document ID associated with the annotation.
	 * 
	 * @return Document ID.
	 */
	public long getDocID();
	
	/**
	 * 
	 * Method to get the annotation type (e.g. gene, protein).
	 * 
	 * @return Annotation type string.
	 */
	public String getAnnotType();
	
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
	
	/**
	 * 
	 * Method to get the annotation score value.
	 * 
	 * @return score.
	 */
	public double getScore();
	
	/**
	 * 
	 * Method that compares the offsets of two Annotations.
	 * 
	 * @param annotationToCompare - Annotation ({@link IBioTMLAnnotation}) to compare.
	 * @return Boolean that validates if the two annotations have the same offsets.
	 */
	public boolean haveTheSameOffsets(IBioTMLAnnotation annotationToCompare);

}
