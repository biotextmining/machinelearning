package com.silicolife.textmining.machinelearning.biotml.core.features.modules;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * A class responsible for window features adapted from Mallet features to be used in other ML libraries.
 * This module must be integrated in the feature generator module due the dependency of features already generator.
 * (e.g. the window feature of lemma feature: The module that generates the lemma feature must call this module to create the lemma window feature.)
 * Please read the example usage in the ClearNLPFeatures module.
 * 
 * @since 1.0.0
 * @author Ruben Rodrigues ({@code rrodrigues@silicolife.com})
 */
public class WindowFeatures {
	
	private String featureString;
	private List<String> featuresList;
	private int leftBoundaryOffset;
	private int rightBoundaryOffset;

	/**
	 * 
	 * Initializes the window features.
	 * 
	 * @param featureString - Feature window name.
	 * @param featuresList - List of feature attributes generated.
	 * @param leftBoundaryOffset - Window left boundary offset.
	 * @param rightBoundaryOffset - Window right boundary offset.
	 */
	public WindowFeatures(String featureString, List<String> featuresList, int leftBoundaryOffset, int rightBoundaryOffset){
		this.featureString = featureString;
		this.featuresList = featuresList;
		this.leftBoundaryOffset = leftBoundaryOffset;
		this.rightBoundaryOffset = rightBoundaryOffset;
	}
	
	private String getFeaturesString(){
		return featureString;
	}
	
	private List<String> getFeaturesList(){
		return featuresList;
	}
	
	private int getFeatSize(){
		return getFeaturesList().size();
	}
	
	private int getLeftBoundaryOffset(){
		return leftBoundaryOffset;
	}
	
	private int getRightBoundaryOffset(){
		return rightBoundaryOffset;
	}
	
	public List<String> generateFeatures(){
		List<String> featResultList = new ArrayList<String>();
		for(int i=0; i<getFeatSize(); i++){
			featResultList.add(generateFeatWindow(i));
		}
		return featResultList;
	}

	
	private String generateFeatWindow(int featIndex){
		String newFeature = new String();
		if(getLeftBoundaryOffset()<0){
			for(int i=getLeftBoundaryOffset(); i<0;i++){
				if((i+featIndex)>=0){
					newFeature += getFeaturesString()+getFeaturesList().get(i+featIndex)+"\t";
				}
			}
		}
		if(getRightBoundaryOffset()>0){
			for(int i=1; i <= getRightBoundaryOffset(); i++){
				if((i+featIndex)<getFeatSize()){
					newFeature += getFeaturesString()+getFeaturesList().get(i+featIndex)+"\t";
				}
			}
		}
		return newFeature;
	}
	
	
}
