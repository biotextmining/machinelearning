package com.silicolife.textmining.machinelearning.biotml.core.features.modules;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * A class responsible for offset conjunctions features adapted from Mallet features to be used in other ML libraries.
 * This module must be integrated in the feature generator module due the dependency of features already generator.
 * (e.g. the offset conjunctions of lemma feature: The module that generates the  lemma feature must call this module to create the lemma offset conjunctions.)
 * Please read the example usage in the ClearNLPFeatures module.
 * 
 * @since 1.0.0
 * @author Ruben Rodrigues ({@code rrodrigues@silicolife.com})
 */
public class OffsetConjunctions {

	private List<String> featuresList;
	private int[][] conjunctions;

	/**
	 * 
	 * Initializes the offset conjunction features.
	 * 
	 * @param featuresList - List of feature attributes generated.
	 * @param conjunctions - Array of pair of attribute index to create an offset conjunction.
	 */
	public OffsetConjunctions(List<String> featuresList, int[][] conjunctions){
		this.featuresList = featuresList;
		this.conjunctions = conjunctions;
	}
	
	private List<String> getFeaturesList(){
		return featuresList;
	}
	
	private int getFeatSize(){
		return getFeaturesList().size();
	}
	
	private int[][] getConjunctions(){
		return conjunctions;
	}
	
	public List<String> generateFeatures(){
		List<String> featResultList = new ArrayList<String>();
		for(int i=0; i<getFeatSize(); i++){
			String feature = new String();
			for(int[] conjunction : getConjunctions()){
				if(!redundant(conjunction)){
					feature += generateFeatConjuction(conjunction,i) + "\t";
				}
			}
			featResultList.add(feature);
		}
		return featResultList;
	}
	
	private boolean redundant(int[] conjunction){
		for(int i = 1; i<conjunction.length; i++){
			if(conjunction[i] == conjunction[i-1]){
				return true;
			}
		}
		return false;
	}
	
	private String generateFeatConjuction(int[] conjunction, int featIndex){
		String newFeature = new String();
		int i=0;
		boolean stop = false;
		while(!stop && (i<conjunction.length)){
			if(0<=(conjunction[i] + featIndex) && (conjunction[i] + featIndex)<getFeatSize()){
				if(newFeature.isEmpty()){
					newFeature += getFeaturesList().get(featIndex + conjunction[i]);
				} else{
					newFeature += "_&_" + getFeaturesList().get(featIndex + conjunction[i]);
				}
				newFeature += (conjunction[i]==0 ? "" : ("@" + conjunction[i]));
			} else{
				stop = true;
				newFeature = new String();
			}
			i++;
		}

		if(newFeature.compareTo(getFeaturesList().get(featIndex))==0){
			return new String();
		}
		return newFeature;
	}
}