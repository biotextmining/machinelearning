package com.silicolife.textmining.machinelearning.biotml.core.interfaces;

import java.io.Serializable;
import java.util.regex.Pattern;

public interface IBioTMLMalletTransducerConfiguration extends Serializable{

	public String getStart();
	
	public void setStart(String start);
	
	public Integer getModelOrder();
	
	public void setModelOrder(Integer modelOrder);
	
	public Pattern getAllowedTransitionStates();
	
	public void setAllowedTransitionStates(Pattern allowedTransitionStates);
	
	public Pattern getForbiddenTransitionStates();
	
	public void setForbiddenTransitionStates(Pattern forbiddenTransitionStates);
	
}
