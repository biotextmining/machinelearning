package com.silicolife.textmining.machinelearning.biotml.core.evaluation.datastrucures;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLConfusionMatrix;

public class BioTMLConfusionMatrixImpl<O> implements IBioTMLConfusionMatrix<O>{
	
	private static final long serialVersionUID = 1L;
	private List<O> truePositives;
	private List<O> trueNegatives;
	private List<O> falsePositives;
	private List<O> falseNegatives;
	
	public BioTMLConfusionMatrixImpl(){
		this.truePositives = new ArrayList<>();
		this.trueNegatives = new ArrayList<>();
		this.falsePositives = new ArrayList<>();
		this.falseNegatives = new ArrayList<>();
	}

	public List<O> getTruePositives() {
		return truePositives;
	}

	public List<O> getTrueNegatives() {
		return trueNegatives;
	}

	public List<O> getFalsePositives() {
		return falsePositives;
	}

	public List<O> getFalseNegatives() {
		return falseNegatives;
	}
	
	public void addTruePositive(O truePositive){
		getTruePositives().add(truePositive);
	}
	
	public void addAllTruePositives(Collection<O> truePositives){
		getTruePositives().addAll(truePositives);
	}
	
	public void addTrueNegative(O trueNegative){
		getTrueNegatives().add(trueNegative);
	}
	
	public void addAllTrueNegatives(Collection<O> truePositives){
		getTrueNegatives().addAll(truePositives);
	}
	
	public void addFalsePositive(O falsePositive){
		getFalsePositives().add(falsePositive);
	}
	
	public void addAllFalsePositives(Collection<O> falsePositives){
		getFalsePositives().addAll(falsePositives);
	}
	
	public void addFalseNegative(O falseNegative){
		getFalseNegatives().add(falseNegative);
	}
	
	public void addAllFalseNegatives(Collection<O> falseNegatives){
		getFalseNegatives().addAll(falseNegatives);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((falseNegatives == null) ? 0 : falseNegatives.hashCode());
		result = prime * result + ((falsePositives == null) ? 0 : falsePositives.hashCode());
		result = prime * result + ((trueNegatives == null) ? 0 : trueNegatives.hashCode());
		result = prime * result + ((truePositives == null) ? 0 : truePositives.hashCode());
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
		@SuppressWarnings("rawtypes")
		BioTMLConfusionMatrixImpl other = (BioTMLConfusionMatrixImpl) obj;
		if (falseNegatives == null) {
			if (other.falseNegatives != null)
				return false;
		} else if (!falseNegatives.equals(other.falseNegatives))
			return false;
		if (falsePositives == null) {
			if (other.falsePositives != null)
				return false;
		} else if (!falsePositives.equals(other.falsePositives))
			return false;
		if (trueNegatives == null) {
			if (other.trueNegatives != null)
				return false;
		} else if (!trueNegatives.equals(other.trueNegatives))
			return false;
		if (truePositives == null) {
			if (other.truePositives != null)
				return false;
		} else if (!truePositives.equals(other.truePositives))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "BioTMLConfusionMatrixImpl [truePositives=" + truePositives + ", trueNegatives=" + trueNegatives
				+ ", falsePositives=" + falsePositives + ", falseNegatives=" + falseNegatives + "]";
	}

}
