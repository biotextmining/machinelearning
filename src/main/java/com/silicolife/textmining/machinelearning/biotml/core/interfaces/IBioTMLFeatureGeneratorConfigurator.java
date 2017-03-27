package com.silicolife.textmining.machinelearning.biotml.core.interfaces;

import java.io.Serializable;
import java.util.Set;

/**
 * 
 * Feature generator module configurator interface.
 * 
 * @since 1.0.0
 * @author Ruben Rodrigues ({@code rrodrigues@silicolife.com})
 */
public interface IBioTMLFeatureGeneratorConfigurator extends Serializable{
	
	/**
	 * 
	 * Method to get the set of feature names that is used in features generation system.
	 * 
	 * @return Set of feature names.
	 */
	public Set<String> getFeaturesUIDs();
	
	/**
	 * 
	 * Method that verifies if a feature name was initialized in this system.
	 * 
	 * @param featureUID - String that represents the feature.
	 * @return Boolean that validates the feature.
	 */
	public boolean hasFeatureUID(String featureUID);
	
	public IBioTMLFeatureSelectionConfiguration getFeatureSelectionConfiguration();

}