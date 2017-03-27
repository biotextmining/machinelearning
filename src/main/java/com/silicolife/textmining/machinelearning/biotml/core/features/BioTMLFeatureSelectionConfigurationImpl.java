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

}
