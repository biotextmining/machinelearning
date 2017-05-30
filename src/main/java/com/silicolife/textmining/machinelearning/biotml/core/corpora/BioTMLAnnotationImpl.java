package com.silicolife.textmining.machinelearning.biotml.core.corpora;

import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLAnnotation;

public class BioTMLAnnotationImpl implements IBioTMLAnnotation{

	private static final long serialVersionUID = 1L;
	private String annotationType;
	private Double annotationScore;

	public BioTMLAnnotationImpl(String annotationType, Double annotationScore){
		this.annotationType = annotationType;
		this.annotationScore = annotationScore;
	}
	
	public BioTMLAnnotationImpl(String annotationType){
		this.annotationType = annotationType;
		this.annotationScore = 100000.0;
	}
	
	@Override
	public String getAnnotationType() {
		return annotationType;
	}

	@Override
	public Double getAnnotationScore() {
		return annotationScore;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((annotationScore == null) ? 0 : annotationScore.hashCode());
		result = prime * result + ((annotationType == null) ? 0 : annotationType.hashCode());
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
		BioTMLAnnotationImpl other = (BioTMLAnnotationImpl) obj;
		if (annotationScore == null) {
			if (other.annotationScore != null)
				return false;
		} else if (!annotationScore.equals(other.annotationScore))
			return false;
		if (annotationType == null) {
			if (other.annotationType != null)
				return false;
		} else if (!annotationType.equals(other.annotationType))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "BioTMLAnnotationImpl [annotationType=" + annotationType + ", annotationScore=" + annotationScore + "]";
	}	

}
