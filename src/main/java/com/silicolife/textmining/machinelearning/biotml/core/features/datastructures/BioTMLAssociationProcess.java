package com.silicolife.textmining.machinelearning.biotml.core.features.datastructures;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * Association processor class to associate tokens with the given index. 
 * Each string contains the token and the tokens index that will be associated to that token (e.g. 'CH4	(2-6)', this token will be associated with the tokens from index 2 to 6).
 * 
 * @since 1.0.0
 * @author Ruben Rodrigues ({@code rrodrigues@silicolife.com})
 */

public class BioTMLAssociationProcess {
	
	private List<Integer> annotationIndexs;
	private List<String> tokens;

	public BioTMLAssociationProcess(List<String> binaryAssociations){
		this.annotationIndexs = processAnnotationIndexs(binaryAssociations);
		this.tokens = retrieveTokensAndDefineAnnotation(binaryAssociations);
	}
	
	public List<String> getTokens(){
		return tokens;
	}
	
	private List<Integer> getAnnotationIndexs(){
		return annotationIndexs;
	}
	
	public List<String> associateAnnotationFeatureToFeatureColumn(List<String> featureColumn){
		if(getAnnotationIndexs().isEmpty()){
			return featureColumn;
		}
		String annotationFeature = new String();
		for(int annotationIndex=getAnnotationIndexs().get(0); annotationIndex<getAnnotationIndexs().get(1)+1; annotationIndex++){
			if(annotationFeature.isEmpty()){
				annotationFeature = featureColumn.get(annotationIndex);
			}else{
				annotationFeature = annotationFeature + featureColumn.get(annotationIndex);
			}
		}
		if(!annotationFeature.isEmpty()){
			for(int tokenIndex = 0; tokenIndex<getTokens().size(); tokenIndex++){
				if(tokenIndex<=getAnnotationIndexs().get(1)){
					featureColumn.set(tokenIndex, featureColumn.get(tokenIndex)+"_&&_"+annotationFeature);
				}
				if(tokenIndex>getAnnotationIndexs().get(1)){
					featureColumn.set(tokenIndex, annotationFeature+"_&&_"+ featureColumn.get(tokenIndex));
				}
			}
		}
		return featureColumn;
	}
	
	private List<String> retrieveTokensAndDefineAnnotation(List<String> binaryAssociations){
		List<String> tokens = new ArrayList<String>();
		if(!getAnnotationIndexs().isEmpty()){
			for(String association : binaryAssociations){
				String[] tokenAndAnnotation = association.split("\t");
				tokens.add(tokenAndAnnotation[0]);
			}
		}else{
			tokens = binaryAssociations;
		}
		return tokens;
	}
	
	private List<Integer> processAnnotationIndexs(List<String> binaryAssociations){
		List<Integer> annotationIndex = new ArrayList<Integer>(2);
		String[] pair = binaryAssociations.get(0).split("\t");
		if(pair.length>1){
			String annotationIndexString = pair[pair.length-1];
			String[] annotationIndexStringSplited = annotationIndexString.split(" | ");
			annotationIndex.add(Integer.valueOf(annotationIndexStringSplited[0].substring(1)));
			annotationIndex.add(Integer.valueOf(annotationIndexStringSplited[2].substring(0, annotationIndexStringSplited[2].length()-1)));
			return annotationIndex;
		}
		return annotationIndex;
	}
}
