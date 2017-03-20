package com.silicolife.textmining.machinelearning.biotml.core.features.modules;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.silicolife.textmining.machinelearning.biotml.core.exception.BioTMLException;
import com.silicolife.textmining.machinelearning.biotml.core.features.datastructures.BioTMLFeatureColumns;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLAssociation;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLFeatureColumns;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLFeatureGenerator;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLFeatureGeneratorConfigurator;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLToken;
import com.silicolife.textmining.machinelearning.biotml.core.nlp.nlp4j.BioTMLNLP4J;

/**
 * 
 * A class responsible for features from ClearNLP.
 * 
 * @since 1.0.0
 * @author Ruben Rodrigues ({@code rrodrigues@silicolife.com})
 */
public class NLP4JFeatures implements IBioTMLFeatureGenerator{

	/**
	 * 
	 * Initializes the insertion of features from ClearNLP.
	 * 
	 */
	public  NLP4JFeatures(){
	}

	public Set<String> getNERFeatureIds() {
		Set<String> uids = new TreeSet<String>();
		uids.add("NLP4JLEMMA");
		uids.add("NLP4JPOS");
		uids.add("NLP4JDEPENDECY");//to be corrected for RE
		//		uids.add("CLEARNLPSRL");
		uids.add("CONJUCTNLP4JLEMMA");
		uids.add("CONJUCTNLP4JPOS");
		uids.add("WINDOWNLP4JLEMMA");
		uids.add("WINDOWNLP4JPOS");
		return uids;
	}

	public Set<String> getRecomendedNERFeatureIds(){
		Set<String> uids = new TreeSet<String>();
		uids.add("NLP4JLEMMA");
		uids.add("NLP4JPOS");
		uids.add("CONJUCTNLP4JLEMMA");
		return uids;
	}

	@Override
	public Map<String, String> getNERFeatureIdsInfos() {
		Map<String, String> infoMap = new HashMap<>();
		infoMap.put("NLP4JLEMMA", "The NLP4J lemmatization system is used to create a feature that stores the lemma of each token.");
		infoMap.put("NLP4JPOS", "The NLP4J part-of-speech system is used to create a feature that stores the POS of each token.");
		infoMap.put("NLP4JDEPENDECY", "The NLP4J dependency parsing system is used to create a feature that stores the dependecy label of each token.");
		infoMap.put("CONJUCTNLP4JLEMMA", "An adaptation of conjunctions from mallet is used to create conjunctions for NLP4J lemmatization features.");
		infoMap.put("CONJUCTNLP4JPOS", "An adaptation of conjunctions from mallet is used to create conjunctions for NLP4J part-of-speech features.");
		infoMap.put("WINDOWNLP4JLEMMA", "An adaptation of windows from mallet is used to create a 'Sliding window' for NLP4J lemmatization features.");
		infoMap.put("WINDOWNLP4JPOS", "An adaptation of windows from mallet is used to create a 'Sliding window' for NLP4J part-of-speech features.");
		return infoMap;
	}
	
	@Override
	public Set<String> getREFeatureIds() {
		Set<String> uids = new TreeSet<String>();
//		uids.add("NLP4JLEMMA");
//		uids.add("NLP4JPOS");
//		
		return uids;
	}

	@Override
	public Map<String, String> getREFeatureIdsInfos() {
		Map<String, String> infoMap = new HashMap<>();
		return infoMap;
	}

	@Override
	public Set<String> getRecomendedREFeatureIds() {
		return new TreeSet<String>();
	}

	public IBioTMLFeatureColumns<IBioTMLToken> getFeatureColumns(List<IBioTMLToken> tokens, IBioTMLFeatureGeneratorConfigurator configuration) throws BioTMLException {

		if(tokens.isEmpty()){
			throw new BioTMLException(27);
		}

		IBioTMLFeatureColumns<IBioTMLToken> features = new BioTMLFeatureColumns<>(tokens, getNERFeatureIds(), configuration);


		if(configuration.hasFeatureUID("NLP4JLEMMA")){
			String[] lemmas = BioTMLNLP4J.getInstance().processLemma(tokens.toArray(new String[0]));
			for(String lemma : lemmas)
				features.addBioTMLObjectFeature("NLP4JLEMMA=" + lemma, "NLP4JLEMMA");
		}

		if(configuration.hasFeatureUID("NLP4JPOS")){
			String[] poss = BioTMLNLP4J.getInstance().processPos(tokens.toArray(new String[0]));
			for(String pos : poss)
				features.addBioTMLObjectFeature("NLP4JPOS=" + pos, "NLP4JPOS");
		}


		if(configuration.hasFeatureUID("NLP4JSRL")){
			//			features.addTokenFeature("Somthing", "NLP4JSRL");//TODO
		}

		if(configuration.hasFeatureUID("CONJUCTNLP4JLEMMA")){
			List<String> lemmas = new ArrayList<>();
			if(configuration.hasFeatureUID("NLP4JLEMMA"))
				lemmas = features.getFeatureColumByUID("NLP4JLEMMA");
			else
				lemmas = Arrays.asList(BioTMLNLP4J.getInstance().processLemma(tokens.toArray(new String[0])));
			OffsetConjunctions conjuctions = new OffsetConjunctions(lemmas,  new int[][]{{-1, 0}, {-2, -1}, {0, 1}, {-1, 1}, {-3, -1}});
			features.updateBioTMLObjectFeatures(conjuctions.generateFeatures(), "CONJUCTNLP4JLEMMA");
			features.setUIDhasMultiFeatureColumn("CONJUCTNLP4JLEMMA");
		}

		if(configuration.hasFeatureUID("CONJUCTNLP4JPOS")){
			List<String> pos = new ArrayList<>();
			if(configuration.hasFeatureUID("NLP4JPOS"))
				pos = features.getFeatureColumByUID("NLP4JPOS");
			else
				pos = Arrays.asList(BioTMLNLP4J.getInstance().processPos(tokens.toArray(new String[0])));
			OffsetConjunctions conjuctions = new OffsetConjunctions(pos,  new int[][]{{-1, 0}, {-2, -1}, {0, 1}, {-1, 1}, {-3, -1}});
			features.updateBioTMLObjectFeatures(conjuctions.generateFeatures(), "CONJUCTNLP4JPOS");
			features.setUIDhasMultiFeatureColumn("CONJUCTNLP4JPOS");
		}

		if(configuration.hasFeatureUID("WINDOWNLP4JLEMMA")){
			List<String> lemmas = new ArrayList<>();
			if(configuration.hasFeatureUID("NLP4JLEMMA"))
				lemmas = features.getFeatureColumByUID("NLP4JLEMMA");
			else
				lemmas = Arrays.asList(BioTMLNLP4J.getInstance().processLemma(tokens.toArray(new String[0])));
			WindowFeatures windows = new WindowFeatures("WINDOW_LEMMA=", lemmas, -3, 3);
			features.updateBioTMLObjectFeatures(windows.generateFeatures(), "WINDOWNLP4JLEMMA");
			features.setUIDhasMultiFeatureColumn("WINDOWNLP4JLEMMA");
		}

		if(configuration.hasFeatureUID("WINDOWNLP4JPOS")){
			List<String> pos = new ArrayList<>();
			if(configuration.hasFeatureUID("NLP4JPOS"))
				pos = features.getFeatureColumByUID("NLP4JPOS");
			else
				pos = Arrays.asList(BioTMLNLP4J.getInstance().processPos(tokens.toArray(new String[0])));
			WindowFeatures windows = new WindowFeatures("WINDOW_POS=", pos, -3, 3);
			features.updateBioTMLObjectFeatures(windows.generateFeatures(), "WINDOWNLP4JPOS");
			features.setUIDhasMultiFeatureColumn("WINDOWNLP4JPOS");
		}

		return features;
	}

	public void cleanMemory() {
		BioTMLNLP4J.getInstance().clearModelsInMemory();
	}

	@SuppressWarnings("rawtypes")
	@Override
	public IBioTMLFeatureColumns<IBioTMLAssociation> getEventFeatureColumns(List<IBioTMLToken> tokens, List<IBioTMLAssociation> associations,
			IBioTMLFeatureGeneratorConfigurator configuration) throws BioTMLException {

		IBioTMLFeatureColumns<IBioTMLAssociation> features = new BioTMLFeatureColumns<>(associations, getREFeatureIds(), configuration);
		for(IBioTMLAssociation association : associations){
			//features
		}
		
		return features;
	}

}