package com.silicolife.textmining.machinelearning.biotml.core.features;

import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLFeatureSelectionConfiguration;
import com.silicolife.textmining.machinelearning.biotml.core.mllibraries.BioTMLFeatureSelectionAlgorithm;

public class BioTMLFeatureSelectionConfigurationImpl implements IBioTMLFeatureSelectionConfiguration{

	private static final long serialVersionUID = 1L;
	private BioTMLFeatureSelectionAlgorithm featureselectionAlgorithm;
	private int selectedFeaturesSize;
	
	public BioTMLFeatureSelectionConfigurationImpl(){
		this.featureselectionAlgorithm = BioTMLFeatureSelectionAlgorithm.none;
		this.selectedFeaturesSize = 100;
	}
	
	public BioTMLFeatureSelectionConfigurationImpl(BioTMLFeatureSelectionAlgorithm featureselectionAlgorithm, int selectedFeaturesSize){
		this.featureselectionAlgorithm = featureselectionAlgorithm;
		this.selectedFeaturesSize = selectedFeaturesSize;
	}

	@Override
	public BioTMLFeatureSelectionAlgorithm getFeatureSelectionAlgorithm() {
		return featureselectionAlgorithm;
	}

	@Override
	public int getSelectedFeaturesSize() {
		return selectedFeaturesSize;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((featureselectionAlgorithm == null) ? 0 : featureselectionAlgorithm.hashCode());
		result = prime * result + selectedFeaturesSize;
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
		BioTMLFeatureSelectionConfigurationImpl other = (BioTMLFeatureSelectionConfigurationImpl) obj;
		if (featureselectionAlgorithm != other.featureselectionAlgorithm)
			return false;
		if (selectedFeaturesSize != other.selectedFeaturesSize)
			return false;
		return true;
	}

}
