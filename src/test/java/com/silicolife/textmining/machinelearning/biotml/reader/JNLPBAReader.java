package com.silicolife.textmining.machinelearning.biotml.reader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.silicolife.textmining.machinelearning.biotml.core.corpora.BioTMLEntityImpl;
import com.silicolife.textmining.machinelearning.biotml.core.corpora.BioTMLCorpusImpl;
import com.silicolife.textmining.machinelearning.biotml.core.corpora.BioTMLDocumentImpl;
import com.silicolife.textmining.machinelearning.biotml.core.corpora.BioTMLSentenceImpl;
import com.silicolife.textmining.machinelearning.biotml.core.corpora.BioTMLTokenImpl;
import com.silicolife.textmining.machinelearning.biotml.core.exception.BioTMLException;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLEntity;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLCorpus;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLCorpusWriter;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLDocument;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLSentence;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLToken;
import com.silicolife.textmining.machinelearning.biotml.writer.BioTMLCorpusWriterImpl;

public class JNLPBAReader {
	private static String jnlpbatoread = "C:/Users/RRodrigues/Desktop/JNLPBA/test/Genia4EReval2.raw";
	private static String jnlpbatowrite = "C:/Users/RRodrigues/Desktop/JNLPBA/Genia4EReval2.gz";
	private static File jnlpbaFile = new File(jnlpbatoread);
	private static String corpusName = "JNLPBA test Set";

	@Test
	public void test() throws IOException, BioTMLException {
		BufferedReader reader = new BufferedReader(new FileReader(jnlpbaFile));

		String line;
		String externalID = new String();
		long documentID = -1;
		long startOffset = 0;
		List<IBioTMLDocument> documents = new ArrayList<IBioTMLDocument>();
		List<IBioTMLSentence> documentSentences = new ArrayList<IBioTMLSentence>();
		List<IBioTMLToken> sentenceTokens = new ArrayList<IBioTMLToken>();
		List<IBioTMLEntity> annotations = new ArrayList<IBioTMLEntity>();
		while((line = reader.readLine())!= null)
		{
			if(line.startsWith("###MEDLINE")){
				documentSentences = addDocument(documentID, externalID, startOffset, documents, documentSentences);
				startOffset = 0;
				externalID = line.trim().substring(11);
				documentID++;
			}else{
				if(line.isEmpty()){
					sentenceTokens = addSentenceToDocument(documentSentences, sentenceTokens);
				}else{
					//token to add to sentence
					String[] tokenAndTag = line.split("\t");
					long endOffset = startOffset + tokenAndTag[0].length();
					sentenceTokens.add(new BioTMLTokenImpl(tokenAndTag[0], startOffset, endOffset));
					if(tokenAndTag.length>1){
						if(tokenAndTag[tokenAndTag.length-1].startsWith("B-") ){
							annotations.add(new BioTMLEntityImpl(documentID, tokenAndTag[tokenAndTag.length-1].substring(2), startOffset, endOffset));
						}else if(tokenAndTag[tokenAndTag.length-1].startsWith("I-")){
							int prevAnnotID= annotations.size()-1;
							IBioTMLEntity previousAnnotation = annotations.get(prevAnnotID);
							annotations.set(prevAnnotID, new BioTMLEntityImpl(documentID, tokenAndTag[tokenAndTag.length-1].substring(2), previousAnnotation.getStartOffset(), endOffset));
						}
					}
					startOffset = endOffset + 1;
				}
			}
		}
		//final document
		sentenceTokens = addSentenceToDocument(documentSentences, sentenceTokens);
		documentSentences = addDocument(documentID, externalID, startOffset, documents, documentSentences);

		reader.close();
			
		IBioTMLCorpus corpus = new BioTMLCorpusImpl(documents, annotations, corpusName);
		IBioTMLCorpusWriter writer = new BioTMLCorpusWriterImpl(corpus);
		writer.writeGZBioTMLCorpusFile(jnlpbatowrite);
	}

	private List<IBioTMLSentence> addDocument(Long documentID,String externalID, long startOffset, List<IBioTMLDocument> documents, List<IBioTMLSentence> documentSentences) {
		if(!documentSentences.isEmpty() && !externalID.isEmpty() && documentID!=-1){
			//new document
			IBioTMLSentence title = documentSentences.get(0);
			documents.add(new BioTMLDocumentImpl(documentID, externalID, title.toString(), documentSentences));
		}
		return new ArrayList<IBioTMLSentence>();
	}

	private List<IBioTMLToken> addSentenceToDocument(List<IBioTMLSentence> documentSentences,
			List<IBioTMLToken> sentenceTokens) {
		if(!sentenceTokens.isEmpty()){
			//new sentence
			documentSentences.add(new BioTMLSentenceImpl(sentenceTokens, sentenceToSource(sentenceTokens)));
		}
		return new ArrayList<IBioTMLToken>();
	}

	private String sentenceToSource(List<IBioTMLToken> sentenceTokens) {
		StringBuilder source = new StringBuilder();
		for( IBioTMLToken token : sentenceTokens){
			source.append(token.toString());
			source.append(" ");
		}
		return source.toString();
	}

}
