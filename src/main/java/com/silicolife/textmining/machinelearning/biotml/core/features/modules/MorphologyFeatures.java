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
 * A class responsible for token morphology features adapted from Gimli WordShape.
 * 
 * 
 * @since 1.0.0
 * @author Ruben Rodrigues ({@code rrodrigues@silicolife.com})<br>
 * <b>Original Author:</b> David Campos (<a href="mailto:david.campos@ua.pt">david.campos@ua.pt</a>)
 * 
 */

public class MorphologyFeatures implements IBioTMLFeatureGenerator{
	
	/**
	 * 
	 * Initializes the insertion of token morphology features.
	 * 
	 */
	public  MorphologyFeatures(){
	}

	public Set<String> getUIDs() {
		Set<String> uids = new TreeSet<String>();
		uids.add("MORPHOLOGYTYPEI");
		uids.add("MORPHOLOGYTYPEII");
		uids.add("MORPHOLOGYTYPEIII");
		return uids;
	}
	
	public Set<String> getRecomendedUIDs(){
		return getUIDs();
	}
	
	public Map<String, String> getUIDInfos() {
		Map<String, String> infoMap = new HashMap<>();
		infoMap.put("MORPHOLOGYTYPEI", "Uses word shape to replace the digits in the token into '*'.");
		infoMap.put("MORPHOLOGYTYPEII", "Uses word shape to replace the digits in the token into '*', letters to 'a' and non letters to '#'.");
		infoMap.put("MORPHOLOGYTYPEIII", "Uses word shape to replace the digits in the token into '*', uppercase letters to 'A', lowercase letters to 'a' and non letters to '#'.");
		return infoMap;
	}
	
    /**
     * 
     * Convert an integer to the respective character representation.~
     * 
     * @param n The number.
     * @return The respective character.
     */
    private char Int2Char(int n) {
        if (n == 0) {
            return '*';
        }
        if (n == 1) {
            return 'a';
        }
        if (n == 2) {
            return '#';
        }
        return ' ';
    }
    

	public IBioTMLFeatureColumns getFeatureColumns(List<String> tokensToProcess,
			IBioTMLFeatureGeneratorConfigurator configuration)
			throws BioTMLException {
		
		if(tokensToProcess.isEmpty()){
			throw new BioTMLException(27);
		}
		
		BioTMLAssociationProcess tokenAnnotProcess = new BioTMLAssociationProcess(tokensToProcess);
		List<String> tokens = tokenAnnotProcess.getTokens();
		IBioTMLFeatureColumns features = new BioTMLFeatureColumns(tokens, getUIDs(), configuration);
		
		for (int i = 0; i < tokens.size(); i++){
			String token = tokens.get(i);
			char[] tokenText = token.toCharArray();
			
			String typeI = "";
	        String typeII = "";
	        String typeIII = "";
	        boolean isDigitI = false;
            int prev = -1;
            int current = -1;
            for (int k = 0; k < tokenText.length; k++) {
            	
            	// Word Shape Type I
                if (Character.isDigit(tokenText[k])) {
                    if (k == tokenText.length - 1) {
                        typeI += "*";
                    } else {
                        isDigitI = true;
                    }
                } else {
                    if (isDigitI) {
                        typeI += "*";
                        isDigitI = false;
                        typeI += tokenText[k];
                    } else {
                        typeI += tokenText[k];
                    }
                }

                // Word Shape Type II
                if (Character.isDigit(tokenText[k])) {
                    current = 0;
                } else if (Character.isLetter(tokenText[k])) {
                    current = 1;
                } else {
                    current = 2;
                }

                if (( k == tokenText.length - 1 ) && ( prev == current )) {
                    typeII += Int2Char(current);
                }

                if (( prev != current ) && ( prev != -1 )) {
                    typeII += Int2Char(prev);
                    if (k == tokenText.length - 1) {
                        typeII += Int2Char(current);
                    }
                }

                prev = current;

                // Word Shape Type III
                if (Character.isLetter(tokenText[k])) {
                    if (Character.isUpperCase(tokenText[k])) {
                        typeIII += "A";
                    } else if (Character.isLowerCase(tokenText[k])) {
                        typeIII += "a";
                    }
                } else if (Character.isDigit(tokenText[k])) {
                    typeIII += "1";
                } else {
                    typeIII += "#";
                }
            }
            
            if(!typeI.isEmpty()){
            	features.addTokenFeature("MORPHOLOGYTYPEI=" + typeI, "MORPHOLOGYTYPEI");
            } else{
            	features.addTokenFeature(new String(), "MORPHOLOGYTYPEI");
            }
            if(!typeII.isEmpty()){
            	features.addTokenFeature("MORPHOLOGYTYPEII=" + typeII, "MORPHOLOGYTYPEII");
            } else{
            	features.addTokenFeature(new String(), "MORPHOLOGYTYPEII");
            }
            if(!typeIII.isEmpty()){
            	features.addTokenFeature("MORPHOLOGYTYPEIII=" + typeIII, "MORPHOLOGYTYPEIII");
            } else{
            	features.addTokenFeature(new String(), "MORPHOLOGYTYPEIII");
            }   
		}
		
		features.updateTokenFeaturesUsingAssociationProcess(tokenAnnotProcess);

		return features;
	}


	public void cleanMemory() {
	}

}