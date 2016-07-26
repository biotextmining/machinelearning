package com.silicolife.textmining.machinelearning.biotml.core.corpora;

import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLAnnotation;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLCorpus;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLDocument;

/**
 * 
 * Represents a annotation.
 * 
 * @since 1.0.0
 * @version 1.0.1
 * @author Ruben Rodrigues ({@code rrodrigues@silicolife.com})
 */

public class BioTMLAnnotation implements IBioTMLAnnotation{

	private static final long serialVersionUID = 1L;
	private long docID;
	private String annotType;
	private long startOffset;
	private long endOffset;
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
	public BioTMLAnnotation( long docID, String annotType, long startOffset, long endOffset ){
		this.docID = docID;
		this.annotType = annotType;
		this.startOffset = startOffset;
		this.endOffset = endOffset;
		this.score = 0;
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
	public BioTMLAnnotation( long docID, String annotType, long startOffset, long endOffset, double score ){
		this.docID = docID;
		this.annotType = annotType;
		this.startOffset = startOffset;
		this.endOffset = endOffset;
		this.score = score;
	}

	public long getDocID() {
		return docID;
	}

	public String getAnnotType() {
		return annotType;
	}

	public long getStartOffset() {
		return startOffset;
	}

	public long getEndOffset() {
		return endOffset;
	}

	public double getScore() {
		return score;
	}

	public boolean haveTheSameOffsets(IBioTMLAnnotation annotationToCompare){
		if(	this.getStartOffset() == annotationToCompare.getStartOffset()
				&& this.getEndOffset() == annotationToCompare.getEndOffset()){
			return true;
		}
		return false;
	}

	public String toString(){
		return "DocID: " + getDocID() + " Type: " + getAnnotType() + " ( " + getStartOffset() + " - " + getEndOffset() + " ) ";
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((annotType == null) ? 0 : annotType.hashCode());
		result = prime * result + (int) (docID ^ (docID >>> 32));
		result = prime * result + (int) (endOffset ^ (endOffset >>> 32));
		long temp;
		temp = Double.doubleToLongBits(score);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + (int) (startOffset ^ (startOffset >>> 32));
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
		BioTMLAnnotation other = (BioTMLAnnotation) obj;
		if (annotType == null) {
			if (other.annotType != null)
				return false;
		} else if (!annotType.equals(other.annotType))
			return false;
		if (docID != other.docID)
			return false;
		if (endOffset != other.endOffset)
			return false;
		if (Double.doubleToLongBits(score) != Double.doubleToLongBits(other.score))
			return false;
		if (startOffset != other.startOffset)
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
		if(o.getScore()<this.getScore()){
			return 1;
		}
		if(o.getScore()>this.getScore()){
			return -1;
		}
		return o.getAnnotType().compareTo(this.getAnnotType());
	}
}
