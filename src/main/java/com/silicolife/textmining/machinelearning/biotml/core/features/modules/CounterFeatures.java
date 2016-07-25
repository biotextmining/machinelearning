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

	public Set<String> getUIDs() {
		Set<String> uids = new TreeSet<String>();
		uids.add("WORD");
		uids.add("NUMCAPS");
		uids.add("NUMDIGITS");
		uids.add("LENGTH");
		uids.add("LENGTHGROUP");
		return uids;
	}
	
	public Set<String> getRecomendedUIDs(){
		Set<String> uids = new TreeSet<String>();
		uids.add("WORD");
		uids.add("NUMCAPS");
		uids.add("NUMDIGITS");
		uids.add("LENGTHGROUP");
		return uids;
	}
	
	public Map<String, String> getUIDInfos() {
		Map<String, String> infoMap = new HashMap<>();
		infoMap.put("WORD", "Uses the token as a feature.");
		infoMap.put("NUMCAPS", "Counts the number of uppercase letters present in the token.");
		infoMap.put("NUMDIGITS", "Counts the number of digits present in the token.");
		infoMap.put("LENGTH", "Counts the token length.");
		infoMap.put("LENGTHGROUP", "Groups the token by token length. (Size 1, 2, 3-5 or 6+.");
		return infoMap;
	}
	
	public IBioTMLFeatureColumns getFeatureColumnsForRelations(List<String> tokensToProcess, int startAnnotationIndex, int endAnnotationIndex, IBioTMLFeatureGeneratorConfigurator configuration) throws BioTMLException {
		BioTMLAssociationProcess tokenAnnotProcess = new BioTMLAssociationProcess(tokensToProcess, startAnnotationIndex, endAnnotationIndex);
		List<String> tokens = tokenAnnotProcess.getTokens();
		IBioTMLFeatureColumns features = getFeatureColumns(tokens, configuration);
		features.updateTokenFeaturesUsingAssociationProcess(tokenAnnotProcess);
		return features;
	}

	public IBioTMLFeatureColumns getFeatureColumns(List<String> tokens,
			IBioTMLFeatureGeneratorConfigurator configuration)
			throws BioTMLException {

		IBioTMLFeatureColumns features = new BioTMLFeatureColumns(tokens, getUIDs(), configuration);
		

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

}