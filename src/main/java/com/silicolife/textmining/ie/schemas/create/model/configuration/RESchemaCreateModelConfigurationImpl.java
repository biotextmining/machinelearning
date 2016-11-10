package com.silicolife.textmining.ie.schemas.create.model.configuration;

import com.silicolife.textmining.core.interfaces.process.IE.IRESchema;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLFeatureGeneratorConfigurator;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLModelConfigurator;

public class RESchemaCreateModelConfigurationImpl implements IRESchemaCreateModelConfiguration{
	
	
	private IRESchema reSchema;
	private String nlpId;
	private IBioTMLFeatureGeneratorConfigurator configurator;
	private IBioTMLModelConfigurator modelConfiguration;
	private String fileModelPath;

	public RESchemaCreateModelConfigurationImpl(IRESchema reSchema,String bioTMLNLPSystems,IBioTMLFeatureGeneratorConfigurator configurator,
			IBioTMLModelConfigurator modelConfiguration, String fileModelPath)
	{
		this.reSchema=reSchema;
		this.nlpId=bioTMLNLPSystems;
		this.configurator=configurator;
		this.modelConfiguration=modelConfiguration;
		this.fileModelPath=fileModelPath;
	}

	@Override
	public IRESchema getRESchema() {
		return reSchema;
	}

	@Override
	public String getBioTMLNLPSystem() {
		return nlpId;
	}

	@Override
	public IBioTMLFeatureGeneratorConfigurator getBioTMLFeatureGeneratorConfigurator() {
		return configurator;
	}

	@Override
	public IBioTMLModelConfigurator getBioTMLModelConfigurator() {
		return modelConfiguration;
	}

	@Override
	public String getFileModelPath() {
		return fileModelPath;
	}

}
