package com.silicolife.textmining.machinelearning.biotml.core;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import com.silicolife.textmining.machinelearning.biotml.core.corpora.BioTMLAnnotationImpl;
import com.silicolife.textmining.machinelearning.biotml.core.corpora.BioTMLAssociationImpl;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLAnnotation;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLAssociation;

public class DatastructuresTest {

	@Test
	public void test() {
		IBioTMLAnnotation A = new BioTMLAnnotationImpl(0, "test", 0, 2);
		IBioTMLAnnotation B = new BioTMLAnnotationImpl(0, "test", 2, 4);
		IBioTMLAnnotation Bcopy = new BioTMLAnnotationImpl(0, "test", 2, 4);
		IBioTMLAnnotation C = new BioTMLAnnotationImpl(0, "test", 4, 6);

		IBioTMLAssociation<IBioTMLAnnotation, IBioTMLAnnotation> AB = new BioTMLAssociationImpl<>(A, B);
		IBioTMLAssociation<IBioTMLAnnotation, IBioTMLAnnotation> BA = new BioTMLAssociationImpl<>(B, A);
		System.out.println(AB.equals(BA));

		IBioTMLAssociation<IBioTMLAnnotation, IBioTMLAnnotation> BBcopy = new BioTMLAssociationImpl<>(B,Bcopy);
		System.out.println(BBcopy.isValid());

		Set<IBioTMLAssociation<IBioTMLAnnotation, IBioTMLAnnotation>> associations =  new HashSet<>();
		associations.add(AB);
		associations.add(BA);
		System.out.println(associations.size());
		
		IBioTMLAssociation<IBioTMLAnnotation, IBioTMLAnnotation> AC = new BioTMLAssociationImpl<>(A,C);
		associations.add(AC);
		
		System.out.println(associations.size());

	}

}
