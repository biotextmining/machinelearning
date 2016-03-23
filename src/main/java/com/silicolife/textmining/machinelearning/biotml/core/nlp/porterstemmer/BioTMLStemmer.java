package com.silicolife.textmining.machinelearning.biotml.core.nlp.porterstemmer;

/**
 * 
 * A class responsible for stemmer feature from Porter Stemmer for usage simplification.
 * 
 * @since 1.0.0
 * @author Ruben Rodrigues ({@code rrodrigues@silicolife.com})
 */

public class BioTMLStemmer {
	
	private String token;

	/**
	 * 
	 * Initializes the stemmer with the input token.
	 * 
	 * @param token - Token string to be stemmed.
	 */
	public BioTMLStemmer(String token){
		this.token = token;
	}
	
	private String getToken(){
		return token;
	}
	
	/**
	 * 
	 * Process the stemming of input token.
	 * 
	 * @return Token stemmed.
	 */
	public String getStem(){
		Stemmer s = new Stemmer();
		String tokenString = getToken().toLowerCase();
		char[] word = tokenString.toCharArray();
		boolean stop = false;
		int ii = 0;
		while(ii<word.length && !stop){
			if(Character.isLetter(word[ii])){
    			ii++;
			}
			else{
				stop = true;
			}
		}
		s.add(word, ii);
		s.stem();
		return s.toString();
	}

}
