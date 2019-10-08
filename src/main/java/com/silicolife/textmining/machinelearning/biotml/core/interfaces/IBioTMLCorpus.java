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
	 * Method to get all entities in all documents.
	 * 
	 * @return List of {@link IBioTMLEntity}.
	 */
	public List<IBioTMLEntity> getEntities();
	
	public List<IBioTMLEntity> getEntitiesByAnnotationTypes(Set<String> annotationTypes);
	
	/**
	 * 
	 * Method to get all events in all documents;
	 * 
	 * @return List of {@link IBioTMLEvent}.
	 */
	public List<IBioTMLEvent> getEvents();
	
	public List<IBioTMLEvent> getEventsByEventTypes(Set<String> eventTypes);
	
	/**
	 * 
	 * Method to get a all parts of documents (sentences) that contains annotations. 
	 * 
	 * @return - List of {@link IBioTMLDocument} with only sentences that contains annotations.
	 * @throws {@link BioTMLException}.
	 */
	public List<IBioTMLDocument> getSubDocumentsWithEntities() throws BioTMLException;
	
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
	public List<IBioTMLEntity> getDocEntities(long docID);
	
	/**
	 * 
	 * Method to get all events from a {@link IBioTMLDocument} ID.
	 * 
	 * @param docID - {@link IBioTMLDocument} ID.
	 * @return Set of {@link IBioTMLEvent}.
	 */
	public Set<IBioTMLEvent> getDocEvents(long docID);
	
	/**
	 * 
	 * Method to get all annotations from relations that are inputed.
	 * 
	 * @param relations - List of {@link IBioTMLEvent}.
	 * @return List of {@link IBioTMLEntity}.
	 */
	public List<IBioTMLEntity> getEntitiesFromEvents(List<IBioTMLEvent> relations);
	
	/**
	 * 
	 * @param docID - Document ID.
	 * @param startOffset - Possible start offset of an annotation.
	 * @param endOffset - Possible end offset of an annotation.
	 * @return if found an {@link IBioTMLEntity}.
	 * @throws {@link BioTMLException}.
	 */
	public IBioTMLEntity getEntityFromDocAndOffsets(long docID, long startOffset, long endOffset) throws BioTMLException;

	/**
	 * 
	 * Get all annotations from a sentence in a document id
	 * 
	 * @param docID
	 * @param sentence
	 * @return
	 */
	public Set<IBioTMLEntity> getEntitiesFromSentenceInDocumentId(long docID, IBioTMLSentence sentence);
	
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
	public Set<IBioTMLEntity> getEntitiesFromSentenceInDocumentIdAndTokenIndex(long docId, IBioTMLSentence sentence, int annotationTokenIndex) throws BioTMLException;

	/**
	 * 
	 * Verifies if exists a anotation that contains the token.
	 * 
	 * @param annotations
	 * @param token
	 * @return
	 */
	public boolean isTokenInEntities(Set<IBioTMLEntity> annotations, IBioTMLToken token);

	/**
	 * 
	 * Get all annotations from a document offsets range.
	 * 
	 * @param docID
	 * @param startOffset
	 * @param endOffset
	 * @return
	 */
	public Set<IBioTMLEntity> getEntitiesFromDocAndOffsets(long docID, long startOffset, long endOffset);
	
	public Set<IBioTMLEvent> getDocEventsWithBestScore(long docID);
	
	public void deleteDocumentByExternalID(String externalID) throws BioTMLException;
}
