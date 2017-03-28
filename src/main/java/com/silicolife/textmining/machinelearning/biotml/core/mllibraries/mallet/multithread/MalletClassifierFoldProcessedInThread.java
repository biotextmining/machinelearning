package com.silicolife.textmining.machinelearning.biotml.core.mllibraries.mallet.multithread;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.silicolife.textmining.machinelearning.biotml.core.BioTMLConstants;
import com.silicolife.textmining.machinelearning.biotml.core.evaluation.BioTMLEvaluationImpl;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLConfusionMatrix;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLEvaluation;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLModelConfigurator;
import com.silicolife.textmining.machinelearning.biotml.core.mllibraries.BioTMLAlgorithm;
import com.silicolife.textmining.machinelearning.biotml.core.mllibraries.libsvm.mallet.SVMClassifierTrainer;
import com.silicolife.textmining.machinelearning.biotml.core.mllibraries.mallet.evaluation.TrialBioTMLExtended;

import cc.mallet.classify.C45Trainer;
import cc.mallet.classify.ClassifierTrainer;
import cc.mallet.classify.DecisionTreeTrainer;
import cc.mallet.classify.MaxEntTrainer;
import cc.mallet.classify.NaiveBayesTrainer;
import cc.mallet.pipe.Pipe;
import cc.mallet.types.Instance;
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
	private Pipe pipe;
	private Map<String, List<IBioTMLEvaluation>> multiEvaluations;
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
	public MalletClassifierFoldProcessedInThread(InstanceList trainingData, InstanceList testingData, Pipe pipe,
			Map<String, List<IBioTMLEvaluation>> multiEvaluations, IBioTMLModelConfigurator iBioTMLModelConfigurator, String foldDescription){
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
	 * @return Set of evaluations ({@link IBioTMLEvaluation}).
	 */
	public Map<String, List<IBioTMLEvaluation>> getMultiEvaluations() {
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
		
		return modelTraining;
	}
	
	private synchronized void addToMultiEvaluations(String classificationLabel, IBioTMLEvaluation evaluation){
		if(!getMultiEvaluations().containsKey(classificationLabel))
			getMultiEvaluations().put(classificationLabel, new ArrayList<IBioTMLEvaluation>());
		List<IBioTMLEvaluation> evaluations = getMultiEvaluations().get(classificationLabel);
		evaluations.add(evaluation);
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
			TrialBioTMLExtended trial = new TrialBioTMLExtended(evaluationModelTraining.getClassifier(), getTestingData());
			int size = getTestingData().getTargetAlphabet().size();

			for(int i=0; i<size; i++){
				Object label = getTestingData().getTargetAlphabet().lookupObject(i);
				IBioTMLConfusionMatrix<Instance> confusionMatrix = trial.getConfusionMatrix(i);
				addToMultiEvaluations(label.toString(), new BioTMLEvaluationImpl(confusionMatrix, getFoldDescription()));
			}
		}
	}

}
