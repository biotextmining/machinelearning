package com.silicolife.textmining.machinelearning.biotml.core.nlp.standford;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.junit.Test;

import edu.stanford.nlp.dcoref.CorefChain;
import edu.stanford.nlp.dcoref.CorefCoreAnnotations.CorefChainAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.process.Morphology;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations.BasicDependenciesAnnotation;
import edu.stanford.nlp.semgraph.SemanticGraphFactory;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import edu.stanford.nlp.trees.EnglishGrammaticalStructure;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.SemanticHeadFinder;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations.TreeAnnotation;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.Filters;

public class StandFordTest {

	public StandFordTest(){

	}

	@Test
	public void test1(){
		// creates a StanfordCoreNLP object, with POS tagging, lemmatization, NER, parsing, and coreference resolution 
		Properties props = new Properties();
		props.setProperty("annotators", "tokenize,ssplit, pos, lemma, parse");
		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

		// read some text in the text variable
		String text = "In glioblastomas, the localization of IGF-1 immunoreactivity was notable for several features: frequent accentuation in the perivascular tumor cells surrounding microvascular hyperplasia; increased levels in reactive astrocytes at the margins of tumor infiltration; and selective expression in microvascular cells exhibiting endothelial/pericytic hyperplasia."; // Add your text here!

		// create an empty Annotation just with the given text
		Annotation document = new Annotation(text);

		// run all Annotators on this text
		pipeline.annotate(document);

		// these are all the sentences in this document
		// a CoreMap is essentially a Map that uses class objects as keys and has values with custom types
		List<CoreMap> sentences = document.get(SentencesAnnotation.class);

		for(CoreMap sentence: sentences) {
			// traversing the words in the current sentence
			// a CoreLabel is a CoreMap with additional token-specific methods
			for (CoreLabel token: sentence.get(TokensAnnotation.class)) {
				// this is the text of the token
				String word = token.get(TextAnnotation.class);
				System.out.println(word);
				// this is the POS tag of the token
				String pos = token.get(PartOfSpeechAnnotation.class);
				System.out.println(pos);
				// this is the NER label of the token
				String ne = token.get(NamedEntityTagAnnotation.class);
				System.out.println(ne);
			}

			System.out.println("POS Test");
			testPOSTaggerLemma();

			// this is the parse tree of the current sentence
			Tree tree = sentence.get(TreeAnnotation.class);

			System.out.println(tree.toString());

			// this is the Stanford dependency graph of the current sentence
			SemanticGraph dependencies = sentence.get(BasicDependenciesAnnotation.class);
			System.out.println(dependencies.toString());
		}

		// This is the coreference link graph
		// Each chain stores a set of mentions that link to each other,
		// along with a method for getting the most representative mention
		// Both sentence and token offsets start at 1!
		@SuppressWarnings("unused")
		Map<Integer, CorefChain> graph = 
				document.get(CorefChainAnnotation.class);

	}
	
//	@Test
	public String phrasalVerb(Morphology morpha, String word, String tag) {
		String[] prep = {"abroad", "across", "after", "ahead", "along", "aside", "away", "around", "back", "down", "forward", "in", "off", "on", "over", "out", "round", "together", "through", "up"};
		List<String> particles = Arrays.asList(prep);
		// must be a verb and contain an underscore
		assert(word != null);
		assert(tag != null);
		if(!tag.startsWith("VB")  || !word.contains("_")) return null;

		// check whether the last part is a particle
		String[] verb = word.split("_");
		if(verb.length != 2) return null;
		String particle = verb[1];
		if(particles.contains(particle)) {
			String base = verb[0];
			String lemma = morpha.lemma(base, tag);
			return lemma + '_' + particle;
		}

		return null;
	}
	
//	@Test
	public void testPOSTaggerLemma(){
		String[] sentence = new String[]{"In","glioblastomas",",","the","localization","of","IGF","-",
				"1","immunoreactivity","was","notable","for","several","features",":","frequent","accentuation","in","the","perivascular","tumor","cells","surrounding","microvascular",
				"hyperplasia",";","increased","levels","in","reactive","astrocytes","at","the","margins","of","tumor","infiltration",";",
				"and","selective","expression","in","microvascular","cells","exhibiting","endothelial","/","pericytic","hyperplasia","."};
		StringBuilder builder = new StringBuilder();
		for(int i=0;i<sentence.length; i++) {
			builder.append(sentence[i]);
			if( i!= sentence.length-1){
				builder.append(" ");
			}
		}
		MaxentTagger tagger = new MaxentTagger(MaxentTagger.DEFAULT_JAR_PATH);
//		Morphology taggerLemma = new Morphology();
		String test = tagger.tagTokenizedString(builder.toString());
		String[] res = test.split(" ");
		for(String result : res){
			String[] finalRes = result.split("_");
			System.out.println(finalRes[1]);
			//lemma code:
			//			String phrasalVerb = phrasalVerb(taggerLemma, finalRes[0], finalRes[1]);
			//			if (phrasalVerb == null) {
			//				System.out.println(taggerLemma.lemma(finalRes[0], finalRes[1]));
			//		      } else {
			//		    	System.out.println(phrasalVerb);
			//		      }

		}
	}

////	@Test
//	public void testChunkingParser(){
//		String[] sentence = new String[]{"In","glioblastomas",",","the","localization","of","IGF-1","immunoreactivity","was","notable","for","several","features",":","frequent","accentuation","in","the","perivascular","tumor","cells","surrounding","microvascular",
//				"hyperplasia",";","increased","levels","in","reactive","astrocytes","at","the","margins","of","tumor","infiltration",";",
//				"and","selective","expression","in","microvascular","cells","exhibiting","endothelial/pericytic","hyperplasia","."};
//		List<String> list = Arrays.asList(sentence);
//		LexicalizedParser lp = LexicalizedParser.loadModel(LexicalizedParser.DEFAULT_PARSER_LOC);
//		Tree tree = lp.parseStrings(list);
//		System.out.println(tree.toString());
////		Filter<String> wordFilt = Filters.acceptFilter();//new PennTreebankLanguagePack().punctuationWordRejectFilter();//
//		GrammaticalStructure gs = new EnglishGrammaticalStructure(tree, wordFilt, new SemanticHeadFinder(true), true);
//		SemanticGraph unDeps = SemanticGraphFactory.makeFromTree(gs, SemanticGraphFactory.Mode.BASIC, false, true, null);
//
//		System.out.println(unDeps.toString());
//		Collection<IndexedWord> roots = unDeps.getRoots();
//		Iterator<IndexedWord> itRoots = roots.iterator();
//		System.out.println(itRoots.next().word());
//		//	    for(IndexedWord vertex : unDeps.vertexSet()){
//		//	    	System.out.println(vertex.word());
//		//	    	List<IndexedWord> finaltest = unDeps.getPathToRoot(vertex);
//		//	    	IndexedWord tes = unDeps.getParent(vertex);
//		//	    	System.out.println(tes.toString());
//		//	    	if(finaltest.size()!=0){
//		//		    	IndexedWord finalVertex = finaltest.get(finaltest.size()-1);
//		//		    	System.out.println(finalVertex.word());
//		//		    	List<SemanticGraphEdge> res = unDeps.getAllEdges(finalVertex, vertex);
//		//		    	System.out.println(finaltest.toString());
//		//		    	System.out.println(res.toString());
//		//	    	}
//
//		//	    }
//		//		SemanticGraph unDeps = SemanticGraphFactory.generateUncollapsedDependencies(tree);
//		//		Set<IndexedWord> test = unDeps.vertexSet();
//
//		//		System.out.println(test.toString());
//		//		List<IndexedWord> finaltest = unDeps.getPathToRoot(test);
//		//		System.out.println(finaltest.toString());
//		//		SemanticGraph ccDeps = SemanticGraphFactory.generateCCProcessedDependencies(new EnglishGrammaticalStructure(tree));
//		//		System.out.println(ccDeps.toString());
//		//		IndexedWord tests = ccDeps.getNodeByIndex(0);
//		//		System.out.println(tests.toString());
//	}
}
