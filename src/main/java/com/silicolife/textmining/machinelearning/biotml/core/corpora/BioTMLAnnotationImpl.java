package com.silicolife.textmining.machinelearning.biotml.core.corpora;

import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLAnnotation;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLCorpus;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLDocument;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLOffsetsPair;

/**
 * 
 * Represents a annotation.
 * 
 * @since 1.0.0
 * @version 1.0.2
 * @author Ruben Rodrigues ({@code rrodrigues@silicolife.com})
 */

public class BioTMLAnnotationImpl implements IBioTMLAnnotation{

	private static final long serialVersionUID = 1L;
	private long docID;
	private IBioTMLOffsetsPair annotationOffsets;
	private String annotType;
	private double score;

	/**
	 * 
	 * Initializes one annotation in one document.
	 * 
	 * @param docID - {@link IBioTMLDocument} ID present in {@link IBioTMLCorpus}.
	 * @param annotType - String that represents the annotation class type (e.g. gene, protein).
	 * @param startOffset - Annotation start offset in raw text.
	 * @param endOffset - Annotation end offset in raw text.
	 */
	public BioTMLAnnotationImpl( long docID, String annotType, long startOffset, long endOffset ){
		this(docID, annotType, startOffset, endOffset, 0.0);
	}

	/**
	 * 
	 * Initializes one annotation in one document.
	 * 
	 * @param docID - {@link IBioTMLDocument} ID present in {@link IBioTMLCorpus}.
	 * @param annotType - String that represents the annotation class type (e.g. gene, protein).
	 * @param startOffset - Annotation start offset in raw text.
	 * @param endOffset - Annotation end offset in raw text.
	 * @param score - Score value from model evaluation.
	 */
	public BioTMLAnnotationImpl( long docID, String annotType, long startOffset, long endOffset, double score ){
		this.docID = docID;
		this.annotationOffsets = new BioTMLOffsetsPairImpl(startOffset, endOffset);
		this.annotType = annotType;
		this.score = score;
	}

	@Override
	public long getDocID() {
		return docID;
	}
	
	@Override
	public IBioTMLOffsetsPair getAnnotationOffsets() {
		return annotationOffsets;
	}

	@Override
	public String getAnnotType() {
		return annotType;
	}

	@Override
	public long getStartOffset() {
		return getAnnotationOffsets().getStartOffset();
	}

	@Override
	public long getEndOffset() {
		return getAnnotationOffsets().getEndOffset();
	}

	@Override
	public double getScore() {
		return score;
	}

	@Override
	public String toString(){
		return "DocID: " + getDocID() + " Type: " + getAnnotType() + " ( " + getStartOffset() + " - " + getEndOffset() + " ) ";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((annotType == null) ? 0 : annotType.hashCode());
		result = prime * result + ((annotationOffsets == null) ? 0 : annotationOffsets.hashCode());
		result = prime * result + (int) (docID ^ (docID >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BioTMLAnnotationImpl other = (BioTMLAnnotationImpl) obj;
		if (annotType == null) {
			if (other.annotType != null)
				return false;
		} else if (!annotType.equals(other.annotType))
			return false;
		if (annotationOffsets == null) {
			if (other.annotationOffsets != null)
				return false;
		} else if (!annotationOffsets.equals(other.annotationOffsets))
			return false;
		if (docID != other.docID)
			return false;
		return true;
	}

	@Override
	public int compareTo(IBioTMLAnnotation o) {
		if(	this.equals(o)){
			return 0;
		}
		if(o.getDocID()>this.getDocID()){
			return -1;
		}
		if(o.getDocID()<this.getDocID()){
			return 1;
		}
		if(o.getStartOffset()<this.getStartOffset()){
			return 1;
		}
		if(o.getStartOffset()>this.getStartOffset()){
			return -1;
		}
		if(o.getEndOffset()<this.getEndOffset()){
			return 1;
		}
		if(o.getEndOffset()>this.getEndOffset()){
			return -1;
		}
//		if(o.getScore()<this.getScore()){
//			return 1;
//		}
//		if(o.getScore()>this.getScore()){
//			return -1;
//		}
		return o.getAnnotType().compareTo(this.getAnnotType());
	}
}
