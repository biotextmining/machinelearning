package com.silicolife.textmining.machinelearning.biotml.core.interfaces;

import java.io.Serializable;

/**
 * 
 * Represents Annotation interface.
 * 
 * @since 1.0.0
 * @author Ruben Rodrigues ({@code rrodrigues@silicolife.com})
 */
public interface IBioTMLAnnotation extends Serializable{
	
	/**
	 * 
	 * Method to get the annotation type.
	 * 
	 * @return Annotation type string.
	 */
	public String getAnnotationType();
	
	/**
	 * 
	 * Method to get the annotation score.
	 * 
	 * @return Annotation score.
	 */
	public Double getAnnotationScore();

}
