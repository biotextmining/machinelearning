package com.silicolife.textmining.ie;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.silicolife.textmining.core.datastructures.annotation.AnnotationPosition;
import com.silicolife.textmining.core.datastructures.annotation.AnnotationPositions;
import com.silicolife.textmining.core.datastructures.annotation.AnnotationType;
import com.silicolife.textmining.core.datastructures.annotation.ner.EntityAnnotationImpl;
import com.silicolife.textmining.core.datastructures.annotation.re.EventAnnotationImpl;
import com.silicolife.textmining.core.datastructures.annotation.re.EventPropertiesImpl;
import com.silicolife.textmining.core.datastructures.documents.AnnotatedDocumentImpl;
import com.silicolife.textmining.core.datastructures.documents.PublicationImpl;
import com.silicolife.textmining.core.datastructures.general.AnoteClass;
import com.silicolife.textmining.core.datastructures.init.InitConfiguration;
import com.silicolife.textmining.core.interfaces.core.annotation.IEntityAnnotation;
import com.silicolife.textmining.core.interfaces.core.annotation.IEventAnnotation;
import com.silicolife.textmining.core.interfaces.core.dataaccess.exception.ANoteException;
import com.silicolife.textmining.core.interfaces.core.document.IAnnotatedDocument;
import com.silicolife.textmining.core.interfaces.core.document.IDocumentSet;
import com.silicolife.textmining.core.interfaces.core.document.IPublication;
import com.silicolife.textmining.core.interfaces.core.document.IPublicationExternalSourceLink;
import com.silicolife.textmining.core.interfaces.core.document.labels.IPublicationLabel;
import com.silicolife.textmining.core.interfaces.core.document.structure.IPublicationField;
import com.silicolife.textmining.core.interfaces.core.report.processes.INERProcessReport;
import com.silicolife.textmining.core.interfaces.core.report.processes.IREProcessReport;
import com.silicolife.textmining.core.interfaces.process.ProcessTypeEnum;
import com.silicolife.textmining.core.interfaces.process.IE.IIEProcess;
import com.silicolife.textmining.machinelearning.biotml.core.BioTMLConstants;
import com.silicolife.textmining.machinelearning.biotml.core.corpora.BioTMLAnnotationImpl;
import com.silicolife.textmining.machinelearning.biotml.core.corpora.BioTMLAssociationImpl;
import com.silicolife.textmining.machinelearning.biotml.core.corpora.BioTMLCorpusImpl;
import com.silicolife.textmining.machinelearning.biotml.core.corpora.BioTMLDocumentImpl;
import com.silicolife.textmining.machinelearning.biotml.core.corpora.BioTMLEventImpl;
import com.silicolife.textmining.machinelearning.biotml.core.exception.BioTMLException;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLAnnotation;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLAssociation;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLCorpus;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLDocument;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLEvent;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLSentence;
import com.silicolife.textmining.machinelearning.biotml.core.nlp.BioTMLNLPManager;

public class BioTMLConverter {

	private IIEProcess process;
	private String nlpSystem;
	private boolean stop = false;

	public BioTMLConverter(String nlpSystem, IIEProcess process){
		this.process = process;
		this.nlpSystem = nlpSystem;
	}

	private IIEProcess getProcess(){
		return process;
	}

	private String getNLPSystem(){
		return nlpSystem;
	}

	public IBioTMLCorpus convertToBioTMLCorpus() throws ANoteException, BioTMLException{
		if(getProcess() != null){
			return convertAnoteCorpusWithProcess(getProcess(), getNLPSystem());
		}
		return null;
	}

	public void convertBioTMLCorpusToAnote(IIEProcess process,IBioTMLCorpus annotatedcorpus, INERProcessReport report) throws BioTMLException, ANoteException{
		if(process.getType().getType().equals(ProcessTypeEnum.NER.toString())){
			annotateNERInAnote(annotatedcorpus, process, false, report);
		}
	}

	public void convertBioTMLCorpusToAnote(IIEProcess process,IBioTMLCorpus annotatedcorpus, IREProcessReport report) throws BioTMLException, ANoteException{
		if(process.getType().getType().equals(ProcessTypeEnum.RE.toString())){
			annotateREInAnote(annotatedcorpus, process, false, true, report);
		}
	}

	public void stop(){
		stop = true;
	}

	private BioTMLCorpusImpl convertAnoteCorpusWithProcess(IIEProcess baseProcess, String nlpSystem) throws ANoteException, BioTMLException {
		IDocumentSet docs = baseProcess.getCorpus().getArticlesCorpus();
		List<IBioTMLDocument> listDocuments = new ArrayList<IBioTMLDocument>();
		List<IBioTMLAnnotation> listAnnotations = new ArrayList<IBioTMLAnnotation>();
		List<IBioTMLEvent> listEvents = new ArrayList<IBioTMLEvent>();
		for(IPublication doc:docs){
			IAnnotatedDocument annotDoc = new AnnotatedDocumentImpl(doc, baseProcess, baseProcess.getCorpus());
			List<IBioTMLSentence> sentences = null;
			String text = annotDoc.getDocumentAnnotationText();
			text = text.replaceAll("\\p{C}", " ");
			if(BioTMLNLPManager.getInstance().getNLPById(nlpSystem)==null)
				throw new BioTMLException("The NLP System is not recognized!");
			sentences = BioTMLNLPManager.getInstance().getNLPById(nlpSystem).getSentences(text);
//			if(nlpSystem == BioTMLNLPSystemsEnum.clearnlp){
//				sentences = BioTMLClearNLP.getInstance().getSentences(text);
//			}else if(nlpSystem == BioTMLNLPSystemsEnum.opennlp){
//				sentences = BioTMLOpenNLP.getInstance().getSentences(text);
//			}else if(nlpSystem == BioTMLNLPSystemsEnum.stanfordnlp){
//				sentences = BioTMLStanfordNLP.getInstance().getSentences(text);
//			}

			if(sentences == null){
				throw new BioTMLException("The document text wasn't tokenized by the nlp system!");
			}
			String title = (annotDoc.getTitle()!=null)?annotDoc.getTitle():new String();
			String extenalLinks = PublicationImpl.getPublicationExternalIDsStream(annotDoc);
			listDocuments.add(new BioTMLDocumentImpl(annotDoc.getId(), title, extenalLinks, sentences));
			long lastIndex = sentences.get(sentences.size()-1).getEndSentenceOffset();
			for(IEntityAnnotation entity : annotDoc.getEntitiesAnnotations()){
				String classType = entity.getClassAnnotation().getName();
				if(entity.getStartOffset()>lastIndex|| entity.getEndOffset()>lastIndex){
					throw new BioTMLException("The annotation offsets are bigger than the document size!");
				}
				listAnnotations.add(new BioTMLAnnotationImpl(doc.getId(),classType, entity.getStartOffset(), entity.getEndOffset()));
			}

			if(baseProcess.getType().getType().equals(ProcessTypeEnum.RE.toString())){

				for(IEventAnnotation event : annotDoc.getEventAnnotations()){
					Set<IBioTMLAssociation<IBioTMLAnnotation, IBioTMLAnnotation>> associations = new HashSet<>();
					IBioTMLAnnotation trigger = new BioTMLAnnotationImpl(doc.getId(), BioTMLConstants.trigger.toString(), event.getStartOffset(), event.getEndOffset());
					if(trigger.getStartOffset() != trigger.getEndOffset()){
						Set<IBioTMLAnnotation> leftAnnots = convertEntityAnnotationsToBioTMLAnnotations(event.getEntitiesAtLeft(),  doc.getId());
						for(IBioTMLAnnotation leftAnnot : leftAnnots){
							associations.add(new BioTMLAssociationImpl<>(trigger, leftAnnot));
						}
						Set<IBioTMLAnnotation> rightAnnots = convertEntityAnnotationsToBioTMLAnnotations(event.getEntitiesAtRight(),  doc.getId());
						for(IBioTMLAnnotation rightAnnot : rightAnnots){
							associations.add(new BioTMLAssociationImpl<>(trigger, rightAnnot));
						}
					}else{
						Set<IBioTMLAnnotation> annots = convertEntityAnnotationsToBioTMLAnnotations(event.getEntitiesAtLeft(),  doc.getId());
						annots.addAll(convertEntityAnnotationsToBioTMLAnnotations(event.getEntitiesAtRight(),  doc.getId()));
						Iterator<IBioTMLAnnotation> iteratorI = annots.iterator();
						while(iteratorI.hasNext()){
							IBioTMLAnnotation entity = iteratorI.next();
							Iterator<IBioTMLAnnotation> iteratorJ = annots.iterator();
							while(iteratorJ.hasNext()){
								IBioTMLAnnotation secondEntity = iteratorJ.next();
								IBioTMLAssociation<IBioTMLAnnotation, IBioTMLAnnotation> association = new BioTMLAssociationImpl<>(entity, secondEntity);
								if(association.isValid()){
									associations.add(association);
								}
							}
						}
					}
					
					for(IBioTMLAssociation<IBioTMLAnnotation, IBioTMLAnnotation> association : associations){
						String eventType = new String(); //TODO: event property to get classification/type
						listEvents.add(new BioTMLEventImpl(association, eventType));
					}

				}
			} 
		}

		if(!listEvents.isEmpty()){
			return new BioTMLCorpusImpl(listDocuments, listAnnotations, listEvents, baseProcess.getCorpus().toString());
		}else{
			return new BioTMLCorpusImpl(listDocuments, listAnnotations, baseProcess.getCorpus().toString());
		}
	}

	private Set<IBioTMLAnnotation> convertEntityAnnotationsToBioTMLAnnotations(List<IEntityAnnotation> annotations, long docID){
		Set<IBioTMLAnnotation> annotationsResult = new HashSet<>();
		for(IEntityAnnotation entity : annotations){
			annotationsResult.add(new BioTMLAnnotationImpl(docID, entity.getClassAnnotation().getName(), entity.getStartOffset(), entity.getEndOffset()));
		}
		return annotationsResult;
	}



	private void annotateNERInAnote(IBioTMLCorpus annotatedCorpus, IIEProcess process, boolean loadCluesAsEntities, INERProcessReport report) throws BioTMLException, ANoteException{
		Iterator<IBioTMLDocument> idDoc = annotatedCorpus.getDocuments().iterator();
		while(idDoc.hasNext() && !stop){
			IBioTMLDocument doc = idDoc.next();
			AnnotationPositions positions = loadAllDocEnities(annotatedCorpus, doc, loadCluesAsEntities);
			List<IEntityAnnotation> entities = positions.getEntitiesFromAnnoattionPositions();
			if(!stop){
				IPublication document =  new PublicationImpl(doc.getID(),
						"", "", "", "", "",
						"", "", "", "", "", "",
						"", false, "", "",
						new ArrayList<IPublicationExternalSourceLink>() ,
						new ArrayList<IPublicationField>() ,
						new ArrayList<IPublicationLabel>() );
				InitConfiguration.getDataAccess().addProcessDocumentEntitiesAnnotations(process, document, entities);
				report.incrementEntitiesAnnotated(entities.size());
			}
		}
	}

	private void annotateREInAnote(IBioTMLCorpus annotatedCorpus, IIEProcess process, boolean loadCluesAsEntities, boolean annotateOnlyPairsInSameClue, IREProcessReport report) throws BioTMLException, ANoteException{

		IDocumentSet documents = process.getCorpus().getArticlesCorpus();
		Iterator<IBioTMLDocument> idDoc = annotatedCorpus.getDocuments().iterator();
		while(idDoc.hasNext() && !stop){
			IBioTMLDocument doc = idDoc.next();
			AnnotationPositions positions = loadAllDocEnities(annotatedCorpus, doc,loadCluesAsEntities);
			List<IEntityAnnotation> entities = positions.getEntitiesFromAnnoattionPositions();
			List<IEventAnnotation> events = loadAllDocEvents(annotatedCorpus, doc, entities);
			if(annotateOnlyPairsInSameClue){
				events = associatePairsInSameClue(events);
			}
			
			IPublication anoteDocument = documents.getDocument(doc.getID());
			InitConfiguration.getDataAccess().addProcessDocumentEntitiesAnnotations(process, anoteDocument, entities);
			InitConfiguration.getDataAccess().addProcessDocumentEventAnnoations(process, anoteDocument, events);
			report.incrementEntitiesAnnotated(entities.size());
			report.increaseRelations(events.size());
		}

	}

	private AnnotationPositions loadAllDocEnities(IBioTMLCorpus annotatedCorpus, IBioTMLDocument doc, boolean loadCluesAsEntities) throws BioTMLException {
		AnnotationPositions positions = new AnnotationPositions();
		for(IBioTMLAnnotation annotation : annotatedCorpus.getDocAnnotations(doc.getID())){
			if(loadCluesAsEntities || !annotation.getAnnotType().equals(BioTMLConstants.trigger.toString())){
				String tokensString = doc.toString().substring((int)annotation.getStartOffset(), (int)annotation.getEndOffset());
				if(tokensString.length() > 499){
					String[] tokenStrings = tokensString.split(", ");
					if(tokenStrings.length>1){
						splitInsertPostProcessingMerhtod(positions, annotation, tokenStrings, ", ");
					}else{
						tokenStrings = tokensString.split(" ");
						if(tokenStrings.length>1){
							splitInsertPostProcessingMerhtod(positions, annotation, tokenStrings, " ");
						}else{
							tokenStrings = new String[2];
							tokenStrings[0] = tokensString.substring(0, tokensString.length()/2);
							tokenStrings[1] = tokensString.substring(tokensString.length()/2);
							splitInsertPostProcessingMerhtod(positions, annotation, tokenStrings, "");
						}
					}
				}else{
					positions.addAnnotationWhitConflitsAndReplaceIfRangeIsMore(new AnnotationPosition((int)annotation.getStartOffset(), (int)annotation.getEndOffset()),new EntityAnnotationImpl(annotation.getStartOffset(), annotation.getEndOffset(), new AnoteClass(annotation.getAnnotType()), null, tokensString, false,false, null));
				}
			}
		}
		return positions;
	}

	private void splitInsertPostProcessingMerhtod(
			AnnotationPositions positions, IBioTMLAnnotation annotation, String[] tokenStrings, String stringToRemove) {
		long offset = 0;
		for(String token : tokenStrings){
			long endoffset = offset + (long)token.length();
			if(!token.equals(stringToRemove)){
				if(offset == 0){
					endoffset = annotation.getStartOffset() + (long)token.length();

					positions.addAnnotationWhitConflitsAndReplaceIfRangeIsMore(new AnnotationPosition((int)annotation.getStartOffset(), (int)endoffset),new EntityAnnotationImpl( annotation.getStartOffset(), endoffset, new AnoteClass(annotation.getAnnotType()), null, token, false,false, null));
				}else if(offset + (long)token.length() == annotation.getEndOffset()){
					positions.addAnnotationWhitConflitsAndReplaceIfRangeIsMore(new AnnotationPosition((int)offset, (int)annotation.getEndOffset()),new EntityAnnotationImpl( offset, annotation.getEndOffset(), new AnoteClass(annotation.getAnnotType()), null, token, false,false, null));
				}else{
					positions.addAnnotationWhitConflitsAndReplaceIfRangeIsMore(new AnnotationPosition((int)offset, (int)endoffset), new EntityAnnotationImpl( offset, endoffset, new AnoteClass(annotation.getAnnotType()), null, token, false,false, null));
				}
			}
			offset = endoffset + (long)stringToRemove.length();
		}
	}

	private List<IEventAnnotation> loadAllDocEvents(IBioTMLCorpus annotatedCorpus, IBioTMLDocument doc, List<IEntityAnnotation> entities) throws BioTMLException {
		Set<IBioTMLEvent> biotmlEvents = annotatedCorpus.getDocEventsWithBestScore(doc.getID());
		List<IEventAnnotation> events = new ArrayList<IEventAnnotation>();
		Map<IBioTMLAnnotation, Set<IBioTMLAnnotation>> triggerToEntities = new HashMap<>();
		for(IBioTMLEvent event : biotmlEvents){

			IBioTMLAnnotation clue = null;
			if(event.getTrigger().getAnnotType().equals(BioTMLConstants.trigger.toString())){
				clue = event.getTrigger();
			}else if(event.getEntity().getAnnotType().equals(BioTMLConstants.trigger.toString())){
				clue = event.getEntity();
			}

			if(clue !=null){
				if(!triggerToEntities.containsKey(clue))
					triggerToEntities.put(clue, new HashSet<>());
				Set<IBioTMLAnnotation> entitiesonmap = triggerToEntities.get(clue);
				if(!clue.equals(event.getTrigger()))
					entitiesonmap.add(event.getTrigger());
				if(!clue.equals(event.getEntity()))
					entitiesonmap.add(event.getEntity());
				triggerToEntities.put(clue, entitiesonmap);
			}else{
				
				IBioTMLAnnotation entOne = event.getTrigger();
				IBioTMLAnnotation entTwo = event.getEntity();
				Set<IEntityAnnotation> leftEnt = new HashSet<>();
				Set<IEntityAnnotation> rightEnt = new HashSet<>();
				Set<IBioTMLAnnotation> leftAnnots = new HashSet<>();
				Set<IBioTMLAnnotation> rightAnnots = new HashSet<>();
				if(entOne.compareTo(entTwo)>0){
					leftAnnots.add(entTwo);
					rightAnnots.add(entOne);
					leftEnt = loadEntitiesInRelations(doc, leftAnnots, entities);
					rightEnt = loadEntitiesInRelations(doc, rightAnnots, entities);
				}else{
					leftAnnots.add(entOne);
					leftAnnots.add(entTwo);
					leftEnt = loadEntitiesInRelations(doc, leftAnnots, entities);
					rightEnt = loadEntitiesInRelations(doc, rightAnnots, entities);
				}

				if(!(leftEnt.isEmpty() && rightEnt.isEmpty())){
					events.add(new EventAnnotationImpl(1, 1, AnnotationType.re.name(), new ArrayList<>(leftEnt), new ArrayList<>(rightEnt), new String(),new EventPropertiesImpl(),false));

				}
			}
		}
		
		for(IBioTMLAnnotation clue : triggerToEntities.keySet()){
			Set<IBioTMLAnnotation> entitiesToOrder = triggerToEntities.get(clue);
			Set<IBioTMLAnnotation> leftAnnots = new HashSet<>();
			Set<IBioTMLAnnotation> rightAnnots = new HashSet<>();
			for(IBioTMLAnnotation entityToOrder : entitiesToOrder){
				if(clue.compareTo(entityToOrder)>0){
					leftAnnots.add(entityToOrder);
				}else{
					rightAnnots.add(entityToOrder);
				}
			}
			Set<IEntityAnnotation> leftEnt = loadEntitiesInRelations(doc, leftAnnots, entities);
			Set<IEntityAnnotation> rightEnt = loadEntitiesInRelations(doc, rightAnnots, entities);
			events.add(new EventAnnotationImpl(clue.getStartOffset(), clue.getEndOffset(), AnnotationType.re.name(), new ArrayList<>(leftEnt), new ArrayList<>(rightEnt), doc.toString().substring((int)clue.getStartOffset(), (int)clue.getEndOffset()), new EventPropertiesImpl(),false));
		}
	
		return events;
	}

	private Set<IEntityAnnotation> loadEntitiesInRelations(IBioTMLDocument document, Set<IBioTMLAnnotation> annotationsInOneSide, List<IEntityAnnotation> entities) throws BioTMLException {
		Set<IEntityAnnotation> entitiesInThatSide = new HashSet<>();
		for(IBioTMLAnnotation annotation : annotationsInOneSide){
			if(!annotation.getAnnotType().equals(BioTMLConstants.trigger.toString())){
				String tokensString = document.toString().substring((int)annotation.getStartOffset(), (int)annotation.getEndOffset());
				IEntityAnnotation entityAnnotation = new EntityAnnotationImpl(annotation.getStartOffset(), annotation.getEndOffset(), new AnoteClass(annotation.getAnnotType()), null, tokensString, false,false, null);
				boolean found = false;
				Iterator<IEntityAnnotation> itEnt = entities.iterator();
				while(!found && itEnt.hasNext()){
					IEntityAnnotation entity=itEnt.next();
					if(entityAnnotation.getStartOffset() == entity.getStartOffset() 
							&& entityAnnotation.getEndOffset() == entity.getEndOffset() 
							&& entityAnnotation.getClassAnnotation().equals(entity.getClassAnnotation())
							&& entityAnnotation.getAnnotationValue().equals(entity.getAnnotationValue())){
						entitiesInThatSide.add(entity);
						found = true;
					}
				}
				if(!found){
					throw new BioTMLException("The NER annotation of one relation is not present in the BioTML corpus file! Please review it.");
				}
			}
		}
		return entitiesInThatSide;
	}

	private List<IEventAnnotation> associatePairsInSameClue(List<IEventAnnotation> pairs){
		List<IEventAnnotation> relations = new ArrayList<IEventAnnotation>();
		while(!pairs.isEmpty()){
			IEventAnnotation relation = pairs.get(0);
			pairs.remove(0);
			if(!relation.getEntitiesAtLeft().isEmpty() && !relation.getEntitiesAtRight().isEmpty()){
				relations.add(relation);
			}else{
				for(int i= 0; i<pairs.size();i++){
					IEventAnnotation otherRelation = pairs.get(i);
					if(	relation.getStartOffset() == otherRelation.getStartOffset()
							&& relation.getEndOffset() == otherRelation.getEndOffset() 
							&&(
									(!relation.getEntitiesAtLeft().isEmpty()
											&& otherRelation.getEntitiesAtLeft().isEmpty())
											|| (relation.getEntitiesAtLeft().isEmpty()
													&& !otherRelation.getEntitiesAtLeft().isEmpty()))){
						List<IEntityAnnotation> leftEnt = new ArrayList<IEntityAnnotation>();
						leftEnt.addAll(relation.getEntitiesAtLeft());
						leftEnt.addAll(otherRelation.getEntitiesAtLeft());
						List<IEntityAnnotation> rightEnt = new ArrayList<IEntityAnnotation>();
						rightEnt.addAll(relation.getEntitiesAtRight());
						rightEnt.addAll(otherRelation.getEntitiesAtRight());
						relations.add(new EventAnnotationImpl(relation.getStartOffset(), relation.getEndOffset(), ProcessTypeEnum.RE.toString(), leftEnt, rightEnt, relation.getEventClue(), new EventPropertiesImpl(),false));
					}
				}
			}
		}
		return relations;
	}

}
