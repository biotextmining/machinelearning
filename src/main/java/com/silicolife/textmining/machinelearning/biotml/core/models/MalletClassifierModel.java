package com.silicolife.textmining.machinelearning.biotml.core.models;

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

import com.silicolife.textmining.machinelearning.biotml.core.evaluation.BioTMLEvaluation;
import com.silicolife.textmining.machinelearning.biotml.core.evaluation.BioTMLModelEvaluationResults;
import com.silicolife.textmining.machinelearning.biotml.core.evaluation.BioTMLMultiEvaluation;
import com.silicolife.textmining.machinelearning.biotml.core.evaluation.utils.BioTMLCrossValidationCorpusIterator;
import com.silicolife.textmining.machinelearning.biotml.core.exception.BioTMLException;
import com.silicolife.textmining.machinelearning.biotml.core.features.BioTMLFeaturesManager;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLCorpus;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLCorpusToInstanceMallet;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLEvaluation;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLFeatureGeneratorConfigurator;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLModel;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLModelConfigurator;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLModelEvaluationConfigurator;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLModelEvaluationResults;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLModelMatrixToPrint;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLMultiEvaluation;
import com.silicolife.textmining.machinelearning.biotml.core.mllibraries.BioTMLAlgorithms;
import com.silicolife.textmining.machinelearning.biotml.core.mllibraries.libsvm.mallet.SVMClassifierTrainer;
import com.silicolife.textmining.machinelearning.biotml.core.mllibraries.mallet.BioTMLCorpusToInstanceMallet;
import com.silicolife.textmining.machinelearning.biotml.core.mllibraries.mallet.FeatureVectorSequence2FeatureVectorsFixed;
import com.silicolife.textmining.machinelearning.biotml.core.mllibraries.mallet.features.CorpusWithFeatures2TokenSequence;
import com.silicolife.textmining.machinelearning.biotml.core.mllibraries.mallet.multithread.MalletClassifierFoldProcessedInThread;

import cc.mallet.classify.Classifier;
import cc.mallet.classify.ClassifierTrainer;
import cc.mallet.classify.Trial;
import cc.mallet.pipe.Pipe;
import cc.mallet.pipe.SerialPipes;
import cc.mallet.pipe.TokenSequence2FeatureVectorSequence;
import cc.mallet.types.Alphabet;
import cc.mallet.types.FeatureVector;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import cc.mallet.types.Label;

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
	private IBioTMLModelMatrixToPrint matrix;
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
		this.matrix = null;
	}

	public MalletClassifierModel(	IBioTMLCorpus corpus, 
			IBioTMLFeatureGeneratorConfigurator featureConfiguration, 
			IBioTMLModelConfigurator modelConfiguration,
			IBioTMLModelEvaluationConfigurator modelEvaluationConfiguration){
		super(featureConfiguration, modelConfiguration, modelEvaluationConfiguration);
		setClassifierModel(null);
		this.corpus = corpus;
		this.pipe = setupPipe();
		this.matrix = null;
	}

	public MalletClassifierModel(	Classifier classifier,		
			IBioTMLFeatureGeneratorConfigurator featureConfiguration, 
			IBioTMLModelConfigurator modelConfiguration) throws BioTMLException{
		super(featureConfiguration, modelConfiguration);
		setClassifierModel(classifier);
		this.corpus = null;
		this.pipe = getModel().getInstancePipe();
		this.matrix = null;
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

	@SuppressWarnings("unused")
	private InstanceList loadCorpus(IBioTMLCorpus corpusToLoad) throws BioTMLException{
		IBioTMLCorpusToInstanceMallet malletCorpus = new BioTMLCorpusToInstanceMallet(corpusToLoad, getModelConfiguration().getClassType(), getModelConfiguration().getIEType());
		return malletCorpus.exportToMallet(getPipe());
	}

	private InstanceList loadCorpus(IBioTMLCorpus corpusToLoad, int numThreads) throws BioTMLException{
		IBioTMLCorpusToInstanceMallet malletCorpus = new BioTMLCorpusToInstanceMallet(corpusToLoad, getModelConfiguration().getClassType(), getModelConfiguration().getIEType());
		return malletCorpus.exportToMalletFeatures(getPipe(), numThreads, getFeatureConfiguration());
	}

	private void loadMatrix(InstanceList dataset) throws BioTMLException{
		this.matrix =  new ModelMatrixToPrint(getFeatureConfiguration().getFeaturesUIDs());
		
		Iterator<Instance> intData = dataset.iterator();
		while(intData.hasNext()){
			Instance instanceData = intData.next();
			FeatureVector row = (FeatureVector) instanceData.getData();
			Label target = (Label) instanceData.getTarget();
			String rowToString = row.toString()+ "LABEL=" + target.toString() + "\n";
			getMatrix().addMatrixRow(rowToString.split("\n"));
		}
	}
	
	@SuppressWarnings("rawtypes")
	private ClassifierTrainer train(InstanceList dataToTrain, boolean saveModel){
		ClassifierTrainer modelTraining=null;
		if(getModelConfiguration().getAlgorithmType().equals(BioTMLAlgorithms.malletsvm.toString())){
			modelTraining = new SVMClassifierTrainer(getModelConfiguration().getSVMParameters());
		}
		modelTraining.train(dataToTrain);
		if(saveModel){
			setClassifierModel(modelTraining.getClassifier());
		}
		return modelTraining;
	}

	@SuppressWarnings({ "rawtypes", "unused" })
	private IBioTMLEvaluation evaluateFold(InstanceList trainingData, InstanceList testingData){
		ClassifierTrainer evaluationModelTraining = train(trainingData, false);
		Trial trial = new Trial(evaluationModelTraining.getClassifier(), testingData);
		int size = testingData.getTargetAlphabet().size();
		double precision = 0.0;
		double recall = 0.0;
		double f1 = 0.0;
		for(int i=0; i<size; i++){
			precision += trial.getPrecision(i);
			recall += trial.getRecall(i);
			f1 += trial.getF1(i);
		}
		return new BioTMLEvaluation((float)precision/(float)size, (float)recall/(float)size, (float)f1/(float)size);
	}

	private IBioTMLEvaluation evaluateByDocumentCrossValidation() throws BioTMLException{
		Set<IBioTMLEvaluation> multiEvaluations = new HashSet<IBioTMLEvaluation>();
		ExecutorService executor = Executors.newFixedThreadPool(getModelConfiguration().getNumThreads());
		Iterator<IBioTMLCorpus[]> itCross = new BioTMLCrossValidationCorpusIterator(getCorpus(), getModelEvaluationConfiguration().getCVFoldsByDocuments());
		while(itCross.hasNext()){
			IBioTMLCorpus[] folds = itCross.next();	        
			InstanceList trainingData = loadCorpus(folds[0], getModelConfiguration().getNumThreads());
			InstanceList testingData = loadCorpus(folds[1], getModelConfiguration().getNumThreads());
//			multiEvaluations.add(evaluateFold(trainingData, testingData));
			executor.execute(new MalletClassifierFoldProcessedInThread(trainingData, testingData, multiEvaluations, getModelConfiguration()));
		}
		executor.shutdown();
		try {
			executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		} catch (InterruptedException exc) {
			throw new BioTMLException(21, exc);
		}
		IBioTMLMultiEvaluation modelScores = new BioTMLMultiEvaluation(multiEvaluations);
		return new BioTMLEvaluation(modelScores.getMeanPrecision(), modelScores.getMeanRecall(), modelScores.getMeanFscore());
	}

	private IBioTMLEvaluation evaluateBySentenceCrossValidation() throws BioTMLException{
		Set<IBioTMLEvaluation> multiEvaluations = new HashSet<IBioTMLEvaluation>();
		ExecutorService executor = Executors.newFixedThreadPool(getModelConfiguration().getNumThreads());
		InstanceList datasetToEvaluate = loadCorpus(getCorpus(), getModelConfiguration().getNumThreads());
		Iterator<InstanceList[]> itCross = datasetToEvaluate.crossValidationIterator(getModelEvaluationConfiguration().getCVFoldsBySentences());
		while(itCross.hasNext()){
			InstanceList[] dataSplited = itCross.next();
			InstanceList trainingData = dataSplited[0];
			InstanceList testingData = dataSplited[1];
			executor.execute(new MalletClassifierFoldProcessedInThread(trainingData, testingData, multiEvaluations, getModelConfiguration()));
//			multiEvaluations.add(evaluateFold(trainingData, testingData));
		}
		executor.shutdown();
		try {
			executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		} catch (InterruptedException exc) {
			throw new BioTMLException(21, exc);
		}
		IBioTMLMultiEvaluation modelScores = new BioTMLMultiEvaluation(multiEvaluations);
		return new BioTMLEvaluation(modelScores.getMeanPrecision(), modelScores.getMeanRecall(), modelScores.getMeanFscore());
	}

	public IBioTMLModelEvaluationResults evaluate() throws BioTMLException{
		Map<String, IBioTMLEvaluation> evaluationResults = new HashMap<String, IBioTMLEvaluation>();
		if(getModelEvaluationConfiguration().isUseCrossValidationByDocuments()){
			evaluationResults.put("CVbyDOC", evaluateByDocumentCrossValidation());
		}
		if(getModelEvaluationConfiguration().isUseCrossValidationBySentences()){
			evaluationResults.put("CVbySENT", evaluateBySentenceCrossValidation());
		}
		return new BioTMLModelEvaluationResults(evaluationResults);
	}

	private void setClassifierModel(Classifier model){
		this.classifierModel=model;
	}

	public void train() throws BioTMLException {
		trainingdataset = loadCorpus(getCorpus(), getModelConfiguration().getNumThreads());
		//dataset = reprocessInstances(dataset);
		BioTMLFeaturesManager.getInstance().cleanMemoryFeaturesClass();
		loadMatrix(trainingdataset);
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

	public IBioTMLModelMatrixToPrint getMatrix() throws BioTMLException {
		if(matrix == null){
			throw new BioTMLException(22);
		}
		return matrix;
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
