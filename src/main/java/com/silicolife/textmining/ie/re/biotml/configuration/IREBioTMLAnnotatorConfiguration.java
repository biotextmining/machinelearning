package com.silicolife.textmining.ie.re.biotml.configuration;

import com.silicolife.textmining.core.interfaces.process.IE.re.IREConfiguration;
import com.silicolife.textmining.machinelearning.biotml.core.nlp.BioTMLNLPSystemsEnum;

public interface IREBioTMLAnnotatorConfiguration extends IREConfiguration{
	
	public BioTMLNLPSystemsEnum getNLPSystem();
	
	public int getThreads();
	
	public String getModelPath();

}
