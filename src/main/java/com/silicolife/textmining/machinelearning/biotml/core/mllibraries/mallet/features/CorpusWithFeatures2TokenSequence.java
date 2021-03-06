package com.silicolife.textmining.machinelearning.biotml.core.mllibraries.mallet.features;

import java.util.List;

import com.silicolife.textmining.machinelearning.biotml.core.corpora.otherdatastructures.BioTMLObjectWithFeaturesAndLabels;

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
		BioTMLObjectWithFeaturesAndLabels<?> bioTMLObjectWithLabels = (BioTMLObjectWithFeaturesAndLabels<?>) carrier.getData();
		TokenSequence intancesData = new TokenSequence(bioTMLObjectWithLabels.getBioTMLObjects().size());
		LabelSequence targetData = new LabelSequence((LabelAlphabet) getTargetAlphabet(), bioTMLObjectWithLabels.getBioTMLObjects().size());

		for( int i = 0; i<bioTMLObjectWithLabels.getBioTMLObjects().size(); i++){

			Object tokenString = bioTMLObjectWithLabels.getBioTMLObjects().get(i);
			Token token = new Token(tokenString.toString());
			List<String> features = bioTMLObjectWithLabels.getFeatures().get(i);
			
			for(String feature: features)
				token.setFeatureValue(feature, 1.0);
			
			intancesData.add(token);
			if(!bioTMLObjectWithLabels.getLabels().isEmpty())
				targetData.add(bioTMLObjectWithLabels.getLabels().get(i).toString());

		}

		carrier.setData(intancesData);
		carrier.setTarget(targetData);
		carrier.setName(bioTMLObjectWithLabels.getBioTMLObjects());
		carrier.setSource(carrier.getSource());
		return carrier;
	}

}
