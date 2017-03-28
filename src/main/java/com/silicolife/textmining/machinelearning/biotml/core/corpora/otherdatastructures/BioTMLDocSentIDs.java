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
	private int annotTokenRelationStartIndex = -1;
	private int annotTokenRelationEndIndex = -1;
	private boolean onlyAnnotations = false;
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
	
	public int getAnnotTokenRelationStartIndex(){
		return annotTokenRelationStartIndex;
	}
	
	public int getAnnotTokenRelationEndIndex(){
		return annotTokenRelationEndIndex;
	}

	public boolean isOnlyAnnotations() {
		return onlyAnnotations;
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

	public void setAnnotTokenRelationStartIndex(int annotTokenRelationStartIndex) {
		this.annotTokenRelationStartIndex = annotTokenRelationStartIndex;
	}

	public void setAnnotTokenRelationEndIndex(int annotTokenRelationEndIndex) {
		this.annotTokenRelationEndIndex = annotTokenRelationEndIndex;
	}

	public void setOnlyAnnotations(boolean onlyAnnotations) {
		this.onlyAnnotations = onlyAnnotations;
	}

	public void setAssociations(List<IBioTMLAssociation> associations) {
		this.associations = associations;
	}

	public void setAssociation(IBioTMLAssociation association) {
		this.association = association;
	}
	
	
}
