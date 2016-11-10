package com.silicolife.textmining.machinelearning.biotml.core.interfaces;

import java.util.List;

import javax.swing.ImageIcon;

import com.silicolife.textmining.machinelearning.biotml.core.exception.BioTMLException;

public interface IBioTMLNLP {
	
	public String getID();
	public String getName();
	public String getDescription();
	public ImageIcon getNLPImageIcon();
	public List<IBioTMLSentence> getSentences(String document) throws BioTMLException;

}
