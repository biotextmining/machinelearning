package com.silicolife.textmining.machinelearning.biotml.core.interfaces;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

public interface IBioTMLConfusionMatrix<O> extends Serializable{

	public List<O> getTruePositives();
	
	public List<O> getTrueNegatives();
	
	public List<O> getFalsePositives();
	
	public List<O> getFalseNegatives();
	
	public void addTruePositive(O truePositive);
	
	public void addAllTruePositives(Collection<O> truePositives);
	
	public void addTrueNegative(O trueNegative);
	
	public void addAllTrueNegatives(Collection<O> truePositives);
	
	public void addFalsePositive(O falsePositive);
	
	public void addAllFalsePositives(Collection<O> falsePositives);
	
	public void addFalseNegative(O falseNegative);
	
	public void addAllFalseNegatives(Collection<O> falseNegatives);
	
}
