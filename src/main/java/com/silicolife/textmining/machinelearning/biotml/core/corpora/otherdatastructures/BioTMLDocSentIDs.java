package com.silicolife.textmining.machinelearning.biotml.core.corpora.otherdatastructures;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLAssociation;

@SuppressWarnings("rawtypes")
public class BioTMLDocSentIDs implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private long docId;
	private int sentId;
	private int tokenId = -1;
	private List<IBioTMLAssociation> associations = new ArrayList<>();
	private IBioTMLAssociation association;

	public BioTMLDocSentIDs(long docId, int sentId){
		this.docId = docId;
		this.sentId = sentId;
	}

	public long getDocId() {
		return docId;
	}

	public int getSentId() {
		return sentId;
	}

	public int getTokenId() {
		return tokenId;
	}

	public List<IBioTMLAssociation> getAssociations() {
		return associations;
	}

	public IBioTMLAssociation getAssociation() {
		return association;
	}

	public void setTokenId(int tokenId) {
		this.tokenId = tokenId;
	}

	public void setAssociations(List<IBioTMLAssociation> associations) {
		this.associations = associations;
	}

	public void setAssociation(IBioTMLAssociation association) {
		this.association = association;
	}
	
	
}
