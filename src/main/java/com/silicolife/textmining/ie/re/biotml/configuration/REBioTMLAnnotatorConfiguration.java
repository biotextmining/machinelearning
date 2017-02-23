package com.silicolife.textmining.ie.re.biotml.configuration;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.silicolife.textmining.core.datastructures.process.IEProcessImpl;
import com.silicolife.textmining.core.datastructures.process.ProcessRunStatusConfigurationEnum;
import com.silicolife.textmining.core.datastructures.process.ProcessTypeImpl;
import com.silicolife.textmining.core.datastructures.process.re.REConfigurationImpl;
import com.silicolife.textmining.core.datastructures.utils.Utils;
import com.silicolife.textmining.core.interfaces.core.document.corpus.ICorpus;
import com.silicolife.textmining.core.interfaces.process.IE.IIEProcess;
import com.silicolife.textmining.ie.re.biotml.REBioTMLTagger;

public class REBioTMLAnnotatorConfiguration extends REConfigurationImpl implements IREBioTMLAnnotatorConfiguration{
	
	public static String reBioTMLUID = "re.biotml";

	
	private String nlpsystem;
	private int threads;
	private String modelpath;
	
	public static final String bioTMLTagger = "BioTML RE Tagger";

	
	public REBioTMLAnnotatorConfiguration(ICorpus corpus,ProcessRunStatusConfigurationEnum processRunStatusConfigurationEnum,String nlpSystemSelected, IIEProcess iieprocess, int threadsnumber, String modelFilename){
		super(bioTMLTagger, corpus,build(corpus),processRunStatusConfigurationEnum,iieprocess,false,null);
		this.nlpsystem = nlpSystemSelected;
		this.threads = threadsnumber;
		this.modelpath = modelFilename;
	}
	
	public static IIEProcess build(ICorpus corpus)
	{
		String name = REBioTMLTagger.bioTMLTagger  + " " +Utils.SimpleDataFormat.format(new Date());
		String notes = new String();
		Properties properties = new Properties();
		IEProcessImpl reProcess = new IEProcessImpl(corpus,name ,
				notes, ProcessTypeImpl.getREProcessType(), REBioTMLTagger.bioTMLOrigin, properties);
		return reProcess;
	}

	public String getNLPSystem() {
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
		properties.put(REBioTMLTaggerDefaultSettings.NLP_SYSTEM_ID, getNLPSystem().toString());
		properties.put(REBioTMLTaggerDefaultSettings.NUM_THREADS, String.valueOf(getThreads()));
		return properties;
	}

	@Override
	public String getConfigurationUID() {
		return REBioTMLAnnotatorConfiguration.reBioTMLUID;
	}

	@Override
	public void setConfigurationUID(String uid) {
		REBioTMLAnnotatorConfiguration.reBioTMLUID=uid;		
	}

}