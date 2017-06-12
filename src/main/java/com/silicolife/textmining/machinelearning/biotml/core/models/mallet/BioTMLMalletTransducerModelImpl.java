package com.silicolife.textmining.machinelearning.biotml.core.models.mallet;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import com.silicolife.textmining.machinelearning.biotml.core.BioTMLConstants;
import com.silicolife.textmining.machinelearning.biotml.core.exception.BioTMLException;
import com.silicolife.textmining.machinelearning.biotml.core.features.BioTMLFeaturesManager;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLCorpus;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLEvaluation;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLFeatureGeneratorConfigurator;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLModel;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLModelConfigurator;
import com.silicolife.textmining.machinelearning.biotml.core.mllibraries.BioTMLAlgorithm;
import com.silicolife.textmining.machinelearning.biotml.core.mllibraries.mallet.features.CorpusWithFeatures2TokenSequence;
import com.silicolife.textmining.machinelearning.biotml.core.mllibraries.mallet.multithread.MalletTransducerFoldProcessedInThread;

import cc.mallet.fst.CRF;
import cc.mallet.fst.CRFTrainerByThreadedLabelLikelihood;
import cc.mallet.fst.HMM;
import cc.mallet.fst.HMMTrainerByLikelihood;
import cc.mallet.fst.Transducer;
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

public class BioTMLMalletTransducerModelImpl extends BioTMLMalletModel implements IBioTMLModel{

	private static final long serialVersionUID = 1L;
	private Transducer transducerModel;
	private Pipe pipe;
	private InstanceList trainingdataset;
	private boolean isTrained;
	
	public BioTMLMalletTransducerModelImpl( 
			IBioTMLFeatureGeneratorConfigurator featureConfiguration, 
			IBioTMLModelConfigurator modelConfiguration){
		super(featureConfiguration, modelConfiguration);
		setTransducerModel(null);
		this.pipe = setupPipe();
		this.isTrained = false;
	}

	public BioTMLMalletTransducerModelImpl(Transducer model, 			
			IBioTMLFeatureGeneratorConfigurator featureConfiguration, 
			IBioTMLModelConfigurator modelConfiguration){
		super(featureConfiguration, modelConfiguration);
		setTransducerModel(model);
		this.pipe = getModel().getInputPipe();
		this.isTrained = true;
	}
	
	@Override
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
			|| getModelConfiguration().getAlgorithmType().equals(BioTMLAlgorithm.mallethmm)) 
					&& (getModel() == null || getModel() instanceof Transducer))
				return true;
		}
		return false;
	}
	
	@Override
	public void train(IBioTMLCorpus corpus) throws BioTMLException {
		if(corpus == null)
			throw new BioTMLException(21);
		if(!isValid())
			throw new BioTMLException("MalletTransducerModel: The model configuration inputed is not valid!\n" + getModelConfiguration());
		trainingdataset = loadCorpus(corpus, getModelConfiguration().getNumThreads());
		trainingdataset = loadFeaturesSelection(trainingdataset, getFeatureConfiguration().getFeatureSelectionConfiguration());
		BioTMLFeaturesManager.getInstance().cleanMemoryFeaturesClass();
		// Train with Threads
		if(getModelConfiguration().getAlgorithmType().equals(BioTMLAlgorithm.malletcrf)){
			trainByThreadedLabelLikelihood(trainingdataset, defineCRF(trainingdataset), true);
			isTrained = true;
		}
		else if(getModelConfiguration().getAlgorithmType().equals(BioTMLAlgorithm.mallethmm)){
			trainByLikelihood(trainingdataset, defineHMM(trainingdataset), true);
			isTrained = true;
		}

	}
	
	@Override
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
	
	@Override
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

	@Override
	protected Pipe setupPipe(){
		ArrayList<Pipe> pipe = new ArrayList<Pipe>();
		pipe.add(new CorpusWithFeatures2TokenSequence());
//		pipe.add(new Corpus2TokenSequence()); 	
//		pipe.add(new FeaturesClasses2MalletFeatures(getFeatureConfiguration()));
//		pipe.add(new PrintTokenSequenceFeatures());
		if(getModelConfiguration().getAlgorithmType().equals(BioTMLAlgorithm.malletcrf))
			pipe.add(new TokenSequence2FeatureVectorSequence(true, true));
		if(getModelConfiguration().getAlgorithmType().equals(BioTMLAlgorithm.mallethmm))
			pipe.add(new TokenSequence2FeatureSequence());
		return new SerialPipes(pipe);
	}
	

	@Override
	protected Runnable getFoldProcessedThread(InstanceList trainingData, InstanceList testingData,
			List<IBioTMLEvaluation> multiEvaluations, String foldDescription) {
		return new MalletTransducerFoldProcessedInThread(trainingData, testingData, getPipe(), multiEvaluations, getModelConfiguration(), foldDescription);
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
		if(saveModel)
			setTransducerModel(model);
		
		return modelTraining;
	}
	
	private HMMTrainerByLikelihood trainByLikelihood(InstanceList dataToTrain, HMM model, boolean saveModel){
		HMMTrainerByLikelihood modelTraining = new HMMTrainerByLikelihood(model);
		modelTraining.train(dataToTrain);
		if(saveModel)
			setTransducerModel(model);
		
		return modelTraining;
	}

	private void setTransducerModel(Transducer model){
		this.transducerModel = model;
	}

	
}
