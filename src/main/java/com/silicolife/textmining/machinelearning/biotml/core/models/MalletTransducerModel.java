package com.silicolife.textmining.machinelearning.biotml.core.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import com.silicolife.textmining.machinelearning.biotml.core.BioTMLConstants;
import com.silicolife.textmining.machinelearning.biotml.core.evaluation.BioTMLEvaluationImpl;
import com.silicolife.textmining.machinelearning.biotml.core.evaluation.BioTMLModelEvaluationResultsImpl;
import com.silicolife.textmining.machinelearning.biotml.core.evaluation.BioTMLMultiEvaluationImpl;
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
import com.silicolife.textmining.machinelearning.biotml.core.mllibraries.mallet.BioTMLCorpusToInstanceMallet;
import com.silicolife.textmining.machinelearning.biotml.core.mllibraries.mallet.features.CorpusWithFeatures2TokenSequence;
import com.silicolife.textmining.machinelearning.biotml.core.mllibraries.mallet.fst.MultiSegmentationEvaluator;

import cc.mallet.fst.CRF;
import cc.mallet.fst.CRFTrainerByThreadedLabelLikelihood;
import cc.mallet.fst.HMM;
import cc.mallet.fst.HMMTrainerByLikelihood;
import cc.mallet.fst.Transducer;
import cc.mallet.fst.TransducerTrainer;
import cc.mallet.pipe.Pipe;
import cc.mallet.pipe.SerialPipes;
import cc.mallet.pipe.TokenSequence2FeatureSequence;
import cc.mallet.pipe.TokenSequence2FeatureVectorSequence;
import cc.mallet.types.Alphabet;
import cc.mallet.types.FeatureVector;
import cc.mallet.types.FeatureVectorSequence;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import cc.mallet.types.Label;
import cc.mallet.types.LabelSequence;

/**
 * 
 * Mallet Transducer model. This class trains a transducer model (e.g. CRF, HMM or MEMM model) and test it.
 * 
 * @since 1.0.0
 * @author Ruben Rodrigues ({@code rrodrigues@silicolife.com})
 */

public class MalletTransducerModel extends BioTMLModel implements IBioTMLModel{

	private IBioTMLCorpus corpus;
	private Transducer transducerModel;
	private IBioTMLModelMatrixToPrint matrix;
	private Pipe pipe;
	private InstanceList trainingdataset;
	
	public MalletTransducerModel( 
			IBioTMLFeatureGeneratorConfigurator featureConfiguration, 
			IBioTMLModelConfigurator modelConfiguration){
		super(featureConfiguration, modelConfiguration);
	}
	
	public MalletTransducerModel(	IBioTMLCorpus corpus, 
			IBioTMLFeatureGeneratorConfigurator featureConfiguration, 
			IBioTMLModelConfigurator modelConfiguration){
		super(featureConfiguration, modelConfiguration);
		setTransducerModel(null);
		this.corpus = corpus;
		this.pipe = setupPipe();
		this.matrix = null;
	}

	public MalletTransducerModel(	IBioTMLCorpus corpus,
			IBioTMLFeatureGeneratorConfigurator featureConfiguration, 
			IBioTMLModelConfigurator modelConfiguration,
			IBioTMLModelEvaluationConfigurator modelEvaluationConfiguration){
		super(featureConfiguration, modelConfiguration, modelEvaluationConfiguration);
		setTransducerModel(null);
		this.corpus = corpus;
		this.pipe = setupPipe();
		this.matrix = null;
	}

	public MalletTransducerModel(Transducer model, 			
			IBioTMLFeatureGeneratorConfigurator featureConfiguration, 
			IBioTMLModelConfigurator modelConfiguration){
		super(featureConfiguration, modelConfiguration);
		setTransducerModel(model);
		this.corpus = null;
		this.pipe = getModel().getInputPipe();
		this.matrix = null;
	}

	public IBioTMLCorpus getCorpus() throws BioTMLException{
		if( this.corpus != null){
			return this.corpus;
		}
		throw new BioTMLException(21);
	}

	private Pipe setupPipe(){
		ArrayList<Pipe> pipe = new ArrayList<Pipe>();
		pipe.add(new CorpusWithFeatures2TokenSequence());
//		pipe.add(new Corpus2TokenSequence()); 	
//		pipe.add(new FeaturesClasses2MalletFeatures(getFeatureConfiguration()));
//		pipe.add(new PrintTokenSequenceFeatures());
		if(getModelConfiguration().getAlgorithmType().equals(BioTMLAlgorithms.malletcrf.toString()))
			pipe.add(new TokenSequence2FeatureVectorSequence(true, true));
		if(getModelConfiguration().getAlgorithmType().equals(BioTMLAlgorithms.mallethmm.toString()))
			pipe.add(new TokenSequence2FeatureSequence());
		return new SerialPipes(pipe);
	}

	private Pipe getPipe(){
		if(pipe != null){
			if(pipe.getDataAlphabet() != null){
				return pipe;
			}
		}
		if(getModel() != null){
			Pipe pipeModel = getModel().getInputPipe();
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

	private int[] getModelOrders(){
		int order = getModelConfiguration().getModelOrder() + 1;
		int[] orders = new int[order];
		for (int i = 0; i < order; i++) {
			orders[i] = i;
		}
		return orders;
	}

	private CRF defineCRF(InstanceList dataToProcess){
		CRF crfModel = new CRF(dataToProcess.getPipe(), (Pipe) null);
		String startStateName = crfModel.addOrderNStates( dataToProcess, getModelOrders(), null, BioTMLConstants.o.toString(), Pattern.compile(BioTMLConstants.o.toString()+","+BioTMLConstants.i.toString()), null, true); 
		// first param is the training data
		//second param are the orders of the CRF (investigate that for our study)
		//third param "defaults" parameter; see mallet javadoc
		//fourth param non entity target param
		//fifth param defines that a token must initialize with a B and not with I
		//last param true for a fully connected CRF

		for (int i = 0; i < crfModel.numStates(); i++) {
			crfModel.getState(i).setInitialWeight(Transducer.IMPOSSIBLE_WEIGHT);
		}
		crfModel.getState(startStateName).setInitialWeight(0.0);
		crfModel.setWeightsDimensionAsIn(dataToProcess, true);
		return crfModel;
	}
	
	private HMM defineHMM(InstanceList dataToProcess){
		HMM hmmModel = new HMM(dataToProcess.getPipe(), (Pipe) null);
/*		String startStateName = hmmModel.addOrderNStates( dataToProcess, getModelOrders(), null, "O", Pattern.compile("O,I"), null, true); 
		// first param is the training data
		//second param are the orders of the HMM (investigate that for our study)
		//third param "defaults" parameter; see mallet javadoc
		//fourth param non entity target param
		//fifth param defines that a token must initialize with a B and not with I
		//last param true for a fully connected CRF
		//hmmModel.addStatesForLabelsConnectedAsIn(dataToProcess);
		for (int i = 0; i < hmmModel.numStates(); i++) {
			hmmModel.getState(i).setInitialWeight(Transducer.IMPOSSIBLE_WEIGHT);
		}
		hmmModel.getState(startStateName).setInitialWeight(0.0);*/
		hmmModel.addFullyConnectedStatesForLabels();
		
		return hmmModel;
	}

	@SuppressWarnings("unchecked")
	private void loadMatrix(InstanceList dataset) throws BioTMLException{
		this.matrix =  new ModelMatrixToPrint(getFeatureConfiguration().getFeaturesUIDs());
		if(getModelConfiguration().getAlgorithmType().equals(BioTMLAlgorithms.malletcrf.toString())){
			Iterator<Instance> intData = dataset.iterator();
			while(intData.hasNext()){
				Instance instanceData = intData.next();
				FeatureVectorSequence data = (FeatureVectorSequence) instanceData.getData();
				LabelSequence dataTarget = (LabelSequence) instanceData.getTarget();
				Iterator<FeatureVector> dataIt = data.iterator();
				Iterator<Label> dataTargetIt = dataTarget.iterator();
				while(dataIt.hasNext() && dataTargetIt.hasNext()){
					FeatureVector row = dataIt.next();
					Label target = dataTargetIt.next();
					String rowToString = row.toString()+ "LABEL=" + target.toString() + "\n";
					getMatrix().addMatrixRow(rowToString.split("\n"));
				}
			} 
		}
	}

	private CRFTrainerByThreadedLabelLikelihood trainByThreadedLabelLikelihood(InstanceList dataToTrain, CRF model, boolean saveModel) throws BioTMLException{
		CRFTrainerByThreadedLabelLikelihood modelTraining = new CRFTrainerByThreadedLabelLikelihood(model, getModelConfiguration().getNumThreads());
		modelTraining.train(dataToTrain);
		modelTraining.shutdown();
		if(saveModel){
			setTransducerModel(model);
		}
		return modelTraining;
	}
	
	private HMMTrainerByLikelihood trainByLikelihood(InstanceList dataToTrain, HMM model, boolean saveModel){
		HMMTrainerByLikelihood modelTraining = new HMMTrainerByLikelihood(model);
		modelTraining.train(dataToTrain);
		if(saveModel){
			setTransducerModel(model);
		}
		return modelTraining;
	}

	private IBioTMLEvaluation evaluateFold(InstanceList trainingData, InstanceList testingData, String foldIDString) throws BioTMLException{
		TransducerTrainer evaluationModelTraining = null;
		if(getModelConfiguration().getAlgorithmType().equals(BioTMLAlgorithms.malletcrf.toString())){
			evaluationModelTraining = trainByThreadedLabelLikelihood(trainingData, defineCRF(trainingData), false);
		}
		if(getModelConfiguration().getAlgorithmType().equals(BioTMLAlgorithms.mallethmm.toString())){
			evaluationModelTraining = trainByLikelihood(trainingData, defineHMM(trainingData), false);
		}
		MultiSegmentationEvaluator evaluator = new MultiSegmentationEvaluator(
				new InstanceList[]{testingData},
				new String[]{foldIDString}, new String[]{BioTMLConstants.b.toString(), BioTMLConstants.i.toString()}, new String[]{BioTMLConstants.b.toString(), BioTMLConstants.i.toString()}) {
		};
		evaluator.evaluate(evaluationModelTraining);
		return new BioTMLEvaluationImpl(evaluator.getOverallPrecision(), evaluator.getOverallRecall(), evaluator.getOverallF1());
	}

	private IBioTMLEvaluation evaluateByDocumentCrossValidation() throws BioTMLException{
		Set<IBioTMLEvaluation> multiEvaluations = new HashSet<IBioTMLEvaluation>();
		int foldID = 1;
		Iterator<IBioTMLCorpus[]> itCross = new BioTMLCrossValidationCorpusIterator(getCorpus(), getModelEvaluationConfiguration().getCVFoldsByDocuments());
		while(itCross.hasNext()){
			IBioTMLCorpus[] folds = itCross.next();	        
			InstanceList trainingData = loadCorpus(folds[0], getModelConfiguration().getNumThreads());
			InstanceList testingData = loadCorpus(folds[1], getModelConfiguration().getNumThreads());
			multiEvaluations.add(evaluateFold(trainingData, testingData, "CV By Doc Fold:" + String.valueOf(foldID)));
			foldID++;
		}
		IBioTMLMultiEvaluation modelScores = new BioTMLMultiEvaluationImpl(multiEvaluations);
		return new BioTMLEvaluationImpl(modelScores.getMeanPrecision(), modelScores.getMeanRecall(), modelScores.getMeanFscore());
	}

	private IBioTMLEvaluation evaluateBySentenceCrossValidation() throws BioTMLException{
		Set<IBioTMLEvaluation> multiEvaluations = new HashSet<IBioTMLEvaluation>();
		int foldID = 1;
		InstanceList datasetToEvaluate = loadCorpus(getCorpus(), getModelConfiguration().getNumThreads());
		Iterator<InstanceList[]> itCross = datasetToEvaluate.crossValidationIterator(getModelEvaluationConfiguration().getCVFoldsBySentences());
		while(itCross.hasNext()){
			InstanceList[] dataSplited = itCross.next();
			InstanceList trainingData = dataSplited[0];
			InstanceList testingData = dataSplited[1];
			multiEvaluations.add(evaluateFold(trainingData, testingData, "CV By Sent Fold:" + String.valueOf(foldID)));
			foldID++;
		}
		IBioTMLMultiEvaluation modelScores = new BioTMLMultiEvaluationImpl(multiEvaluations);
		return new BioTMLEvaluationImpl(modelScores.getMeanPrecision(), modelScores.getMeanRecall(), modelScores.getMeanFscore());
	}

	public IBioTMLModelEvaluationResults evaluate() throws BioTMLException{
		Map<String, IBioTMLEvaluation> evaluationResults = new HashMap<String, IBioTMLEvaluation>();
		if(getModelEvaluationConfiguration().isUseCrossValidationByDocuments()){
			evaluationResults.put("CVbyDOC", evaluateByDocumentCrossValidation());
		}
		if(getModelEvaluationConfiguration().isUseCrossValidationBySentences()){
			evaluationResults.put("CVbySENT", evaluateBySentenceCrossValidation());
		}
		return new BioTMLModelEvaluationResultsImpl(evaluationResults);
	}

	public void train() throws BioTMLException {

		trainingdataset = loadCorpus(getCorpus(), getModelConfiguration().getNumThreads());
		BioTMLFeaturesManager.getInstance().cleanMemoryFeaturesClass();
		loadMatrix(trainingdataset);
		// Train with Threads
		if(getModelConfiguration().getAlgorithmType().equals(BioTMLAlgorithms.malletcrf.toString())){
			trainByThreadedLabelLikelihood(trainingdataset, defineCRF(trainingdataset), true);
		}
		if(getModelConfiguration().getAlgorithmType().equals(BioTMLAlgorithms.mallethmm.toString())){
			trainByLikelihood(trainingdataset, defineHMM(trainingdataset), true);
		}

	}
	
	public void cleanAlphabetMemory(){
		if(trainingdataset != null){
			for(Alphabet alphabet :trainingdataset.getAlphabets()){
				alphabet.cleanAlphabetFromMemory();
			}
		}
		if(getModel() != null){
			if(getModel() instanceof CRF){
				CRF crf = (CRF) getModel();
				if(crf.getParameters() != null){
					crf.getParameters().cleanFactorsFromMemory();
				}
				if(crf.getInputAlphabet() != null){
					crf.getInputAlphabet().cleanAlphabetFromMemory();
				}
				if(crf.getOutputAlphabet() != null){
					crf.getOutputAlphabet().cleanAlphabetFromMemory();
				}
			}
			if(getModel() instanceof HMM){
				HMM hmm = (HMM) getModel();
				if(hmm.getInputAlphabet() != null){
					hmm.getInputAlphabet().cleanAlphabetFromMemory();
				}
				if(hmm.getOutputAlphabet() != null){
					hmm.getOutputAlphabet().cleanAlphabetFromMemory();
				}
			}
		}
	}
	
	public void cleanPipeMemory(){
		if(pipe != null){
			pipe.cleanPipeFromMemory();
			Pipe inputPipe = getModel().getInputPipe();
			if(inputPipe!=null){
				inputPipe.cleanPipeFromMemory();
			}
			Pipe outputPipe = getModel().getOutputPipe();
			if(outputPipe != null){
				outputPipe.cleanPipeFromMemory();
			}
		}
	}

	public IBioTMLModelMatrixToPrint getMatrix() throws BioTMLException {
		if(matrix == null){
			throw new BioTMLException(23);
		}
		return matrix;
	}
	
	private void setTransducerModel(Transducer model){
		this.transducerModel = model;
	}
	
	public Transducer getModel(){
		return transducerModel;
	}
}
