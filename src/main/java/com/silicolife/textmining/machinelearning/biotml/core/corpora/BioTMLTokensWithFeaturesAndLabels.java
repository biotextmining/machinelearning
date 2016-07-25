package com.silicolife.textmining.machinelearning.biotml.core.corpora;

import java.util.ArrayList;
import java.util.List;

import com.silicolife.textmining.machinelearning.biotml.core.BioTMLConstants;
import com.silicolife.textmining.machinelearning.biotml.core.exception.BioTMLException;

public class BioTMLTokensWithFeaturesAndLabels {

	List<String> tokens;
	List<BioTMLConstants> labels;
	List<BioTMLConstants> isAnnotationOrNot;
	List<List<String>> tokenFeatures;
	int annotationForRelationStartIndex = -1;
	int annotationForRelationEndIndex = -1;
	
	
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
	
	public void addTokenForNERModel(String token, BioTMLConstants label){
		getTokens().add(token);
		getLabels().add(label);
		getTokenFeatures().add(new ArrayList<>());
		
	}
	
	public void addTokenForNERModel(String token, BioTMLConstants label, BioTMLConstants isAnnotationOrNot){
		getTokens().add(token);
		getLabels().add(label);
		getIsAnnotationOrNot().add(isAnnotationOrNot);
		getTokenFeatures().add(new ArrayList<>());
	}
	
	public void addTokenForREModel(String token, BioTMLConstants label, int annotationForRelationStartIndex, int annotationForRelationEndIndex){
		getTokens().add(token);
		getLabels().add(label);
		getTokenFeatures().add(new ArrayList<>());
		this.annotationForRelationStartIndex = annotationForRelationStartIndex;
		this.annotationForRelationEndIndex = annotationForRelationEndIndex;
	}
	
	public void addTokenForREModel(String token, BioTMLConstants label, BioTMLConstants isAnnotationOrNot, int annotationForRelationStartIndex, int annotationForRelationEndIndex){
		getTokens().add(token);
		getLabels().add(label);
		getTokenFeatures().add(new ArrayList<>());
		getIsAnnotationOrNot().add(isAnnotationOrNot);
		this.annotationForRelationStartIndex = annotationForRelationStartIndex;
		this.annotationForRelationEndIndex = annotationForRelationEndIndex;
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


	public int getAnnotationForRelationStartIndex() {
		return annotationForRelationStartIndex;
	}


	public int getAnnotationForRelationEndIndex() {
		return annotationForRelationEndIndex;
	}
	
}
