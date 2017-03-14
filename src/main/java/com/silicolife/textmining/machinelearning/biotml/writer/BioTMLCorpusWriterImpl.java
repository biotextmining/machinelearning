package com.silicolife.textmining.machinelearning.biotml.writer;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.zip.GZIPOutputStream;

import com.silicolife.textmining.machinelearning.biotml.core.corpora.BioTMLCorpusImpl;
import com.silicolife.textmining.machinelearning.biotml.core.exception.BioTMLException;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLAnnotation;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLEvent;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLCorpus;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLCorpusWriter;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLDocument;

/**
 * 
 * BioTML corpus writer class.
 * 
 * @since 1.0.0
 * @author Ruben Rodrigues ({@code rrodrigues@silicolife.com})
 */

public class BioTMLCorpusWriterImpl implements IBioTMLCorpusWriter{
	
	private IBioTMLCorpus corpus;

	/**
	 * 
	 * Initializes the writer with a corpus.
	 * 
	 * @param corpus Corpus to be written ({@link IBioTMLCorpus}).
	 */
	public BioTMLCorpusWriterImpl(IBioTMLCorpus corpus) {
		this.corpus = corpus;
	}
	
	private IBioTMLCorpus getCorpus(){
		return corpus;
	}
	
	public void writeGZBioTMLCorpusFile(String filenamepath) throws BioTMLException{
		try {
			ObjectOutputStream oos = new ObjectOutputStream(new GZIPOutputStream(new FileOutputStream(filenamepath)));
			oos.writeObject(getCorpus());
			oos.close();
		} catch (IOException ex) {
			ex.printStackTrace();
			throw new BioTMLException("There was a problem writing the corpus to file.");
		}
	}
	
	public void writeGZBioTMLCorpusFileWithoutAnnotations(String filenamepath) throws BioTMLException{
		try {
			ObjectOutputStream oos = new ObjectOutputStream(new GZIPOutputStream(new FileOutputStream(filenamepath)));
			oos.writeObject(new BioTMLCorpusImpl(getCorpus().getDocuments(), getCorpus().toString()));
			oos.close();
		} catch (IOException ex) {
			ex.printStackTrace();
			throw new BioTMLException("There was a problem writing the corpus to file.");
		}
	}
	
	public void writeBioTMLCorpusFileSplitedForML(String dirnamepath) throws BioTMLException{
		if(getCorpus().getAnnotations().size()==0){
			throw new BioTMLException("The inputed corpus must have annotations.");
		}
		List<IBioTMLDocument> documents = getCorpus().getDocuments();
		if(documents.size()>100){
			Collections.shuffle(documents);
			createCorpusFromDocuments(new ArrayList<>(documents.subList(0, documents.size()/3)), dirnamepath, getCorpus().toString()+"_training");
			createCorpusFromDocuments(new ArrayList<>(documents.subList(documents.size()/3, 2*(documents.size()/3))), dirnamepath, getCorpus().toString()+"_development");
			createCorpusFromDocuments(new ArrayList<>(documents.subList(2*(documents.size()/3), documents.size())), dirnamepath, getCorpus().toString()+"_evaluation");
			writeCorpusFile(dirnamepath+"/"+getCorpus().toString()+"_training_unnanotated"+".gz",  new BioTMLCorpusImpl(new ArrayList<>(documents.subList(0, documents.size()/3)), getCorpus().toString()+"_training_unnanotated"));
			writeCorpusFile(dirnamepath+"/"+getCorpus().toString()+"_development_unnanotated"+".gz",  new BioTMLCorpusImpl(new ArrayList<>(documents.subList(documents.size()/3, 2*(documents.size()/3))), getCorpus().toString()+"_development_unnanotated"));
			writeCorpusFile(dirnamepath+"/"+getCorpus().toString()+"_evaluation_unnanotated"+".gz",  new BioTMLCorpusImpl(new ArrayList<>(documents.subList(2*(documents.size()/3), documents.size())), getCorpus().toString()+"_evaluation_unnanotated"));
		}else{
			throw new BioTMLException("The inputed corpus must have more than 100 documents.");
		}
	}
	
	private void createCorpusFromDocuments(List<IBioTMLDocument> documents, String dirnamepath, String corpusName) throws BioTMLException{
		List<IBioTMLAnnotation> annotations = new ArrayList<>();
		List<IBioTMLEvent> events = new ArrayList<>();
		for(IBioTMLDocument doc : documents){
			annotations.addAll(getCorpus().getDocAnnotations(doc.getID()));
			events.addAll(getCorpus().getDocAnnotationEvents(doc.getID()));
		}
		writeCorpusFile(dirnamepath+"/"+corpusName+".gz",  new BioTMLCorpusImpl(documents, annotations, events, corpusName));
	}
	
	private void writeCorpusFile(String filenamepath, IBioTMLCorpus corpusToWrite) throws BioTMLException{
		try {
			ObjectOutputStream oos = new ObjectOutputStream(new GZIPOutputStream(new FileOutputStream(filenamepath)));
			oos.writeObject(corpusToWrite);
			oos.close();
		} catch (IOException ex) {
			ex.printStackTrace();
			throw new BioTMLException("There was a problem writing the corpus to file.");
		}
	}

}
