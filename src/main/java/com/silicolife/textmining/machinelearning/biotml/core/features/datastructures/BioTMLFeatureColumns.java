package com.silicolife.textmining.machinelearning.biotml.core.features.datastructures;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLFeatureColumns;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLFeatureGeneratorConfigurator;

/**
 * 
 * Feature generated columns class.
 * Each class contains all tokens from one sentence or document and generated features.
 * 
 * @since 1.0.0
 * @author Ruben Rodrigues ({@code rrodrigues@silicolife.com})
 */
public class BioTMLFeatureColumns implements IBioTMLFeatureColumns{


	private List<String> tokens;
	private Map<String, List<String>> uIDsToFeatureColumnMap;
	private Map<String, List<String>> uIDsToFeatureStoredToGenerateOtherFeaturesMap;
	private Set<String> uIDsWithMultiFeat;

	public BioTMLFeatureColumns(List<String> tokens, Set<String> moduleUIDs, IBioTMLFeatureGeneratorConfigurator configuration){
		this.tokens = tokens;
		this.uIDsToFeatureColumnMap = initUIDMap(moduleUIDs, configuration);
		this.uIDsWithMultiFeat = new HashSet<String>();
		this.uIDsToFeatureStoredToGenerateOtherFeaturesMap = new HashMap<String, List<String>>();
	}


	public List<String> getTokens(){
		return tokens;
	}

	public Set<String> getUIDs() {
		return getUIDsToFeatureColumnMap().keySet();
	}

	public List<String> getFeatureColumByUID(String featureUID) {
		if(getUIDs().contains(featureUID)){
			return getUIDsToFeatureColumnMap().get(featureUID);
		}else if(getUIDsToFeatureStoredToGenerateOtherFeaturesMap().containsKey(featureUID)){
			return getUIDsToFeatureStoredToGenerateOtherFeaturesMap().get(featureUID);
		}
		return new ArrayList<String>();
	}
	
	public List<String> getTokenFeatures(int tokenIndex){
		List<String> features = new ArrayList<>();
		for(String uID : getUIDs()){
			if(isMultiFeatureColumn(uID)){
				String[] tokenfeatures = getFeatureColumByUID(uID).get(tokenIndex).split("\t");
				for(String feat : tokenfeatures){
					features.add(feat);
				}
			}else{
				features.add(getFeatureColumByUID(uID).get(tokenIndex));
			}

		}
		return features;
	}

	public void addTokenFeature(String tokenFeature, String uID){
		if(getUIDs().contains(uID)){
			List<String> tokenFeatures = getUIDsToFeatureColumnMap().get(uID);
			tokenFeatures.add(tokenFeature);
			getUIDsToFeatureColumnMap().put(uID,tokenFeatures);
		}else{
			if(!getUIDsToFeatureStoredToGenerateOtherFeaturesMap().containsKey(uID)){
				getUIDsToFeatureStoredToGenerateOtherFeaturesMap().put(uID, new ArrayList<String>());
			}
			List<String> tokenFeatures = getUIDsToFeatureStoredToGenerateOtherFeaturesMap().get(uID);
			tokenFeatures.add(tokenFeature);
			getUIDsToFeatureStoredToGenerateOtherFeaturesMap().put(uID,tokenFeatures);
		}
	}

	public void updateTokenFeatures(List<String> tokenFeatures, String uID){
		if(getUIDs().contains(uID)){
			getUIDsToFeatureColumnMap().put(uID,tokenFeatures);
		}else{
			getUIDsToFeatureStoredToGenerateOtherFeaturesMap().put(uID, tokenFeatures);
		}
	}

	public void updateTokenFeaturesUsingAssociationProcess(BioTMLAssociationProcess tokenAnnotProcess) {
		for(String uID:getUIDs()){
			List<String> featureColum = getFeatureColumByUID(uID);
			featureColum = tokenAnnotProcess.associateAnnotationFeatureToFeatureColumn(featureColum);
			updateTokenFeatures(featureColum, uID);
		}
	}

	public void setUIDhasMultiFeatureColumn(String uID){
		if(getUIDs().contains(uID)){
			getUIDsWithMultiFeat().add(uID);
		}
	}

	public boolean isMultiFeatureColumn(String uID) {
		if(getUIDsWithMultiFeat().contains(uID)){
			return true;
		}
		return false;
	}
	
	private Map<String, List<String>> initUIDMap(Set<String> moduleUIDs, IBioTMLFeatureGeneratorConfigurator configuration){
		Map<String, List<String>> uIDsToFeatureColumnMap = new HashMap<String, List<String>>();
		for(String moduleUID:moduleUIDs){
			if(configuration.hasFeatureUID(moduleUID)){
				uIDsToFeatureColumnMap.put(moduleUID, new ArrayList<String>());
			}
		}
		return uIDsToFeatureColumnMap;
	}

	private Map<String, List<String>> getUIDsToFeatureColumnMap(){
		return uIDsToFeatureColumnMap;
	}

	private Set<String> getUIDsWithMultiFeat(){
		return uIDsWithMultiFeat;
	}
	
	private Map<String, List<String>> getUIDsToFeatureStoredToGenerateOtherFeaturesMap(){
		return uIDsToFeatureStoredToGenerateOtherFeaturesMap;
	}

}