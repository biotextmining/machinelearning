package com.silicolife.textmining.machinelearning.biotml.core.annotator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.silicolife.textmining.machinelearning.biotml.core.BioTMLConstants;
import com.silicolife.textmining.machinelearning.biotml.core.corpora.BioTMLAnnotation;
import com.silicolife.textmining.machinelearning.biotml.core.corpora.BioTMLAnnotationsRelation;
import com.silicolife.textmining.machinelearning.biotml.core.corpora.BioTMLCorpus;
import com.silicolife.textmining.machinelearning.biotml.core.corpora.BioTMLDocSentTokenIDs;
import com.silicolife.textmining.machinelearning.biotml.core.exception.BioTMLException;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLAnnotation;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLAnnotationsRelation;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLAnnotator;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLCorpus;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLDocument;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLModel;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLToken;
import com.silicolife.textmining.machinelearning.biotml.core.mllibraries.BioTMLAlgorithms;
import com.silicolife.textmining.machinelearning.biotml.core.mllibraries.mallet.BioTMLCorpusToInstanceMallet;
import com.silicolife.textmining.machinelearning.biotml.core.models.BioTMLREModelTypes;

import cc.mallet.classify.Classifier;
import cc.mallet.fst.NoopTransducerTrainer;
import cc.mallet.fst.SumLatticeDefault;
import cc.mallet.fst.Transducer;
import cc.mallet.pipe.Pipe;
import cc.mallet.types.Alphabet;
import cc.mallet.types.AugmentableFeatureVector;
import cc.mallet.types.FeatureVector;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import cc.mallet.types.Labeling;
import cc.mallet.types.Sequence;
import cc.mallet.types.Token;

/**
 * 
 * Represents the mallet annotator to apply a mallet model and annotate a corpus.
 * 
 * @since 1.0.0
 * @version 1.0.1
 * @author Ruben Rodrigues ({@code rrodrigues@silicolife.com})
 */

public class BioTMLMalletAnnotator implements IBioTMLAnnotator{

	private IBioTMLCorpus corpus;
	private boolean stop = false;
	private BioTMLCorpusToInstanceMallet malletCorpus;

	/**
	 * 
	 * Initializes the annotator with a unannotated corpus.
	 * 
	 * @param corpus - Unannotated {@link IBioTMLCorpus}.
	 */
	public BioTMLMalletAnnotator(IBioTMLCorpus corpus){
		this.corpus = corpus;
	}

	public IBioTMLCorpus generateAnnotatedBioTMCorpus(IBioTMLModel model, int threads) throws BioTMLException{
		if(!validateModel(model))
		{
			throw new BioTMLException(5);
		}
		List<IBioTMLAnnotation> annotations = new ArrayList<IBioTMLAnnotation>();
		if(model.getModelConfiguration().getIEType().equals(BioTMLConstants.ner.toString())){
			if(model.getModelConfiguration().getAlgorithmType().equals(BioTMLAlgorithms.malletcrf.toString())&&!stop
					|| model.getModelConfiguration().getAlgorithmType().equals(BioTMLAlgorithms.mallethmm)&&!stop){
				annotations = processAnnotationsUsingTranducerMallet(model, threads);
			}
			if(model.getModelConfiguration().getAlgorithmType().equals(BioTMLAlgorithms.malletsvm.toString())&&!stop){
				annotations = processAnnotationsUsingClassifierMallet(model, threads);
			}
			return new BioTMLCorpus(getBasedBioTMCorpus().getDocuments(), annotations, getBasedBioTMCorpus().toString());
		}
		if(model.getModelConfiguration().getIEType().equals(BioTMLConstants.re.toString())){
			List<IBioTMLAnnotationsRelation> relations = new ArrayList<IBioTMLAnnotationsRelation>();
			if(model.getModelConfiguration().getAlgorithmType().equals(BioTMLAlgorithms.malletcrf.toString())&&!stop
					|| model.getModelConfiguration().getAlgorithmType().equals(BioTMLAlgorithms.mallethmm.toString())&&!stop){
				relations = processAnnotationRelationsUsingTranducerMallet(model, threads);
			}
			if(model.getModelConfiguration().getAlgorithmType().equals(BioTMLAlgorithms.malletsvm.toString())&&!stop){
				relations = processAnnotationRelationsUsingClassifierMallet(model, threads);
			}
			Set<IBioTMLAnnotation> annotationsAndgenFromRelations = new HashSet<IBioTMLAnnotation>(getBasedBioTMCorpus().getAnnotations());
			annotationsAndgenFromRelations.addAll(getBasedBioTMCorpus().getAnnotationsFromRelations(relations));
			annotations.addAll(annotationsAndgenFromRelations);
			return new BioTMLCorpus(getBasedBioTMCorpus().getDocuments(), annotations, relations, getBasedBioTMCorpus().toString());
		}
		throw new BioTMLException(5);
	}

	public IBioTMLCorpus generateAnnotatedBioTMCorpus(List<IBioTMLModel> models, int threads) throws BioTMLException{
		List<IBioTMLAnnotation> annotations = new ArrayList<IBioTMLAnnotation>();
		List<IBioTMLAnnotationsRelation> relations = new ArrayList<IBioTMLAnnotationsRelation>();
		Iterator<IBioTMLModel> itModels = models.iterator();
		while(itModels.hasNext() && !stop){
			IBioTMLModel model = itModels.next();
			if(!validateModel(model))
			{
				throw new BioTMLException(5);
			}
			if(model.getModelConfiguration().getIEType().equals(BioTMLConstants.ner.toString()) && !stop){
				if(model.getModelConfiguration().getAlgorithmType().equals(BioTMLAlgorithms.malletcrf.toString()) && !stop
						|| model.getModelConfiguration().getAlgorithmType().equals(BioTMLAlgorithms.mallethmm.toString()) && !stop){
					annotations.addAll(processAnnotationsUsingTranducerMallet(model, threads));
				}
				if(model.getModelConfiguration().getAlgorithmType().equals(BioTMLAlgorithms.malletsvm.toString()) && !stop){
					annotations.addAll(processAnnotationsUsingClassifierMallet(model, threads));
				}
			}
			if(model.getModelConfiguration().getIEType().equals(BioTMLConstants.re.toString()) && !stop){
				if(model.getModelConfiguration().getAlgorithmType().equals(BioTMLAlgorithms.malletcrf.toString()) && !stop
						|| model.getModelConfiguration().getAlgorithmType().equals(BioTMLAlgorithms.mallethmm.toString()) && !stop){
					relations.addAll(processAnnotationRelationsUsingTranducerMallet(model, threads));
				}
				if(model.getModelConfiguration().getAlgorithmType().equals(BioTMLAlgorithms.malletsvm.toString()) && !stop){
					relations.addAll(processAnnotationRelationsUsingClassifierMallet(model, threads));
				}
			}

		}
		if(!relations.isEmpty()){
			Set<IBioTMLAnnotation> annotationsAndgenFromRelations = new HashSet<IBioTMLAnnotation>(getBasedBioTMCorpus().getAnnotations());
			annotationsAndgenFromRelations.addAll(getBasedBioTMCorpus().getAnnotationsFromRelations(relations));
			annotations.addAll(annotationsAndgenFromRelations);
			return new BioTMLCorpus(getBasedBioTMCorpus().getDocuments(), annotations, relations,  getBasedBioTMCorpus().toString());
		}else{
			if(!getBasedBioTMCorpus().getAnnotations().isEmpty()){
				annotations.addAll(getBasedBioTMCorpus().getAnnotations());
			}
			return new BioTMLCorpus(getBasedBioTMCorpus().getDocuments(), annotations, getBasedBioTMCorpus().toString());
		}
	}

	public IBioTMLCorpus getBasedBioTMCorpus() {
		return corpus;
	}

	public boolean validateModel(IBioTMLModel model) {
		if (model.getModel() instanceof Transducer){
			if(	model.getModelConfiguration().getAlgorithmType().equals(BioTMLAlgorithms.malletcrf.toString())
					|| model.getModelConfiguration().getAlgorithmType().equals(BioTMLAlgorithms.mallethmm.toString())){
				return true;
			}
		}
		if (model.getModel() instanceof Classifier && model.getModelConfiguration().getAlgorithmType().equals(BioTMLAlgorithms.malletsvm.toString())){
			return true;
		}
		return false;
	}
	

	public void stopAnnotator() {
		stop = true;
		if(malletCorpus!= null){
			malletCorpus.stopAllFeatureThreads();
		}
		Alphabet.cleanAllAphabetsFromMemory();
		Pipe.cleanAllPipesFromMemory();
	}

	private List<IBioTMLAnnotation> processAnnotationsUsingClassifierMallet(IBioTMLModel model, int threads) throws BioTMLException{
		List<IBioTMLAnnotation> annotations = new ArrayList<IBioTMLAnnotation>();
		malletCorpus = new BioTMLCorpusToInstanceMallet(getBasedBioTMCorpus(), model.getModelConfiguration().getClassType(), model.getModelConfiguration().getIEType());
		Classifier modelClassifier = (Classifier) model.getModel();
		Pipe classificationPipe = modelClassifier.getInstancePipe();
		InstanceList dataToAnnotate = malletCorpus.exportToMalletFeatures(classificationPipe, threads, model.getFeatureConfiguration());
		
		Iterator<Instance> itToken = dataToAnnotate.iterator();
		while(itToken.hasNext() && !stop){
			Instance token = itToken.next();
			Labeling labelRanks = modelClassifier.classify(token).getLabeling();
			BioTMLDocSentTokenIDs ids = (BioTMLDocSentTokenIDs)token.getName();
			IBioTMLDocument doc = getBasedBioTMCorpus().getDocumentByID(ids.getDocId());
			annotations = addPredictedAnnotation(annotations, doc, ids.getSentId(), ids.getTokenId(), model.getModelConfiguration().getClassType(), labelRanks.getLabelAtRank(0).toString(), labelRanks.getValueAtRank(0));
		}
		
//		cleanMalletAlphabets(dataToAnnotate);
//		clearMalletPipe(classificationPipe);
		return annotations;
	};

	@SuppressWarnings("rawtypes")
	private List<IBioTMLAnnotation> processAnnotationsUsingTranducerMallet(IBioTMLModel model, int threads) throws BioTMLException{
		List<IBioTMLAnnotation> annotations = new ArrayList<IBioTMLAnnotation>();
		malletCorpus = new BioTMLCorpusToInstanceMallet(getBasedBioTMCorpus(), model.getModelConfiguration().getClassType(), model.getModelConfiguration().getIEType());
		Transducer modelTransducer = (Transducer) model.getModel();
		//InstanceList dataToAnnotate = malletCorpus.exportToMallet(modelCRF.getInputPipe());
		Pipe transducingPipe = modelTransducer.getInputPipe();
		InstanceList dataToAnnotate = malletCorpus.exportToMalletFeatures(transducingPipe, threads, model.getFeatureConfiguration());
		NoopTransducerTrainer transducerModelTrained = new NoopTransducerTrainer(modelTransducer);

		Iterator<Instance> itSentence = dataToAnnotate.iterator();
		while(itSentence.hasNext() && !stop){
			Instance sentence = itSentence.next();
			Transducer modelPredictor = transducerModelTrained.getTransducer();
			Sequence input = (Sequence) sentence.getData();
			Sequence predictedSeq = modelPredictor.transduce(input);
			BioTMLDocSentTokenIDs ids = (BioTMLDocSentTokenIDs)sentence.getName();
			IBioTMLDocument doc = getBasedBioTMCorpus().getDocumentByID(ids.getDocId());
			double predictionScore = getTransducerPredictionScore(modelPredictor,  input,  predictedSeq);
			for(int tokenIndex=0; tokenIndex<predictedSeq.size(); tokenIndex++){
				annotations = addPredictedAnnotation(annotations, doc, ids.getSentId(), tokenIndex, model.getModelConfiguration().getClassType(), predictedSeq.get(tokenIndex).toString(), predictionScore);
			}
		}
		
//		cleanMalletAlphabets(dataToAnnotate);
//		clearMalletPipe(transducingPipe);
		return annotations;
	}

	@SuppressWarnings("rawtypes")
	private List<IBioTMLAnnotationsRelation> processAnnotationRelationsUsingTranducerMallet(IBioTMLModel model, int threads) throws BioTMLException{
		Set<IBioTMLAnnotationsRelation> relations = new LinkedHashSet<IBioTMLAnnotationsRelation>();
		malletCorpus = new BioTMLCorpusToInstanceMallet(getBasedBioTMCorpus(), model.getModelConfiguration().getClassType(), model.getModelConfiguration().getIEType());
		Transducer modelTransducer = (Transducer) model.getModel();
		Pipe transducingPipe = modelTransducer.getInputPipe();
		InstanceList dataToAnnotate = malletCorpus.exportToMalletFeatures(transducingPipe, threads, model.getFeatureConfiguration());
		NoopTransducerTrainer transducerModelTrained = new NoopTransducerTrainer(modelTransducer);

		Iterator<Instance> itSentence = dataToAnnotate.iterator();
		while(itSentence.hasNext() && !stop){
			Instance sentence = itSentence.next();
			Transducer modelPredictor = transducerModelTrained.getTransducer();
			Sequence input = (Sequence) sentence.getData();
			Sequence predictedSeq = modelPredictor.transduce(input);
			BioTMLDocSentTokenIDs ids = (BioTMLDocSentTokenIDs)sentence.getName();
			IBioTMLDocument doc = getBasedBioTMCorpus().getDocumentByID(ids.getDocId());
			double predictionScore = getTransducerPredictionScore(modelPredictor,  input,  predictedSeq);
			for(int tokenIndex=0; tokenIndex<predictedSeq.size(); tokenIndex++){
				String prediction = predictedSeq.get(tokenIndex).toString();
				if(!prediction.equals(BioTMLConstants.o.toString())){
					IBioTMLAnnotation annotation = getAnnotation(doc, ids.getSentId(),ids.getAnnotTokenStartIndex(), ids.getAnnotTokenEndIndex());
					if(annotation != null){
//						if((annotation.getAnnotType().equals(BioTMLConstants.clue.toString()) 
//								&& model.getModelConfiguration().getClassType().equals(BioTMLREModelTypes.allclasseswithclues.toString()))
//								|| (!annotation.getAnnotType().equals(BioTMLConstants.clue.toString()) 
//										&& model.getModelConfiguration().getClassType().equals(BioTMLREModelTypes.entityentiy.toString()))
//								|| (!annotation.getAnnotType().equals(BioTMLConstants.clue.toString()) 
//										&& model.getModelConfiguration().getClassType().equals(BioTMLREModelTypes.entityentiyonlyannotations.toString()))){
							relations = addPredictedRelation(relations, doc, ids.getSentId(), tokenIndex, annotation, model.getModelConfiguration().getClassType(), prediction, predictionScore);
//						}
					}
				}
			}
		}

//		cleanMalletAlphabets(dataToAnnotate);
//		clearMalletPipe(transducingPipe);
		return new ArrayList<IBioTMLAnnotationsRelation>(relations);
	}

	private List<IBioTMLAnnotationsRelation> processAnnotationRelationsUsingClassifierMallet(IBioTMLModel model, int threads) throws BioTMLException{
		Set<IBioTMLAnnotationsRelation> relations = new HashSet<IBioTMLAnnotationsRelation>();
		malletCorpus = new BioTMLCorpusToInstanceMallet(getBasedBioTMCorpus(), model.getModelConfiguration().getClassType(), model.getModelConfiguration().getIEType());
		Classifier modelClassifier = (Classifier) model.getModel();
		Pipe classificationPipe = modelClassifier.getInstancePipe();
		InstanceList dataToAnnotate = malletCorpus.exportToMalletFeatures(classificationPipe, threads, model.getFeatureConfiguration());
		
		Iterator<Instance> itToken = dataToAnnotate.iterator();
		while(itToken.hasNext() && !stop){
			Instance token = itToken.next();
			Labeling labelRanks = modelClassifier.classify(token).getLabeling();
			BioTMLDocSentTokenIDs ids = (BioTMLDocSentTokenIDs)token.getName();
			IBioTMLDocument doc = getBasedBioTMCorpus().getDocumentByID(ids.getDocId());
			IBioTMLAnnotation annotation = null;
			if(token.getData() instanceof Token){
				Token tokenandAnnotationInstance = (Token) token.getData();
				int startIndex = (int) tokenandAnnotationInstance.getProperty("startAnnotIndex");
				int endIndex = (int) tokenandAnnotationInstance.getProperty("endAnnotIndex");
				annotation = getAnnotation(doc, ids.getSentId(), startIndex, endIndex);
			}else if(token.getData() instanceof AugmentableFeatureVector || token.getData() instanceof FeatureVector){
				BioTMLDocSentTokenIDs tokensource = (BioTMLDocSentTokenIDs)token.getName();
				annotation = getAnnotation(doc,  ids.getSentId(), tokensource.getAnnotTokenStartIndex(), tokensource.getAnnotTokenEndIndex());
			}

			if(annotation != null){
				if((annotation.getAnnotType().equals(BioTMLConstants.clue.toString()) 
						&& model.getModelConfiguration().getClassType().equals(BioTMLREModelTypes.allclasseswithclues.toString()))
						|| (!annotation.getAnnotType().equals(BioTMLConstants.clue.toString()) 
								&& model.getModelConfiguration().getClassType().equals(BioTMLREModelTypes.entityentiy.toString()))){
					relations = addPredictedRelation(relations, doc, ids.getSentId(), ids.getTokenId(), annotation, model.getModelConfiguration().getClassType(), labelRanks.getLabelAtRank(0).toString(), labelRanks.getValueAtRank(0));
				}
			}
		}
		
//		cleanMalletAlphabets(dataToAnnotate);
//		clearMalletPipe(classificationPipe);
		return new ArrayList<IBioTMLAnnotationsRelation>(relations);
	}
	
	@SuppressWarnings("unused")
	private void cleanMalletAlphabets(InstanceList dataToAnnotate){
		if(dataToAnnotate!=null){
			for(Alphabet alphabet : dataToAnnotate.getAlphabets()){
				if(alphabet!=null){
					alphabet.cleanAlphabetFromMemory();
				}
//				method added to serialpipes
//				@Override
//				public void cleanPipeFromMemory(){
//					super.cleanPipeFromMemory();
//					for(Pipe pipe : pipes){
//						pipe.cleanPipeFromMemory();
//					}
//					pipes.clear();
//					if(dataAlphabet != null){
//						dataAlphabet.cleanAlphabetFromMemory();
//					}
//					if(targetAlphabet != null){
//						targetAlphabet.cleanAlphabetFromMemory();
//					}
//				}
//				method added to Alphabet class to clean the memory in mallet. 
//				public void cleanAlphabetFromMemory(){
//					if(instanceId != null){
//						Object obj = deserializedEntries.remove(this.instanceId);
////						System.out.println(" *** Alphabet Removed from memory! instance id= " + instanceId);
////						System.out.println("Alphabet entries in memory: " + String.valueOf(deserializedEntries.size()));
//						if(obj != null){
//							Alphabet alphabet = (Alphabet) obj;
//							alphabet.map.clear();
//							alphabet.entries.clear();
//							alphabet.map = null;
//							alphabet.entries = null;
//							alphabet.entryClass = null;
//							alphabet.instanceId = null;
//						}
//					}
//					System.gc();
//				}
//
//				public static void cleanAllAphabetsFromMemory(){
////					System.out.println("Alphabet entries in memory before clear: " + String.valueOf(deserializedEntries.size()));
//					deserializedEntries.clear();
////					System.out.println("Alphabet entries in memory after clear: " + String.valueOf(deserializedEntries.size()));
//				}
//				method added to pipe
//				
//				public void cleanPipeFromMemory(){
//					if(this.instanceId != null){
//						Object obj = deserializedEntries.remove(this.instanceId);
////						System.out.println(" *** Pipe Removed from memory! instance id= " + instanceId);
////						System.out.println("Pipe entries in memory: " + String.valueOf(deserializedEntries.size()));
//						if(obj != null){
//							Pipe pipe = (Pipe) obj;
//							if(pipe.dataAlphabet != null){
//								pipe.dataAlphabet.cleanAlphabetFromMemory();
//								pipe.dataAlphabet = null;
//							}
//							if(pipe.targetAlphabet != null){
//								pipe.targetAlphabet.cleanAlphabetFromMemory();
//								pipe.targetAlphabet = null;
//							}
//							pipe.instanceId = null;
//						}
//					}
//					System.gc();
//				}
//				
//				public static void cleanAllPipesFromMemory(){
//					deserializedEntries.clear();
////					System.out.println("Pipe entries in memory: " + String.valueOf(deserializedEntries.size()));
//				}
//				And added to LabelAplhabet
//				public void cleanAlphabetFromMemory(){
//					super.cleanAlphabetFromMemory();
//					this.labels.clear();
//					this.labels = new ArrayList ();
//				}
//				method added to CRF in factors
//				public void cleanFactorsFromMemory(){
//					this.defaultWeights = null;
//					this.finalWeights = null;
//					this.initialWeights = null;
//					if(this.weightAlphabet != null){
//						this.weightAlphabet.cleanAlphabetFromMemory();
//					}
//					this.weightAlphabet = null;
//					this.weights = null;
//					this.weightsFrozen = null;
//				}
//				the mallet methods shutdown in thread optimizer contains a max number to wait for shutdown. 
			}
		}
	}
	
//	private void clearMalletPipe(Pipe pipe){
//		if(pipe != null){
//			pipe.cleanPipeFromMemory();
//			method added to Pipe class to clean the memory in mallet. 
//			public void cleanPipeFromMemory(){
//				if(this.instanceId != null){
////					System.out.println(" *** Alphabet Removed from memory! instance id= " + instanceId);
////					System.out.println("Alphabet entries in memory: " + String.valueOf(deserializedEntries.size()));
//					Object obj = deserializedEntries.remove(this.instanceId);
//					if(obj != null){
//						Pipe pipe = (Pipe) obj;
//						pipe.dataAlphabet = null;
//						pipe.targetAlphabet = null;
//						pipe.instanceId = null;
//					}
//				}
//				System.gc();
//			}
//			
//			public static void cleanAllPipesFromMemory(){
//				deserializedEntries.clear();
////				System.out.println("Alphabet entries in memory: " + String.valueOf(deserializedEntries.size()));
//			}
//		}
//	}

//	/**
//	 * 
//	 * The doc ID of the sentence is retrieved using the sentence mallet name.
//	 * 
//	 * @param sentenceName - Sentence name.
//	 * @return Document ID.
//	 */
//	private long getDocIDFromSentenceName(String sentenceName){
//		long docID = -1;
//		String[] docandsentencesplit = sentenceName.split("\t");
//		if(docandsentencesplit[0].startsWith("DocID:")){
//			docID = Long.valueOf(docandsentencesplit[0].substring(6));
//		}
//		return docID;
//	}

//	/**
//	 * 
//	 * The sentence index is retrieved using the sentence mallet name.
//	 * 
//	 * @param sentenceName - Sentence name.
//	 * @return Sentence index.
//	 */
//	private int getSentIdxFromSentenceName(String sentenceName){
//		int sentIndex = -1;
//		String[] docandsentencesplit = sentenceName.split("\t");
//		if(docandsentencesplit[1].startsWith("SentIdx:")){
//			sentIndex = Integer.valueOf(docandsentencesplit[1].substring(8));
//		}
//		return sentIndex;
//	}
//
//	/**
//	 * 
//	 * The token index in sentence is retrieved using the sentence mallet name.
//	 * 
//	 * @param sentenceName - Sentence name.
//	 * @return Token index.
//	 */
//	private int getTokenIdxFromSentenceName(String sentenceName){
//		int tokenIndex = -1;
//		String[] docandSentIdxAndTokIdxsplit = sentenceName.split("\t");
//		if(docandSentIdxAndTokIdxsplit[2].startsWith("tokensequence:")){
//			tokenIndex = Integer.valueOf(docandSentIdxAndTokIdxsplit[2].substring(14));
//		}
//		return tokenIndex;
//	}

	/**
	 * 
	 * Calculates the prediction score for mallet transducer prediction.
	 * 
	 * @param modelPredictor - Mallet transducer.
	 * @param input - Features of the sentence to be predicted.
	 * @param predictedSeq - Predicted label sentence.
	 * @return Score associated with the sentence prediction.
	 */
	@SuppressWarnings("rawtypes")
	private double getTransducerPredictionScore(Transducer modelPredictor, Sequence input, Sequence predictedSeq){
		double logScore = new SumLatticeDefault(modelPredictor, input, predictedSeq).getTotalWeight();
		double logZ = new SumLatticeDefault(modelPredictor, input).getTotalWeight();
		return Math.exp(logScore - logZ);
	}

	private List<IBioTMLAnnotation> addPredictedAnnotation(
			List<IBioTMLAnnotation> annotations, 
			IBioTMLDocument doc,
			int sentIndex,
			int tokenIndex,			
			String tokenClass,
			String prediction,
			double predictionScore){

		IBioTMLToken token = doc.getSentence(sentIndex).getToken(tokenIndex);
		if(prediction.equals(BioTMLConstants.b.toString())){
			IBioTMLAnnotation annot = new BioTMLAnnotation(doc.getID(), tokenClass, token.getStartOffset(), token.getEndOffset(), predictionScore);
			annotations.add(annot);
		}
		if(prediction.equals(BioTMLConstants.i.toString())){
			if(annotations.size()>0){
				IBioTMLAnnotation annotiationBegin = annotations.get(annotations.size()-1);
				if(validatePrevToken(annotiationBegin, token, doc.getID())){
					IBioTMLAnnotation newannot = new BioTMLAnnotation(doc.getID(), tokenClass, annotiationBegin.getStartOffset(), token.getEndOffset(), predictionScore);
					annotations.set(annotations.size()-1, newannot);
				}else{
					IBioTMLAnnotation annot = new BioTMLAnnotation(doc.getID(), tokenClass, token.getStartOffset(), token.getEndOffset(), predictionScore);
					annotations.add(annot);
				}
			}
			else{
				IBioTMLAnnotation annot = new BioTMLAnnotation(doc.getID(), tokenClass, token.getStartOffset(), token.getEndOffset(), predictionScore);
				annotations.add(annot);
			}
		}
		return annotations;
	}

	/**
	 * 
	 * Method to retrieve the annotation in one sentence.
	 * 
	 * @param doc - Document {@link IBioTMLDocument}.
	 * @param sentIndex - Sentence index.
	 * @param annotationIndex - Pair of integers that indicates the index of the first and last token offsets.
	 * @return Annotation {@link IBioTMLAnnotation}.
	 */
	private IBioTMLAnnotation getAnnotation(IBioTMLDocument doc, int sentIndex, int startTokenIndex, int endTokenIndex){
		IBioTMLToken firstTokenAnnotation = doc.getSentence(sentIndex).getToken(startTokenIndex);
		IBioTMLToken lastTokenAnnotation = doc.getSentence(sentIndex).getToken(endTokenIndex);
		IBioTMLAnnotation annotation = null;
		try {
			annotation = getBasedBioTMCorpus().getAnnotationFromDocAndOffsets(doc.getID(),firstTokenAnnotation.getStartOffset(), lastTokenAnnotation.getEndOffset());
		} catch (BioTMLException e) {}
		return annotation;
	}

	/**
	 * 
	 * Method to add a predicted relation regarding the prediction value of the model.
	 * 
	 * @param relations - Set of all relations ({@link IBioTMLAnnotationsRelation}) predicted to add the relation.
	 * @param doc - Document in which the relation is present.
	 * @param sentIndex - Sentence index.
	 * @param tokenIndex - Token index.
	 * @param firstAnnotation - Annotation of the relation to be associated.
	 * @param tokenClass - Token class type.
	 * @param prediction - Prediction value (e.g. B, I or O).
	 * @param predictionScore - Prediction score.
	 * @return Set of all relations ({@link IBioTMLAnnotationsRelation}) predicted.
	 * @throws {@link BioTMLException}.
	 */
	private Set<IBioTMLAnnotationsRelation> addPredictedRelation(
			Set<IBioTMLAnnotationsRelation> relations, 
			IBioTMLDocument doc,
			int sentIndex,
			int tokenIndex,
			IBioTMLAnnotation firstAnnotation,
			String tokenClass,
			String prediction,
			double predictionScore) throws BioTMLException{

		IBioTMLToken token = doc.getSentence(sentIndex).getToken(tokenIndex);
		IBioTMLAnnotation annotation = null;
		try {
			annotation = getBasedBioTMCorpus().getAnnotationFromDocAndOffsets(doc.getID(), token.getStartOffset(), token.getEndOffset());
		} catch (BioTMLException e) {}
		if((tokenClass.equals(BioTMLREModelTypes.allclasseswithclues.toString()) || tokenClass.equals(BioTMLREModelTypes.entityentiy.toString())) && annotation == null){
			return relations;
		}
		if(prediction.equals(BioTMLConstants.b.toString())){
			if(annotation == null){
				annotation = new BioTMLAnnotation(doc.getID(), tokenClass, token.getStartOffset(), token.getEndOffset(), predictionScore);
			}
			relations = addRelation(relations, annotation, firstAnnotation, predictionScore);
		}else if(prediction.equals(BioTMLConstants.i.toString())){
			if(annotation == null){
				IBioTMLAnnotationsRelation prevRelation =  new ArrayList<IBioTMLAnnotationsRelation>(relations).get(relations.size()-1);
				Set<IBioTMLAnnotation> annots = prevRelation.getRelation();
				Set<IBioTMLAnnotation> relationAnnotsCorrected = new LinkedHashSet<IBioTMLAnnotation>();
				Iterator<IBioTMLAnnotation> itAnn = annots.iterator();
				boolean foundPrevTokenInRelation = false;
				while(itAnn.hasNext() && !foundPrevTokenInRelation){
					annotation = itAnn.next();
					foundPrevTokenInRelation = validatePrevToken(annotation, token, doc.getID());
					if(foundPrevTokenInRelation){
						annotation = new BioTMLAnnotation(doc.getID(), tokenClass, annotation.getStartOffset(), token.getEndOffset(), predictionScore);
					}
					relationAnnotsCorrected.add(annotation);
				}
				if(itAnn.hasNext()){
					while(itAnn.hasNext()){
						relationAnnotsCorrected.add(itAnn.next());
					}
				}
				if(!foundPrevTokenInRelation){
					annotation = new BioTMLAnnotation(doc.getID(), tokenClass, token.getStartOffset(), token.getEndOffset(), predictionScore);
					relations = addRelation(relations, annotation, firstAnnotation, predictionScore);
				}else{
					relations = correctLastRelation(relations, predictionScore, relationAnnotsCorrected);
				}
			}
		}
		return relations;
	}

	/**
	 * 
	 * Method that changes the last relation with other relation.
	 * 
	 * @param relations - Set of relations ({@link IBioTMLAnnotationsRelation}).
	 * @param predictionScore - Model prediction score.
	 * @param relationAnnotsCorrected - Relation that will replace the last relation of the set.
	 * @return Set of relations ({@link IBioTMLAnnotationsRelation}) modified.
	 * @throws {@link BioTMLException}.
	 */
	private Set<IBioTMLAnnotationsRelation> correctLastRelation(
			Set<IBioTMLAnnotationsRelation> relations, double predictionScore,
			Set<IBioTMLAnnotation> relationAnnotsCorrected)
					throws BioTMLException {
		IBioTMLAnnotationsRelation lastRelationCorrected = new BioTMLAnnotationsRelation(relationAnnotsCorrected, predictionScore);
		List<IBioTMLAnnotationsRelation> listRelations = new ArrayList<IBioTMLAnnotationsRelation>(relations);
		listRelations.set(listRelations.size()-1, lastRelationCorrected);
		relations = new LinkedHashSet<IBioTMLAnnotationsRelation>(listRelations);
		return relations;
	}

	/**
	 * 
	 * Method that adds a relation regarding the position of the annotation and token.
	 * 
	 * @param relations - Set of all relations ({@link IBioTMLAnnotationsRelation}) predicted to add the relation.
	 * @param annotation - Annotation ({@link IBioTMLAnnotation}) that belongs to the relation.
	 * @param firstAnnotation - Annotation ({@link IBioTMLAnnotation}) that belongs to the relation.
	 * @param score - Score value associated to the prediction.
	 * @return Set of all relations ({@link IBioTMLAnnotationsRelation}) predicted.
	 * @throws BioTMLException
	 */
	private Set<IBioTMLAnnotationsRelation> addRelation(Set<IBioTMLAnnotationsRelation> relations, IBioTMLAnnotation annotation, IBioTMLAnnotation firstAnnotation, double score) throws BioTMLException{
		Set<IBioTMLAnnotation> relation = new LinkedHashSet<IBioTMLAnnotation>();
		if(firstAnnotation.getStartOffset()>annotation.getEndOffset()){
			relation.add(annotation);
			relation.add(firstAnnotation);
			relations.add(new BioTMLAnnotationsRelation(relation, score));
		}
		if(firstAnnotation.getEndOffset()<annotation.getStartOffset()){
			relation.add(firstAnnotation);
			relation.add(annotation);
			relations.add(new BioTMLAnnotationsRelation(relation, score));
		}
		return relations;
	}

	/**
	 * 
	 * Method that validates if the previous annotation contains offsets of tokens that are next to the inputed token in the same document.
	 * 
	 * @param prevAnnotiation - Annotation ({@link IBioTMLAnnotation}) to be validated as a previous annotation.
	 * @param token - Token that is next to annotation and could be incorporated in the annotation.
	 * @param docID - Document ID.
	 * @return Boolean that validates if is or not a previous annotation.
	 */
	private boolean validatePrevToken(IBioTMLAnnotation prevAnnotiation, IBioTMLToken token, long docID){
		if(prevAnnotiation.getDocID()==docID
				&& prevAnnotiation.getStartOffset()<token.getStartOffset()
				&& prevAnnotiation.getEndOffset()<token.getEndOffset()
				&& (token.getStartOffset() - prevAnnotiation.getEndOffset()) <3){
			return true;
		}
		return false;
	}


}
