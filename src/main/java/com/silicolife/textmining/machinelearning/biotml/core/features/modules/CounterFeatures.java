package com.silicolife.textmining.machinelearning.biotml.core.features.modules;

import java.util.ArrayList;
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, String> getREFeatureIdsInfos() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> getRecomendedREFeatureIds() {
		// TODO Auto-generated method stub
		return null;
	}

	public IBioTMLFeatureColumns getFeatureColumns(List<String> tokens,
			IBioTMLFeatureGeneratorConfigurator configuration)
			throws BioTMLException {

		IBioTMLFeatureColumns features = new BioTMLFeatureColumns(tokens, getNERFeatureIds(), configuration);
		

		for (int i = 0; i < tokens.size(); i++){
			String token = tokens.get(i);
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
            
            features.addTokenFeature("WORD="+token, "WORD");
            if(numCaps> 0){
            	features.addTokenFeature("NUMCAPS=" + String.valueOf(numCaps), "NUMCAPS");
            } else {
            	features.addTokenFeature(new String(), "NUMCAPS");
            }
            if(numDigits>0){
            	features.addTokenFeature("NUMDIGITS=" + String.valueOf(numDigits), "NUMDIGITS");
            } else{
            	features.addTokenFeature(new String(), "NUMDIGITS");
            }
            features.addTokenFeature("LENGTH=" + String.valueOf(tokenText.length), "LENGTH");
            if(tokenText.length == 1){
            	 features.addTokenFeature("LENGTHGROUP=1", "LENGTHGROUP");
            } else if(tokenText.length == 2){
            	 features.addTokenFeature("LENGTHGROUP=2", "LENGTHGROUP");
            }else if(( tokenText.length >= 3 ) && ( tokenText.length <= 5 )){
            	 features.addTokenFeature("LENGTHGROUP=3-5", "LENGTHGROUP");
            }else if(tokenText.length>=6){
            	 features.addTokenFeature("LENGTHGROUP=6+", "LENGTHGROUP");
            }
		}
		
		return features;
	}

	public void cleanMemory() {		
	}

	@Override
	public IBioTMLFeatureColumns getEventFeatureColumns(List<String> tokens, List<IBioTMLAssociation> associations,
			IBioTMLFeatureGeneratorConfigurator configuration) throws BioTMLException {
		List<String> associationStrings = new ArrayList<>();
		
		for(IBioTMLAssociation association : associations){
			associationStrings.add(association.toString());
		}
		IBioTMLFeatureColumns features = new BioTMLFeatureColumns(associationStrings, getREFeatureIds(), configuration);
		for(IBioTMLAssociation association : associations){
			//features
		}
		
		return features;
	}

}