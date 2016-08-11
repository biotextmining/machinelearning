package com.silicolife.textmining.machinelearning.biotml.core.features;

import java.util.Iterator;
import java.util.Set;

import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLFeatureGeneratorConfigurator;

/**
 * 
 * Feature generator module configurator class.
 * 
 * @since 1.0.0
 * @author Ruben Rodrigues ({@code rrodrigues@silicolife.com})
 */
public class BioTMLFeatureGeneratorConfiguratorImpl implements IBioTMLFeatureGeneratorConfigurator{

	private static final long serialVersionUID = 1L;
	private Set<String> featuresUIDs;
	
	/**
	 *
	 * Initializes the feature generator system with a set of feature names.
	 * The system will find all modules that contains the feature name and execute the generation of those features.
	 * 
	 * @param featuresUIDs - Set of feature names.
	 */
	public BioTMLFeatureGeneratorConfiguratorImpl(Set<String> featuresUIDs){
		this.featuresUIDs = featuresUIDs;
	}

	public Set<String> getFeaturesUIDs() {
		return featuresUIDs;
	}

	public boolean hasFeatureUID(String featureUID) {
		Set<String> featuresUID = getFeaturesUIDs();
		Iterator<String> featureInt = featuresUID.iterator();
		while(featureInt.hasNext()){
			String featUID = featureInt.next();
			if(featUID.equals(featureUID)){
				return true;
			}
		}
		return false;
	}

}