package com.silicolife.textmining.machinelearning.biotml.core.nlp.clearnlp;

import java.io.BufferedReader;
import java.io.File;
import java.io.PrintStream;
import java.util.List;

import org.junit.Test;

import com.clearnlp.component.AbstractComponent;
import com.clearnlp.dependency.DEPTree;
import com.clearnlp.nlp.NLPGetter;
import com.clearnlp.nlp.NLPMode;
import com.clearnlp.reader.AbstractReader;
import com.clearnlp.segmentation.AbstractSegmenter;
import com.clearnlp.tokenization.AbstractTokenizer;
import com.clearnlp.util.UTInput;
import com.clearnlp.util.UTOutput;

public class TestClearNLP {

	private String language = AbstractReader.LANG_EN;
	public TestClearNLP(){		
	};

	public void testWithSentenceString(String modelType, String sentence) throws Exception
	{
		AbstractTokenizer tokenizer  = NLPGetter.getTokenizer(language);
		AbstractComponent tagger     = NLPGetter.getComponent(modelType, language, NLPMode.MODE_POS);
		AbstractComponent parser     = NLPGetter.getComponent(modelType, language, NLPMode.MODE_DEP);
		AbstractComponent identifier = NLPGetter.getComponent(modelType, language, NLPMode.MODE_PRED);
		AbstractComponent classifier = NLPGetter.getComponent(modelType, language, NLPMode.MODE_ROLE);
		AbstractComponent labeler    = NLPGetter.getComponent(modelType, language, NLPMode.MODE_SRL);

		AbstractComponent[] components = {tagger, parser, identifier, classifier, labeler};
		process(tokenizer, components, sentence);
	}

	public void testWithSentenceFile(String modelType, String inputFile, String outputFile) throws Exception
	{
		AbstractTokenizer tokenizer  = NLPGetter.getTokenizer(language);
		AbstractComponent tagger     = NLPGetter.getComponent(modelType, language, NLPMode.MODE_POS);
		AbstractComponent parser     = NLPGetter.getComponent(modelType, language, NLPMode.MODE_DEP);
		AbstractComponent identifier = NLPGetter.getComponent(modelType, language, NLPMode.MODE_PRED);
		AbstractComponent classifier = NLPGetter.getComponent(modelType, language, NLPMode.MODE_ROLE);
		AbstractComponent labeler    = NLPGetter.getComponent(modelType, language, NLPMode.MODE_SRL);

		AbstractComponent[] components = {tagger, parser, identifier, classifier, labeler};
		process(tokenizer, components, UTInput.createBufferedFileReader(inputFile), UTOutput.createPrintBufferedFileStream(outputFile));
	}

	public void process(AbstractTokenizer tokenizer, AbstractComponent[] components, String sentence)
	{
		DEPTree tree = NLPGetter.toDEPTree(tokenizer.getTokens(sentence));

		for (AbstractComponent component : components)
			component.process(tree);

		System.out.println(tree.toStringSRL()+"\n");
	}

	public void process(AbstractTokenizer tokenizer, AbstractComponent[] components, BufferedReader reader, PrintStream fout)
	{
		AbstractSegmenter segmenter = NLPGetter.getSegmenter(language, tokenizer);
		DEPTree tree;

		for (List<String> tokens : segmenter.getSentences(reader))
		{
			tree = NLPGetter.toDEPTree(tokens);

			for (AbstractComponent component : components)
				component.process(tree);

			fout.println(tree.toStringSRL()+"\n");
		}

		fout.close();
	}

	@Test
	public void testClearNLP() throws Exception{
		String modelType  = "medical-en";
		String packagePath = "machinelearning/"+ this.getClass().getPackage().getName().replace(".", "/");
		File file = new File(packagePath.substring(0, packagePath.lastIndexOf("/")));
		String path = file.getAbsolutePath();
		String inputFile  = path+"/sentences.txt";
		String outputFile = path+"/test1.txt";
		String sentence = "Although posttranscriptional regulation of RNA metabolism is increasingly recognized as a key regulatory process in plant response to environmental stresses, reports demonstrating the importance of RNA metabolism control in crop improvement under adverse environmental stresses are severely limited.";
		testWithSentenceString(modelType,sentence);
		testWithSentenceFile(modelType, inputFile, outputFile);
	}

}
