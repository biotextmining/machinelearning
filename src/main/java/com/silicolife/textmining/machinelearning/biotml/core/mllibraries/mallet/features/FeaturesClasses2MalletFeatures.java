package com.silicolife.textmining.machinelearning.biotml.core.mllibraries.mallet.features;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.silicolife.textmining.machinelearning.biotml.core.exception.BioTMLException;
import com.silicolife.textmining.machinelearning.biotml.core.features.BioTMLFeaturesManager;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLFeatureColumns;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLFeatureGenerator;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLFeatureGeneratorConfigurator;

import cc.mallet.pipe.Pipe;
import cc.mallet.types.Instance;
import cc.mallet.types.Token;
import cc.mallet.types.TokenSequence;

/**
 * 
 * A class responsible to insert all features generated on features module in training matrix of Mallet API.
 * 
 * @since 1.0.0
 * @author Ruben Rodrigues ({@code rrodrigues@silicolife.com})
 */

public class FeaturesClasses2MalletFeatures extends Pipe{

	private static final long serialVersionUID = 1L;
	private IBioTMLFeatureGeneratorConfigurator configuration;

	/**
	 * 
	 * Initializes the insertion feature from features modules in training matrix of Mallet API.
	 * 
	 * @param configuration - Features configuration to activate/deactivate features generation in features module.
	 */
	public  FeaturesClasses2MalletFeatures(IBioTMLFeatureGeneratorConfigurator configuration){
		this.configuration = configuration;
	}
	
	private IBioTMLFeatureGeneratorConfigurator getConfiguration(){
		return this.configuration;
	}
	
	private void processColumns(IBioTMLFeatureColumns columns, TokenSequence instanceData){
		for(String featureUID : columns.getUIDs()){
			List<String> results = columns.getFeatureColumByUID(featureUID);
			setFeatures(results, columns.isMultiFeatureColumn(featureUID), featureUID, instanceData);
		}
	}
	
	private void setFeatures(List<String> results, boolean isMultiFeature, String featureUID, TokenSequence instanceData){
		Iterator<String> itRes = results.iterator();
		Iterator<Token> itData = instanceData.iterator();
		while(itRes.hasNext() && itData.hasNext()){
			String result = itRes.next();
			Token token = itData.next();
			if(!result.isEmpty()){
				if(getConfiguration().hasFeatureUID(featureUID)){
					if(!isMultiFeature){
						token.setFeatureValue(result, 1.0);
					} else {
						String[] resultField = result.split("\t");
						for(String res : resultField){
							if(!res.isEmpty()){
								token.setFeatureValue(res, 1.0);
							}
						}
					}
				}
			}
		}
	}
	
    /**
     * 
     * Extract the data and features from input data.
     * @param carrier Raw input data.
     * @return Processed instance with correct data and features.
     */
    public Instance pipe(Instance carrier) {
    	TokenSequence instanceData = (TokenSequence) carrier.getData();
    	
    	List<String> tokensStrings = new ArrayList<String>();
    	
    	for(int i=0; i<instanceData.size(); i++ ){
    		Token token = instanceData.get(i);
    		String tokenString = token.getText();
    		tokensStrings.add(tokenString);
    	}
    	
    	List<String> visitedUID = new ArrayList<String>();
    	for(String classUID : getConfiguration().getFeaturesUIDs()){
    		if(!visitedUID.contains(classUID)){
    			try {
    				IBioTMLFeatureGenerator classProcesser = BioTMLFeaturesManager.getInstance().getClass(classUID);
    				visitedUID.addAll(classProcesser.getUIDs());
    				IBioTMLFeatureColumns columns = classProcesser.getFeatureColumns(tokensStrings,  getConfiguration());
    				processColumns(columns, instanceData);
    			} catch (BioTMLException exc) {
    				exc.printStackTrace();
    			}	
    		}
    	}
    	return carrier;
    }

}