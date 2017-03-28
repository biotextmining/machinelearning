package com.silicolife.textmining.machinelearning.biotml.core.mllibraries.mallet.evaluation;

import com.silicolife.textmining.machinelearning.biotml.core.evaluation.BioTMLConfusionMatrixImpl;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLConfusionMatrix;

import cc.mallet.classify.Classifier;
import cc.mallet.classify.Trial;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;

public class TrialBioTMLExtended extends Trial{

	private static final long serialVersionUID = 1L;

	public TrialBioTMLExtended(Classifier c, InstanceList ilist) {
		super(c, ilist);
	}

	public IBioTMLConfusionMatrix<Instance> getConfusionMatrix(int labelIndex){
		IBioTMLConfusionMatrix<Instance> confusionMatrix = new BioTMLConfusionMatrixImpl<>();
		int trueLabel, classLabel;
		for (int i = 0; i<this.size(); i++) {
			trueLabel = this.get(i).getInstance().getLabeling().getBestIndex();
			classLabel = this.get(i).getLabeling().getBestIndex();
			if (classLabel == labelIndex) {
				// predicted instance belongs to the index label. So is a Positive case
				if(trueLabel == labelIndex)
					confusionMatrix.addTruePositive(this.get(i).getInstance());
				else
					confusionMatrix.addFalsePositive(this.get(i).getInstance());
				
			}else{
				// predicted instance don't belongs to the index label. So is a Negative case
				if(trueLabel == labelIndex)
					confusionMatrix.addFalseNegative(this.get(i).getInstance());
				else
					confusionMatrix.addTrueNegative(this.get(i).getInstance());		
			}
		}
		return confusionMatrix;
	}
}
