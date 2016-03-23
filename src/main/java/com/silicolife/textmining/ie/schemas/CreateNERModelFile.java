package com.silicolife.textmining.ie.schemas;

import com.silicolife.textmining.core.interfaces.core.dataaccess.exception.ANoteException;
import com.silicolife.textmining.ie.BioTMLConverter;
import com.silicolife.textmining.ie.schemas.create.model.configuration.INERSchemaCreateModelConfiguration;
import com.silicolife.textmining.machinelearning.biotml.core.exception.BioTMLException;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLCorpus;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLMultiModel;
import com.silicolife.textmining.machinelearning.biotml.core.models.BioTMLMultiModel;

public class CreateNERModelFile {
	
	public CreateNERModelFile()
	{
		
	}
	
	public void createModel(INERSchemaCreateModelConfiguration configuration) throws ANoteException, BioTMLException
	{
		BioTMLConverter converter = new BioTMLConverter(configuration.getBioTMLNLPSystemsEnum(), configuration.getNERSChema());
		IBioTMLCorpus bioTMLCorpus = converter.convertToBioTMLCorpus();
		if(bioTMLCorpus!= null){
			IBioTMLMultiModel model = new BioTMLMultiModel(bioTMLCorpus, configuration.getFeaturesSet(), configuration.getModelConfigurations());
			model.trainAndSaveFile(configuration.getFileModelPath());
		}
		System.gc();
	}
	

}
