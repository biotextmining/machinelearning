package com.silicolife.textmining.machinelearning.biotml.core.interfaces;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.silicolife.textmining.machinelearning.biotml.core.evaluation.datastrucures.BioTMLConfusionMatrixIndex;

public interface IBioTMLConfusionMatrix<O> extends Serializable{

	public List<String> getLabels();
	
	public Map<BioTMLConfusionMatrixIndex, List<O>> getConfusionMatrixIndexesToPredictedObjects();
	
	public void addPrediction(O predictionObject, String predictionLabel, String correctLabel);

	public List<O> getTruePositivesOfLabel(String label);

	public List<O> getTrueNegativesOfLabel(String label);

	public List<O> getFalsePositivesOfLabel(String label);

	public List<O> getFalseNegativesOfLabel(String label);

}
