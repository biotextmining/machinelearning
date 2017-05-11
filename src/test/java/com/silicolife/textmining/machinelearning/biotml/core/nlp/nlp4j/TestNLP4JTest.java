package com.silicolife.textmining.machinelearning.biotml.core.nlp.nlp4j;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import edu.emory.mathcs.nlp.common.util.NLPUtils;
import edu.emory.mathcs.nlp.component.template.NLPComponent;
import edu.emory.mathcs.nlp.component.template.node.NLPNode;
import edu.emory.mathcs.nlp.component.tokenizer.EnglishTokenizer;
import edu.emory.mathcs.nlp.component.tokenizer.Tokenizer;
import edu.emory.mathcs.nlp.component.tokenizer.token.Token;
import edu.emory.mathcs.nlp.decode.NLPDecoder;

public class TestNLP4JTest {

//	@Test
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
	
	@Test
	public void testDependecy(){
		NLPComponent<NLPNode> pos = NLPUtils.getComponent("edu/emory/mathcs/nlp/models/en-pos.xz");
		NLPComponent<NLPNode> dep = NLPUtils.getComponent("edu/emory/mathcs/nlp/models/en-dep.xz");
		
		String test = "The xpto isn't essential gene.";
		NLPDecoder decoder = new NLPDecoder();
		decoder.setTokenizer(new EnglishTokenizer());
		List<NLPComponent<NLPNode>> components = new ArrayList<>();
		components.add(pos);
		components.add(dep);
		decoder.setComponents(components);
		NLPNode[] nodes = decoder.decode(test);
		for(NLPNode node : nodes){
//			System.out.println(node.getWordForm());
			System.out.println(node.getDependencyLabel());
		}
			
	}

}
