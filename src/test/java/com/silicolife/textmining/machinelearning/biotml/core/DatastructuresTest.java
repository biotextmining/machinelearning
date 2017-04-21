package com.silicolife.textmining.machinelearning.biotml.core;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import com.silicolife.textmining.machinelearning.biotml.core.corpora.BioTMLEntityImpl;
import com.silicolife.textmining.machinelearning.biotml.core.corpora.BioTMLAssociationImpl;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLEntity;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLAssociation;

public class DatastructuresTest {

	@Test
	public void test() {
		IBioTMLEntity A = new BioTMLEntityImpl(0, "test", 0, 2);
		IBioTMLEntity B = new BioTMLEntityImpl(0, "test", 2, 4);
		IBioTMLEntity Bcopy = new BioTMLEntityImpl(0, "test", 2, 4);
		IBioTMLEntity C = new BioTMLEntityImpl(0, "test", 4, 6);

		IBioTMLAssociation<IBioTMLEntity, IBioTMLEntity> AB = new BioTMLAssociationImpl<>(A, B);
		IBioTMLAssociation<IBioTMLEntity, IBioTMLEntity> BA = new BioTMLAssociationImpl<>(B, A);
		System.out.println(AB.equals(BA));

		IBioTMLAssociation<IBioTMLEntity, IBioTMLEntity> BBcopy = new BioTMLAssociationImpl<>(B,Bcopy);
		System.out.println(BBcopy.isValid());

		Set<IBioTMLAssociation<IBioTMLEntity, IBioTMLEntity>> associations =  new HashSet<>();
		associations.add(AB);
		associations.add(BA);
		System.out.println(associations.size());
		
		IBioTMLAssociation<IBioTMLEntity, IBioTMLEntity> AC = new BioTMLAssociationImpl<>(A,C);
		associations.add(AC);
		
		System.out.println(associations.size());

	}

}
