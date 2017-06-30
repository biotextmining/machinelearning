package com.silicolife.textmining.machinelearning.biotml.core.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.silicolife.textmining.machinelearning.biotml.core.annotator.BioTMLMalletAnnotatorImpl;
import com.silicolife.textmining.machinelearning.biotml.core.evaluation.BioTMLModelsCrossValidationCorpusEvaluator;
import com.silicolife.textmining.machinelearning.biotml.core.exception.BioTMLException;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLAnnotator;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLCorpus;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLFeatureGeneratorConfigurator;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLModel;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLModelConfigurator;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLModelEvaluationConfigurator;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLModelReader;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLMultiEvaluation;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLMultiModel;
import com.silicolife.textmining.machinelearning.biotml.core.mllibraries.BioTMLAlgorithm;
import com.silicolife.textmining.machinelearning.biotml.core.models.mallet.BioTMLMalletClassifierModelImpl;
import com.silicolife.textmining.machinelearning.biotml.core.models.mallet.BioTMLMalletTransducerModelImpl;
import com.silicolife.textmining.machinelearning.biotml.reader.BioTMLModelReaderImpl;

/**
 * 
 * Multi Model class that could contain CRF, MEMM, HMM, SVM, etc models.
 * 
 * @since 1.0.0
 * @author Ruben Rodrigues ({@code rrodrigues@silicolife.com})
 */

public class BioTMLMultiModelImpl implements IBioTMLMultiModel{

	private static final long serialVersionUID = 1L;
	private List<IBioTMLModel> models;

	public BioTMLMultiModelImpl(Map<IBioTMLFeatureGeneratorConfigurator, List<IBioTMLModelConfigurator>> modelFeaturesAndConfigurations){
		models = new ArrayList<>();
		for(IBioTMLFeatureGeneratorConfigurator features : modelFeaturesAndConfigurations.keySet()){
			List<IBioTMLModelConfigurator> modelconfigs = modelFeaturesAndConfigurations.get(features);
			for(IBioTMLModelConfigurator modelconfig :modelconfigs){
				IBioTMLModel model = iniModel(features, modelconfig);
				if(model != null)
					models.add(model);
			}
		}
	}

	/**
	 * 
	 * Initializes a multi-model from a absolute path file.
	 * 
	 * @param file - Multi-model file absolute path string.
	 * @throws BioTMLException
	 */
	public BioTMLMultiModelImpl(String file) throws BioTMLException{
		IBioTMLModelReader reader = new BioTMLModelReaderImpl();
		this.models = reader.loadModelFromZipFile(file);
	}

	private IBioTMLModel iniModel(IBioTMLFeatureGeneratorConfigurator features, IBioTMLModelConfigurator configuration) {

		if(configuration.getAlgorithmType().equals(BioTMLAlgorithm.malletcrf) 
				|| configuration.getAlgorithmType().equals(BioTMLAlgorithm.mallethmm))
			return new BioTMLMalletTransducerModelImpl(features, configuration);

		if(configuration.getAlgorithmType().equals(BioTMLAlgorithm.malletsvm)
				|| configuration.getAlgorithmType().equals(BioTMLAlgorithm.malletnaivebayes)
				|| configuration.getAlgorithmType().equals(BioTMLAlgorithm.malletdecisiontree)
				|| configuration.getAlgorithmType().equals(BioTMLAlgorithm.malletmaxent)
				|| configuration.getAlgorithmType().equals(BioTMLAlgorithm.malletc45))
			return new BioTMLMalletClassifierModelImpl(features, configuration);

		return null;
	}

	@Override
	public List<IBioTMLModel> getModels() {
		return models;
	}

	@Override
	public void train(IBioTMLCorpus corpus) throws BioTMLException {
		for(IBioTMLModel model : getModels())
			model.train(corpus);
	}

	@Override
	public IBioTMLCorpus predict(IBioTMLCorpus corpus) throws BioTMLException{
		IBioTMLAnnotator annotator = new BioTMLMalletAnnotatorImpl(corpus);
		return annotator.generateAnnotatedBioTMCorpus(getModels(), getModels().get(0).getModelConfiguration().getNumThreads());
	}

	@Override
	public Map<String, IBioTMLMultiEvaluation> evaluate(IBioTMLCorpus corpus, IBioTMLModelEvaluationConfigurator modelEvaluationConfiguration) throws BioTMLException {
		Map<String, IBioTMLMultiEvaluation> evaluationByTypes = new HashMap<>();
		if(modelEvaluationConfiguration.isUseMultipleModelsToEvaluate()){
			BioTMLModelsCrossValidationCorpusEvaluator cvEvaluator = new BioTMLModelsCrossValidationCorpusEvaluator(getModels(), modelEvaluationConfiguration);
			IBioTMLMultiEvaluation result = cvEvaluator.evaluate(corpus);
			evaluationByTypes.put("General", result);
		}else
			for(IBioTMLModel model : getModels())
				evaluationByTypes.put(model.getModelConfiguration().getClassType(), model.evaluate(corpus, modelEvaluationConfiguration));

		return evaluationByTypes;
	}

}
