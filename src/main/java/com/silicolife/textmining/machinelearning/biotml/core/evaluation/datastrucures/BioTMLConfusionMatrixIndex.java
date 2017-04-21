package com.silicolife.textmining.machinelearning.biotml.core.evaluation.datastrucures;

import java.io.Serializable;

public class BioTMLConfusionMatrixIndex implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private int predictedClassificationIndex;
	private int correctClassificationIndex;

	public BioTMLConfusionMatrixIndex(int predictedClassificationIndex, int correctClassificationIndex){
		this.predictedClassificationIndex = predictedClassificationIndex;
		this.correctClassificationIndex = correctClassificationIndex;
	}

	public int getPredictedClassificationIndex() {
		return predictedClassificationIndex;
	}
	
	public int getCorrectClassificationIndex() {
		return correctClassificationIndex;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + correctClassificationIndex;
		result = prime * result + predictedClassificationIndex;
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
		BioTMLConfusionMatrixIndex other = (BioTMLConfusionMatrixIndex) obj;
		if (correctClassificationIndex != other.correctClassificationIndex)
			return false;
		if (predictedClassificationIndex != other.predictedClassificationIndex)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "BioTMLConfusionMatrixIndex (" + correctClassificationIndex
				+ "-" + predictedClassificationIndex + ")";
	}
	
	

}
