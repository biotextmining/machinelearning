package com.silicolife.textmining.machinelearning.biotml.core.models;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.Iterator;
import java.util.Set;

import com.silicolife.textmining.machinelearning.biotml.core.exception.BioTMLException;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLModelMatrixToPrint;

/**
 * 
 * A class that represents the model training matrix.
 * 
 * @since 1.0.0
 * @author Ruben Rodrigues ({@code rrodrigues@silicolife.com})
 */
public class ModelMatrixToPrint implements IBioTMLModelMatrixToPrint{
	
	private Set<String> features;
	private File matrixFile;
	
	/**
	 * 
	 * Initializes the representation of model training matrix.
	 * 
	 * @param features - Set of features.
	 * @throws BioTMLException 
	 */
	public ModelMatrixToPrint(Set<String> features) throws BioTMLException{
		this.features = features;
		this.matrixFile = initMatrix(getFeatures());
	}
	
	public Set<String> getFeatures() {
		return features;
	}
	
	public void addMatrixRow(String[] rowString) throws BioTMLException{
		try {
			writeRow(rowString, getMatrixFile());
		} catch (IOException e) {
			throw new BioTMLException(6);
		}
	}
	
	
	private File getMatrixFile(){
		return matrixFile;
	}
	
	private File createTempFile() throws BioTMLException{
			File file;
			try {
				file = File.createTempFile("matrix_temp", ".txt");
			} catch (IOException e) {
				throw new BioTMLException(7);
			}
			return file;
	}
	
	private File initMatrix(Set<String> features) throws BioTMLException {
		File matrixfile = createTempFile();
		writeInitMatrix(features, matrixfile);
		matrixfile.deleteOnExit();
		return matrixfile;
	}

	private void writeInitMatrix(Set<String> features, File matrixFile) throws BioTMLException{
		FileWriter writer;
		try {
			writer = new FileWriter(matrixFile);
			Iterator<String> itFeat = features.iterator();
			int i=0;
			while(itFeat.hasNext()){
				String feature = itFeat.next();
				if(i==0){
					writer.write("Features: " + feature);
				}
				else if(i==features.size()-1){
					writer.write(feature + " \n Matrix: \n");
				}
				else{
					writer.write("\t"+feature);
				}
				i++;
			}
			writer.flush();
			writer.close();
		} catch (IOException e) {
			throw new BioTMLException(7);
		}
	}
	
	private void writeRow(String[] row, File matrixFile) throws IOException{
		FileWriter writer = new FileWriter(matrixFile, true);
		for(int i=0; i<row.length; i++){
			if(i==row.length-1){
				writer.write(row[i] + " \n ");
			}else{
				writer.write(row[i] + " | ");
			}
		}
		writer.flush();
		writer.close();
	}

	@SuppressWarnings("resource")
	public void saveMatrix(String fileDirectory) throws BioTMLException {
		File fileToCopy = new File(fileDirectory);
	    FileChannel inputChannel = null;
	    FileChannel outputChannel = null;
	    try {
	    	inputChannel = new FileInputStream(getMatrixFile()).getChannel();
	    	outputChannel = new FileOutputStream(fileToCopy).getChannel();
	    	outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
	    	inputChannel.close();
	    	outputChannel.close();
		} catch (IOException e) {
			throw new BioTMLException(8);
		}
	}
}
