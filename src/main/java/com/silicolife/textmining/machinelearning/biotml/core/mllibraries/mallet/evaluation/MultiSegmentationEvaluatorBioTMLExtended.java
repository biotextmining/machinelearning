package com.silicolife.textmining.machinelearning.biotml.core.mllibraries.mallet.evaluation;

import com.silicolife.textmining.machinelearning.biotml.core.evaluation.datastrucures.BioTMLConfusionMatrixImpl;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLConfusionMatrix;

import cc.mallet.fst.MultiSegmentationEvaluator;
import cc.mallet.fst.Transducer;
import cc.mallet.fst.TransducerTrainer;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import cc.mallet.types.Sequence;

public class MultiSegmentationEvaluatorBioTMLExtended extends MultiSegmentationEvaluator{

	private IBioTMLConfusionMatrix<Instance> confusionMatrix;

	public MultiSegmentationEvaluatorBioTMLExtended(InstanceList instanceList1, String description1,
			InstanceList instanceList2, String description2, InstanceList instanceList3, String description3,
			Object[] segmentStartTags, Object[] segmentContinueTags) {
		super(instanceList1, description1, instanceList2, description2, instanceList3, description3, segmentStartTags, segmentContinueTags);
		this.confusionMatrix = new BioTMLConfusionMatrixImpl<>();
	}
	
	public MultiSegmentationEvaluatorBioTMLExtended(InstanceList instanceList1, String description1,
			InstanceList instanceList2, String description2, Object[] segmentStartTags, Object[] segmentContinueTags) {
		super(instanceList1, description1, instanceList2, description2, segmentStartTags, segmentContinueTags);
		this.confusionMatrix = new BioTMLConfusionMatrixImpl<>();
	}
	
	public MultiSegmentationEvaluatorBioTMLExtended(InstanceList instanceList1, String description1, Object[] segmentStartTags, Object[] segmentContinueTags) {
		super(instanceList1, description1, segmentStartTags, segmentContinueTags);
		this.confusionMatrix = new BioTMLConfusionMatrixImpl<>();
	}

	public MultiSegmentationEvaluatorBioTMLExtended(InstanceList[] instanceLists, String[] instanceListDescriptions, Object[] segmentStartTags, Object[] segmentContinueTags) {
		super(instanceLists, instanceListDescriptions, segmentStartTags, segmentContinueTags);
		this.confusionMatrix = new BioTMLConfusionMatrixImpl<>();
	}
	
	public IBioTMLConfusionMatrix<Instance> getConfusionMatrix(){
		return confusionMatrix;
	}
	
	@Override
	@SuppressWarnings("rawtypes")
	public void evaluateInstanceList (TransducerTrainer tt, InstanceList data, String description){
		super.evaluateInstanceList(tt, data, description);
		Transducer model = tt.getTransducer();
	    for (int i = 0; i < data.size(); i++) {
	        Instance instance = data.get(i);
			Sequence input = (Sequence) instance.getData();
	        Sequence trueOutput = (Sequence) instance.getTarget();
	        assert (input.size() == trueOutput.size());
	        Sequence predOutput = model.transduce(input);
	        assert (predOutput.size() == trueOutput.size());
	        for(int j = 0; j < trueOutput.size(); j++)
	        	getConfusionMatrix().addPrediction(instance, predOutput.get(j).toString(), trueOutput.get(j).toString());
	    }
		
	}
}
