package com.silicolife.textmining.machinelearning.biotml.core.nlp.nlp4j;

import java.util.List;

import javax.swing.ImageIcon;

import com.silicolife.textmining.machinelearning.biotml.core.exception.BioTMLException;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLNLP;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLSentence;

public class NLP4JImpl implements IBioTMLNLP{

	public NLP4JImpl(){
	}
	
	@Override
	public String getID() {
		return "nlp4j";
	}

	@Override
	public String getName() {
		return "NLP4J";
	}

	@Override
	public String getDescription() {
		return "The Natural Language Processing for JVM languages (NLP4J) project provides:\n"
				+ "* NLP tools readily available for research in various disciplines.\n"
				+ "* Frameworks for fast development of efficient and robust NLP components.\n"
				+ "* API for manipulating computational structures in NLP (e.g., dependency graph).";
	}

	public ImageIcon getNLPImageIcon() {
		return new ImageIcon(getClass().getClassLoader().getResource("icons/clearnlp-logo.png"));
	}

	@Override
	public List<IBioTMLSentence> getSentences(String document) throws BioTMLException {
		return BioTMLNLP4J.getInstance().getSentences(document);
	}

}
