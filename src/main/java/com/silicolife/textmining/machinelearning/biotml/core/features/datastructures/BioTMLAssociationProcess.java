package com.silicolife.textmining.machinelearning.biotml.core.features.datastructures;

import java.util.List;

import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLToken;

/**
 * 
 * Association processor class to associate tokens with the given index. 
 * Each string contains the token and the tokens index that will be associated to that token (e.g. 'CH4	(2-6)', this token will be associated with the tokens from index 2 to 6).
 * 
 * @since 1.0.0
 * @author Ruben Rodrigues ({@code rrodrigues@silicolife.com})
 */

public class BioTMLAssociationProcess {
	
	private List<IBioTMLToken> tokens;
	private int startAnnotation;
	private int endAnnotation;

	public BioTMLAssociationProcess(List<IBioTMLToken> tokens, int startAnnotation, int endAnnotation){
		this.tokens = tokens;
		this.startAnnotation = startAnnotation;
		this.endAnnotation = endAnnotation;
	}
	
	public List<IBioTMLToken> getTokens(){
		return tokens;
	}
	
	public int getStartAnnotation() {
		return startAnnotation;
	}

	public int getEndAnnotation() {
		return endAnnotation;
	}

	public List<String> associateAnnotationFeatureToFeatureColumn(List<String> featureColumn){
		String annotationFeature = new String();
		for(int annotationIndex=getStartAnnotation(); annotationIndex<getEndAnnotation()+1; annotationIndex++){
			if(annotationFeature.isEmpty()){
				annotationFeature = featureColumn.get(annotationIndex);
			}else{
				annotationFeature = annotationFeature + featureColumn.get(annotationIndex);
			}
		}
		if(!annotationFeature.isEmpty()){
			for(int tokenIndex = 0; tokenIndex<getTokens().size(); tokenIndex++){
				if(tokenIndex<=getEndAnnotation()){
					featureColumn.set(tokenIndex, featureColumn.get(tokenIndex)+"_&&_"+annotationFeature);
				}
				if(tokenIndex>getEndAnnotation()){
					featureColumn.set(tokenIndex, annotationFeature+"_&&_"+ featureColumn.get(tokenIndex));
				}
			}
		}
		return featureColumn;
	}
	
}
