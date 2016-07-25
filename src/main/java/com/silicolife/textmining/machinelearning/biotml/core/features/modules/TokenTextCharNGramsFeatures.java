package com.silicolife.textmining.machinelearning.biotml.core.features.modules;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.silicolife.textmining.machinelearning.biotml.core.exception.BioTMLException;
import com.silicolife.textmining.machinelearning.biotml.core.features.datastructures.BioTMLAssociationProcess;
import com.silicolife.textmining.machinelearning.biotml.core.features.datastructures.BioTMLFeatureColumns;
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
	
	public Set<String> getUIDs() {
		return uIDs;
	}
	
	public Set<String> getRecomendedUIDs(){
		return new TreeSet<String>();
	}
	
	
	public Map<String, String> getUIDInfos() {
		Map<String, String> infoMap = new HashMap<>();
		infoMap.put("CHARNGRAM", "A token N gram is used as feature. (N=2,3 and 4).");
		return infoMap;
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

	public IBioTMLFeatureColumns getFeatureColumnsForRelations(List<String> tokensToProcess, int startAnnotationIndex, int endAnnotationIndex, IBioTMLFeatureGeneratorConfigurator configuration) throws BioTMLException {
		if(tokensToProcess.isEmpty()){
			throw new BioTMLException(27);
		}
		BioTMLAssociationProcess tokenAnnotProcess = new BioTMLAssociationProcess(tokensToProcess, startAnnotationIndex, endAnnotationIndex);
		List<String> tokens = tokenAnnotProcess.getTokens();
		IBioTMLFeatureColumns features = getFeatureColumns(tokens, configuration);
		features.updateTokenFeaturesUsingAssociationProcess(tokenAnnotProcess);
		return features;
	}
	
	public IBioTMLFeatureColumns getFeatureColumns(List<String> tokens,
			IBioTMLFeatureGeneratorConfigurator configuration)
			throws BioTMLException {
		
		if(tokens.isEmpty()){
			throw new BioTMLException(27);
		}

		IBioTMLFeatureColumns features = new BioTMLFeatureColumns(tokens, getUIDs(), configuration);
		
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

}