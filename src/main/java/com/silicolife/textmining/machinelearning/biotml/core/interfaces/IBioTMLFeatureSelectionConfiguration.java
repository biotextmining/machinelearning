package com.silicolife.textmining.machinelearning.biotml.core.interfaces;

import java.io.Serializable;

import com.silicolife.textmining.machinelearning.biotml.core.mllibraries.BioTMLFeatureSelectionAlgorithm;

public interface IBioTMLFeatureSelectionConfiguration extends Serializable{

	public BioTMLFeatureSelectionAlgorithm getFeatureSelectionAlgorithm();
	
	public int getSelectedFeaturesSize();
	
}
