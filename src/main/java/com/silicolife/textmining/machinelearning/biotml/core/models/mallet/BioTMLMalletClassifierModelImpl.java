package com.silicolife.textmining.machinelearning.biotml.core.models.mallet;

import java.util.ArrayList;
import java.util.List;

import com.silicolife.textmining.machinelearning.biotml.core.BioTMLConstants;
import com.silicolife.textmining.machinelearning.biotml.core.exception.BioTMLException;
import com.silicolife.textmining.machinelearning.biotml.core.features.BioTMLFeaturesManager;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLCorpus;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLEvaluation;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLFeatureGeneratorConfigurator;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLModel;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLModelConfigurator;
import com.silicolife.textmining.machinelearning.biotml.core.mllibraries.BioTMLAlgorithm;
import com.silicolife.textmining.machinelearning.biotml.core.mllibraries.libsvm.mallet.SVMClassifierTrainer;
import com.silicolife.textmining.machinelearning.biotml.core.mllibraries.mallet.FeatureVectorSequence2FeatureVectorsFixed;
import com.silicolife.textmining.machinelearning.biotml.core.mllibraries.mallet.features.CorpusWithFeatures2TokenSequence;
import com.silicolife.textmining.machinelearning.biotml.core.mllibraries.mallet.multithread.MalletClassifierFoldProcessedInThread;

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
import cc.mallet.types.InstanceList;

/**
 * 
 * Mallet Classifier model. This class trains a model and test it.
 * 
 * @since 1.0.0
 * @author Ruben Rodrigues ({@code rrodrigues@silicolife.com})
 */

public class BioTMLMalletClassifierModelImpl extends BioTMLMalletModel implements IBioTMLModel{

	private static final long serialVersionUID = 1L;
	private Classifier classifierModel;
	private Pipe pipe;
	private InstanceList trainingdataset;
	private boolean isTrained;
	
	public BioTMLMalletClassifierModelImpl(
			IBioTMLFeatureGeneratorConfigurator featureConfiguration, 
			IBioTMLModelConfigurator modelConfiguration){
		super(featureConfiguration, modelConfiguration);
		setClassifierModel(null);
		this.pipe = setupPipe();
		this.isTrained = false;
	}

	public BioTMLMalletClassifierModelImpl(Classifier classifier,		
			IBioTMLFeatureGeneratorConfigurator featureConfiguration, 
			IBioTMLModelConfigurator modelConfiguration) throws BioTMLException{
		super(featureConfiguration, modelConfiguration);
		setClassifierModel(classifier);
		this.pipe = getModel().getInstancePipe();
		this.isTrained = true;
	}
	
	@Override
	public boolean isTrained() {
		return isTrained;
	}
	
	@Override
	public Classifier getModel(){
		return this.classifierModel;
	}
	
	@Override
	public boolean isValid() {
		if(getModelConfiguration().getIEType().equals(BioTMLConstants.ner.toString())
				|| getModelConfiguration().getIEType().equals(BioTMLConstants.re.toString())){
			if((getModelConfiguration().getAlgorithmType().equals(BioTMLAlgorithm.malletsvm)
				|| getModelConfiguration().getAlgorithmType().equals(BioTMLAlgorithm.malletnaivebayes)
				|| getModelConfiguration().getAlgorithmType().equals(BioTMLAlgorithm.malletdecisiontree)
				|| getModelConfiguration().getAlgorithmType().equals(BioTMLAlgorithm.malletmaxent)
				|| getModelConfiguration().getAlgorithmType().equals(BioTMLAlgorithm.malletc45)) 
					&& (getModel() == null || getModel() instanceof Classifier))
				return true;
		}
		return false;
	}
	
	@Override
	public void train(IBioTMLCorpus corpus) throws BioTMLException {
		if(corpus == null)
			throw new BioTMLException(21);
		if(!isValid())
			throw new BioTMLException("MalletClassifierModel: The model configuration inputed is not valid!\n" + getModelConfiguration());
		trainingdataset = loadCorpus(corpus, getModelConfiguration().getNumThreads());
		trainingdataset = loadFeaturesSelection(trainingdataset, getFeatureConfiguration().getFeatureSelectionConfiguration());
		BioTMLFeaturesManager.getInstance().cleanMemoryFeaturesClass();
		// Train with Threads
		train(trainingdataset, true);
		isTrained = true;
	}
	
	@Override
	public void cleanAlphabetMemory(){
		if(trainingdataset != null){
			for(Alphabet alphabet :trainingdataset.getAlphabets()){
				alphabet.cleanAlphabetFromMemory();
			}
		}
	}
	
	@Override
	public void cleanPipeMemory(){
		if(pipe != null){
			pipe.cleanPipeFromMemory();
			Pipe inputPipe = getModel().getInstancePipe();
			if(inputPipe!=null){
				inputPipe.cleanPipeFromMemory();
			}
		}
	}

	@Override
	protected Runnable getFoldProcessedThread(InstanceList trainingData, InstanceList testingData,
			List<IBioTMLEvaluation> multiEvaluations, String foldDescription) {
		return new  MalletClassifierFoldProcessedInThread(trainingData, testingData, getPipe(), multiEvaluations, getModelConfiguration(), foldDescription);
	}

	protected Pipe setupPipe(){
		ArrayList<Pipe> pipe = new ArrayList<Pipe>();
		pipe.add(new CorpusWithFeatures2TokenSequence());
//		pipe.add(new PrintTokenSequenceFeatures());
		pipe.add(new TokenSequence2FeatureVectorSequence(true, true));
		pipe.add(new FeatureVectorSequence2FeatureVectorsFixed());
		return new SerialPipes(pipe);
	}
	
	private void setClassifierModel(Classifier model){
		this.classifierModel=model;
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

}
