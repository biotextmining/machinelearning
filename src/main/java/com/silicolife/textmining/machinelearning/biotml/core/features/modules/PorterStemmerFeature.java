package com.silicolife.textmining.machinelearning.biotml.core.features.modules;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.silicolife.textmining.machinelearning.biotml.core.exception.BioTMLException;
import com.silicolife.textmining.machinelearning.biotml.core.features.datastructures.BioTMLFeatureColumns;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLAnnotation;
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
		Set<String> uids = new TreeSet<String>();
		uids.add("PORTERSTEM");
		return uids;
	}

	@Override
	public Map<String, String> getREFeatureIdsInfos() {
		Map<String, String> infoMap = new HashMap<>();
		infoMap.put("PORTERSTEM", "The Porter Stemmer system is used to create a feature that stores the stem of each annotation token in event.");
		return infoMap;
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
		for(IBioTMLAssociation association : associations){
			if(association.getEntryOne() instanceof IBioTMLAnnotation && association.getEntryTwo() instanceof IBioTMLAnnotation){
				IBioTMLAnnotation annotationOne = (IBioTMLAnnotation) association.getEntryOne();
				IBioTMLAnnotation annotationTwo = (IBioTMLAnnotation) association.getEntryTwo();
				
				if(configuration.hasFeatureUID("PORTERSTEM"))
					features.addBioTMLObjectFeature("PORTERSTEM="+getSteamsAssociated(annotationOne, annotationTwo, tokens), "PORTERSTEM");
				
			}else if(association.getEntryOne() instanceof IBioTMLAnnotation && association.getEntryTwo() instanceof IBioTMLAssociation){
				//TODO
			}else if(association.getEntryOne() instanceof IBioTMLAssociation && association.getEntryTwo() instanceof IBioTMLAnnotation){
				//TODO
			}else if(association.getEntryOne() instanceof IBioTMLAssociation && association.getEntryTwo() instanceof IBioTMLAssociation){
				//TODO
			}
		}
		
		return features;
	}
	
	private String getSteamsAssociated(IBioTMLAnnotation annotationOne, IBioTMLAnnotation annotationTwo, List<IBioTMLToken> tokens){
		String result = new String();
		Collections.sort(tokens);
		for(IBioTMLToken token : tokens){
			BioTMLStemmer stemmer = new BioTMLStemmer(token.getToken());
    		String stem = stemmer.getStem();
			if(annotationOne.getAnnotationOffsets().offsetsOverlap(token.getTokenOffsetsPair())){
				if(!result.isEmpty())
					result = result +"__&__";
				result = result+ stem;
			}
			if(annotationTwo.getAnnotationOffsets().offsetsOverlap(token.getTokenOffsetsPair())){
				if(!result.isEmpty())
					result = result +"__&__";
				result = result+ stem;
			}
		}
		return result;
	}

}