package com.silicolife.textmining.machinelearning.biotml.core.nlp.clearnlp;

/**
 * 
 * Modified ClearNLP StringBooleanPair to accept offsets.
 * 
 * @since 1.0.0
 * @author Ruben Rodrigues ({@code rrodrigues@silicolife.com})
 */

public class StringBooleanPair
{
	public String  s;
	public boolean b;
	public long start;
	public long end;
	
	/**
	 * 
	 * Initializes a string with offsets.
	 * 
	 * @param s - String text.
	 * @param b - Boolean to block the modification of the string.
	 * @param start - Start offset.
	 * @param end - End offset.
	 */
	public StringBooleanPair(String s, boolean b, long start, long end)
	{
		set(s, b, start, end);
	}
	
	/**
	 * 
	 * Method to modify the string.
	 * 
	 * @param s - String text.
	 * @param b - Boolean to block the modification of the string.
	 * @param start - Start offset.
	 * @param end - End offset.
	 */
	public void set(String s, boolean b, long start, long end)
	{
		this.s = s;
		this.b = b;
		this.start = start;
		this.end = end;
	}
}
