package com.silicolife.textmining.machinelearning.biotml.core.interfaces;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import com.silicolife.textmining.machinelearning.biotml.core.exception.BioTMLException;

/**
 * 
 * BioTML corpus interface.
 * 
 * @since 1.0.0
 * @version 1.0.1
 * @author Ruben Rodrigues ({@code rrodrigues@silicolife.com})
 */

public interface IBioTMLCorpus extends Serializable {
	
	/**
	 * 
	 * Method to get all documents.
	 * 
	 * @return List of {@link IBioTMLDocument}.
	 */
	public List<IBioTMLDocument> getDocuments();
	
	/**
	 * 
	 * Method to get all annotations in all documents.
	 * 
	 * @return List of {@link IBioTMLAnnotation}.
	 */
	public List<IBioTMLAnnotation> getAnnotations();
	
	/**
	 * 
	 * Method to get all relations between annotations in all documents;
	 * 
	 * @return List of {@link IBioTMLAnnotationsRelation}.
	 */
	public List<IBioTMLAnnotationsRelation> getRelations();
	
	/**
	 * 
	 * Method to get a all parts of documents (sentences) that contains annotations. 
	 * 
	 * @return - List of {@link IBioTMLDocument} with only sentences that contains annotations.
	 * @throws {@link BioTMLException}.
	 */
	public List<IBioTMLDocument> getSubDocumentsWithAnnotations() throws BioTMLException;
	
	/**
	 * 
	 * Method to get a {@link IBioTMLDocument} by document index list in corpus.
	 * 
	 * @param index - Integer index in corpus document list.
	 * @return {@link IBioTMLDocument}.
	 */
	public IBioTMLDocument getDocument(int index);
	
	/**
	 * 
	 * Method to get a {@link IBioTMLDocument} by document ID.
	 * 
	 * @param index - Document ID.
	 * @return {@link IBioTMLDocument}.
	 * @throws {@link BioTMLException}.
	 */
	public IBioTMLDocument getDocumentByID(long docID) throws BioTMLException;
	
	/**
	 * 
	 * Method to get a {@link IBioTMLDocument} by document external ID.
	 * 
	 * @param externalID - Document external ID.
	 * @return {@link IBioTMLDocument}.
	 * @throws {@link BioTMLException}.
	 */
	public IBioTMLDocument getDocumentByExternalID(String externalID) throws BioTMLException;
	
	/**
	 * 
	 * Method to get all annotations from a {@link IBioTMLDocument} ID.
	 * 
	 * @param docID - {@link IBioTMLDocument} ID.
	 * @return List of {@link IBioTMLDocument}.
	 */
	public List<IBioTMLAnnotation> getDocAnnotations(long docID);
	
	/**
	 * 
	 * Method to get all relations of annotations from a {@link IBioTMLDocument} ID.
	 * 
	 * @param docID - {@link IBioTMLDocument} ID.
	 * @return Set of {@link IBioTMLAnnotationsRelation}.
	 */
	public Set<IBioTMLAnnotationsRelation> getDocAnnotationRelations(long docID);
	
	/**
	 * 
	 * Method to get the relations of annotations with the best score  from a {@link IBioTMLDocument} ID.
	 * 
	 * @param docID - {@link IBioTMLDocument} ID.
	 * @return Set of {@link IBioTMLAnnotationsRelation}.
	 */
	public Set<IBioTMLAnnotationsRelation> getDocAnnotationRelationsWithBestScore(long docID);
	
	/**
	 * 
	 * Method to get all annotations from relations that are inputed.
	 * 
	 * @param relations - List of {@link IBioTMLAnnotationsRelation}.
	 * @return List of {@link IBioTMLAnnotation}.
	 */
	public List<IBioTMLAnnotation> getAnnotationsFromRelations(List<IBioTMLAnnotationsRelation> relations);
	
	/**
	 * 
	 * @param docID - Document ID.
	 * @param startOffset - Possible start offset of an annotation.
	 * @param endOffset - Possible end offset of an annotation.
	 * @return if found an {@link IBioTMLAnnotation}.
	 * @throws {@link BioTMLException}.
	 */
	public IBioTMLAnnotation getAnnotationFromDocAndOffsets(long docID, long startOffset, long endOffset) throws BioTMLException;

	/**
	 * 
	 * Get all annotations from a sentence in a document id
	 * 
	 * @param docID
	 * @param sentence
	 * @return
	 */
	public Set<IBioTMLAnnotation> getAnnotationsFromSentenceInDocumentId(long docID, IBioTMLSentence sentence);
	
	/**
	 * 
	 * Get all annotations from a token index in sentence in a document id.
	 * (A token could have multiple annotations)
	 * 
	 * @param docId
	 * @param sentence
	 * @param annotationTokenIndex
	 * @return
	 * @throws BioTMLException
	 */
	public Set<IBioTMLAnnotation> getAnnotationsFromSentenceInDocumentIdAndTokenIndex(long docId, IBioTMLSentence sentence, int annotationTokenIndex) throws BioTMLException;

	/**
	 * 
	 * Verifies if exists a anotation that contains the token.
	 * 
	 * @param annotations
	 * @param token
	 * @return
	 */
	public boolean isTokenInAnnotations(Set<IBioTMLAnnotation> annotations, IBioTMLToken token);

	/**
	 * 
	 * Get all annotations from a document offsets range.
	 * 
	 * @param docID
	 * @param startOffset
	 * @param endOffset
	 * @return
	 */
	public Set<IBioTMLAnnotation> getAnnotationsFromDocAndOffsets(long docID, long startOffset, long endOffset);
}
