package com.silicolife.textmining.machinelearning.biotml.core.mllibraries.mallet.evaluation;

import com.silicolife.textmining.machinelearning.biotml.core.evaluation.datastrucures.BioTMLConfusionMatrixImpl;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLConfusionMatrix;

import cc.mallet.classify.Classifier;
import cc.mallet.classify.Trial;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import cc.mallet.types.Label;
import cc.mallet.types.Labeling;

public class TrialBioTMLExtended extends Trial{

	private static final long serialVersionUID = 1L;

	public TrialBioTMLExtended(Classifier c, InstanceList ilist) {
		super(c, ilist);
	}

	public IBioTMLConfusionMatrix<Instance> getConfusionMatrix(){
		IBioTMLConfusionMatrix<Instance> confusionMatrix = new BioTMLConfusionMatrixImpl<>();

		for (int i = 0; i<this.size(); i++) {
			Instance originalInstance = this.get(i).getInstance();
			Labeling predictedLabeling = this.get(i).getLabeling();
			Label correctLabel = originalInstance.getLabeling().getBestLabel();
			Label predictionLabel = predictedLabeling.getBestLabel();
			Instance predictedInstance = cloneInstance(originalInstance);
			predictedInstance.setLabeling(predictedLabeling);
			confusionMatrix.addPrediction(predictedInstance, predictionLabel.toString(), correctLabel.toString());
		}
		return confusionMatrix;
	}
	
	@SuppressWarnings("deprecation")
	private Instance cloneInstance(Instance instance){
		Instance ret = new Instance (instance.getData(), instance.getTarget(), instance.getName(), instance.getSource());
		ret.setPropertyList(instance.getProperties());
		return ret;
	}
}
