package com.silicolife.textmining.machinelearning.biotml.core.evaluation;

import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLModelEvaluationConfigurator;

/**
 * 
 * Represents model evaluation configurator.
 * 
 * @since 1.0.0
 * @author Ruben Rodrigues ({@code rrodrigues@silicolife.com})
 */

public class BioTMLModelEvaluationConfigurator implements IBioTMLModelEvaluationConfigurator{
	
	private int cvFoldsByDoc;
	private int cvFoldsBySent;
	
	/**
	 * 
	 * Initializes the model evaluation configurator.
	 * 
	 */
	
	public BioTMLModelEvaluationConfigurator(){
		this.cvFoldsByDoc = 0;
		this.cvFoldsBySent = 0;
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
	
}