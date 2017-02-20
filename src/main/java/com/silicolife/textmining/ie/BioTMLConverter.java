package com.silicolife.textmining.ie;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
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
import com.silicolife.textmining.machinelearning.biotml.core.corpora.BioTMLAnnotationsRelationImpl;
import com.silicolife.textmining.machinelearning.biotml.core.corpora.BioTMLCorpusImpl;
import com.silicolife.textmining.machinelearning.biotml.core.corpora.BioTMLDocumentImpl;
import com.silicolife.textmining.machinelearning.biotml.core.exception.BioTMLException;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLAnnotation;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLAnnotationsRelation;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLCorpus;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLDocument;
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
		if(process.getType().equals(ProcessTypeEnum.RE.toString())){
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
		List<IBioTMLAnnotationsRelation> listRelations = new ArrayList<IBioTMLAnnotationsRelation>();
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
					Set<IBioTMLAnnotation> relation = new LinkedHashSet<IBioTMLAnnotation>();
					List<IEntityAnnotation> leftannots = new ArrayList<IEntityAnnotation>();
					for( IEntityAnnotation left : event.getEntitiesAtLeft()){
						leftannots.add(left);
					}
					List<IEntityAnnotation> rightannots = new ArrayList<IEntityAnnotation>();
					for( IEntityAnnotation right : event.getEntitiesAtRight()){
						rightannots.add(right);
					}
					if(!leftannots.isEmpty() && !rightannots.isEmpty()){
						addEventAnnotationsToBioTMLRelation(relation, leftannots, doc.getId());
						IBioTMLAnnotation clue = new BioTMLAnnotationImpl(doc.getId(), BioTMLConstants.clue.toString(), event.getStartOffset(), event.getEndOffset());
						if(clue.getStartOffset() != clue.getEndOffset()){
							listAnnotations.add(clue);
							relation.add(clue);
						}
						addEventAnnotationsToBioTMLRelation(relation, rightannots, doc.getId());
						listRelations.add(new BioTMLAnnotationsRelationImpl(relation));
					}

				}
			} 
		}

		if(!listRelations.isEmpty()){
			return new BioTMLCorpusImpl(listDocuments, listAnnotations, listRelations, baseProcess.getCorpus().toString());
		}else{
			return new BioTMLCorpusImpl(listDocuments, listAnnotations, baseProcess.getCorpus().toString());
		}
	}

	private void addEventAnnotationsToBioTMLRelation(Set<IBioTMLAnnotation> relation, List<IEntityAnnotation> annotationsInEvent, long docID){
		for(IEntityAnnotation entity : annotationsInEvent){
			relation.add(new BioTMLAnnotationImpl(docID, entity.getClassAnnotation().getName(), entity.getStartOffset(), entity.getEndOffset()));
		}
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
			if(loadCluesAsEntities || !annotation.getAnnotType().equals(BioTMLConstants.clue.toString())){
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
		Set<IBioTMLAnnotationsRelation> relations = annotatedCorpus.getDocAnnotationRelationsWithBestScore(doc.getID());
		List<IEventAnnotation> events = new ArrayList<IEventAnnotation>();
		for(IBioTMLAnnotationsRelation relation : relations){

			Set<IEntityAnnotation> leftEnt = new HashSet<IEntityAnnotation>();
			Set<IEntityAnnotation> rightEnt = new HashSet<IEntityAnnotation>();
			Set<IBioTMLAnnotation> leftAnnots = new HashSet<IBioTMLAnnotation>();
			Set<IBioTMLAnnotation> rightAnnots = new HashSet<IBioTMLAnnotation>();
			IBioTMLAnnotation clue = null;

			try{ clue = relation.getFirstAnnotationByType(BioTMLConstants.clue.toString()); }catch(BioTMLException e){};

			if(clue !=null){
				try{ leftAnnots = relation.getAnnotsAtLeftOfAnnotation(clue); }catch(BioTMLException e){}

				if(!leftAnnots.isEmpty()){
					leftEnt = loadEntitiesInRelations(doc, leftAnnots, entities);
				}

				try{ rightAnnots = relation.getAnnotsAtRightOfAnnotation(clue); }catch(BioTMLException e){}

				if(!rightAnnots.isEmpty()){
					rightEnt = loadEntitiesInRelations(doc, rightAnnots, entities);
				}
				if(!(leftEnt.isEmpty() && rightEnt.isEmpty())){
					events.add(new EventAnnotationImpl(clue.getStartOffset(), clue.getEndOffset(), AnnotationType.re.name(), new ArrayList<>(leftEnt), new ArrayList<>(rightEnt), doc.toString().substring((int)clue.getStartOffset(), (int)clue.getEndOffset()), new EventPropertiesImpl(),false));
				}
			}else{
				Set<IBioTMLAnnotation> relationToAdd = relation.getRelation();
				int relationsize = relationToAdd.size();
				Iterator<IBioTMLAnnotation> itRelation = relationToAdd.iterator();
				int i = 0;
				while(itRelation.hasNext()){
					if(i<relationsize/2){
						leftAnnots.add(itRelation.next());
					}else{
						rightAnnots.add(itRelation.next());
					}
					i++;
				}
				if(!leftAnnots.isEmpty()){
					leftEnt = loadEntitiesInRelations(doc, leftAnnots, entities);
				}
				if(!rightAnnots.isEmpty()){
					rightEnt = loadEntitiesInRelations(doc, rightAnnots, entities);
				}
				if(!(leftEnt.isEmpty() && rightEnt.isEmpty())){
					events.add(new EventAnnotationImpl(1, 1, AnnotationType.re.name(), new ArrayList<>(leftEnt), new ArrayList<>(rightEnt), new String(),new EventPropertiesImpl(),false));

				}
			}
		}
		return events;
	}

	private Set<IEntityAnnotation> loadEntitiesInRelations(IBioTMLDocument document, Set<IBioTMLAnnotation> annotationsInOneSide, List<IEntityAnnotation> entities) throws BioTMLException {
		Set<IEntityAnnotation> entitiesInThatSide = new HashSet<>();
		for(IBioTMLAnnotation annotation : annotationsInOneSide){
			if(!annotation.getAnnotType().equals(BioTMLConstants.clue.toString())){
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
