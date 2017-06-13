package com.silicolife.textmining.ie.schemas;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.silicolife.textmining.core.interfaces.core.dataaccess.exception.ANoteException;
import com.silicolife.textmining.ie.BioTMLConverter;
import com.silicolife.textmining.ie.schemas.create.model.configuration.INERSchemaCreateModelConfiguration;
import com.silicolife.textmining.machinelearning.biotml.core.exception.BioTMLException;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLCorpus;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLFeatureGeneratorConfigurator;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLModelConfigurator;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLModelWriter;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLMultiModel;
import com.silicolife.textmining.machinelearning.biotml.core.models.BioTMLMultiModelImpl;
import com.silicolife.textmining.machinelearning.biotml.writer.BioTMLModelWriterImpl;

public class CreateNERModelFile {
	
	public CreateNERModelFile()
	{
		
	}
	
	public void createModel(INERSchemaCreateModelConfiguration configuration) throws ANoteException, BioTMLException
	{
		BioTMLConverter converter = new BioTMLConverter(configuration.getBioTMLNLPSystemsEnum(), configuration.getNERSChema());
		IBioTMLCorpus bioTMLCorpus = converter.convertToBioTMLCorpus();
		if(bioTMLCorpus!= null){
			Map<IBioTMLFeatureGeneratorConfigurator, List<IBioTMLModelConfigurator>> configurationMap = new HashMap<>();
			IBioTMLFeatureGeneratorConfigurator confis = configuration.getFeaturesSet();
			List<IBioTMLModelConfigurator> configs = configuration.getModelConfigurations();
			configurationMap.put(confis, configs);
			IBioTMLMultiModel model = new BioTMLMultiModelImpl(configurationMap);
			model.train(bioTMLCorpus);
			IBioTMLModelWriter writer = new BioTMLModelWriterImpl(configuration.getFileModelPath());
			writer.writeMultiModel(model);
		}
		System.gc();
	}
	

}
