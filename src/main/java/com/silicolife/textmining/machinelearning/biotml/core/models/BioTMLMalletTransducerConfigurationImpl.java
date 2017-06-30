package com.silicolife.textmining.machinelearning.biotml.core.models;

import java.util.regex.Pattern;

import com.silicolife.textmining.machinelearning.biotml.core.BioTMLConstants;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLMalletTransducerConfiguration;

public class BioTMLMalletTransducerConfigurationImpl implements IBioTMLMalletTransducerConfiguration{
	
	private static final long serialVersionUID = 1L;
	private String start;
	private Integer modelOrder;
	private Pattern allowedTransitionStates;
	private Pattern forbiddenTransitionStates;
	
	
	public BioTMLMalletTransducerConfigurationImpl(){
		this.modelOrder = 1;
		this.start = BioTMLConstants.o.toString();
		this.forbiddenTransitionStates = Pattern.compile(BioTMLConstants.o.toString()+","+BioTMLConstants.i.toString());
	}

	public String getStart() {
		return start;
	}

	public void setStart(String start) {
		this.start = start;
	}

	public Integer getModelOrder() {
		return modelOrder;
	}

	public void setModelOrder(Integer modelOrder) {
		this.modelOrder = modelOrder;
	}

	public Pattern getAllowedTransitionStates() {
		return allowedTransitionStates;
	}

	public void setAllowedTransitionStates(Pattern allowedTransitionStates) {
		this.allowedTransitionStates = allowedTransitionStates;
	}

	public Pattern getForbiddenTransitionStates() {
		return forbiddenTransitionStates;
	}

	public void setForbiddenTransitionStates(Pattern forbiddenTransitionStates) {
		this.forbiddenTransitionStates = forbiddenTransitionStates;
	}

}
