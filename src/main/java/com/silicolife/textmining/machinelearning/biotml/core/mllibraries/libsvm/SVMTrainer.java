package com.silicolife.textmining.machinelearning.biotml.core.mllibraries.libsvm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import libsvm.svm;
import libsvm.svm_model;
import libsvm.svm_node;
import libsvm.svm_parameter;
import libsvm.svm_problem;

/**
 * 
 * Represents the SVM trainer.
 * This object is based in the Refactored LibSVM of Syeed Ibn Faiz - University of Western Ontario modified to use the latest LibSVM.
 * 
 * @since 1.0.0
 * @author Ruben Rodrigues ({@code rrodrigues@silicolife.com})<br>
 * <b>Original Author:</b> Syeed Ibn Faiz ({@code syeedibnfaiz@gmail.com})
 */

public class SVMTrainer {
	
    private static svm_problem prepareProblem(SVMInstance[] instances) {
        svm_problem prob = new svm_problem();
        prob.l = instances.length;
        prob.y = new double[instances.length];
        prob.x = new svm_node[instances.length][];
        
        for (int i = 0; i < instances.length; i++) {
        	prob.y[i] = instances[i].getLabel();
        	prob.x[i] = instances[i].getData();
        }
        return prob;
    }
    
    /**
     * 
     * Method that builds an SVM model using an array of SVM instances and SVM parameters.
     * Please read LibSVM documentation to define the svm_parameter object.
     * 
     * @param instances - Array of {@link SVMInstance}.
     * @param param - SVM parameter object.
     * @return SVM model.
     */
    public static svm_model train(SVMInstance[] instances, svm_parameter param) {
        //prepare svm_problem
        svm_problem prob = prepareProblem(instances);
        
        String error_msg = svm.svm_check_parameter(prob, param);

        if (error_msg != null) {
            System.err.print("ERROR: " + error_msg + "\n");
        }
                
        return svm.svm_train(prob, param);
    }
    
    /**
     * 
     * Method that builds an SVM model using a list of SVM instances and SVM parameters.
     * Please read LibSVM documentation to define the svm_parameter object.
     * 
     * @param instances - List of {@link SVMInstance}.
     * @param param - SVM parameter object.
     * @return SVM model.
     */
    public static svm_model train(List<SVMInstance> instances, svm_parameter param) {
    	SVMInstance[] array = new SVMInstance[instances.size()];
        array = instances.toArray(array);
        return train(array, param);
    }
    
    /**
     * 
     * Method that performs N-fold cross validation.
     * Please read LibSVM documentation to define the svm_parameter object.
     * 
     * @param instances - Array of {@link SVMInstance}.
     * @param param parameters - SVM parameter object.
     * @param nr_fold - Number of folds (N).
     * @param binary - Boolean if doing binary classification or not.
     */
    public static void doCrossValidation(SVMInstance[] instances, svm_parameter param, int nr_fold, boolean binary) {
        svm_problem prob = prepareProblem(instances);
        
        int i;
        int total_correct = 0;
        double total_error = 0;
        double sumv = 0, sumy = 0, sumvv = 0, sumyy = 0, sumvy = 0;
        double[] target = new double[prob.l];

        svm.svm_cross_validation(prob, param, nr_fold, target);
        if (param.svm_type == svm_parameter.EPSILON_SVR
                || param.svm_type == svm_parameter.NU_SVR) {
            for (i = 0; i < prob.l; i++) {
                double y = prob.y[i];
                double v = target[i];
                total_error += (v - y) * (v - y);
                sumv += v;
                sumy += y;
                sumvv += v * v;
                sumyy += y * y;
                sumvy += v * y;
            }
            System.out.print("Cross Validation Mean squared error = " + total_error / prob.l + "\n");
            System.out.print("Cross Validation Squared correlation coefficient = "
                    + ((prob.l * sumvy - sumv * sumy) * (prob.l * sumvy - sumv * sumy))
                    / ((prob.l * sumvv - sumv * sumv) * (prob.l * sumyy - sumy * sumy)) + "\n");
        } else {
            int tp = 0;
            int fp = 0;
            int fn = 0;
            
            for (i = 0; i < prob.l; i++) {
                if (target[i] == prob.y[i]) {
                    ++total_correct;
                    if (prob.y[i] > 0) {
                        tp++;
                    }
                } else if (prob.y[i] > 0) {
                    fn++;
                } else if (prob.y[i] < 0) {
                    fp++;
                }
            }
            System.out.print("Cross Validation Accuracy = " + 100.0 * total_correct / prob.l + "%\n");
            if (binary) {
                double precision = (double) tp / (tp + fp);
                double recall = (double) tp / (tp + fn);
                System.out.println("Precision: " + precision);
                System.out.println("Recall: " + recall);
                System.out.println("FScore: " + 2 * precision * recall / (precision + recall));
            }
        }
    }
    
    /**
     * 
     * Method that performs N-fold cross validation without random fold splitting.
     * Please read LibSVM documentation to define the svm_parameter object.
     * 
     * @param instances - Array of {@link SVMInstance}.
     * @param param parameters - SVM parameter object.
     * @param nr_fold - Number of folds (N).
     * @param binary - Boolean if doing binary classification or not.
     */
    public static void doInOrderCrossValidation(SVMInstance[] instances, svm_parameter param, int nr_fold, boolean binary) {        
        int size = instances.length;
        int chunkSize = size/nr_fold;
        int begin = 0;
        int end = chunkSize - 1;
        int tp = 0;
        int fp = 0;
        int fn = 0;
        
        for (int i = 0; i < nr_fold; i++) {
            System.out.println("Iteration: " + (i+1));
            List<SVMInstance> trainingInstances = new ArrayList<SVMInstance>();
            List<SVMInstance> testingInstances = new ArrayList<SVMInstance>();
            for (int j = 0; j < size; j++) {
                if (j >= begin && j <= end) {
                    testingInstances.add(instances[j]);
                } else {
                    trainingInstances.add(instances[j]);
                }
            }                                    
            
            svm_model trainModel = train(trainingInstances, param);
            double[] predictions = SVMPredictor.predict(testingInstances, trainModel);
            for (int k = 0; k < predictions.length; k++) {
                
                if (predictions[k] == testingInstances.get(k).getLabel()) {
                //if (Math.abs(predictions[k] - testingInstances.get(k).getLabel()) < 0.00001) {
                    if (testingInstances.get(k).getLabel() > 0) {
                        tp++;
                    }
                } else if (testingInstances.get(k).getLabel() > 0) {
                    fn++;
                } else if (testingInstances.get(k).getLabel() < 0) {
                    //System.out.println(testingInstances.get(k).getData());
                    fp++;
                }
            }
            //update
            begin = end+1;
            end = begin + chunkSize - 1;
            if (end >= size) {
                end = size-1;
            }
        }
        
        double precision = (double) tp / (tp + fp);
        double recall = (double) tp / (tp + fn);
        System.out.println("Precision: " + precision);
        System.out.println("Recall: " + recall);
        System.out.println("FScore: " + 2 * precision * recall / (precision + recall));
    }
    
    /**
     * 
     * Method that saves a model in defined path file.
     * 
     * @param model - LibSVM model ({@link svm_model}).
     * @param filePath - String file path.
     * @throws IOException
     */
    public static void saveModel(svm_model model, String filePath) throws IOException {
        svm.svm_save_model(filePath, model);
    }
}
