package com.silicolife.textmining.machinelearning.biotml.core.evaluation.datastrucures;

import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLCrossValidationFold;

public class BioTMLCrossValidationFoldImpl<O> implements IBioTMLCrossValidationFold<O>{
	
	private static final long serialVersionUID = 1L;
	private O trainingDataset;
	private O testingDataset;

	public BioTMLCrossValidationFoldImpl(O trainingDataset, O testingDataset){
		this.trainingDataset = trainingDataset;
		this.testingDataset = testingDataset;
	}

	public O getTrainingDataset() {
		return trainingDataset;
	}

	public O getTestingDataset() {
		return testingDataset;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((testingDataset == null) ? 0 : testingDataset.hashCode());
		result = prime * result + ((trainingDataset == null) ? 0 : trainingDataset.hashCode());
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
		BioTMLCrossValidationFoldImpl<?> other = (BioTMLCrossValidationFoldImpl<?>) obj;
		if (testingDataset == null) {
			if (other.testingDataset != null)
				return false;
		} else if (!testingDataset.equals(other.testingDataset))
			return false;
		if (trainingDataset == null) {
			if (other.trainingDataset != null)
				return false;
		} else if (!trainingDataset.equals(other.trainingDataset))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "BioTMLCrossValidationFoldImpl [trainingDataset=" + trainingDataset + ", testingDataset="
				+ testingDataset + "]";
	}

}
