package com.silicolife.textmining.machinelearning.biotml.core.interfaces;

import java.io.Serializable;
import java.util.Set;

public interface IBioTMLREMethodologyConfiguration extends Serializable{

	public Set<IBioTMLAssociation<IBioTMLAnnotation, IBioTMLAnnotation>> getAllowedAssociations();
	
	public void addAllowedAssociation(IBioTMLAssociation<IBioTMLAnnotation, IBioTMLAnnotation> association);
	
	public boolean containsTriggers();
}
