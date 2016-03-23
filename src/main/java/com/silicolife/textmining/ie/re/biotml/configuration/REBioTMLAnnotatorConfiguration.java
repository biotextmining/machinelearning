package com.silicolife.textmining.ie.re.biotml.configuration;

import java.util.HashMap;
import java.util.Map;

import com.silicolife.textmining.core.datastructures.process.re.REConfigurationImpl;
import com.silicolife.textmining.core.interfaces.core.document.corpus.ICorpus;
import com.silicolife.textmining.core.interfaces.process.IE.IIEProcess;
import com.silicolife.textmining.machinelearning.biotml.core.nlp.BioTMLNLPSystemsEnum;

public class REBioTMLAnnotatorConfiguration extends REConfigurationImpl implements IREBioTMLAnnotatorConfiguration{
	
	private BioTMLNLPSystemsEnum nlpsystem;
	private int threads;
	private String modelpath;
	
	public static final String bioTMLTagger = "BioTML RE Tagger";

	
	public REBioTMLAnnotatorConfiguration(ICorpus corpus, BioTMLNLPSystemsEnum nlpSystemSelected, IIEProcess iieprocess, int threadsnumber, String modelFilename){
		super(bioTMLTagger, corpus,iieprocess,false,null);
		this.nlpsystem = nlpSystemSelected;
		this.threads = threadsnumber;
		this.modelpath = modelFilename;
	}

	public BioTMLNLPSystemsEnum getNLPSystem() {
		return nlpsystem;
	}

	public int getThreads() {
		return threads;
	}

	public String getModelPath() {
		return modelpath;
	}

	public Map<String, String> getREProperties() {
		Map<String, String> properties = new HashMap<String, String>();
		properties.put(REBioTMLTaggerDefaultSettings.NLP_SYSTEM, getNLPSystem().toString());
		properties.put(REBioTMLTaggerDefaultSettings.NUM_THREADS, String.valueOf(getThreads()));
		return properties;
	}

}