package com.silicolife.textmining.machinelearning.biotml.core.mllibraries.libsvm.mallet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.silicolife.textmining.machinelearning.biotml.core.mllibraries.libsvm.SVMInstance;
import com.silicolife.textmining.machinelearning.biotml.core.mllibraries.libsvm.SVMPredictor;

import cc.mallet.classify.Classification;
import cc.mallet.classify.Classifier;
import cc.mallet.pipe.Pipe;
import cc.mallet.types.FeatureVector;
import cc.mallet.types.Instance;
import cc.mallet.types.LabelAlphabet;
import cc.mallet.types.LabelVector;
import libsvm.svm_model;
import libsvm.svm_node;
import libsvm.svm_parameter;

/**
 * 
 * Represents the SVM classifier to wrap the LibSVM into Mallet lib.
 * This object is based in the Refactored LibSVM Mallet wrapper of Syeed Ibn Faiz - University of Western Ontario modified to use the latest LibSVM.
 * 
 * @since 1.0.0
 * @author Ruben Rodrigues ({@code rrodrigues@silicolife.com})<br>
 * <b>Original Author:</b> Syeed Ibn Faiz ({@code syeedibnfaiz@gmail.com})
 */

public class SVMClassifier extends Classifier implements Serializable {

	private static final long serialVersionUID = 1L;
	private svm_model model;
	private svm_parameter params;
    private Map<String, Double> mltLabel2svmLabel;       //mapping from Mallet to SVM label
    private Map<Double, String> svmLabel2mltLabel;       //mapping from SVM label to Mallet Label
    private int[] svmIndex2mltIndex;                    //mapping from SVM Label indices (svm.label) to Mallet Label indices (targetLabelAlphabet)

    /**
     * 
     * Initializes the SVM classifier used in Mallet lib.
     * 
     * @param model - LibSVM model ({@link svm_model}).
     * @param params - LibSVM params ({@link svm_parameter}).
     * @param mLabel2sLabel - Map of Mallet labels into LibSVM label.
     * @param instancePipe - Mallet Pipe.
     */
    public SVMClassifier(svm_model model, svm_parameter params, Map<String, Double> mLabel2sLabel, Pipe instancePipe) {
        super(instancePipe);
        this.model = model;
        this.params = params;
        this.mltLabel2svmLabel = mLabel2sLabel;
        init();
    }

    private void init() {
        svmLabel2mltLabel = new HashMap<Double, String>();
        for (Entry<String, Double> entry : mltLabel2svmLabel.entrySet())
            svmLabel2mltLabel.put(entry.getValue(), entry.getKey());
        
        svmIndex2mltIndex = new int[model.nr_class];
        int[] sLabels = model.label;
        LabelAlphabet labelAlphabet = getLabelAlphabet();
        for (int sIndex = 0; sIndex < sLabels.length; sIndex++) {
            double sLabel = sLabels[sIndex];
            String mLabel = svmLabel2mltLabel.get(sLabel * 1.0);
            int mIndex = labelAlphabet.lookupIndex(mLabel.toString(), false);
            svmIndex2mltIndex[sIndex] = mIndex;
        }
    }
    
    /**
     * 
     * SVM model's label indices differ from labelAlphabet's label indices, which is why we
     * need to rearrange the score vector returned by the SVM model.
     * 
     * @param scores - Array of scores
     * @return rearranged scores 
     */
    private double[] rearrangeScores(double[] scores) {
    	
    	double[] finalScores = new double[scores.length];
        for (int i = 0; i < scores.length; i++)
        	finalScores[i] = scores[svmIndex2mltIndex[i]];
        
        return finalScores;
    }

    @Override
    public Classification classify(Instance instance) {

        double[] scores = new double[model.nr_class];
        svm_node[] vector = featureVectorToSVMNodes(instance);
        double p = SVMPredictor.predictProbability(new SVMInstance(vector), getSVMModel(), scores);

        // scores are reorganized to match the mallet labelaphabet lookupIndex
        scores = rearrangeScores(scores);
        
        //if SVM is not predicting probability then assign a score of 1.0 to the best class predicted value p
        //and 0.0 to the other classes
        if (params.probability == 0) {
            String label = svmLabel2mltLabel.get(p);
            int index = getLabelAlphabet().lookupIndex(label, false);
            for(int i=0; i<scores.length; i++){
            	scores[i] = 0.0;
            	if(i == index)
            		scores[i] = 1.0;
            }
        }
        return new Classification(instance, this, new LabelVector(getLabelAlphabet(), scores));
    }
    
    /**
     * 
     * Method to convert the Mallet instance (feature vector) into LibSVM instance data.
     * 
     * @param instance - Mallet instance (as feature vector)
     * @return Array of {@link svm_node} LibSVM instance data.
     */
    public static svm_node[] featureVectorToSVMNodes(Instance instance){	
    	FeatureVector fv = (FeatureVector) instance.getData();
    	int[] indices = fv.getIndices();
    	svm_node[] nodes = new svm_node[indices.length];
    	for(int i = 0; i< indices.length; i++){
    		svm_node node = new svm_node();
    		node.index = indices[i];
    		node.value = 1.0;
    		nodes[i] = node;
    	}
    	Arrays.sort(nodes, new Comparator<svm_node>() {
    		public int compare(final svm_node o1, final svm_node o2) {
    			if(o1.index == o2.index)
    				return 0;
    			return o1.index < o2.index ? -1 : 1;
    		}
    	} );
    	return nodes;
    }
    
    /**
     * 
     * Method to convert the Mallet instance (feature vector) into LibSVM instance data.
     * 
     * @param instance - Mallet instance (as feature vector)
     * @return Array of {@link svm_node} LibSVM instance data.
     */
    public static svm_node[] featureVectorWithFeatureSelectionToSVMNodes(Instance instance, BitSet featureSelected){	
    	FeatureVector fv = (FeatureVector) instance.getData();
    	int[] indices = fv.getIndices();
    	List<Integer> allowedIndices = new ArrayList<>();
    	for(int i = 0; i< indices.length; i++)
    		if(featureSelected.get(indices[i]))
    			allowedIndices.add(indices[i]);
    		
    	
    	svm_node[] nodes = new svm_node[allowedIndices.size()];
    	for(int i = 0; i< allowedIndices.size(); i++){
    		svm_node node = new svm_node();
    		node.index = allowedIndices.get(i);
    		node.value = 1.0;
    		nodes[i] = node;
    	}
    	Arrays.sort(nodes, new Comparator<svm_node>() {
    		public int compare(final svm_node o1, final svm_node o2) {
    			if(o1.index == o2.index)
    				return 0;
    			return o1.index < o2.index ? -1 : 1;
    		}
    	} );
    	return nodes;
    }
    
    /**
     * 
     * Method to get the LibSVM model used.
     * 
     * @return LibSVM model ({@link svm_model}).
     */
    public svm_model getSVMModel(){
    	return model;
    }
}
