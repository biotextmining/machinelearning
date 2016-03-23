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
	public int compareTo(IBioTMLAnnotation o) {
		if(	(o.getAnnotType().equals(this.getAnnotType())) 
				&&(o.getDocID()==this.getDocID())
				&&(o.getStartOffset()==this.getStartOffset())
				&&(o.getEndOffset()==this.getEndOffset())){
			return 0;
		}
		else if(o.getDocID()==this.getDocID()){
			if(o.getStartOffset()>this.getStartOffset()){
				return -1;
			}
			else if(o.getStartOffset()<this.getStartOffset()){
				return 1;
			} else{
				if(o.getEndOffset()>this.getEndOffset()){
					return -1;
				}else{
					return 1;
				}
			}

		}else{
			return 1;
		}
	}
}
