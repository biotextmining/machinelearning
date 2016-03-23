package com.silicolife.textmining.machinelearning.biotml.core.features.modules;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.silicolife.textmining.machinelearning.biotml.core.exception.BioTMLException;
import com.silicolife.textmining.machinelearning.biotml.core.features.datastructures.BioTMLAssociationProcess;
import com.silicolife.textmining.machinelearning.biotml.core.features.datastructures.BioTMLFeatureColumns;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLFeatureColumns;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLFeatureGenerator;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLFeatureGeneratorConfigurator;
import com.silicolife.textmining.machinelearning.biotml.core.nlp.opennlp.BioTMLOpenNLP;

/**
 * 
 * A class responsible for features from OpenNLP.
 * 
 * @since 1.0.0
 * @author Ruben Rodrigues ({@code rrodrigues@silicolife.com})
 */

public class OpenNLPFeatures implements IBioTMLFeatureGenerator{

	/**
	 * 
	 * Initializes the insertion of features from OpenNLP.
	 * 
	 */
	public  OpenNLPFeatures(){
	}

	public Set<String> getUIDs() {
		Set<String> uids = new TreeSet<String>();
		uids.add("OPENNLPPOS");
		uids.add("OPENNLPCHUNK");
		uids.add("OPENNLPCHUNKPARSING");//to be corrected for RE
		uids.add("CONJUCTOPENNLPCHUNK");
		uids.add("CONJUCTOPENNLPPOS");
		uids.add("WINDOWOPENNLPCHUNK");
		uids.add("WINDOWOPENNLPPOS");
		return uids;
	}
	
	public Set<String> getRecomendedUIDs() {
		Set<String> uids = new TreeSet<String>();
		uids.add("OPENNLPPOS");
		uids.add("OPENNLPCHUNK");
		return uids;
	}
	
	public Map<String, String> getUIDInfos() {
		Map<String, String> infoMap = new HashMap<>();
		infoMap.put("OPENNLPPOS", "The OpenNLP part-of-speech system is used to create a feature that stores the POS of each token.");
		infoMap.put("OPENNLPCHUNK", "The OpenNLP chunking system is used to create a feature that stores the chunk of each token.");
		infoMap.put("OPENNLPCHUNKPARSING",  "The OpenNLP parsing system is used to create a feature that stores the a path of chunks from sentence root into token chunk.");
		infoMap.put("CONJUCTOPENNLPCHUNK", "An adaptation of conjunctions from mallet is used to create conjunctions for OpenNLP chunking features.");
		infoMap.put("CONJUCTOPENNLPPOS", "An adaptation of conjunctions from mallet is used to create conjunctions for OpenNLP part-of-speech features.");
		infoMap.put("WINDOWOPENNLPCHUNK", "An adaptation of windows from mallet is used to create 'Sliding window' for OpenNLP chunking features.");
		infoMap.put("WINDOWOPENNLPPOS", "An adaptation of windows from mallet is used to create 'Sliding window' for OpenNLP part-of-speech features.");
		return infoMap;
	}


	public IBioTMLFeatureColumns getFeatureColumns(List<String> tokensToProcess, IBioTMLFeatureGeneratorConfigurator configuration) throws BioTMLException {

		if(tokensToProcess.isEmpty()){
			throw new BioTMLException(27);
		}
		
		BioTMLAssociationProcess tokenAnnotProcess = new BioTMLAssociationProcess(tokensToProcess);
		List<String> tokens = tokenAnnotProcess.getTokens();
		IBioTMLFeatureColumns features = new BioTMLFeatureColumns(tokens, getUIDs(), configuration);

		String[] sentence = tokens.toArray(new String[0]);
		String[] posTags = {new String()};
		String[] chunckTags = {new String()};
		String[] chunckParsingTags = {new String()};

		if(configuration.hasFeatureUID("OPENNLPPOS") ||
				configuration.hasFeatureUID("WINDOWOPENNLPPOS") ||
				configuration.hasFeatureUID("CONJUCTOPENNLPPOS")){
			posTags = BioTMLOpenNLP.getInstance().processPos(sentence);
			if(configuration.hasFeatureUID("OPENNLPCHUNK") ||
					configuration.hasFeatureUID("WINDOWOPENNLPCHUNK") ||
					configuration.hasFeatureUID("CONJUCTOPENNLPCHUNK"))
				chunckTags = BioTMLOpenNLP.getInstance().processChunking(sentence, posTags);
		}else{
			if(configuration.hasFeatureUID("OPENNLPCHUNK") ||
					configuration.hasFeatureUID("WINDOWOPENNLPCHUNK") ||
					configuration.hasFeatureUID("CONJUCTOPENNLPCHUNK"))
				chunckTags = BioTMLOpenNLP.getInstance().processChunking(sentence,  BioTMLOpenNLP.getInstance().processPos(sentence));
		}
		if(configuration.hasFeatureUID("OPENNLPCHUNKPARSING")){
			chunckParsingTags = BioTMLOpenNLP.getInstance().processChunkingParsing(sentence);
			features.setUIDhasMultiFeatureColumn("OPENNLPCHUNKPARSING");
		}
			

		for(int i=0; i<sentence.length; i++){
			if(configuration.hasFeatureUID("OPENNLPPOS") ||
					configuration.hasFeatureUID("WINDOWOPENNLPPOS") ||
					configuration.hasFeatureUID("CONJUCTOPENNLPPOS")){
				features.addTokenFeature("OPENNLPPOS="+posTags[i], "OPENNLPPOS");
			}
			if(configuration.hasFeatureUID("OPENNLPCHUNK") ||
					configuration.hasFeatureUID("WINDOWOPENNLPCHUNK") ||
					configuration.hasFeatureUID("CONJUCTOPENNLPCHUNK")){
				features.addTokenFeature("OPENNLPCHUNK="+chunckTags[i], "OPENNLPCHUNK");
			}
			if(configuration.hasFeatureUID("OPENNLPCHUNKPARSING")){
				features.addTokenFeature(chunckParsingTags[i], "OPENNLPCHUNKPARSING");
			}
		}
		
		if(configuration.hasFeatureUID("CONJUCTOPENNLPCHUNK")){
			OffsetConjunctions conjuctions = new OffsetConjunctions(features.getFeatureColumByUID("OPENNLPCHUNK"),  new int[][]{{-1, 0}, {-2, -1}, {0, 1}, {-1, 1}, {-3, -1}});
			features.updateTokenFeatures(conjuctions.generateFeatures(), "CONJUCTOPENNLPCHUNK");
			features.setUIDhasMultiFeatureColumn("CONJUCTOPENNLPCHUNK");
		}
		
		if(configuration.hasFeatureUID("CONJUCTOPENNLPPOS")){
			OffsetConjunctions conjuctions = new OffsetConjunctions(features.getFeatureColumByUID("OPENNLPPOS"),  new int[][]{{-1, 0}, {-2, -1}, {0, 1}, {-1, 1}, {-3, -1}});
			features.updateTokenFeatures(conjuctions.generateFeatures(), "CONJUCTOPENNLPPOS");
			features.setUIDhasMultiFeatureColumn("CONJUCTOPENNLPPOS");
		}
		
		if(configuration.hasFeatureUID("WINDOWOPENNLPCHUNK")){
			WindowFeatures windows = new WindowFeatures("WINDOW_CHUNK=", features.getFeatureColumByUID("OPENNLPCHUNK"), -3, 3);
			features.updateTokenFeatures(windows.generateFeatures(), "WINDOWOPENNLPCHUNK");
			features.setUIDhasMultiFeatureColumn("WINDOWOPENNLPCHUNK");
		}
		
		if(configuration.hasFeatureUID("WINDOWOPENNLPPOS")){
			WindowFeatures windows = new WindowFeatures("WINDOW_POS=", features.getFeatureColumByUID("OPENNLPPOS"), -3, 3);
			features.updateTokenFeatures(windows.generateFeatures(), "WINDOWOPENNLPPOS");
			features.setUIDhasMultiFeatureColumn("WINDOWOPENNLPPOS");
		}
		
		features.updateTokenFeaturesUsingAssociationProcess(tokenAnnotProcess);

		return features;
	}

	public void cleanMemory() {
		BioTMLOpenNLP.getInstance().clearModelsInMemory();
	}

}