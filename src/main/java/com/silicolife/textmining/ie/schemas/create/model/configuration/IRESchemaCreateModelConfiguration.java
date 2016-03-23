package com.silicolife.textmining.ie.schemas.create.model.configuration;

import com.silicolife.textmining.core.interfaces.process.IE.IRESchema;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLFeatureGeneratorConfigurator;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLModelConfigurator;
import com.silicolife.textmining.machinelearning.biotml.core.nlp.BioTMLNLPSystemsEnum;

public interface IRESchemaCreateModelConfiguration {
	
	public IRESchema getRESchema();
	
	public BioTMLNLPSystemsEnum getBioTMLNLPSystem();
	
	public IBioTMLFeatureGeneratorConfigurator getBioTMLFeatureGeneratorConfigurator();
	
	public IBioTMLModelConfigurator getBioTMLModelConfigurator();
	
	public String getFileModelPath();

}
