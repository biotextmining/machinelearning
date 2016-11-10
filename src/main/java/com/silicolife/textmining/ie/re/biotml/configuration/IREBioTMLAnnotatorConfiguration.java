package com.silicolife.textmining.ie.re.biotml.configuration;

import com.silicolife.textmining.core.interfaces.process.IE.re.IREConfiguration;

public interface IREBioTMLAnnotatorConfiguration extends IREConfiguration{
	
	public String getNLPSystem();
	
	public int getThreads();
	
	public String getModelPath();

}
