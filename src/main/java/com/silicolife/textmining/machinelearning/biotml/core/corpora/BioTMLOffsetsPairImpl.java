package com.silicolife.textmining.machinelearning.biotml.core.corpora;

import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLOffsetsPair;

public class BioTMLOffsetsPairImpl implements IBioTMLOffsetsPair{

	private static final long serialVersionUID = 1L;
	private long startOffset;
	private long endOffset;

	public BioTMLOffsetsPairImpl(long startOffset, long endOffset){
		insertOffsets(startOffset, endOffset);
	}

	private void insertOffsets(long startOffset, long endOffset) {
		if(endOffset<startOffset){
			this.endOffset = startOffset;
			this.startOffset = endOffset;
		}else{
			this.startOffset = startOffset;
			this.endOffset = endOffset;
		}
	}

	@Override
	public long getStartOffset() {
		return startOffset;
	}

	@Override
	public long getEndOffset() {
		return endOffset;
	}
	
	@Override
	public boolean offsetsEquals(long startOffset, long endOffset){
		IBioTMLOffsetsPair pairToCompare = new BioTMLOffsetsPairImpl(startOffset, endOffset);
		return this.equals(pairToCompare);
	}
	
	@Override
	public boolean containsInside(long startOffset, long endOffset){
		IBioTMLOffsetsPair pairToBeInside = new BioTMLOffsetsPairImpl(startOffset, endOffset);
		return this.containsInside(pairToBeInside);
	}
	
	@Override
	public boolean containsInside(IBioTMLOffsetsPair pairToBeInside){
		if(getStartOffset()<= pairToBeInside.getStartOffset() && getEndOffset() >= pairToBeInside.getEndOffset()){
			return true;
		}
		return false;
	}
	
	@Override
	public boolean offsetsOverlap(IBioTMLOffsetsPair pairToCompare){
		return offsetsOverlap(pairToCompare.getStartOffset(), pairToCompare.getEndOffset());
	}

	@Override
	public boolean offsetsOverlap(long startOffset, long endOffset){
		if(!(this.getEndOffset() <= startOffset) && !(this.getStartOffset() >= endOffset)){
			return true;
		}
		return false;
	}

	@Override
	public boolean startsWith(IBioTMLOffsetsPair pairToCompare) {
		return startsWith(pairToCompare.getStartOffset());
	}

	@Override
	public boolean startsWith(long startOffset) {
		return getStartOffset() == startOffset;
	}
	
	@Override
	public boolean endsWith(IBioTMLOffsetsPair pairToCompare) {
		return endsWith(pairToCompare.getEndOffset());
	}

	@Override
	public boolean endsWith(long endOffset) {
		return getEndOffset() == endOffset;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (endOffset ^ (endOffset >>> 32));
		result = prime * result + (int) (startOffset ^ (startOffset >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BioTMLOffsetsPairImpl other = (BioTMLOffsetsPairImpl) obj;
		if (endOffset != other.endOffset)
			return false;
		if (startOffset != other.startOffset)
			return false;
		return true;
	}

	@Override
	public int compareTo(IBioTMLOffsetsPair o) {
		if(this.equals(o)){
			return 0;
		}
		if(this.getStartOffset()>o.getStartOffset()){
			return 1;
		}
		if(this.getStartOffset()<o.getStartOffset()){
			return -1;
		}
		if(this.getEndOffset()> o.getEndOffset()){
			return 1;
		}
		if(this.getEndOffset()< o.getEndOffset()){
			return -1;
		}
		return 0;
	}

	@Override
	public String toString() {
		return "(" + startOffset + " - " + endOffset + ")";
	}

}
