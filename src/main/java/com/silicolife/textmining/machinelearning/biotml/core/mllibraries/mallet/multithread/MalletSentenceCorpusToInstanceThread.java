package com.silicolife.textmining.machinelearning.biotml.core.mllibraries.mallet.multithread;

import cc.mallet.types.Instance;

/**
 * 
 * A class responsible to convert a sentence string into a Mallet instance using a multi-threaded system.
 * 
 * @since 1.0.0
 * @author Ruben Rodrigues ({@code rrodrigues@silicolife.com})
 */

public class MalletSentenceCorpusToInstanceThread implements Runnable{
	
	private String docIDandSentIdx;
	private String sentence;
	private InstanceListExtended instances;

	/**
	 * 
	 * Initializes a thread with a sentence to be converted into Mallet instance.
	 * 
	 * @param docIDandSentIdx - String that identifies the document ID and sentence index.
	 * @param sentence - Sentence string.
	 * @param instances - InstanceList with thread safety to be populated with all sentences.
	 */
	public MalletSentenceCorpusToInstanceThread(String docIDandSentIdx, String sentence, InstanceListExtended instances){
		this.docIDandSentIdx = docIDandSentIdx;
		this.sentence = sentence;
		this.instances = instances;
	}
	
	private String getDocIDandSentIdx(){
		return docIDandSentIdx;
	}
	
	private String getSentence(){
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
