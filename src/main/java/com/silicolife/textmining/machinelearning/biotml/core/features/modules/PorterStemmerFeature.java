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
import com.silicolife.textmining.machinelearning.biotml.core.nlp.porterstemmer.BioTMLStemmer;

/**
 * 
 * A class responsible for stemmer feature from Porter Stemmer.
 * 
 * @since 1.0.0
 * @author Ruben Rodrigues ({@code rrodrigues@silicolife.com})
 */

public class PorterStemmerFeature implements IBioTMLFeatureGenerator{
	
	/**
	 * 
	 * Initializes the insertion of stemmer feature from Porter Stemmer.
	 * 
	 */
	public  PorterStemmerFeature(){
	}

	public Set<String> getUIDs() {
		Set<String> uids = new TreeSet<String>();
		uids.add("PORTERSTEM");
		return uids;
	}
	
	public Set<String> getRecomendedUIDs(){
		return getUIDs();
	}
	
	public Map<String, String> getUIDInfos() {
		Map<String, String> infoMap = new HashMap<>();
		infoMap.put("PORTERSTEM", "The Porter Stemmer system is used to create a feature that stores the stem of each token.");
		return infoMap;
	}

	public IBioTMLFeatureColumns getFeatureColumns(List<String> tokensToProcess,
			IBioTMLFeatureGeneratorConfigurator configuration)
			throws BioTMLException {
		
		if(tokensToProcess.isEmpty()){
			throw new BioTMLException(27);
		}
		
		BioTMLAssociationProcess tokenAnnotProcess = new BioTMLAssociationProcess(tokensToProcess);
		List<String> tokens = tokenAnnotProcess.getTokens();
		IBioTMLFeatureColumns features = new BioTMLFeatureColumns(tokens, getUIDs(), configuration);

		for (int i = 0; i < tokens.size(); i++){
			String tokenString = tokens.get(i);
			BioTMLStemmer stemmer = new BioTMLStemmer(tokenString);
    		String stem = stemmer.getStem();
    		if(!stem.isEmpty()){
    			features.addTokenFeature("PORTERSTEM="  + stem, "PORTERSTEM");
    		}
    		else{
    			features.addTokenFeature("PORTERSTEM="  + tokenString, "PORTERSTEM");
    		}
		}
		
		features.updateTokenFeaturesUsingAssociationProcess(tokenAnnotProcess);

		return features;
	}

	public void cleanMemory(){
	}

}