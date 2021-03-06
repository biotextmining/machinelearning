package com.silicolife.textmining.machinelearning.biotml.core.mllibraries.mallet.multithread;

import java.util.ArrayList;
import java.util.List;

import com.silicolife.textmining.machinelearning.biotml.core.corpora.otherdatastructures.BioTMLObjectWithFeaturesAndLabels;
import com.silicolife.textmining.machinelearning.biotml.core.exception.BioTMLException;
import com.silicolife.textmining.machinelearning.biotml.core.features.BioTMLFeaturesManager;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLAssociation;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLDocument;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLFeatureColumns;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLFeatureGenerator;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLFeatureGeneratorConfigurator;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLToken;

import cc.mallet.types.Instance;

/**
 * 
 * A class responsible to convert a sentence string into a Mallet instance using a multi-threaded system.
 * 
 * @since 1.0.0
 * @author Ruben Rodrigues ({@code rrodrigues@silicolife.com})
 */

public class CorpusSentenceAndFeaturesToInstanceThread implements Runnable{

	private IBioTMLDocument document;
	private BioTMLObjectWithFeaturesAndLabels<?> bioTMLObjectWithFeaturesAndLabels;
	private InstanceListExtended instances;
	private IBioTMLFeatureGeneratorConfigurator configuration;

	/**
	 * 
	 * Initializes a thread with a sentence to be converted into Mallet instance.
	 * 
	 * @param document - Document source.
	 * @param biotmlObject - object that will be used on prediction
	 * @param tokensWithLabels - Sentence string.
	 * @param instances - InstanceList with thread safety to be populated with all sentences.
	 */
	public CorpusSentenceAndFeaturesToInstanceThread(IBioTMLDocument document, BioTMLObjectWithFeaturesAndLabels<?> tokensWithFeaturesAndLabels, InstanceListExtended instances,  IBioTMLFeatureGeneratorConfigurator configuration){
		this.document = document;
		this.bioTMLObjectWithFeaturesAndLabels = tokensWithFeaturesAndLabels;
		this.instances = instances;
		this.configuration = configuration;
	}

	private IBioTMLDocument getDocument(){
		return document;
	}
	

	private BioTMLObjectWithFeaturesAndLabels<?> getBioTMLObjectWithFeaturesAndLabels(){
		return bioTMLObjectWithFeaturesAndLabels;
	}

	private InstanceListExtended getInstances(){
		return instances;
	}

	private IBioTMLFeatureGeneratorConfigurator getConfiguration(){
		return configuration;
	}

	private void processColumns(List<IBioTMLFeatureColumns<?>> columns, InstanceListExtended instances) throws BioTMLException {
		for(int i =0; i<getBioTMLObjectWithFeaturesAndLabels().getBioTMLObjects().size(); i++)
			for(IBioTMLFeatureColumns<?> column : columns)
				getBioTMLObjectWithFeaturesAndLabels().addFeaturesToBioTMLObjectIndex(i, column.getBioTMLObjectFeatures(i));
			
		
		getInstances().addThruPipe(new Instance(getBioTMLObjectWithFeaturesAndLabels(), null, null, getDocument()));
	}


	/**
	 * 
	 * Thread safe process to add the sentence into Mallet instances.
	 * 
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void run() {
		List<String> visitedUID = new ArrayList<String>();
		List<IBioTMLFeatureColumns<?>> allColumns = new ArrayList<>();
		for(String classUID : getConfiguration().getFeaturesUIDs()){
			if(!visitedUID.contains(classUID)){
				try {
					List<IBioTMLToken> tokens = getBioTMLObjectWithFeaturesAndLabels().getTokens();
					if(getBioTMLObjectWithFeaturesAndLabels().getBioTMLObjectClass().isAssignableFrom(IBioTMLToken.class)){
						IBioTMLFeatureGenerator classProcesser = BioTMLFeaturesManager.getInstance().getNERClass(classUID);
						visitedUID.addAll(classProcesser.getNERFeatureIds());
						allColumns.add(classProcesser.getFeatureColumns(getBioTMLObjectWithFeaturesAndLabels().getTokens(),  getConfiguration()));
					}else if(getBioTMLObjectWithFeaturesAndLabels().getBioTMLObjectClass().isAssignableFrom(IBioTMLAssociation.class)){
						IBioTMLFeatureGenerator classProcesser = BioTMLFeaturesManager.getInstance().getREClass(classUID);
						visitedUID.addAll(classProcesser.getREFeatureIds());
						List<IBioTMLAssociation> associations = (List<IBioTMLAssociation>) getBioTMLObjectWithFeaturesAndLabels().getBioTMLObjects();
						allColumns.add(classProcesser.getEventFeatureColumns(tokens, associations, getConfiguration()));
					}
				} catch (BioTMLException exc) {
					exc.printStackTrace();
				}	
			}
		}
		try {
			processColumns(allColumns, getInstances());
		} catch (BioTMLException exc) {
			exc.printStackTrace();
		}	
	}



}
