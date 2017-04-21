package com.silicolife.textmining.machinelearning.biotml.core.features.modules;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.silicolife.textmining.machinelearning.biotml.core.corpora.BioTMLOffsetsPairImpl;
import com.silicolife.textmining.machinelearning.biotml.core.exception.BioTMLException;
import com.silicolife.textmining.machinelearning.biotml.core.features.datastructures.BioTMLFeatureColumns;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLAssociation;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLEntity;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLFeatureColumns;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLFeatureGenerator;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLFeatureGeneratorConfigurator;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLOffsetsPair;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLToken;

/**
 * 
 * A class responsible for counter token features.
 * 
 * @since 1.0.0
 * @author Ruben Rodrigues ({@code rrodrigues@silicolife.com})
 */

public class CounterFeatures implements IBioTMLFeatureGenerator{
	
	/**
	 * 
	 * Initializes the insertion of counter token features.
	 * 
	 */
	public  CounterFeatures(){
	}

	@Override
	public Set<String> getNERFeatureIds() {
		Set<String> uids = new TreeSet<String>();
		uids.add("WORD");
		uids.add("NUMCAPS");
		uids.add("NUMDIGITS");
		uids.add("LENGTH");
		uids.add("LENGTHGROUP");
		return uids;
	}
	
	@Override
	public Set<String> getRecomendedNERFeatureIds(){
		Set<String> uids = new TreeSet<String>();
		uids.add("WORD");
		uids.add("NUMCAPS");
		uids.add("NUMDIGITS");
		uids.add("LENGTHGROUP");
		return uids;
	}
	
	@Override
	public Map<String, String> getNERFeatureIdsInfos() {
		Map<String, String> infoMap = new HashMap<>();
		infoMap.put("WORD", "Uses the token as a feature.");
		infoMap.put("NUMCAPS", "Counts the number of uppercase letters present in the token.");
		infoMap.put("NUMDIGITS", "Counts the number of digits present in the token.");
		infoMap.put("LENGTH", "Counts the token length.");
		infoMap.put("LENGTHGROUP", "Groups the token by token length. (Size 1, 2, 3-5 or 6+.");
		return infoMap;
	}
	
	@Override
	public Set<String> getREFeatureIds() {
		Set<String> uids = new TreeSet<String>();
		uids.add("WORD");
		uids.add("COUNTTOKENSBETWEEN");
		uids.add("COUNTTOKENSOUTSIDE");
		uids.add("POSITIONSINSENTENCE");
		uids.add("ANNOTCLASSIFICATION");
		uids.add("ANNOTSTARTSWITHNON");
		uids.add("ISBETWEENCOMMAS");
		uids.add("ISBETWEENPARENTESIS");
		return uids;
	}

	@Override
	public Map<String, String> getREFeatureIdsInfos() {
		Map<String, String> infoMap = new HashMap<>();
		infoMap.put("WORD", "Gives association of all tokens from event annotations");
		infoMap.put("COUNTTOKENSBETWEEN", "Counts the number of tokens between the two annotations on the event.");
		infoMap.put("COUNTTOKENSOUTSIDE", "Counts the number of tokens outside the two annotations on the event.");
		infoMap.put("POSITIONSINSENTENCE", "Gives the pair of annotations postion/(size position) in sentence.");
		infoMap.put("ANNOTCLASSIFICATION", "Puts the pair of annotation classification.");
		infoMap.put("ANNOTSTARTSWITHNON", "Adds annotations that contains with 'non'");
		infoMap.put("ISBETWEENCOMMAS", "");
		infoMap.put("ISBETWEENPARENTESIS", "");
		return infoMap;
	}

	@Override
	public Set<String> getRecomendedREFeatureIds() {
		Set<String> uids = new TreeSet<String>();
		return uids;
	}

	public IBioTMLFeatureColumns<IBioTMLToken> getFeatureColumns(List<IBioTMLToken> tokens,
			IBioTMLFeatureGeneratorConfigurator configuration)
			throws BioTMLException {

		IBioTMLFeatureColumns<IBioTMLToken> features = new BioTMLFeatureColumns<>(tokens, getNERFeatureIds(), configuration);
		

		for (int i = 0; i < tokens.size(); i++){
			String token = tokens.get(i).getToken();
			char[] tokenText = token.toCharArray();
			
            int numCaps = 0;
            int numDigits = 0;
            for (int k = 0; k < tokenText.length; k++) {
                if (Character.isUpperCase(tokenText[k])) {
                	numCaps++;
                }
                if (Character.isDigit(tokenText[k])) {
                	numDigits++;
                }
            }
            
            features.addBioTMLObjectFeature("WORD="+token, "WORD");
            if(numCaps> 0){
            	features.addBioTMLObjectFeature("NUMCAPS=" + String.valueOf(numCaps), "NUMCAPS");
            } else {
            	features.addBioTMLObjectFeature(new String(), "NUMCAPS");
            }
            if(numDigits>0){
            	features.addBioTMLObjectFeature("NUMDIGITS=" + String.valueOf(numDigits), "NUMDIGITS");
            } else{
            	features.addBioTMLObjectFeature(new String(), "NUMDIGITS");
            }
            features.addBioTMLObjectFeature("LENGTH=" + String.valueOf(tokenText.length), "LENGTH");
            if(tokenText.length == 1){
            	 features.addBioTMLObjectFeature("LENGTHGROUP=1", "LENGTHGROUP");
            } else if(tokenText.length == 2){
            	 features.addBioTMLObjectFeature("LENGTHGROUP=2", "LENGTHGROUP");
            }else if(( tokenText.length >= 3 ) && ( tokenText.length <= 5 )){
            	 features.addBioTMLObjectFeature("LENGTHGROUP=3-5", "LENGTHGROUP");
            }else if(tokenText.length>=6){
            	 features.addBioTMLObjectFeature("LENGTHGROUP=6+", "LENGTHGROUP");
            }
		}
		
		return features;
	}

	public void cleanMemory() {		
	}

	@SuppressWarnings("rawtypes")
	@Override
	public IBioTMLFeatureColumns<IBioTMLAssociation> getEventFeatureColumns(List<IBioTMLToken> tokens, List<IBioTMLAssociation> associations,
			IBioTMLFeatureGeneratorConfigurator configuration) throws BioTMLException {
		Collections.sort(tokens);
		
		IBioTMLFeatureColumns<IBioTMLAssociation> features = new BioTMLFeatureColumns<>(associations, getREFeatureIds(), configuration);
		for(IBioTMLAssociation association : associations){
			if(association.getEntryOne() instanceof IBioTMLEntity && association.getEntryTwo() instanceof IBioTMLEntity){
				IBioTMLEntity annotationOne = (IBioTMLEntity) association.getEntryOne();
				IBioTMLEntity annotationTwo = (IBioTMLEntity) association.getEntryTwo();
				
				if(configuration.hasFeatureUID("WORD"))
					features.addBioTMLObjectFeature("WORD="+getTokensAssociated(annotationOne, annotationTwo, tokens), "WORD");
				
				if(configuration.hasFeatureUID("COUNTTOKENSBETWEEN"))
					features.addBioTMLObjectFeature("COUNTTOKENSBETWEEN="+countTokensBetween(annotationOne, annotationTwo, tokens), "COUNTTOKENSBETWEEN");

				if(configuration.hasFeatureUID("COUNTTOKENSOUTSIDE")){
					int countTokensOutside = countTokensOutside(annotationOne, annotationTwo, tokens);
					if(countTokensOutside>0)
						features.addBioTMLObjectFeature("COUNTTOKENSOUTSIDE="+countTokensOutside, "COUNTTOKENSOUTSIDE");
					else
						features.addBioTMLObjectFeature(new String(), "COUNTTOKENSOUTSIDE");
				}

				if(configuration.hasFeatureUID("POSITIONSINSENTENCE"))
					features.addBioTMLObjectFeature("POSITIONSINSENTENCE="+getPositionPair(annotationOne, annotationTwo, tokens), "POSITIONSINSENTENCE");
				
				if(configuration.hasFeatureUID("ANNOTCLASSIFICATION"))
					features.addBioTMLObjectFeature("ANNOTCLASSIFICATION="+annotationOne.getAnnotationType() + "__&&__" + annotationTwo.getAnnotationType(), "ANNOTCLASSIFICATION");
				
				if(configuration.hasFeatureUID("ANNOTSTARTSWITHNON")){
					String featureString = getAnnotationNonFeature(tokens, annotationOne, annotationTwo);
					features.addBioTMLObjectFeature(featureString, "ANNOTSTARTSWITHNON");
					features.setUIDhasMultiFeatureColumn("ANNOTSTARTSWITHNON");
				}
				
				if(configuration.hasFeatureUID("ISBETWEENCOMMAS")){
					String featureString = getTokensBetweenCommas(annotationOne, annotationTwo, tokens);
					features.addBioTMLObjectFeature(featureString, "ISBETWEENCOMMAS");
					features.setUIDhasMultiFeatureColumn("ISBETWEENCOMMAS");
				}
				
				if(configuration.hasFeatureUID("ISBETWEENPARENTESIS")){
					String featureString = getTokensBetweenParentesis(annotationOne, annotationTwo, tokens);
					features.addBioTMLObjectFeature(featureString, "ISBETWEENPARENTESIS");
					features.setUIDhasMultiFeatureColumn("ISBETWEENPARENTESIS");
				}
				
			}else if(association.getEntryOne() instanceof IBioTMLEntity && association.getEntryTwo() instanceof IBioTMLAssociation){
				//TODO
			}else if(association.getEntryOne() instanceof IBioTMLAssociation && association.getEntryTwo() instanceof IBioTMLEntity){
				//TODO
			}else if(association.getEntryOne() instanceof IBioTMLAssociation && association.getEntryTwo() instanceof IBioTMLAssociation){
				//TODO
			}
		}
		
		return features;
	}
	
	private String getTokensBetweenParentesis(IBioTMLEntity annotationOne, IBioTMLEntity annotationTwo,
			List<IBioTMLToken> tokens){
		
		String result = getAnnotationParentesis(annotationOne, tokens);
		if(!result.isEmpty())
			result = "ISBETWEENPARENTESIS=" + result;
		
		String result2 = getAnnotationParentesis(annotationOne, tokens);
		if(!result2.isEmpty()){
			if(!result.isEmpty())
				result = result + "\t";
			result = result + "ISBETWEENPARENTESIS=" + result2;
		}
		
		return new String();
	}

	private String getAnnotationParentesis(IBioTMLEntity annotation, List<IBioTMLToken> tokens) {
		IBioTMLOffsetsPair annotationOffsets = annotation.getAnnotationOffsets();
		boolean startParentesis = false;
		boolean endParentesis = true;
		
		for(IBioTMLToken token : tokens){
			IBioTMLOffsetsPair tokenoffsets = token.getTokenOffsetsPair();
			if(token.getToken().startsWith("(") 
					&& tokenoffsets.isLessDistantThan(annotationOffsets, 3) 
					&& tokenoffsets.compareTo(annotationOffsets)<=0 ){
				startParentesis = true;
			}else if(token.getToken().endsWith(")")
					&& tokenoffsets.isLessDistantThan(annotationOffsets, 3) 
					&& tokenoffsets.compareTo(annotationOffsets)>=0){
				endParentesis = true;
			}
		}
		if(startParentesis && endParentesis)
			return annotation.getAnnotationType();
		return new String();
	}


	private String getTokensBetweenCommas(IBioTMLEntity annotationOne, IBioTMLEntity annotationTwo,
			List<IBioTMLToken> tokens) {
		Set<IBioTMLToken> commas = new HashSet<>();
		for(IBioTMLToken token : tokens)
			if(token.getToken().equals(","))
				commas.add(token);
		
		String result = getCommaVerificationToken(annotationOne, commas);
		if(!result.isEmpty())
			result = "ISBETWEENCOMMAS=" + result + "\t";
		
		String result2 = getCommaVerificationToken(annotationTwo, commas);
		if(!result2.isEmpty()){
			if(!result.isEmpty())
				result = result + "\t";
			result = result + "ISBETWEENCOMMAS=" + result2;
		}
		
		return result;
	}

	private String getCommaVerificationToken(IBioTMLEntity annotation, Set<IBioTMLToken> commas) {
		IBioTMLOffsetsPair annotationOffsets = annotation.getAnnotationOffsets();
		IBioTMLToken commaafter = null;
		boolean commabefore = false;
		Iterator<IBioTMLToken> itCommas = commas.iterator();
		while(itCommas.hasNext()){
			IBioTMLToken comma = itCommas.next();
			if(comma.getTokenOffsetsPair().compareTo(annotationOffsets)<0)
				commabefore = true;
			else if(comma.getTokenOffsetsPair().compareTo(annotationOffsets)>0){
				if(commaafter == null)
					commaafter = comma;
				else if( commaafter.getTokenOffsetsPair().compareTo(comma.getTokenOffsetsPair())<0)
					commaafter = comma;
			}
				
		}
		
		if(commaafter != null && commabefore || commaafter != null && commaafter.getTokenOffsetsPair().isLessDistantThan(annotationOffsets, 3))
			return annotation.getAnnotationType();
		return new String();
	}

	private String getAnnotationNonFeature(List<IBioTMLToken> tokens, IBioTMLEntity annotationOne,
			IBioTMLEntity annotationTwo) {
		String featureString = new String();
		featureString = addNonFeature(featureString, tokens, annotationOne, "non");
		featureString = addNonFeature(featureString, tokens, annotationTwo, "non");
		featureString = addNonFeature(featureString, tokens, annotationOne, "un");
		featureString = addNonFeature(featureString, tokens, annotationTwo, "un");
		featureString = addNonFeature(featureString, tokens, annotationOne, "in");
		featureString = addNonFeature(featureString, tokens, annotationTwo, "in");
		return featureString;
	}
	
	private String addNonFeature(String featureString, List<IBioTMLToken> tokens, IBioTMLEntity annotation, String startingby){
		if(!featureString.isEmpty())
			featureString = featureString + "\t";
		featureString = featureString + "ANNOTSTARTSWITHNON="+ getAnnotationStartingBy(annotation, tokens, startingby) ;
		return featureString;
	}
	
	private int countTokensBetween(IBioTMLEntity annotationOne, IBioTMLEntity annotationTwo, List<IBioTMLToken> tokens){
		int count = 0;
		for(IBioTMLToken token :tokens){
			if(isBetween(annotationOne.getAnnotationOffsets(), annotationTwo.getAnnotationOffsets(), token.getTokenOffsetsPair()))
				count++;
		}
		return count;
	}
	
	private int countTokensOutside(IBioTMLEntity annotationOne, IBioTMLEntity annotationTwo, List<IBioTMLToken> tokens){
		int count = 0;
		for(IBioTMLToken token :tokens){
			if(isOutside(annotationOne.getAnnotationOffsets(), annotationTwo.getAnnotationOffsets(), token.getTokenOffsetsPair()))
				count++;
		}
		return count;
	}
	
	private boolean isBetween(IBioTMLOffsetsPair annotationOne, IBioTMLOffsetsPair annotationTwo, IBioTMLOffsetsPair token){
		IBioTMLOffsetsPair betweenOffsets = null;
		if(annotationOne.compareTo(annotationTwo)<0)
			betweenOffsets = new BioTMLOffsetsPairImpl(annotationOne.getEndOffset(), annotationTwo.getStartOffset());
		else
			betweenOffsets = new BioTMLOffsetsPairImpl(annotationTwo.getEndOffset(), annotationOne.getStartOffset());
		
		return betweenOffsets.offsetsOverlap(token);
	}
	
	private boolean isOutside(IBioTMLOffsetsPair annotationOne, IBioTMLOffsetsPair annotationTwo, IBioTMLOffsetsPair token){
		IBioTMLOffsetsPair betweenOffsets = null;
		if(annotationOne.compareTo(annotationTwo)<0)
			betweenOffsets = new BioTMLOffsetsPairImpl(annotationOne.getStartOffset(), annotationTwo.getEndOffset());
		else
			betweenOffsets = new BioTMLOffsetsPairImpl(annotationTwo.getStartOffset(), annotationOne.getEndOffset());
		
		return !betweenOffsets.offsetsOverlap(token);
	}
	
	private String getPositionPair(IBioTMLEntity annotationOne, IBioTMLEntity annotationTwo, List<IBioTMLToken> tokens){
		if(annotationOne.compareTo(annotationTwo)>0)
			return String.format("%.1f", getRelativeAnnotationPosition(annotationTwo, tokens))+ "-"+ String.format("%.1f", getRelativeAnnotationPosition(annotationOne, tokens));
		return String.format("%.1f", getRelativeAnnotationPosition(annotationOne, tokens))+ "-"+ String.format("%.1f", getRelativeAnnotationPosition(annotationTwo, tokens));
	}

	private double getRelativeAnnotationPosition(IBioTMLEntity annotationOne, List<IBioTMLToken> tokens) {
		double tokensinAnnotCount = 0;
		double tokensbeforeCount = 0;
		for(IBioTMLToken token : tokens){
			if(annotationOne.getAnnotationOffsets().offsetsOverlap(token.getTokenOffsetsPair()))
				tokensinAnnotCount++;
			else if(annotationOne.getAnnotationOffsets().compareTo(token.getTokenOffsetsPair())>0)
				tokensbeforeCount++;
		}
		double tokenInannot = 0;
		if((tokensinAnnotCount % 2) == 0)
			tokenInannot = tokensinAnnotCount/2;
		else{
			tokenInannot = tokensinAnnotCount - 1;
			tokenInannot = tokenInannot/2;
			tokenInannot = tokenInannot + 1;
		}
			
		double result = (tokensbeforeCount + tokenInannot)/(double)tokens.size();
		return result;
	}
	
	private String getTokensAssociated(IBioTMLEntity annotationOne, IBioTMLEntity annotationTwo, List<IBioTMLToken> tokens){
		String result = new String();
		for(IBioTMLToken token : tokens){
			if(annotationOne.getAnnotationOffsets().offsetsOverlap(token.getTokenOffsetsPair())){
				if(!result.isEmpty())
					result = result +"__&__";
				result = result+ token.getToken();
			}
			if(annotationTwo.getAnnotationOffsets().offsetsOverlap(token.getTokenOffsetsPair())){
				if(!result.isEmpty())
					result = result +"__&__";
				result = result+ token.getToken();
			}
		}
		return result;
	}
	
	private String getAnnotationStartingBy(IBioTMLEntity annotation, List<IBioTMLToken> tokens, String startingBy){
		String result = new String();
		for(IBioTMLToken token : tokens){
			if(annotation.getAnnotationOffsets().offsetsOverlap(token.getTokenOffsetsPair())){
				if(token.getToken().toLowerCase().startsWith(startingBy.toLowerCase()))
					return annotation.getAnnotationType() + "_STARTSBY:"+ startingBy;
				else
					return result;
			}
		}
		return result;
	}

}