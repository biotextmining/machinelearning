package com.silicolife.textmining.machinelearning.biotml.core.models.mallet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.silicolife.textmining.machinelearning.biotml.core.BioTMLConstants;
import com.silicolife.textmining.machinelearning.biotml.core.evaluation.datastrucures.BioTMLMultiEvaluationImpl;
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
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLMultiEvaluation;
import com.silicolife.textmining.machinelearning.biotml.core.mllibraries.BioTMLAlgorithm;
import com.silicolife.textmining.machinelearning.biotml.core.mllibraries.mallet.BioTMLCorpusToInstanceMallet;
import com.silicolife.textmining.machinelearning.biotml.core.mllibraries.mallet.features.CorpusWithFeatures2TokenSequence;
import com.silicolife.textmining.machinelearning.biotml.core.mllibraries.mallet.fst.MultiSegmentationEvaluator;
import com.silicolife.textmining.machinelearning.biotml.core.models.BioTMLModel;

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
import cc.mallet.types.InstanceList;

/**
 * 
 * Mallet Transducer model. This class trains a transducer model (e.g. CRF, HMM or MEMM model) and test it.
 * 
 * @since 1.0.0
 * @author Ruben Rodrigues ({@code rrodrigues@silicolife.com})
 */

public class MalletTransducerModel extends BioTMLModel implements IBioTMLModel{

	private Transducer transducerModel;
	private Pipe pipe;
	private InstanceList trainingdataset;
	private boolean isTrained;
	
	public MalletTransducerModel( 
			IBioTMLFeatureGeneratorConfigurator featureConfiguration, 
			IBioTMLModelConfigurator modelConfiguration){
		super(featureConfiguration, modelConfiguration);
		setTransducerModel(null);
		this.pipe = setupPipe();
		this.isTrained = false;
	}

	public MalletTransducerModel(Transducer model, 			
			IBioTMLFeatureGeneratorConfigurator featureConfiguration, 
			IBioTMLModelConfigurator modelConfiguration){
		super(featureConfiguration, modelConfiguration);
		setTransducerModel(model);
		this.pipe = getModel().getInputPipe();
		this.isTrained = true;
	}

	private Pipe setupPipe(){
		ArrayList<Pipe> pipe = new ArrayList<Pipe>();
		pipe.add(new CorpusWithFeatures2TokenSequence());
//		pipe.add(new Corpus2TokenSequence()); 	
//		pipe.add(new FeaturesClasses2MalletFeatures(getFeatureConfiguration()));
//		pipe.add(new PrintTokenSequenceFeatures());
		if(getModelConfiguration().getAlgorithmType().equals(BioTMLAlgorithm.malletcrf.toString()))
			pipe.add(new TokenSequence2FeatureVectorSequence(true, true));
		if(getModelConfiguration().getAlgorithmType().equals(BioTMLAlgorithm.mallethmm.toString()))
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

	private Map<String, IBioTMLEvaluation> evaluateFold(InstanceList trainingData, InstanceList testingData, String foldIDString) throws BioTMLException{
		TransducerTrainer evaluationModelTraining = null;
		if(getModelConfiguration().getAlgorithmType().equals(BioTMLAlgorithm.malletcrf.toString())){
			evaluationModelTraining = trainByThreadedLabelLikelihood(trainingData, defineCRF(trainingData), false);
		}
		if(getModelConfiguration().getAlgorithmType().equals(BioTMLAlgorithm.mallethmm.toString())){
			evaluationModelTraining = trainByLikelihood(trainingData, defineHMM(trainingData), false);
		}
		MultiSegmentationEvaluator evaluator = new MultiSegmentationEvaluator(
				new InstanceList[]{testingData},
				new String[]{foldIDString}, new String[]{BioTMLConstants.b.toString(), BioTMLConstants.i.toString()}, new String[]{BioTMLConstants.b.toString(), BioTMLConstants.i.toString()}) {
		};
		evaluator.evaluate(evaluationModelTraining);
		System.err.println("The MultiSegmentationEvaluator don't fill the confusion matrix! Evaluation for Transducers is not completed!");
//		Map<String, IBioTMLConfusionMatrix<Object>> confusion = evaluator.getConfusionMatrixBySegmentString();
//		return new BioTMLEvaluationImpl(evaluator.getOverallPrecision(), evaluator.getOverallRecall(), evaluator.getOverallF1(), foldIDString);
		return null;
	}

	private Map<String, List<IBioTMLEvaluation>> evaluateByDocumentCrossValidation(IBioTMLCorpus corpus, IBioTMLModelEvaluationConfigurator configuration) throws BioTMLException{
		Map<String, List<IBioTMLEvaluation>> multiEvaluations = new HashMap<>();
		int foldID = 1;
		Iterator<IBioTMLCrossValidationFold<IBioTMLCorpus>> itCross = new BioTMLCrossValidationCorpusIterator(corpus, configuration.getCVFoldsByDocuments(), configuration.isSuffleDataBeforeCV());
		while(itCross.hasNext()){
			IBioTMLCrossValidationFold<IBioTMLCorpus> folds = itCross.next();	        
			InstanceList trainingData = loadCorpus(folds.getTrainingDataset(), getModelConfiguration().getNumThreads());
			InstanceList testingData = loadCorpus(folds.getTestingDataset(), getModelConfiguration().getNumThreads());
			
			Map<String, IBioTMLEvaluation> evaluationByLabel = evaluateFold(trainingData, testingData, "CV By Doc Fold: " + String.valueOf(foldID));
			for(String label : evaluationByLabel.keySet()){
				if(!multiEvaluations.containsKey(label))
					multiEvaluations.put(label, new ArrayList<>());
				List<IBioTMLEvaluation> evaluations = multiEvaluations.get(label);
				evaluations.add(evaluationByLabel.get(label));
				multiEvaluations.put(label, evaluations);
			}

			foldID++;
		}
		return multiEvaluations;
	}

	private Map<String, List<IBioTMLEvaluation>> evaluateBySentenceCrossValidation(IBioTMLCorpus corpus, IBioTMLModelEvaluationConfigurator configuration) throws BioTMLException{
		Map<String, List<IBioTMLEvaluation>> multiEvaluations = new HashMap<>();
		int foldID = 1;
		InstanceList datasetToEvaluate = loadCorpus(corpus, getModelConfiguration().getNumThreads());
		Iterator<InstanceList[]> itCross = datasetToEvaluate.crossValidationIterator(configuration.getCVFoldsBySentences());
		while(itCross.hasNext()){
			InstanceList[] dataSplited = itCross.next();
			InstanceList trainingData = dataSplited[0];
			InstanceList testingData = dataSplited[1];
			
			Map<String, IBioTMLEvaluation> evaluationByLabel = evaluateFold(trainingData, testingData, "CV By Sent Fold: " + String.valueOf(foldID));
			for(String label : evaluationByLabel.keySet()){
				if(!multiEvaluations.containsKey(label))
					multiEvaluations.put(label, new ArrayList<>());
				List<IBioTMLEvaluation> evaluations = multiEvaluations.get(label);
				evaluations.add(evaluationByLabel.get(label));
				multiEvaluations.put(label, evaluations);
			}
			
			foldID++;
		}
		return multiEvaluations;
	}

	public IBioTMLMultiEvaluation evaluate(IBioTMLCorpus corpus, IBioTMLModelEvaluationConfigurator configuration) throws BioTMLException{
		if(corpus == null)
			throw new BioTMLException(21);
		
		Map<String, List<IBioTMLEvaluation>> evaluationResults = new HashMap<>();
		if(configuration.isUseCrossValidationByDocuments()){
			Map<String, List<IBioTMLEvaluation>> evaluationsDocCV = evaluateByDocumentCrossValidation(corpus, configuration);
			for(String evaluationkey : evaluationsDocCV.keySet()){
				String evaluationkeyString = "CVbyDOC:\t"+evaluationkey;
				if(!evaluationResults.containsKey(evaluationkeyString))
					evaluationResults.put(evaluationkeyString, new ArrayList<>());
				List<IBioTMLEvaluation> evaluations = evaluationResults.get(evaluationkeyString);
				evaluations.addAll(evaluationsDocCV.get(evaluationkey));
				evaluationResults.put(evaluationkeyString, evaluations);
			}
		}
		if(configuration.isUseCrossValidationBySentences()){
			Map<String, List<IBioTMLEvaluation>> evaluationsSentCV = evaluateBySentenceCrossValidation(corpus, configuration);
			for(String evaluationkey : evaluationsSentCV.keySet()){
				String evaluationkeyString = "CVbySENT:\t"+evaluationkey;
				if(!evaluationResults.containsKey(evaluationkeyString))
					evaluationResults.put(evaluationkeyString, new ArrayList<>());
				List<IBioTMLEvaluation> evaluations = evaluationResults.get(evaluationkeyString);
				evaluations.addAll(evaluationsSentCV.get(evaluationkey));
				evaluationResults.put(evaluationkeyString, evaluations);
			}
		}
		return new BioTMLMultiEvaluationImpl(evaluationResults);
	}

	public void train(IBioTMLCorpus corpus) throws BioTMLException {
		if(corpus == null)
			throw new BioTMLException(21);
		trainingdataset = loadCorpus(corpus, getModelConfiguration().getNumThreads());
		BioTMLFeaturesManager.getInstance().cleanMemoryFeaturesClass();
		// Train with Threads
		if(getModelConfiguration().getAlgorithmType().equals(BioTMLAlgorithm.malletcrf.toString())){
			trainByThreadedLabelLikelihood(trainingdataset, defineCRF(trainingdataset), true);
			isTrained = true;
		}
		else if(getModelConfiguration().getAlgorithmType().equals(BioTMLAlgorithm.mallethmm.toString())){
			trainByLikelihood(trainingdataset, defineHMM(trainingdataset), true);
			isTrained = true;
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
	
	private void setTransducerModel(Transducer model){
		this.transducerModel = model;
	}
	
	public Transducer getModel(){
		return transducerModel;
	}

	@Override
	public boolean isTrained() {
		return isTrained;
	}

	@Override
	public boolean isValid() {
		if(getModelConfiguration().getIEType().equals(BioTMLConstants.ner.toString())
				|| getModelConfiguration().getIEType().equals(BioTMLConstants.re.toString())){
			if((getModelConfiguration().getAlgorithmType().equals(BioTMLAlgorithm.malletcrf) 
			|| getModelConfiguration().getAlgorithmType().equals(BioTMLAlgorithm.mallethmm)) && getModel() instanceof Transducer)
				return true;
		}
		return false;
	}
}
