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
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLAnnotation;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLAssociation;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLCorpus;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLDocument;
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
	private List<IBioTMLAnnotation> annotations;
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
	 * @param annotations - List of {@link IBioTMLAnnotation} with offsets and class types.
	 * @param name - Corpus name.
	 */

	public BioTMLCorpusImpl(List<IBioTMLDocument> documents, List<IBioTMLAnnotation> annotations, String name){
		this(documents, annotations, new ArrayList<>(), name);
	}

	/**
	 * 
	 * Initializes a corpus with annotations and annotations relation
	 * 
	 * @param documents - List of {@link IBioTMLDocument} tokenized.
	 * @param annotations - List of {@link IBioTMLAnnotation} with offsets and class types.
	 * @param name - Corpus name.
	 */

	public BioTMLCorpusImpl(List<IBioTMLDocument> documents, List<IBioTMLAnnotation> annotations, List<IBioTMLEvent> events, String name){
		this.documents = documents;
		this.annotations = annotations;
		this.events = events;
		this.name = name;
	}

	public List<IBioTMLDocument> getDocuments() {
		return documents;
	}

	public List<IBioTMLAnnotation> getAnnotations(){
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
			List<IBioTMLAnnotation> annots = getAllDocAnnotations(doc.getID());
			List<IBioTMLToken> tokens = new ArrayList<IBioTMLToken>();
			for(IBioTMLAnnotation annot: annots){
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
	
	public List<IBioTMLAnnotation> getDocAnnotations(long docID){
		return retrieveAnnotationsWithBestScore(docID);
	}

	public List<IBioTMLAnnotation> getAllDocAnnotations(long docID){
		List<IBioTMLAnnotation> docAnnotations = new ArrayList<IBioTMLAnnotation>();
		for( IBioTMLAnnotation annotation : getAnnotations()){
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

	private List<IBioTMLAnnotation> retrieveAnnotationsWithBestScore(long docID){
		List<IBioTMLAnnotation> annotations = getAllDocAnnotations(docID);
		List<IBioTMLAnnotation> finalAnnotations = new ArrayList<IBioTMLAnnotation>();
		if(!annotations.isEmpty()){
			Collections.sort(annotations);
			Iterator<IBioTMLAnnotation> itAnnot = annotations.iterator();
			IBioTMLAnnotation prevAnnot = null;
			while(itAnnot.hasNext()){
				if(prevAnnot == null){
					prevAnnot = itAnnot.next();
					finalAnnotations.add(prevAnnot);
				}else{
					IBioTMLAnnotation currentAnnot = itAnnot.next();
					if((prevAnnot.getStartOffset() == currentAnnot.getStartOffset()) 
							&& (prevAnnot.getEndOffset()== currentAnnot.getEndOffset())){
						if(!(prevAnnot.getScore()>=currentAnnot.getScore())){
							finalAnnotations.set(finalAnnotations.size()-1, currentAnnot);
						}
					}
					finalAnnotations.add(currentAnnot);
				}
			}
		}
		return finalAnnotations;
	}

	public List<IBioTMLAnnotation> getAnnotationsFromEvents(List<IBioTMLEvent> relations){
		List<IBioTMLAnnotation> annotsres = new ArrayList<IBioTMLAnnotation>();
		Set<IBioTMLAnnotation> annots = new HashSet<IBioTMLAnnotation>();
		for(IBioTMLEvent relation: relations){
			if(relation.getTrigger() != null)
				annots.add(relation.getTrigger());
			if(relation.getEntity()!=null)
				annots.add(relation.getEntity());
		}
		annotsres.addAll(annots);
		return annotsres;
	}

	public IBioTMLAnnotation getAnnotationFromDocAndOffsets(long docID, long startOffset, long endOffset) throws BioTMLException{
		List<IBioTMLAnnotation> annots = getAllDocAnnotations(docID);
		for(IBioTMLAnnotation annot : annots){
			if(annot.getAnnotationOffsets().offsetsOverlap(startOffset, endOffset)){
				return annot;
			}
		}
		throw new BioTMLException(29);
	}
	
	@Override
	public Set<IBioTMLAnnotation> getAnnotationsFromDocAndOffsets(long docID, long startOffset, long endOffset){
		Set<IBioTMLAnnotation> resultAnnotations = new HashSet<>();
		List<IBioTMLAnnotation> annots = getAllDocAnnotations(docID);
		for(IBioTMLAnnotation annot : annots){
			if(annot.getAnnotationOffsets().offsetsOverlap(startOffset, endOffset)){
				resultAnnotations.add(annot);
			}
		}
		return resultAnnotations;
	}
	
	@Override
	public Set<IBioTMLAnnotation> getAnnotationsFromSentenceInDocumentId(long docID, IBioTMLSentence sentence){
		Set<IBioTMLAnnotation> annotations = new HashSet<>();
		IBioTMLDocument document;
		try {
			document = getDocumentByID(docID);
		} catch (BioTMLException e) {
			return annotations;
		}
		List<IBioTMLAnnotation> documentAnnotations = getDocAnnotations(docID);
		if(!documentAnnotations.isEmpty() && document.getSentences().contains(sentence)){
			Iterator<IBioTMLAnnotation> itAnnot = documentAnnotations.iterator();
			while(itAnnot.hasNext()){
				IBioTMLAnnotation annotation = itAnnot.next();
				if(sentence.getSentenceOffsetsPair().containsInside(annotation.getAnnotationOffsets())){
					annotations.add(annotation);
				}
			}
		}
		return annotations;
	}
	
	@Override
	public Set<IBioTMLAnnotation> getAnnotationsFromSentenceInDocumentIdAndTokenIndex(long docId,
			IBioTMLSentence sentence, int annotationTokenIndex) throws BioTMLException {
		Set<IBioTMLAnnotation> sentenceAnnotations = getAnnotationsFromSentenceInDocumentId(docId, sentence);
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
	public boolean isTokenInAnnotations(Set<IBioTMLAnnotation> annotations, IBioTMLToken token){
		for(IBioTMLAnnotation annotation : annotations){
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
			if(event.getScore() > storedEvent.getScore())
				map.put(event.getAssociation(), event);
		}
		result.addAll(map.values());
		return result;
	}


}
