package com.silicolife.textmining.machinelearning.biotml.core.interfaces;

import java.io.Serializable;

public interface IBioTMLCrossValidationFold<O> extends Serializable {
	
	public O getTrainingDataset();
	
	public O getTestingDataset();

}
