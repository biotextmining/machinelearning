package com.silicolife.textmining.machinelearning.biotml.core.features.modules;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.clearnlp.dependency.DEPTree;
import com.clearnlp.nlp.NLPGetter;
import com.silicolife.textmining.machinelearning.biotml.core.exception.BioTMLException;
import com.silicolife.textmining.machinelearning.biotml.core.features.datastructures.BioTMLAssociationProcess;
import com.silicolife.textmining.machinelearning.biotml.core.features.datastructures.BioTMLFeatureColumns;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLFeatureColumns;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLFeatureGenerator;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLFeatureGeneratorConfigurator;
import com.silicolife.textmining.machinelearning.biotml.core.nlp.clearnlp.BioTMLClearNLP;

/**
 * 
 * A class responsible for features from ClearNLP.
 * 
 * @since 1.0.0
 * @author Ruben Rodrigues ({@code rrodrigues@silicolife.com})
 */
public class ClearNLPFeatures implements IBioTMLFeatureGenerator{

	/**
	 * 
	 * Initializes the insertion of features from ClearNLP.
	 * 
	 */
	public  ClearNLPFeatures(){
	}

	public Set<String> getUIDs() {
		Set<String> uids = new TreeSet<String>();
		uids.add("CLEARNLPLEMMA");
		uids.add("CLEARNLPPOS");
		uids.add("CLEARNLPDEPENDECY");//to be corrected for RE
		//		uids.add("CLEARNLPSRL");
		uids.add("CONJUCTCLEARNLPLEMMA");
		uids.add("CONJUCTCLEARNLPPOS");
		uids.add("WINDOWCLEARNLPLEMMA");
		uids.add("WINDOWCLEARNLPPOS");
		return uids;
	}
	
	public Set<String> getRecomendedUIDs(){
		Set<String> uids = new TreeSet<String>();
		uids.add("CLEARNLPLEMMA");
		uids.add("CLEARNLPPOS");
		uids.add("CONJUCTCLEARNLPLEMMA");
		return uids;
	}
	
	@Override
	public Map<String, String> getUIDInfos() {
		Map<String, String> infoMap = new HashMap<>();
		infoMap.put("CLEARNLPLEMMA", "The ClearNLP lemmatization system is used to create a feature that stores the lemma of each token.");
		infoMap.put("CLEARNLPPOS", "The ClearNLP part-of-speech system is used to create a feature that stores the POS of each token.");
		infoMap.put("CLEARNLPDEPENDECY", "The ClearNLP dependency parsing system is used to create a feature that stores the dependecy label of each token.");
		infoMap.put("CONJUCTCLEARNLPLEMMA", "An adaptation of conjunctions from mallet is used to create conjunctions for ClearNLP lemmatization features.");
		infoMap.put("CONJUCTCLEARNLPPOS", "An adaptation of conjunctions from mallet is used to create conjunctions for ClearNLP part-of-speech features.");
		infoMap.put("WINDOWCLEARNLPLEMMA", "An adaptation of windows from mallet is used to create a 'Sliding window' for ClearNLP lemmatization features.");
		infoMap.put("WINDOWCLEARNLPPOS", "An adaptation of windows from mallet is used to create a 'Sliding window' for ClearNLP part-of-speech features.");
		return infoMap;
	}

	private String[] executeClearNLP(List<String> tokens, IBioTMLFeatureGeneratorConfigurator configuration) throws BioTMLException{

		DEPTree tree = NLPGetter.toDEPTree(tokens);

		//The mode POS must be the first process in ClearNLP
		BioTMLClearNLP.getInstance().processPos(tree);

		if(configuration.hasFeatureUID("CLEARNLPDEPENDECY") || configuration.hasFeatureUID("CLEARNLPSRL")){
			//After POS processing the ClearNLP applies the DEP parsing
			BioTMLClearNLP.getInstance().processDependency(tree);

			if(configuration.hasFeatureUID("CLEARNLPSRL")){
				//With DEP parsing, ClearNLP applies Semantic Role parsing
				BioTMLClearNLP.getInstance().processSRL(tree);		
			}
		}

		if(configuration.hasFeatureUID("CLEARNLPSRL")){
			return tree.toStringSRL().split("\n");
		}
		return tree.toStringDEP().split("\n");

	}

	private String recursiveStringDependency(String[] tokenLines, Integer depInt, String depTag){
		if(depTag.equals("root")){
			return "";
		}else{
			String depLine = tokenLines[depInt];
			String[] depfeat = depLine.split("\t");
			return depTag.toUpperCase()+"_OF=" + depfeat[2] + "\t" + recursiveStringDependency(tokenLines, Integer.valueOf(depfeat[5])-1, depfeat[6]);
		}
	}

	public IBioTMLFeatureColumns getFeatureColumnsForRelations(List<String> tokensToProcess, int startAnnotationIndex, int endAnnotationIndex, IBioTMLFeatureGeneratorConfigurator configuration) throws BioTMLException {
		BioTMLAssociationProcess tokenAnnotProcess = new BioTMLAssociationProcess(tokensToProcess, startAnnotationIndex, endAnnotationIndex);
		List<String> tokens = tokenAnnotProcess.getTokens();
		IBioTMLFeatureColumns features = getFeatureColumns(tokens, configuration);
		features.updateTokenFeaturesUsingAssociationProcess(tokenAnnotProcess);
		return features;
	}


	public IBioTMLFeatureColumns getFeatureColumns(List<String> tokens, IBioTMLFeatureGeneratorConfigurator configuration) throws BioTMLException {
		
		if(tokens.isEmpty()){
			throw new BioTMLException(27);
		}
		
		IBioTMLFeatureColumns features = new BioTMLFeatureColumns(tokens, getUIDs(), configuration);

		String[] tokenLines = executeClearNLP(tokens, configuration);

		for (int i = 0; i < tokens.size(); i++){
			String treeRes = tokenLines[i];
			String[] featureStrings = treeRes.split("\t");
			features.addTokenFeature("CLEARNLPLEMMA=" + featureStrings[2], "CLEARNLPLEMMA");
			features.addTokenFeature("CLEARNLPPOS="  + featureStrings[3], "CLEARNLPPOS");
			if(configuration.hasFeatureUID("CLEARNLPDEPENDECY")){
				features.addTokenFeature(recursiveStringDependency(tokenLines, Integer.valueOf(featureStrings[5])-1, featureStrings[6]), "CLEARNLPDEPENDECY");
				features.setUIDhasMultiFeatureColumn("CLEARNLPDEPENDECY");
			}
			if(configuration.hasFeatureUID("CLEARNLPSRL")){
				features.addTokenFeature("Somthing", "CLEARNLPSRL");//TODO
			}
		}

		if(configuration.hasFeatureUID("CONJUCTCLEARNLPLEMMA")){
			OffsetConjunctions conjuctions = new OffsetConjunctions(features.getFeatureColumByUID("CLEARNLPLEMMA"),  new int[][]{{-1, 0}, {-2, -1}, {0, 1}, {-1, 1}, {-3, -1}});
			features.updateTokenFeatures(conjuctions.generateFeatures(), "CONJUCTCLEARNLPLEMMA");
			features.setUIDhasMultiFeatureColumn("CONJUCTCLEARNLPLEMMA");
		}

		if(configuration.hasFeatureUID("CONJUCTCLEARNLPPOS")){
			OffsetConjunctions conjuctions = new OffsetConjunctions(features.getFeatureColumByUID("CLEARNLPPOS"),  new int[][]{{-1, 0}, {-2, -1}, {0, 1}, {-1, 1}, {-3, -1}});
			features.updateTokenFeatures(conjuctions.generateFeatures(), "CONJUCTCLEARNLPPOS");
			features.setUIDhasMultiFeatureColumn("CONJUCTCLEARNLPPOS");
		}

		if(configuration.hasFeatureUID("WINDOWCLEARNLPLEMMA")){
			WindowFeatures windows = new WindowFeatures("WINDOW_LEMMA=", features.getFeatureColumByUID("CLEARNLPLEMMA"), -3, 3);
			features.updateTokenFeatures(windows.generateFeatures(), "WINDOWCLEARNLPLEMMA");
			features.setUIDhasMultiFeatureColumn("WINDOWCLEARNLPLEMMA");
		}
		
		if(configuration.hasFeatureUID("WINDOWCLEARNLPPOS")){
			WindowFeatures windows = new WindowFeatures("WINDOW_POS=", features.getFeatureColumByUID("CLEARNLPPOS"), -3, 3);
			features.updateTokenFeatures(windows.generateFeatures(), "WINDOWCLEARNLPPOS");
			features.setUIDhasMultiFeatureColumn("WINDOWCLEARNLPPOS");
		}
		
		return features;
	}

	public void cleanMemory() {
		BioTMLClearNLP.getInstance().clearModelsInMemory();
	}

}