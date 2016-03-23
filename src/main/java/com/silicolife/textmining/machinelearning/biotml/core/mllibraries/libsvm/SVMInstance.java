package com.silicolife.textmining.machinelearning.biotml.core.mllibraries.libsvm;

import libsvm.svm_node;

/**
 * 
 * Represents an SVM instance.
 * This object is based in the Refactored LibSVM of Syeed Ibn Faiz - University of Western Ontario modified to use the latest LibSVM.
 * 
 * @since 1.0.0
 * @author Ruben Rodrigues ({@code rrodrigues@silicolife.com})<br>
 * <b>Original Author:</b>  Syeed Ibn Faiz ({@code syeedibnfaiz@gmail.com})
 */

public class SVMInstance {
    private double label;
    private svm_node[] data;
    
    /**
     * 
     * Initializes the SVM instance with a label and a array of {@link svm_node} (original LibSVM structures).
     * The svm_nodes indices must be in ASCENDING order.
     * 
     * @param label - Double that represents the label class or regression.
     * @param data - Array of {@link svm_node} in ascending order of index that represents the instance.
     */
    public SVMInstance(double label, svm_node[] data) {
        this.label = label;
        this.data = data;
    }
    
    /**
     * 
     * Initializes the SVM instance with a array of {@link svm_node} (original LibSVM structures) to be used in label prediction.
     * The svm_nodes indices must be in ASCENDING order.
     * Note: the default label of this instance is 0.0. If the 0.0 value is used as label, please modify the default label to other unused value.
     * Otherwise, the prediction results could be induced wrongly.
     * 
     * @param data - Array of {@link svm_node} in ascending order of index that represents the instance.
     */
    public SVMInstance(svm_node[] data) {
        this.label = 0.0;
        this.data = data;
    }
    
    /**
     * 
     * Method to get the SVM instance data.
     * 
     * @return List of {@link svm_node}.
     */
    public svm_node[] getData() {
        return data;
    }

    /**
     * 
     * Method to set the SVM instance data.
     * 
     * @param data - Array of {@link svm_node} in ascending order of index that represents the instance..
     */
    public void setData(svm_node[] data) {
        this.data = data;
    }

    /**
     * 
     * Method to get the SVM instance label.
     * 
     * @return Double that represents the label class or regression.
     */
    public double getLabel() {
        return label;
    }

    /**
     * 
     * Method to set the SVM instance label.
     * 
     * @param label - Double that represents the label class or regression.
     */
    public void setLabel(double label) {
        this.label = label;
    }            
}
