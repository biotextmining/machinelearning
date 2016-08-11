package com.silicolife.textmining.machinelearning.biotml.core.corpora;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import com.silicolife.textmining.machinelearning.biotml.core.exception.BioTMLException;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLAnnotation;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLAnnotationsRelation;

/**
 * 
 * Represents a relation annotation.
 * 
 * @since 1.0.0
 * @version 1.0.2
 * @author Ruben Rodrigues ({@code rrodrigues@silicolife.com})
 */

public class BioTMLAnnotationsRelationImpl implements IBioTMLAnnotationsRelation {
	
	private static final long serialVersionUID = 1L;
	private Set<IBioTMLAnnotation> relation;
	private double score;
	
	/**
	 * 
	 * Initializes a relation.
	 * 
	 * @param relation - A set of {@link IBioTMLAnnotation}. (The set must be a linked!).
	 * @throws BioTMLException
	 */
	
	public BioTMLAnnotationsRelationImpl(Set<IBioTMLAnnotation> relation) throws BioTMLException{
		if(!relationIsValid(relation)){
			throw new BioTMLException(26);
		}
		this.relation = relation;
		this.score = 0;
	}
	
	public BioTMLAnnotationsRelationImpl(Set<IBioTMLAnnotation> relation, double score) throws BioTMLException{
		if(!relationIsValid(relation)){
			throw new BioTMLException(26);
		}
		this.relation = relation;
		this.score = score;
	}

	public Set<IBioTMLAnnotation> getRelation(){
		return relation;
	}
	
	public double getScore(){
		return score;
	}
	
	public long getDocID(){
		Iterator<IBioTMLAnnotation> itAnnt = getRelation().iterator();
		IBioTMLAnnotation annotation = itAnnt.next();
		return annotation.getDocID();
	}
	
	private boolean relationIsValid(Set<IBioTMLAnnotation> relation){
		if(!(relation instanceof LinkedHashSet)){
			return false;
		}
		
		if(relation.size()<2){
			return false;
		}
		for(IBioTMLAnnotation annot : relation){
			if( annot == null){
				return false;
			}
		}
		return true;
	}
	
	public String getRelationType(){
		StringBuilder relationType = new StringBuilder();
		Iterator<IBioTMLAnnotation> itRel = getRelation().iterator();
		while(itRel.hasNext()){
			if(relationType.length()>0){
				relationType.append(" - ");
			}
			IBioTMLAnnotation annotation = itRel.next();
			relationType.append(annotation.getAnnotType());
		}
		return relationType.toString();
	}
	
	public boolean findAnnotationInRelation(IBioTMLAnnotation annot){
		for(IBioTMLAnnotation annotInRelation : getRelation()){
			if(annot.compareTo(annotInRelation) == 0){
				return true;
			}
		}
		return false;
	}
	
	public IBioTMLAnnotation getAnnotationInRelationByOffsets(long startOffset, long endOffset) throws BioTMLException{
		Iterator<IBioTMLAnnotation> itAnnotation = getRelation().iterator();
		while(itAnnotation.hasNext()){
			IBioTMLAnnotation annot = itAnnotation.next();
			if(annot.getAnnotationOffsets().offsetsEquals(startOffset, endOffset)){
				return annot;
			}
		}
		throw new BioTMLException(28);
	}
	
	public IBioTMLAnnotation getFirstAnnotationByType(String annotType) throws BioTMLException{
		Iterator<IBioTMLAnnotation> itRel = getRelation().iterator();
		while(itRel.hasNext()){
			IBioTMLAnnotation annot = itRel.next();
			if(annot.getAnnotType().equals(annotType)){
				return annot;
			}
		}
		throw new BioTMLException(25);
	}
	
	public Set<IBioTMLAnnotation> getAnnotsAtLeftOfAnnotation(IBioTMLAnnotation annot) throws BioTMLException{
		Set<IBioTMLAnnotation> leftAnnots = new LinkedHashSet<IBioTMLAnnotation>();
		Iterator<IBioTMLAnnotation> itRel = getRelation().iterator();
		boolean isTheAnnot = false;
		while(itRel.hasNext() && !isTheAnnot){
			IBioTMLAnnotation leftAnnot = itRel.next();
			if(leftAnnot.equals(annot)){
				isTheAnnot = true;
			}else{
				leftAnnots.add(leftAnnot);
			}
		}
		if(leftAnnots.size() == getRelation().size()){
			throw new BioTMLException(25);
		}
		return leftAnnots;
	}
	
	public Set<IBioTMLAnnotation> getAnnotsAtRightOfAnnotation(IBioTMLAnnotation annot) throws BioTMLException{
		Set<IBioTMLAnnotation> rightAnnots = new LinkedHashSet<IBioTMLAnnotation>();
		Iterator<IBioTMLAnnotation> itRel = getRelation().iterator();
		boolean isTheAnnot = false;
		while(itRel.hasNext()){
			IBioTMLAnnotation rightAnnot = itRel.next();
			if(rightAnnot.equals(annot)){
				isTheAnnot = true;
			}else{
				if(isTheAnnot){
					rightAnnots.add(rightAnnot);
				}
			}
		}
		if(rightAnnots.size() == 0){
			throw new BioTMLException(25);
		}
		return rightAnnots;
	}
	
	public String toString(){
		StringBuilder sb = new StringBuilder();
		Iterator<IBioTMLAnnotation> itRel = getRelation().iterator();
		while(itRel.hasNext()){
			IBioTMLAnnotation annot = itRel.next();
			if(sb.length()>0){
				sb.append(" - ");
			}
			sb.append(annot.toString());
		}
		return sb.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((relation == null) ? 0 : relation.hashCode());
		long temp;
		temp = Double.doubleToLongBits(score);
		result = prime * result + (int) (temp ^ (temp >>> 32));
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
		BioTMLAnnotationsRelationImpl other = (BioTMLAnnotationsRelationImpl) obj;
		if (relation == null) {
			if (other.relation != null)
				return false;
		} else if (!relation.equals(other.relation))
			return false;
		if (Double.doubleToLongBits(score) != Double.doubleToLongBits(other.score))
			return false;
		return true;
	}

	@Override
	public boolean haveTheSameOffsetsAndAnnotationTypes(IBioTMLAnnotationsRelation relation) {
		if(!getRelationType().equals(relation.getRelationType())){
			return false;
		}
		if(getRelation().size() != relation.getRelation().size()){
			return false;
		}
		Iterator<IBioTMLAnnotation> itThisRelation = getRelation().iterator();
		Iterator<IBioTMLAnnotation> itOtherRelation = relation.getRelation().iterator();
		while(itThisRelation.hasNext() && itOtherRelation.hasNext()){
			IBioTMLAnnotation thisAnnot = itThisRelation.next();
			IBioTMLAnnotation otherAnnot = itOtherRelation.next();
			if(!thisAnnot.getAnnotationOffsets().equals(otherAnnot.getAnnotationOffsets())){
				return false;
			}
		}
		
		return true;
	}

}