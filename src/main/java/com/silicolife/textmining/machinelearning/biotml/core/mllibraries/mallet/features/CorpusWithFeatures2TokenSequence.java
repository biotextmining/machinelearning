package com.silicolife.textmining.machinelearning.biotml.core.mllibraries.mallet.features;

import com.silicolife.textmining.machinelearning.biotml.core.BioTMLConstants;

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
		String sentence = (String) carrier.getData();
		String[] tokensWithLabels = sentence.split("\n");
		TokenSequence intancesData = new TokenSequence(tokensWithLabels.length);
		LabelSequence targetData = new LabelSequence((LabelAlphabet) getTargetAlphabet(), tokensWithLabels.length);
		StringBuilder sourceData = new StringBuilder();

		for( String tokenWithLabel: tokensWithLabels){
			String[] features = tokenWithLabel.split("\t");
			Token token = null;
			if(features.length>1){
				if(features[1].startsWith("(")&&features[1].endsWith(")")){
					token = new Token(features[0]+"\t"+features[1]);
				}else{
					token = new Token(features[0]);
				}
			}else{
				token = new Token(features[0]);
			}
			for(int i=1; i<features.length-1; i++){
				token.setFeatureValue(features[i], 1.0);
			}
			intancesData.add(token);
			if(features[features.length-1].equals(BioTMLConstants.b.toString())
				|| features[features.length-1].equals(BioTMLConstants.i.toString())
				||features[features.length-1].equals(BioTMLConstants.o.toString())){
				targetData.add(features[features.length-1]);
			}
			if(features.length>1){
				if(features[1].startsWith("(")&&features[1].endsWith(")")){
					sourceData.append(features[0]+"\t"+features[1]);
				}else{
					sourceData.append(features[0]);
				}
			}else{
				sourceData.append(features[0]);
			}
			
			sourceData.append(" ");

		}

		carrier.setData(intancesData);
		carrier.setTarget(targetData);
		carrier.setSource(sourceData.toString());
		return carrier;
	}
}
