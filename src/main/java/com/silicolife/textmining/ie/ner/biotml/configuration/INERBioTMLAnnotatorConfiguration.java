package com.silicolife.textmining.ie.ner.biotml.configuration;

import java.util.Set;

import com.silicolife.textmining.core.interfaces.process.IE.ner.INERConfiguration;

public interface INERBioTMLAnnotatorConfiguration extends INERConfiguration{
	
	public String getNLPSystem();
	
	public int getThreads();
	
	public String getModelPath();
	
	public Set<String> getNERClasses();

}