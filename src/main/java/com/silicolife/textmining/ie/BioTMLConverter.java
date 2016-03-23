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
import com.silicolife.textmining.core.datastructures.textprocessing.NormalizationForm;
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
import com.silicolife.textmining.ie.ner.biotml.NERBioTMLTagger;
import com.silicolife.textmining.ie.re.biotml.REBioTMLTagger;
import com.silicolife.textmining.machinelearning.biotml.core.BioTMLConstants;
import com.silicolife.textmining.machinelearning.biotml.core.corpora.BioTMLAnnotation;
import com.silicolife.textmining.machinelearning.biotml.core.corpora.BioTMLAnnotationsRelation;
import com.silicolife.textmining.machinelearning.biotml.core.corpora.BioTMLCorpus;
import com.silicolife.textmining.machinelearning.biotml.core.corpora.BioTMLDocument;
import com.silicolife.textmining.machinelearning.biotml.core.exception.BioTMLException;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLAnnotation;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLAnnotationsRelation;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLCorpus;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLDocument;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLSentence;
import com.silicolife.textmining.machinelearning.biotml.core.nlp.BioTMLNLPSystemsEnum;
import com.silicolife.textmining.machinelearning.biotml.core.nlp.clearnlp.BioTMLClearNLP;
import com.silicolife.textmining.machinelearning.biotml.core.nlp.opennlp.BioTMLOpenNLP;
import com.silicolife.textmining.machinelearning.biotml.core.nlp.stanford.BioTMLStanfordNLP;

public class BioTMLConverter {

	private IIEProcess process;
	private NERBioTMLTagger nertagger;
	private REBioTMLTagger retagger;
	private BioTMLNLPSystemsEnum nlpSystem;
	private boolean stop = false;

	public BioTMLConverter(BioTMLNLPSystemsEnum nlpSystem, IIEProcess process){
		this.process = process;
		this.nlpSystem = nlpSystem;
	}

	public BioTMLConverter(NERBioTMLTagger process, BioTMLNLPSystemsEnum nlpSystem){
		this.nertagger = process;
		this.process = process;
		this.nlpSystem = nlpSystem;
	}

	public BioTMLConverter(REBioTMLTagger process, BioTMLNLPSystemsEnum nlpSystem){
		this.retagger = process;
		this.process = process.getREconfiguration().getIEProcess();
		this.nlpSystem = nlpSystem;
	}

	private IIEProcess getProcess(){
		return process;
	}

	private NERBioTMLTagger getNERTagger(){
		return nertagger;
	}

	private REBioTMLTagger getRETagger(){
		return retagger;
	}

	private BioTMLNLPSystemsEnum getNLPSystem(){
		return nlpSystem;
	}

	public IBioTMLCorpus convertToBioTMLCorpus() throws ANoteException, BioTMLException{
		if(getProcess() != null){
			return convertAnoteCorpusWithProcess(getProcess(), getNLPSystem());
		}
		return null;
	}

	public void convertBioTMLCorpusToAnote(IBioTMLCorpus annotatedcorpus, INERProcessReport report) throws BioTMLException, ANoteException{
		if(getNERTagger().getType().getType().equals(ProcessTypeEnum.NER.toString())){
			annotateNERInAnote(annotatedcorpus, getNERTagger(), false, report);
		}
	}

	public void convertBioTMLCorpusToAnote(IBioTMLCorpus annotatedcorpus, IREProcessReport report) throws BioTMLException, ANoteException{
		if(getRETagger().getType().getType().equals(ProcessTypeEnum.RE.toString())){
			annotateREInAnote(annotatedcorpus, getRETagger(), false, true, report);
		}
	}

	public void stop(){
		stop = true;
	}

	private BioTMLCorpus convertAnoteCorpusWithProcess(IIEProcess baseProcess, BioTMLNLPSystemsEnum nlpSystem) throws ANoteException, BioTMLException {
		IDocumentSet docs = baseProcess.getCorpus().getArticlesCorpus();
		List<IBioTMLDocument> listDocuments = new ArrayList<IBioTMLDocument>();
		List<IBioTMLAnnotation> listAnnotations = new ArrayList<IBioTMLAnnotation>();
		List<IBioTMLAnnotationsRelation> listRelations = new ArrayList<IBioTMLAnnotationsRelation>();
		for(IPublication doc:docs){
			IAnnotatedDocument annotDoc = new AnnotatedDocumentImpl(doc, baseProcess, baseProcess.getCorpus());
			List<IBioTMLSentence> sentences = null;
			String text = annotDoc.getDocumentAnnotationText();
			text = text.replaceAll("\\p{C}", " ");
			if(nlpSystem == BioTMLNLPSystemsEnum.clearnlp){
				sentences = BioTMLClearNLP.getInstance().getSentences(text);
			}else if(nlpSystem == BioTMLNLPSystemsEnum.opennlp){
				sentences = BioTMLOpenNLP.getInstance().getSentences(text);
			}else if(nlpSystem == BioTMLNLPSystemsEnum.stanfordnlp){
				sentences = BioTMLStanfordNLP.getInstance().getSentences(text);
			}

			if(sentences == null){
				throw new BioTMLException("The document text wasn't tokenized by the nlp system!");
			}
			String title = (annotDoc.getTitle()!=null)?annotDoc.getTitle():new String();
			String extenalLinks = PublicationImpl.getPublicationExternalIDsStream(annotDoc);
			listDocuments.add(new BioTMLDocument(annotDoc.getId(), title, extenalLinks, sentences));
			long lastIndex = sentences.get(sentences.size()-1).getEndSentenceOffset();
			for(IEntityAnnotation entity : annotDoc.getEntitiesAnnotations()){
				String classType = entity.getClassAnnotation().getName();
				if(entity.getStartOffset()>lastIndex|| entity.getEndOffset()>lastIndex){
					throw new BioTMLException("The annotation offsets are bigger than the document size!");
				}
				listAnnotations.add(new BioTMLAnnotation(doc.getId(),classType, entity.getStartOffset(), entity.getEndOffset()));
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
						IBioTMLAnnotation clue = new BioTMLAnnotation(doc.getId(), BioTMLConstants.clue.toString(), event.getStartOffset(), event.getEndOffset());
						if(clue.getStartOffset() != clue.getEndOffset()){
							listAnnotations.add(clue);
							relation.add(clue);
						}
						addEventAnnotationsToBioTMLRelation(relation, rightannots, doc.getId());
						listRelations.add(new BioTMLAnnotationsRelation(relation));
					}

				}
			} 
		}

		if(!listRelations.isEmpty()){
			return new BioTMLCorpus(listDocuments, listAnnotations, listRelations, baseProcess.getCorpus().toString());
		}else{
			return new BioTMLCorpus(listDocuments, listAnnotations, baseProcess.getCorpus().toString());
		}
	}

	private void addEventAnnotationsToBioTMLRelation(Set<IBioTMLAnnotation> relation, List<IEntityAnnotation> annotationsInEvent, long docID){
		for(IEntityAnnotation entity : annotationsInEvent){
			relation.add(new BioTMLAnnotation(docID, entity.getClassAnnotation().getName(), entity.getStartOffset(), entity.getEndOffset()));
		}
	}



	private void annotateNERInAnote(IBioTMLCorpus annotatedCorpus, NERBioTMLTagger process, boolean loadCluesAsEntities, INERProcessReport report) throws BioTMLException, ANoteException{
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

	private void annotateREInAnote(IBioTMLCorpus annotatedCorpus, REBioTMLTagger process, boolean loadCluesAsEntities, boolean annotateOnlyPairsInSameClue, IREProcessReport report) throws BioTMLException, ANoteException{

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
					positions.addAnnotationWhitConflitsAndReplaceIfRangeIsMore(new AnnotationPosition((int)annotation.getStartOffset(), (int)annotation.getEndOffset()),new EntityAnnotationImpl(annotation.getStartOffset(), annotation.getEndOffset(), new AnoteClass(annotation.getAnnotType()), null, tokensString, NormalizationForm.getNormalizationForm(tokensString), null));
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

					positions.addAnnotationWhitConflitsAndReplaceIfRangeIsMore(new AnnotationPosition((int)annotation.getStartOffset(), (int)endoffset),new EntityAnnotationImpl( annotation.getStartOffset(), endoffset, new AnoteClass(annotation.getAnnotType()), null, token, NormalizationForm.getNormalizationForm(token), null));
				}else if(offset + (long)token.length() == annotation.getEndOffset()){
					positions.addAnnotationWhitConflitsAndReplaceIfRangeIsMore(new AnnotationPosition((int)offset, (int)annotation.getEndOffset()),new EntityAnnotationImpl( offset, annotation.getEndOffset(), new AnoteClass(annotation.getAnnotType()), null, token, NormalizationForm.getNormalizationForm(token), null));
				}else{
					positions.addAnnotationWhitConflitsAndReplaceIfRangeIsMore(new AnnotationPosition((int)offset, (int)endoffset), new EntityAnnotationImpl( offset, endoffset, new AnoteClass(annotation.getAnnotType()), null, token, NormalizationForm.getNormalizationForm(token), null));
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
					events.add(new EventAnnotationImpl(clue.getStartOffset(), clue.getEndOffset(), AnnotationType.re.name(), new ArrayList<>(leftEnt), new ArrayList<>(rightEnt), doc.toString().substring((int)clue.getStartOffset(), (int)clue.getEndOffset()), 0L, new String(), new EventPropertiesImpl()));
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
					events.add(new EventAnnotationImpl(1, 1, AnnotationType.re.name(), new ArrayList<>(leftEnt), new ArrayList<>(rightEnt), new String(), 0, new String(), new EventPropertiesImpl()));

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
				IEntityAnnotation entityAnnotation = new EntityAnnotationImpl(annotation.getStartOffset(), annotation.getEndOffset(), new AnoteClass(annotation.getAnnotType()), null, tokensString, NormalizationForm.getNormalizationForm(tokensString), null);
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
						relations.add(new EventAnnotationImpl(relation.getStartOffset(), relation.getEndOffset(), ProcessTypeEnum.RE.toString(), leftEnt, rightEnt, relation.getEventClue(), 0L, new String(), new EventPropertiesImpl()));
					}
				}
			}
		}
		return relations;
	}

}
