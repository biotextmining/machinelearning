package com.silicolife.textmining.machinelearning.biotml.core.interfaces;

public interface IBioTMLAssociation<O,T> {
	
	public O getEntryOne();
	
	public T getEntryTwo();
	
	public boolean isValid();

}
