package com.silicolife.textmining.machinelearning.biotml.core.mllibraries.mallet.multithread;

import java.util.List;
import java.util.concurrent.ExecutorService;

import com.silicolife.textmining.machinelearning.biotml.core.BioTMLConstants;
import com.silicolife.textmining.machinelearning.biotml.core.corpora.otherdatastructures.BioTMLDocSentTokenIDs;
import com.silicolife.textmining.machinelearning.biotml.core.corpora.otherdatastructures.BioTMLTokensWithFeaturesAndLabels;
import com.silicolife.textmining.machinelearning.biotml.core.exception.BioTMLException;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLAnnotation;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLCorpus;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLCorpusToInstancesThreadCreator;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLDocument;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLFeatureGeneratorConfigurator;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLSentence;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLToken;

public class BioTMLCorpusToNERInstancesThreadCreator implements IBioTMLCorpusToInstancesThreadCreator{
	
	private IBioTMLCorpus corpus;
	private String annotType;
	private boolean stop = false;

	public BioTMLCorpusToNERInstancesThreadCreator(IBioTMLCorpus corpus, String annotType){
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
	public void insertInstancesIntoExecutor(ExecutorService executor, IBioTMLFeatureGeneratorConfigurator configuration, InstanceListExtended instances) throws BioTMLException{
		for(IBioTMLDocument document : getCorpus().getDocuments()){
			int sentID = 0;
			for(IBioTMLSentence sentence : document.getSentences()){
				BioTMLTokensWithFeaturesAndLabels tokensWithLabels = sentenceToExportForNER(document.getID(), sentence);
				if(!tokensWithLabels.getTokens().isEmpty()){
					BioTMLDocSentTokenIDs ids = new BioTMLDocSentTokenIDs(document.getID(), sentID);
					executor.execute(new CorpusSentenceAndFeaturesToInstanceThread(ids, tokensWithLabels, instances, configuration));
				}
				sentID++;
				if(stop)
					break;
			}
			if(stop)
				break;
		}
	}
	
	@Override
	public void stopInsertion(){
		this.stop = true;
	}
	
	private BioTMLTokensWithFeaturesAndLabels sentenceToExportForNER(long docID, IBioTMLSentence sentence) throws BioTMLException{
		BioTMLTokensWithFeaturesAndLabels tokensWithLabels = new BioTMLTokensWithFeaturesAndLabels();
		for( IBioTMLToken token : sentence.getTokens()){
			if(getCorpus().getAnnotations()!= null){
				if(!getCorpus().getAnnotations().isEmpty()){
					BioTMLConstants tokenLabel = getTokenLabel(docID, token);
					tokensWithLabels.addTokenForModel(token.getToken().toString(), tokenLabel);
				}else{
					tokensWithLabels.addTokenForPrediction(token.getToken().toString());
				}
			}else{
				tokensWithLabels.addTokenForPrediction(token.getToken().toString());
			}
			if(stop)
				break;
		}
		return tokensWithLabels;
	}
	
	private BioTMLConstants getTokenLabel(long docID, IBioTMLToken token){
		List<IBioTMLAnnotation> docAnnotations = getCorpus().getDocAnnotations(docID);
		if(!docAnnotations.isEmpty()){
			for(IBioTMLAnnotation annotation : docAnnotations){
				if(token.getEndOffset()<annotation.getStartOffset()){
					return BioTMLConstants.o;
				}
				if(annotation.getAnnotType().equals(getAnnotType())){
					if(annotation.getStartOffset() == token.getStartOffset()){
						return BioTMLConstants.b;
					}
					if(annotation.getAnnotationOffsets().offsetsOverlap(token.getTokenOffsetsPair())){
						return BioTMLConstants.i;
					}
				}
				if(stop)
					break;
			}
		}
		return BioTMLConstants.o;
	}

}
