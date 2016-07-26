package com.silicolife.textmining.machinelearning.biotml.core.corpora;

public class BioTMLDocSentTokenIDs {
	
	private long docId;
	private int sentId;
	private int tokenId;
	private int annotTokenStartIndex;
	private int annotTokenEndIndex;

	public BioTMLDocSentTokenIDs(long docId, int sentId){
		this.docId = docId;
		this.sentId = sentId;
	}
	
	public BioTMLDocSentTokenIDs(long docId, int sentId, int tokenId){
		this.docId = docId;
		this.sentId = sentId;
		this.tokenId = tokenId;
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
	
	public int getAnnotTokenStartIndex(){
		return annotTokenStartIndex;
	}
	
	public int getAnnotTokenEndIndex(){
		return annotTokenEndIndex;
	}

	public void setAnnotTokenStartIndex(int annotTokenStartIndex) {
		this.annotTokenStartIndex = annotTokenStartIndex;
	}

	public void setAnnotTokenEndIndex(int annotTokenEndIndex) {
		this.annotTokenEndIndex = annotTokenEndIndex;
	}

	@Override
	public String toString() {
		return "BioTMLDocSentTokenIDs [docId=" + docId + ", sentId=" + sentId + ", tokenId=" + tokenId
				+ ", annotTokenStartIndex=" + annotTokenStartIndex + ", annotTokenEndIndex=" + annotTokenEndIndex + "]";
	}
	
	
	
}
