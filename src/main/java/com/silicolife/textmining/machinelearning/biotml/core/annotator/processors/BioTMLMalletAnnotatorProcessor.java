package com.silicolife.textmining.machinelearning.biotml.core.annotator.processors;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.silicolife.textmining.machinelearning.biotml.core.BioTMLConstants;
import com.silicolife.textmining.machinelearning.biotml.core.corpora.BioTMLAssociationImpl;
import com.silicolife.textmining.machinelearning.biotml.core.corpora.BioTMLEntityImpl;
import com.silicolife.textmining.machinelearning.biotml.core.corpora.BioTMLEventImpl;
import com.silicolife.textmining.machinelearning.biotml.core.exception.BioTMLException;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLAssociation;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLCorpus;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLDocument;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLEntity;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLEvent;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLSentence;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLToken;

import cc.mallet.types.InstanceList;

/**
 * 
 * Represents the mallet annotator processor, it contains generic methods used in annotators.
 * All extended classes need to implement the generation of a prediction Matrix.
 *  
 * @since 1.1.0
 * @version 1.1.0
 * @author Ruben Rodrigues ({@code rrodrigues@silicolife.com})
 */

public abstract class BioTMLMalletAnnotatorProcessor {

	public BioTMLMalletAnnotatorProcessor(){

	}

	public abstract InstanceList generatePredictionMatrix() throws BioTMLException;

	/**
	 * 
	 * This method adds a new annotation.
	 * 
	 * @param annotations
	 * @param doc
	 * @param sentIndex
	 * @param tokenIndex
	 * @param tokenClass
	 * @param prediction
	 * @param predictionScore
	 * @return
	 */
	public Set<IBioTMLEntity> addPredictedAnnotation(Set<IBioTMLEntity> annotations, IBioTMLDocument doc, 
			int sentIndex, int tokenIndex, String tokenClass, String prediction,double predictionScore){

		IBioTMLToken token = doc.getSentence(sentIndex).getToken(tokenIndex);
		if(prediction.equals(BioTMLConstants.b.toString())){
			IBioTMLEntity annot = new BioTMLEntityImpl(doc.getID(), tokenClass, token.getStartOffset(), token.getEndOffset(), predictionScore);
			annotations.add(annot);
		}else if(prediction.equals(BioTMLConstants.i.toString())){
			joinTokenToLastAnnotation(annotations, doc, tokenClass, predictionScore, token);
		}
		return annotations;
	}

	/**
	 * 
	 *  This method searches for a annotation that is previous the given token. If it's found, the annotation is modified to include the given token.
	 * 
	 * @param annotations
	 * @param doc
	 * @param tokenClass
	 * @param predictionScore
	 * @param token
	 */
	private void joinTokenToLastAnnotation(Set<IBioTMLEntity> annotations, IBioTMLDocument doc, String tokenClass, double predictionScore, IBioTMLToken token) {
		if(annotations.size()>0){
			IBioTMLEntity annotiationBegin = null;
			boolean foundPreviousAnnotation = false;
			Iterator<IBioTMLEntity> itAnnot = annotations.iterator();
			while(itAnnot.hasNext() && !foundPreviousAnnotation){
				annotiationBegin = itAnnot.next();
				if(validatePrevToken(annotiationBegin, token, doc.getID())){
					foundPreviousAnnotation = true;
				}
			}
			if(foundPreviousAnnotation && annotiationBegin != null && annotations.remove(annotiationBegin)){
				IBioTMLEntity newannot = new BioTMLEntityImpl(doc.getID(), tokenClass, annotiationBegin.getStartOffset(), token.getEndOffset(), predictionScore);
				annotations.add(newannot);
			}else{
				IBioTMLEntity annot = new BioTMLEntityImpl(doc.getID(), tokenClass, token.getStartOffset(), token.getEndOffset(), predictionScore);
				annotations.add(annot);
			}
		}
		else{
			IBioTMLEntity annot = new BioTMLEntityImpl(doc.getID(), tokenClass, token.getStartOffset(), token.getEndOffset(), predictionScore);
			annotations.add(annot);
		}
	}

	/**
	 * 
	 * Method that validates if the previous annotation contains offsets of tokens that are next to the inputed token in the same document.
	 * 
	 * @param prevAnnotiation - Annotation ({@link IBioTMLEntity}) to be validated as a previous annotation.
	 * @param token - Token that is next to annotation and could be incorporated in the annotation.
	 * @param docID - Document ID.
	 * @return Boolean that validates if is or not a previous annotation.
	 */
	private boolean validatePrevToken(IBioTMLEntity prevAnnotiation, IBioTMLToken token, long docID){
		if(prevAnnotiation.getDocID()==docID
				&& prevAnnotiation.getStartOffset()<token.getStartOffset()
				&& prevAnnotiation.getEndOffset()<token.getEndOffset()
				&& (token.getStartOffset() - prevAnnotiation.getEndOffset()) <3){
			return true;
		}
		return false;
	}

	/**
	 * 
	 * Method to retrieve the annotation in one sentence.
	 * 
	 * @param doc - Document {@link IBioTMLDocument}.
	 * @param sentIndex - Sentence index.
	 * @param annotationIndex - Pair of integers that indicates the index of the first and last token offsets.
	 * @return Annotation {@link IBioTMLEntity}.
	 */
	public IBioTMLEntity getAnnotation(IBioTMLCorpus corpus, IBioTMLDocument doc, int sentIndex, int startTokenIndex, int endTokenIndex){
		IBioTMLToken firstTokenAnnotation = doc.getSentence(sentIndex).getToken(startTokenIndex);
		IBioTMLToken lastTokenAnnotation = doc.getSentence(sentIndex).getToken(endTokenIndex);
		IBioTMLEntity annotation = null;
		try {
			annotation = corpus.getAnnotationFromDocAndOffsets(doc.getID(),firstTokenAnnotation.getStartOffset(), lastTokenAnnotation.getEndOffset());
		} catch (BioTMLException e) {}
		return annotation;
	}

	/**
	 * 
	 * Method to add a predicted relation.
	 * 
	 * @param events - Set of all relations ({@link IBioTMLEvent}) predicted to add the relation.
	 * @param doc - Document in which the relation is present.
	 * @param sentIndex - Sentence index.
	 * @param tokenOrAnnotationIndex - Token index.
	 * @param trigger - Annotation of the relation to be associated.
	 * @param tokenClass - Token class type.
	 * @param prediction - Prediction value (e.g. B, I or O).
	 * @param predictionScore - Prediction score.
	 * @return Set of all relations ({@link IBioTMLEvent}) predicted.
	 * @throws {@link BioTMLException}.
	 */
	public Set<IBioTMLEvent> addPredictedEvent(IBioTMLCorpus corpus,
			Set<IBioTMLEvent> events, 
			IBioTMLDocument doc,
			int sentIndex,
			int tokenOrAnnotationIndex,
			IBioTMLEntity trigger,
			boolean onlyAnnotations,
			String tokenClass,
			String reMethodology,
			String prediction,
			double predictionScore) throws BioTMLException{
		
		IBioTMLSentence sentence = doc.getSentence(sentIndex);
		
		if(!onlyAnnotations){
			IBioTMLToken token = sentence.getToken(tokenOrAnnotationIndex);
			IBioTMLEntity annotationToAssociate = null;
			try {
				annotationToAssociate = corpus.getAnnotationFromDocAndOffsets(doc.getID(), token.getStartOffset(), token.getEndOffset());
			} catch (BioTMLException e) {
				return events;
//				if(!reMethodology.equals(BioTMLREModelTypes.entitycluegenerateentity.toString())
//						&& !reMethodology.equals(BioTMLREModelTypes.entityentiygenerateentity.toString())
//						&& !reMethodology.equals(BioTMLREModelTypes.entityclueonlyannotationsgenerateentity.toString())
//						&& !reMethodology.equals(BioTMLREModelTypes.entityentiyonlyannotationsgenerateentity.toString())){
//					return events;
//				}else if(annotationToAssociate == null){
//					annotationToAssociate = new BioTMLAnnotationImpl(doc.getID(), tokenClass, token.getStartOffset(), token.getEndOffset(), predictionScore);
//				}
//				if(prediction.equals(BioTMLConstants.i.toString())){
//					events = joinTokenToLastAnnotationAndCorrectEvents(events, doc, trigger, tokenClass, predictionScore, token, annotationToAssociate);
//				}
			}
			if(prediction.equals(BioTMLConstants.b.toString())){
				events = addEvent(events, annotationToAssociate, trigger, tokenClass, predictionScore);
			}
		}else{
			Set<IBioTMLEntity> annotationsToAssociate = corpus.getAnnotationsFromSentenceInDocumentIdAndTokenIndex(doc.getID(), sentence, tokenOrAnnotationIndex);
			if(prediction.equals(BioTMLConstants.b.toString())){
				for(IBioTMLEntity annotationToAssociate : annotationsToAssociate){
					events = addEvent(events, annotationToAssociate, trigger, tokenClass, predictionScore);
				}
			}
		}

		return events;
	}

	/**
	 * 
	 * This method searches for a annotation that is previous the given token. If it's found, the annotation is modified to include the given token.
	 * All relations that contained the found annotation are updated with the new annotation. 
	 * If the anontation is not found, a new annotation and a new relation are created.
	 * 
	 * @param events
	 * @param doc
	 * @param firstAnnotation
	 * @param tokenClass
	 * @param predictionScore
	 * @param token
	 * @param annotation
	 * @return
	 * @throws BioTMLException
	 */
	private Set<IBioTMLEvent> joinTokenToLastAnnotationAndCorrectEvents(Set<IBioTMLEvent> events,
			IBioTMLDocument doc, IBioTMLEntity firstAnnotation, String tokenClass, double predictionScore,
			IBioTMLToken token, IBioTMLEntity annotation) throws BioTMLException {
		Map<IBioTMLEntity, Set<IBioTMLEvent>> annotationsToRelations = generateMapOfAnnotationsFromEvents(events);
		IBioTMLEntity prevAnnotation = null;
		Set<IBioTMLEntity> annots = annotationsToRelations.keySet();
		Iterator<IBioTMLEntity> itAnn = annots.iterator();
		boolean foundPrevTokenInRelation = false;
		while(itAnn.hasNext() && !foundPrevTokenInRelation){
			prevAnnotation = itAnn.next();
			if(validatePrevToken(annotation, token, doc.getID())){
				foundPrevTokenInRelation = true;
			}
		}
		if(foundPrevTokenInRelation && prevAnnotation != null){
			correctRelationsWithPreviousAnnotation(events, doc, tokenClass, predictionScore, token, annotationsToRelations, prevAnnotation);
		}else{
			annotation = new BioTMLEntityImpl(doc.getID(), tokenClass, token.getStartOffset(), token.getEndOffset(), predictionScore);
			events = addEvent(events, annotation, firstAnnotation, tokenClass, predictionScore);
		}
		return events;
	}

	/**
	 * 
	 * This method generates a map of all annotations present in relations.
	 * 
	 * @param relations
	 * @return
	 */
	private Map<IBioTMLEntity, Set<IBioTMLEvent>> generateMapOfAnnotationsFromEvents(Set<IBioTMLEvent> relations) {
		Map<IBioTMLEntity, Set<IBioTMLEvent>> annotationsToRelations = new HashMap<>();
		for(IBioTMLEvent relation : relations){
			if(relation.getTrigger() != null){
				if(!annotationsToRelations.containsKey(relation.getTrigger())){
					annotationsToRelations.put(relation.getTrigger(), new HashSet<>());
				}
				Set<IBioTMLEvent> relationsInMap = annotationsToRelations.get(relation.getTrigger());
				relationsInMap.add(relation);
				annotationsToRelations.put(relation.getTrigger(), relationsInMap);
			}
			if(relation.getEntity() != null){
				if(!annotationsToRelations.containsKey(relation.getEntity())){
					annotationsToRelations.put(relation.getEntity(), new HashSet<>());
				}
				Set<IBioTMLEvent> relationsInMap = annotationsToRelations.get(relation.getEntity());
				relationsInMap.add(relation);
				annotationsToRelations.put(relation.getEntity(), relationsInMap);
			}
		}
		return annotationsToRelations;
	}

	/**
	 * 
	 * This method removes relations that contains the prevAnnotation annotation and generates new ones with an annotation that contains the tokens of
	 * prevAnnotation and the given token to this method.
	 * 
	 * @param events - Set of all events ({@link IBioTMLEvent}) predicted to add the event.
	 * @param doc
	 * @param tokenClass
	 * @param predictionScore
	 * @param token
	 * @param annotationsToRelations
	 * @param prevAnnotation
	 * @throws BioTMLException
	 */
	private void correctRelationsWithPreviousAnnotation(Set<IBioTMLEvent> events, IBioTMLDocument doc,
			String tokenClass, double predictionScore, IBioTMLToken token,
			Map<IBioTMLEntity, Set<IBioTMLEvent>> annotationsToRelations,
			IBioTMLEntity prevAnnotation) throws BioTMLException {

		IBioTMLEntity annotation;
		Set<IBioTMLEvent> eventsToFix = annotationsToRelations.get(prevAnnotation);
		annotation = new BioTMLEntityImpl(doc.getID(), tokenClass, prevAnnotation.getStartOffset(), token.getEndOffset(), predictionScore);
		events.removeAll(eventsToFix);

		for(IBioTMLEvent event : eventsToFix){
			IBioTMLEntity trigger = event.getTrigger();
			IBioTMLEntity entity = event.getEntity();
			if(event.getTrigger().equals(prevAnnotation))
				trigger = annotation;
			else
				entity = annotation;
			
			events.add(new BioTMLEventImpl(new BioTMLAssociationImpl<>(trigger, entity), event.getAnnotationType(), event.getAnnotationScore()));
		}
	}


	/**
	 * 
	 * Method to add a relation.
	 * 
	 * @param events - Set of all events ({@link IBioTMLEvent}) predicted to add the event.
	 * @param entity - Annotation ({@link IBioTMLEntity}) that belongs to the event.
	 * @param trigger - Annotation ({@link IBioTMLEntity}) that belongs to the event.
	 * @param eventClass 
	 * @param score - Score value associated to the prediction.
	 * @return Set of all relations ({@link IBioTMLEvent}) predicted.
	 * @throws BioTMLException
	 */
	@SuppressWarnings("rawtypes")
	private Set<IBioTMLEvent> addEvent(Set<IBioTMLEvent> events, IBioTMLEntity entity, IBioTMLEntity trigger, String eventClass, double score) throws BioTMLException{
		IBioTMLAssociation association = new BioTMLAssociationImpl<>(trigger, entity);
		return addEvent(events, association, eventClass, score);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Set<IBioTMLEvent> addEvent(Set<IBioTMLEvent> events, IBioTMLAssociation association, String eventClass, double score){
		events.add(new BioTMLEventImpl(association, eventClass, score));
		return events;
	}
}
