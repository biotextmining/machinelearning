package com.silicolife.textmining.ie.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import com.silicolife.textmining.core.datastructures.annotation.AnnotationType;
import com.silicolife.textmining.core.datastructures.annotation.ner.EntityAnnotationImpl;
import com.silicolife.textmining.core.datastructures.annotation.re.EventAnnotationImpl;
import com.silicolife.textmining.core.datastructures.annotation.re.EventPropertiesImpl;
import com.silicolife.textmining.core.datastructures.documents.PublicationImpl;
import com.silicolife.textmining.core.datastructures.general.AnoteClass;
import com.silicolife.textmining.core.interfaces.core.annotation.IEntityAnnotation;
import com.silicolife.textmining.core.interfaces.core.annotation.IEventAnnotation;
import com.silicolife.textmining.core.interfaces.core.annotation.re.IEventProperties;
import com.silicolife.textmining.core.interfaces.core.dataaccess.exception.ANoteException;
import com.silicolife.textmining.core.interfaces.core.document.IAnnotatedDocument;
import com.silicolife.textmining.core.interfaces.core.general.classe.IAnoteClass;
import com.silicolife.textmining.machinelearning.biotml.core.BioTMLConstants;
import com.silicolife.textmining.machinelearning.biotml.core.corpora.BioTMLAssociationImpl;
import com.silicolife.textmining.machinelearning.biotml.core.corpora.BioTMLDocumentImpl;
import com.silicolife.textmining.machinelearning.biotml.core.corpora.BioTMLEntityImpl;
import com.silicolife.textmining.machinelearning.biotml.core.corpora.BioTMLEventImpl;
import com.silicolife.textmining.machinelearning.biotml.core.exception.BioTMLException;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLAssociation;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLDocument;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLEntity;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLEvent;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLSentence;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLToken;
import com.silicolife.textmining.machinelearning.biotml.core.nlp.BioTMLNLPManager;

public class BioTMLConversionUtils {


	public static IBioTMLDocument convertPublication(IAnnotatedDocument publication, String nlpSystem) throws BioTMLException, ANoteException{

		String text = publication.getDocumentAnnotationText();
		text = text.replaceAll("\\p{C}", " ");

		if(BioTMLNLPManager.getInstance().getNLPById(nlpSystem)==null)
			throw new BioTMLException("The NLP System is not recognized!");

		List<IBioTMLSentence> sentences = BioTMLNLPManager.getInstance().getNLPById(nlpSystem).getSentences(text);

		if(sentences == null)
			throw new BioTMLException("The document text wasn't tokenized by the nlp system!");

		String title = (publication.getTitle()!=null) ? publication.getTitle() : new String();
		String extenalLinks = PublicationImpl.getPublicationExternalIDsStream(publication);
		return new BioTMLDocumentImpl(publication.getId(), title, extenalLinks, sentences);

	}

	public static IBioTMLEntity convertEntityAnnotation(IEntityAnnotation entity, Long docId){
		String classType = entity.getClassAnnotation().getName();
		return new BioTMLEntityImpl(docId, classType, entity.getStartOffset(), entity.getEndOffset());
	}

	public static List<IBioTMLEntity> convertEntityAnnotations(List<IEntityAnnotation> entities, Long docId){
		List<IBioTMLEntity> annotations = new ArrayList<>();

		for(IEntityAnnotation entity : entities)
			annotations.add(convertEntityAnnotation(entity, docId));
		return annotations;
	}

	public static List<IBioTMLEvent> convertEventAnnotation(IEventAnnotation event, Long docId){
		List<IBioTMLEvent> events = new ArrayList<>();

		Set<IBioTMLAssociation<IBioTMLEntity, IBioTMLEntity>> associations = new HashSet<>();

		IBioTMLEntity trigger = new BioTMLEntityImpl(docId, BioTMLConstants.trigger.toString(), event.getStartOffset(), event.getEndOffset());
		if(trigger.getStartOffset() != trigger.getEndOffset()){

			List<IBioTMLEntity> entities = convertEntityAnnotations(event.getEntitiesAtLeft(), docId);
			for(IBioTMLEntity entity : entities)
				associations.add(new BioTMLAssociationImpl<>(trigger, entity));

			entities = convertEntityAnnotations(event.getEntitiesAtRight(), docId);
			for(IBioTMLEntity entity : entities)
				associations.add(new BioTMLAssociationImpl<>(trigger, entity));

		}else{

			List<IBioTMLEntity> entities = new ArrayList<>();
			entities.addAll(convertEntityAnnotations(event.getEntitiesAtLeft(), docId));
			entities.addAll(convertEntityAnnotations(event.getEntitiesAtRight(), docId));

			for(int i=0; i<entities.size(); i++){
				for(int j=i+1; j<entities.size(); j++){
					associations.add(new BioTMLAssociationImpl<>(entities.get(i), entities.get(j)));
				}
			}
		}

		String eventClass = event.getEventProperties().getClassification();

		for(IBioTMLAssociation<IBioTMLEntity, IBioTMLEntity> association : associations){
			if(association.isValid())
				events.add(new BioTMLEventImpl(association, eventClass));
		}

		return events;
	}

	public static List<IBioTMLEvent> convertEventAnnotations(List<IEventAnnotation> eventannotations, Long docId){
		List<IBioTMLEvent> events = new ArrayList<>();

		for(IEventAnnotation eventannotation : eventannotations)
			events.addAll(convertEventAnnotation(eventannotation, docId));

		return events;
	}

	public static IEntityAnnotation convertBioTMLAnnotation(IBioTMLEntity annotation, IBioTMLDocument document) throws BioTMLException{
		IAnoteClass anoteClass = new AnoteClass(annotation.getAnnotationType());
		List<IBioTMLToken> tokens = document.getTokens(annotation.getStartOffset(), annotation.getEndOffset());
		String annotationString = convertTokensToString(tokens);

		return new EntityAnnotationImpl(annotation.getStartOffset(), annotation.getEndOffset(), anoteClass, null, annotationString, false, false, new Properties());
	}

	public static List<IEntityAnnotation> convertBioTMLAnnotations(List<IBioTMLEntity> annotations, IBioTMLDocument document) throws BioTMLException{
		List<IEntityAnnotation> entities = new ArrayList<>();

		for(IBioTMLEntity annotation : annotations)
			entities.add(convertBioTMLAnnotation(annotation, document));

		return entities;
	}

	public static String convertTokensToString(List<IBioTMLToken> tokens){
		StringBuilder sb = new StringBuilder();
		Collections.sort(tokens);

		long endOffset = 0;

		for(IBioTMLToken token : tokens){
			if(sb.length() > 0){
				long spaceCharsSize = token.getStartOffset() - endOffset;
				for(long i=0; i<spaceCharsSize; i++)
					sb.append(" ");
			}
			sb.append(token.getToken());
			endOffset = token.getEndOffset();
		}

		return sb.toString();
	}

	public static IEventAnnotation convertBioTMLEvent(IBioTMLEvent event, IBioTMLDocument document) throws BioTMLException{
		@SuppressWarnings("rawtypes")
		IBioTMLAssociation association = event.getAssociation();
		if(association.getEntryOne() instanceof IBioTMLEntity && association.getEntryTwo() instanceof IBioTMLEntity){
			IBioTMLEntity annotationOne = (IBioTMLEntity) association.getEntryOne();
			IBioTMLEntity annotationTwo = (IBioTMLEntity) association.getEntryTwo();

			IEventProperties eventProperties = new EventPropertiesImpl();
			eventProperties.setClassification(event.getAnnotationType());
			List<IEntityAnnotation> left = new ArrayList<>();
			List<IEntityAnnotation> right = new ArrayList<>();

			if(annotationOne.getAnnotationType().equals(BioTMLConstants.trigger.toString()) && !annotationTwo.getAnnotationType().equals(BioTMLConstants.trigger.toString())){
				if(annotationOne.compareTo(annotationTwo) > 0)
					left.add(convertBioTMLAnnotation(annotationTwo, document));
				else
					right.add(convertBioTMLAnnotation(annotationTwo, document));

				List<IBioTMLToken> tokens = document.getTokens(annotationOne.getStartOffset(), annotationOne.getEndOffset());
				String clueString = convertTokensToString(tokens);
				return new EventAnnotationImpl(annotationOne.getStartOffset(), annotationOne.getEndOffset(), AnnotationType.re.name(), left, right, clueString, eventProperties, false); 

			}else if(!annotationOne.getAnnotationType().equals(BioTMLConstants.trigger.toString()) && annotationTwo.getAnnotationType().equals(BioTMLConstants.trigger.toString())){
				if(annotationTwo.compareTo(annotationOne)>0)
					left.add(convertBioTMLAnnotation(annotationOne, document));
				else
					right.add(convertBioTMLAnnotation(annotationOne, document));

				List<IBioTMLToken> tokens = document.getTokens(annotationTwo.getStartOffset(), annotationTwo.getEndOffset());
				String clueString = convertTokensToString(tokens);
				return new EventAnnotationImpl(annotationTwo.getStartOffset(), annotationTwo.getEndOffset(), AnnotationType.re.name(), left, right, clueString, eventProperties, false);

			}else{

				if(annotationOne.compareTo(annotationTwo) <0){
					left.add(convertBioTMLAnnotation(annotationOne, document));
					right.add(convertBioTMLAnnotation(annotationTwo, document));
				}else{
					left.add(convertBioTMLAnnotation(annotationTwo, document));
					right.add(convertBioTMLAnnotation(annotationOne, document));
				}
				return new EventAnnotationImpl(-1, -1, AnnotationType.re.name(), left, right, new String(), eventProperties, false);
			}
		}
		return null;
	}

	public static List<IEventAnnotation> convertBioTMLEvents(List<IBioTMLEvent> biotmlevents, IBioTMLDocument document) throws BioTMLException{
		List<IEventAnnotation> events = new ArrayList<>();
		for(IBioTMLEvent biotmlevent :biotmlevents){
			IEventAnnotation event = convertBioTMLEvent(biotmlevent, document);
			if(event != null)
				events.add(event);
		}

		return events;
	}

	public static IEventAnnotation convertBioTMLEventWithEntityAnnotations(IBioTMLEvent event, List<IEntityAnnotation> entities, IBioTMLDocument document) throws BioTMLException{
		@SuppressWarnings("rawtypes")
		IBioTMLAssociation association = event.getAssociation();
		if(association.getEntryOne() instanceof IBioTMLEntity && association.getEntryTwo() instanceof IBioTMLEntity){
			IBioTMLEntity annotationOne = (IBioTMLEntity) association.getEntryOne();
			IBioTMLEntity annotationTwo = (IBioTMLEntity) association.getEntryTwo();

			IEventProperties eventProperties = new EventPropertiesImpl();
			eventProperties.setClassification(event.getAnnotationType());
			List<IEntityAnnotation> left = new ArrayList<>();
			List<IEntityAnnotation> right = new ArrayList<>();

			if(annotationOne.getAnnotationType().equals(BioTMLConstants.trigger.toString()) && !annotationTwo.getAnnotationType().equals(BioTMLConstants.trigger.toString())){
				if(annotationOne.compareTo(annotationTwo) > 0)
					left.add(findBioTMLAnnotationInEntitesList(annotationTwo, document, entities));
				else
					right.add(findBioTMLAnnotationInEntitesList(annotationTwo, document, entities));

				List<IBioTMLToken> tokens = document.getTokens(annotationOne.getStartOffset(), annotationOne.getEndOffset());
				String clueString = convertTokensToString(tokens);
				return new EventAnnotationImpl(annotationOne.getStartOffset(), annotationOne.getEndOffset(), AnnotationType.re.name(), left, right, clueString, eventProperties, false); 

			}else if(!annotationOne.getAnnotationType().equals(BioTMLConstants.trigger.toString()) && annotationTwo.getAnnotationType().equals(BioTMLConstants.trigger.toString())){
				if(annotationTwo.compareTo(annotationOne)>0)
					left.add(findBioTMLAnnotationInEntitesList(annotationOne, document, entities));
				else
					right.add(findBioTMLAnnotationInEntitesList(annotationOne, document, entities));

				List<IBioTMLToken> tokens = document.getTokens(annotationTwo.getStartOffset(), annotationTwo.getEndOffset());
				String clueString = convertTokensToString(tokens);
				return new EventAnnotationImpl(annotationTwo.getStartOffset(), annotationTwo.getEndOffset(), AnnotationType.re.name(), left, right, clueString, eventProperties, false);

			}else{

				if(annotationOne.compareTo(annotationTwo) <0){
					left.add(findBioTMLAnnotationInEntitesList(annotationOne, document, entities));
					right.add(findBioTMLAnnotationInEntitesList(annotationTwo, document, entities));
				}else{
					left.add(findBioTMLAnnotationInEntitesList(annotationTwo, document, entities));
					right.add(findBioTMLAnnotationInEntitesList(annotationOne, document, entities));
				}
				return new EventAnnotationImpl(-1, -1, AnnotationType.re.name(), left, right, new String(), eventProperties, false);
			}
		}
		return null;
	}

	public static IEntityAnnotation findBioTMLAnnotationInEntitesList(IBioTMLEntity annotation, IBioTMLDocument document, List<IEntityAnnotation> entities) throws BioTMLException {

		IEntityAnnotation entityAnnotation = convertBioTMLAnnotation(annotation, document);

		for(IEntityAnnotation entity : entities)
			if(entityAnnotation.getStartOffset() == entity.getStartOffset() 
			&& entityAnnotation.getEndOffset() == entity.getEndOffset() 
			&& entityAnnotation.getClassAnnotation().equals(entity.getClassAnnotation())
			&& entityAnnotation.getAnnotationValue().equals(entity.getAnnotationValue()))
				return entity;

		throw new BioTMLException("The NER annotation of one event is not present in the BioTML corpus! Please review it.");
	}
	
	public static List<IEventAnnotation> convertBioTMLEventsWithEntityAnnotations(List<IBioTMLEvent> biotmlevents, List<IEntityAnnotation> entities, IBioTMLDocument document) throws BioTMLException{
		List<IEventAnnotation> events = new ArrayList<>();
		for(IBioTMLEvent btmlevent : biotmlevents){
			IEventAnnotation event = convertBioTMLEventWithEntityAnnotations(btmlevent, entities, document);
			if(event != null)
				events.add(event);
		}
			
		return events;
	}
}
