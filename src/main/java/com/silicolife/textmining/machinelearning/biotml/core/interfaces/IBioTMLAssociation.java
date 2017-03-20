package com.silicolife.textmining.machinelearning.biotml.core.interfaces;

import java.io.Serializable;

public interface IBioTMLAssociation<O,T> extends Serializable{
	
	public O getEntryOne();
	
	public T getEntryTwo();
	
	public boolean isValid();

}
