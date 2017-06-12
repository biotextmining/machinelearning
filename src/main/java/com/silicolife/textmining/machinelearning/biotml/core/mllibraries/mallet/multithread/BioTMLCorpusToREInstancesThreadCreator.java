package com.silicolife.textmining.machinelearning.biotml.core.mllibraries.mallet.multithread;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;

import com.silicolife.textmining.machinelearning.biotml.core.BioTMLConstants;
import com.silicolife.textmining.machinelearning.biotml.core.corpora.BioTMLAnnotationImpl;
import com.silicolife.textmining.machinelearning.biotml.core.corpora.BioTMLAssociationImpl;
import com.silicolife.textmining.machinelearning.biotml.core.corpora.otherdatastructures.BioTMLObjectWithFeaturesAndLabels;
import com.silicolife.textmining.machinelearning.biotml.core.exception.BioTMLException;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLAnnotation;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLAssociation;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLCorpus;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLCorpusToInstancesThreadCreator;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLDocument;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLEntity;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLEvent;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLFeatureGeneratorConfigurator;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLREMethodologyConfiguration;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLSentence;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLToken;

public class BioTMLCorpusToREInstancesThreadCreator implements IBioTMLCorpusToInstancesThreadCreator{

	private IBioTMLCorpus corpus;
	private IBioTMLREMethodologyConfiguration reMethodology;
	private String eventType;
	private boolean stop = false;

	public BioTMLCorpusToREInstancesThreadCreator(IBioTMLCorpus corpus, IBioTMLREMethodologyConfiguration reMethodology, String eventType){
		this.corpus = corpus;
		this.reMethodology = reMethodology;
		this.eventType = eventType;
	}
	
	private IBioTMLCorpus getCorpus() {
		return corpus;
	}

	private IBioTMLREMethodologyConfiguration getREMethodology() {
		return reMethodology;
	}
	
	private String getAnnotationType(){
		return eventType;
	}
	
	@Override
	public void insertInstancesIntoExecutor(ExecutorService executor, IBioTMLFeatureGeneratorConfigurator configuration, InstanceListExtended instances) throws BioTMLException {
		for(IBioTMLDocument document : getCorpus().getDocuments()){
			for(IBioTMLSentence sentence : document.getSentences()){
				Set<IBioTMLEntity> annotations = getCorpus().getEntitiesFromSentenceInDocumentId(document.getID(), sentence);
				if(getREMethodology().getAllowedAssociations().isEmpty()){
					generateInstanceForAllEvents(executor, configuration, instances, document, sentence, annotations);
				}else{
					generateInstanceForREMethodology(executor, configuration, instances, document, sentence, annotations, getREMethodology().getAllowedAssociations());
				}
				if(stop)
					break;
			}
			if(stop)
				break;
		}
	}
	
	@SuppressWarnings("rawtypes")
	private void generateInstanceForAllEvents(ExecutorService executor,
			IBioTMLFeatureGeneratorConfigurator configuration, InstanceListExtended instances, IBioTMLDocument document,
			IBioTMLSentence sentence, Set<IBioTMLEntity> annotations) {
		
		Set<IBioTMLAssociation<IBioTMLEntity, IBioTMLEntity>> associations = generatePossibleAssociationsInSentence(annotations);
		
		BioTMLObjectWithFeaturesAndLabels<IBioTMLAssociation> associationsWithLabels = fillAssociationsWithLabels(document.getID(), sentence, associations);
		
		if(!associationsWithLabels.getBioTMLObjects().isEmpty())
			executor.execute(new CorpusSentenceAndFeaturesToInstanceThread(document, associationsWithLabels, instances, configuration));
		
	}
	
	@SuppressWarnings("rawtypes")
	private void generateInstanceForREMethodology(ExecutorService executor,
			IBioTMLFeatureGeneratorConfigurator configuration, InstanceListExtended instances, IBioTMLDocument document,
			IBioTMLSentence sentence, Set<IBioTMLEntity> annotations, Set<IBioTMLAssociation<IBioTMLAnnotation, IBioTMLAnnotation>> allowedAssociations) {
		
		Set<IBioTMLAssociation<IBioTMLEntity, IBioTMLEntity>> associations = generatePossibleAssociationsInSentence(annotations);
		
		associations = filterAssociationsWithAllowedAssociations(allowedAssociations, associations);
		
		BioTMLObjectWithFeaturesAndLabels<IBioTMLAssociation> associationsWithLabels = fillAssociationsWithLabels(document.getID(), sentence, associations);
		
		if(!associationsWithLabels.getBioTMLObjects().isEmpty())
			executor.execute(new CorpusSentenceAndFeaturesToInstanceThread(document, associationsWithLabels, instances, configuration));
		
	}

	private Set<IBioTMLAssociation<IBioTMLEntity, IBioTMLEntity>> filterAssociationsWithAllowedAssociations(
			Set<IBioTMLAssociation<IBioTMLAnnotation, IBioTMLAnnotation>> allowedAssociations,
			Set<IBioTMLAssociation<IBioTMLEntity, IBioTMLEntity>> associations) {
		Set<IBioTMLAssociation<IBioTMLEntity, IBioTMLEntity>> filteredAssociations = new HashSet<>();
		for(IBioTMLAssociation<IBioTMLEntity, IBioTMLEntity> association : associations){
			IBioTMLAssociation<IBioTMLAnnotation, IBioTMLAnnotation> associationType = new BioTMLAssociationImpl<>(new BioTMLAnnotationImpl(association.getEntryOne().getAnnotationType()),new BioTMLAnnotationImpl(association.getEntryTwo().getAnnotationType()));
			if(allowedAssociations.contains(associationType))
				filteredAssociations.add(association);
		}
		
		return filteredAssociations;
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


	@Override
	public void stopInsertion() {
		this.stop = true;
	}
	
}
