package com.silicolife.textmining.machinelearning.biotml.core.mllibraries.mallet;

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
import java.util.regex.Pattern;
import java.util.zip.GZIPOutputStream;

import com.silicolife.textmining.machinelearning.biotml.core.evaluation.datastrucures.BioTMLModelEvaluationResultsImpl;
import com.silicolife.textmining.machinelearning.biotml.core.evaluation.utils.BioTMLCrossValidationCorpusIterator;
import com.silicolife.textmining.machinelearning.biotml.core.exception.BioTMLException;
import com.silicolife.textmining.machinelearning.biotml.core.features.BioTMLFeaturesManager;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLCorpus;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLCorpusToInstanceMallet;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLCrossValidationFold;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLEvaluation;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLFeatureGeneratorConfigurator;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLModel;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLModelConfigurator;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLModelEvaluationConfigurator;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLModelEvaluationResults;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLModelMatrixToPrint;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLMultiEvaluation;
import com.silicolife.textmining.machinelearning.biotml.core.mllibraries.mallet.features.Corpus2TokenSequence;
import com.silicolife.textmining.machinelearning.biotml.core.mllibraries.mallet.features.FeaturesClasses2MalletFeatures;
import com.silicolife.textmining.machinelearning.biotml.core.mllibraries.mallet.fst.MultiSegmentationEvaluator;
import com.silicolife.textmining.machinelearning.biotml.core.models.BioTMLModel;

import cc.mallet.fst.MEMM;
import cc.mallet.fst.MEMMTrainer;
import cc.mallet.pipe.Pipe;
import cc.mallet.pipe.SerialPipes;
import cc.mallet.pipe.TokenSequence2FeatureVectorSequence;
import cc.mallet.types.InstanceList;

/**
 * 
 * Mallet MEMM model. This class trains a model and test it.
 * 
 * @since 1.0.0
 * @author Ruben Rodrigues ({@code rrodrigues@silicolife.com})
 */

public class MalletMEMMModel extends BioTMLModel implements IBioTMLModel{

	private IBioTMLCorpus corpus;
	private MEMM memmModel;
	private IBioTMLModelMatrixToPrint matrix;
	private Pipe pipe;

	public MalletMEMMModel(	IBioTMLCorpus corpus, 
			IBioTMLFeatureGeneratorConfigurator featureConfiguration, 
			IBioTMLModelConfigurator modelConfiguration){
		super( featureConfiguration, modelConfiguration);
		this.corpus = corpus;
		this.pipe = setupPipe();
		this.matrix = null;
		this.memmModel = null;
	}

	public MalletMEMMModel(MEMM model,
			IBioTMLFeatureGeneratorConfigurator featureConfiguration, 
			IBioTMLModelConfigurator modelConfiguration) throws BioTMLException{
		super(featureConfiguration, modelConfiguration);
		setMEMMModel(model);
		this.pipe = getModel().getInputPipe();
		this.corpus = null;
		this.matrix = null;
	}


	private Pipe setupPipe(){
		ArrayList<Pipe> pipe = new ArrayList<Pipe>();
		pipe.add(new Corpus2TokenSequence()); 	
		pipe.add(new FeaturesClasses2MalletFeatures(getFeatureConfiguration()));
		//pipe.add(new PrintTokenSequenceFeatures());
		pipe.add(new TokenSequence2FeatureVectorSequence(true, true));

		return new SerialPipes(pipe);
	}

	private Pipe getPipe(){
		return pipe;
	}

	private InstanceList loadCorpus(IBioTMLCorpus corpusToLoad, int numThreads) throws BioTMLException{
		IBioTMLCorpusToInstanceMallet malletCorpus = new BioTMLCorpusToInstanceMallet(corpusToLoad, getModelConfiguration());
		return malletCorpus.exportToMalletFeatures(getPipe(), numThreads, getFeatureConfiguration());
	}

	private int[] getModelOrders(){
		int order = getModelConfiguration().getModelOrder() + 1;
		int[] orders = new int[order];
		for (int i = 0; i < order; i++) {
			orders[i] = i;
		}
		return orders;
	}

	private MEMM defineMEMM(InstanceList dataToProcess){
		MEMM memmModel = new MEMM(dataToProcess.getPipe(), (Pipe) null);
		@SuppressWarnings("unused")
		String startStateName = memmModel.addOrderNStates( dataToProcess, getModelOrders(), null, "O", Pattern.compile("O,I"), null, true); 
		// first param is the training data
		//second param are the orders of the MEMM (investigate that for our study)
		//third param "defaults" parameter; see mallet javadoc
		//fourth param non entity target param
		//fifth param defines that a token must initialize with a B and not with I
		//last param true for a fully connected MEMM

//		for (int i = 0; i < memmModel.numStates(); i++) {
//			memmModel.getState(i).setInitialWeight(Transducer.IMPOSSIBLE_WEIGHT);
//		}
//		memmModel.getState(startStateName).setInitialWeight(0.0);
//		memmModel.setWeightsDimensionAsIn(dataToProcess, true);
		memmModel.addFullyConnectedStatesForLabels();
		return memmModel;
	}

	private MEMMTrainer trainByLikelihood(InstanceList dataToTrain, MEMM model, boolean saveModel){
		MEMMTrainer modelTraining = new MEMMTrainer(model);
		modelTraining.train(dataToTrain);
		if(saveModel){
			setMEMMModel(model);
		}
		return modelTraining;
	}

	private IBioTMLEvaluation evaluateFold(InstanceList trainingData, InstanceList testingData, String foldIDString){
		MEMM evaluationModel = defineMEMM(trainingData);
		MEMMTrainer evaluationModelTraining = trainByLikelihood(trainingData, evaluationModel, false);
		MultiSegmentationEvaluator evaluator = new MultiSegmentationEvaluator(
				new InstanceList[]{testingData},
				new String[]{foldIDString}, new String[]{"B", "I"}, new String[]{"B", "I"}) {
		};
		evaluator.evaluate(evaluationModelTraining);
//		return new BioTMLEvaluationImpl(evaluator.getOverallPrecision(), evaluator.getOverallRecall(), evaluator.getOverallF1(), foldIDString);
		return null;
	}

	private IBioTMLMultiEvaluation evaluateByDocumentCrossValidation(IBioTMLCorpus corpus, IBioTMLModelEvaluationConfigurator configuration) throws BioTMLException{
		Set<IBioTMLEvaluation> multiEvaluations = new HashSet<IBioTMLEvaluation>();
		int foldID = 1;
		Iterator<IBioTMLCrossValidationFold<IBioTMLCorpus>> itCross = new BioTMLCrossValidationCorpusIterator(corpus, configuration.getCVFoldsByDocuments());
		while(itCross.hasNext()){
			IBioTMLCrossValidationFold<IBioTMLCorpus> folds = itCross.next();	        
			InstanceList trainingData = loadCorpus(folds.getTrainingDataset(), getModelConfiguration().getNumThreads());
			InstanceList testingData = loadCorpus(folds.getTestingDataset(), getModelConfiguration().getNumThreads());
			multiEvaluations.add(evaluateFold(trainingData, testingData, "CV By Doc Fold:" + String.valueOf(foldID)));
			foldID++;
		}
//		return new BioTMLMultiEvaluationImpl(multiEvaluations);
		return null;
	}

	private IBioTMLMultiEvaluation evaluateBySentenceCrossValidation(IBioTMLCorpus corpus, IBioTMLModelEvaluationConfigurator configuration) throws BioTMLException{
		Set<IBioTMLEvaluation> multiEvaluations = new HashSet<IBioTMLEvaluation>();
		int foldID = 1;
		InstanceList datasetToEvaluate = loadCorpus(corpus, getModelConfiguration().getNumThreads());
		Iterator<InstanceList[]> itCross = datasetToEvaluate.crossValidationIterator(configuration.getCVFoldsBySentences());
		while(itCross.hasNext()){
			InstanceList[] dataSplited = itCross.next();
			InstanceList trainingData = dataSplited[0];
			InstanceList testingData = dataSplited[1];
			multiEvaluations.add(evaluateFold(trainingData, testingData, "CV By Sent Fold:" + String.valueOf(foldID)));
			foldID++;
		}
//		return new BioTMLMultiEvaluationImpl(multiEvaluations);
		return null;
	}

	public IBioTMLModelEvaluationResults evaluate(IBioTMLCorpus corpus, IBioTMLModelEvaluationConfigurator configuration) throws BioTMLException{
		Map<String, IBioTMLMultiEvaluation> evaluationResults = new HashMap<>();
		if(configuration.isUseCrossValidationByDocuments()){
			evaluationResults.put("CVbyDOC", evaluateByDocumentCrossValidation(corpus, configuration));
		}
		if(configuration.isUseCrossValidationBySentences()){
			evaluationResults.put("CVbySENT", evaluateBySentenceCrossValidation(corpus, configuration));
		}
		return new BioTMLModelEvaluationResultsImpl(evaluationResults);
	}

	private void setMEMMModel(MEMM memmModel){
		this.memmModel=memmModel;
	}

	public void train(IBioTMLCorpus corpus) throws BioTMLException {

		InstanceList dataset = loadCorpus(corpus, getModelConfiguration().getNumThreads());
		BioTMLFeaturesManager.getInstance().cleanMemoryFeaturesClass();
		// Define MEMM
		MEMM memmModel = defineMEMM(dataset);
		// Train with Threads
		trainByLikelihood(dataset,memmModel, true);

	}

	public IBioTMLModelMatrixToPrint getMatrix() throws BioTMLException {
		if(matrix == null){
			throw new BioTMLException("The matrix data wasn't loaded!");
		}
		return matrix;
	}
	
	public String getAlgorithmType() {
		return "MalletMEMM";
	}

	public MEMM getModel(){
		return this.memmModel;
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
			modelresult.add(getAlgorithmType());
			oos.writeObject(modelresult);
			oos.close();
		} catch (IOException ex) {
			throw new BioTMLException("There was a problem writing the model to file.");
		}
	}

	@Override
	public void cleanAlphabetMemory() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void cleanPipeMemory() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isTrained() {
		// TODO Auto-generated method stub
		return false;
	}

}
