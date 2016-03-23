package com.silicolife.textmining.machinelearning.biotml.core.mllibraries.libsvm.mallet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.silicolife.textmining.machinelearning.biotml.core.mllibraries.libsvm.SVMInstance;
import com.silicolife.textmining.machinelearning.biotml.core.mllibraries.libsvm.SVMTrainer;

import libsvm.svm_model;
import libsvm.svm_parameter;
import cc.mallet.classify.ClassifierTrainer;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import cc.mallet.types.Label;

/**
 * 
 * Represents the SVM classifier trainer to wrap the LibSVM into Mallet lib.
 * This object is based in the Refactored LibSVM Mallet wrapper of Syeed Ibn Faiz - University of Western Ontario modified to use the latest LibSVM.
 * 
 * @since 1.0.0
 * @author Ruben Rodrigues ({@code rrodrigues@silicolife.com})<br>
 * <b>Original Author:</b> Syeed Ibn Faiz ({@code syeedibnfaiz@gmail.com})
 */

public class SVMClassifierTrainer extends ClassifierTrainer<SVMClassifier> {

    private SVMClassifier classifier;
    private Map<String, Double> mLabel2sLabel;
    private int numClasses;
    private svm_parameter param;

//    /**
//     * 
//     * Initializes the SVM classifier trainer with LibSVM default parameters.
//     * 
//     */
//    public SVMClassifierTrainer() {
//        super();
//        mLabel2sLabel = new HashMap<String, Double>();
//        param = initdefparams();
//    }
    
    /**
     * 
     * Initializes the SVM classifier trainer with LibSVM parameters defined by user.
     * 
     * @param params - LibSVM parameters ({@link svm_parameter}).
     */
    public SVMClassifierTrainer(svm_parameter params) {
        super();
        mLabel2sLabel = new HashMap<String, Double>();
        param = params;
    }
    
//    private svm_parameter initdefparams(){
//    	svm_parameter params = new svm_parameter();
//        params.svm_type = svm_parameter.C_SVC;
//        params.kernel_type = svm_parameter.LINEAR;
//        params.degree = 3;
//        params.gamma = 0;	// 1/num_features
//        params.coef0 = 0;
//        params.nu = 0.5;
//        params.cache_size = 100;
//        params.C = 1;
//        params.eps = 1e-3;
//        params.p = 0.1;
//        params.shrinking = 1;
//        params.probability = 0;
//        params.nr_weight = 0;
//        params.weight_label = new int[0];
//        return params;
//    }

    /**
     * 
     * Method to get the LibSVM parameters.
     * 
     * @return LibSVM parameters ({@link svm_parameter}).
     */
    public svm_parameter getParam() {
        return param;
    }

    /**
     * 
     * Method to the SVM classifier.
     * 
     * @return {@link SVMClassifier}.
     */
    public SVMClassifier getClassifier() {
        return classifier;
    }

    /**
     * 
     * Trains a SVM classifier using a instance list.
     * 
     * @return {@link SVMClassifier}. 
     */
    public SVMClassifier train(InstanceList trainingSet) {
        cleanUp();
        svm_model model = SVMTrainer.train(getSVMInstances(trainingSet), param);
        classifier = new SVMClassifier(model, param, mLabel2sLabel, trainingSet.getPipe());
        return classifier;
    }

    private void cleanUp() {
        mLabel2sLabel.clear();
        numClasses = 0;
    }

    private List<SVMInstance> getSVMInstances(InstanceList instanceList) {
        List<SVMInstance> list = new ArrayList<SVMInstance>();
        for (Instance instance : instanceList) {
            list.add(new SVMInstance(getLabel((Label) instance.getTarget()), SVMClassifier.featureVectorToSVMNodes(instance)));
        }
        return list;
    }

    private double getLabel(Label target) {
        Double label = mLabel2sLabel.get(target.toString());
        if (label == null) {
            numClasses++;
            label = 1.0 * numClasses;
            mLabel2sLabel.put(target.toString(), label);
        }
        return label;
    }
}
