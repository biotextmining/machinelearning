package com.silicolife.textmining.machinelearning.biotml.core.mllibraries.mallet;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import com.silicolife.textmining.machinelearning.biotml.core.BioTMLConstants;
import com.silicolife.textmining.machinelearning.biotml.core.BioTMLModelLabelType;
import com.silicolife.textmining.machinelearning.biotml.core.exception.BioTMLException;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLCorpus;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLCorpusToInstanceMallet;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLCorpusToInstancesThreadCreator;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLFeatureGeneratorConfigurator;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLModelConfigurator;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLREMethodologyConfiguration;
import com.silicolife.textmining.machinelearning.biotml.core.mllibraries.mallet.multithread.BioTMLCorpusToNERInstancesThreadCreator;
import com.silicolife.textmining.machinelearning.biotml.core.mllibraries.mallet.multithread.BioTMLCorpusToREInstancesThreadCreator;
import com.silicolife.textmining.machinelearning.biotml.core.mllibraries.mallet.multithread.InstanceListExtended;

import cc.mallet.pipe.Pipe;
import cc.mallet.types.InstanceList;

/**
 * 
 * A class responsible for corpus ({@link IBioTMLCorpus}) conversion into Mallet Instances.
 * 
 * @since 1.0.0
 * @version 1.0.3
 * @author Ruben Rodrigues ({@code rrodrigues@silicolife.com})
 */

public class BioTMLCorpusToInstanceMallet implements IBioTMLCorpusToInstanceMallet{

	private IBioTMLCorpus corpus;
	private String annotType;
	private String ieType;
	private IBioTMLREMethodologyConfiguration reMehtodology;
	private ExecutorService executor;
	private IBioTMLCorpusToInstancesThreadCreator instancesCreator;
	private BioTMLModelLabelType modelLabelType;

	/**
	 * 
	 * Initializes the conversion of BioTMCorpus into Mallet Instances regarding the annotation type.
	 * 
	 * @param corpus - {@link IBioTMLCorpus} to convert.
	 * @param modelConfiguration - model configuration.
	 */
	public BioTMLCorpusToInstanceMallet(IBioTMLCorpus corpus, IBioTMLModelConfigurator modelConfiguration){
		this.corpus = corpus;
		this.annotType = modelConfiguration.getClassType();
		this.ieType = modelConfiguration.getIEType();
		this.reMehtodology = modelConfiguration.getREMethodology();
		this.modelLabelType = modelConfiguration.getModelLabelType();
	}

	public IBioTMLCorpus getCorpusToConvert() {
		return corpus;
	}

	public String getConsideredAnnotationType() {
		return annotType;
	}

	public String getIEAnnotationType(){
		return ieType;
	}
	
	public IBioTMLREMethodologyConfiguration getREMehtodology() {
		return reMehtodology;
	}
	
	public BioTMLModelLabelType getModelLabelType(){
		return modelLabelType;
	}

	public InstanceList exportToMalletFeatures(Pipe p, int threads,  IBioTMLFeatureGeneratorConfigurator configuration) throws BioTMLException{
		InstanceListExtended instances = new InstanceListExtended(p);
		createFeaturesThreadExecutor(threads);
		if(getIEAnnotationType().equals(BioTMLConstants.ner.toString())){
			instancesCreator = new BioTMLCorpusToNERInstancesThreadCreator(getCorpusToConvert(), getConsideredAnnotationType(), getModelLabelType());
			instancesCreator.insertInstancesIntoExecutor(executor, configuration, instances);
		}
		if(getIEAnnotationType().equals(BioTMLConstants.re.toString())){
			instancesCreator = new BioTMLCorpusToREInstancesThreadCreator(getCorpusToConvert(), getREMehtodology(), getConsideredAnnotationType());
			instancesCreator.insertInstancesIntoExecutor(executor, configuration, instances);
		}
		finishThreadsFromExecutor();
		return instances;
	}

	private void finishThreadsFromExecutor() {
		executor.shutdown();
		try {
			executor.awaitTermination(Long.MAX_VALUE, TimeUnit.MINUTES);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		executor = null;
	}

	private void createFeaturesThreadExecutor(int threads) {
		final AtomicLong count = new AtomicLong(0);
		executor = Executors.newFixedThreadPool(threads,new ThreadFactory(){
			@Override
			public Thread newThread(Runnable runnable) {
				Thread thread = new Thread(runnable);
				thread.setName("BioTML-Features-Generation-Thread-" + count.getAndIncrement());
				thread.setDaemon(false);
				thread.setPriority(Thread.NORM_PRIORITY);
				return thread;
			};
		});
	}

	public void stopAllFeatureThreads(){
		if(instancesCreator != null){
			instancesCreator.stopInsertion();
		}
		if(executor != null){
			executor.shutdown();
			try {
				if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
					executor.shutdownNow();
					if (!executor.awaitTermination(60, TimeUnit.SECONDS))
						System.err.println("BioTML Features pool did not terminate");
				}
			} catch (InterruptedException ie) {
				executor.shutdownNow();
			}
		}
		instancesCreator = null;
		executor = null;
	}

}
