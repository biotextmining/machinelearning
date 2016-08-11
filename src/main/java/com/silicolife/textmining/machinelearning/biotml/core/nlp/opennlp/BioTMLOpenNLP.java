package com.silicolife.textmining.machinelearning.biotml.core.nlp.opennlp;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.silicolife.textmining.machinelearning.biotml.core.corpora.BioTMLSentenceImpl;
import com.silicolife.textmining.machinelearning.biotml.core.corpora.BioTMLTokenImpl;
import com.silicolife.textmining.machinelearning.biotml.core.exception.BioTMLException;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLSentence;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLToken;

import opennlp.tools.chunker.ChunkerME;
import opennlp.tools.chunker.ChunkerModel;
import opennlp.tools.parser.AbstractBottomUpParser;
import opennlp.tools.parser.Parse;
import opennlp.tools.parser.Parser;
import opennlp.tools.parser.ParserFactory;
import opennlp.tools.parser.ParserModel;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.Span;

/**
 * 
 * Singleton class of OpenNLP.
 * 
 * @since 1.0.0
 * @author Ruben Rodrigues ({@code rrodrigues@silicolife.com})
 */
public class BioTMLOpenNLP {

	private final static String sentenceModelFile = "nlpmodels/en-sent.bin";
	private final static String tokeniserModelFile = "nlpmodels/en-token.bin";
	private final static String postaggingModelFile = "nlpmodels/en-pos-maxent.bin";
	private final static String parsingModelFile = "nlpmodels/en-parser-chunking.bin";
	private final static String chunkerModelFile = "nlpmodels/en-chunker.bin";
	private final static String sentenceModelFileBIN = "processes/src/main/resources/nlpmodels/en-sent.bin";
	private final static String tokeniserModelFileBIN = "processes/src/main/resources/nlpmodels/en-token.bin";
	private final static String postaggingModelFileBIN = "processes/src/main/resources/nlpmodels/en-pos-maxent.bin";
	private final static String parsingModelFileBIN = "processes/src/main/resources/nlpmodels/en-parser-chunking.bin";
	private final static String chunkerModelFileBIN = "processes/src/main/resources/nlpmodels/en-chunker.bin";

	private SentenceModel sentenceModel;
	private POSModel postaggerModel;
	private TokenizerModel tokeniserModel; 
	private ChunkerModel chunkerModel;
	private ParserModel parserModel;

	private static BioTMLOpenNLP _instance;

	private BioTMLOpenNLP()
	{

	}

	/**
	 * Gives access to the OpenNLP instance
	 * @return OpenNLP instance.
	 */
	public static synchronized BioTMLOpenNLP getInstance() {
		if (_instance == null) {
			BioTMLOpenNLP.createInstance();
		}
		return _instance;
	}

	/**
	 * Creates the singleton instance.
	 */
	private static void createInstance(){

		if (_instance == null) {
			_instance = new BioTMLOpenNLP();
		}
	}

	private synchronized void initChunkerModelModel() throws BioTMLException {
		if(getChunkerModel()==null){
			try {
				InputStream modelIn = BioTMLOpenNLP.class.getClassLoader().getResourceAsStream(chunkerModelFile);
				if(modelIn==null){

					modelIn = new FileInputStream(chunkerModelFileBIN);

				}
				chunkerModel = new ChunkerModel(modelIn);
				modelIn.close();
			} catch ( IOException exc) {
				throw new BioTMLException(13,exc);
			}
		}
	}

	private synchronized void initParserModel() throws BioTMLException{
		if(getParserModel()==null){
			try {
				InputStream parsingModelIn = BioTMLOpenNLP.class.getClassLoader().getResourceAsStream(parsingModelFile);
				if(parsingModelIn==null){
					parsingModelIn = new FileInputStream(parsingModelFileBIN);
				}
				parserModel = new ParserModel(parsingModelIn);
				parsingModelIn.close();
			} catch ( IOException exc) {
				throw new BioTMLException(13,exc);
			}
		}
	}

	private synchronized void initSentenceModel() throws BioTMLException{	
		if(getSentenceModel()==null){
			try {
				InputStream modelIn = BioTMLOpenNLP.class.getClassLoader().getResourceAsStream(sentenceModelFile);
				if(modelIn==null){
					modelIn = new FileInputStream(sentenceModelFileBIN);
				}
				sentenceModel = new SentenceModel(modelIn);
				modelIn.close();
			} catch ( IOException exc) {
				throw new BioTMLException(13,exc);
			}
		}
	}

	private synchronized void initTokenizerModel() throws BioTMLException{
		if(getTokenizerModel()==null){
			try {
				InputStream tokenizerFileInput = BioTMLOpenNLP.class.getClassLoader().getResourceAsStream(tokeniserModelFile);
				if(tokenizerFileInput==null){
					tokenizerFileInput = new FileInputStream(tokeniserModelFileBIN);
				}
				tokeniserModel = new TokenizerModel(tokenizerFileInput);
				tokenizerFileInput.close();
			} catch ( IOException exc) {
				throw new BioTMLException(13,exc);
			}
		}
	}

	private synchronized void initPosTagModel() throws BioTMLException{
		if(getPOSModel()==null){
			try {
				InputStream postaggerFileInput = BioTMLOpenNLP.class.getClassLoader().getResourceAsStream(postaggingModelFile);
				if(postaggerFileInput==null){
					postaggerFileInput = new FileInputStream(postaggingModelFileBIN);
				}
				postaggerModel = new POSModel(postaggerFileInput);
				postaggerFileInput.close();
			} catch ( IOException exc) {
				throw new BioTMLException(13,exc);
			}
		}
	}

	private ChunkerModel getChunkerModel(){
		return chunkerModel;
	}

	private ParserModel getParserModel(){
		return parserModel;
	}

	private Parser getParser(){
		return ParserFactory.create(getParserModel());
	}

	private SentenceModel getSentenceModel(){
		return sentenceModel;
	}

	private TokenizerModel getTokenizerModel(){
		return tokeniserModel;
	}

	private POSModel getPOSModel(){
		return postaggerModel;
	}

	private Set<IBioTMLToken> getListTokens(String text) throws BioTMLException{
		if(getTokenizerModel() == null){
			initTokenizerModel();
		}
		Set<IBioTMLToken> listTokens = new LinkedHashSet<IBioTMLToken>();
		Tokenizer tokenizer = new TokenizerME(getTokenizerModel());
		Span[] tokens = tokenizer.tokenizePos(text);
		for(Span token : tokens){
			listTokens.add(new BioTMLTokenImpl((String)token.getCoveredText(text), (long)token.getStart(), (long)token.getEnd()));
		}
		return listTokens;
	}

	private List<IBioTMLToken> getSentecebyTokens(Set<IBioTMLToken> tokens, Long sentenceStart, Long sentenceEnd){
		List<IBioTMLToken> listTokens = new ArrayList<IBioTMLToken>();
		boolean stop = false;
		Iterator<IBioTMLToken> itTokens = tokens.iterator();
		while(itTokens.hasNext() && !stop){
			IBioTMLToken token = itTokens.next();
			if(!(token.getStartOffset()<sentenceStart)){
				if((token.getStartOffset()>=sentenceStart) &&(token.getEndOffset()<=sentenceEnd)){
					listTokens.add(token);
				}
				if(token.getEndOffset()>=sentenceEnd){
					stop = true;
				}
			}
		}
		return listTokens;
	}

	/**
	 * 
	 * Method that processes the document tokenization and sentence spliting. 
	 * The document is a string of all raw text.
	 * 
	 * @param document - String that contains all document text.
	 * @return List of {@link IBioTMLSentence}.
	 * @throws BioTMLException
	 */
	public List<IBioTMLSentence> getSentences(String document) throws BioTMLException{
		if(getSentenceModel()==null){
			initSentenceModel();
		}
		List<IBioTMLSentence> sentences = new ArrayList<IBioTMLSentence>();
		Set<IBioTMLToken> tokens = getListTokens(document);
		SentenceDetectorME sentenceDetector = new SentenceDetectorME(getSentenceModel());
		Span[] sentencespos = sentenceDetector.sentPosDetect(document);
		for(Span sentence : sentencespos){
			sentences.add(new BioTMLSentenceImpl(getSentecebyTokens(tokens, (long) sentence.getStart(), (long) sentence.getEnd()), document.substring(sentence.getStart(), sentence.getEnd())));
		}
		return sentences;
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
	 * @throws BioTMLException
	 */
	public String[] processPos(String[] sentence) throws BioTMLException{
		if(getPOSModel()==null){
			initPosTagModel();
		}
		POSTaggerME tagger = new POSTaggerME(getPOSModel());
		return tagger.tag(sentence);
	}

	/**
	 *
	 * Method that processes the chunking tagging.
	 * The array of strings represents a array of tokens that are present in one sentence.
	 * The processing result creates a array of chunking tags.
	 * The tags array contains one chunk tag for each token in the input array. 
	 * The corresponding tag can be found at the same index as the token has in the input array.
	 * 
	 * @param sentence - Array of token strings.
	 * @param posTaggedSentence - Array of part-of-speech tags.
	 * @return Array of chunking tags.
	 * @throws BioTMLException
	 */
	public String[] processChunking(String[] sentence, String[] posTaggedSentence) throws BioTMLException{
		if(getChunkerModel()==null){
			initChunkerModelModel();
		}
		ChunkerME chunker = new ChunkerME(getChunkerModel());
		return chunker.chunk(sentence, posTaggedSentence);
	}

	/**
	 * 
	 * Method that processes the chunking parsing (namely known as one type of Shallow parsing). 
	 * This method processes a Shallow parsing tree of one sentence. 
	 * Each string contains the path of chunking heads from token into tree root.
	 * The corresponding path can be found at the same index as the token has in the input array.
	 * 
	 * @param sentence - Array of token strings.
	 * @return Array of chunking head paths. 
	 * @throws BioTMLException
	 */

	public String[] processChunkingParsing(String[] sentence) throws BioTMLException{
		if(getTokenizerModel() == null){
			initTokenizerModel();
		}
		if(getParserModel() == null){
			initParserModel();
		}
		StringBuilder builder = new StringBuilder();
		for(int i=0;i<sentence.length; i++) {
			builder.append(sentence[i]);
			if( i!= sentence.length-1){
				builder.append(" ");
			}
		}
		String text = builder.toString();
		Parse p = new Parse(text, new Span(0, text.length()), AbstractBottomUpParser.INC_NODE, 1, 0);
		Tokenizer tokenizer = new TokenizerME(getTokenizerModel());
		Span[] spans = tokenizer.tokenizePos(text);
		for (int idx=0; idx < spans.length; idx++) {
			Span span = spans[idx];
			p.insert(new Parse(text, span, AbstractBottomUpParser.TOK_NODE, 0, idx));
		}
		Parse actualParse;
		actualParse = getParser().parse(p);
		String[] result = new String[spans.length];
		//		Parse[] actualParse = ParserTool.parseLine(text, getParser(), 1);
		recursiveParseResult(actualParse, result, spans);
		return result;
	}

	private String getStringChain(Parse node, String text){
		if(node.getType().equals(AbstractBottomUpParser.TOP_NODE)){
			return text;
		}
		else{
			if(node.getParent()==null){
				//solution for non tree parsing... The sentence is not parsed by the model...
				if(text.isEmpty()){
					return text;
				}else{
					return text.substring(1);
				}
			}
			if(node.getType().equals(AbstractBottomUpParser.TOK_NODE))
				return getStringChain(node.getParent(),text);
			if(node.getParent().getType().equals(AbstractBottomUpParser.TOP_NODE))
				if(text.isEmpty()){
					return getStringChain(node.getParent(),text);
				}else{
					return getStringChain(node.getParent(),text.substring(1));
				}
				
			return getStringChain(node.getParent(),"\t"+node.getParent().getType()+"_OF="+node.getType()+text);
		}
	}

	private void recursiveParseResult(Parse res, String[] result, Span[] spans){
		if(res.getType().equals(AbstractBottomUpParser.TOK_NODE)){
			Span thisNode = res.getSpan();
			for(int i=0; i<spans.length; i++){
				if((spans[i].getStart() == thisNode.getStart())&&(spans[i].getEnd() == thisNode.getEnd())){
					result[i] = getStringChain(res, new String());
					return;
				}
			}
			return;
		}
		for(Parse child : res.getChildren()){
			recursiveParseResult(child, result, spans);
		}
	}

	/**
	 * 
	 * Method that releases the memory allocation for the OpenNLP models.
	 * 
	 */
	public void clearModelsInMemory(){
		sentenceModel = null;
		postaggerModel = null;
		tokeniserModel = null; 
		chunkerModel = null;
		parserModel = null;
		_instance = null;
	}
}
