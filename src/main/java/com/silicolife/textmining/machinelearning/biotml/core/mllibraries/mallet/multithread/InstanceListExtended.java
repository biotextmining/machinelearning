package com.silicolife.textmining.machinelearning.biotml.core.mllibraries.mallet.multithread;

import java.util.Iterator;

import cc.mallet.pipe.Pipe;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import cc.mallet.types.SingleInstanceIterator;

/**
 * 
 * Mallet InstanceList with thread safety to be populated from a multi-threading pipe processing. 
 * 
 * @since 1.0.0
 * @author Ruben Rodrigues ({@code rrodrigues@silicolife.com})
 */

public class InstanceListExtended extends InstanceList{
	
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 * Initializes the InstanceList with a Mallet Pipe.
	 * 
	 * @param pipe - Mallet Pipe.
	 */
	
	public InstanceListExtended(Pipe pipe){
		super(pipe);
	}
	
	/**
	 * 
	 * Method that populates a InstanceList with thread safety passing a multi-threaded Mallet Pipe.
	 * 
	 */
	public void addThruPipe (Iterator<Instance> ii)
	{
		Iterator<Instance> pipedInstanceIterator = super.getPipe().newIteratorFrom(ii);
		while (pipedInstanceIterator.hasNext())
		{	
			Instance processedInstance = pipedInstanceIterator.next();
			synchronized(this){
				add(processedInstance);
			}	
		}
	}
	/**
	 * 
	 * Method to populate with a single Mallet instance.
	 * 
	 */
	public void addThruPipe(Instance inst)
	{
		this.addThruPipe(new SingleInstanceIterator(inst));
	}	
}