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

public interface IBioTMLAnnotationsRelation extends Serializable {
	
	/**
	 * 
	 * Method to get all annotations present in the relation in a ordered set.
	 * 
	 * @return Ordered set of annotations.
	 */
	public Set<IBioTMLAnnotation> getRelation();
	
	/**
	 * 
	 * Method to get the document ID associated with the relation annotation.
	 * 
	 * @return Document ID.
	 */
	public long getDocID();
	
	/**
	 * 
	 * Method to get the relation type (e.g chemical-drug).
	 * 
	 * @return Relation type string.
	 */
	public String getRelationType();
	
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
	public boolean findAnnotationInRelation(IBioTMLAnnotation annot);
	
	/**
	 * 
	 * Method to find a {@link IBioTMLAnnotation} in the relation using the annotation offsets.
	 * 
	 * @param startOffset Annotation start offset.
	 * @param endOffset Annotation end offset.
	 * @return {@link IBioTMLAnnotation} found.
	 * @throws BioTMLException if is not found.
	 */
	public IBioTMLAnnotation getAnnotationInRelationByOffsets(long startOffset, long endOffset) throws BioTMLException;
	
	/**
	 * 
	 * Method to find the first {@link IBioTMLAnnotation} using the annotation type.
	 * 
	 * @param annotType Annotation type string.
	 * @return {@link IBioTMLAnnotation} found.
	 * @throws BioTMLException if is not found.
	 */
	public IBioTMLAnnotation getFirstAnnotationByType(String annotType) throws BioTMLException;
	
	/**
	 * 
	 * Given a {@link IBioTMLAnnotation} that is present in the relation, this method returns all left annotations related to the inserted annotation.
	 * 
	 * @param annot {@link IBioTMLAnnotation} present in the relation.
	 * @return Set of {@link IBioTMLAnnotation} at left of inserted annotation.
	 * @throws BioTMLException
	 */
	public Set<IBioTMLAnnotation> getAnnotsAtLeftOfAnnotation(IBioTMLAnnotation annot) throws BioTMLException;
	
	/**
	 * 
	 * Given a {@link IBioTMLAnnotation} that is present in the relation, this method returns all right annotations related to the inserted annotation.
	 * 
	 * @param annot {@link IBioTMLAnnotation} present in the relation.
	 * @return Set of {@link IBioTMLAnnotation} at right of inserted annotation.
	 * @throws BioTMLException
	 */
	public Set<IBioTMLAnnotation> getAnnotsAtRightOfAnnotation(IBioTMLAnnotation annot) throws BioTMLException;
	
	public String toString();
	
	public boolean haveTheSameOffsetsAndAnnotationTypes(IBioTMLAnnotationsRelation relation);

}
