package com.silicolife.textmining.ie.schemas;

import java.util.ArrayList;
import java.util.List;

import com.silicolife.textmining.core.interfaces.core.dataaccess.exception.ANoteException;
import com.silicolife.textmining.ie.BioTMLConverter;
import com.silicolife.textmining.ie.schemas.create.model.configuration.IRESchemaCreateModelConfiguration;
import com.silicolife.textmining.machinelearning.biotml.core.BioTMLConstants;
import com.silicolife.textmining.machinelearning.biotml.core.exception.BioTMLException;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLCorpus;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLModelConfigurator;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLMultiModel;
import com.silicolife.textmining.machinelearning.biotml.core.models.BioTMLModelConfiguratorImpl;
import com.silicolife.textmining.machinelearning.biotml.core.models.BioTMLMultiModelImpl;

public class CreateREModelFile {
	
	public CreateREModelFile()
	{
		
	}
	
	public void setModelFile(IRESchemaCreateModelConfiguration modelConfiguration) throws ANoteException, BioTMLException
	{
		BioTMLConverter converter = new BioTMLConverter(modelConfiguration.getBioTMLNLPSystem(), modelConfiguration.getRESchema());
		IBioTMLCorpus bioTMLCorpus = converter.convertToBioTMLCorpus();
		if(bioTMLCorpus!= null){
			List<IBioTMLModelConfigurator> configurations = new ArrayList<IBioTMLModelConfigurator>();
			IBioTMLModelConfigurator configuration = modelConfiguration.getBioTMLModelConfigurator();
			configurations.add(configuration );
			if(configuration.getREMethodology().containsTriggers()){
				IBioTMLModelConfigurator cluesConfiguration = new BioTMLModelConfiguratorImpl(BioTMLConstants.trigger.toString(), BioTMLConstants.ner.toString());
				cluesConfiguration.setAlgorithmType(configuration.getAlgorithmType());
				cluesConfiguration.setModelOrder(configuration.getModelOrder());
				cluesConfiguration.setNumThreads(configuration.getNumThreads());
				cluesConfiguration.setSVMParameters(configuration.getSVMParameters());
				cluesConfiguration.setUsedNLPSystem(configuration.getUsedNLPSystem());
				configurations.add(cluesConfiguration);
			}
			IBioTMLMultiModel model = new BioTMLMultiModelImpl(modelConfiguration.getBioTMLFeatureGeneratorConfigurator(), configurations);
			model.trainAndSaveFile(bioTMLCorpus, modelConfiguration.getFileModelPath());
			System.gc();
		}
	}
}
