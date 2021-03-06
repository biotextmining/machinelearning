package com.silicolife.textmining.machinelearning.ie.ner;

import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import com.silicolife.textmining.core.datastructures.exceptions.process.InvalidConfigurationException;
import com.silicolife.textmining.core.datastructures.init.exception.InvalidDatabaseAccess;
import com.silicolife.textmining.core.datastructures.process.ProcessRunStatusConfigurationEnum;
import com.silicolife.textmining.core.interfaces.core.dataaccess.exception.ANoteException;
import com.silicolife.textmining.core.interfaces.core.document.corpus.ICorpus;
import com.silicolife.textmining.core.interfaces.core.report.processes.INERProcessReport;
import com.silicolife.textmining.core.interfaces.process.IR.exception.InternetConnectionProblemException;
import com.silicolife.textmining.ie.ner.biotml.NERBioTMLTagger;
import com.silicolife.textmining.ie.ner.biotml.configuration.INERBioTMLAnnotatorConfiguration;
import com.silicolife.textmining.ie.ner.biotml.configuration.NERBioTMLAnnotatorConfiguration;
import com.silicolife.textmining.machinelearning.DatabaseConnectionInit;
import com.silicolife.textmining.machinelearning.processes.corpora.loaders.CreateCorpusFromPublicationManagerTest;

public class CreateNERSchemaFromBioTMLModelTest {

	@Test
	public void test() throws InvalidDatabaseAccess, ANoteException, InternetConnectionProblemException, InvalidConfigurationException {
		ICorpus corpusToAnnotate = createCorpus();
		String nlpSystemSelected = "clearnlp";
		Set<String> nerClasses = getNERClasses();
		int numberThreads = Runtime.getRuntime().availableProcessors();
		String biotmlModelFile = "src/test/resources/BioTMLModelTest.zip";
		INERBioTMLAnnotatorConfiguration nerTaggerConfiguration = 
				new NERBioTMLAnnotatorConfiguration(corpusToAnnotate,ProcessRunStatusConfigurationEnum.createnew,nlpSystemSelected,nerClasses,numberThreads,biotmlModelFile);
		NERBioTMLTagger tagger = new NERBioTMLTagger();
		System.out.println("Execute BioTML NER Tagger");
		INERProcessReport report = tagger.executeCorpusNER(nerTaggerConfiguration);
		assertTrue(report.isFinishing());
	}
	
	private ICorpus createCorpus() throws InvalidDatabaseAccess, ANoteException, InternetConnectionProblemException, InvalidConfigurationException{
		DatabaseConnectionInit.init("localhost","3306","createdatest","root","admin");
		return CreateCorpusFromPublicationManagerTest.createCorpus().getCorpus();
	}
	
	private Set<String> getNERClasses(){
		Set<String> classes = new HashSet<>();
		classes.add("Protein");
		classes.add("Gene");
		classes.add("Compound");
		return classes;
	}

}
