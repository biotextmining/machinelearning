package com.silicolife.textmining.machinelearning.biotml.core.annotator.processors;

import java.util.Iterator;
import java.util.Set;

import com.silicolife.textmining.machinelearning.biotml.core.BioTMLConstants;
import com.silicolife.textmining.machinelearning.biotml.core.corpora.BioTMLEntityImpl;
import com.silicolife.textmining.machinelearning.biotml.core.corpora.BioTMLEventImpl;
import com.silicolife.textmining.machinelearning.biotml.core.exception.BioTMLException;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLAssociation;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLCorpus;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLDocument;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLEntity;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLEvent;
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
			IBioTMLToken token, String tokenClass, String prediction,double predictionScore){
		if(token.getToken().contains("New") && !prediction.equals(BioTMLConstants.o.toString()))
			System.out.println();
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
		if(!annotations.isEmpty()){
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
	public IBioTMLEntity getEntity(IBioTMLCorpus corpus, IBioTMLDocument doc, int sentIndex, int startTokenIndex, int endTokenIndex){
		IBioTMLToken firstTokenAnnotation = doc.getSentence(sentIndex).getToken(startTokenIndex);
		IBioTMLToken lastTokenAnnotation = doc.getSentence(sentIndex).getToken(endTokenIndex);
		IBioTMLEntity annotation = null;
		try {
			annotation = corpus.getEntityFromDocAndOffsets(doc.getID(),firstTokenAnnotation.getStartOffset(), lastTokenAnnotation.getEndOffset());
		} catch (BioTMLException e) {}
		return annotation;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Set<IBioTMLEvent> addEvent(Set<IBioTMLEvent> events, IBioTMLAssociation association, String eventClass, double score){
		events.add(new BioTMLEventImpl(association, eventClass, score));
		return events;
	}
}
