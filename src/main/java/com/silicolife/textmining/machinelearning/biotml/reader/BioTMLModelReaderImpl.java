package com.silicolife.textmining.machinelearning.biotml.reader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.FileUtils;

import com.silicolife.textmining.machinelearning.biotml.core.BioTMLConstants;
import com.silicolife.textmining.machinelearning.biotml.core.exception.BioTMLException;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLFeatureGeneratorConfigurator;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLModel;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLModelConfigurator;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLModelReader;
import com.silicolife.textmining.machinelearning.biotml.core.mllibraries.BioTMLAlgorithm;
import com.silicolife.textmining.machinelearning.biotml.core.models.mallet.MalletClassifierModel;
import com.silicolife.textmining.machinelearning.biotml.core.models.mallet.MalletTransducerModel;

import cc.mallet.classify.Classifier;
import cc.mallet.fst.CRF;
import cc.mallet.fst.Transducer;
import cc.mallet.types.Alphabet;

/**
 * 
 * BioTML model reader class.
 * 
 * @since 1.0.0
 * @author Ruben Rodrigues ({@code rrodrigues@silicolife.com})
 */

public class BioTMLModelReaderImpl implements IBioTMLModelReader{

	/**
	 * 
	 * Initializes the model reader.
	 * 
	 */
	public BioTMLModelReaderImpl() {}
	
	public List<IBioTMLModel> loadModelFromZipFile(String modelFileName) throws BioTMLException{
		List<IBioTMLModel> model = new ArrayList<>();
		if(validateZIPBioTMLModel(modelFileName)){
			try {
				Path biotmltempdir = unzipDirectory(modelFileName);
				File tempdir = biotmltempdir.toFile();
				for(File submodel : tempdir.listFiles()){
					if(submodel.getName().endsWith(".gz")){
						model.add(loadModelFromGZFile(submodel.getAbsolutePath()));
					}
				}
				FileUtils.deleteDirectory(tempdir);
				Files.deleteIfExists(biotmltempdir);
				return model;
			} catch (IOException exc) {
				throw new BioTMLException(14, exc);
			}
		}
		throw new BioTMLException("Not a valid zip model file!");
	}
	
	public List<String> loadSubmodelsToStringFromZipFile(String modelFileName) throws BioTMLException{
		List<String> stringModels = new ArrayList<>();
		if(validateZIPBioTMLModel(modelFileName)){
			try {
				Path biotmltempdir = unzipDirectory(modelFileName);
				File tempdir = biotmltempdir.toFile();
				for(File submodel : tempdir.listFiles()){
					if(submodel.getName().endsWith(".gz")){
						stringModels.add(submodel.getAbsolutePath());
					}
				}
				tempdir.deleteOnExit();
				biotmltempdir.toFile().deleteOnExit();
				return stringModels;
			} catch (IOException exc) {
				throw new BioTMLException(14, exc);
			}
		}
		throw new BioTMLException("Not a valid zip model file!");
	}
	

	/**
	 * Load model from gz file.
	 * @param modelFileName The gz file that contains the model.
	 * @throws BioTMLException Problem reading the input file.
	 */
	public synchronized IBioTMLModel loadModelFromGZFile(String modelFileName) throws BioTMLException{
		if(validateBioTMLGZModel(modelFileName)){
			ObjectInputStream ois = null;
			List<?> modelresult = new ArrayList<>();
			try {
				ois = new ObjectInputStream(new GZIPInputStream(new FileInputStream(modelFileName)));
				modelresult = (List<?>) ois.readObject();
				IBioTMLFeatureGeneratorConfigurator features = (IBioTMLFeatureGeneratorConfigurator) modelresult.get(0);
				IBioTMLModelConfigurator configuration = (IBioTMLModelConfigurator) modelresult.get(1);
				if(configuration.getAlgorithmType().equals(BioTMLAlgorithm.malletcrf.toString()) 
						|| configuration.getAlgorithmType().equals(BioTMLAlgorithm.mallethmm.toString())){
					Transducer transducer = (Transducer) modelresult.get(2);
					ois.close();
					return new MalletTransducerModel(transducer, features, configuration);
				}
				if(configuration.getAlgorithmType().equals(BioTMLAlgorithm.malletsvm.toString())){
					ois.close();
					Classifier classifier = (Classifier) modelresult.get(2);
					return new MalletClassifierModel(classifier, features, configuration);
				}
				ois.close();
			} catch (ClassNotFoundException ex) {
				try {
					ois.close();
				} catch (IOException exc) {
					throw new BioTMLException(17,exc);
				}
				throw new BioTMLException(24,ex);
			} catch (IOException exc) {
				throw new BioTMLException(17,exc);
			}
		}
		throw new BioTMLException("Not a valid gz model file!");
	}
	
	public List<IBioTMLModel> loadConfigurationsModelFromZipFile(String modelFileName) throws BioTMLException{
		List<IBioTMLModel> model = new ArrayList<>();
		if(validateZIPBioTMLModel(modelFileName)){
			try {
				Path biotmltempdir = unzipDirectory(modelFileName);
				File tempdir = biotmltempdir.toFile();
				for(File submodel : tempdir.listFiles()){
					if(submodel.getName().endsWith(".gz")){
						model.add(loadConfigurationsModelFromGZFile(submodel.getAbsolutePath()));
					}
				}
				FileUtils.deleteDirectory(tempdir);
				Files.deleteIfExists(biotmltempdir);
				return model;
			} catch (IOException exc) {
				throw new BioTMLException(14, exc);
			}
		}
		throw new BioTMLException("Not a valid zip model file!");
	}
	
	/**
	 * Load model from gz file.
	 * @param modelFileName The gz file that contains the model.
	 * @throws BioTMLException Problem reading the input file.
	 */
	@SuppressWarnings("unchecked")
	public synchronized IBioTMLModel loadConfigurationsModelFromGZFile(String modelFileName) throws BioTMLException{
		if(validateBioTMLGZModel(modelFileName)){
			ObjectInputStream ois = null;
			List<Object> modelresult = new ArrayList<Object>();
			try {
				ois = new ObjectInputStream(new GZIPInputStream(new FileInputStream(modelFileName)));
				modelresult = (List<Object>) ois.readObject();
				IBioTMLFeatureGeneratorConfigurator features = (IBioTMLFeatureGeneratorConfigurator) modelresult.get(0);
				IBioTMLModelConfigurator configuration = (IBioTMLModelConfigurator) modelresult.get(1);
				if(configuration.getAlgorithmType().equals(BioTMLAlgorithm.malletcrf.toString()) 
						|| configuration.getAlgorithmType().equals(BioTMLAlgorithm.mallethmm.toString())){
					ois.close();
					cleanMalletModelFromMemory(modelresult);
					return new MalletTransducerModel(features, configuration);
				}
				if(configuration.getAlgorithmType().equals(BioTMLAlgorithm.malletsvm.toString())){
					ois.close();
					cleanMalletModelFromMemory(modelresult);
					return new MalletClassifierModel(features, configuration);
				}
				ois.close();
			} catch (ClassNotFoundException ex) {
				try {
					ois.close();
				} catch (IOException exc) {
					throw new BioTMLException(17,exc);
				}
				throw new BioTMLException(24,ex);
			} catch (IOException exc) {
				throw new BioTMLException(17,exc);
			}
		}
		throw new BioTMLException("Not a valid gz model file!");
	}

	public boolean validateModel(String modelFileName){
		if(modelFileName.endsWith(".gz")){
			return validateBioTMLGZModel(modelFileName);
		}else if(modelFileName.endsWith(".zip")){
			return validateZIPBioTMLModel(modelFileName);
		}
		return false;
	}

	public String getModelType(String modelFileName){
		if(modelFileName.endsWith(".gz")){
			return getBioTMLGZModelType(modelFileName);
		}else if(modelFileName.endsWith(".zip")){
			return getBioTMLZipModelType(modelFileName);
		}
		return new String();
	}


	private String getBioTMLZipModelType(String modelFileName) {
		try {
			Path biotmltempdir = unzipDirectory(modelFileName);
			File tempdir = biotmltempdir.toFile();
			String modeltype = new String();
			for(File model : tempdir.listFiles()){
				if(model.getName().endsWith(".gz")){
					IBioTMLModel modelfile = loadConfigurationsModelFromGZFile(model.getAbsolutePath());
					if(modelfile.getModelConfiguration().getIEType().equals(BioTMLConstants.re.toString())){
						FileUtils.deleteDirectory(tempdir);
						Files.deleteIfExists(biotmltempdir);
						return BioTMLConstants.re.toString();
					}else if(modelfile.getModelConfiguration().getIEType().equals(BioTMLConstants.ner.toString())){
						modeltype = BioTMLConstants.ner.toString();
					}
				}
			}
			FileUtils.deleteDirectory(tempdir);
			Files.deleteIfExists(biotmltempdir);
			return modeltype;
		} catch (BioTMLException | IOException e) {
			return new String();
		}
	}

	private boolean validateZIPBioTMLModel(String modelFileName) {
		try {
			Path biotmltempdir = unzipDirectory(modelFileName);
			File tempdir = biotmltempdir.toFile();
			for(File model : tempdir.listFiles()){
				if(model.getName().endsWith(".gz")){
					if(!validateBioTMLGZModel(model.getAbsolutePath())){
						FileUtils.deleteDirectory(tempdir);
						Files.deleteIfExists(biotmltempdir);
						return false;
					}
				}
			}
			FileUtils.deleteDirectory(tempdir);
			Files.deleteIfExists(biotmltempdir);
			return true;
		} catch (BioTMLException | IOException e) {
			return false;
		}
	}

	private String getBioTMLGZModelType(String modelFileName){
		try {
			IBioTMLModel model = loadConfigurationsModelFromGZFile(modelFileName);
			return model.getModelConfiguration().getIEType();
		} catch (BioTMLException e) {
			return new String();
		}
	}

	private boolean validateBioTMLGZModel(String modelFileName) {
		try {
			ObjectInputStream ois = new ObjectInputStream(new GZIPInputStream(new FileInputStream(modelFileName)));
			List<?> modelresult = new ArrayList<>();
			modelresult = (List<?>) ois.readObject();

			if(!(modelresult.get(0) instanceof IBioTMLFeatureGeneratorConfigurator)){
				ois.close();
				return false;
			}
			if(!(modelresult.get(1) instanceof IBioTMLModelConfigurator)){
				ois.close();
				return false;
			}
			if(!(modelresult.get(2) instanceof Transducer) 
					&& !(modelresult.get(2) instanceof Classifier)){
				ois.close();
				return false;
			}
			ois.close();
			cleanMalletModelFromMemory(modelresult);
			return true;
		} catch (IOException | ClassNotFoundException e) {
			return false;
		}
	}
	
	private void cleanMalletModelFromMemory(List<?> modelresult){
		if(modelresult.size()>2){
			if(modelresult.get(2) instanceof Transducer){
				Transducer model = (Transducer) modelresult.get(2);
				if(model.getInputPipe() != null){
					for(Alphabet alphabet : model.getInputPipe().getAlphabets()){
						if(alphabet !=null){
							alphabet.cleanAlphabetFromMemory();
						}
					}
					model.getInputPipe().cleanPipeFromMemory();

				}
				if(model.getOutputPipe() != null){
					for(Alphabet alphabet : model.getInputPipe().getAlphabets()){
						if(alphabet !=null){
							alphabet.cleanAlphabetFromMemory();
						}
					}
					model.getOutputPipe().cleanPipeFromMemory();
				}
				if(model instanceof CRF){
					CRF crf = (CRF) model;
					if(crf.getParameters() != null){
						crf.getParameters().cleanFactorsFromMemory();
					}
					if(crf.getInputAlphabet() != null){
						crf.getInputAlphabet().cleanAlphabetFromMemory();
					}
					if(crf.getOutputAlphabet() != null){
						crf.getOutputAlphabet().cleanAlphabetFromMemory();
					}
				}
			}
			if(modelresult.get(2) instanceof Classifier){
				Classifier model = (Classifier) modelresult.get(2);
				for(Alphabet alphabet : model.getAlphabets()){
					if(alphabet !=null){
						alphabet.cleanAlphabetFromMemory();
					}
				}
				if(model.getInstancePipe() != null){
					model.getInstancePipe().cleanPipeFromMemory();
				}
			}
		}
		
	}

	private Path unzipDirectory(String file) throws BioTMLException, IOException{
		Path biotmltempdir;
		try {
			biotmltempdir = Files.createTempDirectory("BioTMLMultiModelTemp");
		} catch (IOException exc) {
			throw new BioTMLException(17, exc);
		}
		File tempdir = biotmltempdir.toFile();
		if(!tempdir.exists()){
			tempdir.mkdirs();
		}
		byte[] buffer = new byte[1024];
		ZipInputStream zis = new ZipInputStream(new FileInputStream(file));
		ZipEntry ze = zis.getNextEntry();
		while(ze!=null){
			String fileName = ze.getName();
			File newFile = new File(tempdir.getAbsolutePath() + File.separator + fileName);
			new File(newFile.getParent()).mkdirs();
			FileOutputStream fos = new FileOutputStream(newFile);             
			int len;
			while ((len = zis.read(buffer)) > 0) {
				fos.write(buffer, 0, len);
			}

			fos.close();   
			ze = zis.getNextEntry();
		}
		zis.closeEntry();
		zis.close();
		return biotmltempdir;
	}
}
