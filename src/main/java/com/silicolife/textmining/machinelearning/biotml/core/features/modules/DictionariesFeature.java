package com.silicolife.textmining.machinelearning.biotml.core.features.modules;

import java.io.IOException;
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
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLToken;
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
		return new TreeSet<String>();
	}

	@Override
	public Map<String, String> getREFeatureIdsInfos() {
		return new HashMap<>();
	}

	@Override
	public Set<String> getRecomendedREFeatureIds() {
		return new TreeSet<String>();
	}

	public IBioTMLFeatureColumns<IBioTMLToken> getFeatureColumns(List<IBioTMLToken> tokens,
			IBioTMLFeatureGeneratorConfigurator configuration)
			throws BioTMLException {
		
		IBioTMLFeatureColumns<IBioTMLToken> features = new BioTMLFeatureColumns<>(tokens, getNERFeatureIds(), configuration);
		
		for (int i = 0; i < tokens.size(); i++){
			IBioTMLToken token = tokens.get(i);
			for(String uID : getNERFeatureIds()){
				if(configuration.hasFeatureUID(uID)){
					features.addBioTMLObjectFeature(isInDataList(token.getToken(), uID), uID);
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

	@SuppressWarnings("rawtypes")
	@Override
	public IBioTMLFeatureColumns<IBioTMLAssociation> getEventFeatureColumns(List<IBioTMLToken> tokens, List<IBioTMLAssociation> associations,
			IBioTMLFeatureGeneratorConfigurator configuration) throws BioTMLException {
		
		IBioTMLFeatureColumns<IBioTMLAssociation> features = new BioTMLFeatureColumns<>(associations, getREFeatureIds(), configuration);
//		for(IBioTMLAssociation association : associations){
//			//features
//		}
		
		return features;
	}
	
}
