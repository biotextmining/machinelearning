package com.silicolife.textmining.machinelearning.biotml.core.nlp.nlp4j;

import java.util.ArrayList;
import java.util.List;

import com.silicolife.textmining.machinelearning.biotml.core.corpora.BioTMLSentenceImpl;
import com.silicolife.textmining.machinelearning.biotml.core.corpora.BioTMLTokenImpl;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLSentence;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLToken;

import edu.emory.mathcs.nlp.common.util.Language;
import edu.emory.mathcs.nlp.common.util.NLPUtils;
import edu.emory.mathcs.nlp.component.morph.MorphologicalAnalyzer;
import edu.emory.mathcs.nlp.component.template.NLPComponent;
import edu.emory.mathcs.nlp.component.template.node.NLPNode;
import edu.emory.mathcs.nlp.component.tokenizer.EnglishTokenizer;
import edu.emory.mathcs.nlp.component.tokenizer.token.Token;
import edu.emory.mathcs.nlp.decode.NLPDecoder;

/**
 * 
 * BioTMLNLP4J Singleton to accelerate the BioTMLNLP4J parser models performance access.
 * 
 * @since 1.1.0
 * @author Ruben Rodrigues ({@code rrodrigues@silicolife.com})
 */
public class BioTMLNLP4J {
	
	private final static String posModelFile = "edu/emory/mathcs/nlp/models/en-pos.xz";
	private final static String depModelFile ="edu/emory/mathcs/nlp/models/en-dep.xz";
	
	private static BioTMLNLP4J _instance;
	private NLPComponent<NLPNode> posModel;
	private NLPComponent<NLPNode> dependencyModel;
	
	public BioTMLNLP4J(){
	}
	
	/**
	 * Gives access to the NLP4J instance.
	 * 
	 * @return Instance of NLP4J singleton.
	 */
	public static synchronized BioTMLNLP4J getInstance() {
		if (_instance == null) 
			BioTMLNLP4J.createInstance();
		
		return _instance;
	}

	/**
	 * Creates the singleton instance.
	 *  
	 */
	private static void createInstance(){
		if (_instance == null) 
			_instance = new BioTMLNLP4J();
		
	}
	
	private NLPComponent<NLPNode> getPosModel() {
		return posModel;
	}

	private NLPComponent<NLPNode> getDependecyModel() {
		return dependencyModel;
	}

	
	private synchronized void initPOSModel(){
		if(getPosModel() == null)
			posModel = NLPUtils.getComponent(posModelFile);
	}

	private synchronized void initDependecyModel(){
		if(getDependecyModel() == null)
			dependencyModel = NLPUtils.getComponent(depModelFile);
	}
	
	private IBioTMLSentence getSentence(List<Token> nlp4jTokens, String document){
		List<IBioTMLToken> sentence = new ArrayList<>();
		for(Token nlp4jToken : nlp4jTokens)
			sentence.add(new BioTMLTokenImpl(nlp4jToken.getWordForm(), nlp4jToken.getStartOffset(), nlp4jToken.getEndOffset()));
		
		String source = new String();
		if(!sentence.isEmpty()){
			long start = sentence.get(0).getStartOffset();
			long end = sentence.get(sentence.size()-1).getEndOffset();
			source = document.substring((int)start, (int) end);
		}
		return new BioTMLSentenceImpl(sentence, source);
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
		List<IBioTMLSentence> documentSentences = new ArrayList<>();
		EnglishTokenizer tokenizer = new EnglishTokenizer();
		List<List<Token>> sentences = tokenizer.segmentize(document);
		for(List<Token> sentence : sentences)
			documentSentences.add(getSentence(sentence, document));
		
		return documentSentences;
	}
	
	/**
	 * 
	 * Method that processes the part-of-speech tagging.
	 * The array of strings represents a array of tokens that are present in one sentence.
	 * The processing result creates a array of part-of-speech tags.
	 * The tags array contains one part-of-speech tag for each token in the input array. 
	 * The corresponding tag can be found at the same index as the token has in the input array.
	 * 
	 * @param sentence - Array of token strings.
	 * @return Array of part-of-speech tags.
	 */
	public List<String> processPos(List<String> sentence){
		List<String> pos = new ArrayList<>();
		if(getPosModel()==null)
			initPOSModel();
		
		NLPDecoder decoder = getDecoder(getPosModel());
		NLPNode[] nodes = decoder.toNodeArray(convertArrayStringToListToken(sentence));
		nodes = decoder.decode(nodes);
		
		for(int i=1; i<nodes.length; i++)
			pos.add(nodes[i].getPartOfSpeechTag());
		
		return pos;
	}
	
	/**
	 * 
	 * Method that processes the lemmatizer.
	 * The array of strings represents a array of tokens that are present in one sentence.
	 * The processing result creates a array of lemmas.
	 * The tags array contains one lemma for each token in the input array. 
	 * The corresponding tag can be found at the same index as the token has in the input array.
	 * 
	 * @param sentence - Array of token strings.
	 * @return Array of lemmas.
	 */
	public List<String> processLemma(List<String> sentence){
		List<String> lemmas = new ArrayList<>();
		if(getPosModel()==null)
			initPOSModel();
		
		List<NLPComponent<NLPNode>> components = new ArrayList<>();
		components.add(getPosModel());
		components.add(new MorphologicalAnalyzer<>(Language.ENGLISH));
		
		NLPDecoder decoder = getDecoder(components);
		
		NLPNode[] nodes = decoder.toNodeArray(convertArrayStringToListToken(sentence));
		nodes = decoder.decode(nodes);
		for(int i=1; i<nodes.length; i++)
			lemmas.add(nodes[i].getLemma());
		
		return lemmas;
	}
	
	public NLPNode[] processDependency(List<String> sentence){
		if(getDependecyModel() == null)
			initDependecyModel();
		
		NLPDecoder decoder = getDecoder(getDependecyModel());
		NLPNode[] nodes = decoder.toNodeArray(convertArrayStringToListToken(sentence));
		return decoder.decode(nodes);
	}
	
	/**
	 * 
	 * Method that releases the memory allocation for the OpenNLP models.
	 * 
	 */
	public void clearModelsInMemory(){
		posModel = null;
		dependencyModel = null;
	}
	
	private List<Token> convertArrayStringToListToken(List<String> sentence){
		List<Token> tokens = new ArrayList<>();
		for(String token : sentence)
			tokens.add(new Token(token));
		
		return tokens;
	}
	
	private NLPDecoder getDecoder(NLPComponent<NLPNode> component){
		NLPDecoder decoder = new NLPDecoder();
		List<NLPComponent<NLPNode>> components = new ArrayList<>();
		components.add(component);
		decoder.setComponents(components);
		return decoder;
	}
	
	private NLPDecoder getDecoder(List<NLPComponent<NLPNode>> components){
		NLPDecoder decoder = new NLPDecoder();
		decoder.setComponents(components);
		return decoder;
	}
	


}
