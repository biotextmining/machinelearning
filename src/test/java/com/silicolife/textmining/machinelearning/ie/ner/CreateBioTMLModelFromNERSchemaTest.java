package com.silicolife.textmining.machinelearning.ie.ner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import com.silicolife.textmining.DatabaseConnectionInit;
import com.silicolife.textmining.core.datastructures.init.exception.InvalidDatabaseAccess;
import com.silicolife.textmining.core.datastructures.schemas.NERSchemaImpl;
import com.silicolife.textmining.core.interfaces.core.dataaccess.exception.ANoteException;
import com.silicolife.textmining.core.interfaces.core.document.corpus.ICorpus;
import com.silicolife.textmining.core.interfaces.core.report.processes.INERProcessReport;
import com.silicolife.textmining.core.interfaces.process.IE.INERSchema;
import com.silicolife.textmining.core.interfaces.process.IR.exception.InternetConnectionProblemException;
import com.silicolife.textmining.core.interfaces.resource.dictionary.IDictionary;
import com.silicolife.textmining.ie.schemas.CreateNERModelFile;
import com.silicolife.textmining.ie.schemas.create.model.configuration.INERSchemaCreateModelConfiguration;
import com.silicolife.textmining.ie.schemas.create.model.configuration.NERSchemaCreateModelConfigurationImpl;
import com.silicolife.textmining.machinelearning.biotml.core.BioTMLConstants;
import com.silicolife.textmining.machinelearning.biotml.core.exception.BioTMLException;
import com.silicolife.textmining.machinelearning.biotml.core.features.BioTMLFeatureGeneratorConfigurator;
import com.silicolife.textmining.machinelearning.biotml.core.features.BioTMLFeaturesManager;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLFeatureGeneratorConfigurator;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLModelConfigurator;
import com.silicolife.textmining.machinelearning.biotml.core.models.BioTMLModelConfigurator;
import com.silicolife.textmining.machinelearning.biotml.core.nlp.BioTMLNLPSystemsEnum;
import com.silicolife.textmining.processes.corpora.loaders.CreateCorpusFromPublicationManagerTest;
import com.silicolife.textmining.processes.ie.ner.linnaeus.LinnaeusTest;

public class CreateBioTMLModelFromNERSchemaTest {

	@Test
	public void test() throws ANoteException, IOException, InvalidDatabaseAccess, InternetConnectionProblemException, BioTMLException {
		INERSchema nerSchema = createNERSchema();
		CreateNERModelFile biotmlModelCreator = new CreateNERModelFile();
		String outputModelFolder = "src/test/resources/BioTMLModelResult.zip";
		BioTMLNLPSystemsEnum nlpSystemSelected = BioTMLNLPSystemsEnum.clearnlp;
		IBioTMLFeatureGeneratorConfigurator mlFeaturesConfiguration = getSelectedFeatures();
		List<IBioTMLModelConfigurator> nerModels = getSubModelsConfigurations();
		INERSchemaCreateModelConfiguration creationConfiguration = 
				new NERSchemaCreateModelConfigurationImpl(nerSchema,nlpSystemSelected,mlFeaturesConfiguration,nerModels,outputModelFolder);
		System.out.println("Creating BioTML NER Model");
		biotmlModelCreator.createModel(creationConfiguration);
	}

	private INERSchema createNERSchema()
			throws InvalidDatabaseAccess, ANoteException, InternetConnectionProblemException, IOException {
		DatabaseConnectionInit.init("localhost","3306","createdatest","root","admin");
		ICorpus corpus = CreateCorpusFromPublicationManagerTest.createCorpus().getCorpus();
		IDictionary dictionary = LinnaeusTest.createDictionaryAndUpdateditWithByocycFiles();
		INERProcessReport report = LinnaeusTest.executeLinnaeus(corpus, dictionary);
		return new NERSchemaImpl(report.getNERProcess());
	}
	
	/**
	 * As this is a test, we selected the recommended default features.
	 * However a custom set of features could be selected.
	 * The possible features UIDs are in the classes present in 
	 * com.silicolife.textmining.machinelearning.biotml.core.features.modules package.
	 */
	private IBioTMLFeatureGeneratorConfigurator getSelectedFeatures() throws BioTMLException{
		BioTMLFeaturesManager featuresManager = BioTMLFeaturesManager.getInstance();
		Set<String> features = featuresManager.getRecomendedDefaultFeatures();
		return new BioTMLFeatureGeneratorConfigurator(features);
	}
	
	/**
	 * To create a BioTML Model, you must define information extraction type of the model,
	 * in this case, an named entity recognition and the annotation class.
	 * (Must have the same class name of the NER Schema!)
	 * For multiple NER annotations, you must add multiple sub model configurations.
	 * By default the CRF algorithm is used, for SVM or other algorithm usage, please set it in the BioTMLModelConfigurator
	 */
	private List<IBioTMLModelConfigurator> getSubModelsConfigurations(){
		List<IBioTMLModelConfigurator> subModelsConfiguration = new ArrayList<>();
		IBioTMLModelConfigurator enzymeSubmodel = new BioTMLModelConfigurator("Enzyme",BioTMLConstants.ner.toString());
		subModelsConfiguration.add(enzymeSubmodel);
		IBioTMLModelConfigurator geneSubmodel = new BioTMLModelConfigurator("Gene",BioTMLConstants.ner.toString());
		subModelsConfiguration.add(geneSubmodel);
		IBioTMLModelConfigurator compoundSubmodel = new BioTMLModelConfigurator("Compound",BioTMLConstants.ner.toString());
		subModelsConfiguration.add(compoundSubmodel);
		IBioTMLModelConfigurator pathwaysSubmodel = new BioTMLModelConfigurator("Pathways",BioTMLConstants.ner.toString());
		subModelsConfiguration.add(pathwaysSubmodel);
		IBioTMLModelConfigurator proteinSubmodel = new BioTMLModelConfigurator("Protein",BioTMLConstants.ner.toString());
		subModelsConfiguration.add(proteinSubmodel);
		return subModelsConfiguration;
	}

}