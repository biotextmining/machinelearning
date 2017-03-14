package com.silicolife.textmining.machinelearning.biotml.core.interfaces;

import java.io.Serializable;

/**
 * 
 * BioTML offsets pair interface.
 * 
 * @since 1.0.2
 * @version 1.0.2
 * @author Ruben Rodrigues ({@code rrodrigues@silicolife.com})
 */

public interface IBioTMLOffsetsPair extends Comparable<IBioTMLOffsetsPair>, Serializable{
	
	/**
	 * 
	 * Method to get the start offset.
	 * 
	 * @return Start offset.
	 */
	public long getStartOffset();
	
	/**
	 * 
	 * Method to get the end offset.
	 * 
	 * @return End offset.
	 */
	public long getEndOffset();
	
	/**
	 * 
	 * Method to verify if the inputed offsets are equal to this pair of offsets.
	 * 
	 * @param startOffset
	 * @param endOffset
	 * @return Boolean
	 */
	public boolean offsetsEquals(long startOffset, long endOffset);
	
	/**
	 * 
	 * Method to verify if the inputed offsets are equal or between this pair of offsets.
	 * 
	 * @param startOffset
	 * @param endOffset
	 * @return
	 */
	public boolean containsInside(long startOffset, long endOffset);
	
	/**
	 * 
	 * Method to verify if the inputed pair of offsets are equal or between this pair of offsets.
	 * 
	 * @param pairToCompare
	 * @return
	 */
	public boolean containsInside(IBioTMLOffsetsPair pairToCompare);
	
	/**
	 * 
	 * Method to verify if the offsets overlaps in start or end or both to this pair of offsets.
	 * 
	 * @param pairToCompare
	 * @return
	 */
	public boolean offsetsOverlap(IBioTMLOffsetsPair pairToCompare);

	/**
	 * 
	 * Method to verify if the offsets overlaps in start or end or both to this pair of offsets.
	 * 
	 * @param pairToCompare
	 * @return
	 */
	public boolean offsetsOverlap(long startOffset, long endOffset);
	
	
	public boolean startsWith(IBioTMLOffsetsPair pairToCompare);
	
	public boolean startsWith(long startOffset);
	
	public boolean endsWith(IBioTMLOffsetsPair pairToCompare);
	
	public boolean endsWith(long endOffset);
}
