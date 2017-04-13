package com.silicolife.textmining.machinelearning.biotml.core.features;

import java.util.Iterator;
import java.util.Set;

import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLFeatureGeneratorConfigurator;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLFeatureSelectionConfiguration;

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
	private IBioTMLFeatureSelectionConfiguration featureSelectionConfiguration;
	
	/**
	 *
	 * Initializes the feature generator system with a set of feature names.
	 * The system will find all modules that contains the feature name and execute the generation of those features.
	 * 
	 * @param featuresUIDs - Set of feature names.
	 */
	public BioTMLFeatureGeneratorConfiguratorImpl(Set<String> featuresUIDs){
		this.featuresUIDs = featuresUIDs;
		this.featureSelectionConfiguration = new BioTMLFeatureSelectionConfigurationImpl();
	}
	
	public BioTMLFeatureGeneratorConfiguratorImpl(Set<String> featuresUIDs, IBioTMLFeatureSelectionConfiguration featureSelectionConfiguration){
		this.featuresUIDs = featuresUIDs;
		this.featureSelectionConfiguration = featureSelectionConfiguration;
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

	@Override
	public IBioTMLFeatureSelectionConfiguration getFeatureSelectionConfiguration() {
		return featureSelectionConfiguration;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((featureSelectionConfiguration == null) ? 0 : featureSelectionConfiguration.hashCode());
		result = prime * result + ((featuresUIDs == null) ? 0 : featuresUIDs.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BioTMLFeatureGeneratorConfiguratorImpl other = (BioTMLFeatureGeneratorConfiguratorImpl) obj;
		if (featureSelectionConfiguration == null) {
			if (other.featureSelectionConfiguration != null)
				return false;
		} else if (!featureSelectionConfiguration.equals(other.featureSelectionConfiguration))
			return false;
		if (featuresUIDs == null) {
			if (other.featuresUIDs != null)
				return false;
		} else if (!featuresUIDs.equals(other.featuresUIDs))
			return false;
		return true;
	}
	
	

}