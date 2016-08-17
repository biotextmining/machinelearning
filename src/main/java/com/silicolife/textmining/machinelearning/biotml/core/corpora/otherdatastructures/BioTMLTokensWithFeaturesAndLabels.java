package com.silicolife.textmining.machinelearning.biotml.core.corpora.otherdatastructures;

import java.util.ArrayList;
import java.util.List;

import com.silicolife.textmining.machinelearning.biotml.core.BioTMLConstants;
import com.silicolife.textmining.machinelearning.biotml.core.exception.BioTMLException;

public class BioTMLTokensWithFeaturesAndLabels {

	List<String> tokens;
	List<BioTMLConstants> labels;
	List<BioTMLConstants> isAnnotationOrNot;
	List<List<String>> tokenFeatures;
	
	public BioTMLTokensWithFeaturesAndLabels(){
		this.tokens = new ArrayList<>();
		this.labels = new ArrayList<>();
		this.tokenFeatures = new ArrayList<>();
		this.isAnnotationOrNot = new ArrayList<>();
	}
	
	public void addFeaturesToTokenIndex(int tokenIndex, List<String> features) throws BioTMLException{
		if(tokenIndex >= 0  && tokenIndex<getTokens().size()){
			List<String> allFeatures = getTokenFeatures().get(tokenIndex);
			allFeatures.addAll(features);
		}else{
			throw new BioTMLException("The token index of features is wrong!");
		}
	}
	
	public void addTokenForPrediction(String token){
		getTokens().add(token);
		getTokenFeatures().add(new ArrayList<>());
		
	}
	
	public void addTokenForModel(String token, BioTMLConstants label){
		this.addTokenForPrediction(token);
		getLabels().add(label);
	}
	
	public void addTokenForPredictionAnnotationFiltering(String token, BioTMLConstants isAnnotationOrNot){
		this.addTokenForPrediction(token);
		getIsAnnotationOrNot().add(isAnnotationOrNot);
	}
	
	public void addTokenForModelAnnotationFiltering(String token, BioTMLConstants label, BioTMLConstants isAnnotationOrNot){
		this.addTokenForPredictionAnnotationFiltering(token, isAnnotationOrNot);
		getLabels().add(label);
	}

	public List<String> getTokens() {
		return tokens;
	}

	public List<BioTMLConstants> getLabels() {
		return labels;
	}
	
	public List<List<String>> getTokenFeatures(){
		return tokenFeatures;
	}


	public List<BioTMLConstants> getIsAnnotationOrNot() {
		return isAnnotationOrNot;
	}

}
