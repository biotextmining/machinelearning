package com.silicolife.textmining.machinelearning.biotml.core.nlp.opennlp;

import java.util.List;

import javax.swing.ImageIcon;

import com.silicolife.textmining.machinelearning.biotml.core.exception.BioTMLException;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLNLP;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLSentence;

public class OpenNLPImpl implements IBioTMLNLP {
	
	public OpenNLPImpl() {
		
	}

	public String getID() {
		return "opennlp";
	}

	public String getName() {
		return "OpenNLP";
	}

	public String getDescription() {
		return "The Apache OpenNLP library is a machine learning based toolkit for the processing of natural language text.\n"
				+ "It supports the most common NLP tasks, such as tokenization, sentence segmentation, part-of-speech tagging, named entity extraction, chunking, parsing, and coreference resolution.\n"
				+ "These tasks are usually required to build more advanced text processing services.\n"
				+ "OpenNLP also includes maximum entropy and perceptron based machine learning.";
	}

	public ImageIcon getNLPImageIcon() {
		return new ImageIcon(getClass().getClassLoader().getResource("icons/onlp-logo.png"));
	}

	public List<IBioTMLSentence> getSentences(String document) throws BioTMLException {
		return BioTMLOpenNLP.getInstance().getSentences(document);
	}
	

}
