package com.silicolife.textmining.machinelearning.biotml.core.interfaces;

import java.util.Set;

import com.silicolife.textmining.machinelearning.biotml.core.exception.BioTMLException;

/**
 * 
 * Model matrix string representation interface.
 * 
 * @since 1.0.0
 * @author Ruben Rodrigues ({@code rrodrigues@silicolife.com})
 */

public interface IBioTMLModelMatrixToPrint {
	
	/**
	 * 
	 * Method to fill the matrix by row.
	 * 
	 * @param rowString - Array of strings that represents the matrix row.
	 */
	
	public void addMatrixRow(String[] rowString) throws BioTMLException;
	
	/**
	 * 
	 * Method to save the training matrix file.
	 * 
	 * @param fileDirectory - Directory file path string. 
	 */
	public void saveMatrix(String fileDirectory)  throws BioTMLException;
	
	/**
	 * 
	 * Method to get the features used in training matrix.
	 * 
	 * @return Set of feature strings.
	 */
	public Set<String> getFeatures();

}
