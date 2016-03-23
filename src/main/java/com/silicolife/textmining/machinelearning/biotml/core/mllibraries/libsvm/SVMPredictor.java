package com.silicolife.textmining.machinelearning.biotml.core.mllibraries.libsvm;

import java.io.IOException;
import java.util.List;

import libsvm.svm;
import libsvm.svm_model;

/**
 * 
 * Represents the SVM predictor.
 * This object is based in the Refactored LibSVM of Syeed Ibn Faiz - University of Western Ontario modified to use the latest LibSVM.
 * 
 * @since 1.0.0
 * @author Ruben Rodrigues ({@code rrodrigues@silicolife.com})<br>
 * <b>Original Author:</b> Syeed Ibn Faiz ({@code syeedibnfaiz@gmail.com})
 */

public class SVMPredictor {

	/**
	 * 
	 * Method that predicts the labels of a list of SVM instances using a SVM model.
	 * Per default the prediction scores are printed in console.
	 * 
	 * @param instances - List of {@link SVMInstance} for label prediction.
	 * @param model - LibSVM model ({@link svm_model}).
	 * @return Array of predicted labels of the inputed {@link SVMInstance} list using the {@link svm_model}.
	 */
    public static double[] predict(List<SVMInstance> instances, svm_model model) {
        return predict(instances, model, true);
    }

    /**
     * 
     * Method that predicts the labels of a list of SVM instances using a SVM model.
     * 
     * @param instances - List of {@link SVMInstance} for label prediction.
     * @param model - LibSVM model ({@link svm_model}).
     * @param displayResult - Boolean to activate/deactivate prediction scores printing in console.
     * @return Array of predicted labels of the inputed {@link SVMInstance} list using the {@link svm_model}.
     */
    public static double[] predict(List<SVMInstance> instances, svm_model model, boolean displayResult) {
        SVMInstance[] array = new SVMInstance[instances.size()];
        array = instances.toArray(array);
        return predict(array, model, displayResult);
    }

    /**
     * 
     * Method that predicts the label of a SVM instance using a SVM model.
     * 
     * @param instance - {@link SVMInstance} for label prediction.
     * @param model - LibSVM model ({@link svm_model}).
     * @param displayResult - Boolean to activate/deactivate prediction scores printing in console.
     * @return Predicted label of the inputed {@link SVMInstance} using the {@link svm_model}.
     */
    public static double predict(SVMInstance instance, svm_model model, boolean displayResult) {
        return svm.svm_predict(model, instance.getData());
    }
    
    /**
     * 
     * Method that does classification or regression on a SVM instance given a model with probability information.
     * For a classification model with probability information, this function gives probability estimates in the probabilities array with size of number of classes.
     * The number of classes can be obtained from the nr_class variable of SVM model. 
     * The class with the highest probability is returned.
     * For regression/one-class SVM, the array probability is unchanged and the returned value is the same as that of predict method.
     * 
     * @param instance - {@link SVMInstance} for label prediction.
     * @param model - LibSVM model ({@link svm_model}).
     * @param probabilities - Array of doubles that represents the probabilities of each class.
     * @return Class with the highest probability or predicted label of the inputed {@link SVMInstance} using the {@link svm_model}.
     */
    public static double predictProbability(SVMInstance instance, svm_model model, double[] probabilities) {
        return svm.svm_predict_probability(model,instance.getData(), probabilities);
    }

    /**
     * 
     * Method that predicts the labels of an array of SVM instances using a SVM model.
     * 
     * @param instances - Array of {@link SVMInstance} for label prediction.
     * @param model - LibSVM model ({@link svm_model}).
     * @param displayResult - Boolean to activate/deactivate prediction scores printing in console.
     * @return Array of predicted labels of the inputed {@link SVMInstance} list using the {@link svm_model}.
     */
    public static double[] predict(SVMInstance[] instances, svm_model model, boolean displayResult) {
        int total = 0;
        int correct = 0;

        int tp = 0;
        int fp = 0;
        int fn = 0;

        boolean binary = model.nr_class == 2;
        double[] predictions = new double[instances.length];
        int count = 0;

        for (SVMInstance instance : instances) {
            double target = instance.getLabel();
            double p = svm.svm_predict(model, instance.getData());
            predictions[count++] = p;

            ++total;
            if (p == target) {
                correct++;
                if (target > 0) {
                    tp++;
                }
            } else if (target > 0) {
                fn++;
            } else {
                fp++;
            }
        }
        if (displayResult) {
            System.out.print("Accuracy = " + (double) correct / total * 100
                    + "% (" + correct + "/" + total + ") (classification)\n");

            if (binary) {
                double precision = (double) tp / (tp + fp);
                double recall = (double) tp / (tp + fn);
                System.out.println("Precision: " + precision);
                System.out.println("Recall: " + recall);
                System.out.println("Fscore: " + 2 * precision * recall / (precision + recall));
            }
        }
        return predictions;
    }
    
    /**
     * 
     * Method that loads a SVM model file from a sting file path.
     * 
     * @param filePath - String file path.
     * @return {@link svm_model} object.
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static svm_model loadModel(String filePath) throws IOException, ClassNotFoundException {
        return svm.svm_load_model(filePath);
    }
}
