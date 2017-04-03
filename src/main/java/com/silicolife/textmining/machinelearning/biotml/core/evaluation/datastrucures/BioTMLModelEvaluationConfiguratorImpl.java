package com.silicolife.textmining.machinelearning.biotml.core.evaluation.datastrucures;

import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLModelEvaluationConfigurator;

/**
 * 
 * Represents model evaluation configurator.
 * 
 * @since 1.0.0
 * @author Ruben Rodrigues ({@code rrodrigues@silicolife.com})
 */

public class BioTMLModelEvaluationConfiguratorImpl implements IBioTMLModelEvaluationConfigurator{
	
	private int cvFoldsByDoc;
	private int cvFoldsBySent;
	private boolean useMultipleModelsToEvaluate;
	
	/**
	 * 
	 * Initializes the model evaluation configurator.
	 * 
	 */
	
	public BioTMLModelEvaluationConfiguratorImpl(){
		this.cvFoldsByDoc = 0;
		this.cvFoldsBySent = 0;
		this.useMultipleModelsToEvaluate=false;
	}

	public void setCrossValidationByCorpusDoc(int folds) {
		this.cvFoldsByDoc = folds;
		
	}
	
	public int getCVFoldsByDocuments() {
		return cvFoldsByDoc;
	}

	public boolean isUseCrossValidationByDocuments() {
		if(getCVFoldsByDocuments()>0){
			return true;
		}
		return false;
	}

	public void setCrossValidationByCorpusSent(int folds) {
		this.cvFoldsBySent = folds;
		
	}
	
	public int getCVFoldsBySentences() {
		return cvFoldsBySent;
	}


	public boolean isUseCrossValidationBySentences() {
		if(getCVFoldsBySentences()>0){
			return true;
		}
		return false;
	}

	@Override
	public boolean isUseMultipleModelsToEvaluate() {
		return useMultipleModelsToEvaluate;
	}

	@Override
	public void setUseMultipleModelsToEvaluate(boolean useMultipleModelsToEvaluate) {
		this.useMultipleModelsToEvaluate = useMultipleModelsToEvaluate;
	}
	
}