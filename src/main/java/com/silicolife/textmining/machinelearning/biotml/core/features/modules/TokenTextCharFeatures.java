package com.silicolife.textmining.machinelearning.biotml.core.features.modules;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import com.silicolife.textmining.machinelearning.biotml.core.exception.BioTMLException;
import com.silicolife.textmining.machinelearning.biotml.core.features.datastructures.BioTMLAssociationProcess;
import com.silicolife.textmining.machinelearning.biotml.core.features.datastructures.BioTMLFeatureColumns;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLFeatureColumns;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLFeatureGenerator;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLFeatureGeneratorConfigurator;


/**
 * 
 * A class responsible for token text char features.
 * 
 * @since 1.0.0
 * @author Ruben Rodrigues ({@code rrodrigues@silicolife.com})
 */

public class TokenTextCharFeatures implements IBioTMLFeatureGenerator{
	
	private Set<String> uIDs;
	private Map<String, String> listMethodsByUIDFeatures;
	private Map<String, Integer> listParamByUIDFeatures;


	/**
	 * 
	 * Initializes the insertion of token text char features.
	 * 
	 */
	public  TokenTextCharFeatures(){
		this.uIDs = initUIDs();
		this.listMethodsByUIDFeatures = initListMethodsByUIDFeatures();
		this.listParamByUIDFeatures = initListParamByUIDFeatures();
	}

	public Set<String> initUIDs() {
		Set<String> uids = new TreeSet<String>();
		uids.add("2SUFFIX");
		uids.add("3SUFFIX");
		uids.add("4SUFFIX");
		uids.add("2PREFIX");
		uids.add("3PREFIX");
		uids.add("4PREFIX");
		return uids;
	}
	
	public Set<String> getRecomendedUIDs(){
		return getUIDs();
	}
	
	public Map<String, String> getUIDInfos() {
		Map<String, String> infoMap = new HashMap<>();
		infoMap.put("2SUFFIX", "The last two token characters are used as feature.");
		infoMap.put("3SUFFIX", "The last three token characters are used as feature.");
		infoMap.put("4SUFFIX", "The last four last token characters are used as feature.");
		infoMap.put("2PREFIX", "The first two token characters are used as feature.");
		infoMap.put("3PREFIX", "The first three token characters are used as feature.");
		infoMap.put("4PREFIX", "The first four token characters are used as feature.");
		return infoMap;
	}
	
	public Set<String> getUIDs() {
		return uIDs;
	}
	
	private Map<String, String> initListMethodsByUIDFeatures(){
		Map<String, String> listMethodsByUIDFeatures = new TreeMap<String, String>();
		listMethodsByUIDFeatures.put("2SUFFIX", "tokenTextCharSuffix");
		listMethodsByUIDFeatures.put("3SUFFIX", "tokenTextCharSuffix");
		listMethodsByUIDFeatures.put("4SUFFIX", "tokenTextCharSuffix");
		listMethodsByUIDFeatures.put("2PREFIX", "tokenTextCharPrefix");
		listMethodsByUIDFeatures.put("3PREFIX", "tokenTextCharPrefix");
		listMethodsByUIDFeatures.put("4PREFIX", "tokenTextCharPrefix");

		return listMethodsByUIDFeatures;
	}
	
	private Map<String, String> getListMethodsByUIDFeatures(){
		return listMethodsByUIDFeatures;
	}
	
	private Map<String, Integer> initListParamByUIDFeatures(){
		Map<String, Integer> listParamByUIDFeatures = new TreeMap<String, Integer>();
		listParamByUIDFeatures.put("2SUFFIX", 2);
		listParamByUIDFeatures.put("3SUFFIX", 3);
		listParamByUIDFeatures.put("4SUFFIX", 4);
		listParamByUIDFeatures.put("2PREFIX", 2);
		listParamByUIDFeatures.put("3PREFIX", 3);
		listParamByUIDFeatures.put("4PREFIX", 4);
		return listParamByUIDFeatures;
	}
	
	private Map<String, Integer> getListParamByUIDFeatures(){
		return listParamByUIDFeatures;
	}
	
	public String tokenTextCharPrefix(String token, String featureName, Integer prefixLength){
		if (token.length() > prefixLength)
			return featureName + "=" + token.substring (0, prefixLength);
		return new String();
	}
	
	public String tokenTextCharSuffix(String token, String featureName, Integer suffixLength){
		if (token.length() > suffixLength)
			return featureName + "=" + token.substring (token.length()  - suffixLength, token.length());
		return new String();
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
			for(String uID : getUIDs()){
				if(configuration.hasFeatureUID(uID)){
					String methodName = getListMethodsByUIDFeatures().get(uID);
					Integer paramInt = getListParamByUIDFeatures().get(uID);
					try {
						Method method = this.getClass().getMethod(methodName, String.class, String.class, Integer.class);
						String result = (String) method.invoke(this, token,uID, paramInt);
						features.addTokenFeature(result, uID);
					} catch (NoSuchMethodException 
							| SecurityException 
							| IllegalAccessException
							| IllegalArgumentException
							| InvocationTargetException exc) {
							throw new BioTMLException(13,exc);
					}
				}
			}
		}
		
		features.updateTokenFeaturesUsingAssociationProcess(tokenAnnotProcess);

		return features;
	}

	public void cleanMemory() {
	}

}