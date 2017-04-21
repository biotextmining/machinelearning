package com.silicolife.textmining.machinelearning.biotml.core.corpora;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.silicolife.textmining.machinelearning.biotml.core.exception.BioTMLException;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLAssociation;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLCorpus;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLDocument;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLEntity;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLEvent;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLSentence;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLToken;

/**
 * 
 * Represents a corpus.
 * 
 * @since 1.0.0
 * @version 1.0.1
 * @author Ruben Rodrigues ({@code rrodrigues@silicolife.com})
 */

public class BioTMLCorpusImpl implements IBioTMLCorpus{

	private static final long serialVersionUID = 1L;
	private List<IBioTMLDocument> documents;
	private List<IBioTMLEntity> annotations;
	private List<IBioTMLEvent> events;
	private String name;

	/**
	 * 
	 * Initializes a corpus without annotations.
	 * 
	 * @param documents - List of {@link IBioTMLDocument} tokenized.
	 * @param name - Corpus name.
	 */

	public BioTMLCorpusImpl(List<IBioTMLDocument> documents, String name){
		this(documents, new ArrayList<>(), new ArrayList<>(), name);
	}

	/**
	 * 
	 * Initializes a corpus with annotations
	 * 
	 * @param documents - List of {@link IBioTMLDocument} tokenized.
	 * @param annotations - List of {@link IBioTMLEntity} with offsets and class types.
	 * @param name - Corpus name.
	 */

	public BioTMLCorpusImpl(List<IBioTMLDocument> documents, List<IBioTMLEntity> annotations, String name){
		this(documents, annotations, new ArrayList<>(), name);
	}

	/**
	 * 
	 * Initializes a corpus with annotations and annotations relation
	 * 
	 * @param documents - List of {@link IBioTMLDocument} tokenized.
	 * @param annotations - List of {@link IBioTMLEntity} with offsets and class types.
	 * @param name - Corpus name.
	 */

	public BioTMLCorpusImpl(List<IBioTMLDocument> documents, List<IBioTMLEntity> annotations, List<IBioTMLEvent> events, String name){
		this.documents = documents;
		this.annotations = addMissingAnnotationsFromEvents(annotations, events);
		this.events = events;
		this.name = name;
	}

	public List<IBioTMLDocument> getDocuments() {
		return documents;
	}

	public List<IBioTMLEntity> getAnnotations(){
		return annotations;
	}

	public List<IBioTMLEvent> getEvents(){
		return events;
	}

	public IBioTMLDocument getDocument(int index){
		return getDocuments().get(index);
	}

	public List<IBioTMLDocument> getSubDocumentsWithAnnotations() throws BioTMLException{
		List<IBioTMLDocument> subdocuments = new ArrayList<IBioTMLDocument>();
		if(getAnnotations().isEmpty()){
			return subdocuments;
		}
		for(IBioTMLDocument doc : getDocuments()){
			List<IBioTMLEntity> annots = getAllDocAnnotations(doc.getID());
			List<IBioTMLToken> tokens = new ArrayList<IBioTMLToken>();
			for(IBioTMLEntity annot: annots){
				tokens.addAll(doc.getTokens(annot.getStartOffset(), annot.getEndOffset()));
			}
			subdocuments.add(new BioTMLDocumentImpl(doc.getID(), doc.getTitle(), doc.getSentencesOfTokens(tokens)));
		}
		return subdocuments;
	}

	public IBioTMLDocument getDocumentByID(long docID) throws BioTMLException{
		Iterator<IBioTMLDocument> intDoc = getDocuments().iterator();
		while(intDoc.hasNext()){
			IBioTMLDocument doc = intDoc.next();
			if(doc.getID() == docID){
				return doc;
			}
		}
		throw new BioTMLException(0);
	}
	
	public IBioTMLDocument getDocumentByExternalID(String externalID) throws BioTMLException{
		for(IBioTMLDocument document : getDocuments()){
			if(document.getExternalID().equals(externalID)){
				return document;
			}
		}
		throw new BioTMLException(0);
	}
	
	public List<IBioTMLEntity> getDocAnnotations(long docID){
		return retrieveAnnotationsWithBestScore(docID);
	}

	public List<IBioTMLEntity> getAllDocAnnotations(long docID){
		List<IBioTMLEntity> docAnnotations = new ArrayList<IBioTMLEntity>();
		for( IBioTMLEntity annotation : getAnnotations()){
			if(annotation.getDocID() == docID){
				docAnnotations.add(annotation);
			}
		}
		return docAnnotations;
	}

	public Set<IBioTMLEvent> getDocAnnotationEvents(long docID){
		Set<IBioTMLEvent> docAnnotationRelations = new HashSet<IBioTMLEvent>();
		for( IBioTMLEvent relation : getEvents()){
			if(relation.getDocID() == docID){
				docAnnotationRelations.add(relation);
			}
		}
		return docAnnotationRelations;
	}

	private List<IBioTMLEntity> retrieveAnnotationsWithBestScore(long docID){
		List<IBioTMLEntity> annotations = getAllDocAnnotations(docID);
		List<IBioTMLEntity> finalAnnotations = new ArrayList<IBioTMLEntity>();
		if(!annotations.isEmpty()){
			Collections.sort(annotations);
			Iterator<IBioTMLEntity> itAnnot = annotations.iterator();
			IBioTMLEntity prevAnnot = null;
			while(itAnnot.hasNext()){
				if(prevAnnot == null){
					prevAnnot = itAnnot.next();
					finalAnnotations.add(prevAnnot);
				}else{
					IBioTMLEntity currentAnnot = itAnnot.next();
					if((prevAnnot.getStartOffset() == currentAnnot.getStartOffset()) 
							&& (prevAnnot.getEndOffset()== currentAnnot.getEndOffset())){
						if(!(prevAnnot.getAnnotationScore()>=currentAnnot.getAnnotationScore())){
							finalAnnotations.set(finalAnnotations.size()-1, currentAnnot);
						}
					}
					finalAnnotations.add(currentAnnot);
				}
			}
		}
		return finalAnnotations;
	}

	public List<IBioTMLEntity> getAnnotationsFromEvents(List<IBioTMLEvent> relations){
		List<IBioTMLEntity> annotsres = new ArrayList<IBioTMLEntity>();
		Set<IBioTMLEntity> annots = new HashSet<IBioTMLEntity>();
		for(IBioTMLEvent relation: relations){
			if(relation.getTrigger() != null)
				annots.add(relation.getTrigger());
			if(relation.getEntity()!=null)
				annots.add(relation.getEntity());
		}
		annotsres.addAll(annots);
		return annotsres;
	}

	public IBioTMLEntity getAnnotationFromDocAndOffsets(long docID, long startOffset, long endOffset) throws BioTMLException{
		List<IBioTMLEntity> annots = getAllDocAnnotations(docID);
		for(IBioTMLEntity annot : annots){
			if(annot.getAnnotationOffsets().offsetsOverlap(startOffset, endOffset)){
				return annot;
			}
		}
		throw new BioTMLException(29);
	}
	
	@Override
	public Set<IBioTMLEntity> getAnnotationsFromDocAndOffsets(long docID, long startOffset, long endOffset){
		Set<IBioTMLEntity> resultAnnotations = new HashSet<>();
		List<IBioTMLEntity> annots = getAllDocAnnotations(docID);
		for(IBioTMLEntity annot : annots){
			if(annot.getAnnotationOffsets().offsetsOverlap(startOffset, endOffset)){
				resultAnnotations.add(annot);
			}
		}
		return resultAnnotations;
	}
	
	@Override
	public Set<IBioTMLEntity> getAnnotationsFromSentenceInDocumentId(long docID, IBioTMLSentence sentence){
		Set<IBioTMLEntity> annotations = new HashSet<>();
		IBioTMLDocument document;
		try {
			document = getDocumentByID(docID);
		} catch (BioTMLException e) {
			return annotations;
		}
		List<IBioTMLEntity> documentAnnotations = getDocAnnotations(docID);
		if(!documentAnnotations.isEmpty() && document.getSentences().contains(sentence)){
			Iterator<IBioTMLEntity> itAnnot = documentAnnotations.iterator();
			while(itAnnot.hasNext()){
				IBioTMLEntity annotation = itAnnot.next();
				if(sentence.getSentenceOffsetsPair().containsInside(annotation.getAnnotationOffsets())){
					annotations.add(annotation);
				}
			}
		}
		return annotations;
	}
	
	@Override
	public Set<IBioTMLEntity> getAnnotationsFromSentenceInDocumentIdAndTokenIndex(long docId,
			IBioTMLSentence sentence, int annotationTokenIndex) throws BioTMLException {
		Set<IBioTMLEntity> sentenceAnnotations = getAnnotationsFromSentenceInDocumentId(docId, sentence);
		if(!sentenceAnnotations.isEmpty()){
			int index = 0;
			for(IBioTMLToken token :sentence.getTokens()){
				if(isTokenInAnnotations(sentenceAnnotations, token)){
					if(index == annotationTokenIndex){
						return getAnnotationsFromDocAndOffsets(docId, token.getStartOffset(), token.getEndOffset());
					}
					index++;
				}
			}
		}
		throw new BioTMLException("Annotation Token Index not found!");
	}
	
	@Override
	public boolean isTokenInAnnotations(Set<IBioTMLEntity> annotations, IBioTMLToken token){
		for(IBioTMLEntity annotation : annotations){
			if(annotation.getAnnotationOffsets().offsetsOverlap(token.getTokenOffsetsPair())){
				return true;
			}
		}
		return false;
	}

	public String toString() {
		return name;
	}

	@Override
	public Set<IBioTMLEvent> getDocEventsWithBestScore(long docID) {
		Set<IBioTMLEvent> result = new HashSet<>();
		@SuppressWarnings("rawtypes")
		Map<IBioTMLAssociation, IBioTMLEvent> map = new HashMap<>();
		Set<IBioTMLEvent> resultevents = getDocAnnotationEvents(docID);
		for(IBioTMLEvent event : resultevents){
			if(!map.containsKey(event.getAssociation())){
				map.put(event.getAssociation(), event);
			}
			IBioTMLEvent storedEvent = map.get(event.getAssociation());
			if(event.getAnnotationScore() > storedEvent.getAnnotationScore())
				map.put(event.getAssociation(), event);
		}
		result.addAll(map.values());
		return result;
	}
	
	private List<IBioTMLEntity> addMissingAnnotationsFromEvents(List<IBioTMLEntity> annotations, List<IBioTMLEvent> events){
		for(IBioTMLEvent event : events){
			Set<IBioTMLEntity> annotationsInEvent = event.getAllAnnotationsFromEvent();
			for(IBioTMLEntity annotationInEvent : annotationsInEvent)
				if(!annotations.contains(annotationInEvent))
					annotations.add(annotationInEvent);
		}
		return annotations;
	}

	@Override
	public List<IBioTMLEntity> getAnnotationsByAnnotationTypes(Set<String> annotationTypes) {
		List<IBioTMLEntity> annotationsResult = new ArrayList<>();
		
		for(IBioTMLEntity annotation : getAnnotations())
			if(annotationTypes.contains(annotation.getAnnotationType()))
				annotationsResult.add(annotation);

		return annotationsResult;
	}

	@Override
	public List<IBioTMLEvent> getEventsByEventTypes(Set<String> eventTypes) {
		List<IBioTMLEvent> eventsResult = new ArrayList<>();
		
		for(IBioTMLEvent event : getEvents())
			if(eventTypes.contains(event.getAnnotationType()))
				eventsResult.add(event);
		
		return eventsResult;
	}


}
