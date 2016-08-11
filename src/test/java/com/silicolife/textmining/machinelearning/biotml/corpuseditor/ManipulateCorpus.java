package com.silicolife.textmining.machinelearning.biotml.corpuseditor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;

import com.silicolife.textmining.machinelearning.biotml.core.corpora.BioTMLAnnotationImpl;
import com.silicolife.textmining.machinelearning.biotml.core.corpora.BioTMLCorpusImpl;
import com.silicolife.textmining.machinelearning.biotml.core.exception.BioTMLException;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLAnnotation;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLCorpus;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLDocument;
import com.silicolife.textmining.machinelearning.biotml.reader.BioTMLCorpusReaderImpl;
import com.silicolife.textmining.machinelearning.biotml.writer.BioTMLCorpusWriterImpl;

public class ManipulateCorpus {

	@Test
	public void copyCluesFromOneCorpusToOtherCorpus() throws BioTMLException {
		String annotatedCorpusFilename = "C:\\Users\\RRodrigues\\Desktop\\corpora\\entityentity\\tocreatemodels\\AImed_Corpus_training.gz";
		String unannotatedCorpusFilename = "C:\\Users\\RRodrigues\\Desktop\\corpora\\entityentity\\tocreatemodels\\AImed_Corpus_training_unnanotated.gz";
		BioTMLCorpusReaderImpl reader = new BioTMLCorpusReaderImpl();
		IBioTMLCorpus annotatedcorpus = reader.readBioTMLCorpusFromFile(annotatedCorpusFilename);
		IBioTMLCorpus unannotatedcorpus = reader.readBioTMLCorpusFromFile(unannotatedCorpusFilename);
		
		List<IBioTMLAnnotation> AllAnnotations = new ArrayList<>();
		
		for(IBioTMLDocument annotateddocument :annotatedcorpus.getDocuments()){
			Iterator<IBioTMLDocument> itUnnAnnoted = unannotatedcorpus.getDocuments().iterator();
			boolean found = false;
			while(itUnnAnnoted.hasNext() && !found){
				IBioTMLDocument unnanotatedDocument = itUnnAnnoted.next();
				if(annotateddocument.equals(unnanotatedDocument)){
					List<IBioTMLAnnotation> annotations = annotatedcorpus.getDocAnnotations(annotateddocument.getID());
//					List<IBioTMLAnnotation> cluesInAnnotated = getClueAnnotations(annotations);
					AllAnnotations.addAll(setAnnotationsToDocument(annotations, unnanotatedDocument));
					found = true;
				}
			}
		}
		
		unannotatedcorpus = new BioTMLCorpusImpl(unannotatedcorpus.getDocuments(), AllAnnotations, unannotatedcorpus.toString()+"_with_clues");
		
		BioTMLCorpusWriterImpl writer = new BioTMLCorpusWriterImpl(unannotatedcorpus);
		writer.writeGZBioTMLCorpusFile(unannotatedCorpusFilename);
	}
	
//	private List<IBioTMLAnnotation> getClueAnnotations(List<IBioTMLAnnotation> annotations){
//		List<IBioTMLAnnotation> clues = new ArrayList<>();
//		for(IBioTMLAnnotation annotation :annotations){
//			if(annotation.getAnnotType().equals(BioTMLConstants.clue.toString())){
//				clues.add(annotation);
//			}
//		}
//		return clues;
//	}
	
	private List<IBioTMLAnnotation> setAnnotationsToDocument(List<IBioTMLAnnotation> annotations, IBioTMLDocument unannotateddocument){
		List<IBioTMLAnnotation> annotationsConverted = new ArrayList<>();
		for(IBioTMLAnnotation annotation : annotations){
			annotationsConverted.add(new BioTMLAnnotationImpl(unannotateddocument.getID(), annotation.getAnnotType(), annotation.getStartOffset(), annotation.getEndOffset()));
		}
		return annotationsConverted;
	}

}
