package com.silicolife.textmining.machinelearning.biotml.core.interfaces;

public interface IBioTMLOffsetsPair extends Comparable<IBioTMLOffsetsPair>{
	
	public long getStartOffset();
	
	public long getEndOffset();
	
	boolean offsetsEquals(long startOffset, long endOffset);
	
	boolean containsInside(long startOffset, long endOffset);
	
	boolean containsInside(IBioTMLOffsetsPair pairToCompare);
	
	public boolean offsetsOverlap(IBioTMLOffsetsPair pairToCompare);

	public boolean offsetsOverlap(long startOffset, long endOffset);
	
}
