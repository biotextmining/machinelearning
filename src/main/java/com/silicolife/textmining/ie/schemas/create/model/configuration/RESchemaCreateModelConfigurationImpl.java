package com.silicolife.textmining.ie.schemas.create.model.configuration;

import com.silicolife.textmining.core.interfaces.process.IE.IRESchema;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLFeatureGeneratorConfigurator;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLModelConfigurator;
import com.silicolife.textmining.machinelearning.biotml.core.nlp.BioTMLNLPSystemsEnum;

public class RESchemaCreateModelConfigurationImpl implements IRESchemaCreateModelConfiguration{
	
	
	private IRESchema reSchema;
	private BioTMLNLPSystemsEnum bioTMLNLPSystems;
	private IBioTMLFeatureGeneratorConfigurator configurator;
	private IBioTMLModelConfigurator modelConfiguration;
	private String fileModelPath;

	public RESchemaCreateModelConfigurationImpl(IRESchema reSchema,BioTMLNLPSystemsEnum bioTMLNLPSystems,IBioTMLFeatureGeneratorConfigurator configurator,
			IBioTMLModelConfigurator modelConfiguration, String fileModelPath)
	{
		this.reSchema=reSchema;
		this.bioTMLNLPSystems=bioTMLNLPSystems;
		this.configurator=configurator;
		this.modelConfiguration=modelConfiguration;
		this.fileModelPath=fileModelPath;
	}

	@Override
	public IRESchema getRESchema() {
		return reSchema;
	}

	@Override
	public BioTMLNLPSystemsEnum getBioTMLNLPSystem() {
		return bioTMLNLPSystems;
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
