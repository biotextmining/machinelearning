package com.silicolife.textmining.machinelearning.biotml.core.models;

import java.util.HashSet;
import java.util.Set;

import com.silicolife.textmining.machinelearning.biotml.core.BioTMLConstants;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLAnnotation;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLAssociation;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLREMethodologyConfiguration;

public class BioTMLREMethodologyConfigurationImpl implements IBioTMLREMethodologyConfiguration{

	private static final long serialVersionUID = 1L;
	private Set<IBioTMLAssociation<IBioTMLAnnotation, IBioTMLAnnotation>> allowedAssociations;

	public BioTMLREMethodologyConfigurationImpl(){
		this.allowedAssociations = new HashSet<>();
	}

	@Override
	public Set<IBioTMLAssociation<IBioTMLAnnotation, IBioTMLAnnotation>> getAllowedAssociations() {
		return allowedAssociations;
	}
	
	@Override
	public void addAllowedAssociation(IBioTMLAssociation<IBioTMLAnnotation, IBioTMLAnnotation> association){
		getAllowedAssociations().add(association);
	}
	
	@Override
	public boolean containsTriggers() {
		for(IBioTMLAssociation<IBioTMLAnnotation, IBioTMLAnnotation> association : getAllowedAssociations())
			if(association.getEntryOne().getAnnotationType().equals(BioTMLConstants.trigger.toString())
					|| association.getEntryTwo().getAnnotationType().equals(BioTMLConstants.trigger.toString()))
				return true;
		
		return false;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((allowedAssociations == null) ? 0 : allowedAssociations.hashCode());
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
		BioTMLREMethodologyConfigurationImpl other = (BioTMLREMethodologyConfigurationImpl) obj;
		if (allowedAssociations == null) {
			if (other.allowedAssociations != null)
				return false;
		} else if (!allowedAssociations.equals(other.allowedAssociations))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "BioTMLREMethodologyConfiguration [allowedAssociations=" + allowedAssociations + "]";
	}
	
}
