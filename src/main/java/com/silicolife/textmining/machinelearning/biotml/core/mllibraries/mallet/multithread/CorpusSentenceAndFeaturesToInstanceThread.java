package com.silicolife.textmining.machinelearning.biotml.core.mllibraries.mallet.multithread;

import java.util.ArrayList;
import java.util.List;

import com.silicolife.textmining.machinelearning.biotml.core.corpora.otherdatastructures.BioTMLDocSentTokenIDs;
import com.silicolife.textmining.machinelearning.biotml.core.corpora.otherdatastructures.BioTMLTokensWithFeaturesAndLabels;
import com.silicolife.textmining.machinelearning.biotml.core.exception.BioTMLException;
import com.silicolife.textmining.machinelearning.biotml.core.features.BioTMLFeaturesManager;
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

	private BioTMLDocSentTokenIDs docIDandSentIdx;
	private BioTMLTokensWithFeaturesAndLabels tokensWithFeaturesAndLabels;
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
	public CorpusSentenceAndFeaturesToInstanceThread(BioTMLDocSentTokenIDs docIDandSentIdx, BioTMLTokensWithFeaturesAndLabels tokensWithFeaturesAndLabels, InstanceListExtended instances,  IBioTMLFeatureGeneratorConfigurator configuration){
		this.docIDandSentIdx = docIDandSentIdx;
		this.tokensWithFeaturesAndLabels = tokensWithFeaturesAndLabels;
		this.instances = instances;
		this.configuration = configuration;
	}

	private BioTMLDocSentTokenIDs getDocIDandSentIdx(){
		return docIDandSentIdx;
	}

	private BioTMLTokensWithFeaturesAndLabels getTokensWithFeaturesAndLabels(){
		return tokensWithFeaturesAndLabels;
	}

	private InstanceListExtended getInstances(){
		return instances;
	}

	private IBioTMLFeatureGeneratorConfigurator getConfiguration(){
		return configuration;
	}

	private void processColumns(List<IBioTMLFeatureColumns> columns, InstanceListExtended instances) throws BioTMLException {
		for(int i =0; i<getTokensWithFeaturesAndLabels().getTokens().size(); i++){
			for(IBioTMLFeatureColumns column : columns){
				getTokensWithFeaturesAndLabels().addFeaturesToTokenIndex(i, column.getTokenFeatures(i));
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
					if(getDocIDandSentIdx().getAnnotTokenRelationStartIndex() != -1 && getDocIDandSentIdx().getAnnotTokenRelationEndIndex() != -1){
						allColumns.add(classProcesser.getFeatureColumnsForRelations(getTokensWithFeaturesAndLabels().getTokens(), getDocIDandSentIdx().getAnnotTokenRelationStartIndex(), getDocIDandSentIdx().getAnnotTokenRelationEndIndex(),  getConfiguration()));
					}else{
						allColumns.add(classProcesser.getFeatureColumns(getTokensWithFeaturesAndLabels().getTokens(),  getConfiguration()));
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
