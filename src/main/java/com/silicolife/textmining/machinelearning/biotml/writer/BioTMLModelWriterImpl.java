package com.silicolife.textmining.machinelearning.biotml.writer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;

import com.silicolife.textmining.machinelearning.biotml.core.exception.BioTMLException;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLModel;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLModelWriter;

/**
 * 
 * BioTML model writer class.
 * 
 * @since 1.0.0
 * @author Ruben Rodrigues ({@code rrodrigues@silicolife.com})
 */

public class BioTMLModelWriterImpl implements IBioTMLModelWriter {
	
	private String filename;

	/**
	 * 
	 * Initializes the model writer.
	 * 
	 * @param filename Absolute path file to save the model.
	 */
	public BioTMLModelWriterImpl(String filename){
		this.filename = filename;
	}
	
	private String getFileName(){
		return filename;
	}
	
	public synchronized void writeGZModelFile(IBioTMLModel model) throws BioTMLException{
		if(!getFileName().endsWith(".gz")){
			filename = filename + ".gz";
		}
		try {
			ObjectOutputStream oos = new ObjectOutputStream(new GZIPOutputStream(new FileOutputStream(getFileName())));
			oos.writeObject(model);
			oos.close();
		} catch (IOException exc) {
			throw new BioTMLException(20,exc);
		}
	}
	
	public synchronized void writeZIPModelFile(List<IBioTMLModel> submodels, File readmeFile) throws BioTMLException{
		if(!getFileName().endsWith(".zip")){
			filename = filename + ".zip";
		}
		Path biotmltempdir;
		try {
			biotmltempdir = Files.createTempDirectory("BioTMLMultiModelTemp");
		} catch (IOException exc) {
			throw new BioTMLException(18, exc);
		}
		File tempdir = biotmltempdir.toFile();
		if(!tempdir.exists()){
			tempdir.mkdirs();
		}
		String finaldirfile = getFileName();
		for(IBioTMLModel submodel : submodels){
			filename = tempdir.getAbsolutePath()+"/"+submodel.getModelConfiguration().getClassType()+".gz";
			writeGZModelFile(submodel);
		}
		try {
			//add readme file.
			Files.move(readmeFile.toPath(), new File(tempdir.getAbsolutePath()+"/"+readmeFile.getName()).toPath(), StandardCopyOption.REPLACE_EXISTING);
			zipDirectory(finaldirfile, tempdir);
			FileUtils.deleteDirectory(tempdir);
			Files.deleteIfExists(biotmltempdir);
		} catch (IOException exc) {
			throw new BioTMLException(19,exc);
		}
	}
	
	public synchronized String saveGZModelForMultiModel(IBioTMLModel model) throws BioTMLException{
		Path biotmltempdir;
		try {
			biotmltempdir = Files.createTempDirectory("BioTMLMultiModelTemp");
		} catch (IOException exc) {
			throw new BioTMLException(18, exc);
		}
		File tempdir = biotmltempdir.toFile();
		if(!tempdir.exists()){
			tempdir.mkdirs();
		}
		String originalFilename= filename;
		filename = tempdir.getAbsolutePath()+"/"+model.getModelConfiguration().getClassType()+".gz";
		writeGZModelFile(model);
		String modelFileName = filename;
		filename = originalFilename;
		return modelFileName;
	}
	
	public synchronized void writeZIPModelFilesSaved(List<String> modelFilesPaths, File readmeFile) throws BioTMLException{
		if(!getFileName().endsWith(".zip")){
			filename = filename + ".zip";
		}
		Path biotmltempdir;
		try {
			biotmltempdir = Files.createTempDirectory("BioTMLMultiModelTemp");
		} catch (IOException exc) {
			throw new BioTMLException(18, exc);
		}
		File tempdir = biotmltempdir.toFile();
		if(!tempdir.exists()){
			tempdir.mkdirs();
		}
		String finaldirfile = getFileName();
		try {
			for(String modelpath:modelFilesPaths){
				Path path = Paths.get(modelpath);
				Files.move(path, new File(tempdir.getAbsolutePath()+"/"+modelpath.substring(modelpath.lastIndexOf("/") + 1)).toPath(), StandardCopyOption.REPLACE_EXISTING);
			}
			//add readme file.
			Files.move(readmeFile.toPath(), new File(tempdir.getAbsolutePath()+"/"+readmeFile.getName()).toPath(), StandardCopyOption.REPLACE_EXISTING);
			zipDirectory(finaldirfile, tempdir);
			FileUtils.deleteDirectory(tempdir);
			Files.deleteIfExists(biotmltempdir);
		} catch (IOException exc) {
			throw new BioTMLException(19,exc);
		}
	}
	
	private synchronized void zipDirectory(String zipFile, File dir) throws IOException{
		byte[] buffer = new byte[1024];
		FileOutputStream fos = new FileOutputStream(zipFile);
		ZipOutputStream zos = new ZipOutputStream(fos);
		for(String file : dir.list()){
			ZipEntry ze= new ZipEntry(file);
			zos.putNextEntry(ze);
			FileInputStream in = new FileInputStream(dir.getAbsolutePath() + File.separator + file);
			int len;
			while ((len = in.read(buffer)) > 0) {
				zos.write(buffer, 0, len);
			}

			in.close();
		}
		zos.closeEntry();
		zos.close();
		fos.close();
	}

	
}
