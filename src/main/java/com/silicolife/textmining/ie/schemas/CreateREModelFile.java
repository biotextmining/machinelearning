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
import com.silicolife.textmining.machinelearning.biotml.core.models.BioTMLModelConfigurator;
import com.silicolife.textmining.machinelearning.biotml.core.models.BioTMLMultiModel;
import com.silicolife.textmining.machinelearning.biotml.core.models.BioTMLREModelTypes;

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
			if(configuration.getClassType().equals(BioTMLREModelTypes.entityclue.toString())
					|| configuration.getClassType().equals(BioTMLREModelTypes.entityclueonlyannotations.toString())){
				IBioTMLModelConfigurator cluesConfiguration = new BioTMLModelConfigurator(BioTMLConstants.trigger.toString(), BioTMLConstants.ner.toString());
				cluesConfiguration.setAlgorithmType(configuration.getAlgorithmType());
				cluesConfiguration.setModelOrder(configuration.getModelOrder());
				cluesConfiguration.setNumThreads(configuration.getNumThreads());
				cluesConfiguration.setSVMParameters(configuration.getSVMParameters());
				cluesConfiguration.setUsedNLPSystem(configuration.getUsedNLPSystem());
				configurations.add(cluesConfiguration);
			}
			IBioTMLMultiModel model = new BioTMLMultiModel(bioTMLCorpus, modelConfiguration.getBioTMLFeatureGeneratorConfigurator(), configurations);
			model.trainAndSaveFile(modelConfiguration.getFileModelPath());
			System.gc();
		}
	}
}
