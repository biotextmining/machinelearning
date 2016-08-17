package com.silicolife.textmining.machinelearning.biotml.core.interfaces;

import java.util.concurrent.ExecutorService;

import com.silicolife.textmining.machinelearning.biotml.core.exception.BioTMLException;
import com.silicolife.textmining.machinelearning.biotml.core.mllibraries.mallet.multithread.InstanceListExtended;

public interface IBioTMLCorpusToInstancesThreadCreator {
	
	/**
	 * 
	 * Method to insert a instance into a executor as a thread
	 * 
	 */
	public void insertInstancesIntoExecutor(ExecutorService executor, IBioTMLFeatureGeneratorConfigurator configuration, InstanceListExtended instances) throws BioTMLException;
	
	/**
	 * 
	 * Method to stop the thread
	 * 
	 */
	public void stopInsertion();

}
