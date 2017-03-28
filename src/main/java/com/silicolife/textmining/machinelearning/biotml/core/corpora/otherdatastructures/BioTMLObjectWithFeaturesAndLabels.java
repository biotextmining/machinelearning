package com.silicolife.textmining.machinelearning.biotml.core.corpora.otherdatastructures;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.silicolife.textmining.machinelearning.biotml.core.BioTMLConstants;
import com.silicolife.textmining.machinelearning.biotml.core.exception.BioTMLException;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLToken;

public class BioTMLObjectWithFeaturesAndLabels<O> implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private Class<O> type;
	private List<O> bioTMLObjects;
	private List<BioTMLConstants> labels;
	private List<BioTMLConstants> filterConstants;
	private List<List<String>> features;
	private List<IBioTMLToken> tokens;
	
	
	public BioTMLObjectWithFeaturesAndLabels(Class<O> type){
		this.type = type;
		this.bioTMLObjects = new ArrayList<>();
		this.labels = new ArrayList<>();
		this.features = new ArrayList<>();
		this.filterConstants = new ArrayList<>();
		this.tokens = new ArrayList<>();
	}
	
	public Class<O> getBioTMLObjectClass(){
		return type;
	}
	
	public List<O> getBioTMLObjects() {
		return bioTMLObjects;
	}

	public List<BioTMLConstants> getLabels() {
		return labels;
	}

	public List<BioTMLConstants> getFilterConstants() {
		return filterConstants;
	}

	public List<List<String>> getFeatures() {
		return features;
	}
	
	public List<IBioTMLToken> getTokens(){
		return tokens;
	}

	public void addFeaturesToBioTMLObjectIndex(int objIndex, List<String> features) throws BioTMLException{
		if(objIndex >= 0  && objIndex<getBioTMLObjects().size()){
			List<String> objectFeatures = getFeatures().get(objIndex);
			objectFeatures.addAll(features);
		}else{
			throw new BioTMLException("The object index of features is wrong!");
		}
	}
	
	public void addBioTMLObjectForPrediction(O obj){
		getBioTMLObjects().add(obj);
		getFeatures().add(new ArrayList<>());
		
	}
	
	public void addBioTMLObjectForModel(O obj, BioTMLConstants label){
		this.addBioTMLObjectForPrediction(obj);
		getLabels().add(label);
	}
	
	public void addBioTMLObjectForPredictionAnnotationFiltering(O obj, BioTMLConstants filterConstant){
		this.addBioTMLObjectForPrediction(obj);
		getFilterConstants().add(filterConstant);
	}
	
	public void addBioTMLObjectForModelAnnotationFiltering(O obj, BioTMLConstants label, BioTMLConstants filterConstant){
		this.addBioTMLObjectForPredictionAnnotationFiltering(obj, filterConstant);
		getLabels().add(label);
	}
	
	public void addToken(IBioTMLToken token){
		getTokens().add(token);
	}

}
