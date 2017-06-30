package com.silicolife.textmining.machinelearning.biotml.writer;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.GZIPOutputStream;

import com.silicolife.textmining.machinelearning.biotml.core.BioTMLConstants;
import com.silicolife.textmining.machinelearning.biotml.core.corpora.BioTMLCorpusImpl;
import com.silicolife.textmining.machinelearning.biotml.core.exception.BioTMLException;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLAnnotation;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLAssociation;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLCorpus;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLCorpusWriter;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLDocument;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLEntity;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLEvent;

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

	@Override
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

	@Override
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

	@Override
	public void writeBioTMLCorpusFileSplitedForML(String dirnamepath) throws BioTMLException{
		if(getCorpus().getEntities().size()==0)
			throw new BioTMLException("The inputed corpus must have annotations.");

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

	@Override
	public void writeToA1A2Format(String dirnamepath) throws BioTMLException {
		try{
			List<IBioTMLDocument> documents = getCorpus().getDocuments();
			for(IBioTMLDocument document : documents){
				String id = (!document.getExternalID().isEmpty()) ? document.getExternalID() : String.valueOf(document.getID());
				String docString = document.toString();
				writeDocumentA1(dirnamepath, id, docString);
				List<IBioTMLEntity> entites = getCorpus().getDocEntities(document.getID());
				if(!entites.isEmpty()){
					Map<IBioTMLEntity, String> mapForA2 = writeEntitiesInA1(dirnamepath, document, id, docString, entites);
					writeEventsInA2(dirnamepath, document, id, docString, mapForA2);
				}

			}
		}catch(Exception e){
			throw new BioTMLException(e);
		}
	}

	private void writeEventsInA2(String dirnamepath, IBioTMLDocument document, String id, String docString,
			Map<IBioTMLEntity, String> mapForA2) throws IOException {
		FileWriter fw = new FileWriter(dirnamepath+"/"+id+".a2");
		for(IBioTMLEntity entity : mapForA2.keySet()){
			if(entity.getAnnotationType().equals(BioTMLConstants.trigger.toString()))
				fw.write(mapForA2.get(entity) + "\t" + entity.getAnnotationType()
				+ " " + String.valueOf(entity.getStartOffset())
				+ " " + String.valueOf(entity.getEndOffset())
				+ "\t"+ docString.substring((int)entity.getStartOffset(), (int)entity.getEndOffset())
				+ System.lineSeparator()
			);
		}
		Set<IBioTMLEvent> events = getCorpus().getDocEvents(document.getID());
		int i = 1;
		for(IBioTMLEvent event : events){
			@SuppressWarnings("unchecked")
			IBioTMLAssociation<IBioTMLAnnotation, IBioTMLAnnotation> association = event.getAssociation();
			if(mapForA2.containsKey(association.getEntryOne()) && mapForA2.containsKey(association.getEntryTwo())){
				if(association.getEntryOne().getAnnotationType().equals(BioTMLConstants.trigger.toString())
						&& !association.getEntryTwo().getAnnotationType().equals(BioTMLConstants.trigger.toString())){
					fw.write("E"+i+"\t"
							+ association.getEntryOne().getAnnotationType() +":"+ mapForA2.get(association.getEntryOne()) + " "
							+ event.getAnnotationType() +":"+ mapForA2.get(association.getEntryTwo()) 
							+ System.lineSeparator());
				}else if(!association.getEntryOne().getAnnotationType().equals(BioTMLConstants.trigger.toString())
						&& association.getEntryTwo().getAnnotationType().equals(BioTMLConstants.trigger.toString())){
					fw.write("E"+i+"\t"
							+ association.getEntryTwo().getAnnotationType() +":"+ mapForA2.get(association.getEntryTwo())+ " "
							+ event.getAnnotationType() +":"+ mapForA2.get(association.getEntryOne())
							+ System.lineSeparator());
				}else{
					fw.write("E"+i+"\t"
							+ association.getEntryOne().getAnnotationType() +":"+ mapForA2.get(association.getEntryOne()) + " "
							+ association.getEntryTwo().getAnnotationType() +":"+ mapForA2.get(association.getEntryTwo()) 
							+ System.lineSeparator());
				}

				i++;
			}
		}
		fw.close();
	}

	private Map<IBioTMLEntity, String> writeEntitiesInA1(String dirnamepath, IBioTMLDocument document, String id, String docString,
			List<IBioTMLEntity> entites) throws IOException {
		Map<IBioTMLEntity, String> mapForA2 = new HashMap<>();
		FileWriter fw = new FileWriter(dirnamepath+"/"+id+".a1");
		int i = 1;
		for(IBioTMLEntity entity : entites){
			mapForA2.put(entity, "T" + i );
			if(!entity.getAnnotationType().equals(BioTMLConstants.trigger.toString()))
				fw.write("T" + i + "\t" + entity.getAnnotationType()
							+ " " + String.valueOf(entity.getStartOffset())
							+ " " + String.valueOf(entity.getEndOffset())
							+ "\t"+ docString.substring((int)entity.getStartOffset(), (int)entity.getEndOffset())
							+ System.lineSeparator()
						);
			i++;
		}
		fw.close();
		return mapForA2;
	}

	private void writeDocumentA1(String dirnamepath, String id, String docString) throws IOException {
		FileWriter fw = new FileWriter(dirnamepath+"/"+id+".txt");
		fw.write(docString);
		fw.close();
	}

	private void createCorpusFromDocuments(List<IBioTMLDocument> documents, String dirnamepath, String corpusName) throws BioTMLException{
		List<IBioTMLEntity> entities = new ArrayList<>();
		List<IBioTMLEvent> events = new ArrayList<>();
		for(IBioTMLDocument doc : documents){
			entities.addAll(getCorpus().getDocEntities(doc.getID()));
			events.addAll(getCorpus().getDocEvents(doc.getID()));
		}
		writeCorpusFile(dirnamepath+"/"+corpusName+".gz",  new BioTMLCorpusImpl(documents, entities, events, corpusName));
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
