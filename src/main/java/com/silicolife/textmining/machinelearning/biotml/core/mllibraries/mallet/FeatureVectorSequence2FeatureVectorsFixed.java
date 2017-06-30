package com.silicolife.textmining.machinelearning.biotml.core.mllibraries.mallet;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Iterator;
import java.util.List;

import cc.mallet.pipe.Pipe;
import cc.mallet.types.FeatureVectorSequence;
import cc.mallet.types.Instance;
import cc.mallet.types.LabelSequence;

/** 
 * 
 * Given instances with a FeatureVectorSequence in the data field, break up the sequence into 
 * the individual FeatureVectors, producing one FeatureVector per Instance.
 * The hasNext method was fixed, the name object has list of objects to be iterated.
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
		Iterator dataSubiterator, targetSubiterator, nameSubIterator;
		int count = 0;
		public FeatureVectorIterator (Iterator<Instance> inputIterator) {
			superInstance = inputIterator.next();
			dataSubiterator = ((FeatureVectorSequence)superInstance.getData()).iterator();
			targetSubiterator = ((LabelSequence)superInstance.getTarget()).iterator();
			nameSubIterator = ((List<?>)superInstance.getName()).iterator();
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
			
			return new Instance (dataSubiterator.next(), targetSubiterator.next(), nameSubIterator.next(), superInstance.getSource());

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
