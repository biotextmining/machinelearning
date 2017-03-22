package com.silicolife.textmining.machinelearning.biotml.core.mllibraries.mallet.multithread;

import java.util.Set;

import com.silicolife.textmining.machinelearning.biotml.core.evaluation.BioTMLEvaluationImpl;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLEvaluation;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLModelConfigurator;
import com.silicolife.textmining.machinelearning.biotml.core.mllibraries.BioTMLAlgorithms;
import com.silicolife.textmining.machinelearning.biotml.core.mllibraries.libsvm.mallet.SVMClassifierTrainer;

import cc.mallet.classify.ClassifierTrainer;
import cc.mallet.classify.Trial;
import cc.mallet.types.InstanceList;

/**
 * 
 * Mallet cross-validation fold classifier processed in one thread.
 * 
 * @since 1.0.0
 * @author Ruben Rodrigues ({@code rrodrigues@silicolife.com})
 */

public class MalletClassifierFoldProcessedInThread implements Runnable{

	private InstanceList trainingData;
	private InstanceList testingData;
	private Set<IBioTMLEvaluation> multiEvaluations;
	private IBioTMLModelConfigurator modelConfiguration;
	private String foldDescription;

	/**
	 * 
	 * Initializes the cross-validation fold classifier for multi-threading.
	 * 
	 * @param trainingData - Mallet instance list of training data.
	 * @param testingData - Mallet instance list of testing data.
	 * @param multiEvaluations - Set of fold evaluations to be populated in multi-threading process.
	 * @param iBioTMLModelConfigurator - Mallet algorithm type.
	 */
	public MalletClassifierFoldProcessedInThread(InstanceList trainingData, InstanceList testingData, 
			Set<IBioTMLEvaluation> multiEvaluations, IBioTMLModelConfigurator iBioTMLModelConfigurator, String foldDescription){
		this.trainingData = trainingData;
		this.testingData = testingData;
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

	/**
	 * 
	 * Method to get the set of cross-validation evaluations.
	 * 
	 * @return Set of evaluations ({@link IBioTMLEvaluation}).
	 */
	public Set<IBioTMLEvaluation> getMultiEvaluations() {
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
	
	@SuppressWarnings("rawtypes")
	private ClassifierTrainer train(InstanceList dataToTrain){
		ClassifierTrainer modelTraining=null;
		if(getModelConfiguration().getAlgorithmType().equals(BioTMLAlgorithms.malletsvm.toString())){
			modelTraining = new SVMClassifierTrainer(getModelConfiguration().getSVMParameters());
			modelTraining.train(dataToTrain);
		}
		return modelTraining;
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
		@SuppressWarnings("rawtypes")
		ClassifierTrainer evaluationModelTraining = train(getTrainingData());
		if(evaluationModelTraining !=null){
			Trial trial = new Trial(evaluationModelTraining.getClassifier(), getTestingData());
			int size = getTestingData().getTargetAlphabet().size();
			double precision = 0.0;
			double recall = 0.0;
			double f1 = 0.0;
			for(int i=0; i<size; i++){
				precision += trial.getPrecision(i);
				recall += trial.getRecall(i);
				f1 += trial.getF1(i);
			}
			addToMultiEvaluations(new BioTMLEvaluationImpl((float)precision/(float)size, (float)recall/(float)size, (float)f1/(float)size, getFoldDescription()));
		}
	}

}
