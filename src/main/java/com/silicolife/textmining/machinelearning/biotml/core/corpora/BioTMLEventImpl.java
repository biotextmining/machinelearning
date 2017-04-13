package com.silicolife.textmining.machinelearning.biotml.core.corpora;

import java.util.HashSet;
import java.util.Set;

import com.silicolife.textmining.machinelearning.biotml.core.exception.BioTMLException;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLAnnotation;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLAssociation;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLEvent;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLOffsetsPair;

/**
 * 
 * Represents a event annotation.
 * 
 * @since 1.0.0
 * @version 1.0.2
 * @author Ruben Rodrigues ({@code rrodrigues@silicolife.com})
 */

public class BioTMLEventImpl implements IBioTMLEvent {
	
	private static final long serialVersionUID = 1L;
	private IBioTMLAssociation<IBioTMLAnnotation, IBioTMLAnnotation> association;
	private String eventType;
	private double score;
	
	public BioTMLEventImpl(IBioTMLAssociation<IBioTMLAnnotation, IBioTMLAnnotation> association, String eventType){
		this(association, eventType, 100000.0);
	}
	
	public BioTMLEventImpl(IBioTMLAssociation<IBioTMLAnnotation, IBioTMLAnnotation> association, String eventType, double score){
		this.association = association;
		this.eventType = eventType;
		this.score = score;
	}
	
	@Override
	public IBioTMLAnnotation getTrigger() {
		return association.getEntryOne();
	}

	@Override
	public IBioTMLAnnotation getEntity() {
		return association.getEntryTwo();
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public IBioTMLAssociation getAssociation() {
		return association;
	}

	@Override
	public String getEventType() {
		return eventType;
	}

	@Override
	public double getScore(){
		return score;
	}
	
	@Override
	public long getDocID(){
		return getTrigger().getDocID();
	}
	
	@Override
	public Set<IBioTMLAnnotation> getAllAnnotationsFromEvent() {
		Set<IBioTMLAnnotation> annotations = new HashSet<>();
		
		if(getAssociation().getEntryOne() instanceof IBioTMLAnnotation)
			annotations.add((IBioTMLAnnotation) getAssociation().getEntryOne());
		else if(getAssociation().getEntryOne() instanceof IBioTMLEvent)
			annotations.addAll(((IBioTMLEvent)getAssociation().getEntryOne()).getAllAnnotationsFromEvent());
		
		if(getAssociation().getEntryTwo() instanceof IBioTMLAnnotation)
			annotations.add((IBioTMLAnnotation) getAssociation().getEntryTwo());
		else if(getAssociation().getEntryTwo() instanceof IBioTMLEvent)
			annotations.addAll(((IBioTMLEvent)getAssociation().getEntryTwo()).getAllAnnotationsFromEvent());
		
		return annotations;
	}

	@Override
	public boolean findAnnotationInEvent(IBioTMLAnnotation annot) {
		if(annot.equals(getTrigger()))
			return true;
		if(annot.equals(getEntity()))
			return true;
		return false;
	}

	@Override
	public IBioTMLAnnotation getAnnotationInEventByOffsets(long startOffset, long endOffset) throws BioTMLException{
		IBioTMLOffsetsPair offsetPair = new BioTMLOffsetsPairImpl(startOffset, endOffset);
		if(getTrigger().getAnnotationOffsets().equals(offsetPair))
			return getTrigger();
		if(getEntity().getAnnotationOffsets().equals(offsetPair))
			return getEntity();
		
		throw new BioTMLException(28);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((association == null) ? 0 : association.hashCode());
		result = prime * result + ((eventType == null) ? 0 : eventType.hashCode());
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
		BioTMLEventImpl other = (BioTMLEventImpl) obj;
		if (association == null) {
			if (other.association != null)
				return false;
		} else if (!association.equals(other.association))
			return false;
		if (eventType == null) {
			if (other.eventType != null)
				return false;
		} else if (!eventType.equals(other.eventType))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "( " + association + ", eventType=" + eventType + ", score=" + score + " )";
	}
	
	

}
