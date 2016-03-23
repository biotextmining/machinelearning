package com.silicolife.textmining.machinelearning.biotml.reader;

import com.silicolife.textmining.machinelearning.biotml.core.exception.BioTMLException;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLModel;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLModelReader;

public class Read {

	public static void main(String[] args) throws BioTMLException {
		IBioTMLModelReader reader = new BioTMLModelReader();
		IBioTMLModel model = reader.loadConfigurationsModelFromGZFile("C:\\Users\\RRodrigues\\Desktop\\BioTML Models\\chemical_systematic.gz");
		System.out.println(model);
	}

}
