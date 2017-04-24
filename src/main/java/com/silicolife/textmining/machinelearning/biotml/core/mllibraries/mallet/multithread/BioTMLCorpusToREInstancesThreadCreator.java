package com.silicolife.textmining.machinelearning.biotml.core.mllibraries.mallet.multithread;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;

import com.silicolife.textmining.machinelearning.biotml.core.BioTMLConstants;
import com.silicolife.textmining.machinelearning.biotml.core.corpora.BioTMLAssociationImpl;
import com.silicolife.textmining.machinelearning.biotml.core.corpora.otherdatastructures.BioTMLDocSentIDs;
import com.silicolife.textmining.machinelearning.biotml.core.corpora.otherdatastructures.BioTMLObjectWithFeaturesAndLabels;
import com.silicolife.textmining.machinelearning.biotml.core.exception.BioTMLException;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLAssociation;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLCorpus;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLCorpusToInstancesThreadCreator;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLDocument;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLEntity;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLEvent;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLFeatureGeneratorConfigurator;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLSentence;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLToken;
import com.silicolife.textmining.machinelearning.biotml.core.models.BioTMLREModelTypes;

public class BioTMLCorpusToREInstancesThreadCreator implements IBioTMLCorpusToInstancesThreadCreator{

	private IBioTMLCorpus corpus;
	private String reMethodology;
	private String eventType;
	private boolean stop = false;

	public BioTMLCorpusToREInstancesThreadCreator(IBioTMLCorpus corpus, String reMethodology, String eventType){
		this.corpus = corpus;
		this.reMethodology = reMethodology;
		this.eventType = eventType;
	}
	
	private IBioTMLCorpus getCorpus() {
		return corpus;
	}

	private String getREMethodology() {
		return reMethodology;
	}
	
	private String getAnnotationType(){
		return eventType;
	}
	
	@Override
	public void insertInstancesIntoExecutor(ExecutorService executor, IBioTMLFeatureGeneratorConfigurator configuration, InstanceListExtended instances) throws BioTMLException {
		for(IBioTMLDocument document : getCorpus().getDocuments()){
			int sentID = 0;
			for(IBioTMLSentence sentence : document.getSentences()){
				Set<IBioTMLEntity> annotations = getCorpus().getEntitiesFromSentenceInDocumentId(document.getID(), sentence);
				if(getREMethodology().equals(BioTMLREModelTypes.alltokenscoocurrencewithtriggers.toString())
						|| getREMethodology().equals(BioTMLREModelTypes.annotationtokenscoocurrencewithtriggers.toString())){
					generateInstanceForREWithTriggers(executor, configuration, instances, document.getID(), sentID, sentence, annotations);
				}else if(getREMethodology().equals(BioTMLREModelTypes.alltokenscoocurrencewithentities.toString())
						|| getREMethodology().equals(BioTMLREModelTypes.annotationtokenscoocureencewithentities.toString())){
					generateInstanceForREWithEntityEntity(executor, configuration, instances, document.getID(), sentID, sentence, annotations);
				}else if(getREMethodology().equals(BioTMLREModelTypes.events.toString())){
					generateInstanceForREEvents(executor, configuration, instances, document.getID(), sentID, sentence, annotations);
				}
				sentID++;
				if(stop)
					break;
			}
			if(stop)
				break;
		}
	}
	
	@SuppressWarnings("rawtypes")
	private void generateInstanceForREEvents(ExecutorService executor,
			IBioTMLFeatureGeneratorConfigurator configuration, InstanceListExtended instances, long docID, int sentID,
			IBioTMLSentence sentence, Set<IBioTMLEntity> annotations) {
		
		Set<IBioTMLAssociation<IBioTMLEntity, IBioTMLEntity>> associations = generatePossibleAssociationsInSentence(annotations);
		
		BioTMLObjectWithFeaturesAndLabels<IBioTMLAssociation> associationsWithLabels = fillAssociationsWithLabels(docID, sentence, associations);
		
		if(!associationsWithLabels.getBioTMLObjects().isEmpty()){
			BioTMLDocSentIDs ids = new BioTMLDocSentIDs(docID, sentID);
			List<IBioTMLAssociation> associationsList = Arrays.asList(associations.toArray(new IBioTMLAssociation[0]));
			ids.setAssociations(associationsList);
			executor.execute(new CorpusSentenceAndFeaturesToInstanceThread(ids, associationsWithLabels, instances, configuration));
		}
		
	}

	@SuppressWarnings("rawtypes")
	private BioTMLObjectWithFeaturesAndLabels<IBioTMLAssociation> fillAssociationsWithLabels(long docID,
			IBioTMLSentence sentence, Set<IBioTMLAssociation<IBioTMLEntity, IBioTMLEntity>> associations) {
		Set<IBioTMLEvent> events = getCorpus().getDocEvents(docID);
		BioTMLObjectWithFeaturesAndLabels<IBioTMLAssociation> associationsWithLabels = new BioTMLObjectWithFeaturesAndLabels<>(IBioTMLAssociation.class);
		for(IBioTMLAssociation<IBioTMLEntity, IBioTMLEntity> association : associations){
			if(getCorpus().getEvents() != null){
				if(!getCorpus().getEvents().isEmpty()){
					BioTMLConstants label = getAssociationLabel(association, events);
					associationsWithLabels.addBioTMLObjectForModel(association, label);
				}else{
					associationsWithLabels.addBioTMLObjectForPrediction(association);
				}
			}else{
				associationsWithLabels.addBioTMLObjectForPrediction(association);
			}
		}
		
		for(IBioTMLToken token : sentence.getTokens()){
			associationsWithLabels.addToken(token);
		}
		return associationsWithLabels;
	}

	private Set<IBioTMLAssociation<IBioTMLEntity, IBioTMLEntity>> generatePossibleAssociationsInSentence(
			Set<IBioTMLEntity> annotations) {
		List<IBioTMLEntity> annotationsList = Arrays.asList(annotations.toArray(new IBioTMLEntity[0]));
		Collections.sort(annotationsList);
		Set<IBioTMLAssociation<IBioTMLEntity, IBioTMLEntity>> associations = new LinkedHashSet<>();
		for(int i=0; i<annotationsList.size(); i++)
			for(int j=i+1; j<annotationsList.size(); j++)
				associations.add(new BioTMLAssociationImpl<IBioTMLEntity, IBioTMLEntity>(annotationsList.get(i), annotationsList.get(j)));
		
		return associations;
	}

	private BioTMLConstants getAssociationLabel(IBioTMLAssociation<?,?> association, Set<IBioTMLEvent> events) {
		for(IBioTMLEvent event : events){
			if(event.getAnnotationType().equals(getAnnotationType()) && event.getAssociation().equals(association))
				return BioTMLConstants.b;
		}
		return BioTMLConstants.o;
	}

	private void generateInstanceForREWithEntityEntity(ExecutorService executor, IBioTMLFeatureGeneratorConfigurator configuration,
			InstanceListExtended instances, long docID, int sentID, 
			IBioTMLSentence sentence, Set<IBioTMLEntity> annotations) throws BioTMLException{
		for(IBioTMLEntity annotation :annotations){
			BioTMLObjectWithFeaturesAndLabels<IBioTMLToken> sentenceText = null;
			boolean onlyannotations = false;
			if(getREMethodology().equals(BioTMLREModelTypes.annotationtokenscoocureencewithentities.toString())){
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
			IBioTMLSentence sentence, Set<IBioTMLEntity> annotations) throws BioTMLException{
		Set<IBioTMLEntity> triggers = getTriggerAnnotations(annotations);
		for(IBioTMLEntity trigger : triggers){
			BioTMLObjectWithFeaturesAndLabels<IBioTMLToken> sentenceText = null;
			boolean onlyannotations = false;
			if(getREMethodology().equals(BioTMLREModelTypes.annotationtokenscoocurrencewithtriggers.toString())){
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
	
	private Set<IBioTMLEntity> getTriggerAnnotations(Set<IBioTMLEntity> annotations){
		Set<IBioTMLEntity> triggers = new HashSet<>();
		for(IBioTMLEntity annotation : annotations){
			if(annotation.getAnnotationType().equals(BioTMLConstants.trigger.toString())){
				triggers.add(annotation);
			}
			if(stop)
				break;
		}
		return triggers;
	}
	
	private BioTMLObjectWithFeaturesAndLabels<IBioTMLToken> sentenceToExportForRE(long docID, IBioTMLSentence sentence, IBioTMLEntity annotation) throws BioTMLException{
		BioTMLObjectWithFeaturesAndLabels<IBioTMLToken> tokensWithLabels = new BioTMLObjectWithFeaturesAndLabels<>(IBioTMLToken.class);
		for(IBioTMLToken token : sentence.getTokens()){
			if(getCorpus().getEvents() != null){
				if(!getCorpus().getEvents().isEmpty()){
					BioTMLConstants tokenLabel = getTokenLabelEvent(docID, token, annotation);
					tokensWithLabels.addBioTMLObjectForModel(token, tokenLabel);
					tokensWithLabels.addToken(token);
				}else{
					tokensWithLabels.addBioTMLObjectForPrediction(token);
					tokensWithLabels.addToken(token);
				}
			}else{
				tokensWithLabels.addBioTMLObjectForPrediction(token);
				tokensWithLabels.addToken(token);
			}

			if(stop)
				break;
		}
		return tokensWithLabels;
	}
	
	private BioTMLObjectWithFeaturesAndLabels<IBioTMLToken> sentenceToExportForREOnlyAnnotations(long docID, IBioTMLSentence sentence, IBioTMLEntity annotation, Set<IBioTMLEntity> annotations) throws BioTMLException{
		BioTMLObjectWithFeaturesAndLabels<IBioTMLToken> tokensWithLabels = new BioTMLObjectWithFeaturesAndLabels<>(IBioTMLToken.class);
		for(IBioTMLToken token : sentence.getTokens()){
			BioTMLConstants isTokeninAnnots = null;
			if(getCorpus().isTokenInEntities(annotations, token)){
				isTokeninAnnots = BioTMLConstants.isAnnotation;
			}else{
				isTokeninAnnots = BioTMLConstants.isNotAnnotation;
			}
			if(getCorpus().getEvents() != null){
				if(!getCorpus().getEvents().isEmpty()){
					BioTMLConstants tokenLabel = getTokenLabelEvent(docID, token, annotation);
					tokensWithLabels.addBioTMLObjectForModelAnnotationFiltering(token, tokenLabel, isTokeninAnnots);
					tokensWithLabels.addToken(token);
				}else{
					tokensWithLabels.addBioTMLObjectForPredictionAnnotationFiltering(token, isTokeninAnnots);
					tokensWithLabels.addToken(token);
				}
			}else{
				tokensWithLabels.addBioTMLObjectForPredictionAnnotationFiltering(token, isTokeninAnnots);
				tokensWithLabels.addToken(token);
			}
			if(stop)
				break;
		}
		return tokensWithLabels;
	}

	private BioTMLConstants getTokenLabelEvent(long docID, IBioTMLToken token, IBioTMLEntity annotation){
		Set<IBioTMLEvent> docEvents = getCorpus().getDocEvents(docID);
		if( !docEvents.isEmpty()){
			for(IBioTMLEvent event : docEvents){
				if(event.getAnnotationType().equals(getAnnotationType()) && event.findAnnotationInEvent(annotation)){
					try {
						IBioTMLEntity tokenBelongsToAnAnnotation = event.getAnnotationInEventByOffsets(token.getStartOffset(), token.getEndOffset());
						if(!tokenBelongsToAnAnnotation.getAnnotationType().equals(BioTMLConstants.trigger.toString())
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
