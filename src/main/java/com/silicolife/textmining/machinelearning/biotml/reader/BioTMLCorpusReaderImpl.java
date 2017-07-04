package com.silicolife.textmining.machinelearning.biotml.reader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.GZIPInputStream;

import org.apache.commons.io.FilenameUtils;

import com.silicolife.textmining.machinelearning.biotml.core.corpora.BioTMLCorpusImpl;
import com.silicolife.textmining.machinelearning.biotml.core.corpora.BioTMLDocumentImpl;
import com.silicolife.textmining.machinelearning.biotml.core.corpora.BioTMLEntityImpl;
import com.silicolife.textmining.machinelearning.biotml.core.corpora.BioTMLSentenceImpl;
import com.silicolife.textmining.machinelearning.biotml.core.corpora.BioTMLTokenImpl;
import com.silicolife.textmining.machinelearning.biotml.core.exception.BioTMLException;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLCorpus;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLCorpusReader;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLDocument;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLEntity;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLSentence;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLToken;
import com.silicolife.textmining.machinelearning.biotml.core.nlp.BioTMLNLPManager;

/**
 * 
 * BioTML corpus reader class.
 * 
 * @since 1.0.0
 * @version 1.0.1
 * @author Ruben Rodrigues ({@code rrodrigues@silicolife.com})
 */

public class BioTMLCorpusReaderImpl implements IBioTMLCorpusReader{

	private Map<String, Long> mapDocNameToDocID;

	/**
	 * 
	 * Initializes the corpus reader.
	 * 
	 */
	public BioTMLCorpusReaderImpl() {
		mapDocNameToDocID = new HashMap<>();
	}

	public IBioTMLCorpus readBioTMLCorpusFromDirFolder(String corpusDirFolder, String nlpSystem) throws BioTMLException{
		File corpusFolder = new File(corpusDirFolder);
		if(!corpusFolder.isDirectory()){
			throw new BioTMLException("The dirFolder string must be a path to a folder!");
		}
		if(BioTMLNLPManager.getInstance().getNLPById(nlpSystem)!=null){
			return readCorpusFolderWithNLPSystem(corpusFolder, nlpSystem);
		}else{
			throw new BioTMLException("The NLP System is not recognized!");
		}
	}

	public IBioTMLCorpus readBioTMLCorpusFromBioCFiles(String documentFile, String nlpSystem) throws BioTMLException{
		File corpusFile = new File(documentFile);
		if(!corpusFile.isFile()){
			throw new BioTMLException("The documentFile string must be a path to a file!");
		}
		if(BioTMLNLPManager.getInstance().getNLPById(nlpSystem)!=null){
			return readDocumentsFromBioCFile(corpusFile, nlpSystem);
		}else{
			throw new BioTMLException("The NLP System is not recognized!");
		}
	}

	public IBioTMLCorpus readBioTMLCorpusFromBioCFiles(String documentFile, String annotationsFile,  String nlpSystem) throws BioTMLException{
		File corpusFile = new File(documentFile);
		File corpusAnnotationsFile = new File(annotationsFile);
		if(!corpusFile.isFile()){
			throw new BioTMLException("The documentFile string must be a path to a file!");
		}
		if(!corpusAnnotationsFile.isFile()){
			throw new BioTMLException("The annotationsFile string must be a path to a file!");
		}
		if(BioTMLNLPManager.getInstance().getNLPById(nlpSystem)!=null){
			IBioTMLCorpus corpus = readDocumentsFromBioCFile(corpusFile, nlpSystem);
			return readAnnotationsFromBioCFile(corpus, corpusAnnotationsFile);
		}else{
			throw new BioTMLException("The NLP System is not recognized!");
		}
	}

	public IBioTMLCorpus readBioTMLCorpusFromFile(String filename) throws BioTMLException{
		try {
			ObjectInputStream ois = new ObjectInputStream(new GZIPInputStream(new FileInputStream(filename)));
			IBioTMLCorpus corpus = (IBioTMLCorpus) ois.readObject();
			ois.close();
			return corpus;
		} catch (IOException | ClassNotFoundException exc) {
			throw new BioTMLException(exc);
		}
	}

	private Map<String, Long> getMapDocNameToDocID(){
		return mapDocNameToDocID;
	}

	private long getLastDocID(){
		Integer[] docIDs = getMapDocNameToDocID().values().toArray(new Integer[0]);
		if(docIDs.length>0){
			Arrays.sort(docIDs);
			return docIDs[docIDs.length - 1];
		}else{
			return -1;
		}

	}

	private IBioTMLCorpus readAnnotationsFromBioCFile(IBioTMLCorpus documentCorpus, File corpusAnnotationsFile) throws BioTMLException{
		try {
			List<IBioTMLEntity> annotations = new ArrayList<IBioTMLEntity>();
			BufferedReader reader = new BufferedReader(new FileReader(corpusAnnotationsFile));
			String line;
			while((line = reader.readLine())!=null){
				String[] annoation = line.split("\t");
				if(annoation.length != 6){
					reader.close();
					throw new BioTMLException("The corpusAnnotationsFile is not a compatible BioCreative document file!");
				}
				IBioTMLDocument document = null;
				try{
					document = documentCorpus.getDocumentByExternalID(annoation[0]);
				}catch (BioTMLException exc){}

				if(document != null){
					long startOffset = Long.valueOf(annoation[2]);
					long endOffset = Long.valueOf(annoation[3]);
					if(annoation[1].equals("T")){
						int lastSentence = document.getSentences().size()-1;
						IBioTMLSentence possibleTitle = document.getSentence(lastSentence);
						if(possibleTitle.toString().equals(document.getTitle())){
							long textSize = document.getSentence(lastSentence).getEndSentenceOffset() - (long) document.getTitle().length();
							startOffset = startOffset + textSize;
							endOffset = endOffset + textSize;
						}else{
							reader.close();
							throw new BioTMLException("The title is not equal to last sentence in BioTMLDocument");
						}
					}
					String docTextAnnot = document.toString().substring((int)startOffset, (int)endOffset);
					if(!annoation[4].equals(docTextAnnot) && !detectChemicalFormula(annoation[4])){
						reader.close();
						throw new BioTMLException("Annotation offsets are not correct to token string");
					}
					else if (detectChemicalFormula(annoation[4]) && !processAnnotationFromFile(annoation[4]).equals(docTextAnnot)){
						reader.close();
						throw new BioTMLException("Annotation offsets are not correct to token string");
					}
					annotations.add(new BioTMLEntityImpl(document.getID(), annoation[5], startOffset, endOffset));
				}
			}
			reader.close();
			return new BioTMLCorpusImpl(documentCorpus.getDocuments(), annotations, documentCorpus.toString());
		} catch (IOException exc) {
			throw new BioTMLException(exc);
		}
	}


	private boolean detectChemicalFormula(String token){
		if (token.startsWith("\"")){
			return true;
		}
		return false;

	}

	private String processAnnotationFromFile(String annotation){
		String pattern = "\"{2,3}";
		String b = annotation.replaceAll(pattern, "\"");
		return b.substring(1, b.length()-1);
	}


	private IBioTMLCorpus readDocumentsFromBioCFile(File corpusFile, String nlpSystem) throws BioTMLException{
		try {
			List<IBioTMLDocument> documents = new ArrayList<IBioTMLDocument>();
			BufferedReader reader = new BufferedReader(new FileReader(corpusFile));
			String line;
			long docID = 0;
			while((line = reader.readLine())!=null){
				String[] document = line.split("\t");
				if(document.length != 3){
					reader.close();
					throw new BioTMLException("The documentFile is not a compatible BioCreative document file!");
				}
				List<IBioTMLSentence> sentences = new ArrayList<IBioTMLSentence>();
				String title = document[1];
				String documentText = document[2];

				sentences = BioTMLNLPManager.getInstance().getNLPById(nlpSystem).getSentences(documentText);
				IBioTMLSentence lastSentence = sentences.get(sentences.size()-1);
				long lastOffset = lastSentence.getEndSentenceOffset()+1;
				List<IBioTMLSentence> titleSentences = BioTMLNLPManager.getInstance().getNLPById(nlpSystem).getSentences(title);

				List<IBioTMLToken> sentence = new ArrayList<>();
				for(IBioTMLSentence titleSentence : titleSentences)
					for(IBioTMLToken token : titleSentence.getTokens())
						sentence.add(new BioTMLTokenImpl(token.getToken(), token.getStartOffset()+lastOffset, token.getEndOffset()+lastOffset));

				sentences.add(new BioTMLSentenceImpl(sentence, title));

				documents.add(new BioTMLDocumentImpl(docID, document[1], document[0], sentences));
				docID++;
			}
			reader.close();
			return new BioTMLCorpusImpl(documents, FilenameUtils.getBaseName(corpusFile.getName()));
		} catch (IOException exc) {
			throw new BioTMLException(exc);
		}
	}

	private IBioTMLCorpus readCorpusFolderWithNLPSystem(File corpusFolder, String nlpSystem) throws BioTMLException{
		try {
			List<IBioTMLDocument> documents = new ArrayList<IBioTMLDocument>();
			List<IBioTMLEntity> annotations = new ArrayList<IBioTMLEntity>();
			for(File docFile : corpusFolder.listFiles()){
				if(FilenameUtils.getBaseName(docFile.getName()).startsWith("annotations")){
					annotations = readAnnotations(docFile);
				}else{
					String externalID =  FilenameUtils.getBaseName(docFile.getName());
					BufferedReader reader = new BufferedReader(new FileReader(docFile));
					StringBuilder documentText = new StringBuilder();
					String line;
					String title = null;
					while((line = reader.readLine())!=null){
						if(title == null){
							title = line;
						}
						documentText.append(line);
						documentText.append(System.lineSeparator());
					}
					reader.close();
					if(!getMapDocNameToDocID().containsKey(externalID)){
						getMapDocNameToDocID().put(externalID, getLastDocID()+1);
					}
					List<IBioTMLSentence> sentences = new ArrayList<IBioTMLSentence>();
					sentences = BioTMLNLPManager.getInstance().getNLPById(nlpSystem).getSentences(documentText.toString());
					documents.add(new BioTMLDocumentImpl(getMapDocNameToDocID().get(externalID), title, externalID, sentences));
				}
			}
			return new BioTMLCorpusImpl(documents, annotations, FilenameUtils.getBaseName(corpusFolder.getName()));
		} catch (IOException exc) {
			throw new BioTMLException(exc);
		}
	}

	private List<IBioTMLEntity> readAnnotations(File annotationFile) throws BioTMLException{
		try {
			List<IBioTMLEntity> annotations = new ArrayList<IBioTMLEntity>();
			BufferedReader reader = new BufferedReader(new FileReader(annotationFile));
			String line;
			while((line = reader.readLine())!=null){
				String[] annotationLine = line.split("\t");
				if(!getMapDocNameToDocID().containsKey(annotationLine[0])){
					getMapDocNameToDocID().put(annotationLine[0], getLastDocID()+1);
				}
				annotations.add(new BioTMLEntityImpl(getMapDocNameToDocID().get(annotationLine[0]), annotationLine[1], Long.valueOf(annotationLine[2]), Long.valueOf(annotationLine[3])));
			}
			reader.close();
			return annotations;
		} catch (IOException exc) {
			throw new BioTMLException(exc);
		} 
	}

	public IBioTMLCorpus readBioTMLCorpusFromBioCFiles(String documentFile, String annotationsFile, String modelClassType,  String nlpSystem) throws BioTMLException{
		File corpusFile = new File(documentFile);
		File corpusAnnotationsFile = new File(annotationsFile);
		if(!corpusFile.isFile()){
			throw new BioTMLException("The documentFile string must be a path to a file!");
		}
		if(!corpusAnnotationsFile.isFile()){
			throw new BioTMLException("The annotationsFile string must be a path to a file!");
		}
		if(BioTMLNLPManager.getInstance().getNLPById(nlpSystem)!=null){
			IBioTMLCorpus corpus = readDocumentsFromBioCFile(corpusFile, nlpSystem);
			return readAnnotationsFromBioCFile(corpus, modelClassType, corpusAnnotationsFile);
		}else{
			throw new BioTMLException("The NLP System is not recognized!");
		}
	}

	private IBioTMLCorpus readAnnotationsFromBioCFile(IBioTMLCorpus documentCorpus, String modelClassType,
			File corpusAnnotationsFile) throws BioTMLException {
		try {
			Set<String> trueCorpusEntries=new HashSet<>();
			Set<String> falseCorpusEntries=new HashSet<>();
			List<IBioTMLEntity> annotations = new ArrayList<IBioTMLEntity>();
			BufferedReader reader = new BufferedReader(new FileReader(corpusAnnotationsFile));
			String line;
			while((line = reader.readLine())!=null){
				String[] annoation = line.split("\t");
				if(annoation.length != 6){
					reader.close();
					throw new BioTMLException("The corpusAnnotationsFile is not a compatible BioCreative document file!");
				}
				IBioTMLDocument document = null;
				if (annoation[5].equalsIgnoreCase(modelClassType)){
					trueCorpusEntries.add(annoation[0]);
					try{
						document = documentCorpus.getDocumentByExternalID(annoation[0]);
					}catch (BioTMLException exc){}

					if(document != null){
						long startOffset = Long.valueOf(annoation[2]);
						long endOffset = Long.valueOf(annoation[3]);
						if(annoation[1].equals("T")){
							int lastSentence = document.getSentences().size()-1;
							IBioTMLSentence possibleTitle = document.getSentence(lastSentence);
							if(possibleTitle.toString().equals(document.getTitle())){
								long textSize = document.getSentence(lastSentence).getEndSentenceOffset() - (long) document.getTitle().length();
								startOffset = startOffset + textSize;
								endOffset = endOffset + textSize;
							}else{
								reader.close();
								throw new BioTMLException("The title is not equal to last sentence in BioTMLDocument");
							}
						}
						String docTextAnnot = document.toString().substring((int)startOffset, (int)endOffset);
						if(!annoation[4].equals(docTextAnnot) && !detectChemicalFormula(annoation[4])){
							reader.close();
							throw new BioTMLException("Annotation offsets are not correct to token string");
						}
						else if (detectChemicalFormula(annoation[4]) && !processAnnotationFromFile(annoation[4]).equals(docTextAnnot)){
							reader.close();
							throw new BioTMLException("Annotation offsets are not correct to token string");
						}
						annotations.add(new BioTMLEntityImpl(document.getID(), annoation[5], startOffset, endOffset));
					}
				}
				else{
					falseCorpusEntries.add(annoation[0]);
				}
			}
			reader.close();

			for (String externalID:falseCorpusEntries){
				if (!trueCorpusEntries.contains(externalID)){
					documentCorpus.deleteDocumentByExternalID(externalID);
				}
			}

			return new BioTMLCorpusImpl(documentCorpus.getDocuments(), annotations, documentCorpus.toString());
		} catch (IOException exc) {
			throw new BioTMLException(exc);
		}
	}




}