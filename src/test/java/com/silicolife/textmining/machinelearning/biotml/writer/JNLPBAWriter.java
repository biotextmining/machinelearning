package com.silicolife.textmining.machinelearning.biotml.writer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;

import com.silicolife.textmining.machinelearning.biotml.core.exception.BioTMLException;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLCorpus;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLCorpusReader;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLDocument;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLEntity;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLSentence;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLToken;
import com.silicolife.textmining.machinelearning.biotml.reader.BioTMLCorpusReaderImpl;

public class JNLPBAWriter {
	
	private static String jnlpbatoread = "C:/Users/RRodrigues/Desktop/JNLPBA/Genia4EReval2_annotated_final.gz";
	private static String jnlpbatowrite = "C:/Users/RRodrigues/Desktop/JNLPBA/test/result_all.iob2";
	private static File jnlpbaFile = new File(jnlpbatowrite);
	private static IBioTMLCorpusReader reader = new BioTMLCorpusReaderImpl();

	@Test
	public void test() throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter(jnlpbaFile, false));
		IBioTMLCorpus corpus = null;
		try {
			corpus = reader.readBioTMLCorpusFromFile(jnlpbatoread);
		} catch (BioTMLException e) {
			e.printStackTrace();
		}
		System.out.println(corpus.getEntities());
		List<IBioTMLDocument> docs = corpus.getDocuments();
		Iterator<IBioTMLDocument> itDocs = docs.iterator();
		String toWrite = new String();
		while(itDocs.hasNext()){
			IBioTMLDocument document = itDocs.next();
			toWrite = "###MEDLINE:" + document.getID();
			writer.write(toWrite);
			writer.newLine();
			writer.newLine();
			for(IBioTMLSentence sentence : document.getSentences()){
				for(IBioTMLToken token : sentence.getTokens()){
					toWrite = token.getToken();
					try {
						IBioTMLEntity entity = corpus.getEntityFromDocAndOffsets(document.getID(), token.getStartOffset(), token.getEndOffset());
						if(entity.getStartOffset() == token.getStartOffset()){
							toWrite = toWrite + "\tB-"+entity.getAnnotationType();
						}else{
							toWrite = toWrite + "\tI-"+entity.getAnnotationType();
						}
					} catch (BioTMLException e) {
						toWrite = toWrite + "\tO";
					}
					writer.write(toWrite);
					writer.newLine();
				}
				writer.newLine();
			}
		}
		writer.close();
	}

}
