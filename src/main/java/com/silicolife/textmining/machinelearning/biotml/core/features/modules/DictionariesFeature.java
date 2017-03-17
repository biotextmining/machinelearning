package com.silicolife.textmining.machinelearning.biotml.core.features.modules;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.silicolife.textmining.machinelearning.biotml.core.exception.BioTMLException;
import com.silicolife.textmining.machinelearning.biotml.core.features.datastructures.BioTMLFeatureColumns;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLAssociation;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLFeatureColumns;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLFeatureGenerator;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLFeatureGeneratorConfigurator;
import com.silicolife.textmining.machinelearning.biotml.core.resources.datalists.BioTMLDataLists;

public class DictionariesFeature implements IBioTMLFeatureGenerator{

	public Set<String> getNERFeatureIds() {
		Set<String> uids = new TreeSet<String>();
		uids.add("INCHEMICALLIST");
		uids.add("INFREQUENTLIST");
		uids.add("INCLUESLIST");
		return uids;
	}
	
	public Set<String> getRecomendedNERFeatureIds(){
		return new TreeSet<String>();
	}
	
	public Map<String, String> getNERFeatureIdsInfos() {
		Map<String, String> infoMap = new HashMap<>();
		infoMap.put("INCHEMICALLIST", "Verifies if the token is present in the BioTML chemical list.");
		infoMap.put("INFREQUENTLIST", "Verifies if the token is present in the 10000 most frequent words.");
		infoMap.put("INCLUESLIST", "Verifies if the token is present in clues list.");
		return infoMap;
	}
	
	
	@Override
	public Set<String> getREFeatureIds() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, String> getREFeatureIdsInfos() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> getRecomendedREFeatureIds() {
		// TODO Auto-generated method stub
		return null;
	}

	public IBioTMLFeatureColumns getFeatureColumns(List<String> tokens,
			IBioTMLFeatureGeneratorConfigurator configuration)
			throws BioTMLException {
		
		IBioTMLFeatureColumns features = new BioTMLFeatureColumns(tokens, getNERFeatureIds(), configuration);
		
		for (int i = 0; i < tokens.size(); i++){
			String token = tokens.get(i);
			for(String uID : getNERFeatureIds()){
				if(configuration.hasFeatureUID(uID)){
					features.addTokenFeature(isInDataList(token, uID), uID);
				}
			}		
		}

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

	@Override
	public IBioTMLFeatureColumns getEventFeatureColumns(List<String> tokens, List<IBioTMLAssociation> associations,
			IBioTMLFeatureGeneratorConfigurator configuration) throws BioTMLException {
		List<String> associationStrings = new ArrayList<>();
		
		for(IBioTMLAssociation association : associations){
			associationStrings.add(association.toString());
		}
		IBioTMLFeatureColumns features = new BioTMLFeatureColumns(associationStrings, getREFeatureIds(), configuration);
		for(IBioTMLAssociation association : associations){
			//features
		}
		
		return features;
	}
	
}
