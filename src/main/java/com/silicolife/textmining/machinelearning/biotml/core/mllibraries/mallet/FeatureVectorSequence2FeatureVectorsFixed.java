package com.silicolife.textmining.machinelearning.biotml.core.mllibraries.mallet;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Iterator;
import java.util.List;

import com.silicolife.textmining.machinelearning.biotml.core.corpora.otherdatastructures.BioTMLDocSentIDs;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLAssociation;

import cc.mallet.pipe.Pipe;
import cc.mallet.types.FeatureVectorSequence;
import cc.mallet.types.Instance;
import cc.mallet.types.LabelSequence;

/** 
 * 
 * Given instances with a FeatureVectorSequence in the data field, break up the sequence into 
 * the individual FeatureVectors, producing one FeatureVector per Instance.
 * The hasNext method was fixed, the tokensequence counting was added to instance name and the source was inserted in the source instance.
 * 
 * @since 1.0.0
 * @author Ruben Rodrigues ({@code rrodrigues@silicolife.com})
 */
public class FeatureVectorSequence2FeatureVectorsFixed extends Pipe 
{

	final class FeatureVectorIterator implements Iterator<Instance> 
	{
		Iterator<Instance> superIterator;
		Instance superInstance;
		@SuppressWarnings("rawtypes")
		Iterator dataSubiterator, targetSubiterator;
		int count = 0;
		public FeatureVectorIterator (Iterator<Instance> inputIterator) {
			superInstance = inputIterator.next();
			dataSubiterator = ((FeatureVectorSequence)superInstance.getData()).iterator();
			targetSubiterator = ((LabelSequence)superInstance.getTarget()).iterator();
		}
		public Instance next () {
			if (!dataSubiterator.hasNext()) {
				assert (superIterator.hasNext());
				superInstance = superIterator.next();
				dataSubiterator = ((FeatureVectorSequence)superInstance.getData()).iterator();
				targetSubiterator = ((LabelSequence)superInstance.getTarget()).iterator();
			}
			// We are assuming sequences don't have zero length
			assert (dataSubiterator.hasNext());
			assert (targetSubiterator.hasNext());
			
			BioTMLDocSentIDs oldIDs = (BioTMLDocSentIDs)superInstance.getName();
			BioTMLDocSentIDs ids = new BioTMLDocSentIDs(oldIDs.getDocId(), oldIDs.getSentId());
			ids.setAnnotTokenRelationStartIndex(oldIDs.getAnnotTokenRelationStartIndex());
			ids.setAnnotTokenRelationEndIndex(oldIDs.getAnnotTokenRelationEndIndex());
			ids.setOnlyAnnotations(oldIDs.isOnlyAnnotations());
			@SuppressWarnings("rawtypes")
			List<IBioTMLAssociation> associations = oldIDs.getAssociations();
			if(!associations.isEmpty())
				ids.setAssociation(associations.get(count));
			ids.setAssociations(associations);
			ids.setTokenId(count++);
			return new Instance (dataSubiterator.next(), targetSubiterator.next(), ids, superInstance.getSource());

		}
		public boolean hasNext () {
			if(dataSubiterator != null){
				return dataSubiterator.hasNext();
			}
			if(superIterator != null){
				return superIterator.hasNext();
			}
			return false;
		}
		public void remove () { }
	}

	public FeatureVectorSequence2FeatureVectorsFixed() {}

	public Iterator<Instance> newIteratorFrom (Iterator<Instance> inputIterator) {
		return new FeatureVectorIterator (inputIterator);
	}

	// Serialization 
	private static final long serialVersionUID = 1;
	private static final int CURRENT_SERIAL_VERSION = 0;
	private void writeObject (ObjectOutputStream out) throws IOException {
		out.writeInt (CURRENT_SERIAL_VERSION);
	}
	private void readObject (ObjectInputStream in) throws IOException, ClassNotFoundException {
		@SuppressWarnings("unused")
		int version = in.readInt ();
	}

}
