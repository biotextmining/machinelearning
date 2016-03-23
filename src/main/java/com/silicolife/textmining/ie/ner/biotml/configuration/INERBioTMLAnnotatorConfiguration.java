package com.silicolife.textmining.ie.ner.biotml.configuration;

import java.util.Set;

import com.silicolife.textmining.core.interfaces.process.IE.ner.INERConfiguration;
import com.silicolife.textmining.machinelearning.biotml.core.nlp.BioTMLNLPSystemsEnum;

public interface INERBioTMLAnnotatorConfiguration extends INERConfiguration{
	
	public BioTMLNLPSystemsEnum getNLPSystem();
	
	public int getThreads();
	
	public String getModelPath();
	
	public Set<String> getNERClasses();

}