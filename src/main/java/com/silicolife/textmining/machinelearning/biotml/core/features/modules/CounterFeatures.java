package com.silicolife.textmining.machinelearning.biotml.core.features.modules;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.silicolife.textmining.machinelearning.biotml.core.exception.BioTMLException;
import com.silicolife.textmining.machinelearning.biotml.core.features.datastructures.BioTMLFeatureColumns;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLAnnotation;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLAssociation;
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
		return uids;
	}

	@Override
	public Map<String, String> getREFeatureIdsInfos() {
		Map<String, String> infoMap = new HashMap<>();
		infoMap.put("WORD", "Gives association of all tokens from event annotations");
		infoMap.put("COUNTTOKENSBETWEEN", "Counts the number of tokens between the two annotations on the event.");
		infoMap.put("COUNTTOKENSOUTSIDE", "Counts the number of tokens outside the two annotations on the event.");
		infoMap.put("POSITIONSINSENTENCE", "Gives the pair of annotations postion/(size position) in sentence.");
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
		
		IBioTMLFeatureColumns<IBioTMLAssociation> features = new BioTMLFeatureColumns<>(associations, getREFeatureIds(), configuration);
		for(IBioTMLAssociation association : associations){
			if(association.getEntryOne() instanceof IBioTMLAnnotation && association.getEntryTwo() instanceof IBioTMLAnnotation){
				IBioTMLAnnotation annotationOne = (IBioTMLAnnotation) association.getEntryOne();
				IBioTMLAnnotation annotationTwo = (IBioTMLAnnotation) association.getEntryTwo();
				
				if(configuration.hasFeatureUID("WORD"))
					features.addBioTMLObjectFeature("WORD="+getTokensAssociated(annotationOne, annotationTwo, tokens), "WORD");
				
				if(configuration.hasFeatureUID("COUNTTOKENSBETWEEN")){
					int countTokensBetween = countTokensBetween(annotationOne, annotationTwo, tokens);
					if(countTokensBetween>0)
						features.addBioTMLObjectFeature("COUNTTOKENSBETWEEN="+countTokensBetween, "COUNTTOKENSBETWEEN");
					else
						features.addBioTMLObjectFeature(new String(), "COUNTTOKENSBETWEEN");
				}

				if(configuration.hasFeatureUID("COUNTTOKENSOUTSIDE")){
					int countTokensOutside = countTokensOutside(annotationOne, annotationTwo, tokens);
					if(countTokensOutside>0)
						features.addBioTMLObjectFeature("COUNTTOKENSOUTSIDE="+countTokensOutside, "COUNTTOKENSOUTSIDE");
					else
						features.addBioTMLObjectFeature(new String(), "COUNTTOKENSOUTSIDE");
				}

				if(configuration.hasFeatureUID("POSITIONSINSENTENCE"))
					features.addBioTMLObjectFeature("POSITIONSINSENTENCE="+getPositionPair(annotationOne, annotationTwo, tokens), "POSITIONSINSENTENCE");
				
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
	
	private int countTokensBetween(IBioTMLAnnotation annotationOne, IBioTMLAnnotation annotationTwo, List<IBioTMLToken> tokens){
		int count = 0;
		for(IBioTMLToken token :tokens){
			if(isBetween(annotationOne.getAnnotationOffsets(), annotationTwo.getAnnotationOffsets(), token.getTokenOffsetsPair()))
				count++;
		}
		return count;
	}
	
	private int countTokensOutside(IBioTMLAnnotation annotationOne, IBioTMLAnnotation annotationTwo, List<IBioTMLToken> tokens){
		int count = 0;
		for(IBioTMLToken token :tokens){
			if(!isBetween(annotationOne.getAnnotationOffsets(), annotationTwo.getAnnotationOffsets(), token.getTokenOffsetsPair()))
				count++;
		}
		return count;
	}
	
	private boolean isBetween(IBioTMLOffsetsPair annotationOne, IBioTMLOffsetsPair annotationTwo, IBioTMLOffsetsPair token){
		if(annotationOne.compareTo(annotationTwo)<0){
			return annotationOne.getEndOffset() < token.getStartOffset() && token.getEndOffset() < annotationTwo.getStartOffset();
		}
		return annotationTwo.getEndOffset() < token.getStartOffset() && token.getEndOffset() < annotationOne.getStartOffset();
	}
	
	private String getPositionPair(IBioTMLAnnotation annotationOne, IBioTMLAnnotation annotationTwo, List<IBioTMLToken> tokens){
		if(annotationOne.compareTo(annotationTwo)>0)
			return String.format("%.1f", getRelativeAnnotationPosition(annotationTwo, tokens))+ "-"+ String.format("%.1f", getRelativeAnnotationPosition(annotationOne, tokens));
		return String.format("%.1f", getRelativeAnnotationPosition(annotationOne, tokens))+ "-"+ String.format("%.1f", getRelativeAnnotationPosition(annotationTwo, tokens));
	}

	private double getRelativeAnnotationPosition(IBioTMLAnnotation annotationOne, List<IBioTMLToken> tokens) {
		int tokensinAnnotCount = 0;
		int tokensbeforeCount = 0;
		for(IBioTMLToken token : tokens){
			if(annotationOne.getAnnotationOffsets().containsInside(token.getTokenOffsetsPair()))
				tokensinAnnotCount++;
			else if(annotationOne.getAnnotationOffsets().compareTo(token.getTokenOffsetsPair())>0){
				tokensbeforeCount++;
			}
		}
		return  (tokensbeforeCount + ((tokensinAnnotCount - (tokensinAnnotCount % 2))/2) + (tokensinAnnotCount % 2))/tokens.size();
	}
	
	private String getTokensAssociated(IBioTMLAnnotation annotationOne, IBioTMLAnnotation annotationTwo, List<IBioTMLToken> tokens){
		String result = new String();
		Collections.sort(tokens);
		for(IBioTMLToken token : tokens){
			if(annotationOne.getAnnotationOffsets().containsInside(token.getTokenOffsetsPair())){
				if(!result.isEmpty())
					result = result +"__&__";
				result = result+ token.getToken();
			}
			if(annotationTwo.getAnnotationOffsets().containsInside(token.getTokenOffsetsPair())){
				if(!result.isEmpty())
					result = result +"__&__";
				result = result+ token.getToken();
			}
		}
		return result;
	}

}