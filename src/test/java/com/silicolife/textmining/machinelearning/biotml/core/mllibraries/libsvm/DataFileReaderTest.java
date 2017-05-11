package com.silicolife.textmining.machinelearning.biotml.core.mllibraries.libsvm;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import com.silicolife.textmining.machinelearning.biotml.core.mllibraries.libsvm.SVMInstance;

import libsvm.svm_node;


public class DataFileReaderTest {
    
    public static SVMInstance[] readDataFile(String fileName) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(fileName));        
        
        ArrayList<Double> labels = new ArrayList<Double>();
        ArrayList<svm_node[]> vectors = new ArrayList<svm_node[]>();
        
        String line;
        int lineCount = 0;
        while ((line = reader.readLine()) != null) {
            lineCount++;
            String[] tokens = line.split("\\s+");
            if (tokens.length < 2) {                
                System.err.println("Inappropriate file format: " + fileName);
                System.err.println("Error in line " + lineCount);
                System.exit(-1);
            }
            
            labels.add(Double.parseDouble(tokens[0]));            
            svm_node[] vector = new svm_node[tokens.length - 1];
            
            for (int i = 1; i < tokens.length; i++) {
                String[] fields = tokens[i].split(":");
                if (fields.length < 2) {
                    System.err.println("Inappropriate file format: " + fileName);
                    System.err.println("Error in line " + lineCount);
                    System.exit(-1);
                }
                svm_node node = new svm_node();
                node.index = Integer.parseInt(fields[0]);
                node.value = Double.parseDouble(fields[1]);
                vector[i-1] = node;
            }
            
            vectors.add(vector);
        }                
        
        SVMInstance[] instances = new SVMInstance[labels.size()];
        for (int i = 0; i < instances.length; i++) {
            instances[i] = new SVMInstance(labels.get(i), vectors.get(i));
        }
        reader.close();
        return instances;
    }
}
