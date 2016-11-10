package com.silicolife.textmining.ie.ner.biotml.configuration;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.silicolife.textmining.core.datastructures.process.ner.NERConfigurationImpl;
import com.silicolife.textmining.core.interfaces.core.document.corpus.ICorpus;

public class NERBioTMLAnnotatorConfiguration extends NERConfigurationImpl implements INERBioTMLAnnotatorConfiguration{
	
	public static String nerBioTMLUID = "ner.biotml";

	private String nlpId;
	private int threads;
	private String modelpath;
	private Set<String> nerclasses;
	
	public static final String bioTMLTagger = "BioTML NER Tagger";

	public NERBioTMLAnnotatorConfiguration(ICorpus corpus, String nlpSystemSelected, Set<String> nerClasses, int threadsnumber, String modelFilename){
		super(corpus, bioTMLTagger, bioTMLTagger);
		this.nlpId = nlpSystemSelected;
		this.nerclasses = nerClasses;
		this.threads = threadsnumber;
		this.modelpath = modelFilename;
	}

	public String getNLPSystem() {
		return nlpId;
	}

	public int getThreads() {
		return threads;
	}

	public String getModelPath() {
		return modelpath;
	}
	
	public Set<String> getNERClasses() {
		return nerclasses;
	}
	
	public Map<String, String> getNERProperties() {
		Map<String, String> properties = new HashMap<String, String>();
		properties.put(NERBioTMLTaggerDefaultSettings.NLP_SYSTEM, getNLPSystem().toString());
		properties.put(NERBioTMLTaggerDefaultSettings.NUM_THREADS, String.valueOf(getThreads()));
		return properties;
	}

	public void setConfiguration(Object obj) {
	}

	@Override
	public String getConfigurationUID() {
		return NERBioTMLAnnotatorConfiguration.nerBioTMLUID;
	}

	@Override
	public void setConfigurationUID(String uid) {
		NERBioTMLAnnotatorConfiguration.nerBioTMLUID=uid;
		
	}

}