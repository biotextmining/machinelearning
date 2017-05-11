package com.silicolife.textmining.machinelearning.biotml.core.mllibraries.libsvm;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import libsvm.svm_model;
import libsvm.svm_parameter;

import org.junit.Test;

import com.silicolife.textmining.machinelearning.biotml.core.mllibraries.libsvm.SVMInstance;
import com.silicolife.textmining.machinelearning.biotml.core.mllibraries.libsvm.SVMPredictor;
import com.silicolife.textmining.machinelearning.biotml.core.mllibraries.libsvm.SVMTrainer;

public class LibSVMTest {
	
	public svm_parameter loadParams(){
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

	public void testLinearKernel(String trainFileName, String testFileName, String outputFileName, String outputModelfile, svm_parameter param) throws IOException, ClassNotFoundException {

		//Read training file
		SVMInstance[] trainingInstances = DataFileReaderTest.readDataFile(trainFileName);
		
		//Read test file
		SVMInstance[] testingInstances = DataFileReaderTest.readDataFile(testFileName);
		
		//Test params and data with cross validation
		SVMTrainer.doCrossValidation(trainingInstances, param, 10, true);
		SVMTrainer.doInOrderCrossValidation(trainingInstances, param, 10, true);
		
		//Train the model
		System.out.println("Training started...");
		svm_model model = SVMTrainer.train(trainingInstances, param);
		System.out.println("Training completed.");

		//Save the trained model
		SVMTrainer.saveModel(model, outputModelfile);
		model = SVMPredictor.loadModel(outputModelfile);

		//test predictProb
		for(SVMInstance instance: testingInstances){
			double[] scores = new double[model.nr_class];
			double p = SVMPredictor.predictProbability(instance, model, scores);
			System.out.println(p);
		}

		//Predict results
		double[] predictions = SVMPredictor.predict(testingInstances, model, true);
		writeOutputs(outputFileName, predictions);

	}
	
	private void writeOutputs(String outputFileName, double[] predictions) throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter(outputFileName));
		for (double p : predictions) {
			writer.write(String.format("%.0f\n", p));
		}
		writer.close();
	}

	@Test
	public void testSVM() throws IOException, ClassNotFoundException {        
		File file = new File("machinelearning/"+ this.getClass().getPackage().getName().replace(".", "/"));
		String path = file.getAbsolutePath();
		testLinearKernel(path+"/a1a.train", path+"/a1a.test", path+"/a1a.out", path+"/a1a.model", loadParams());
	}

}
