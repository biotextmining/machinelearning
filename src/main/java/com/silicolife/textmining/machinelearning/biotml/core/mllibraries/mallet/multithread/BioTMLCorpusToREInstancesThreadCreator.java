package com.silicolife.textmining.machinelearning.biotml.core.mllibraries.mallet.multithread;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;

import com.silicolife.textmining.machinelearning.biotml.core.BioTMLConstants;
import com.silicolife.textmining.machinelearning.biotml.core.corpora.otherdatastructures.BioTMLDocSentTokenIDs;
import com.silicolife.textmining.machinelearning.biotml.core.corpora.otherdatastructures.BioTMLTokensWithFeaturesAndLabels;
import com.silicolife.textmining.machinelearning.biotml.core.exception.BioTMLException;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLAnnotation;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLAnnotationsRelation;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLCorpus;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLCorpusToInstancesThreadCreator;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLDocument;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLFeatureGeneratorConfigurator;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLSentence;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLToken;
import com.silicolife.textmining.machinelearning.biotml.core.models.BioTMLREModelTypes;

public class BioTMLCorpusToREInstancesThreadCreator implements IBioTMLCorpusToInstancesThreadCreator{

	private IBioTMLCorpus corpus;
	private String annotType;
	private boolean stop = false;

	public BioTMLCorpusToREInstancesThreadCreator(IBioTMLCorpus corpus, String annotType){
		this.corpus = corpus;
		this.annotType = annotType;
	}
	
	private IBioTMLCorpus getCorpus() {
		return corpus;
	}

	private String getAnnotType() {
		return annotType;
	}
	
	@Override
	public void insertInstancesIntoExecutor(ExecutorService executor, IBioTMLFeatureGeneratorConfigurator configuration,
			InstanceListExtended instances) throws BioTMLException {
		for(IBioTMLDocument document : getCorpus().getDocuments()){
			int sentID = 0;
			for(IBioTMLSentence sentence : document.getSentences()){
				Set<IBioTMLAnnotation> annotations = getCorpus().getAnnotationsFromSentenceInDocumentId(document.getID(), sentence);
				if(getAnnotType().equals(BioTMLREModelTypes.entityclue.toString())
						|| getAnnotType().equals(BioTMLREModelTypes.entityclueonlyannotations.toString())){
					generateInstanceForREWithClues(executor, configuration, instances, document.getID(), sentID, sentence, annotations);
				}else if(getAnnotType().equals(BioTMLREModelTypes.entityentiy.toString())
						|| getAnnotType().equals(BioTMLREModelTypes.entityentiyonlyannotations.toString())){
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
			BioTMLTokensWithFeaturesAndLabels sentenceText = null;
			boolean onlyannotations = false;
			if(getAnnotType().equals(BioTMLREModelTypes.entityentiyonlyannotations.toString())){
				onlyannotations = true;
				sentenceText = sentenceToExportForREOnlyAnnotations(docID, sentence, annotation, annotations);
			}else{
				sentenceText = sentenceToExportForRE(docID, sentence, annotation);
			}
			if(sentenceText != null && !sentenceText.getTokens().isEmpty()){
				List<Integer> annotationIndexs = sentence.getTokenIndexsbyOffsets(annotation.getStartOffset(), annotation.getEndOffset());
				BioTMLDocSentTokenIDs ids = new BioTMLDocSentTokenIDs(docID, sentID, annotationIndexs.get(0), annotationIndexs.get(annotationIndexs.size()-1), onlyannotations);
				executor.execute(new CorpusSentenceAndFeaturesToInstanceThread(ids, sentenceText, instances, configuration));
			}
			if(stop)
				break;
		}
	}
	
	private void generateInstanceForREWithClues(ExecutorService executor, IBioTMLFeatureGeneratorConfigurator configuration,
			InstanceListExtended instances, long docID, int sentID, 
			IBioTMLSentence sentence, Set<IBioTMLAnnotation> annotations) throws BioTMLException{
		Set<IBioTMLAnnotation> clues = getClueAnnotations(annotations);
		for(IBioTMLAnnotation clue : clues){
			BioTMLTokensWithFeaturesAndLabels sentenceText = null;
			boolean onlyannotations = false;
			if(getAnnotType().equals(BioTMLREModelTypes.entityclueonlyannotations.toString())){
				onlyannotations = true;
				sentenceText = sentenceToExportForREOnlyAnnotations(docID, sentence, clue, annotations);
			}else{
				sentenceText = sentenceToExportForRE(docID, sentence, clue);
			}
			if(sentenceText != null && !sentenceText.getTokens().isEmpty()){
				List<Integer> annotationIndexs = sentence.getTokenIndexsbyOffsets(clue.getStartOffset(), clue.getEndOffset());
				BioTMLDocSentTokenIDs ids = new BioTMLDocSentTokenIDs(docID, sentID, annotationIndexs.get(0), annotationIndexs.get(annotationIndexs.size()-1), onlyannotations);
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
	
	private Set<IBioTMLAnnotation> getClueAnnotations(Set<IBioTMLAnnotation> annotations){
		Set<IBioTMLAnnotation> clues = new HashSet<>();
		for(IBioTMLAnnotation annotation : annotations){
			if(annotation.getAnnotType().equals(BioTMLConstants.clue.toString())){
				clues.add(annotation);
			}
			if(stop)
				break;
		}
		return clues;
	}
	
	private BioTMLTokensWithFeaturesAndLabels sentenceToExportForRE(long docID, IBioTMLSentence sentence, IBioTMLAnnotation annotation) throws BioTMLException{
		BioTMLTokensWithFeaturesAndLabels tokensWithLabels = new BioTMLTokensWithFeaturesAndLabels();
		for(IBioTMLToken token : sentence.getTokens()){
			if(getCorpus().getRelations() != null){
				if(!getCorpus().getRelations().isEmpty()){
					BioTMLConstants tokenLabel = getTokenLabelRelation(docID, token, annotation);
					tokensWithLabels.addTokenForModel(token.toString(), tokenLabel);
				}else{
					tokensWithLabels.addTokenForPrediction(token.toString());
				}
			}else{
				tokensWithLabels.addTokenForPrediction(token.toString());
			}

			if(stop)
				break;
		}
		return tokensWithLabels;
	}
	
	private BioTMLTokensWithFeaturesAndLabels sentenceToExportForREOnlyAnnotations(long docID, IBioTMLSentence sentence, IBioTMLAnnotation annotation, Set<IBioTMLAnnotation> annotations) throws BioTMLException{
		BioTMLTokensWithFeaturesAndLabels tokensWithLabels = new BioTMLTokensWithFeaturesAndLabels();
		for(IBioTMLToken token : sentence.getTokens()){
			BioTMLConstants isTokeninAnnots = null;
			if(getCorpus().isTokenInAnnotations(annotations, token)){
				isTokeninAnnots = BioTMLConstants.isAnnotation;
			}else{
				isTokeninAnnots = BioTMLConstants.isNotAnnotation;
			}
			if(getCorpus().getRelations() != null){
				if(!getCorpus().getRelations().isEmpty()){
					BioTMLConstants tokenLabel = getTokenLabelRelation(docID, token, annotation);
					tokensWithLabels.addTokenForModelAnnotationFiltering(token.toString(), tokenLabel, isTokeninAnnots);
				}else{
					tokensWithLabels.addTokenForPredictionAnnotationFiltering(token.toString(), isTokeninAnnots);
				}
			}else{
				tokensWithLabels.addTokenForPredictionAnnotationFiltering(token.toString(), isTokeninAnnots);
			}
			if(stop)
				break;
		}
		return tokensWithLabels;
	}

	private BioTMLConstants getTokenLabelRelation(long docID, IBioTMLToken token, IBioTMLAnnotation annotationOrClue){
		Set<IBioTMLAnnotationsRelation> docRelations = getCorpus().getDocAnnotationRelations(docID);
		if( !docRelations.isEmpty()){
			for(IBioTMLAnnotationsRelation relation : docRelations){
				if(relation.findAnnotationInRelation(annotationOrClue)){
					try {
						IBioTMLAnnotation tokenBelongsToAnAnnotation = relation.getAnnotationInRelationByOffsets(token.getStartOffset(), token.getEndOffset());
						if(!tokenBelongsToAnAnnotation.getAnnotType().equals(BioTMLConstants.clue.toString())
								&& !tokenBelongsToAnAnnotation.equals(annotationOrClue)){
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
