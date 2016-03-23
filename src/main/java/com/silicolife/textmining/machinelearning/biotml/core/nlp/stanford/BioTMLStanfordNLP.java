package com.silicolife.textmining.machinelearning.biotml.core.nlp.stanford;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.silicolife.textmining.machinelearning.biotml.core.corpora.BioTMLSentence;
import com.silicolife.textmining.machinelearning.biotml.core.corpora.BioTMLToken;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLSentence;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLToken;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.Morphology;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.process.WordToSentenceProcessor;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import edu.stanford.nlp.trees.Tree;

/**
 * 
 * Singleton class of StanfordNLP.
 * 
 * @since 1.0.0
 * @author Ruben Rodrigues ({@code rrodrigues@silicolife.com})
 */

public class BioTMLStanfordNLP {
	
	private static BioTMLStanfordNLP _instance;
	private MaxentTagger posTaggerModel;
	private LexicalizedParser chunkTaggerModel;
	
	private BioTMLStanfordNLP(){
	}
	
	/**
	 * Gives access to the StandFordNLP instance.
	 * @return Standford instance.
	 */
	public static synchronized BioTMLStanfordNLP getInstance() {
		if (_instance == null) {
			BioTMLStanfordNLP.createInstance();
		}
		return _instance;
	}
	
	/**
	 * Creates the singleton instance.
	 */
	private static void createInstance(){

		if (_instance == null) {
			_instance = new BioTMLStanfordNLP();
		}
	}
	
	private synchronized void initPOSTaggerModel(){
		if(getPosTaggerModel() == null){
			posTaggerModel = new MaxentTagger(MaxentTagger.DEFAULT_JAR_PATH);
		}
	}
	
	private synchronized void initChunkTaggerModel(){
		if(getChunkTaggerModel() == null){
			chunkTaggerModel = LexicalizedParser.loadModel(LexicalizedParser.DEFAULT_PARSER_LOC);
		}
	}

	private MaxentTagger getPosTaggerModel() {
		return posTaggerModel;
	}
	
	private LexicalizedParser getChunkTaggerModel() {
		return chunkTaggerModel;
	}
	
	private List<CoreLabel> getListTokens(String document){
		List<CoreLabel> tokens = new ArrayList<CoreLabel>();
		Reader docreader = new StringReader(document);
		PTBTokenizer<CoreLabel> ptbt = new PTBTokenizer<CoreLabel>(docreader, new CoreLabelTokenFactory(), "");
		while(ptbt.hasNext()){
			tokens.add(ptbt.next());
		}
		return tokens;
	}
	
	/**
	 * 
	 * Method that processes the document tokenization and sentence spliting. 
	 * The document is a string all raw text.
	 * 
	 * @param document - String that contains all document text.
	 * @return List of {@link IBioTMLSentence}.
	 */
	public List<IBioTMLSentence> getSentences(String document){
		ArrayList<IBioTMLSentence> sentences = new ArrayList<IBioTMLSentence>();
		List<CoreLabel> tokensCoreLabels = getListTokens(document);
		WordToSentenceProcessor<CoreLabel> wts = new WordToSentenceProcessor<CoreLabel>(WordToSentenceProcessor.NewlineIsSentenceBreak.ALWAYS);
		List<List<CoreLabel>> sentencesCoreLabels = wts.process(tokensCoreLabels);
		for(List<CoreLabel> sentenceCoreLabels : sentencesCoreLabels){
			List<IBioTMLToken> tokens = new ArrayList<IBioTMLToken>();
			for(CoreLabel token : sentenceCoreLabels){
				tokens.add(new BioTMLToken(token.originalText(), (long) token.beginPosition(), (long)token.endPosition()));
			}
			long start = tokens.get(0).getStartOffset();
			long end = tokens.get(tokens.size()-1).getEndOffset();
			sentences.add(new BioTMLSentence(tokens, document.substring((int)start, (int)end)));
		}
		return sentences;
	}
	
	/**
	 * 
	 * Method that processes the part-of-speech tagging.
	 * The list of strings represents a list of tokens that are present in one sentence.
	 * The processing result creates a list of part-of-speech tags.
	 * The tags list contains one part-of-speech tag for each token in the input list. 
	 * The corresponding tag can be found at the same index as the token has in the input list.
	 * 
	 * @param tokens - List token strings.
	 * @return List of part-of-speech tags.
	 */
	public List<String> processPos(List<String> tokens){
		List<String> posTags = new ArrayList<String>();
		StringBuilder builder = new StringBuilder();
		for(int i=0;i<tokens.size(); i++) {
		    builder.append(tokens.get(i));
		    if( i!= tokens.size()-1){
		    	builder.append(" ");
		    }
		}
		if(getPosTaggerModel()==null){
			initPOSTaggerModel();
		}
		String test = getPosTaggerModel().tagTokenizedString(builder.toString());
		String[] result = test.split(" ");
		for(String tokenTagged : result){
			String[] tokenTaggedSplitted = tokenTagged.split("_");
			posTags.add(tokenTaggedSplitted[1]);
		}
		return posTags;
	}
	
	/**
	 * 
	 * Method that processes the lemmatization tagging.
	 * This uses as input a list of tokens that are present in one sentence and the list of part-of-speech tags.
	 * The processing result creates a list of lemmas.
	 * The lemmas list contains one lemma for each token in the input list. 
	 * The corresponding lemma can be found at the same index as the token has in the input list.
	 * 
	 * @param tokens - List token strings.
	 * @param posTags - List of part-of-speech tags.
	 * @return List of lemmas.
	 */
	public List<String> processLemmas(List<String> tokens, List<String> posTags){
		List<String> lemmas = new ArrayList<String>();
		Morphology lemmaTagger = new Morphology();
		int i = 0;
		while(i<tokens.size()){
			String phrasalVerb = phrasalVerb(lemmaTagger, tokens.get(i), posTags.get(i));
			if (phrasalVerb == null) {
				lemmas.add(lemmaTagger.lemma(tokens.get(i), posTags.get(i)));
			} else {
				lemmas.add(phrasalVerb);
			}
			i++;
		}
		return lemmas;
	}
	
	/**
	 * 
	 * Method that processes the chunking parsing (namely known as one type of Shallow parsing). 
	 * This method processes a Shallow parsing tree of one sentence. 
	 * Each string contains the path of chunking heads from token into tree root.
	 * The corresponding path can be found at the same index as the token has in the input array.
	 * 
	 * @param tokens - List of token strings.
	 * @return List of chunking head paths.
	 */
	public List<String> processChunkingParsing(List<String> tokens){
		List<String> chunkingTokenToRoot = new ArrayList<String>();
		if(getChunkTaggerModel()==null){
			initChunkTaggerModel();
		}
		Tree root = getChunkTaggerModel().parseStrings(tokens);
		List<Tree> tokensInTree = root.getLeaves();
		for(Tree token : tokensInTree){
			List<Tree> path = root.pathNodeToNode(token, root);
			String childnode = new String();
			String result = new String();
			for(Tree node : path){
				if(childnode.isEmpty()){
					childnode = node.value();
				}else{
					if(!node.value().equals("S") && !node.value().equals("ROOT")){
						if(result.isEmpty()){
							result = node.value()+"_OF="+childnode;
						}else{
							result = result + "\t"+node.value()+"_OF="+childnode;
						}
						childnode = node.value();
					}
				}
			}
			chunkingTokenToRoot.add(result);
		}
		return chunkingTokenToRoot;
	}
	
	private String phrasalVerb(Morphology morpha, String word, String tag) {
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
	
	public void clearModelsInMemory(){
		posTaggerModel = null;
		chunkTaggerModel = null;
		_instance = null;
	}

}
