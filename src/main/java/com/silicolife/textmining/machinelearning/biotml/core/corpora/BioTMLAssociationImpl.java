package com.silicolife.textmining.machinelearning.biotml.core.corpora;

import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLAssociation;

public class BioTMLAssociationImpl<O,T> implements IBioTMLAssociation<O,T>{
	
	private O entryOne;
	private T entryTwo;

	public BioTMLAssociationImpl(O entryOne, T entryTwo){
		this.entryOne = entryOne;
		this.entryTwo = entryTwo;
	}

	public O getEntryOne() {
		return entryOne;
	}

	public T getEntryTwo() {
		return entryTwo;
	}
	
	public boolean isValid(){
		return !getEntryOne().equals(getEntryTwo());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (((entryOne == null) ? 0 : entryOne.hashCode()) + ((entryTwo == null) ? 0 : entryTwo.hashCode()));
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
		BioTMLAssociationImpl<?,?> other = (BioTMLAssociationImpl<?,?>) obj;
		if ((entryOne.equals(other.entryOne) && entryTwo.equals(other.entryTwo))
				|| (entryOne.equals(other.entryTwo) && entryTwo.equals(other.entryOne)))
			return true;
		return false;
	}
	
}
