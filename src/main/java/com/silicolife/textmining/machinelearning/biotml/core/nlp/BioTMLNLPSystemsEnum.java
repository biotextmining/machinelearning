package com.silicolife.textmining.machinelearning.biotml.core.nlp;

import javax.swing.ImageIcon;

public enum BioTMLNLPSystemsEnum {
	clearnlp{
		@Override
		public String toString() {
			return "ClearNLP";
		}
	},
	opennlp{
		@Override
		public String toString() {
			return "OpenNLP";
		}
	},
	stanfordnlp{
		@Override
		public String toString() {
			return "StanfordCoreNLP";
		}
	};
	
	public String getNLPDescription(BioTMLNLPSystemsEnum nlpenum){
		if(nlpenum == BioTMLNLPSystemsEnum.clearnlp){
			return "The ClearNLP project provides software and resources for natural language processing.\n"
					+ "The project is currently developed by the Center for Language and Information Research at Emory University. "
					+ "This project is under the Apache 2 license.";
		}
		if(nlpenum == BioTMLNLPSystemsEnum.opennlp){
			return "The Apache OpenNLP library is a machine learning based toolkit for the processing of natural language text.\n"
					+ "It supports the most common NLP tasks, such as tokenization, sentence segmentation, part-of-speech tagging, named entity extraction, chunking, parsing, and coreference resolution.\n"
					+ "These tasks are usually required to build more advanced text processing services.\n"
					+ "OpenNLP also includes maximum entropy and perceptron based machine learning.";
		}
		if(nlpenum == BioTMLNLPSystemsEnum.stanfordnlp){
			return "Stanford CoreNLP provides a set of natural language analysis tools which can take raw text input and give the base forms of words, their parts of speech, whether they are names of companies, people, etc., normalize dates, times, and numeric quantities, and mark up the structure of sentences in terms of phrases and word dependencies, indicate which noun phrases refer to the same entities, indicate sentiment, etc.\n"
					+ "Stanford CoreNLP is an integrated framework. Its goal is to make it very easy to apply a bunch of linguistic analysis tools to a piece of text.\n"
					+ "Starting from plain text, you can run all the tools on it with just two lines of code. It is designed to be highly flexible and extensible.\n"
					+ "With a single option you can change which tools should be enabled and which should be disabled. Its analyses provide the foundational building blocks for higher-level and domain-specific text understanding applications.";
		}
		return new String();
	}
	
	public ImageIcon getNLPImageIcon(BioTMLNLPSystemsEnum nlpenum){
		if(nlpenum == BioTMLNLPSystemsEnum.clearnlp){
			return new ImageIcon(getClass().getClassLoader().getResource("icons/clearnlp-logo.png"));
		}
		if(nlpenum == BioTMLNLPSystemsEnum.opennlp){
			return new ImageIcon(getClass().getClassLoader().getResource("icons/onlp-logo.png"));
		}
		if(nlpenum == BioTMLNLPSystemsEnum.stanfordnlp){
			return new ImageIcon(getClass().getClassLoader().getResource("icons/snlp-logo.png"));
		}
		return new ImageIcon();
	}
	
	public static BioTMLNLPSystemsEnum stringValueOf(String toString){
		for(BioTMLNLPSystemsEnum nlpsystem : BioTMLNLPSystemsEnum.values()){
			if(nlpsystem.toString().equals(toString)){
				return nlpsystem;
			}
		}
		return null;
	}
}
