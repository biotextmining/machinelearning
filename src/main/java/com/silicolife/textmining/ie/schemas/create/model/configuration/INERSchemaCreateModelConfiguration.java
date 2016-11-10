package com.silicolife.textmining.ie.schemas.create.model.configuration;

import java.util.List;

import com.silicolife.textmining.core.interfaces.process.IE.INERSchema;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLFeatureGeneratorConfigurator;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLModelConfigurator;

public interface INERSchemaCreateModelConfiguration {

	public INERSchema getNERSChema();
	
	public String getBioTMLNLPSystemsEnum();
	
	public IBioTMLFeatureGeneratorConfigurator getFeaturesSet();
	
	public List<IBioTMLModelConfigurator> getModelConfigurations();
	
	public String getFileModelPath();
}
