package com.silicolife.textmining.machinelearning.biotml.core.features.modules;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.silicolife.textmining.machinelearning.biotml.core.corpora.BioTMLOffsetsPairImpl;
import com.silicolife.textmining.machinelearning.biotml.core.exception.BioTMLException;
import com.silicolife.textmining.machinelearning.biotml.core.features.datastructures.BioTMLFeatureColumns;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLAnnotation;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLAssociation;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLFeatureColumns;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLFeatureGenerator;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLFeatureGeneratorConfigurator;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLOffsetsPair;
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
		uids.add("NLP4JLEMMA");
		uids.add("NLP4JPOS");
		uids.add("BETWEENCONTAINSNOT");
		uids.add("BETWEENVERB");
		return uids;
	}

	@Override
	public Map<String, String> getREFeatureIdsInfos() {
		Map<String, String> infoMap = new HashMap<>();
		infoMap.put("NLP4JLEMMA", "The NLP4J lemmatization system is used to create a feature that associates lemmas for event annotations.");
		infoMap.put("NLP4JPOS", "The NLP4J part-of-speech system is used to create a feature that associates POS for event annotations.");
		infoMap.put("BETWEENCONTAINSNOT", "Verifies if there is a 'not' lemma between event annotations.");
		infoMap.put("BETWEENVERB", "Gives the lemma verbs between annotation events.");
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
		
		List<String> tokenStrings = new ArrayList<>();
		for(IBioTMLToken token:tokens)
			tokenStrings.add(token.getToken());
		
		if(configuration.hasFeatureUID("NLP4JLEMMA")){
			List<String> lemmas = BioTMLNLP4J.getInstance().processLemma(tokenStrings);
			for(String lemma : lemmas)
				features.addBioTMLObjectFeature("NLP4JLEMMA=" + lemma, "NLP4JLEMMA");
		}

		if(configuration.hasFeatureUID("NLP4JPOS")){
			List<String> poss = BioTMLNLP4J.getInstance().processPos(tokenStrings);
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
				lemmas = BioTMLNLP4J.getInstance().processLemma(tokenStrings);
			OffsetConjunctions conjuctions = new OffsetConjunctions(lemmas,  new int[][]{{-1, 0}, {-2, -1}, {0, 1}, {-1, 1}, {-3, -1}});
			features.updateBioTMLObjectFeatures(conjuctions.generateFeatures(), "CONJUCTNLP4JLEMMA");
			features.setUIDhasMultiFeatureColumn("CONJUCTNLP4JLEMMA");
		}

		if(configuration.hasFeatureUID("CONJUCTNLP4JPOS")){
			List<String> pos = new ArrayList<>();
			if(configuration.hasFeatureUID("NLP4JPOS"))
				pos = features.getFeatureColumByUID("NLP4JPOS");
			else
				pos = BioTMLNLP4J.getInstance().processPos(tokenStrings);
			OffsetConjunctions conjuctions = new OffsetConjunctions(pos,  new int[][]{{-1, 0}, {-2, -1}, {0, 1}, {-1, 1}, {-3, -1}});
			features.updateBioTMLObjectFeatures(conjuctions.generateFeatures(), "CONJUCTNLP4JPOS");
			features.setUIDhasMultiFeatureColumn("CONJUCTNLP4JPOS");
		}

		if(configuration.hasFeatureUID("WINDOWNLP4JLEMMA")){
			List<String> lemmas = new ArrayList<>();
			if(configuration.hasFeatureUID("NLP4JLEMMA"))
				lemmas = features.getFeatureColumByUID("NLP4JLEMMA");
			else
				lemmas = BioTMLNLP4J.getInstance().processLemma(tokenStrings);
			WindowFeatures windows = new WindowFeatures("WINDOW_LEMMA=", lemmas, -3, 3);
			features.updateBioTMLObjectFeatures(windows.generateFeatures(), "WINDOWNLP4JLEMMA");
			features.setUIDhasMultiFeatureColumn("WINDOWNLP4JLEMMA");
		}

		if(configuration.hasFeatureUID("WINDOWNLP4JPOS")){
			List<String> pos = new ArrayList<>();
			if(configuration.hasFeatureUID("NLP4JPOS"))
				pos = features.getFeatureColumByUID("NLP4JPOS");
			else
				pos = BioTMLNLP4J.getInstance().processPos(tokenStrings);
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
		
		Collections.sort(tokens);
		List<String> tokenStrings = new ArrayList<>();
		for(IBioTMLToken token:tokens)
			tokenStrings.add(token.getToken());
		
		List<String> lemmas = BioTMLNLP4J.getInstance().processLemma(tokenStrings);
		List<String> poss = BioTMLNLP4J.getInstance().processPos(tokenStrings);
		
		for(IBioTMLAssociation association : associations){
			if(association.getEntryOne() instanceof IBioTMLAnnotation && association.getEntryTwo() instanceof IBioTMLAnnotation){
				IBioTMLAnnotation annotationOne = (IBioTMLAnnotation) association.getEntryOne();
				IBioTMLAnnotation annotationTwo = (IBioTMLAnnotation) association.getEntryTwo();
				
				if(configuration.hasFeatureUID("NLP4JLEMMA"))
					features.addBioTMLObjectFeature("NLP4JLEMMA="+addAssociatedFeature(annotationOne, annotationTwo, tokens, lemmas), "NLP4JLEMMA");
				
				if(configuration.hasFeatureUID("NLP4JPOS"))
					features.addBioTMLObjectFeature("NLP4JPOS="+addAssociatedFeature(annotationOne, annotationTwo, tokens, poss), "NLP4JPOS");
				
				if(configuration.hasFeatureUID("BETWEENCONTAINSNOT")){
					Boolean containsNot = containsNotFeature(annotationOne, annotationTwo, tokens, lemmas);
					features.addBioTMLObjectFeature("BETWEENCONTAINSNOT="+String.valueOf(containsNot), "BETWEENCONTAINSNOT");
				}
				
				if(configuration.hasFeatureUID("BETWEENVERB")){
					features.addBioTMLObjectFeature("BETWEENVERB="+String.valueOf(verbBetween(annotationOne, annotationTwo, tokens, poss, lemmas)), "BETWEENVERB");
				}
				
			}else if(association.getEntryOne() instanceof IBioTMLAnnotation && association.getEntryTwo() instanceof IBioTMLAssociation){
				//TODO
			}else if(association.getEntryOne() instanceof IBioTMLAssociation && association.getEntryTwo() instanceof IBioTMLAnnotation){
				//TODO
			}else if(association.getEntryOne() instanceof IBioTMLAssociation && association.getEntryTwo() instanceof IBioTMLAssociation){
				//TODO
			}
		}
		
		return features;
	}
	
	private String addAssociatedFeature(IBioTMLAnnotation annotationOne, IBioTMLAnnotation annotationTwo, List<IBioTMLToken> tokens, List<String> features) {
		String result = new String();
		for(int i=0; i<tokens.size(); i++){
			if(annotationOne.getAnnotationOffsets().containsInside(tokens.get(i).getTokenOffsetsPair())){
				if(!result.isEmpty())
					result = result +"__&__";
				result = result+ features.get(i);
			}
			if(annotationTwo.getAnnotationOffsets().containsInside(tokens.get(i).getTokenOffsetsPair())){
				if(!result.isEmpty())
					result = result +"__&__";
				result = result+ features.get(i);
			}
		}
		return result;
	}
	
	private Boolean containsNotFeature(IBioTMLAnnotation annotationOne, IBioTMLAnnotation annotationTwo, List<IBioTMLToken> tokens, List<String> lemmas){
		IBioTMLOffsetsPair offsetspair = null;
		if(annotationOne.compareTo(annotationTwo)<0){
			offsetspair = new BioTMLOffsetsPairImpl(annotationOne.getStartOffset(), annotationTwo.getEndOffset());
		}else{
			offsetspair = new BioTMLOffsetsPairImpl(annotationTwo.getStartOffset(), annotationOne.getEndOffset());
		}
		for(int i=0; i<tokens.size(); i++){
			if(offsetspair.containsInside(tokens.get(i).getTokenOffsetsPair()) && lemmas.get(i).equals("not")){
				return true;
			}
		}
		return false;

	}
	
	private String verbBetween(IBioTMLAnnotation annotationOne, IBioTMLAnnotation annotationTwo, List<IBioTMLToken> tokens, List<String> pos, List<String> lemmas){
		String verbString = new String();
		IBioTMLOffsetsPair offsetspair = null;
		if(annotationOne.compareTo(annotationTwo)<0){
			offsetspair = new BioTMLOffsetsPairImpl(annotationOne.getStartOffset(), annotationTwo.getEndOffset());
		}else{
			offsetspair = new BioTMLOffsetsPairImpl(annotationTwo.getStartOffset(), annotationOne.getEndOffset());
		}
		for(int i=0; i<tokens.size(); i++){
			if(offsetspair.containsInside(tokens.get(i).getTokenOffsetsPair()) && pos.get(i).startsWith("VB")){
				if(!verbString.isEmpty())
					verbString = verbString + " ";
				verbString = verbString + lemmas.get(i);
			}
		}
		return verbString;

	}

}