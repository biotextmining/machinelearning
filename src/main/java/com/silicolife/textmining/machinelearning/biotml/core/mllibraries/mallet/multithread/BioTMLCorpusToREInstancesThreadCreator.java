package com.silicolife.textmining.machinelearning.biotml.core.mllibraries.mallet.multithread;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;

import com.silicolife.textmining.machinelearning.biotml.core.BioTMLConstants;
import com.silicolife.textmining.machinelearning.biotml.core.corpora.otherdatastructures.BioTMLDocSentIDs;
import com.silicolife.textmining.machinelearning.biotml.core.corpora.otherdatastructures.BioTMLObjectWithFeaturesAndLabels;
import com.silicolife.textmining.machinelearning.biotml.core.exception.BioTMLException;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLAnnotation;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLCorpus;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLCorpusToInstancesThreadCreator;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLDocument;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLEvent;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLFeatureGeneratorConfigurator;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLSentence;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLToken;
import com.silicolife.textmining.machinelearning.biotml.core.models.BioTMLREModelTypes;

public class BioTMLCorpusToREInstancesThreadCreator implements IBioTMLCorpusToInstancesThreadCreator{

	private IBioTMLCorpus corpus;
	private String reMethodology;
	private boolean stop = false;

	public BioTMLCorpusToREInstancesThreadCreator(IBioTMLCorpus corpus, String reMethodology){
		this.corpus = corpus;
		this.reMethodology = reMethodology;
	}
	
	private IBioTMLCorpus getCorpus() {
		return corpus;
	}

	private String getREMethodology() {
		return reMethodology;
	}
	
	@Override
	public void insertInstancesIntoExecutor(ExecutorService executor, IBioTMLFeatureGeneratorConfigurator configuration,
			InstanceListExtended instances) throws BioTMLException {
		for(IBioTMLDocument document : getCorpus().getDocuments()){
			int sentID = 0;
			for(IBioTMLSentence sentence : document.getSentences()){
				Set<IBioTMLAnnotation> annotations = getCorpus().getAnnotationsFromSentenceInDocumentId(document.getID(), sentence);
				if(getREMethodology().equals(BioTMLREModelTypes.entityclue.toString())
						|| getREMethodology().equals(BioTMLREModelTypes.entityclueonlyannotations.toString())){
					generateInstanceForREWithTriggers(executor, configuration, instances, document.getID(), sentID, sentence, annotations);
				}else if(getREMethodology().equals(BioTMLREModelTypes.entityentiy.toString())
						|| getREMethodology().equals(BioTMLREModelTypes.entityentiyonlyannotations.toString())){
					generateInstanceForREWithEntityEntity(executor, configuration, instances, document.getID(), sentID, sentence, annotations);
				}
				sentID++;
				if(stop)
					break;
			}
			if(stop)
				break;
		}
	}
	
	private void generateInstanceForREWithEntityEntity(ExecutorService executor, IBioTMLFeatureGeneratorConfigurator configuration,
			InstanceListExtended instances, long docID, int sentID, 
			IBioTMLSentence sentence, Set<IBioTMLAnnotation> annotations) throws BioTMLException{
		for(IBioTMLAnnotation annotation :annotations){
			BioTMLObjectWithFeaturesAndLabels<String> sentenceText = null;
			boolean onlyannotations = false;
			if(getREMethodology().equals(BioTMLREModelTypes.entityentiyonlyannotations.toString())){
				onlyannotations = true;
				sentenceText = sentenceToExportForREOnlyAnnotations(docID, sentence, annotation, annotations);
			}else{
				sentenceText = sentenceToExportForRE(docID, sentence, annotation);
			}
			if(sentenceText != null && !sentenceText.getBioTMLObjects().isEmpty()){
				List<Integer> annotationIndexs = sentence.getTokenIndexsbyOffsets(annotation.getStartOffset(), annotation.getEndOffset());
				BioTMLDocSentIDs ids = new BioTMLDocSentIDs(docID, sentID);
				ids.setAnnotTokenRelationStartIndex(annotationIndexs.get(0));
				ids.setAnnotTokenRelationEndIndex(annotationIndexs.get(annotationIndexs.size()-1));
				ids.setOnlyAnnotations(onlyannotations);
				executor.execute(new CorpusSentenceAndFeaturesToInstanceThread(ids, sentenceText, instances, configuration));
			}
			if(stop)
				break;
		}
	}
	
	private void generateInstanceForREWithTriggers(ExecutorService executor, IBioTMLFeatureGeneratorConfigurator configuration,
			InstanceListExtended instances, long docID, int sentID, 
			IBioTMLSentence sentence, Set<IBioTMLAnnotation> annotations) throws BioTMLException{
		Set<IBioTMLAnnotation> triggers = getTriggerAnnotations(annotations);
		for(IBioTMLAnnotation trigger : triggers){
			BioTMLObjectWithFeaturesAndLabels<String> sentenceText = null;
			boolean onlyannotations = false;
			if(getREMethodology().equals(BioTMLREModelTypes.entityclueonlyannotations.toString())){
				onlyannotations = true;
				sentenceText = sentenceToExportForREOnlyAnnotations(docID, sentence, trigger, annotations);
			}else{
				sentenceText = sentenceToExportForRE(docID, sentence, trigger);
			}
			if(sentenceText != null && !sentenceText.getBioTMLObjects().isEmpty()){
				List<Integer> annotationIndexs = sentence.getTokenIndexsbyOffsets(trigger.getStartOffset(), trigger.getEndOffset());
				BioTMLDocSentIDs ids = new BioTMLDocSentIDs(docID, sentID);
				ids.setAnnotTokenRelationStartIndex(annotationIndexs.get(0));
				ids.setAnnotTokenRelationEndIndex(annotationIndexs.get(annotationIndexs.size()-1));
				ids.setOnlyAnnotations(onlyannotations);
				executor.execute(new CorpusSentenceAndFeaturesToInstanceThread(ids, sentenceText, instances, configuration));
			}
			if(stop)
				break;
		}
	}
	

	@Override
	public void stopInsertion() {
		this.stop = true;
	}
	
	private Set<IBioTMLAnnotation> getTriggerAnnotations(Set<IBioTMLAnnotation> annotations){
		Set<IBioTMLAnnotation> triggers = new HashSet<>();
		for(IBioTMLAnnotation annotation : annotations){
			if(annotation.getAnnotType().equals(BioTMLConstants.trigger.toString())){
				triggers.add(annotation);
			}
			if(stop)
				break;
		}
		return triggers;
	}
	
	private BioTMLObjectWithFeaturesAndLabels<String> sentenceToExportForRE(long docID, IBioTMLSentence sentence, IBioTMLAnnotation annotation) throws BioTMLException{
		BioTMLObjectWithFeaturesAndLabels<String> tokensWithLabels = new BioTMLObjectWithFeaturesAndLabels<>(String.class);
		for(IBioTMLToken token : sentence.getTokens()){
			if(getCorpus().getEvents() != null){
				if(!getCorpus().getEvents().isEmpty()){
					BioTMLConstants tokenLabel = getTokenLabelEvent(docID, token, annotation);
					tokensWithLabels.addBioTMLObjectForModel(token.getToken(), tokenLabel);
				}else{
					tokensWithLabels.addBioTMLObjectForPrediction(token.getToken());
				}
			}else{
				tokensWithLabels.addBioTMLObjectForPrediction(token.getToken());
			}

			if(stop)
				break;
		}
		return tokensWithLabels;
	}
	
	private BioTMLObjectWithFeaturesAndLabels<String> sentenceToExportForREOnlyAnnotations(long docID, IBioTMLSentence sentence, IBioTMLAnnotation annotation, Set<IBioTMLAnnotation> annotations) throws BioTMLException{
		BioTMLObjectWithFeaturesAndLabels<String> tokensWithLabels = new BioTMLObjectWithFeaturesAndLabels<>(String.class);
		for(IBioTMLToken token : sentence.getTokens()){
			BioTMLConstants isTokeninAnnots = null;
			if(getCorpus().isTokenInAnnotations(annotations, token)){
				isTokeninAnnots = BioTMLConstants.isAnnotation;
			}else{
				isTokeninAnnots = BioTMLConstants.isNotAnnotation;
			}
			if(getCorpus().getEvents() != null){
				if(!getCorpus().getEvents().isEmpty()){
					BioTMLConstants tokenLabel = getTokenLabelEvent(docID, token, annotation);
					tokensWithLabels.addBioTMLObjectForModelAnnotationFiltering(token.toString(), tokenLabel, isTokeninAnnots);
				}else{
					tokensWithLabels.addBioTMLObjectForPredictionAnnotationFiltering(token.toString(), isTokeninAnnots);
				}
			}else{
				tokensWithLabels.addBioTMLObjectForPredictionAnnotationFiltering(token.toString(), isTokeninAnnots);
			}
			if(stop)
				break;
		}
		return tokensWithLabels;
	}

	private BioTMLConstants getTokenLabelEvent(long docID, IBioTMLToken token, IBioTMLAnnotation annotation){
		Set<IBioTMLEvent> docEvents = getCorpus().getDocAnnotationEvents(docID);
		if( !docEvents.isEmpty()){
			for(IBioTMLEvent event : docEvents){
				if(event.findAnnotationInEvent(annotation)){
					try {
						IBioTMLAnnotation tokenBelongsToAnAnnotation = event.getAnnotationInEventByOffsets(token.getStartOffset(), token.getEndOffset());
						if(!tokenBelongsToAnAnnotation.getAnnotType().equals(BioTMLConstants.trigger.toString())
								&& !tokenBelongsToAnAnnotation.equals(annotation)){
							if(tokenBelongsToAnAnnotation.getStartOffset()==token.getStartOffset()){
								return BioTMLConstants.b;
							}else{
								return BioTMLConstants.i;
							}
						}
					} catch (BioTMLException e) {}
					//the token offsets are not present in the relation
				}
				if(stop)
					break;
			}
		}
		return BioTMLConstants.o;
	}
	
}
