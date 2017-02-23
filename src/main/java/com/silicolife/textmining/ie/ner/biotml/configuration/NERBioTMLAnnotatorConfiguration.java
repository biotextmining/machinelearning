package com.silicolife.textmining.ie.ner.biotml.configuration;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import com.silicolife.textmining.core.datastructures.process.IEProcessImpl;
import com.silicolife.textmining.core.datastructures.process.ProcessRunStatusConfigurationEnum;
import com.silicolife.textmining.core.datastructures.process.ProcessTypeImpl;
import com.silicolife.textmining.core.datastructures.process.ner.NERConfigurationImpl;
import com.silicolife.textmining.core.datastructures.utils.Utils;
import com.silicolife.textmining.core.interfaces.core.document.corpus.ICorpus;
import com.silicolife.textmining.core.interfaces.process.IE.IIEProcess;
import com.silicolife.textmining.ie.ner.biotml.NERBioTMLTagger;

public class NERBioTMLAnnotatorConfiguration extends NERConfigurationImpl implements INERBioTMLAnnotatorConfiguration{
	
	public static String nerBioTMLUID = "ner.biotml";

	private String nlpId;
	private int threads;
	private String modelpath;
	private Set<String> nerclasses;
	
	public static final String bioTMLTagger = "BioTML NER Tagger";

	public NERBioTMLAnnotatorConfiguration(ICorpus corpus,ProcessRunStatusConfigurationEnum processRunStatusConfigurationEnum, String nlpSystemSelected, Set<String> nerClasses, int threadsnumber, String modelFilename){
		super(corpus, bioTMLTagger,build(corpus),processRunStatusConfigurationEnum);
		this.nlpId = nlpSystemSelected;
		this.nerclasses = nerClasses;
		this.threads = threadsnumber;
		this.modelpath = modelFilename;
	}
	
	private static IIEProcess build(ICorpus corpus)
	{
		String description = NERBioTMLTagger.bioTMLTagger + " " +Utils.SimpleDataFormat.format(new Date());
		Properties properties = new Properties();
		String notes = new String();
		IIEProcess runProcess =  new IEProcessImpl(corpus, description , notes , ProcessTypeImpl.getNERProcessType(), NERBioTMLTagger.bioTMLOrigin, properties );
		return runProcess;
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
		properties.put(NERBioTMLTaggerDefaultSettings.NLP_SYSTEM_ID, getNLPSystem().toString());
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