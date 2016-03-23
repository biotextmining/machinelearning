package com.silicolife.textmining.machinelearning.biotml.core.nlp.clearnlp;

import java.io.IOException;
import java.util.List;

import com.clearnlp.component.AbstractComponent;
import com.clearnlp.dependency.DEPTree;
import com.clearnlp.nlp.NLPGetter;
import com.clearnlp.nlp.NLPMode;
import com.clearnlp.reader.AbstractReader;
import com.silicolife.textmining.machinelearning.biotml.core.exception.BioTMLException;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLSentence;
import com.silicolife.textmining.machinelearning.biotml.core.nlp.clearnlp.segmenter.EnglishSegmenterExtender;
import com.silicolife.textmining.machinelearning.biotml.core.nlp.clearnlp.tokenizer.AbstractTokenizer;
import com.silicolife.textmining.machinelearning.biotml.core.nlp.clearnlp.tokenizer.EnglishTokenizerExtender;

/**
 * 
 * ClearNLP Singleton to accelerate the ClearNLP parser models performance access.
 * 
 * @since 1.0.0
 * @author Ruben Rodrigues ({@code rrodrigues@silicolife.com})
 */

public class BioTMLClearNLP {

	private final static String modelFile = "medical-en";

	private AbstractComponent posModel;
	private AbstractComponent dependencyModel;
	private AbstractComponent srlModel;

	private static BioTMLClearNLP _instance;

	private BioTMLClearNLP() {
	}

	/**
	 * Gives access to the ClearNLP instance.
	 * 
	 * @return Instance of ClearNLP singleton.
	 */
	public static synchronized BioTMLClearNLP getInstance() {
		if (_instance == null) {
			BioTMLClearNLP.createInstance();
		}
		return _instance;
	}

	/**
	 * Creates the singleton instance.
	 *  
	 */
	private static void createInstance(){

		if (_instance == null) {
			_instance = new BioTMLClearNLP();
		}
	}

	private synchronized void initPOSModel() throws BioTMLException{
		if(getPosModel() == null){
			try {
				posModel = NLPGetter.getComponent(modelFile, AbstractReader.LANG_EN, NLPMode.MODE_POS);
			} catch (IOException exc) {
				throw new BioTMLException(11,exc);
			}
		}
	}

	private synchronized void initDependecyModel() throws BioTMLException{
		if(getDependecyModel() == null){
			try {
				dependencyModel = NLPGetter.getComponent(modelFile, AbstractReader.LANG_EN, NLPMode.MODE_DEP);
			} catch (IOException exc) {
				throw new BioTMLException(11,exc);
			}
		}
	}

	private synchronized void initSrlModel() throws BioTMLException{
		if(getSrlModel()==null){
			try {
				srlModel = NLPGetter.getComponent(modelFile, AbstractReader.LANG_EN, NLPMode.MODE_SRL);
			} catch (IOException exc) {
				throw new BioTMLException(11,exc);
			}

		}
	}

	private AbstractComponent getPosModel() {
		return posModel;
	}

	private AbstractComponent getDependecyModel() {
		return dependencyModel;
	}

	private AbstractComponent getSrlModel() {
		return srlModel;
	}

	/**
	 * 
	 * Method that processes the document tokenization and sentence spliting. 
	 * The document is a string all raw text.
	 * 
	 * @param document - String that contains all document text.
	 * @return List of {@link IBioTMLSentence}.
	 */
	public List<IBioTMLSentence> getSentences(String document){
		AbstractTokenizer tokenizer  = new EnglishTokenizerExtender();
		EnglishSegmenterExtender segmenter = new EnglishSegmenterExtender(tokenizer);
		return segmenter.getSentences(document);
	}


	/**
	 * 
	 * Method to process the part-of-speech of each token in the sentence.
	 * 
	 * @param tree - ClearNLP DEPTree with POS tags.
	 * @throws BioTMLException
	 */
	public void processPos(DEPTree tree) throws BioTMLException{
		if(getPosModel()==null){
			initPOSModel();
		}
		getPosModel().process(tree);
	}

	/**
	 * 
	 * Method to process the dependency of each token in the sentence.
	 * 
	 * @param tree - ClearNLP DEPTree with dependency tags.
	 * @throws BioTMLException
	 */
	public void processDependency(DEPTree tree) throws BioTMLException{
		if(getDependecyModel()==null){
			initDependecyModel();
		}
		getDependecyModel().process(tree);
	}

	/**
	 * 
	 * Method to process the semantic role of each token in the sentence.
	 * 
	 * @param tree - ClearNLP DEPTree with semantic role tags.
	 * @throws BioTMLException
	 */
	public void processSRL(DEPTree tree) throws BioTMLException{
		if(getSrlModel()==null){
			initSrlModel();
		}
		getSrlModel().process(tree);
	}


	public void clearModelsInMemory(){
		posModel = null;
		dependencyModel = null;
		srlModel = null;
		_instance = null;
	}

}
