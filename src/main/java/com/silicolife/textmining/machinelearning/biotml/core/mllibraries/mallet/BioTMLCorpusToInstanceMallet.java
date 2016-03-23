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
import com.silicolife.textmining.machinelearning.biotml.core.corpora.BioTMLAnnotation;
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
import com.silicolife.textmining.machinelearning.biotml.core.mllibraries.mallet.multithread.MalletSentenceCorpusToInstanceThread;
import com.silicolife.textmining.machinelearning.biotml.core.models.BioTMLREModelTypes;

import cc.mallet.pipe.Pipe;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;

/**
 * 
 * A class responsible for corpus ({@link IBioTMLCorpus}) conversion into Mallet Instances.
 * 
 * @since 1.0.0
 * @version 1.0.1
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

	public InstanceList exportToMallet(Pipe p) throws BioTMLException{
		InstanceList instances = new InstanceList(p);
		int docID=0;
		while( docID<getCorpusToConvert().getDocuments().size() && !stop ){
			IBioTMLDocument doc = getCorpusToConvert().getDocument(docID);
			for( int sentID=0; sentID < doc.getSentences().size(); sentID++){
				IBioTMLSentence sentence = doc.getSentence(sentID);
				if(getIEAnnotationType().equals(BioTMLConstants.re.toString())){
					if(!getConsideredAnnotationType().equals(BioTMLREModelTypes.entityentiy.toString())){
						List<IBioTMLAnnotation> clues = getClueAnnotations(doc.getID(), sentence);
						for(IBioTMLAnnotation clue : clues){
							String text = sentenceToExportForRE(doc.getID(), sentence, clue);
							if(!text.isEmpty()){
								instances.addThruPipe(new Instance(text, null, "DocID:"+doc.getID()+"\tSentIdx:"+sentID, null ));
							}
						}
					}else{
						List<IBioTMLAnnotation> annotations = getSentenceAnnotations(doc.getID(), sentence);
						for(IBioTMLAnnotation annotation :annotations){
							String text = sentenceToExportForRE(doc.getID(), sentence, annotation);
							if(!text.isEmpty()){
								instances.addThruPipe(new Instance(text, null, "DocID:"+doc.getID()+"\tSentIdx:"+sentID, null ));
							}
						}
					}
				}else{
					String text = sentenceToExportForNER(doc.getID(), doc.getSentence(sentID));
					instances.addThruPipe(new Instance(text, null, "DocID:"+doc.getID()+"\tSentIdx:"+sentID, null ));
				}
			}
			docID++;
		}
		return instances;
	}

	public InstanceList exportToMallet(Pipe p, int threads) throws BioTMLException{
		InstanceListExtended instances = new InstanceListExtended(p);
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
		int docID=0;
		while(docID < getCorpusToConvert().getDocuments().size() && !stop){
			IBioTMLDocument doc = getCorpusToConvert().getDocument(docID);
			for( int sentID=0; sentID < doc.getSentences().size(); sentID++){
				IBioTMLSentence sentence = doc.getSentence(sentID);
				if(getIEAnnotationType().equals(BioTMLConstants.re.toString())){
					if(!getConsideredAnnotationType().equals(BioTMLREModelTypes.entityentiy.toString())){
						List<IBioTMLAnnotation> clues = getClueAnnotations(doc.getID(), sentence);
						List<IBioTMLAnnotation> annotations = getSentenceAnnotations(doc.getID(), sentence);
						//						Collections.sort(annotations);
						for(IBioTMLAnnotation clue : clues){
							//TODO model type clue(cluelemma). Get all clues that have that lemma and predict relations.
							//							List<IBioTMLAnnotation> tokenClues = divideAnnotationInSingleTokenAnnotations(clue, sentence);
							//							for(IBioTMLAnnotation subclue : tokenClues){
							//								String sentenceText = sentenceToExportForRE(doc.getID(), sentence, clue);//or subclue
							String sentenceText = sentenceToExportForREOnlyAnnotations(doc.getID(), sentence, clue, annotations);
							if(!sentenceText.isEmpty()){
								executor.execute(new MalletSentenceCorpusToInstanceThread("DocID:"+doc.getID()+"\tSentIdx:"+sentID, sentenceText, instances));
							}
							//							}
						}
					}else if(getConsideredAnnotationType().equals(BioTMLREModelTypes.entityentiy.toString())){
						List<IBioTMLAnnotation> annotations = getSentenceAnnotations(doc.getID(), sentence);
						//						Collections.sort(annotations);
						for(IBioTMLAnnotation annotation :annotations){
							//							List<IBioTMLAnnotation> subAnnots = divideAnnotationInSingleTokenAnnotations(annotation, sentence);
							//							for(IBioTMLAnnotation subAnnot: subAnnots){
							//								String sentenceText = sentenceToExportForRE(doc.getID(), sentence, annotation);
							String sentenceText = sentenceToExportForREOnlyAnnotations(doc.getID(), sentence, annotation, annotations);
							if(!sentenceText.isEmpty()){
								executor.execute(new MalletSentenceCorpusToInstanceThread("DocID:"+doc.getID()+"\tSentIdx:"+sentID, sentenceText, instances));
							}
							//							}
						}
					}
				}
				if(getIEAnnotationType().equals(BioTMLConstants.ner.toString())){
					String sentenceText = sentenceToExportForNER(doc.getID(), sentence);
					executor.execute(new MalletSentenceCorpusToInstanceThread("DocID:"+doc.getID()+"\tSentIdx:"+sentID, sentenceText, instances));
				}	
			}
			docID++;
		}
		executor.shutdown();
		try {
			executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		executor = null;
		return instances;
	}

	public InstanceList exportToMalletFeatures(Pipe p, int threads,  IBioTMLFeatureGeneratorConfigurator configuration) throws BioTMLException{
		InstanceListExtended instances = new InstanceListExtended(p);
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
		int docID=0;
		while(docID < getCorpusToConvert().getDocuments().size() && !stop ){
			IBioTMLDocument doc = getCorpusToConvert().getDocument(docID);
			int sentID=0;
			while(  sentID < doc.getSentences().size()  && !stop ){
				IBioTMLSentence sentence = doc.getSentence(sentID);
				if(getIEAnnotationType().equals(BioTMLConstants.re.toString())){
					if(!getConsideredAnnotationType().equals(BioTMLREModelTypes.entityentiy.toString())
							&& !getConsideredAnnotationType().equals(BioTMLREModelTypes.entityentiyonlyannotations.toString())){
						List<IBioTMLAnnotation> clues = getClueAnnotations(doc.getID(), sentence);
						List<IBioTMLAnnotation> annotations = getSentenceAnnotations(doc.getID(), sentence);
						for(IBioTMLAnnotation clue : clues){
							String sentenceText = new String();
							if(getConsideredAnnotationType().equals(BioTMLREModelTypes.allclasseswithcluesonlyannotations.toString())){
								sentenceText = sentenceToExportForREOnlyAnnotations(doc.getID(), sentence, clue, annotations);
							}else{
								sentenceText = sentenceToExportForRE(doc.getID(), sentence, clue);
							}
							if(!sentenceText.isEmpty()){
								executor.execute(new CorpusSentenceAndFeaturesToInstanceThread("DocID:"+doc.getID()+"\tSentIdx:"+sentID, sentenceText, instances, configuration));
							}
						}
					}else if(getConsideredAnnotationType().equals(BioTMLREModelTypes.entityentiy.toString())||
							getConsideredAnnotationType().equals(BioTMLREModelTypes.entityentiyonlyannotations.toString())){
						List<IBioTMLAnnotation> annotations = getSentenceAnnotations(doc.getID(), sentence);
						for(IBioTMLAnnotation annotation :annotations){
							String sentenceText = new String();
							if(getConsideredAnnotationType().equals(BioTMLREModelTypes.entityentiyonlyannotations.toString())){
								sentenceText = sentenceToExportForREOnlyAnnotations(doc.getID(), sentence, annotation, annotations);
							}else{
								sentenceText = sentenceToExportForRE(doc.getID(), sentence, annotation);
							}
							if(!sentenceText.isEmpty()){
								executor.execute(new CorpusSentenceAndFeaturesToInstanceThread("DocID:"+doc.getID()+"\tSentIdx:"+sentID, sentenceText, instances, configuration));
							}
						}
					}
				}
				if(getIEAnnotationType().equals(BioTMLConstants.ner.toString())){
					String sentenceText = sentenceToExportForNER(doc.getID(), sentence);
					executor.execute(new CorpusSentenceAndFeaturesToInstanceThread("DocID:"+doc.getID()+"\tSentIdx:"+sentID, sentenceText, instances, configuration));
				}	
				sentID++;
			}
			docID++;
		}
		executor.shutdown();
		try {
			executor.awaitTermination(Long.MAX_VALUE, TimeUnit.MINUTES);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		executor = null;
		return instances;
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

	private String sentenceToExportForNER(long docID, IBioTMLSentence sentence) throws BioTMLException{
		StringBuilder str = new StringBuilder();
		for( IBioTMLToken token : sentence.getTokens()){
			str.append(token.toString());
			if(getCorpusToConvert().getAnnotations()!= null){
				if(!getCorpusToConvert().getAnnotations().isEmpty()){
					str.append("\t");
					str.append(getTokenAnnotation(docID, token));
				}
			}
			str.append("\n");
		}
		return str.toString();
	}

	@SuppressWarnings("unused")
	private List<IBioTMLAnnotation> divideAnnotationInSingleTokenAnnotations(IBioTMLAnnotation annotation, IBioTMLSentence sentence){
		List<IBioTMLAnnotation> subAnnots = new ArrayList<>();

		try { 
			List<IBioTMLToken> tokens = sentence.getTokensbyOffsets(annotation.getStartOffset(), annotation.getEndOffset());
			for(IBioTMLToken token : tokens){
				subAnnots.add(new BioTMLAnnotation(annotation.getDocID(), annotation.getAnnotType(), token.getStartOffset(), token.getEndOffset()));
			}
		} catch (BioTMLException e) {}
		return subAnnots;
	}

	private String sentenceToExportForREOnlyAnnotations(long docID, IBioTMLSentence sentence, IBioTMLAnnotation annotation, List<IBioTMLAnnotation> annotations) throws BioTMLException{
		StringBuilder str = new StringBuilder();
		for( IBioTMLToken token : sentence.getTokens()){

			str.append(token.toString());
			str.append("\t");
			List<Integer> listIndex =getAnnotationIndexInAnnotationList(annotation, annotations);
			if(!listIndex.isEmpty()){
				str.append("("+String.valueOf(listIndex.get(0)) +" | "+listIndex.get(listIndex.size()-1) + ")");
			}else{
				throw new BioTMLException(30);
			}
			if(isTokenInAnnotations(annotations, token)){
				str.append("\t");
				str.append("INANNOT");
			}else{
				str.append("\t");
				str.append("OUTANNOT");
			}
			if(getCorpusToConvert().getRelations()!=null){
				if(!getCorpusToConvert().getRelations().isEmpty()){
					str.append("\t");
					str.append(getTokenRelation(docID, token, annotation));
				}
			}
			str.append("\n");
		}
		return str.toString();
	}

	private List<Integer> getAnnotationIndexInAnnotationList(IBioTMLAnnotation annotation, List<IBioTMLAnnotation> annotations){
		List<Integer> result = new ArrayList<>();
		int i = 0;
		for(IBioTMLAnnotation annot : annotations){
			if(annot.equals(annotation)){
				result.add(i);
				result.add(i);
				return result;
			}
		}
		return result;
	}

	private boolean isTokenInAnnotations(List<IBioTMLAnnotation> annotations, IBioTMLToken token){
		for(IBioTMLAnnotation annotation : annotations){
			if(annotation.getStartOffset()<=token.getStartOffset() && annotation.getEndOffset()>= token.getEndOffset()){
				return true;
			}
		}
		return false;
	}

	private String sentenceToExportForRE(long docID, IBioTMLSentence sentence, IBioTMLAnnotation annotation) throws BioTMLException{
		StringBuilder str = new StringBuilder();
		for( IBioTMLToken token : sentence.getTokens()){
			str.append(token.toString());
			str.append("\t");
			List<Integer> listIndex = sentence.getTokenIndexsbyOffsets(annotation.getStartOffset(), annotation.getEndOffset());
			if(!listIndex.isEmpty()){
				str.append("("+String.valueOf(listIndex.get(0)) +" | "+listIndex.get(listIndex.size()-1) + ")");
			}else{
				throw new BioTMLException(30);
			}
			if(getCorpusToConvert().getRelations()!=null){
				if(!getCorpusToConvert().getRelations().isEmpty()){
					str.append("\t");
					str.append(getTokenRelation(docID, token, annotation));
				}
			}
			str.append("\n");
		}
		return str.toString();
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

	private String getTokenAnnotation(long docID, IBioTMLToken token){
		List<IBioTMLAnnotation> docAnnotations = getCorpusToConvert().getDocAnnotations(docID);
		if( !docAnnotations.isEmpty()){
			Iterator<IBioTMLAnnotation> itAnnot = docAnnotations.iterator();
			while(itAnnot.hasNext()){
				IBioTMLAnnotation annotation = itAnnot.next();
				if(token.getEndOffset()<annotation.getStartOffset()){
					return BioTMLConstants.o.toString();
				}
				if(annotation.getAnnotType().equals(getConsideredAnnotationType()) ){
					if(annotation.getStartOffset() == token.getStartOffset()){
						return BioTMLConstants.b.toString();
					}
					if((annotation.getStartOffset() < token.getStartOffset()) &&
							(annotation.getEndOffset() >= token.getEndOffset())){
						return BioTMLConstants.i.toString();
					}
				}
			}
		}
		return BioTMLConstants.o.toString();
	}

	private String getTokenRelation(long docID, IBioTMLToken token, IBioTMLAnnotation annotation){
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
								return BioTMLConstants.b.toString();
							}else{
								return BioTMLConstants.i.toString();
							}
						}
					} catch (BioTMLException e) {}
					//the token offsets are not present in the relation
				}
			}
		}
		return BioTMLConstants.o.toString();
	}
}
