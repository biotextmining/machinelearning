package com.silicolife.textmining.machinelearning.biotml.corpuseditor;

import org.junit.Test;

import com.silicolife.textmining.machinelearning.biotml.core.exception.BioTMLException;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLCorpus;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLDocument;
import com.silicolife.textmining.machinelearning.biotml.reader.BioTMLCorpusReaderImpl;
import com.silicolife.textmining.machinelearning.biotml.writer.BioTMLCorpusWriterImpl;

public class ValidateBioTMLCorpusFromAnote2 {

//	@Test
	public void test() throws BioTMLException {
		String bioTMLCorpusFileName = "C:\\Users\\RRodrigues\\Desktop\\corpora\\final\\biotmlcorpus\\tocreatemodels\\syngenta_corpus_clearnlp_training.gz";
		BioTMLCorpusReaderImpl reader = new BioTMLCorpusReaderImpl();
		IBioTMLCorpus corpus = reader.readBioTMLCorpusFromFile(bioTMLCorpusFileName);
		for(IBioTMLDocument document : corpus.getDocuments()){
			String alltext = document.toString();
			String title = document.getTitle();
			if(!alltext.substring(alltext.length() - title.length(), alltext.length()).equals(title)){
				System.out.println(alltext);
				System.out.println(title);
				System.out.println(document.getID());
			}else{
			}
		}
	}
	
//	@Test
	public void test2() throws BioTMLException{
		String bioTMLCorpusFileName = "C:\\Users\\RRodrigues\\Desktop\\corpora\\entityentity\\annotated\\AImed_Corpus_training_anotated.gz";
		BioTMLCorpusReaderImpl reader = new BioTMLCorpusReaderImpl();
		IBioTMLCorpus corpus = reader.readBioTMLCorpusFromFile(bioTMLCorpusFileName);
		String bioTMLCorpus2FileName = "C:\\Users\\RRodrigues\\Desktop\\corpora\\entityentity\\tocreatemodels\\AImed_Corpus_training.gz";
		BioTMLCorpusReaderImpl reader2 = new BioTMLCorpusReaderImpl();
		IBioTMLCorpus corpus2 = reader2.readBioTMLCorpusFromFile(bioTMLCorpus2FileName);
		for(IBioTMLDocument doc : corpus.getDocuments()){
			boolean docFound = false;
			for(IBioTMLDocument doc2 : corpus2.getDocuments()){
				if(doc.toString().equals(doc2.toString())){
					docFound = true;
				}
			}
			if(!docFound){
				System.out.println(doc);
			}
		}
	}

//	@Test	
	public void test3() throws BioTMLException{
		String bioTMLCorpusFileName = "C:\\Users\\RRodrigues\\Desktop\\corpora\\final\\biotmlcorpus\\exported\\syn_only_one_type_relation.gz";
		BioTMLCorpusReaderImpl reader = new BioTMLCorpusReaderImpl();
		IBioTMLCorpus corpus = reader.readBioTMLCorpusFromFile(bioTMLCorpusFileName);
		BioTMLCorpusWriterImpl writer = new BioTMLCorpusWriterImpl(corpus);
		writer.writeBioTMLCorpusFileSplitedForML("C:\\Users\\RRodrigues\\Desktop\\corpora\\final\\biotmlcorpus\\tomodeltest");
	}
	
	@Test
	public void test4() throws BioTMLException {
		String bioTMLCorpusFileName = "C:/Users/RRodrigues/Desktop/Syngenta_Models/corpus/syn_old_corpus.gz";
		BioTMLCorpusReaderImpl reader = new BioTMLCorpusReaderImpl();
		IBioTMLCorpus corpus = reader.readBioTMLCorpusFromFile(bioTMLCorpusFileName);
		System.out.println(corpus.getAnnotations());
		System.out.println(corpus.getEvents());
//		Set<String> results = new HashSet<>();
//		for(IBioTMLAnnotationsRelation relations :corpus.getRelations()){
//			results.add(relations.getRelationType());
//		}
//		System.out.println(results);
	}

}
