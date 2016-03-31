package com.silicolife.textmining.ie.schemas.create.model.configuration;

import java.util.List;

import com.silicolife.textmining.core.interfaces.process.IE.INERSchema;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLFeatureGeneratorConfigurator;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLModelConfigurator;
import com.silicolife.textmining.machinelearning.biotml.core.nlp.BioTMLNLPSystemsEnum;

public class NERSchemaCreateModelConfigurationImpl implements INERSchemaCreateModelConfiguration{
	
	
	private INERSchema nerSchema;
	private BioTMLNLPSystemsEnum bioTMLSystemNLP;
	private IBioTMLFeatureGeneratorConfigurator featuresSet;
	private List<IBioTMLModelConfigurator> modelConfigurations;
	private String flemodelpath;

	public NERSchemaCreateModelConfigurationImpl(INERSchema nerSchema,BioTMLNLPSystemsEnum bioTMLSystemNLP,IBioTMLFeatureGeneratorConfigurator featuresSet,
			List<IBioTMLModelConfigurator> modelConfigurations,String flemodelpath)
	{
		this.nerSchema=nerSchema;
		this.bioTMLSystemNLP=bioTMLSystemNLP;
		this.featuresSet=featuresSet;
		this.modelConfigurations=modelConfigurations;
		this.flemodelpath=flemodelpath;
	}

	@Override
	public INERSchema getNERSChema() {
		return nerSchema;
	}

	@Override
	public BioTMLNLPSystemsEnum getBioTMLNLPSystemsEnum() {
		return bioTMLSystemNLP;
	}

	@Override
	public IBioTMLFeatureGeneratorConfigurator getFeaturesSet() {
		return featuresSet;
	}

	@Override
	public List<IBioTMLModelConfigurator> getModelConfigurations() {
		return modelConfigurations;
	}

	@Override
	public String getFileModelPath() {
		return flemodelpath;
	}

}
