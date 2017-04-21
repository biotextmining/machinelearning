package com.silicolife.textmining.machinelearning.biotml.core.features.modules;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.silicolife.textmining.machinelearning.biotml.core.exception.BioTMLException;
import com.silicolife.textmining.machinelearning.biotml.core.features.datastructures.BioTMLFeatureColumns;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLEntity;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLAssociation;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLFeatureColumns;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLFeatureGenerator;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLFeatureGeneratorConfigurator;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLToken;

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

	public Set<String> getNERFeatureIds() {
		Set<String> uids = new TreeSet<String>();
		uids.add("MORPHOLOGYTYPEI");
		uids.add("MORPHOLOGYTYPEII");
		uids.add("MORPHOLOGYTYPEIII");
		return uids;
	}

	public Set<String> getRecomendedNERFeatureIds(){
		return getNERFeatureIds();
	}

	public Map<String, String> getNERFeatureIdsInfos() {
		Map<String, String> infoMap = new HashMap<>();
		infoMap.put("MORPHOLOGYTYPEI", "Uses word shape to replace the digits in the token into '*'.");
		infoMap.put("MORPHOLOGYTYPEII", "Uses word shape to replace the digits in the token into '*', letters to 'a' and non letters to '#'.");
		infoMap.put("MORPHOLOGYTYPEIII", "Uses word shape to replace the digits in the token into '*', uppercase letters to 'A', lowercase letters to 'a' and non letters to '#'.");
		return infoMap;
	}

	@Override
	public Set<String> getREFeatureIds() {
		return new TreeSet<String>();
	}

	@Override
	public Map<String, String> getREFeatureIdsInfos() {
		return new HashMap<>();
	}

	@Override
	public Set<String> getRecomendedREFeatureIds() {
		return new TreeSet<String>();
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

	public IBioTMLFeatureColumns<IBioTMLToken> getFeatureColumns(List<IBioTMLToken> tokens,
			IBioTMLFeatureGeneratorConfigurator configuration)
					throws BioTMLException {

		IBioTMLFeatureColumns<IBioTMLToken> features = new BioTMLFeatureColumns<>(tokens, getNERFeatureIds(), configuration);

		for (int i = 0; i < tokens.size(); i++){
			String token = tokens.get(i).getToken();
			char[] tokenText = token.toCharArray();

			String typeI = "";
			String typeII = "";
			String typeIII = "";
			boolean isDigitI = false;
			int prev = -1;
			for (int k = 0; k < tokenText.length; k++) {

				// Word Shape Type I
				typeI = getWordShapeI(tokenText, typeI, isDigitI, k);

				// Word Shape Type II
				typeII = getWordShapeII(tokenText, typeII, prev, k);

				// Word Shape Type III
				typeIII = getWordShapeIII(tokenText, typeIII, k);
			}

			if(!typeI.isEmpty()){
				features.addBioTMLObjectFeature("MORPHOLOGYTYPEI=" + typeI, "MORPHOLOGYTYPEI");
			} else{
				features.addBioTMLObjectFeature(new String(), "MORPHOLOGYTYPEI");
			}
			if(!typeII.isEmpty()){
				features.addBioTMLObjectFeature("MORPHOLOGYTYPEII=" + typeII, "MORPHOLOGYTYPEII");
			} else{
				features.addBioTMLObjectFeature(new String(), "MORPHOLOGYTYPEII");
			}
			if(!typeIII.isEmpty()){
				features.addBioTMLObjectFeature("MORPHOLOGYTYPEIII=" + typeIII, "MORPHOLOGYTYPEIII");
			} else{
				features.addBioTMLObjectFeature(new String(), "MORPHOLOGYTYPEIII");
			}   
		}

		return features;
	}

	private String getWordShapeII(char[] tokenText, String typeII, int prev, int k) {
		int current;
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
		return typeII;
	}

	private String getWordShapeIII(char[] tokenText, String typeIII, int k) {
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
		return typeIII;
	}
	
	

	private String getWordShapeI(char[] tokenText, String typeI, boolean isDigitI, int k) {
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
		return typeI;
	}


	public void cleanMemory() {
	}

	@SuppressWarnings("rawtypes")
	@Override
	public IBioTMLFeatureColumns<IBioTMLAssociation> getEventFeatureColumns(List<IBioTMLToken> tokens, List<IBioTMLAssociation> associations,
			IBioTMLFeatureGeneratorConfigurator configuration) throws BioTMLException {

		IBioTMLFeatureColumns<IBioTMLAssociation> features = new BioTMLFeatureColumns<>(associations, getREFeatureIds(), configuration);
		for(IBioTMLAssociation association : associations){
			if(association.getEntryOne() instanceof IBioTMLEntity && association.getEntryTwo() instanceof IBioTMLEntity){
//				IBioTMLAnnotation annotationOne = (IBioTMLAnnotation) association.getEntryOne();
//				IBioTMLAnnotation annotationTwo = (IBioTMLAnnotation) association.getEntryTwo();
				
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

}