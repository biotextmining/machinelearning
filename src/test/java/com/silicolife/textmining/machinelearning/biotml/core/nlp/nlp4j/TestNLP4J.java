package com.silicolife.textmining.machinelearning.biotml.core.nlp.nlp4j;

import java.io.File;
import java.util.List;

import org.junit.Test;

import edu.emory.mathcs.nlp.component.tokenizer.EnglishTokenizer;
import edu.emory.mathcs.nlp.component.tokenizer.Tokenizer;
import edu.emory.mathcs.nlp.component.tokenizer.token.Token;

public class TestNLP4J {

	@Test
	public void testClearNLP() throws Exception{
		String modelType  = "medical-en";
		String packagePath = "machinelearning/"+ this.getClass().getPackage().getName().replace(".", "/");
		File file = new File(packagePath.substring(0, packagePath.lastIndexOf("/")));
		String path = file.getAbsolutePath();
		String inputFile  = path+"/sentences.txt";
		String outputFile = path+"/test1.txt";
		String sentence = "Although posttranscriptional regulation of RNA metabolism is increasingly recognized as a key regulatory process in plant response to environmental stresses, reports demonstrating the importance of RNA metabolism control in crop improvement under adverse environmental stresses are severely limited.";
		Tokenizer tokenizer = new EnglishTokenizer(); 
		List<List<Token>> sentences = tokenizer.segmentize(sentence);
		for(List<Token> sentencet : sentences){
			System.out.println(sentencet);
		}
	}

}
