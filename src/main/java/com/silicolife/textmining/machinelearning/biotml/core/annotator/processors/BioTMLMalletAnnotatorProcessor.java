package com.silicolife.textmining.machinelearning.biotml.core.annotator.processors;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import com.silicolife.textmining.machinelearning.biotml.core.BioTMLConstants;
import com.silicolife.textmining.machinelearning.biotml.core.corpora.BioTMLAnnotationImpl;
import com.silicolife.textmining.machinelearning.biotml.core.corpora.BioTMLAnnotationsRelationImpl;
import com.silicolife.textmining.machinelearning.biotml.core.exception.BioTMLException;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLAnnotation;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLAnnotationsRelation;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLCorpus;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLDocument;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLToken;
import com.silicolife.textmining.machinelearning.biotml.core.models.BioTMLREModelTypes;

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
	public Set<IBioTMLAnnotation> addPredictedAnnotation(Set<IBioTMLAnnotation> annotations, IBioTMLDocument doc, 
			int sentIndex, int tokenIndex, String tokenClass, String prediction,double predictionScore){

		IBioTMLToken token = doc.getSentence(sentIndex).getToken(tokenIndex);
		if(prediction.equals(BioTMLConstants.b.toString())){
			IBioTMLAnnotation annot = new BioTMLAnnotationImpl(doc.getID(), tokenClass, token.getStartOffset(), token.getEndOffset(), predictionScore);
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
	private void joinTokenToLastAnnotation(Set<IBioTMLAnnotation> annotations, IBioTMLDocument doc, String tokenClass, double predictionScore, IBioTMLToken token) {
		if(annotations.size()>0){
			IBioTMLAnnotation annotiationBegin = null;
			boolean foundPreviousAnnotation = false;
			Iterator<IBioTMLAnnotation> itAnnot = annotations.iterator();
			while(itAnnot.hasNext() && !foundPreviousAnnotation){
				annotiationBegin = itAnnot.next();
				if(validatePrevToken(annotiationBegin, token, doc.getID())){
					foundPreviousAnnotation = true;
				}
			}
			if(foundPreviousAnnotation && annotiationBegin != null && annotations.remove(annotiationBegin)){
				IBioTMLAnnotation newannot = new BioTMLAnnotationImpl(doc.getID(), tokenClass, annotiationBegin.getStartOffset(), token.getEndOffset(), predictionScore);
				annotations.add(newannot);
			}else{
				IBioTMLAnnotation annot = new BioTMLAnnotationImpl(doc.getID(), tokenClass, token.getStartOffset(), token.getEndOffset(), predictionScore);
				annotations.add(annot);
			}
		}
		else{
			IBioTMLAnnotation annot = new BioTMLAnnotationImpl(doc.getID(), tokenClass, token.getStartOffset(), token.getEndOffset(), predictionScore);
			annotations.add(annot);
		}
	}
	
	/**
	 * 
	 * Method that validates if the previous annotation contains offsets of tokens that are next to the inputed token in the same document.
	 * 
	 * @param prevAnnotiation - Annotation ({@link IBioTMLAnnotation}) to be validated as a previous annotation.
	 * @param token - Token that is next to annotation and could be incorporated in the annotation.
	 * @param docID - Document ID.
	 * @return Boolean that validates if is or not a previous annotation.
	 */
	private boolean validatePrevToken(IBioTMLAnnotation prevAnnotiation, IBioTMLToken token, long docID){
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
	 * @return Annotation {@link IBioTMLAnnotation}.
	 */
	public IBioTMLAnnotation getAnnotation(IBioTMLCorpus corpus, IBioTMLDocument doc, int sentIndex, int startTokenIndex, int endTokenIndex){
		IBioTMLToken firstTokenAnnotation = doc.getSentence(sentIndex).getToken(startTokenIndex);
		IBioTMLToken lastTokenAnnotation = doc.getSentence(sentIndex).getToken(endTokenIndex);
		IBioTMLAnnotation annotation = null;
		try {
			annotation = corpus.getAnnotationFromDocAndOffsets(doc.getID(),firstTokenAnnotation.getStartOffset(), lastTokenAnnotation.getEndOffset());
		} catch (BioTMLException e) {}
		return annotation;
	}
	
	/**
	 * 
	 * Method to add a predicted relation.
	 * 
	 * @param relations - Set of all relations ({@link IBioTMLAnnotationsRelation}) predicted to add the relation.
	 * @param doc - Document in which the relation is present.
	 * @param sentIndex - Sentence index.
	 * @param tokenIndex - Token index.
	 * @param firstAnnotation - Annotation of the relation to be associated.
	 * @param tokenClass - Token class type.
	 * @param prediction - Prediction value (e.g. B, I or O).
	 * @param predictionScore - Prediction score.
	 * @return Set of all relations ({@link IBioTMLAnnotationsRelation}) predicted.
	 * @throws {@link BioTMLException}.
	 */
	public Set<IBioTMLAnnotationsRelation> addPredictedRelation(IBioTMLCorpus corpus,
			Set<IBioTMLAnnotationsRelation> relations, 
			IBioTMLDocument doc,
			int sentIndex,
			int tokenIndex,
			IBioTMLAnnotation firstAnnotation,
			String tokenClass,
			String prediction,
			double predictionScore) throws BioTMLException{

		IBioTMLToken token = doc.getSentence(sentIndex).getToken(tokenIndex);
		IBioTMLAnnotation annotation = null;
		try {
			annotation = corpus.getAnnotationFromDocAndOffsets(doc.getID(), token.getStartOffset(), token.getEndOffset());
		} catch (BioTMLException e) {
			if(!(tokenClass.equals(BioTMLREModelTypes.entitycluegenerateentity.toString()) 
					|| tokenClass.equals(BioTMLREModelTypes.entityentiygenerateentity.toString())
					|| tokenClass.equals(BioTMLREModelTypes.entityclueonlyannotationsgenerateentity.toString())
					|| tokenClass.equals(BioTMLREModelTypes.entityentiyonlyannotationsgenerateentity.toString()))){
				return relations;
			}
		}

		if(prediction.equals(BioTMLConstants.b.toString())){
			if(annotation == null){
				annotation = new BioTMLAnnotationImpl(doc.getID(), tokenClass, token.getStartOffset(), token.getEndOffset(), predictionScore);
			}
			relations = addRelation(relations, annotation, firstAnnotation, predictionScore);
		}else if(prediction.equals(BioTMLConstants.i.toString())){
			if(annotation == null){
				relations = joinTokenToLastAnnotationAndCorrectRelations(relations, doc, firstAnnotation, tokenClass, predictionScore, token, annotation);
			}
		}
		return relations;
	}

	/**
	 * 
	 * This method searches for a annotation that is previous the given token. If it's found, the annotation is modified to include the given token.
	 * All relations that contained the found annotation are updated with the new annotation. 
	 * If the anontation is not found, a new annotation and a new relation are created.
	 * 
	 * @param relations
	 * @param doc
	 * @param firstAnnotation
	 * @param tokenClass
	 * @param predictionScore
	 * @param token
	 * @param annotation
	 * @return
	 * @throws BioTMLException
	 */
	private Set<IBioTMLAnnotationsRelation> joinTokenToLastAnnotationAndCorrectRelations(Set<IBioTMLAnnotationsRelation> relations,
			IBioTMLDocument doc, IBioTMLAnnotation firstAnnotation, String tokenClass, double predictionScore,
			IBioTMLToken token, IBioTMLAnnotation annotation) throws BioTMLException {
		Map<IBioTMLAnnotation, Set<IBioTMLAnnotationsRelation>> annotationsToRelations = generateMapOfAnnotationsFromRelations(relations);
		IBioTMLAnnotation prevAnnotation = null;
		Set<IBioTMLAnnotation> annots = annotationsToRelations.keySet();
		Iterator<IBioTMLAnnotation> itAnn = annots.iterator();
		boolean foundPrevTokenInRelation = false;
		while(itAnn.hasNext() && !foundPrevTokenInRelation){
			prevAnnotation = itAnn.next();
			if(validatePrevToken(annotation, token, doc.getID())){
				foundPrevTokenInRelation = true;
			}
		}
		if(foundPrevTokenInRelation && prevAnnotation != null){
			correctRelationsWithPreviousAnnotation(relations, doc, tokenClass, predictionScore, token, annotationsToRelations, prevAnnotation);
		}else{
			annotation = new BioTMLAnnotationImpl(doc.getID(), tokenClass, token.getStartOffset(), token.getEndOffset(), predictionScore);
			relations = addRelation(relations, annotation, firstAnnotation, predictionScore);
		}
		return relations;
	}

	/**
	 * 
	 * This method generates a map of all annotations present in relations.
	 * 
	 * @param relations
	 * @return
	 */
	private Map<IBioTMLAnnotation, Set<IBioTMLAnnotationsRelation>> generateMapOfAnnotationsFromRelations(
			Set<IBioTMLAnnotationsRelation> relations) {
		Map<IBioTMLAnnotation, Set<IBioTMLAnnotationsRelation>> annotationsToRelations = new HashMap<>();
		for(IBioTMLAnnotationsRelation relation : relations){
			for(IBioTMLAnnotation annotationInRelation : relation.getRelation()){
				if(!annotationsToRelations.containsKey(annotationInRelation)){
					annotationsToRelations.put(annotationInRelation, new HashSet<>());
				}
				Set<IBioTMLAnnotationsRelation> relationsInMap = annotationsToRelations.get(annotationInRelation);
				relationsInMap.add(relation);
				annotationsToRelations.put(annotationInRelation, relationsInMap);
			}
		}
		return annotationsToRelations;
	}

	/**
	 * 
	 * This method removes relations that contains the prevAnnotation annotation and generates new ones with an annotation that contains the tokens of
	 * prevAnnotation and the given token to this method.
	 * 
	 * @param relations - Set of all relations ({@link IBioTMLAnnotationsRelation}) predicted to add the relation.
	 * @param doc
	 * @param tokenClass
	 * @param predictionScore
	 * @param token
	 * @param annotationsToRelations
	 * @param prevAnnotation
	 * @throws BioTMLException
	 */
	private void correctRelationsWithPreviousAnnotation(Set<IBioTMLAnnotationsRelation> relations, IBioTMLDocument doc,
			String tokenClass, double predictionScore, IBioTMLToken token,
			Map<IBioTMLAnnotation, Set<IBioTMLAnnotationsRelation>> annotationsToRelations,
			IBioTMLAnnotation prevAnnotation) throws BioTMLException {
		
		IBioTMLAnnotation annotation;
		Set<IBioTMLAnnotationsRelation> relationsToFix = annotationsToRelations.get(prevAnnotation);
		annotation = new BioTMLAnnotationImpl(doc.getID(), tokenClass, prevAnnotation.getStartOffset(), token.getEndOffset(), predictionScore);
		relations.removeAll(relationsToFix);
		
		for(IBioTMLAnnotationsRelation relation : relationsToFix){
			Set<IBioTMLAnnotation> relationannots = new LinkedHashSet<>();
			for(IBioTMLAnnotation annotationInRelation : relation.getRelation()){
				if(!annotationInRelation.equals(prevAnnotation)){
					relationannots.add(annotationInRelation);
				}else{
					relationannots.add(annotation);
				}
			}
			relations.add(new BioTMLAnnotationsRelationImpl(relationannots, relation.getScore()));
		}
	}
	

	/**
	 * 
	 * Method that adds a relation regarding the position of the annotation and token.
	 * 
	 * @param relations - Set of all relations ({@link IBioTMLAnnotationsRelation}) predicted to add the relation.
	 * @param annotation - Annotation ({@link IBioTMLAnnotation}) that belongs to the relation.
	 * @param firstAnnotation - Annotation ({@link IBioTMLAnnotation}) that belongs to the relation.
	 * @param score - Score value associated to the prediction.
	 * @return Set of all relations ({@link IBioTMLAnnotationsRelation}) predicted.
	 * @throws BioTMLException
	 */
	private Set<IBioTMLAnnotationsRelation> addRelation(Set<IBioTMLAnnotationsRelation> relations, IBioTMLAnnotation annotation, IBioTMLAnnotation firstAnnotation, double score) throws BioTMLException{
		Set<IBioTMLAnnotation> relation = new LinkedHashSet<>();
		if(firstAnnotation.getStartOffset()>annotation.getEndOffset()){
			relation.add(annotation);
			relation.add(firstAnnotation);
			relations.add(new BioTMLAnnotationsRelationImpl(relation, score));
		}
		if(firstAnnotation.getEndOffset()<annotation.getStartOffset()){
			relation.add(firstAnnotation);
			relation.add(annotation);
			relations.add(new BioTMLAnnotationsRelationImpl(relation, score));
		}
		return relations;
	}
}
