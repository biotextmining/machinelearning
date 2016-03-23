package com.silicolife.textmining.machinelearning.biotml.core.mllibraries.mallet.multithread;

import java.util.ArrayList;
import java.util.List;

import com.silicolife.textmining.machinelearning.biotml.core.BioTMLConstants;
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
	
	private String docIDandSentIdx;
	private String sentence;
	private InstanceListExtended instances;
	private IBioTMLFeatureGeneratorConfigurator configuration;
	private List<String> tokenStrings;
	private List<String> isOrNotToAdd;
	private List<String> tokenLabel;

	/**
	 * 
	 * Initializes a thread with a sentence to be converted into Mallet instance.
	 * 
	 * @param docIDandSentIdx - String that identifies the document ID and sentence index.
	 * @param sentence - Sentence string.
	 * @param instances - InstanceList with thread safety to be populated with all sentences.
	 */
	public CorpusSentenceAndFeaturesToInstanceThread(String docIDandSentIdx, String sentence, InstanceListExtended instances,  IBioTMLFeatureGeneratorConfigurator configuration){
		this.docIDandSentIdx = docIDandSentIdx;
		this.sentence = sentence;
		this.instances = instances;
		this.configuration = configuration;
		processTokenStrings();
	}
	
	private String getDocIDandSentIdx(){
		return docIDandSentIdx;
	}
	
	private String getSentence(){
		return sentence;
	}

	private InstanceListExtended getInstances(){
		return instances;
	}
	
	private IBioTMLFeatureGeneratorConfigurator getConfiguration(){
		return configuration;
	}
	
	private List<String> getTokenStrings(){
		return tokenStrings;
	}
	
	private List<String> getIsOrNotToAdd(){
		return isOrNotToAdd;
	}
	
	private List<String> getTokenLabel(){
		return tokenLabel;
	}
	
	private void processTokenStrings(){
		tokenStrings = new ArrayList<>();
		isOrNotToAdd = new ArrayList<>();
		tokenLabel = new ArrayList<>();
		String[] tokensWithLabels = getSentence().split("\n");
		for(String tokenlabeled : tokensWithLabels){
			String[] column = tokenlabeled.split("\t");
			if(column.length>1){
				if( !column[1].equals(BioTMLConstants.b.toString())
						&& !column[1].equals(BioTMLConstants.i.toString())
						&& !column[1].equals(BioTMLConstants.o.toString())
						&& !column[1].equals("INANNOT")
						&&!column[1].equals("OUTANNOT")){
					tokenStrings.add(column[0]+"\t"+column[1]);
				}else{
					tokenStrings.add(column[0]);
				}
				if(column.length>2){
					if( column[2].equals("INANNOT")
							|| column[2].equals("OUTANNOT")){
						isOrNotToAdd.add(column[2]);
					}
				}
				if(column[column.length-1].equals(BioTMLConstants.b.toString())
						|| column[column.length-1].equals(BioTMLConstants.i.toString())
						|| column[column.length-1].equals(BioTMLConstants.o.toString())){
					tokenLabel.add(column[column.length-1]);
				}
			}else{
				tokenStrings.add(column[0]);
			}
		}
	}

	
	private void processColumns(List<IBioTMLFeatureColumns> columns, InstanceListExtended instances) {
		StringBuilder sentenceProcessed = new StringBuilder();
		for(int i =0; i<getTokenStrings().size(); i++){
			if(getIsOrNotToAdd().isEmpty() || getIsOrNotToAdd().get(i).equals("INANNOT")){
				sentenceProcessed.append(getTokenStrings().get(i));
				sentenceProcessed.append("\t");
				List<String> features = new ArrayList<>();
				for(IBioTMLFeatureColumns column : columns){
					features.addAll(column.getTokenFeatures(i));
				}
				for(String feature : features){
					sentenceProcessed.append(feature);
					sentenceProcessed.append("\t");
				}
				if(getTokenLabel().size()>i){
					sentenceProcessed.append(getTokenLabel().get(i));
				}
				sentenceProcessed.append("\n");
			}

		}
		getInstances().addThruPipe(new Instance(sentenceProcessed.toString(), null, getDocIDandSentIdx(), sentenceProcessed.toString()));
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
    				allColumns.add(classProcesser.getFeatureColumns(getTokenStrings(),  getConfiguration()));
    			} catch (BioTMLException exc) {
    				exc.printStackTrace();
    			}	
    		}
    	}
    	processColumns(allColumns, getInstances());
	}



}
