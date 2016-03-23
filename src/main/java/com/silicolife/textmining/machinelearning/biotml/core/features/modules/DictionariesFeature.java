package com.silicolife.textmining.machinelearning.biotml.core.features.modules;

import java.io.IOException;
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
import com.silicolife.textmining.machinelearning.biotml.core.resources.datalists.BioTMLDataLists;

public class DictionariesFeature implements IBioTMLFeatureGenerator{

	public Set<String> getUIDs() {
		Set<String> uids = new TreeSet<String>();
		uids.add("INCHEMICALLIST");
		uids.add("INFREQUENTLIST");
		uids.add("INCLUESLIST");
		return uids;
	}
	
	public Set<String> getRecomendedUIDs(){
		return new TreeSet<String>();
	}
	
	public Map<String, String> getUIDInfos() {
		Map<String, String> infoMap = new HashMap<>();
		infoMap.put("INCHEMICALLIST", "Verifies if the token is present in the BioTML chemical list.");
		infoMap.put("INFREQUENTLIST", "Verifies if the token is present in the 10000 most frequent words.");
		infoMap.put("INCLUESLIST", "Verifies if the token is present in clues list.");
		return infoMap;
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
					features.addTokenFeature(isInDataList(token, uID), uID);
				}
			}		
		}

		features.updateTokenFeaturesUsingAssociationProcess(tokenAnnotProcess);

		return features;
	}

	public void cleanMemory(){
	}
	
	private String isInDataList(String token, String uid) throws BioTMLException {
		try{
		if(uid.equals("INCHEMICALLIST")){
			if(BioTMLDataLists.getInstance().findStringInChemicalList(token)){
				return uid;
			}
		}
		if(uid.equals("INFREQUENTLIST")){
			if(BioTMLDataLists.getInstance().findStringInFrequentList(token)){
				return uid;
			}
		}
		if(uid.equals("INCLUESLIST")){
			if(BioTMLDataLists.getInstance().findStringInClueList(token)){
				return uid;
			}
		}
		}catch(IOException|ClassNotFoundException exc){
			throw new BioTMLException(exc);
		}
		return new String();
	}
	
}
