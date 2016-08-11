package com.silicolife.textmining.machinelearning.biotml.reader;

import org.junit.Test;

import com.silicolife.textmining.machinelearning.biotml.core.exception.BioTMLException;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLModel;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLModelReader;
import com.silicolife.textmining.machinelearning.biotml.reader.BioTMLModelReaderImpl;

public class ReaderTest {

	@Test
	public void test() throws BioTMLException {
		String modelFile = "C:/Users/RRodrigues/Desktop/chemical_identifier.gz";
		IBioTMLModelReader reader = new BioTMLModelReaderImpl();
		IBioTMLModel model = reader.loadModelFromGZFile(modelFile);
		model.cleanAlphabetMemory();
		model.cleanPipeMemory();
	}

}
