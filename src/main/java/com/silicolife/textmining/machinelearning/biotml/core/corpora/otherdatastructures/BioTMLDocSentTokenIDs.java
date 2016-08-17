package com.silicolife.textmining.machinelearning.biotml.core.corpora.otherdatastructures;

public class BioTMLDocSentTokenIDs {
	
	private long docId;
	private int sentId;
	private int tokenId;
	private int annotTokenRelationStartIndex = -1;
	private int annotTokenRelationEndIndex = -1;
	private boolean onlyAnnotations = false;

	public BioTMLDocSentTokenIDs(long docId, int sentId){
		this(docId, sentId, -1, -1, -1, false);
	}
	
	public BioTMLDocSentTokenIDs(long docId, int sentId, int annotTokenRelationStartIndex, int annotTokenRelationEndIndex, boolean onlyAnnotations){
		this(docId, sentId, -1, annotTokenRelationStartIndex, annotTokenRelationEndIndex, onlyAnnotations);
	}
	
	public BioTMLDocSentTokenIDs(long docId, int sentId, int tokenId, int annotTokenRelationStartIndex, int annotTokenRelationEndIndex, boolean onlyAnnotations){
		this.docId = docId;
		this.sentId = sentId;
		this.tokenId = tokenId;
		this.annotTokenRelationStartIndex = annotTokenRelationStartIndex;
		this.annotTokenRelationEndIndex = annotTokenRelationEndIndex;
		this.onlyAnnotations = onlyAnnotations;
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

	@Override
	public String toString() {
		return "BioTMLDocSentTokenIDs [docId=" + docId + ", sentId=" + sentId + ", tokenId=" + tokenId
				+ ", annotTokenRelationStartIndex=" + annotTokenRelationStartIndex + ", annotTokenRelationEndIndex="
				+ annotTokenRelationEndIndex + ", onlyAnnotations=" + onlyAnnotations + "]";
	}
	
}
