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
import com.silicolife.textmining.machinelearning.biotml.core.evaluation.BioTMLModelsCrossValidationCorpusEvaluator;
import com.silicolife.textmining.machinelearning.biotml.core.evaluation.datastrucures.BioTMLModelEvaluationConfiguratorImpl;
import com.silicolife.textmining.machinelearning.biotml.core.evaluation.datastrucures.BioTMLModelEvaluationResultsImpl;
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
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLMultiEvaluation;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLMultiModel;
import com.silicolife.textmining.machinelearning.biotml.core.mllibraries.BioTMLAlgorithm;
import com.silicolife.textmining.machinelearning.biotml.core.models.mallet.MalletClassifierModel;
import com.silicolife.textmining.machinelearning.biotml.core.models.mallet.MalletTransducerModel;
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

	private Map<IBioTMLFeatureGeneratorConfigurator, List<IBioTMLModelConfigurator>> modelFeaturesAndConfigurations;
	private List<IBioTMLModel> models;
	private boolean usedEvaluation = false;

	/**
	 * 
	 * Initializes a multi-model from a absolute path file.
	 * 
	 * @param file - Multi-model file absolute path string.
	 * @throws BioTMLException
	 */
	public BioTMLMultiModel(String file) throws BioTMLException{
		IBioTMLModelReader reader = new BioTMLModelReaderImpl();

		this.models = reader.loadModelFromZipFile(file);
		this.modelFeaturesAndConfigurations = new HashMap<>();

		for(IBioTMLModel model : models){
			if(!modelFeaturesAndConfigurations.containsKey(model.getFeatureConfiguration()))
				modelFeaturesAndConfigurations.put(model.getFeatureConfiguration(), new ArrayList<>());
			List<IBioTMLModelConfigurator> configurations = modelFeaturesAndConfigurations.get(model.getFeatureConfiguration());
			configurations.add(model.getModelConfiguration());
			modelFeaturesAndConfigurations.put(model.getFeatureConfiguration(), configurations);
		}

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
	public BioTMLMultiModel(
			IBioTMLFeatureGeneratorConfigurator featureConfiguration,  
			List<IBioTMLModelConfigurator> modelConfigurations){
		this.modelFeaturesAndConfigurations = new HashMap<>();
		this.modelFeaturesAndConfigurations.put(featureConfiguration, modelConfigurations);
	}
	
	public BioTMLMultiModel(Map<IBioTMLFeatureGeneratorConfigurator, List<IBioTMLModelConfigurator>> modelFeaturesAndConfigurations){
		this.modelFeaturesAndConfigurations = modelFeaturesAndConfigurations;
	}

	private List<IBioTMLModel> iniModels(){
		List<IBioTMLModel> modelsList = new ArrayList<IBioTMLModel>();
		for(IBioTMLFeatureGeneratorConfigurator features : getModelFeaturesAndConfiguration().keySet()){
			List<IBioTMLModelConfigurator> modelconfigs = getModelFeaturesAndConfiguration().get(features);
			for(IBioTMLModelConfigurator modelconfig :modelconfigs)
				addInitModelConfig(modelsList, features, modelconfig);
		}
		return modelsList;
	}

	private void addInitModelConfig(List<IBioTMLModel> modelsList, IBioTMLFeatureGeneratorConfigurator features, IBioTMLModelConfigurator configuration) {
		if(configuration.getAlgorithmType().equals(BioTMLAlgorithm.malletcrf) 
				|| configuration.getAlgorithmType().equals(BioTMLAlgorithm.mallethmm)){
			modelsList.add(new MalletTransducerModel(features, configuration));
		}
		if(configuration.getAlgorithmType().equals(BioTMLAlgorithm.malletsvm)
				|| configuration.getAlgorithmType().equals(BioTMLAlgorithm.malletnaivebayes)
				|| configuration.getAlgorithmType().equals(BioTMLAlgorithm.malletdecisiontree)
				|| configuration.getAlgorithmType().equals(BioTMLAlgorithm.malletmaxent)
				|| configuration.getAlgorithmType().equals(BioTMLAlgorithm.malletc45)){
			modelsList.add(new MalletClassifierModel(features, configuration));
		}
	}

	public List<String> getClassTypes() {
		List<String> classTypes = new ArrayList<String>();
		for(IBioTMLModel model : getModels())
			classTypes.add(model.getModelConfiguration().getClassType());
		
		return classTypes;
	}

	public String getIEType(){
		for(IBioTMLModel model : getModels()){
			if(model.getModelConfiguration().getAlgorithmType().equals(BioTMLConstants.re.toString()))
				return BioTMLConstants.re.toString();
		}
		return BioTMLConstants.ner.toString();
	}

	public Map<IBioTMLFeatureGeneratorConfigurator, List<IBioTMLModelConfigurator>> getModelFeaturesAndConfiguration() {
		return modelFeaturesAndConfigurations;
	}

	public Map<String, IBioTMLModelEvaluationResults> evaluate(IBioTMLCorpus corpus, IBioTMLModelEvaluationConfigurator modelEvaluationConfiguration) throws BioTMLException {
		Map<String, IBioTMLModelEvaluationResults> evaluationByTypes = new HashMap<>();
		if(modelEvaluationConfiguration.isUseMultipleModelsToEvaluate()){
			BioTMLModelsCrossValidationCorpusEvaluator cvEvaluator = new BioTMLModelsCrossValidationCorpusEvaluator(getModels(), modelEvaluationConfiguration);
			IBioTMLMultiEvaluation result = cvEvaluator.evaluate(corpus);
			Map<String, IBioTMLMultiEvaluation> evaluationResultscv = new HashMap<>();
			evaluationResultscv.put("", result);
			evaluationByTypes.put("General", new BioTMLModelEvaluationResultsImpl(evaluationResultscv));
		}else
			for(IBioTMLModel model : getModels())
				evaluationByTypes.put(model.getModelConfiguration().getClassType(), model.evaluate(corpus, modelEvaluationConfiguration));

		this.usedEvaluation = true;
		return evaluationByTypes;
	}

	public void train(IBioTMLCorpus corpus) throws BioTMLException {
		for(IBioTMLModel model : getModels()){
			model.train(corpus);
		}
	}

	public void trainAndSaveFile(IBioTMLCorpus corpus, String modelPathAndFilename) throws BioTMLException{
		IBioTMLModelWriter writer = new BioTMLModelWriterImpl(modelPathAndFilename);
		List<String> modelPaths = new ArrayList<>();
		for(IBioTMLModel model : getModels()){
			model.train(corpus);
			String modelpath = writer.saveGZModelForMultiModel(model);
			modelPaths.add(modelpath);
		}
		if(!modelPaths.isEmpty()){
			writer.writeZIPModelFilesSaved(modelPaths, generateReadmeFile(corpus, getModels()));
		}
	}

	public IBioTMLCorpus annotate(IBioTMLCorpus corpusToAnotate) throws BioTMLException {
		IBioTMLAnnotator annotator = new BioTMLMalletAnnotatorImpl(corpusToAnotate);
		return annotator.generateAnnotatedBioTMCorpus(getModels(),getModels().get(0).getModelConfiguration().getNumThreads());
	}

	public List<IBioTMLModel> getModels() {
		if(models == null){
			models = iniModels();
		}
		return models;
	}

	public File generateReadmeFile(IBioTMLCorpus corpus, List<IBioTMLModel> models){
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
			IBioTMLModelEvaluationConfigurator evaluationConfiguration = new BioTMLModelEvaluationConfiguratorImpl();
			String evaluation = (this.usedEvaluation) ? " * Evaluation scores: " + "\n\n"+foldTypesString(evaluationConfiguration) + processEvaluationbyTypes(new HashMap<>()): "";
			bw.write("\n###################### README ########################\n\n"+
					"The multi-model file was trained with:\n\n"+
					" * Corpus name: " + corpus.toString()+"\n\n"+
					" * IE Process Type: " + getIEType()+"\n\n"+
					" * Annotation Types:\n\t\t\t\t\t" + getClassTypes().toString().substring(1,getClassTypes().toString().length()-1).replace(", ", "\n\t\t\t\t\t")+"\n\n");
			for(IBioTMLModel model:models){
				bw.write(
					" * Used NLP System: " + model.getModelConfiguration().getUsedNLPSystem() + "\n\n"+
					" * Machine learning algorithm used: " + model.getModelConfiguration().getAlgorithmType() + "\n\n"+
					" * Features used:\n\t\t\t\t\t" + model.getFeatureConfiguration().getFeaturesUIDs().toString().substring(1, model.getFeatureConfiguration().getFeaturesUIDs().toString().length()-1).replace(", ", "\n\t\t\t\t\t")+"\n\n"+
					evaluation);
			}
			bw.close();
		} catch ( IOException e) {
			e.printStackTrace();
		}
		return readme;
	}

	private String foldTypesString(IBioTMLModelEvaluationConfigurator evaluationConfiguration){
		String res = new String();
		if(evaluationConfiguration.isUseCrossValidationByDocuments())
			res = res + "\tNumber of folds used in corpus documents for cross-validation is " + String.valueOf(evaluationConfiguration.getCVFoldsByDocuments())+"\n";

		if(evaluationConfiguration.isUseCrossValidationBySentences())
			res = res + "\tNumber of folds used in corpus sentences for cross-validation is " + String.valueOf(evaluationConfiguration.getCVFoldsBySentences())+"\n";

		return res;
	}

	private String processEvaluationbyTypes(Map<String, IBioTMLModelEvaluationResults> evaluationByTypes){
		String result = new String();
		for(String key : evaluationByTypes.keySet()){
			result = result + "\n\n\tModel Type: " + key +"\n";
			IBioTMLModelEvaluationResults evalresuts = evaluationByTypes.get(key);
			result = result + evalresuts.printResults();
		}
		return result;
	}

}
