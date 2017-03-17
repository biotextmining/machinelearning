package com.silicolife.textmining.machinelearning.biotml.core.features.modules;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.silicolife.textmining.machinelearning.biotml.core.exception.BioTMLException;
import com.silicolife.textmining.machinelearning.biotml.core.features.datastructures.BioTMLFeatureColumns;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLAssociation;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLFeatureColumns;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLFeatureGenerator;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLFeatureGeneratorConfigurator;


/**
 * 
 * A class responsible for token text char N gram features.
 * 
 * @since 1.0.0
 * @author Ruben Rodrigues ({@code rrodrigues@silicolife.com})
 */

public class TokenTextCharNGramsFeatures implements IBioTMLFeatureGenerator{
	
	private Set<String> uIDs;


	/**
	 * 
	 * Initializes the insertion of  token text char N gram features.
	 * 
	 */
	public  TokenTextCharNGramsFeatures(){
		this.uIDs = initUIDs();
	}

	public Set<String> initUIDs() {
		Set<String> uids = new TreeSet<String>();
		uids.add("CHARNGRAM");
		return uids;
	}
	
	public Set<String> getNERFeatureIds() {
		return uIDs;
	}
	
	public Set<String> getRecomendedNERFeatureIds(){
		return new TreeSet<String>();
	}
	
	
	public Map<String, String> getNERFeatureIdsInfos() {
		Map<String, String> infoMap = new HashMap<>();
		infoMap.put("CHARNGRAM", "A token N gram is used as feature. (N=2,3 and 4).");
		return infoMap;
	}
	
	@Override
	public Set<String> getREFeatureIds() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, String> getREFeatureIdsInfos() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> getRecomendedREFeatureIds() {
		// TODO Auto-generated method stub
		return null;
	}
	
	private String tokenTextCharNGrams(String token, String featureName, Integer gramLength){
		String result = new String();
		if (token.length() > gramLength){
			for (int k = 0; k < (token.length() - gramLength)+1; k++){
				if(!result.isEmpty()){
					result = result + featureName + token.substring (k, k+gramLength) + "\t";
				}
				else{
					result = featureName + token.substring (k, k+gramLength) + "\t";
				}
			}
		}
		return result;
	}	
	
	public IBioTMLFeatureColumns getFeatureColumns(List<String> tokens,
			IBioTMLFeatureGeneratorConfigurator configuration)
			throws BioTMLException {
		
		if(tokens.isEmpty()){
			throw new BioTMLException(27);
		}

		IBioTMLFeatureColumns features = new BioTMLFeatureColumns(tokens, getNERFeatureIds(), configuration);
		
		for (int i = 0; i < tokens.size(); i++){
			String token = tokens.get(i);
			
			String result = tokenTextCharNGrams(token, "CHARNGRAM=", 2) + "\t";
			result = result + tokenTextCharNGrams(token, "CHARNGRAM=", 3) + "\t";
			result = result + tokenTextCharNGrams(token, "CHARNGRAM=", 4);
			features.addTokenFeature(result, "CHARNGRAM");
		}
		features.setUIDhasMultiFeatureColumn("CHARNGRAM");

		return features;
	}

	public void cleanMemory(){
	}

	@Override
	public IBioTMLFeatureColumns getEventFeatureColumns(List<String> tokens, List<IBioTMLAssociation> associations,
			IBioTMLFeatureGeneratorConfigurator configuration) throws BioTMLException {
		List<String> associationStrings = new ArrayList<>();
		
		for(IBioTMLAssociation association : associations){
			associationStrings.add(association.toString());
		}
		IBioTMLFeatureColumns features = new BioTMLFeatureColumns(associationStrings, getREFeatureIds(), configuration);
		for(IBioTMLAssociation association : associations){
			//features
		}
		
		return features;
	}

}