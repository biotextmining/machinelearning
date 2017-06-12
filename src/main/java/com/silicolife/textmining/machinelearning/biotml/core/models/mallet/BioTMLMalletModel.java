package com.silicolife.textmining.machinelearning.biotml.core.models.mallet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.silicolife.textmining.machinelearning.biotml.core.evaluation.datastrucures.BioTMLMultiEvaluationImpl;
import com.silicolife.textmining.machinelearning.biotml.core.evaluation.utils.BioTMLCrossValidationCorpusIterator;
import com.silicolife.textmining.machinelearning.biotml.core.exception.BioTMLException;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLCorpus;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLCorpusToInstanceMallet;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLCrossValidationFold;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLEvaluation;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLFeatureGeneratorConfigurator;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLFeatureSelectionConfiguration;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLModelConfigurator;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLModelEvaluationConfigurator;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLMultiEvaluation;
import com.silicolife.textmining.machinelearning.biotml.core.mllibraries.BioTMLFeatureSelectionAlgorithm;
import com.silicolife.textmining.machinelearning.biotml.core.mllibraries.mallet.BioTMLCorpusToInstanceMallet;
import com.silicolife.textmining.machinelearning.biotml.core.models.BioTMLModel;

import cc.mallet.pipe.Pipe;
import cc.mallet.types.FeatureCounts;
import cc.mallet.types.FeatureSelection;
import cc.mallet.types.InfoGain;
import cc.mallet.types.InstanceList;

public abstract class BioTMLMalletModel extends BioTMLModel{

	private static final long serialVersionUID = 1L;
	private Pipe pipe;
	
	public BioTMLMalletModel(IBioTMLFeatureGeneratorConfigurator featureConfiguration, IBioTMLModelConfigurator modelConfiguration){
		super(featureConfiguration, modelConfiguration);
	}
	
	public IBioTMLMultiEvaluation evaluate(IBioTMLCorpus corpus, IBioTMLModelEvaluationConfigurator configuration) throws BioTMLException{
		if(corpus == null)
			throw new BioTMLException(21);
		if(!isValid())
			throw new BioTMLException("The model configuration inputed is not valid!\n" + getModelConfiguration());
		
		Map<String, List<IBioTMLEvaluation>> evaluationResults = new HashMap<>();
		if(configuration.isUseCrossValidationByDocuments()){
			List<IBioTMLEvaluation> evaluationsDocCV = evaluateByDocumentCrossValidation(corpus, configuration);
			evaluationResults.put("CVbyDOC", evaluationsDocCV);
		}
		if(configuration.isUseCrossValidationBySentences()){
			List<IBioTMLEvaluation> evaluationsSentCV = evaluateBySentenceCrossValidation(corpus, configuration);
			evaluationResults.put("CVbySENT", evaluationsSentCV);
		}
		return new BioTMLMultiEvaluationImpl(evaluationResults);
	}
	
	protected abstract Pipe setupPipe();
	
	protected abstract Runnable getFoldProcessedThread(InstanceList trainingData , InstanceList testingData,  
			List<IBioTMLEvaluation> multiEvaluations, String foldDescription);
	
	protected Pipe getPipe(){
		if(pipe != null)
			if(pipe.getDataAlphabet() != null)
				return pipe;

		pipe = setupPipe();
		return pipe;
	}
	
	protected InstanceList loadCorpus(IBioTMLCorpus corpusToLoad, int numThreads) throws BioTMLException{
		IBioTMLCorpusToInstanceMallet malletCorpus = new BioTMLCorpusToInstanceMallet(corpusToLoad, getModelConfiguration());
		return malletCorpus.exportToMalletFeatures(getPipe(), numThreads, getFeatureConfiguration());
	}
	
	protected InstanceList loadFeaturesSelection(InstanceList intances, IBioTMLFeatureSelectionConfiguration featSelectConfig){
		if(featSelectConfig.getFeatureSelectionAlgorithm().equals(BioTMLFeatureSelectionAlgorithm.none))
			return intances;
		if(featSelectConfig.getFeatureSelectionAlgorithm().equals(BioTMLFeatureSelectionAlgorithm.infogain)){
			InfoGain infogain = new InfoGain(intances);
			FeatureSelection selectedFeatures = new FeatureSelection(infogain, featSelectConfig.getSelectedFeaturesSize());
			intances.setFeatureSelection(selectedFeatures);
		}else if(featSelectConfig.getFeatureSelectionAlgorithm().equals(BioTMLFeatureSelectionAlgorithm.featurecounts)){
			FeatureCounts featureCounts = new FeatureCounts(intances);
			FeatureSelection selectedFeatures = new FeatureSelection(featureCounts, featSelectConfig.getSelectedFeaturesSize());
			intances.setFeatureSelection(selectedFeatures);
		}
		return intances;
	}
	
	private List<IBioTMLEvaluation> evaluateByDocumentCrossValidation(IBioTMLCorpus corpus, IBioTMLModelEvaluationConfigurator configuration) throws BioTMLException{
		List<IBioTMLEvaluation> multiEvaluations = new ArrayList<>();
		ExecutorService executor = Executors.newFixedThreadPool(getModelConfiguration().getNumThreads());
		Iterator<IBioTMLCrossValidationFold<IBioTMLCorpus>> itCross = new BioTMLCrossValidationCorpusIterator(corpus, 
				configuration.getCVFoldsByDocuments(), configuration.isSuffleDataBeforeCV());
		int foldCount = 1;
		while(itCross.hasNext()){
			IBioTMLCrossValidationFold<IBioTMLCorpus> folds = itCross.next();	        
			InstanceList trainingData = loadCorpus(folds.getTrainingDataset(), getModelConfiguration().getNumThreads());
			trainingData = loadFeaturesSelection(trainingData, getFeatureConfiguration().getFeatureSelectionConfiguration());
			InstanceList testingData = loadCorpus(folds.getTestingDataset(), getModelConfiguration().getNumThreads());
			testingData = loadFeaturesSelection(testingData, getFeatureConfiguration().getFeatureSelectionConfiguration());
			executor.execute(getFoldProcessedThread(trainingData, testingData, multiEvaluations, "Cross Validation by documents fold "+foldCount));
			foldCount++;
		}
		executor.shutdown();
		try {
			executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		} catch (InterruptedException exc) {
			throw new BioTMLException(21, exc);
		}
		return multiEvaluations;
	}

	private List<IBioTMLEvaluation> evaluateBySentenceCrossValidation(IBioTMLCorpus corpus, IBioTMLModelEvaluationConfigurator configuration) throws BioTMLException{
		List<IBioTMLEvaluation> multiEvaluations = new ArrayList<>();
		ExecutorService executor = Executors.newFixedThreadPool(getModelConfiguration().getNumThreads());
		InstanceList datasetToEvaluate = loadCorpus(corpus, getModelConfiguration().getNumThreads());
		Iterator<InstanceList[]> itCross = datasetToEvaluate.crossValidationIterator(configuration.getCVFoldsBySentences());
		int foldCount = 1;
		while(itCross.hasNext()){
			InstanceList[] dataSplited = itCross.next();
			InstanceList trainingData = dataSplited[0];
			trainingData = loadFeaturesSelection(trainingData, getFeatureConfiguration().getFeatureSelectionConfiguration());
			InstanceList testingData = dataSplited[1];
			testingData = loadFeaturesSelection(testingData, getFeatureConfiguration().getFeatureSelectionConfiguration());
			executor.execute(getFoldProcessedThread(trainingData, testingData, multiEvaluations, "Cross Validation by sentences fold "+foldCount));
			foldCount++;
		}
		executor.shutdown();
		try {
			executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		} catch (InterruptedException exc) {
			throw new BioTMLException(21, exc);
		}
		return multiEvaluations;
	}
	
}
