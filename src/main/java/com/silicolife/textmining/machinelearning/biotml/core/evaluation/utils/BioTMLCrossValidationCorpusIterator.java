package com.silicolife.textmining.machinelearning.biotml.core.evaluation.utils;

/**
 * 
 * Represents the cross-validation iterator of document from a gold corpus.
 * In each iteration an array of {@link IBioTMLCorpus} is obtained.
 * The array of {@link IBioTMLCorpus} has a size equal of 2.
 * The index 0 represents the corpus to be used as training data.
 * the index 1 represents the corpus to be used as testing data.
 * 
 * 
 * @since 1.0.0
 * @author Ruben Rodrigues ({@code rrodrigues@silicolife.com})
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.silicolife.textmining.machinelearning.biotml.core.corpora.BioTMLCorpusImpl;
import com.silicolife.textmining.machinelearning.biotml.core.evaluation.datastrucures.BioTMLCrossValidationFoldImpl;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLAnnotation;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLCorpus;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLCrossValidationFold;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLDocument;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLEvent;

public class BioTMLCrossValidationCorpusIterator implements Iterator<IBioTMLCrossValidationFold<IBioTMLCorpus>>{
	
	private IBioTMLCorpus corpus;
	private int numFolds;
	private List<IBioTMLCrossValidationFold<IBioTMLCorpus>> folds;
	private int index;

	/**
	 * 
	 * Initializes the iterator with a gold corpus and a given number of folds.
	 * 
	 * @param corpus - Gold {@link IBioTMLCorpus}.
	 * @param numFolds - Number of folds for CV.
	 */
	
	public BioTMLCrossValidationCorpusIterator(IBioTMLCorpus corpus, int numFolds, boolean shuffle){
		this.corpus = corpus;
		this.numFolds = numFolds;
		this.folds = getDocumentsByFolds(numFolds, shuffle);
		this.index = 0;
	}

	private IBioTMLCorpus getCorpus(){
		return corpus;
	}
	
	private int getNumFolds(){
		return numFolds;
	}
	
	private List<IBioTMLCrossValidationFold<IBioTMLCorpus>> getFolds(){
		return folds;
	}
	
	private int getIndex(){
		return index;
	}
	
	private List<IBioTMLCrossValidationFold<IBioTMLCorpus>> getDocumentsByFolds(int folds, boolean shufffle){
		if(folds<=0)
	          throw new IndexOutOfBoundsException("the number of folds must be greater than 0");
		if(folds>getCorpus().getDocuments().size())
			throw new IndexOutOfBoundsException("the number of folds must be less or equal to number of documents");
		List<IBioTMLCrossValidationFold<IBioTMLCorpus>> corpusFolds = new ArrayList<>();
		List<IBioTMLDocument> docListShuffled = getCorpus().getDocuments();
		if(shufffle)
			Collections.shuffle(docListShuffled);
		int foldSize = (int) Math.ceil(docListShuffled.size()/(double)folds);
		int fold = 0;
		boolean finished = false;
		while(!finished){
			int start = fold * foldSize;
	        int end = Math.min(start + foldSize, docListShuffled.size());
	        List<IBioTMLDocument> documentFold = docListShuffled.subList(start, end);
	        List<IBioTMLDocument> documentBigFold = new ArrayList<IBioTMLDocument>();
	        if(start>0){
	        	documentBigFold.addAll(docListShuffled.subList(0, start));
	        }
	        if(end<getCorpus().getDocuments().size()){
	        	documentBigFold.addAll(docListShuffled.subList(end, docListShuffled.size()));
	        }
	        IBioTMLCorpus trainingDataset = new BioTMLCorpusImpl(documentBigFold, getAnnotationsByDocList(documentBigFold), getRelationsByDocList(documentBigFold), getCorpus().toString());
			IBioTMLCorpus testingDataset = new BioTMLCorpusImpl(documentFold, getAnnotationsByDocList(documentFold), getRelationsByDocList(documentFold), getCorpus().toString());
			IBioTMLCrossValidationFold<IBioTMLCorpus> corpusFold = new BioTMLCrossValidationFoldImpl<IBioTMLCorpus>(trainingDataset, testingDataset);
	        corpusFolds.add(corpusFold);
	        if(end == docListShuffled.size()){
	        	finished = true;
	        	continue;
	        }
	        fold++;
		}
		return corpusFolds;
	}
	
	private List<IBioTMLAnnotation> getAnnotationsByDocList(List<IBioTMLDocument> documentFold){
		List<IBioTMLAnnotation> annotations = new ArrayList<IBioTMLAnnotation>();
		for(IBioTMLDocument doc : documentFold){
			List<IBioTMLAnnotation> docAnnot = getCorpus().getDocAnnotations(doc.getID());
			annotations.addAll(docAnnot);
		}
		return annotations;
	}
	
	private List<IBioTMLEvent> getRelationsByDocList(List<IBioTMLDocument> documentFold){
		List<IBioTMLEvent> relations = new ArrayList<IBioTMLEvent>();
		for(IBioTMLDocument doc : documentFold){
			Set<IBioTMLEvent> docAnnot = getCorpus().getDocAnnotationEvents(doc.getID());
			relations.addAll(docAnnot);
		}
		return relations;
	}

	public boolean hasNext() {
		if(getIndex()<getNumFolds()){
			return true;
		}
		return false;
	}

	public IBioTMLCrossValidationFold<IBioTMLCorpus> next() {
		IBioTMLCrossValidationFold<IBioTMLCorpus> res = getFolds().get(getIndex());
		index++;
		return res;
	}

	public void remove() {
		throw new UnsupportedOperationException ();
	}

}