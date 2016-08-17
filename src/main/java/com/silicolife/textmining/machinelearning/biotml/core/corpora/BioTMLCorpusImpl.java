package com.silicolife.textmining.machinelearning.biotml.core.corpora;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import com.silicolife.textmining.machinelearning.biotml.core.BioTMLConstants;
import com.silicolife.textmining.machinelearning.biotml.core.exception.BioTMLException;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLAnnotation;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLAnnotationsRelation;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLCorpus;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLDocument;
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
	private List<IBioTMLAnnotationsRelation> relations;
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

	public BioTMLCorpusImpl(List<IBioTMLDocument> documents, List<IBioTMLAnnotation> annotations, List<IBioTMLAnnotationsRelation> relations, String name){
		this.documents = documents;
		this.annotations = annotations;
		this.relations = relations;
		this.name = name;
	}

	public List<IBioTMLDocument> getDocuments() {
		return documents;
	}

	public List<IBioTMLAnnotation> getAnnotations(){
		return annotations;
	}

	public List<IBioTMLAnnotationsRelation> getRelations(){
		return relations;
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

	public Set<IBioTMLAnnotationsRelation> getDocAnnotationRelations(long docID){
		Set<IBioTMLAnnotationsRelation> docAnnotationRelations = new HashSet<IBioTMLAnnotationsRelation>();
		for( IBioTMLAnnotationsRelation relation : getRelations()){
			if(relation.getDocID() == docID){
				docAnnotationRelations.add(relation);
			}
		}
		return docAnnotationRelations;
	}

	public Set<IBioTMLAnnotationsRelation> getDocAnnotationRelationsWithBestScore(long docID){
		List<IBioTMLAnnotationsRelation> docAnnotationRelations = new ArrayList<IBioTMLAnnotationsRelation>(getDocAnnotationRelations(docID));
		Set<IBioTMLAnnotationsRelation> docAnnotationRelWithBestScores = new HashSet<IBioTMLAnnotationsRelation>();
		while(!docAnnotationRelations.isEmpty()){
			IBioTMLAnnotationsRelation relation = docAnnotationRelations.get(0);
			docAnnotationRelations.remove(0);
			SortedSet<Integer> intToRemove = new TreeSet<>();
			relation = searchBestRelationWithEntitiesinSameOffsets(relation, docAnnotationRelations, intToRemove);
			docAnnotationRelWithBestScores.add(relation);
			Iterator<Integer> itRemove = intToRemove.iterator();
			int diff = 0;
			while(itRemove.hasNext()){
				int index = itRemove.next();
				docAnnotationRelations.remove(index - diff);
				diff++;
			}
		}
		return docAnnotationRelWithBestScores;
	}

	private IBioTMLAnnotationsRelation searchBestRelationWithEntitiesinSameOffsets(IBioTMLAnnotationsRelation relation, List<IBioTMLAnnotationsRelation> docAnnotationRelations, Set<Integer> intToRemove)
	{
		for(int i=0; i<docAnnotationRelations.size(); i++){
			IBioTMLAnnotationsRelation otherRelation = docAnnotationRelations.get(i);
			IBioTMLAnnotation relationClue = null;
			IBioTMLAnnotation otherRelationClue = null;
			try {
				relationClue = relation.getFirstAnnotationByType(BioTMLConstants.clue.toString());
				otherRelationClue = otherRelation.getFirstAnnotationByType(BioTMLConstants.clue.toString());
			} catch (BioTMLException e) {}

			if(relationClue != null && otherRelationClue !=null){
				if(relationClue.equals(otherRelationClue)){
					Set<IBioTMLAnnotation> leftEnt = new HashSet<>();
					try {
						leftEnt = relation.getAnnotsAtLeftOfAnnotation(relationClue);
					} catch (BioTMLException e) {}
					Set<IBioTMLAnnotation> otherLeftEnt = new HashSet<>();
					try {
						otherLeftEnt = otherRelation.getAnnotsAtLeftOfAnnotation(relationClue);
					} catch (BioTMLException e) {}

					boolean leftIsEqual = false;
					boolean rightIsEqual = false;
					if(leftEnt.isEmpty() && otherLeftEnt.isEmpty()){
						leftIsEqual = true;
					}else if( leftEnt.size() == otherLeftEnt.size()){
						IBioTMLAnnotation firstLeftEnt = leftEnt.iterator().next();
						IBioTMLAnnotation firstOtherLeftEnt = otherLeftEnt.iterator().next();
						leftIsEqual = firstLeftEnt.getAnnotationOffsets().equals(firstOtherLeftEnt.getAnnotationOffsets());
					}
					Set<IBioTMLAnnotation> rightEnt = new HashSet<>();
					try {
						rightEnt = relation.getAnnotsAtRightOfAnnotation(relationClue);
					} catch (BioTMLException e) {}
					Set<IBioTMLAnnotation> otherRightEnt = new HashSet<>();
					try {
						otherRightEnt = otherRelation.getAnnotsAtRightOfAnnotation(relationClue);
					} catch (BioTMLException e) {}

					if(rightEnt.isEmpty() && otherRightEnt.isEmpty()){
						rightIsEqual = true;
					}else if(rightEnt.size() == otherRightEnt.size()){ 
						IBioTMLAnnotation firstRightEnt = rightEnt.iterator().next();
						IBioTMLAnnotation firstOtherRightEnt = otherRightEnt.iterator().next();
						rightIsEqual = firstRightEnt.getAnnotationOffsets().equals(firstOtherRightEnt.getAnnotationOffsets());
					}
					if(leftIsEqual && rightIsEqual){
						if(relation.getScore()<otherRelation.getScore()){
							relation = otherRelation;
						}
						intToRemove.add(i);
					}
				}
			}
		}
		return relation;
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

	public List<IBioTMLAnnotation> getAnnotationsFromRelations(List<IBioTMLAnnotationsRelation> relations){
		List<IBioTMLAnnotation> annotsres = new ArrayList<IBioTMLAnnotation>();
		Set<IBioTMLAnnotation> annots = new HashSet<IBioTMLAnnotation>();
		for(IBioTMLAnnotationsRelation relation: relations){
			annots.addAll(relation.getRelation());
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


}
