package com.silicolife.textmining.machinelearning.biotml.core.features.modules;

import java.io.IOException;
import java.util.Arrays;
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
import com.silicolife.textmining.machinelearning.biotml.core.resources.datalists.BioTMLDataLists;

public class DictionariesFeature implements IBioTMLFeatureGenerator{
	
	public Set<String> getNERFeatureIds() {
		Set<String> uids = new TreeSet<String>();
		uids.add("INCHEMICALLIST");
		uids.add("INFREQUENTLIST");
		uids.add("INCLUESLIST");
		return uids;
	}

	public Set<String> getRecomendedNERFeatureIds(){
		return new TreeSet<String>();
	}

	public Map<String, String> getNERFeatureIdsInfos() {
		Map<String, String> infoMap = new HashMap<>();
		infoMap.put("INCHEMICALLIST", "Verifies if the token is present in the BioTML chemical list.");
		infoMap.put("INFREQUENTLIST", "Verifies if the token is present in the 10000 most frequent words.");
		infoMap.put("INCLUESLIST", "Verifies if the token is present in clues list.");
		return infoMap;
	}


	@Override
	public Set<String> getREFeatureIds() {
		Set<String> uids = new TreeSet<String>();
		uids.add("GETPOSITIVEWORDSBETWEENANNOTS");
		uids.add("GETNEGATIVEWORDSBETWEENANNOTS");
		uids.add("GETPOSITIVEWORDSOUTSIDEANNOTS");
		uids.add("GETNEGATIVEWORDSOUTSIDEANNOTS");
		return uids;
	}

	@Override
	public Map<String, String> getREFeatureIdsInfos() {
		Map<String, String> infoMap = new HashMap<>();
		infoMap.put("GETPOSITIVEWORDSBETWEENANNOTS", "Gives the positive words that are in sentence between annotation events.");
		infoMap.put("GETNEGATIVEWORDSBETWEENANNOTS", "Gives the negative words that are in sentence between annotation events.");
		infoMap.put("GETPOSITIVEWORDSOUTSIDEANNOTS", "Gives the positive words that are in sentence outside annotation events.");
		infoMap.put("GETNEGATIVEWORDSOUTSIDEANNOTS", "Gives the negative words that are in sentence outside annotation events.");
		return infoMap;
	}

	@Override
	public Set<String> getRecomendedREFeatureIds() {
		return new TreeSet<String>();
	}

	public IBioTMLFeatureColumns<IBioTMLToken> getFeatureColumns(List<IBioTMLToken> tokens,
			IBioTMLFeatureGeneratorConfigurator configuration)
					throws BioTMLException {

		IBioTMLFeatureColumns<IBioTMLToken> features = new BioTMLFeatureColumns<>(tokens, getNERFeatureIds(), configuration);

		for (int i = 0; i < tokens.size(); i++){
			IBioTMLToken token = tokens.get(i);
			for(String uID : getNERFeatureIds()){
				if(configuration.hasFeatureUID(uID)){
					features.addBioTMLObjectFeature(isInDataList(token.getToken(), uID), uID);
				}
			}		
		}

		return features;
	}

	public void cleanMemory(){
	}

	private String isInDataList(String token, String uid) throws BioTMLException {
		try{
			if(uid.equals("INCHEMICALLIST")){
				if(BioTMLDataLists.getInstance().findStringInChemicalList(token)){
					return uid;
				}
			}
			if(uid.equals("INFREQUENTLIST")){
				if(BioTMLDataLists.getInstance().findStringInFrequentList(token)){
					return uid;
				}
			}
			if(uid.equals("INCLUESLIST")){
				if(BioTMLDataLists.getInstance().findStringInClueList(token)){
					return uid;
				}
			}
		}catch(IOException|ClassNotFoundException exc){
			throw new BioTMLException(exc);
		}
		return new String();
	}

	@SuppressWarnings("rawtypes")
	@Override
	public IBioTMLFeatureColumns<IBioTMLAssociation> getEventFeatureColumns(List<IBioTMLToken> tokens, List<IBioTMLAssociation> associations,
			IBioTMLFeatureGeneratorConfigurator configuration) throws BioTMLException {

		IBioTMLFeatureColumns<IBioTMLAssociation> features = new BioTMLFeatureColumns<>(associations, getREFeatureIds(), configuration);
		features.setUIDhasMultiFeatureColumn("GETPOSITIVEWORDSBETWEENANNOTS");
		features.setUIDhasMultiFeatureColumn("GETNEGATIVEWORDSBETWEENANNOTS");
		features.setUIDhasMultiFeatureColumn("GETPOSITIVEWORDSOUTSIDEANNOTS");
		features.setUIDhasMultiFeatureColumn("GETNEGATIVEWORDSOUTSIDEANNOTS");
		for(IBioTMLAssociation association : associations){
			if(association.getEntryOne() instanceof IBioTMLAnnotation && association.getEntryTwo() instanceof IBioTMLAnnotation){
				IBioTMLAnnotation annotationOne = (IBioTMLAnnotation) association.getEntryOne();
				IBioTMLAnnotation annotationTwo = (IBioTMLAnnotation) association.getEntryTwo();

				if(configuration.hasFeatureUID("GETPOSITIVEWORDSBETWEENANNOTS"))
					features.addBioTMLObjectFeature(getTabbedTokensInDictForAnnots(annotationOne, annotationTwo, tokens, "GETPOSITIVEWORDSBETWEENANNOTS"), "GETPOSITIVEWORDSBETWEENANNOTS");

				if(configuration.hasFeatureUID("GETNEGATIVEWORDSBETWEENANNOTS"))
					features.addBioTMLObjectFeature(getTabbedTokensInDictForAnnots(annotationOne, annotationTwo, tokens, "GETNEGATIVEWORDSBETWEENANNOTS"), "GETNEGATIVEWORDSBETWEENANNOTS");

				if(configuration.hasFeatureUID("GETPOSITIVEWORDSOUTSIDEANNOTS"))
					features.addBioTMLObjectFeature(getTabbedTokensInDictForAnnots(annotationOne, annotationTwo, tokens, "GETPOSITIVEWORDSOUTSIDEANNOTS"), "GETPOSITIVEWORDSOUTSIDEANNOTS");

				if(configuration.hasFeatureUID("GETNEGATIVEWORDSOUTSIDEANNOTS"))
					features.addBioTMLObjectFeature(getTabbedTokensInDictForAnnots(annotationOne, annotationTwo, tokens, "GETNEGATIVEWORDSOUTSIDEANNOTS"), "GETNEGATIVEWORDSOUTSIDEANNOTS");

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

	private String getTabbedTokensInDictForAnnots(IBioTMLAnnotation annotationOne, IBioTMLAnnotation annotationTwo, List<IBioTMLToken> tokens, String featureUID) throws BioTMLException{
		String result = new String();
		try{
			IBioTMLOffsetsPair annotationsOffsets = null;
			if(annotationOne.compareTo(annotationTwo)<0)
				annotationsOffsets = new BioTMLOffsetsPairImpl(annotationOne.getStartOffset(), annotationTwo.getEndOffset());
			else
				annotationsOffsets = new BioTMLOffsetsPairImpl(annotationTwo.getStartOffset(), annotationOne.getEndOffset());

			String[] breakers = {"that", "who", "which", "because"};
			List<String> breakersList = Arrays.asList(breakers);

			IBioTMLToken foundbreaker = null;

			for(IBioTMLToken token : tokens){
				if(!result.isEmpty())
					result = result + "\t";
				if(breakersList.contains(token.getToken().toLowerCase()))
					foundbreaker = token;

				if(annotationsOffsets.offsetsOverlap(token.getTokenOffsetsPair()) 
						&& featureUID.equals("GETPOSITIVEWORDSBETWEENANNOTS")
						&& BioTMLDataLists.getInstance().findStringInPositiveWordsList(token.getToken())){
					result = result +"GETPOSITIVEWORDSBETWEENANNOTS="+ token.getToken();
				}else if(annotationsOffsets.offsetsOverlap(token.getTokenOffsetsPair()) 
						&& featureUID.equals("GETNEGATIVEWORDSBETWEENANNOTS")
						&& BioTMLDataLists.getInstance().findStringInNegativeWordsList(token.getToken())){
					result = result + "GETNEGATIVEWORDSBETWEENANNOTS="+token.getToken();
				}else if(notOverlapNegativeWord(annotationsOffsets, token, foundbreaker, featureUID,"GETPOSITIVEWORDSOUTSIDEANNOTS"))
					result = result + "GETPOSITIVEWORDSOUTSIDEANNOTS="+token.getToken();
				else if(notOverlapNegativeWord(annotationsOffsets, token, foundbreaker, featureUID, "GETNEGATIVEWORDSOUTSIDEANNOTS"))
					result = result + "GETNEGATIVEWORDSOUTSIDEANNOTS="+token.getToken();
			}
		}catch (Exception e) {
			throw new BioTMLException(e);
		}
		return result;
	}

	private boolean notOverlapNegativeWord(IBioTMLOffsetsPair annotationsOffsets, IBioTMLToken token, IBioTMLToken foundbreaker, String featureUID, String feature) throws ClassNotFoundException, IOException{
		if(!annotationsOffsets.offsetsOverlap(token.getTokenOffsetsPair()) && featureUID.equals(feature)){
			if(BioTMLDataLists.getInstance().findStringInNegativeWordsList(token.getToken()))
					if(foundbreaker == null 
					|| (annotationsOffsets.compareTo(foundbreaker.getTokenOffsetsPair())>0 
							&& token.getTokenOffsetsPair().compareTo(foundbreaker.getTokenOffsetsPair())>0)
					|| (annotationsOffsets.compareTo(foundbreaker.getTokenOffsetsPair())<0 
							&& token.getTokenOffsetsPair().compareTo(foundbreaker.getTokenOffsetsPair())<0)
							)
						return true;

		}
		return false;
	}

}
