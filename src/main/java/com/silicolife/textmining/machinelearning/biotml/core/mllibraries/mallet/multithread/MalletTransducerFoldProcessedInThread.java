package com.silicolife.textmining.machinelearning.biotml.core.mllibraries.mallet.multithread;

import java.util.List;

import com.silicolife.textmining.machinelearning.biotml.core.BioTMLConstants;
import com.silicolife.textmining.machinelearning.biotml.core.evaluation.datastrucures.BioTMLEvaluationImpl;
import com.silicolife.textmining.machinelearning.biotml.core.exception.BioTMLException;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLEvaluation;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLModelConfigurator;
import com.silicolife.textmining.machinelearning.biotml.core.mllibraries.BioTMLAlgorithm;
import com.silicolife.textmining.machinelearning.biotml.core.mllibraries.mallet.evaluation.MultiSegmentationEvaluatorBioTMLExtended;

import cc.mallet.fst.CRF;
import cc.mallet.fst.CRFTrainerByThreadedLabelLikelihood;
import cc.mallet.fst.HMM;
import cc.mallet.fst.HMMTrainerByLikelihood;
import cc.mallet.fst.Transducer;
import cc.mallet.fst.TransducerTrainer;
import cc.mallet.pipe.Pipe;
import cc.mallet.types.InstanceList;

/**
 * 
 * Mallet cross-validation fold classifier processed in one thread.
 * 
 * @since 1.0.0
 * @author Ruben Rodrigues ({@code rrodrigues@silicolife.com})
 */

public class MalletTransducerFoldProcessedInThread implements Runnable{

	private InstanceList trainingData;
	private InstanceList testingData;
	private Pipe pipe;
	private List<IBioTMLEvaluation> multiEvaluations;
	private IBioTMLModelConfigurator modelConfiguration;
	private String foldDescription;

	/**
	 * 
	 * Initializes the cross-validation fold classifier for multi-threading.
	 * 
	 * @param trainingData - Mallet instance list of training data.
	 * @param testingData - Mallet instance list of testing data.
	 * @param multiEvaluations - List of fold evaluations to be populated in multi-threading process.
	 * @param iBioTMLModelConfigurator - Mallet algorithm type.
	 */
	public MalletTransducerFoldProcessedInThread(InstanceList trainingData, InstanceList testingData, Pipe pipe,
			List<IBioTMLEvaluation> multiEvaluations, IBioTMLModelConfigurator iBioTMLModelConfigurator, String foldDescription){
		this.trainingData = trainingData;
		this.testingData = testingData;
		this.pipe = pipe;
		this.multiEvaluations = multiEvaluations;
		this.modelConfiguration = iBioTMLModelConfigurator;
		this.foldDescription = foldDescription;
	}

	/**
	 * 
	 * Method to get the training dataset. 
	 * 
	 * @return InstanceList of training data.
	 */
	public InstanceList getTrainingData() {
		return trainingData;
	}

	/**
	 * 
	 * Method to get the testing dataset. 
	 * 
	 * @return InstanceList of testing data.
	 */
	public InstanceList getTestingData() {
		return testingData;
	}
	
	public Pipe getPipe(){
		return pipe;
	}

	/**
	 * 
	 * Method to get the set of cross-validation evaluations.
	 * 
	 * @return List of evaluations ({@link IBioTMLEvaluation}).
	 */
	public List<IBioTMLEvaluation> getMultiEvaluations() {
		return multiEvaluations;
	}
	
	/**
	 * 
	 * Method to get the model configuration used in the cross-evaluation.
	 * 
	 * @return Model configuration ({@link IBioTMLModelConfigurator}).
	 */
	public IBioTMLModelConfigurator getModelConfiguration() {
		return modelConfiguration;
	}
	
	/**
	 * 
	 * Method to get the fold description to be added into evaluation;
	 * 
	 * @return fold description string.
	 */
	public String getFoldDescription(){
		return foldDescription;
	}
	

	private synchronized void addToMultiEvaluations(IBioTMLEvaluation evaluation){
		getMultiEvaluations().add(evaluation);
	}
	
	/**
	 * 
	 * Thread safe process to evaluate the classification fold.
	 * 
	 */
	public void run() {
		TransducerTrainer evaluationModelTraining = null;
		if(getModelConfiguration().getAlgorithmType().equals(BioTMLAlgorithm.malletcrf)){
			try {
				evaluationModelTraining = trainByThreadedLabelLikelihood(trainingData, defineCRF(trainingData));
			} catch (BioTMLException e) {
				e.printStackTrace();
			}
		}
		if(getModelConfiguration().getAlgorithmType().equals(BioTMLAlgorithm.mallethmm)){
			evaluationModelTraining = trainByLikelihood(trainingData, defineHMM(trainingData));
		}
		MultiSegmentationEvaluatorBioTMLExtended evaluator = new MultiSegmentationEvaluatorBioTMLExtended(
				new InstanceList[]{testingData},
				new String[]{getFoldDescription()}, new String[]{BioTMLConstants.b.toString(), BioTMLConstants.i.toString()}, new String[]{BioTMLConstants.b.toString(), BioTMLConstants.i.toString()}) {
		};
		evaluator.evaluate(evaluationModelTraining);
		addToMultiEvaluations(new BioTMLEvaluationImpl(evaluator.getConfusionMatrix(), getFoldDescription()));
	}
	
	private int[] getModelOrders(){
		int order = getModelConfiguration().getTransducerConfiguration().getModelOrder() + 1;
		int[] orders = new int[order];
		for (int i = 0; i < order; i++) {
			orders[i] = i;
		}
		return orders;
	}
	
	private CRF defineCRF(InstanceList dataToProcess){
		CRF crfModel = new CRF(dataToProcess.getPipe(), (Pipe) null);
		String startStateName = crfModel.addOrderNStates( dataToProcess, getModelOrders(), null, 
				getModelConfiguration().getTransducerConfiguration().getStart(), 
				getModelConfiguration().getTransducerConfiguration().getForbiddenTransitionStates(), 
				getModelConfiguration().getTransducerConfiguration().getAllowedTransitionStates(), true); 
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
	
	private CRFTrainerByThreadedLabelLikelihood trainByThreadedLabelLikelihood(InstanceList dataToTrain, CRF model) throws BioTMLException{
		CRFTrainerByThreadedLabelLikelihood modelTraining = new CRFTrainerByThreadedLabelLikelihood(model, getModelConfiguration().getNumThreads());
		modelTraining.train(dataToTrain);
		modelTraining.shutdown();
		return modelTraining;
	}
	
	private HMMTrainerByLikelihood trainByLikelihood(InstanceList dataToTrain, HMM model){
		HMMTrainerByLikelihood modelTraining = new HMMTrainerByLikelihood(model);
		modelTraining.train(dataToTrain);
		return modelTraining;
	}

}
