package com.silicolife.textmining.machinelearning.biotml.core.annotator.processors;

import com.silicolife.textmining.machinelearning.biotml.core.exception.BioTMLException;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLCorpus;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLModel;
import com.silicolife.textmining.machinelearning.biotml.core.mllibraries.mallet.BioTMLCorpusToInstanceMallet;

import cc.mallet.fst.SumLatticeDefault;
import cc.mallet.fst.Transducer;
import cc.mallet.pipe.Pipe;
import cc.mallet.types.Alphabet;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import cc.mallet.types.Sequence;

/**
 * 
 * Represents the mallet transducer annotator processor, it contains methods for mallet transducer models used in annotators.
 *  
 * @since 1.1.0
 * @version 1.1.0
 * @author Ruben Rodrigues ({@code rrodrigues@silicolife.com})
 */

public class BioTMLMalletTransducerAnnotatorProcessor extends BioTMLMalletAnnotatorProcessor{

	private IBioTMLCorpus corpus;
	private IBioTMLModel bioTMLModel;
	private int threads;
	private Transducer transducerModel;
	private BioTMLCorpusToInstanceMallet malletCorpus;

	public BioTMLMalletTransducerAnnotatorProcessor(IBioTMLCorpus corpus, IBioTMLModel bioTMLModel, int threads){
		this.corpus = corpus;
		this.bioTMLModel = bioTMLModel;
		this.threads = threads;
		this.transducerModel = (Transducer) bioTMLModel.getModel();
	}

	private IBioTMLCorpus getCorpus() {
		return corpus;
	}

	private IBioTMLModel getBioTMLModel() {
		return bioTMLModel;
	}

	private int getThreads() {
		return threads;
	}

	private Transducer getTransducerModel() {
		return transducerModel;
	}
	
	public InstanceList generatePredictionMatrix() throws BioTMLException{
		malletCorpus = new BioTMLCorpusToInstanceMallet(getCorpus(), 
				getBioTMLModel().getModelConfiguration().getClassType(), getBioTMLModel().getModelConfiguration().getIEType());
		Pipe transducingPipe = getTransducerModel().getInputPipe();
		return malletCorpus.exportToMalletFeatures(transducingPipe, getThreads(), getBioTMLModel().getFeatureConfiguration());
	}
	

	/**
	 * 
	 * Calculates the prediction score for mallet transducer prediction.
	 * 
	 * @param modelPredictor - Mallet transducer.
	 * @param input - Features of the sentence to be predicted.
	 * @param predictedSeq - Predicted label sentence.
	 * @return Score associated with the sentence prediction.
	 */
	@SuppressWarnings("rawtypes")
	public Double getPredictionScoreForInstance(Instance instanceToPredict){
		Sequence input = (Sequence) instanceToPredict.getData();
		Sequence predictedSeq = getPredictionForInstance(instanceToPredict);
		double logScore = new SumLatticeDefault(getTransducerModel(), input, predictedSeq).getTotalWeight();
		double logZ = new SumLatticeDefault(getTransducerModel(), input).getTotalWeight();
		return Math.exp(logScore - logZ);
	}
	
	@SuppressWarnings("rawtypes")
	public Sequence getPredictionForInstance(Instance instanceToPredict){
		Sequence input = (Sequence) instanceToPredict.getData();
		return getTransducerModel().transduce(input);
	}
	
	public void stopProcessor() {
		if(malletCorpus!= null){
			malletCorpus.stopAllFeatureThreads();
		}
		Alphabet.cleanAllAphabetsFromMemory();
		Pipe.cleanAllPipesFromMemory();
	}
}
