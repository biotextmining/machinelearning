package com.silicolife.textmining.machinelearning.biotml.core.mllibraries.mallet;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import com.silicolife.textmining.machinelearning.biotml.core.BioTMLConstants;
import com.silicolife.textmining.machinelearning.biotml.core.corpora.BioTMLAnnotationImpl;
import com.silicolife.textmining.machinelearning.biotml.core.corpora.otherdatastructures.BioTMLDocSentTokenIDs;
import com.silicolife.textmining.machinelearning.biotml.core.corpora.otherdatastructures.BioTMLTokensWithFeaturesAndLabels;
import com.silicolife.textmining.machinelearning.biotml.core.exception.BioTMLException;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLAnnotation;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLAnnotationsRelation;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLCorpus;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLCorpusToInstanceMallet;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLDocument;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLFeatureGeneratorConfigurator;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLSentence;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLToken;
import com.silicolife.textmining.machinelearning.biotml.core.mllibraries.mallet.multithread.CorpusSentenceAndFeaturesToInstanceThread;
import com.silicolife.textmining.machinelearning.biotml.core.mllibraries.mallet.multithread.InstanceListExtended;
import com.silicolife.textmining.machinelearning.biotml.core.models.BioTMLREModelTypes;

import cc.mallet.pipe.Pipe;
import cc.mallet.types.InstanceList;

/**
 * 
 * A class responsible for corpus ({@link IBioTMLCorpus}) conversion into Mallet Instances.
 * 
 * @since 1.0.0
 * @version 1.0.2
 * @author Ruben Rodrigues ({@code rrodrigues@silicolife.com})
 */

public class BioTMLCorpusToInstanceMallet implements IBioTMLCorpusToInstanceMallet{

	private IBioTMLCorpus corpus;
	private String annotType;
	private String ieType;
	private boolean stop=false;
	private ExecutorService executor;

	/**
	 * 
	 * Initializes the conversion of BioTMCorpus into Mallet Instances regarding the annotation type.
	 * 
	 * @param corpus - {@link IBioTMLCorpus} to convert.
	 * @param annotType - Annotation string type (e.g. protein, gene, etc.).
	 * @param ieType - Information Extraction string type (e.g. NER or RE).
	 */
	public BioTMLCorpusToInstanceMallet(IBioTMLCorpus corpus, String annotType, String ieType){
		this.corpus = corpus;
		this.annotType = annotType;
		this.ieType = ieType;
	}

	public IBioTMLCorpus getCorpusToConvert() {
		return corpus;
	}

	public String getConsideredAnnotationType() {
		return annotType;
	}

	public String getIEAnnotationType(){
		return ieType;
	}

	public InstanceList exportToMalletFeatures(Pipe p, int threads,  IBioTMLFeatureGeneratorConfigurator configuration) throws BioTMLException{
		InstanceListExtended instances = new InstanceListExtended(p);
		createFeaturesThreadExecutor(threads);
		if(getIEAnnotationType().equals(BioTMLConstants.ner.toString())){
			createFeaturesForNER(configuration, instances);
		}
		if(getIEAnnotationType().equals(BioTMLConstants.re.toString())){
			createFeaturesForRE(configuration, instances);
		}
		finishThreadsFromExecutor();
		return instances;
	}

	private void createFeaturesForRE(IBioTMLFeatureGeneratorConfigurator configuration, InstanceListExtended instances)
			throws BioTMLException {
		int docID=0;
		while(docID < getCorpusToConvert().getDocuments().size() && !stop ){
			IBioTMLDocument doc = getCorpusToConvert().getDocument(docID);
			int sentID=0;
			while(  sentID < doc.getSentences().size()  && !stop ){
				IBioTMLSentence sentence = doc.getSentence(sentID);
				processSentenceForRE(configuration, instances, doc, sentID, sentence);
				sentID++;
			}
			docID++;
		}
	}

	private void processSentenceForRE(IBioTMLFeatureGeneratorConfigurator configuration, InstanceListExtended instances,
			IBioTMLDocument doc, int sentID, IBioTMLSentence sentence) throws BioTMLException {
		if(!getConsideredAnnotationType().equals(BioTMLREModelTypes.entityentiy.toString())
				&& !getConsideredAnnotationType().equals(BioTMLREModelTypes.entityentiyonlyannotations.toString())){
			List<IBioTMLAnnotation> clues = getClueAnnotations(doc.getID(), sentence);
			List<IBioTMLAnnotation> annotations = getSentenceAnnotations(doc.getID(), sentence);
			for(IBioTMLAnnotation clue : clues){
				BioTMLTokensWithFeaturesAndLabels sentenceText = null;
				if(getConsideredAnnotationType().equals(BioTMLREModelTypes.entityclueonlyannotations.toString())){
					sentenceText = sentenceToExportForREOnlyAnnotations(doc.getID(), sentence, clue, annotations);
				}else{
					sentenceText = sentenceToExportForRE(doc.getID(), sentence, clue);
				}
				if(sentenceText != null && !sentenceText.getTokens().isEmpty()){
					BioTMLDocSentTokenIDs ids = new BioTMLDocSentTokenIDs(doc.getID(), sentID);
					executor.execute(new CorpusSentenceAndFeaturesToInstanceThread(ids, sentenceText, instances, configuration));
				}
			}
		}else if(getConsideredAnnotationType().equals(BioTMLREModelTypes.entityentiy.toString())||
				getConsideredAnnotationType().equals(BioTMLREModelTypes.entityentiyonlyannotations.toString())){
			List<IBioTMLAnnotation> annotations = getSentenceAnnotations(doc.getID(), sentence);
			for(IBioTMLAnnotation annotation :annotations){
				BioTMLTokensWithFeaturesAndLabels sentenceText = null;
				if(getConsideredAnnotationType().equals(BioTMLREModelTypes.entityentiyonlyannotations.toString())){
					sentenceText = sentenceToExportForREOnlyAnnotations(doc.getID(), sentence, annotation, annotations);
				}else{
					sentenceText = sentenceToExportForRE(doc.getID(), sentence, annotation);
				}
				if(sentenceText != null && !sentenceText.getTokens().isEmpty()){
					BioTMLDocSentTokenIDs ids = new BioTMLDocSentTokenIDs(doc.getID(), sentID);
					executor.execute(new CorpusSentenceAndFeaturesToInstanceThread(ids, sentenceText, instances, configuration));
				}
			}
		}
	}

	private void createFeaturesForNER(IBioTMLFeatureGeneratorConfigurator configuration, InstanceListExtended instances) throws BioTMLException {
		int docID=0;
		while(docID < getCorpusToConvert().getDocuments().size() && !stop ){
			IBioTMLDocument doc = getCorpusToConvert().getDocument(docID);
			int sentID=0;
			while(  sentID < doc.getSentences().size()  && !stop ){
				IBioTMLSentence sentence = doc.getSentence(sentID);
				BioTMLTokensWithFeaturesAndLabels tokensWithLabels = sentenceToExportForNER(doc.getID(), sentence);
				BioTMLDocSentTokenIDs ids = new BioTMLDocSentTokenIDs(doc.getID(), sentID);
				executor.execute(new CorpusSentenceAndFeaturesToInstanceThread(ids, tokensWithLabels, instances, configuration));
				sentID++;
			}
			docID++;
		}
	}

	private void finishThreadsFromExecutor() {
		executor.shutdown();
		try {
			executor.awaitTermination(Long.MAX_VALUE, TimeUnit.MINUTES);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		executor = null;
	}

	private void createFeaturesThreadExecutor(int threads) {
		final AtomicLong count = new AtomicLong(0);
		executor = Executors.newFixedThreadPool(threads,new ThreadFactory(){
			@Override
			public Thread newThread(Runnable runnable) {
				Thread thread = new Thread(runnable);
				thread.setName("BioTML-Features-Generation-Thread-" + count.getAndIncrement());
				thread.setDaemon(false);
				thread.setPriority(Thread.NORM_PRIORITY);
				return thread;
			};
		});
	}

	public void stopAllFeatureThreads(){
		stop = true;
		if(executor != null){
			executor.shutdown();
			try {
				if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
					executor.shutdownNow();
					if (!executor.awaitTermination(60, TimeUnit.SECONDS))
						System.err.println("BioTML Features pool did not terminate");
				}
			} catch (InterruptedException ie) {
				executor.shutdownNow();
			}
		}
		executor = null;
	}

	private BioTMLTokensWithFeaturesAndLabels sentenceToExportForNER(long docID, IBioTMLSentence sentence) throws BioTMLException{
		BioTMLTokensWithFeaturesAndLabels tokensWithLabels = new BioTMLTokensWithFeaturesAndLabels();
		for( IBioTMLToken token : sentence.getTokens()){
			if(getCorpusToConvert().getAnnotations()!= null){
				if(!getCorpusToConvert().getAnnotations().isEmpty()){
					tokensWithLabels.addTokenForNERModel(token.getToken().toString(), getTokenAnnotation(docID, token));
				}else{
					tokensWithLabels.addTokenForNER(token.getToken().toString());
				}
			}else{
				tokensWithLabels.addTokenForNER(token.getToken().toString());
			}
		}
		return tokensWithLabels;
	}

	private BioTMLTokensWithFeaturesAndLabels sentenceToExportForREOnlyAnnotations(long docID, IBioTMLSentence sentence, IBioTMLAnnotation annotation, List<IBioTMLAnnotation> annotations) throws BioTMLException{
		BioTMLTokensWithFeaturesAndLabels tokensWithLabels = new BioTMLTokensWithFeaturesAndLabels();
		for( IBioTMLToken token : sentence.getTokens()){
			//			List<Integer> listIndex =getAnnotationIndexInAnnotationList(annotation, annotations);
			List<Integer> listIndex = sentence.getTokenIndexsbyOffsets(annotation.getStartOffset(), annotation.getEndOffset());
			BioTMLConstants isTokeninAnnots = null;
			if(isTokenInAnnotations(annotations, token)){
				isTokeninAnnots = BioTMLConstants.isAnnotation;
			}else{
				isTokeninAnnots = BioTMLConstants.isNotAnnotation;
			}
			tokensWithLabels.addTokenForREModel(token.toString(), getTokenLabelRelation(docID, token, annotation), isTokeninAnnots,
					listIndex.get(0), listIndex.size()-1);
		}
		return tokensWithLabels;
	}

	private boolean isTokenInAnnotations(List<IBioTMLAnnotation> annotations, IBioTMLToken token){
		for(IBioTMLAnnotation annotation : annotations){
			if(annotation.getStartOffset()<=token.getStartOffset() && annotation.getEndOffset()>= token.getEndOffset()){
				return true;
			}
		}
		return false;
	}

	private BioTMLTokensWithFeaturesAndLabels sentenceToExportForRE(long docID, IBioTMLSentence sentence, IBioTMLAnnotation annotation) throws BioTMLException{
		BioTMLTokensWithFeaturesAndLabels tokensWithLabels = new BioTMLTokensWithFeaturesAndLabels();
		for(IBioTMLToken token : sentence.getTokens()){
			List<Integer> listIndex = sentence.getTokenIndexsbyOffsets(annotation.getStartOffset(), annotation.getEndOffset());
			tokensWithLabels.addTokenForREModel(token.toString(), getTokenLabelRelation(docID, token, annotation), listIndex.get(0), listIndex.get(listIndex.size()-1));
		}
		return tokensWithLabels;
	}

	private List<IBioTMLAnnotation> getClueAnnotations(long docID, IBioTMLSentence sentence) throws BioTMLException{
		List<IBioTMLAnnotation> clueAnnotations = new ArrayList<IBioTMLAnnotation>();
		List<IBioTMLAnnotation> docAnnotations = getCorpusToConvert().getDocAnnotations(docID);
		long startSent = sentence.getStartSentenceOffset();
		long endSent =  sentence.getEndSentenceOffset();;
		if( !docAnnotations.isEmpty()){
			Iterator<IBioTMLAnnotation> itAnnot = docAnnotations.iterator();
			while(itAnnot.hasNext()){
				IBioTMLAnnotation annotation = itAnnot.next();
				if(	annotation.getAnnotType().equals(BioTMLConstants.clue.toString()) 
						&& annotation.getStartOffset()>=startSent 
						&& annotation.getEndOffset()<=endSent ){
					clueAnnotations.add(annotation);
				}
			}
		}
		return clueAnnotations;
	}

	private List<IBioTMLAnnotation> getSentenceAnnotations(long docID, IBioTMLSentence sentence) throws BioTMLException{
		List<IBioTMLAnnotation> annotations = new ArrayList<IBioTMLAnnotation>();
		List<IBioTMLAnnotation> docAnnotations = getCorpusToConvert().getDocAnnotations(docID);
		long startSent = sentence.getStartSentenceOffset();
		long endSent =  sentence.getEndSentenceOffset();;
		if( !docAnnotations.isEmpty()){
			Iterator<IBioTMLAnnotation> itAnnot = docAnnotations.iterator();
			while(itAnnot.hasNext()){
				IBioTMLAnnotation annotation = itAnnot.next();
				if(	annotation.getStartOffset()>=startSent 
						&& annotation.getEndOffset()<=endSent ){
					annotations.add(annotation);
				}
			}
		}
		return annotations;
	}

	private BioTMLConstants getTokenAnnotation(long docID, IBioTMLToken token){
		List<IBioTMLAnnotation> docAnnotations = getCorpusToConvert().getDocAnnotations(docID);
		if( !docAnnotations.isEmpty()){
			Iterator<IBioTMLAnnotation> itAnnot = docAnnotations.iterator();
			while(itAnnot.hasNext()){
				IBioTMLAnnotation annotation = itAnnot.next();
				if(token.getEndOffset()<annotation.getStartOffset()){
					return BioTMLConstants.o;
				}
				if(annotation.getAnnotType().equals(getConsideredAnnotationType()) ){
					if(annotation.getStartOffset() == token.getStartOffset()){
						return BioTMLConstants.b;
					}
					if((annotation.getStartOffset() < token.getStartOffset()) &&
							(annotation.getEndOffset() >= token.getEndOffset())){
						return BioTMLConstants.i;
					}
				}
			}
		}
		return BioTMLConstants.o;
	}

	private BioTMLConstants getTokenLabelRelation(long docID, IBioTMLToken token, IBioTMLAnnotation annotation){
		Set<IBioTMLAnnotationsRelation> docRelations = getCorpusToConvert().getDocAnnotationRelations(docID);
		if( !docRelations.isEmpty()){
			Iterator<IBioTMLAnnotationsRelation> itRelation = docRelations.iterator();
			while(itRelation.hasNext()){
				IBioTMLAnnotationsRelation relation = itRelation.next();
				if(relation.findAnnotationInRelation(annotation)){
					try {
						IBioTMLAnnotation annot = relation.getAnnotationInRelationByOffsets(token.getStartOffset(), token.getEndOffset());
						if(!annot.getAnnotType().equals(BioTMLConstants.clue.toString())){
							if(annot.getStartOffset()==token.getStartOffset()){
								return BioTMLConstants.b;
							}else{
								return BioTMLConstants.i;
							}
						}
					} catch (BioTMLException e) {}
					//the token offsets are not present in the relation
				}
			}
		}
		return BioTMLConstants.o;
	}
}
