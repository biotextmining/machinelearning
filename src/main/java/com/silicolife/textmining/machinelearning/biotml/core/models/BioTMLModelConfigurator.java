package com.silicolife.textmining.machinelearning.biotml.core.models;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLModelConfigurator;
import com.silicolife.textmining.machinelearning.biotml.core.mllibraries.BioTMLAlgorithms;

import libsvm.svm_parameter;

/**
 * 
 * A class that defines all settings related to Mallet ML model.
 * 
 * @since 1.0.0
 * @author Ruben Rodrigues ({@code rrodrigues@silicolife.com})
 */

public class BioTMLModelConfigurator implements IBioTMLModelConfigurator{

	private static final long serialVersionUID = 1L;
	private String classType;
	private String ieType;
	private int modelOrder;
	private int numThreads;
	private String algorithm;
	private svm_parameter svmparams;
	private String nlpSystemUsed;

	/**
	 * 
	 * Initializes Mallet model settings.
	 * 
	 */
	public BioTMLModelConfigurator(){
		this.classType = new String();
		this.ieType = new String();
		this.modelOrder = 1;
		this.numThreads = Runtime.getRuntime().availableProcessors();
		this.algorithm = BioTMLAlgorithms.malletcrf.toString();
		this.svmparams = svmdefparams();
		this.nlpSystemUsed = "clearnlp";
	}

	/**
	 * 
	 * Initializes Mallet model settings.
	 * 
	 * @param classType - A string that represents a trained model for a specific entity class type or relation class type.
	 * @param ieType - A string that represents the type of Information Extraction (e.g NER or RE).
	 */
	public BioTMLModelConfigurator(String classType, String	ieType){
		this.classType = classType;
		this.ieType = ieType;
		this.modelOrder = 1;
		this.numThreads = Runtime.getRuntime().availableProcessors();
		this.algorithm = BioTMLAlgorithms.malletcrf.toString();
		this.svmparams = svmdefparams();
		this.nlpSystemUsed = "clearnlp";
	}
	
	public String getClassType(){
		return classType;
	}
	
	public String getIEType(){
		return ieType;
	}
	
	public int getModelOrder(){
		return modelOrder;
	}
	
	public int getNumThreads(){
		return numThreads;
	}

	public String getAlgorithmType() {
		return algorithm;
	}
	
	public svm_parameter getSVMParameters() {
		return svmparams;
	}
	
	public String getUsedNLPSystem(){
		return nlpSystemUsed;
	}
	
	public void setModelOrder(int modelOrder){
		this.modelOrder = modelOrder;
	}
	
	public void setNumThreads(int numThreads){
		this.numThreads = numThreads;
	}

	public void setSVMParameters(svm_parameter svmparams){
		this.svmparams = svmparams;
	}

	public void setAlgorithmType(String algorithm){
		this.algorithm = algorithm;
	}
	
	public void setUsedNLPSystem(String nlpSystem){
		this.nlpSystemUsed = nlpSystem;
	}
	
    private svm_parameter svmdefparams(){
    	svm_parameter params = new svm_parameter();
        params.svm_type = svm_parameter.C_SVC;
        params.kernel_type = svm_parameter.LINEAR;
        params.degree = 3;
        params.gamma = 0;	// 1/num_features
        params.coef0 = 0;
        params.nu = 0.5;
        params.cache_size = 100;
        params.C = 1;
        params.eps = 1e-3;
        params.p = 0.1;
        params.shrinking = 1;
        params.probability = 0;
        params.nr_weight = 0;
        params.weight_label = new int[0];
        return params;
    }
    
    public String toString(){
    	StringBuilder sb = new StringBuilder();
    	sb.append("ML Algorithm: " + getAlgorithmType( )+ System.lineSeparator());
    	if(!getAlgorithmType().equals(BioTMLAlgorithms.malletsvm.toString())){
    		sb.append("Model Order: " + String.valueOf(getModelOrder())+System.lineSeparator());
    		return sb.toString();
    	}else{
    		for(Field field : getSVMParameters().getClass().getFields()){
    			if (!Modifier.isStatic(field.getModifiers())) {
    		        try {
    		        	String fieldname = field.getName();
    		        	String[] names = fieldname.split("_");
    		        	for(String name:names){
    		        		name = name.replaceFirst(".", String.valueOf(name.charAt(0)).toUpperCase());
    		        		sb.append(name);
    		        		sb.append(" ");
    		        	}
    		        	sb.deleteCharAt(sb.length()-1);
						sb.append(": " + field.get(getSVMParameters())+System.lineSeparator());
					} catch (IllegalArgumentException | IllegalAccessException e) {}
    		    }
    		}
    		return sb.toString();
    	}
    }
	
}