package com.silicolife.textmining.machinelearning.biotml.core.mllibraries.mallet.multithread;

import com.silicolife.textmining.machinelearning.biotml.core.corpora.otherdatastructures.BioTMLDocSentIDs;
import com.silicolife.textmining.machinelearning.biotml.core.corpora.otherdatastructures.BioTMLObjectWithFeaturesAndLabels;

import cc.mallet.types.Instance;

/**
 * 
 * A class responsible to convert a sentence string into a Mallet instance using a multi-threaded system.
 * 
 * @since 1.0.0
 * @author Ruben Rodrigues ({@code rrodrigues@silicolife.com})
 */

public class MalletSentenceCorpusToInstanceThread implements Runnable{
	
	private BioTMLDocSentIDs docIDandSentIdx;
	private BioTMLObjectWithFeaturesAndLabels<?> sentence;
	private InstanceListExtended instances;

	/**
	 * 
	 * Initializes a thread with a sentence to be converted into Mallet instance.
	 * 
	 * @param docIDandSentIdx - String that identifies the document ID and sentence index.
	 * @param sentence - Sentence string.
	 * @param instances - InstanceList with thread safety to be populated with all sentences.
	 */
	public MalletSentenceCorpusToInstanceThread(BioTMLDocSentIDs docIDandSentIdx, BioTMLObjectWithFeaturesAndLabels<?> sentence, InstanceListExtended instances){
		this.docIDandSentIdx = docIDandSentIdx;
		this.sentence = sentence;
		this.instances = instances;
	}
	
	private BioTMLDocSentIDs getDocIDandSentIdx(){
		return docIDandSentIdx;
	}
	
	private BioTMLObjectWithFeaturesAndLabels<?> getSentence(){
		return sentence;
	}

	private InstanceListExtended getInstances(){
		return instances;
	}
	
	/**
	 * 
	 * Thread safe process to add the sentence into Mallet instances.
	 * 
	 */
	public void run() {
		getInstances().addThruPipe(new Instance(getSentence(), null, getDocIDandSentIdx(), getSentence()));
	}

}
