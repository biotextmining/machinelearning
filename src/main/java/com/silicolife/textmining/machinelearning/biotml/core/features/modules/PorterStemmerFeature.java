package com.silicolife.textmining.machinelearning.biotml.core.features.modules;

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
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLToken;
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

	public Set<String> getNERFeatureIds() {
		Set<String> uids = new TreeSet<String>();
		uids.add("PORTERSTEM");
		return uids;
	}
	
	public Set<String> getRecomendedNERFeatureIds(){
		return getNERFeatureIds();
	}
	
	public Map<String, String> getNERFeatureIdsInfos() {
		Map<String, String> infoMap = new HashMap<>();
		infoMap.put("PORTERSTEM", "The Porter Stemmer system is used to create a feature that stores the stem of each token.");
		return infoMap;
	}
	
	@Override
	public Set<String> getREFeatureIds() {
		return new TreeSet<String>();
	}

	@Override
	public Map<String, String> getREFeatureIdsInfos() {
		return new HashMap<>();
	}

	@Override
	public Set<String> getRecomendedREFeatureIds() {
		return new TreeSet<String>();
	}

	public IBioTMLFeatureColumns<IBioTMLToken> getFeatureColumns(List<IBioTMLToken> tokens,
			IBioTMLFeatureGeneratorConfigurator configuration)
			throws BioTMLException {
		
		if(tokens.isEmpty()){
			throw new BioTMLException(27);
		}
	
		IBioTMLFeatureColumns<IBioTMLToken> features = new BioTMLFeatureColumns<>(tokens, getNERFeatureIds(), configuration);

		for (int i = 0; i < tokens.size(); i++){
			String tokenString = tokens.get(i).getToken();
			BioTMLStemmer stemmer = new BioTMLStemmer(tokenString);
    		String stem = stemmer.getStem();
    		if(!stem.isEmpty()){
    			features.addBioTMLObjectFeature("PORTERSTEM="  + stem, "PORTERSTEM");
    		}
    		else{
    			features.addBioTMLObjectFeature("PORTERSTEM="  + tokenString, "PORTERSTEM");
    		}
		}

		return features;
	}

	public void cleanMemory(){
	}

	@SuppressWarnings("rawtypes")
	@Override
	public IBioTMLFeatureColumns<IBioTMLAssociation> getEventFeatureColumns(List<IBioTMLToken> tokens, List<IBioTMLAssociation> associations,
			IBioTMLFeatureGeneratorConfigurator configuration) throws BioTMLException {
		IBioTMLFeatureColumns<IBioTMLAssociation> features = new BioTMLFeatureColumns<>(associations, getREFeatureIds(), configuration);
//		for(IBioTMLAssociation association : associations){
//			//features
//		}
		
		return features;
	}

}