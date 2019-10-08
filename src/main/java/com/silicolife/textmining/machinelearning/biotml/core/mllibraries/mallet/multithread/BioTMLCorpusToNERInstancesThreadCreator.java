package com.silicolife.textmining.machinelearning.biotml.core.mllibraries.mallet.multithread;

import java.util.List;
import java.util.concurrent.ExecutorService;

import com.silicolife.textmining.machinelearning.biotml.core.BioTMLConstants;
import com.silicolife.textmining.machinelearning.biotml.core.BioTMLModelLabelType;
import com.silicolife.textmining.machinelearning.biotml.core.corpora.otherdatastructures.BioTMLObjectWithFeaturesAndLabels;
import com.silicolife.textmining.machinelearning.biotml.core.exception.BioTMLException;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLCorpus;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLCorpusToInstancesThreadCreator;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLDocument;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLEntity;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLFeatureGeneratorConfigurator;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLSentence;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLToken;

public class BioTMLCorpusToNERInstancesThreadCreator implements IBioTMLCorpusToInstancesThreadCreator{

	private IBioTMLCorpus corpus;
	private String annotType;
	private BioTMLModelLabelType modelLabelType;
	private boolean stop = false;

	public BioTMLCorpusToNERInstancesThreadCreator(IBioTMLCorpus corpus, String annotType, BioTMLModelLabelType modelLabelType){
		this.corpus = corpus;
		this.annotType = annotType;
		this.modelLabelType = modelLabelType;
	}

	private IBioTMLCorpus getCorpus() {
		return corpus;
	}

	private String getAnnotationType() {
		return annotType;
	}

	private BioTMLModelLabelType getModelLabelType(){
		return modelLabelType;
	}

	@Override
	public void insertInstancesIntoExecutor(ExecutorService executor, IBioTMLFeatureGeneratorConfigurator configuration, InstanceListExtended instances) throws BioTMLException{
		for(IBioTMLDocument document : getCorpus().getDocuments()){
			for(IBioTMLSentence sentence : document.getSentences()){
				BioTMLObjectWithFeaturesAndLabels<IBioTMLToken> tokensWithLabels = sentenceToExportForNER(document.getID(), sentence);
				if(!tokensWithLabels.getBioTMLObjects().isEmpty())
					executor.execute(new CorpusSentenceAndFeaturesToInstanceThread(document, tokensWithLabels, instances, configuration));
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

	private BioTMLObjectWithFeaturesAndLabels<IBioTMLToken> sentenceToExportForNER(long docID, IBioTMLSentence sentence) throws BioTMLException{
		BioTMLObjectWithFeaturesAndLabels<IBioTMLToken> tokensWithLabels = new BioTMLObjectWithFeaturesAndLabels<>(IBioTMLToken.class);
		for(IBioTMLToken token : sentence.getTokens()){
			if(getCorpus().getEntities()!= null){
				if(!getCorpus().getEntities().isEmpty()){
					BioTMLConstants tokenLabel = getTokenLabel(docID, token);
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

	private BioTMLConstants getTokenLabel(long docID, IBioTMLToken token){
		List<IBioTMLEntity> docAnnotations = getCorpus().getDocEntities(docID);
		if(!docAnnotations.isEmpty()){
			for(IBioTMLEntity annotation : docAnnotations){
				if(annotation.getAnnotationType().equals(getAnnotationType())){
					if(annotation.getStartOffset() == token.getStartOffset())
						return BioTMLConstants.b;

					if(annotation.getAnnotationOffsets().offsetsOverlap(token.getTokenOffsetsPair())
							&& getModelLabelType().equals(BioTMLModelLabelType.bio))
						return BioTMLConstants.i;
					if(annotation.getAnnotationOffsets().offsetsOverlap(token.getTokenOffsetsPair())
							&& getModelLabelType().equals(BioTMLModelLabelType.bo))
						return BioTMLConstants.b;
				}
				if(stop)
					break;
			}
		}
		return BioTMLConstants.o;
	}

}
