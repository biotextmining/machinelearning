package com.silicolife.textmining.machinelearning.biotml.core.mllibraries.mallet.features;

import java.util.List;

import com.silicolife.textmining.machinelearning.biotml.core.corpora.BioTMLTokensWithFeaturesAndLabels;

import cc.mallet.pipe.Pipe;
import cc.mallet.types.Instance;
import cc.mallet.types.LabelAlphabet;
import cc.mallet.types.LabelSequence;
import cc.mallet.types.Token;
import cc.mallet.types.TokenSequence;

/**
 * 
 * A class responsible for label instance definition in Mallet API.
 * 
 * @since 1.0.0
 * @author Ruben Rodrigues ({@code rrodrigues@silicolife.com})
 */

public class CorpusWithFeatures2TokenSequence extends Pipe {

	private static final long serialVersionUID = 1L;

	/**
	 * 
	 * Initializes the label conversion into Mallet API.
	 * 
	 */
	public CorpusWithFeatures2TokenSequence(){
		super(null, new LabelAlphabet());
	}

	/**
	 * 
	 * Pipe conversion of token and label into Mallet instance.
	 * This process is thread safe.
	 * @param carrier Raw input data.
	 * @return Processed instance with correct token and label.
	 */
	public synchronized Instance pipe(Instance carrier) {
		BioTMLTokensWithFeaturesAndLabels tokensWithLabels = (BioTMLTokensWithFeaturesAndLabels) carrier.getData();;
		TokenSequence intancesData = new TokenSequence(tokensWithLabels.getTokens().size());
		LabelSequence targetData = new LabelSequence((LabelAlphabet) getTargetAlphabet(), tokensWithLabels.getTokens().size());
		StringBuilder sourceData = new StringBuilder();

		for( int i = 0; i<tokensWithLabels.getTokens().size(); i++){
			String tokenString = tokensWithLabels.getTokens().get(i);
			Token token = new Token(tokenString);
			List<String> features = tokensWithLabels.getTokenFeatures().get(i);
			for(String feature: features){
				token.setFeatureValue(feature, 1.0);
			}
			if(tokensWithLabels.getAnnotationForRelationStartIndex() != -1){
				token.setProperty("startAnnotIndex", tokensWithLabels.getAnnotationForRelationStartIndex());
			}
			
			if(tokensWithLabels.getAnnotationForRelationEndIndex() != -1){
				token.setProperty("endAnnotIndex", tokensWithLabels.getAnnotationForRelationEndIndex());
			}
			
			intancesData.add(token);
			if(!tokensWithLabels.getLabels().isEmpty()){
				targetData.add(tokensWithLabels.getLabels().get(i).toString());
			}
			sourceData.append(tokenString);
			sourceData.append(" ");

		}

		carrier.setData(intancesData);
		carrier.setTarget(targetData);
		carrier.setSource(sourceData.toString());
		return carrier;
	}
}
