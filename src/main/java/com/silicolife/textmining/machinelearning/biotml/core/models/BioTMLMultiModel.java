package com.silicolife.textmining.machinelearning.biotml.core.models;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.silicolife.textmining.machinelearning.biotml.core.BioTMLConstants;
import com.silicolife.textmining.machinelearning.biotml.core.annotator.BioTMLMalletAnnotatorImpl;
import com.silicolife.textmining.machinelearning.biotml.core.evaluation.BioTMLModelEvaluationConfiguratorImpl;
import com.silicolife.textmining.machinelearning.biotml.core.exception.BioTMLException;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLAnnotator;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLCorpus;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLFeatureGeneratorConfigurator;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLModel;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLModelConfigurator;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLModelEvaluationConfigurator;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLModelEvaluationResults;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLModelReader;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLModelWriter;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLMultiModel;
import com.silicolife.textmining.machinelearning.biotml.core.mllibraries.BioTMLAlgorithms;
import com.silicolife.textmining.machinelearning.biotml.reader.BioTMLModelReaderImpl;
import com.silicolife.textmining.machinelearning.biotml.writer.BioTMLModelWriterImpl;

/**
 * 
 * Multi Model class that could contain CRF, MEMM, HMM, SVM, etc models.
 * 
 * @since 1.0.0
 * @author Ruben Rodrigues ({@code rrodrigues@silicolife.com})
 */

public class BioTMLMultiModel implements IBioTMLMultiModel{
	
	private IBioTMLCorpus corpus;
	private IBioTMLFeatureGeneratorConfigurator featureConfiguration;
	private List<IBioTMLModelConfigurator> modelConfigurations;
	private IBioTMLModelEvaluationConfigurator modelEvaluationConfiguration;
	private List<IBioTMLModel> models;
	private boolean usedEvaluation = false;
	private HashMap<String, IBioTMLModelEvaluationResults> evaluationByTypes;

	/**
	 * 
	 * Initializes a multi-model from a absolute path file.
	 * 
	 * @param file - Multi-model file absolute path string.
	 * @throws BioTMLException
	 */
	public BioTMLMultiModel(String file) throws BioTMLException{
		this.corpus = null;
		IBioTMLModelReader reader = new BioTMLModelReaderImpl();
		List<IBioTMLModelConfigurator> configurations = new ArrayList<>();
		this.models = reader.loadModelFromZipFile(file);
		this.featureConfiguration = models.get(0).getFeatureConfiguration();
		for(IBioTMLModel model : models){
			configurations.add(model.getModelConfiguration());
		}
		this.modelConfigurations = configurations;
		this.modelEvaluationConfiguration = new BioTMLModelEvaluationConfiguratorImpl();
		this.evaluationByTypes = new HashMap<String, IBioTMLModelEvaluationResults>();
	}

	/**
	 * 
	 * Initializes a multi-model using a corpus ({@link IBioTMLCorpus}),
	 * features configuration ({@link IBioTMLFeatureGeneratorConfigurator}) 
	 * and model configurations ({@link IBioTMLModelConfigurator}) that will be used to train the model.
	 * 
	 * @param corpus {@link IBioTMLCorpus} to train the model.
	 * @param featureConfiguration {@link IBioTMLFeatureGeneratorConfigurator} to generate the model training features. 
	 * @param modelConfigurations {@link IBioTMLModelConfigurator} model configurations.
	 */
	public BioTMLMultiModel(IBioTMLCorpus corpus,
							IBioTMLFeatureGeneratorConfigurator featureConfiguration,  
							List<IBioTMLModelConfigurator> modelConfigurations){
		this.corpus = corpus;
		this.featureConfiguration = featureConfiguration;
		this.modelConfigurations = modelConfigurations;
		this.modelEvaluationConfiguration = new BioTMLModelEvaluationConfiguratorImpl();
		this.evaluationByTypes = new HashMap<String, IBioTMLModelEvaluationResults>();
	}
	
	/**
	 * 
	 * Initializes a multi-model using a corpus ({@link IBioTMLCorpus}),
	 * features configuration ({@link IBioTMLFeatureGeneratorConfigurator}) ,
	 * model configurations ({@link IBioTMLModelConfigurator}) that will be used to train the model
	 * and model evaluation settings ({@link IBioTMLModelEvaluationConfigurator}) to evaluate the features generation.
	 * 
	 * @param corpus {@link IBioTMLCorpus} to train the model.
	 * @param featureConfiguration {@link IBioTMLFeatureGeneratorConfigurator} to generate the model training features. 
	 * @param modelConfigurations {@link IBioTMLModelConfigurator} model configurations.
	 * @param modelEvaluationConfiguration {@link IBioTMLModelEvaluationConfigurator} to evaluate the model generated with the initialized features.
	 */
	public BioTMLMultiModel(IBioTMLCorpus corpus,
							IBioTMLFeatureGeneratorConfigurator featureConfiguration,  
							List<IBioTMLModelConfigurator> modelConfigurations, 
							IBioTMLModelEvaluationConfigurator modelEvaluationConfiguration){
		this.corpus = corpus;
		this.featureConfiguration = featureConfiguration;
		this.modelConfigurations = modelConfigurations;
		this.modelEvaluationConfiguration = modelEvaluationConfiguration;
		this.evaluationByTypes = new HashMap<String, IBioTMLModelEvaluationResults>();
	}
	
	private List<IBioTMLModel> iniModels(){
		List<IBioTMLModel> modelsList = new ArrayList<IBioTMLModel>();
		for( IBioTMLModelConfigurator configuration : getModelConfigurations()){
			if(configuration.getAlgorithmType().equals(BioTMLAlgorithms.malletcrf.toString()) 
			|| configuration.getAlgorithmType().equals(BioTMLAlgorithms.mallethmm.toString())){
				modelsList.add(new MalletTransducerModel(getCorpus(), getFeatureConfiguration(), configuration, getModelEvaluationConfiguration()));
			}
			if(configuration.getAlgorithmType().equals(BioTMLAlgorithms.malletsvm.toString())){
				modelsList.add(new MalletClassifierModel(getCorpus(), getFeatureConfiguration(), configuration, getModelEvaluationConfiguration()));
			}
		}
		return modelsList;
	}
	
	private IBioTMLCorpus getCorpus(){
		return corpus;
	}

	public List<String> getClassTypes() {
		List<String> classTypes = new ArrayList<String>();
		for(IBioTMLModelConfigurator modelConfiguration : getModelConfigurations()){
			classTypes.add(modelConfiguration.getClassType());
		}
		return classTypes;
	}
	
	public String getIEType(){
		for(IBioTMLModelConfigurator configurations : getModelConfigurations()){
			if(configurations.getAlgorithmType().equals(BioTMLConstants.re.toString())){
				return BioTMLConstants.re.toString();
			}
		}
		return BioTMLConstants.ner.toString();
	}

	public IBioTMLFeatureGeneratorConfigurator getFeatureConfiguration() {
		return featureConfiguration;
	}

	public List<IBioTMLModelConfigurator> getModelConfigurations() {
		return modelConfigurations;
	}

	public IBioTMLModelEvaluationConfigurator getModelEvaluationConfiguration() {
		return modelEvaluationConfiguration;
	}

	public Map<String, IBioTMLModelEvaluationResults> evaluate()
			throws BioTMLException {
		for(IBioTMLModel model : getModels()){
			evaluationByTypes.put(model.getModelConfiguration().getClassType(), model.evaluate());
		}
		this.usedEvaluation = true;
		return evaluationByTypes;
	}

	public void train() throws BioTMLException {
		for(IBioTMLModel model : getModels()){
			model.train();
		}
	}
	
	public void trainAndSaveFile(String modelPathAndFilename) throws BioTMLException{
		IBioTMLModelWriter writer = new BioTMLModelWriterImpl(modelPathAndFilename);
		List<String> modelPaths = new ArrayList<>();
		for( IBioTMLModelConfigurator configuration : getModelConfigurations()){
			if(configuration.getAlgorithmType().equals(BioTMLAlgorithms.malletcrf.toString()) 
			|| configuration.getAlgorithmType().equals(BioTMLAlgorithms.mallethmm.toString())){
				IBioTMLModel model = new MalletTransducerModel(getCorpus(), getFeatureConfiguration(), configuration, getModelEvaluationConfiguration());
				model.train();
				String modelpath = writer.saveGZModelForMultiModel(model);
				modelPaths.add(modelpath);
				model = null;
				System.gc();
			}
			if(configuration.getAlgorithmType().equals(BioTMLAlgorithms.malletsvm.toString())){
				IBioTMLModel model = new MalletClassifierModel(getCorpus(), getFeatureConfiguration(), configuration, getModelEvaluationConfiguration());
				model.train();
				String modelpath = writer.saveGZModelForMultiModel(model);
				modelPaths.add(modelpath);
				model = null;
				System.gc();
			}
		}
		if(!modelPaths.isEmpty()){
			writer.writeZIPModelFilesSaved(modelPaths, generateReadmeFile());
		}
	}

	public IBioTMLCorpus annotate(IBioTMLCorpus corpusToAnotate) throws BioTMLException {
		IBioTMLAnnotator annotator = new BioTMLMalletAnnotatorImpl(corpusToAnotate);
		return annotator.generateAnnotatedBioTMCorpus(getModels(),getModelConfigurations().get(0).getNumThreads());
	}

	public List<IBioTMLModel> getModels() {
		if(models == null){
			models = iniModels();
		}
		return models;
	}
	
	public File generateReadmeFile(){
		File readme = new File("README");
		try {
			readme.createNewFile();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(readme);
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
			String evaluation = (this.usedEvaluation) ? " * Evaluation scores: " + "\n\n"+foldTypesString() + processEvaluationbyTypes(): "";
			bw.write("\n###################### README ########################\n\n"+
			"The multi-model file was trained with:\n\n"+
			" * Corpus name: " + getCorpus().toString()+"\n\n"+
			" * IE Process Type: " + getIEType()+"\n\n"+
			" * Used NLP System: " + getModelConfigurations().get(0).getUsedNLPSystem() + "\n\n"+
			" * Annotation Types:\n\t\t\t\t\t" + getClassTypes().toString().substring(1,getClassTypes().toString().length()-1).replace(", ", "\n\t\t\t\t\t")+"\n\n"+
			" * Machine learning algorithm used: " + getModelConfigurations().get(0).getAlgorithmType() + "\n\n"+
			" * Features used:\n\t\t\t\t\t" + getFeatureConfiguration().getFeaturesUIDs().toString().substring(1,getFeatureConfiguration().getFeaturesUIDs().toString().length()-1).replace(", ", "\n\t\t\t\t\t")+"\n\n"+
			evaluation);
			bw.close();
		} catch ( IOException e) {
			e.printStackTrace();
		}
		return readme;
	}
	
	private String foldTypesString(){
		String res = new String();
		if(getModelEvaluationConfiguration().isUseCrossValidationByDocuments()){
			res = res + "\tNumber of folds used in corpus documents for cross-validation is " + String.valueOf(getModelEvaluationConfiguration().getCVFoldsByDocuments())+"\n";
		}
		if(getModelEvaluationConfiguration().isUseCrossValidationBySentences()){
			res = res + "\tNumber of folds used in corpus sentences for cross-validation is " + String.valueOf(getModelEvaluationConfiguration().getCVFoldsBySentences())+"\n";
		}
		return res;
	}
	
	private String processEvaluationbyTypes(){
		String result = new String();
		for(String key : evaluationByTypes.keySet()){
			result = result + "\n\n\tModel Type: " + key +"\n";
			IBioTMLModelEvaluationResults evalresuts = evaluationByTypes.get(key);
			result = result + evalresuts.printResults();
		}
		return result;
	}

}
