package com.silicolife.textmining.ie;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.silicolife.textmining.core.datastructures.annotation.AnnotationPosition;
import com.silicolife.textmining.core.datastructures.annotation.AnnotationPositions;
import com.silicolife.textmining.core.datastructures.annotation.re.EventAnnotationImpl;
import com.silicolife.textmining.core.datastructures.annotation.re.EventPropertiesImpl;
import com.silicolife.textmining.core.datastructures.documents.AnnotatedDocumentImpl;
import com.silicolife.textmining.core.datastructures.documents.PublicationImpl;
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
import com.silicolife.textmining.ie.utils.BioTMLConversionUtils;
import com.silicolife.textmining.machinelearning.biotml.core.BioTMLConstants;
import com.silicolife.textmining.machinelearning.biotml.core.corpora.BioTMLCorpusImpl;
import com.silicolife.textmining.machinelearning.biotml.core.exception.BioTMLException;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLEntity;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLCorpus;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLDocument;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLEvent;

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

	private IBioTMLCorpus convertAnoteCorpusWithProcess(IIEProcess baseProcess, String nlpSystem) throws ANoteException, BioTMLException {
		
		List<IBioTMLDocument> listDocuments = new ArrayList<IBioTMLDocument>();
		List<IBioTMLEntity> listAnnotations = new ArrayList<IBioTMLEntity>();
		List<IBioTMLEvent> listEvents = new ArrayList<IBioTMLEvent>();
		
		IDocumentSet docs = baseProcess.getCorpus().getArticlesCorpus();
		
		for(IPublication doc:docs){
			
			IAnnotatedDocument annotDoc = new AnnotatedDocumentImpl(doc, baseProcess, baseProcess.getCorpus());

			IBioTMLDocument biotlmDoc = BioTMLConversionUtils.convertPublication(annotDoc, nlpSystem);
			listDocuments.add(biotlmDoc);
			
			List<IBioTMLEntity> bioTMLAnnotations = BioTMLConversionUtils.convertEntityAnnotations(annotDoc.getEntitiesAnnotations(), doc.getId());
			validateAnnotations(biotlmDoc, bioTMLAnnotations);
			listAnnotations.addAll(bioTMLAnnotations);

			if(baseProcess.getType().getType().equals(ProcessTypeEnum.RE.toString()))
				listEvents.addAll(BioTMLConversionUtils.convertEventAnnotations(annotDoc.getEventAnnotations(),  doc.getId()));
		}

		return new BioTMLCorpusImpl(listDocuments, listAnnotations, listEvents, baseProcess.getCorpus().toString());
	}

	private void validateAnnotations(IBioTMLDocument biotlmDoc, List<IBioTMLEntity> bioTMLAnnotations)
			throws BioTMLException {
		long lastIndex = biotlmDoc.getSentences().get(biotlmDoc.getSentences().size()-1).getEndSentenceOffset();
		for(IBioTMLEntity biotmlAnnotation : bioTMLAnnotations){
			if(biotmlAnnotation.getStartOffset()>lastIndex|| biotmlAnnotation.getEndOffset()>lastIndex){
				throw new BioTMLException("The annotation offsets are bigger than the document size!");
			}
		}
	}

	private void annotateNERInAnote(IBioTMLCorpus annotatedCorpus, IIEProcess process, boolean loadCluesAsEntities, INERProcessReport report) throws BioTMLException, ANoteException{
		Iterator<IBioTMLDocument> idDoc = annotatedCorpus.getDocuments().iterator();
		while(idDoc.hasNext() && !stop){
			IBioTMLDocument doc = idDoc.next();
			List<IEntityAnnotation> entities = loadAllDocEntities(annotatedCorpus, doc,loadCluesAsEntities);
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

	private void annotateREInAnote(IBioTMLCorpus annotatedCorpus, IIEProcess process, boolean loadTriggersAsEntities, boolean annotateOnlyPairsInSameClue, IREProcessReport report) throws BioTMLException, ANoteException{

		IDocumentSet documents = process.getCorpus().getArticlesCorpus();
		Iterator<IBioTMLDocument> idDoc = annotatedCorpus.getDocuments().iterator();
		while(idDoc.hasNext() && !stop){
			IBioTMLDocument doc = idDoc.next();
			List<IEntityAnnotation> entities = loadAllDocEntities(annotatedCorpus, doc, loadTriggersAsEntities);
			List<IEventAnnotation> events = loadAllDocEvents(annotatedCorpus, doc, entities);
			if(annotateOnlyPairsInSameClue){
				events = joinEventsWithSameTrigger(events);
			}
			
			IPublication anoteDocument = documents.getDocument(doc.getID());
			InitConfiguration.getDataAccess().addProcessDocumentEntitiesAnnotations(process, anoteDocument, entities);
			InitConfiguration.getDataAccess().addProcessDocumentEventAnnoations(process, anoteDocument, events);
			report.incrementEntitiesAnnotated(entities.size());
			report.increaseRelations(events.size());
		}

	}

	private List<IEntityAnnotation> loadAllDocEntities(IBioTMLCorpus annotatedCorpus, IBioTMLDocument doc, boolean loadTriggersAsEntities) throws BioTMLException {
		AnnotationPositions positions = new AnnotationPositions();
		
		List<IBioTMLEntity> biotmlannotations = annotatedCorpus.getDocAnnotations(doc.getID());
		List<IEntityAnnotation> annotations = BioTMLConversionUtils.convertBioTMLAnnotations(biotmlannotations, doc);
		
		for(IEntityAnnotation annotation : annotations){
			if(!annotation.getAnnotationType().equals(BioTMLConstants.trigger.toString()))
				positions.addAnnotationWhitConflitsAndReplaceIfRangeIsMore(new AnnotationPosition((int)annotation.getStartOffset(), (int)annotation.getEndOffset()), annotation);
			else if(loadTriggersAsEntities && annotation.getAnnotationType().equals(BioTMLConstants.trigger.toString()))
				positions.addAnnotationWhitConflitsAndReplaceIfRangeIsMore(new AnnotationPosition((int)annotation.getStartOffset(), (int)annotation.getEndOffset()), annotation);
		}
		
		return positions.getEntitiesFromAnnoattionPositions();
	}

	private List<IEventAnnotation> loadAllDocEvents(IBioTMLCorpus annotatedCorpus, IBioTMLDocument doc, List<IEntityAnnotation> entities) throws BioTMLException {
		Set<IBioTMLEvent> biotmlEventset = annotatedCorpus.getDocEventsWithBestScore(doc.getID());
		List<IBioTMLEvent> biotmlEvents = new ArrayList<>();
		biotmlEvents.addAll(biotmlEventset);
		return BioTMLConversionUtils.convertBioTMLEventsWithEntityAnnotations(biotmlEvents, entities, doc);
	}

	private List<IEventAnnotation> joinEventsWithSameTrigger(List<IEventAnnotation> eventsToProcess){
		List<IEventAnnotation> events = new ArrayList<>();
		Map<AnnotationPosition, IEventAnnotation> triggerPositionToEvent = new HashMap<>();
		for(IEventAnnotation eventToProcess : eventsToProcess){
			if(eventToProcess.getStartOffset() == eventToProcess.getEndOffset())
				events.add(eventToProcess);
			else{
				AnnotationPosition triggerPosition = new AnnotationPosition((int)eventToProcess.getStartOffset(), (int)eventToProcess.getEndOffset());
				if(!triggerPositionToEvent.containsKey(triggerPosition)){
					triggerPositionToEvent.put(triggerPosition, eventToProcess);
				}else{
					IEventAnnotation event = triggerPositionToEvent.get(triggerPosition);
					List<IEntityAnnotation> leftEnt = new ArrayList<>();
					leftEnt.addAll(event.getEntitiesAtLeft());
					leftEnt.addAll(eventToProcess.getEntitiesAtLeft());
					List<IEntityAnnotation> rightEnt = new ArrayList<>();
					rightEnt.addAll(event.getEntitiesAtRight());
					rightEnt.addAll(event.getEntitiesAtRight());
					IEventAnnotation newEvent = new EventAnnotationImpl(event.getStartOffset(), event.getEndOffset(), ProcessTypeEnum.RE.toString(), leftEnt, rightEnt, event.getEventClue(), new EventPropertiesImpl(),false);
					triggerPositionToEvent.put(triggerPosition, newEvent);
				}
			}
		}
		return events;
	}

}
