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

public class Corpus2TokenSequence extends Pipe {

	private static final long serialVersionUID = 1L;

	/**
	 * 
	 * Initializes the label conversion into Mallet API.
	 * 
	 */
	public Corpus2TokenSequence(){
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
			//if features is > 2 then we have a pair of ent-clue
			String tokenOrPair = features[0];
			if(features.length>1){
				if( !features[1].equals(BioTMLConstants.b.toString())
						&& !features[1].equals(BioTMLConstants.i.toString())
						&& !features[1].equals(BioTMLConstants.o.toString())){
					tokenOrPair = tokenOrPair + "\t" + features[1];
				}
			}
			Token token = new Token(tokenOrPair);
			intancesData.add(token);
			if(features[features.length-1].equals(BioTMLConstants.b.toString())
				|| features[features.length-1].equals(BioTMLConstants.i.toString())
				||features[features.length-1].equals(BioTMLConstants.o.toString())){
				targetData.add(features[features.length-1]);
			}
			sourceData.append(features[0]);
			sourceData.append(" ");
			if(features.length>1){
				if( !features[1].equals(BioTMLConstants.b.toString())
						&& !features[1].equals(BioTMLConstants.i.toString())
						&& !features[1].equals(BioTMLConstants.o.toString())){
					sourceData.append("\t");
					sourceData.append(features[1]);
					sourceData.append(" ");
				}
			}

		}

		carrier.setData(intancesData);
		carrier.setTarget(targetData);
		carrier.setSource(sourceData.toString());
		return carrier;
	}
}
