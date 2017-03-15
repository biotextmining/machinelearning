package com.silicolife.textmining.machinelearning.biotml.core.mllibraries.mallet.multithread;

import java.util.ArrayList;
import java.util.List;

import com.silicolife.textmining.machinelearning.biotml.core.corpora.otherdatastructures.BioTMLDocSentIDs;
import com.silicolife.textmining.machinelearning.biotml.core.corpora.otherdatastructures.BioTMLObjectWithFeaturesAndLabels;
import com.silicolife.textmining.machinelearning.biotml.core.exception.BioTMLException;
import com.silicolife.textmining.machinelearning.biotml.core.features.BioTMLFeaturesManager;
import com.silicolife.textmining.machinelearning.biotml.core.features.datastructures.BioTMLAssociationProcess;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLAssociation;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLFeatureColumns;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLFeatureGenerator;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLFeatureGeneratorConfigurator;

import cc.mallet.types.Instance;

/**
 * 
 * A class responsible to convert a sentence string into a Mallet instance using a multi-threaded system.
 * 
 * @since 1.0.0
 * @author Ruben Rodrigues ({@code rrodrigues@silicolife.com})
 */

public class CorpusSentenceAndFeaturesToInstanceThread implements Runnable{

	private BioTMLDocSentIDs docIDandSentIdx;
	private BioTMLObjectWithFeaturesAndLabels<?> tokensWithFeaturesAndLabels;
	private InstanceListExtended instances;
	private IBioTMLFeatureGeneratorConfigurator configuration;

	/**
	 * 
	 * Initializes a thread with a sentence to be converted into Mallet instance.
	 * 
	 * @param docIDandSentIdx - String that identifies the document ID and sentence index.
	 * @param tokensWithLabels - Sentence string.
	 * @param instances - InstanceList with thread safety to be populated with all sentences.
	 */
	public CorpusSentenceAndFeaturesToInstanceThread(BioTMLDocSentIDs docIDandSentIdx, BioTMLObjectWithFeaturesAndLabels<?> tokensWithFeaturesAndLabels, InstanceListExtended instances,  IBioTMLFeatureGeneratorConfigurator configuration){
		this.docIDandSentIdx = docIDandSentIdx;
		this.tokensWithFeaturesAndLabels = tokensWithFeaturesAndLabels;
		this.instances = instances;
		this.configuration = configuration;
	}

	private BioTMLDocSentIDs getDocIDandSentIdx(){
		return docIDandSentIdx;
	}

	private BioTMLObjectWithFeaturesAndLabels<?> getTokensWithFeaturesAndLabels(){
		return tokensWithFeaturesAndLabels;
	}

	private InstanceListExtended getInstances(){
		return instances;
	}

	private IBioTMLFeatureGeneratorConfigurator getConfiguration(){
		return configuration;
	}

	private void processColumns(List<IBioTMLFeatureColumns> columns, InstanceListExtended instances) throws BioTMLException {
		for(int i =0; i<getTokensWithFeaturesAndLabels().getBioTMLObjects().size(); i++){
			for(IBioTMLFeatureColumns column : columns){
				getTokensWithFeaturesAndLabels().addFeaturesToBioTMLObjectIndex(i, column.getTokenFeatures(i));
			}
		}
		getInstances().addThruPipe(new Instance(getTokensWithFeaturesAndLabels(), null, getDocIDandSentIdx(), null));
	}


	/**
	 * 
	 * Thread safe process to add the sentence into Mallet instances.
	 * 
	 */
	public void run() {
		List<String> visitedUID = new ArrayList<String>();
		List<IBioTMLFeatureColumns> allColumns = new ArrayList<>();
		for(String classUID : getConfiguration().getFeaturesUIDs()){
			if(!visitedUID.contains(classUID)){
				try {
					IBioTMLFeatureGenerator classProcesser = BioTMLFeaturesManager.getInstance().getClass(classUID);
					visitedUID.addAll(classProcesser.getUIDs());
					
					if(getTokensWithFeaturesAndLabels().getBioTMLObjectClass().isInstance(String.class)){
						List<String> tokens = (List<String>) getTokensWithFeaturesAndLabels().getBioTMLObjects();
						if(getDocIDandSentIdx().getAnnotTokenRelationStartIndex() != -1 && getDocIDandSentIdx().getAnnotTokenRelationEndIndex() != -1){
							BioTMLAssociationProcess tokenAnnotProcess = new BioTMLAssociationProcess(tokens, getDocIDandSentIdx().getAnnotTokenRelationStartIndex(), getDocIDandSentIdx().getAnnotTokenRelationEndIndex());
							IBioTMLFeatureColumns features = classProcesser.getFeatureColumns(tokens, getConfiguration());
							features.updateTokenFeaturesUsingAssociationProcess(tokenAnnotProcess);
							allColumns.add(features);
						}else{
							allColumns.add(classProcesser.getFeatureColumns(tokens,  getConfiguration()));
						}
					}else if(getTokensWithFeaturesAndLabels().getBioTMLObjectClass().isInstance(IBioTMLAssociation.class)){
						
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
