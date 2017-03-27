package com.silicolife.textmining.machinelearning.biotml.core.models.mallet;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPOutputStream;

import com.silicolife.textmining.machinelearning.biotml.core.evaluation.BioTMLModelEvaluationResultsImpl;
import com.silicolife.textmining.machinelearning.biotml.core.evaluation.BioTMLMultiEvaluationImpl;
import com.silicolife.textmining.machinelearning.biotml.core.evaluation.utils.BioTMLCrossValidationCorpusIterator;
import com.silicolife.textmining.machinelearning.biotml.core.exception.BioTMLException;
import com.silicolife.textmining.machinelearning.biotml.core.features.BioTMLFeaturesManager;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLCorpus;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLCorpusToInstanceMallet;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLEvaluation;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLFeatureGeneratorConfigurator;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLFeatureSelectionConfiguration;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLModel;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLModelConfigurator;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLModelEvaluationConfigurator;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLModelEvaluationResults;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLMultiEvaluation;
import com.silicolife.textmining.machinelearning.biotml.core.mllibraries.BioTMLAlgorithm;
import com.silicolife.textmining.machinelearning.biotml.core.mllibraries.BioTMLFeatureSelectionAlgorithm;
import com.silicolife.textmining.machinelearning.biotml.core.mllibraries.libsvm.mallet.SVMClassifierTrainer;
import com.silicolife.textmining.machinelearning.biotml.core.mllibraries.mallet.BioTMLCorpusToInstanceMallet;
import com.silicolife.textmining.machinelearning.biotml.core.mllibraries.mallet.FeatureVectorSequence2FeatureVectorsFixed;
import com.silicolife.textmining.machinelearning.biotml.core.mllibraries.mallet.features.CorpusWithFeatures2TokenSequence;
import com.silicolife.textmining.machinelearning.biotml.core.mllibraries.mallet.multithread.MalletClassifierFoldProcessedInThread;
import com.silicolife.textmining.machinelearning.biotml.core.models.BioTMLModel;

import cc.mallet.classify.C45Trainer;
import cc.mallet.classify.Classifier;
import cc.mallet.classify.ClassifierTrainer;
import cc.mallet.classify.DecisionTreeTrainer;
import cc.mallet.classify.MaxEntTrainer;
import cc.mallet.classify.NaiveBayesTrainer;
import cc.mallet.pipe.Pipe;
import cc.mallet.pipe.SerialPipes;
import cc.mallet.pipe.TokenSequence2FeatureVectorSequence;
import cc.mallet.types.Alphabet;
import cc.mallet.types.FeatureCounts;
import cc.mallet.types.FeatureSelection;
import cc.mallet.types.InfoGain;
import cc.mallet.types.InstanceList;

/**
 * 
 * Mallet Classifier model. This class trains a model and test it.
 * 
 * @since 1.0.0
 * @author Ruben Rodrigues ({@code rrodrigues@silicolife.com})
 */

public class MalletClassifierModel extends BioTMLModel implements IBioTMLModel{

	private IBioTMLCorpus corpus;
	private Classifier classifierModel;
	private Pipe pipe;
	private InstanceList trainingdataset;
	
	public MalletClassifierModel(
			IBioTMLFeatureGeneratorConfigurator featureConfiguration, 
			IBioTMLModelConfigurator modelConfiguration){
		super(featureConfiguration, modelConfiguration);
	}
	
	public MalletClassifierModel(	IBioTMLCorpus corpus, 
			IBioTMLFeatureGeneratorConfigurator featureConfiguration, 
			IBioTMLModelConfigurator modelConfiguration){
		super(featureConfiguration, modelConfiguration);
		setClassifierModel(null);
		this.corpus = corpus;
		this.pipe = setupPipe();
	}

	public MalletClassifierModel(	IBioTMLCorpus corpus, 
			IBioTMLFeatureGeneratorConfigurator featureConfiguration, 
			IBioTMLModelConfigurator modelConfiguration,
			IBioTMLModelEvaluationConfigurator modelEvaluationConfiguration){
		super(featureConfiguration, modelConfiguration, modelEvaluationConfiguration);
		setClassifierModel(null);
		this.corpus = corpus;
		this.pipe = setupPipe();
	}

	public MalletClassifierModel(	Classifier classifier,		
			IBioTMLFeatureGeneratorConfigurator featureConfiguration, 
			IBioTMLModelConfigurator modelConfiguration) throws BioTMLException{
		super(featureConfiguration, modelConfiguration);
		setClassifierModel(classifier);
		this.corpus = null;
		this.pipe = getModel().getInstancePipe();
	}

	public IBioTMLCorpus getCorpus() throws BioTMLException{
		if( this.corpus != null){
			return this.corpus;
		}
		throw new BioTMLException(20);
	}

	private Pipe setupPipe(){
		ArrayList<Pipe> pipe = new ArrayList<Pipe>();
		pipe.add(new CorpusWithFeatures2TokenSequence());
//		pipe.add(new PrintTokenSequenceFeatures());
		pipe.add(new TokenSequence2FeatureVectorSequence(true, true));
		pipe.add(new FeatureVectorSequence2FeatureVectorsFixed());
		return new SerialPipes(pipe);
	}

	private Pipe getPipe(){
		if(pipe != null){
			if(pipe.getDataAlphabet() != null){
				return pipe;
			}
		}
		if(getModel() != null){
			Pipe pipeModel = getModel().getInstancePipe();
			if(pipeModel.getDataAlphabet() != null){
				pipe =  pipeModel;
			}else{
				pipe = setupPipe();
			}

		}else{
			pipe = setupPipe();
		}
		return pipe;
	}

	private InstanceList loadCorpus(IBioTMLCorpus corpusToLoad, int numThreads) throws BioTMLException{
		IBioTMLCorpusToInstanceMallet malletCorpus = new BioTMLCorpusToInstanceMallet(corpusToLoad, getModelConfiguration());
		return malletCorpus.exportToMalletFeatures(getPipe(), numThreads, getFeatureConfiguration());
	}
	
	private InstanceList loadFeaturesSelection(InstanceList intances, IBioTMLFeatureSelectionConfiguration featSelectConfig){
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
	
	@SuppressWarnings("rawtypes")
	private ClassifierTrainer train(InstanceList dataToTrain, boolean saveModel){
		
		ClassifierTrainer modelTraining=null;
		
		if(getModelConfiguration().getAlgorithmType().equals(BioTMLAlgorithm.malletsvm))
			modelTraining = new SVMClassifierTrainer(getModelConfiguration().getSVMParameters());
		else if(getModelConfiguration().getAlgorithmType().equals(BioTMLAlgorithm.malletnaivebayes))
			modelTraining = new NaiveBayesTrainer(getPipe());
		else if(getModelConfiguration().getAlgorithmType().equals(BioTMLAlgorithm.malletdecisiontree))
			modelTraining = new DecisionTreeTrainer();
		else if(getModelConfiguration().getAlgorithmType().equals(BioTMLAlgorithm.malletmaxent))
			modelTraining = new MaxEntTrainer();
		else if(getModelConfiguration().getAlgorithmType().equals(BioTMLAlgorithm.malletc45))
			modelTraining = new C45Trainer();
		
		modelTraining.train(dataToTrain);
		if(saveModel)
			setClassifierModel(modelTraining.getClassifier());
		
		return modelTraining;
	}

	private IBioTMLMultiEvaluation evaluateByDocumentCrossValidation() throws BioTMLException{
		Set<IBioTMLEvaluation> multiEvaluations = new HashSet<IBioTMLEvaluation>();
		ExecutorService executor = Executors.newFixedThreadPool(getModelConfiguration().getNumThreads());
		Iterator<IBioTMLCorpus[]> itCross = new BioTMLCrossValidationCorpusIterator(getCorpus(), getModelEvaluationConfiguration().getCVFoldsByDocuments());
		int foldCount = 1;
		while(itCross.hasNext()){
			IBioTMLCorpus[] folds = itCross.next();	        
			InstanceList trainingData = loadCorpus(folds[0], getModelConfiguration().getNumThreads());
			trainingData = loadFeaturesSelection(trainingData, getFeatureConfiguration().getFeatureSelectionConfiguration());
			InstanceList testingData = loadCorpus(folds[1], getModelConfiguration().getNumThreads());
			testingData = loadFeaturesSelection(testingData, getFeatureConfiguration().getFeatureSelectionConfiguration());
//			multiEvaluations.add(evaluateFold(trainingData, testingData));
			executor.execute(new MalletClassifierFoldProcessedInThread(trainingData, testingData, getPipe(), 
					multiEvaluations, getModelConfiguration(), "Cross Validation by documents fold "+foldCount));
			foldCount++;
		}
		executor.shutdown();
		try {
			executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		} catch (InterruptedException exc) {
			throw new BioTMLException(21, exc);
		}
		return new BioTMLMultiEvaluationImpl(multiEvaluations);
	}

	private IBioTMLMultiEvaluation evaluateBySentenceCrossValidation() throws BioTMLException{
		Set<IBioTMLEvaluation> multiEvaluations = new HashSet<IBioTMLEvaluation>();
		ExecutorService executor = Executors.newFixedThreadPool(getModelConfiguration().getNumThreads());
		InstanceList datasetToEvaluate = loadCorpus(getCorpus(), getModelConfiguration().getNumThreads());
		Iterator<InstanceList[]> itCross = datasetToEvaluate.crossValidationIterator(getModelEvaluationConfiguration().getCVFoldsBySentences());
		int foldCount = 1;
		while(itCross.hasNext()){
			InstanceList[] dataSplited = itCross.next();
			InstanceList trainingData = dataSplited[0];
			trainingData = loadFeaturesSelection(trainingData, getFeatureConfiguration().getFeatureSelectionConfiguration());
			InstanceList testingData = dataSplited[1];
			testingData = loadFeaturesSelection(testingData, getFeatureConfiguration().getFeatureSelectionConfiguration());
			executor.execute(new MalletClassifierFoldProcessedInThread(trainingData, testingData, getPipe(),
					multiEvaluations, getModelConfiguration(), "Cross Validation by sentences fold "+foldCount));
//			multiEvaluations.add(evaluateFold(trainingData, testingData));
			foldCount++;
		}
		executor.shutdown();
		try {
			executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		} catch (InterruptedException exc) {
			throw new BioTMLException(21, exc);
		}
		return new BioTMLMultiEvaluationImpl(multiEvaluations);
	}

	public IBioTMLModelEvaluationResults evaluate() throws BioTMLException{
		Map<String, IBioTMLMultiEvaluation> evaluationResults = new HashMap<>();
		if(getModelEvaluationConfiguration().isUseCrossValidationByDocuments()){
			evaluationResults.put("CVbyDOC", evaluateByDocumentCrossValidation());
		}
		if(getModelEvaluationConfiguration().isUseCrossValidationBySentences()){
			evaluationResults.put("CVbySENT", evaluateBySentenceCrossValidation());
		}
		return new BioTMLModelEvaluationResultsImpl(evaluationResults);
	}

	private void setClassifierModel(Classifier model){
		this.classifierModel=model;
	}

	public void train() throws BioTMLException {
		trainingdataset = loadCorpus(getCorpus(), getModelConfiguration().getNumThreads());
		trainingdataset = loadFeaturesSelection(trainingdataset, getFeatureConfiguration().getFeatureSelectionConfiguration());
		BioTMLFeaturesManager.getInstance().cleanMemoryFeaturesClass();
		// Train with Threads
		train(trainingdataset, true);
	}
	
	public void cleanAlphabetMemory(){
		if(trainingdataset != null){
			for(Alphabet alphabet :trainingdataset.getAlphabets()){
				alphabet.cleanAlphabetFromMemory();
			}
		}
	}
	
	public void cleanPipeMemory(){
		if(pipe != null){
			pipe.cleanPipeFromMemory();
			Pipe inputPipe = getModel().getInstancePipe();
			if(inputPipe!=null){
				inputPipe.cleanPipeFromMemory();
			}
		}
	}

	public Classifier getModel(){
		return this.classifierModel;
	}

	/**
	 * Write the model into a file.
	 * @param file The file to store the model.
	 * @throws BioTMLException Problem writing the output file.
	 */
	public void writeToFile(String file) throws BioTMLException {
		try {
			ObjectOutputStream oos = new ObjectOutputStream(new GZIPOutputStream(new FileOutputStream(file)));
			List<Object> modelresult = new ArrayList<Object>();
			modelresult.add(getFeatureConfiguration());
			modelresult.add(getModelConfiguration());
			modelresult.add(getModel());
			oos.writeObject(modelresult);
			oos.close();
		} catch (IOException exc) {
			throw new BioTMLException(18,exc);
		}
	}

}
