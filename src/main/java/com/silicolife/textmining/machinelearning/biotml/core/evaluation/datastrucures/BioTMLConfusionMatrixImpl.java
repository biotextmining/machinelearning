package com.silicolife.textmining.machinelearning.biotml.core.evaluation.datastrucures;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLConfusionMatrix;

public class BioTMLConfusionMatrixImpl<O> implements IBioTMLConfusionMatrix<O>{
	
	private static final long serialVersionUID = 1L;
	private List<String> labels;
	private Map<BioTMLConfusionMatrixIndex, List<O>> confusionMatrixIndexesToPredictedObjects;
	
	
	public BioTMLConfusionMatrixImpl(){
		this.labels = new ArrayList<>();
		this.confusionMatrixIndexesToPredictedObjects = new HashMap<>();
	}
	
	@Override
	public List<String> getLabels(){
		return labels;
	}
	
	@Override
	public Map<BioTMLConfusionMatrixIndex, List<O>> getConfusionMatrixIndexesToPredictedObjects(){
		return confusionMatrixIndexesToPredictedObjects;
	}
	
	@Override
	public void addPrediction(O predictionObject, String predictionLabel, String correctLabel){
		
		int predictedClassificationIndex = getLabelIndex(predictionLabel);
		int correctClassificationIndex = getLabelIndex(correctLabel);
		BioTMLConfusionMatrixIndex indexOnMatrix = new BioTMLConfusionMatrixIndex(predictedClassificationIndex, correctClassificationIndex);
		
		if(!getConfusionMatrixIndexesToPredictedObjects().containsKey(indexOnMatrix))
			getConfusionMatrixIndexesToPredictedObjects().put(indexOnMatrix, new ArrayList<>());
		
		List<O> predictedObjects = getConfusionMatrixIndexesToPredictedObjects().get(indexOnMatrix);
		predictedObjects.add(predictionObject);
		getConfusionMatrixIndexesToPredictedObjects().put(indexOnMatrix, predictedObjects);
	}
	
	@Override
	public List<O> getTruePositivesOfLabel(String label){
		int indexLabel = getLabelIndex(label);
		BioTMLConfusionMatrixIndex indexLabelOnMatrix = new BioTMLConfusionMatrixIndex(indexLabel, indexLabel);
		
		if(!getConfusionMatrixIndexesToPredictedObjects().containsKey(indexLabelOnMatrix))
			return new ArrayList<>();
		return getConfusionMatrixIndexesToPredictedObjects().get(indexLabelOnMatrix);
	}
	
	@Override
	public List<O> getTrueNegativesOfLabel(String label){
		int indexLabel = getLabelIndex(label);
		
		List<BioTMLConfusionMatrixIndex> trueNegativeIndexesOfMatrix = new ArrayList<>();
		
		for(int i=0; i<getLabels().size(); i++)
			for(int j=0; j<getLabels().size(); j++)
				if(!(i == indexLabel || j == indexLabel))
					trueNegativeIndexesOfMatrix.add(new BioTMLConfusionMatrixIndex(i,j));
		
		return getPredictionObjectsFromMatrixIndexes(trueNegativeIndexesOfMatrix);
	}
	
	@Override
	public List<O> getFalsePositivesOfLabel(String label){
		int predictedIndexLabel = getLabelIndex(label);
		
		List<BioTMLConfusionMatrixIndex> falsePositiveIndexesOfMatrix = new ArrayList<>();
		
		for(int correctIndexLabel=0; correctIndexLabel<getLabels().size(); correctIndexLabel++)
			if(correctIndexLabel != predictedIndexLabel)
				falsePositiveIndexesOfMatrix.add(new BioTMLConfusionMatrixIndex(predictedIndexLabel, correctIndexLabel));
		
		return getPredictionObjectsFromMatrixIndexes(falsePositiveIndexesOfMatrix);
	}
	
	@Override
	public List<O> getFalseNegativesOfLabel(String label){
		int correctIndexLabel = getLabelIndex(label);
		
		List<BioTMLConfusionMatrixIndex> falseNegativesIndexesOfMatrix = new ArrayList<>();
		
		for(int predictionIndexLabel=0; predictionIndexLabel<getLabels().size(); predictionIndexLabel++)
			if(predictionIndexLabel != correctIndexLabel)
				falseNegativesIndexesOfMatrix.add(new BioTMLConfusionMatrixIndex(predictionIndexLabel, correctIndexLabel));
		
		return getPredictionObjectsFromMatrixIndexes(falseNegativesIndexesOfMatrix);
	}
	
	public int[][] getConfusionMatrixCounting(){
		int[][] matrixCounting = new int[getLabels().size()][getLabels().size()];
		
		for(BioTMLConfusionMatrixIndex key : getConfusionMatrixIndexesToPredictedObjects().keySet())
			matrixCounting[key.getCorrectClassificationIndex()][key.getPredictedClassificationIndex()] = getConfusionMatrixIndexesToPredictedObjects().get(key).size();
		return matrixCounting;
	}
	
	private int getLabelIndex(String label){
		if(!getLabels().contains(label)){
			getLabels().add(label);
			return getLabels().size() - 1;
		}
		return getLabels().indexOf(label);
	}
	
	private List<O> getPredictionObjectsFromMatrixIndexes(List<BioTMLConfusionMatrixIndex> matrixIndexes){
		List<O> storedPredictionObjects = new ArrayList<>();
		
		for(BioTMLConfusionMatrixIndex matrixIndex : matrixIndexes)
			if(getConfusionMatrixIndexesToPredictedObjects().containsKey(matrixIndex))
				storedPredictionObjects.addAll(getConfusionMatrixIndexesToPredictedObjects().get(matrixIndex));
		
		return storedPredictionObjects;
	}

	@Override
	public String toString() {
		int[][] matrixCounting = getConfusionMatrixCounting();
		
		StringBuilder sb = new StringBuilder();
		sb.append("Columns are predictions! Rows are correct labels!\n\t\t");
		for(String label : getLabels())
			sb.append( label+"\t");
		sb.append("\n");
		
		for(int i=0; i<getLabels().size(); i++){
			sb.append(getLabels().get(i)+"\t");
			for(int j=0; j<getLabels().size(); j++)
				sb.append(matrixCounting[i][j]+"\t");
			sb.append("\n");
		}
		
		return sb.toString();
	}

}
