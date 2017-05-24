package com.silicolife.textmining.machinelearning.biotml.nlp;

import java.util.List;

import org.junit.Test;

import com.silicolife.textmining.machinelearning.biotml.core.exception.BioTMLException;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLSentence;
import com.silicolife.textmining.machinelearning.biotml.core.nlp.opennlp.BioTMLOpenNLP;

public class OpenNLPTest {

	@Test
	public void test() throws BioTMLException {
		List<IBioTMLSentence> sentences = BioTMLOpenNLP.getInstance().getSentences("This is a document test!");
		for(IBioTMLSentence sentence : sentences){
			System.out.println(sentence.toString());
			String[] sentenceArray = sentence.getTokenStrings().toArray(new String[0]);
			String[] pos = BioTMLOpenNLP.getInstance().processPos(sentenceArray);
			StringBuilder sb = new StringBuilder();
			for(String p : pos)
				sb.append(p + "\t");
			System.err.println(sb.toString());
			String[] chunks = BioTMLOpenNLP.getInstance().processChunking(sentenceArray, pos);
			sb = new StringBuilder();
			for(String p : chunks)
				sb.append(p + "\t");
			System.err.println(sb.toString());
			String[] parsing = BioTMLOpenNLP.getInstance().processChunkingParsing(sentenceArray);
			sb = new StringBuilder();
			for(String p : parsing)
				sb.append(p + "\t");
			System.err.println(sb.toString());
		}
		
	}

}
