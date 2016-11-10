package com.silicolife.textmining.machinelearning.biotml.core.nlp.clearnlp;

import java.util.List;

import javax.swing.ImageIcon;

import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLNLP;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLSentence;

public class ClearNLP implements IBioTMLNLP{
	
	public ClearNLP() {
		
	}

	public String getID() {
		return "clearnlp";
	}

	public String getName() {
		return "ClearNLP";
	}

	public String getDescription() {
		return "The ClearNLP project provides software and resources for natural language processing.\n"
				+ "The project is currently developed by the Center for Language and Information Research at Emory University. "
				+ "This project is under the Apache 2 license.";
	}

	public ImageIcon getNLPImageIcon() {
		return new ImageIcon(getClass().getClassLoader().getResource("icons/clearnlp-logo.png"));
	}

	public List<IBioTMLSentence> getSentences(String document) {
		return BioTMLClearNLP.getInstance().getSentences(document);
	}

}
