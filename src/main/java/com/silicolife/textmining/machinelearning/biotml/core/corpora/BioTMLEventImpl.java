package com.silicolife.textmining.machinelearning.biotml.core.corpora;

import java.util.HashSet;
import java.util.Set;

import com.silicolife.textmining.machinelearning.biotml.core.exception.BioTMLException;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLAssociation;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLEntity;
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

public class BioTMLEventImpl extends BioTMLAnnotationImpl implements IBioTMLEvent {
	
	private static final long serialVersionUID = 1L;
	private IBioTMLAssociation<IBioTMLEntity, IBioTMLEntity> association;
	
	public BioTMLEventImpl(IBioTMLAssociation<IBioTMLEntity, IBioTMLEntity> association, String eventType){
		this(association, eventType, 100000.0);
	}
	
	public BioTMLEventImpl(IBioTMLAssociation<IBioTMLEntity, IBioTMLEntity> association, String eventType, double score){
		super(eventType, score);
		this.association = association;
	}
	
	@Override
	public IBioTMLEntity getTrigger() {
		return association.getEntryOne();
	}

	@Override
	public IBioTMLEntity getEntity() {
		return association.getEntryTwo();
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public IBioTMLAssociation getAssociation() {
		return association;
	}
	
	@Override
	public long getDocID(){
		return getTrigger().getDocID();
	}
	
	@Override
	public Set<IBioTMLEntity> getAllAnnotationsFromEvent() {
		Set<IBioTMLEntity> annotations = new HashSet<>();
		
		if(getAssociation().getEntryOne() instanceof IBioTMLEntity)
			annotations.add((IBioTMLEntity) getAssociation().getEntryOne());
		else if(getAssociation().getEntryOne() instanceof IBioTMLEvent)
			annotations.addAll(((IBioTMLEvent)getAssociation().getEntryOne()).getAllAnnotationsFromEvent());
		
		if(getAssociation().getEntryTwo() instanceof IBioTMLEntity)
			annotations.add((IBioTMLEntity) getAssociation().getEntryTwo());
		else if(getAssociation().getEntryTwo() instanceof IBioTMLEvent)
			annotations.addAll(((IBioTMLEvent)getAssociation().getEntryTwo()).getAllAnnotationsFromEvent());
		
		return annotations;
	}

	@Override
	public boolean findAnnotationInEvent(IBioTMLEntity annot) {
		if(annot.equals(getTrigger()))
			return true;
		if(annot.equals(getEntity()))
			return true;
		return false;
	}

	@Override
	public IBioTMLEntity getAnnotationInEventByOffsets(long startOffset, long endOffset) throws BioTMLException{
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
		int result = super.hashCode();
		result = prime * result + ((association == null) ? 0 : association.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		BioTMLEventImpl other = (BioTMLEventImpl) obj;
		if (association == null) {
			if (other.association != null)
				return false;
		} else if (!association.equals(other.association))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "BioTMLEventImpl [association=" + association + "]";
	}

}
