package com.silicolife.textmining.machinelearning.biotml.core.interfaces;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.silicolife.textmining.machinelearning.biotml.core.exception.BioTMLException;

/**
 * 
 * BioTML multi model interface.
 * 
 * @since 1.0.0
 * @author Ruben Rodrigues ({@code rrodrigues@silicolife.com})
 */

public interface IBioTMLMultiModel extends Serializable{
	
	/**
	 * 
	 * Method to get all submodels present in the multi-model file.
	 * 
	 * @return List of submodels ({@link IBioTMLModel}).
	 */
	public List<IBioTMLModel> getModels();
	
	/**
	 * 
	 * Method to train the multi-model using the initialized configurations.
	 * 
	 * @param corpus {@link IBioTMLCorpus} to train the model.
	 * @throws BioTMLException
	 */
	public void train(IBioTMLCorpus corpus) throws BioTMLException;
	
	/**
	 * 
	 * Method to annotate a corpus using this multi-model.
	 * 
	 * @param corpus Corpus to be annotated with the model ({@link IBioTMLCorpus}).
	 * @return Annotated corpus ({@link IBioTMLCorpus}).
	 * @throws BioTMLException
	 */
	public IBioTMLCorpus predict(IBioTMLCorpus corpus) throws BioTMLException;
	
	/**
	 * 
	 * Method to evaluate the multi-model using the initialized evaluation configurations.
	 * 
	 * @return Map of evaluations by submodel.
	 * @throws BioTMLException
	 */
	public Map<String, IBioTMLMultiEvaluation> evaluate(IBioTMLCorpus corpus, IBioTMLModelEvaluationConfigurator modelEvaluationConfiguration)  throws BioTMLException;
	

}
