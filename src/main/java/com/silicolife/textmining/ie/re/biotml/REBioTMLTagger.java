package com.silicolife.textmining.ie.re.biotml;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import cc.mallet.pipe.Pipe;

import com.silicolife.textmining.core.datastructures.init.InitConfiguration;
import com.silicolife.textmining.core.datastructures.language.LanguageProperties;
import com.silicolife.textmining.core.datastructures.process.IEProcessImpl;
import com.silicolife.textmining.core.datastructures.process.ProcessOriginImpl;
import com.silicolife.textmining.core.datastructures.process.ProcessTypeImpl;
import com.silicolife.textmining.core.datastructures.report.processes.REProcessReportImpl;
import com.silicolife.textmining.core.datastructures.utils.GenerateRandomId;
import com.silicolife.textmining.core.datastructures.utils.Utils;
import com.silicolife.textmining.core.datastructures.utils.conf.GlobalNames;
import com.silicolife.textmining.core.datastructures.utils.conf.GlobalOptions;
import com.silicolife.textmining.core.interfaces.core.dataaccess.exception.ANoteException;
import com.silicolife.textmining.core.interfaces.core.report.processes.IREProcessReport;
import com.silicolife.textmining.core.interfaces.process.IProcessOrigin;
import com.silicolife.textmining.core.interfaces.process.IE.IREProcess;
import com.silicolife.textmining.ie.BioTMLConverter;
import com.silicolife.textmining.ie.re.biotml.configuration.IREBioTMLAnnotatorConfiguration;
import com.silicolife.textmining.machinelearning.biotml.core.BioTMLConstants;
import com.silicolife.textmining.machinelearning.biotml.core.annotator.BioTMLMalletAnnotator;
import com.silicolife.textmining.machinelearning.biotml.core.corpora.BioTMLCorpus;
import com.silicolife.textmining.machinelearning.biotml.core.exception.BioTMLException;
import com.silicolife.textmining.machinelearning.biotml.core.features.BioTMLFeaturesManager;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLAnnotation;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLCorpus;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLDocument;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLModel;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLModelReader;
import com.silicolife.textmining.machinelearning.biotml.reader.BioTMLModelReader;

public class REBioTMLTagger extends IEProcessImpl implements IREProcess{

	public static final String bioTMLTagger = "BioTML RE Tagger";
	public static final IProcessOrigin bioTMLOrigin= new ProcessOriginImpl(GenerateRandomId.generateID(),bioTMLTagger);
	private static int documentsStepSize = 10;// this int is responsible to load documents into memory... for best performance increase the number. However, memory usage will increase dramatically.
	private IREBioTMLAnnotatorConfiguration configuration;
	private boolean stop=false;
	private BioTMLMalletAnnotator annotator;
	private BioTMLConverter converter;

	public REBioTMLTagger(IREBioTMLAnnotatorConfiguration configuration){
		super(configuration.getCorpus(), 
				REBioTMLTagger.bioTMLTagger  + " " +Utils.SimpleDataFormat.format(new Date()),
				configuration.getProcessNotes(),
				ProcessTypeImpl.getREProcessType(),
				bioTMLOrigin, 
				gereateProperties(configuration));
		this.configuration = configuration;
		this.converter = new BioTMLConverter(this, configuration.getNLPSystem());
	}

	private static Properties gereateProperties(IREBioTMLAnnotatorConfiguration configuration){
		Properties prop = new Properties();
		prop.put(GlobalNames.entityBasedProcess, configuration.getIEProcess().toString());
		prop.put("Model File", configuration.getModelPath());
		return prop;
	}

	public IREBioTMLAnnotatorConfiguration getREconfiguration(){
		return configuration;
	}

	public IREProcessReport executeRE() throws ANoteException{
		try {	
			InitConfiguration.getDataAccess().createIEProcess(this);
			long startime = GregorianCalendar.getInstance().getTimeInMillis();
			IREProcessReport report = new REProcessReportImpl(LanguageProperties.getLanguageStream("pt.uminho.anote2.biotml.operation.report.title"), configuration.getIEProcess(),this, false);
			BioTMLConverter anoteconverter = new BioTMLConverter(this, configuration.getNLPSystem());
			IBioTMLCorpus biotmlCorpus = null;

			biotmlCorpus = anoteconverter.convertToBioTMLCorpus();
			if(biotmlCorpus!= null){

				if(!biotmlCorpus.getRelations().isEmpty()){
					biotmlCorpus = new BioTMLCorpus(biotmlCorpus.getDocuments(), biotmlCorpus.getAnnotations(), biotmlCorpus.toString());
				}

				IBioTMLModelReader modelreader = new BioTMLModelReader();
				List<String> submodelsFilename = new ArrayList<>();
				if(configuration.getModelPath().endsWith(".zip")){
					submodelsFilename = modelreader.loadSubmodelsToStringFromZipFile(configuration.getModelPath());
				}else if(configuration.getModelPath().endsWith(".gz")){
					submodelsFilename.add(configuration.getModelPath());
				}


				boolean foundCluesModel = false;
				if(submodelsFilename.size()==1){
					long startimeannotation = GregorianCalendar.getInstance().getTimeInMillis();
					executeAnnotation(report, biotmlCorpus, submodelsFilename, startimeannotation);
				}else{
					List<IBioTMLAnnotation> clues = new ArrayList<>();
					Iterator<String> itModelFilename = submodelsFilename.iterator();
					int i = 0;
					while(itModelFilename.hasNext() && !foundCluesModel && !stop){
						String filename = itModelFilename.next();
						IBioTMLModel model = modelreader.loadModelFromGZFile(filename);
						if(model.getModelConfiguration().getClassType().equals(BioTMLConstants.clue.toString())){
							foundCluesModel = true;
							clues = getNERClues(biotmlCorpus, model);
							model.cleanAlphabetMemory();
							model.cleanPipeMemory();
							submodelsFilename.remove(i);
						}
						i++;
					}
					if(foundCluesModel && !submodelsFilename.isEmpty() && !stop){
						List<IBioTMLAnnotation> allAnnotations = new ArrayList<>();
						allAnnotations.addAll(clues);
						allAnnotations.addAll(biotmlCorpus.getAnnotations());
						IBioTMLCorpus anotatedCorpus = new BioTMLCorpus(biotmlCorpus.getDocuments(), allAnnotations, biotmlCorpus.toString());
						long startimeannotation = GregorianCalendar.getInstance().getTimeInMillis();
						executeAnnotation(report, anotatedCorpus, submodelsFilename, startimeannotation);
					}else if(!foundCluesModel && !submodelsFilename.isEmpty() && !stop){
						long startimeannotation = GregorianCalendar.getInstance().getTimeInMillis();
						executeAnnotation(report, biotmlCorpus, submodelsFilename, startimeannotation);
					}
				}
				Runtime.getRuntime().gc();
				Pipe.cleanAllPipesFromMemory();
				BioTMLFeaturesManager.getInstance().cleanMemoryFeaturesClass();

			}
			if(stop)
			{
				annotator = null;
				converter=null;
				Runtime.getRuntime().gc();
				report.setcancel();
			}
			long endTime = GregorianCalendar.getInstance().getTimeInMillis();
			report.setTime(endTime-startime);
			return report;
		} catch (BioTMLException e) {
			throw new ANoteException(e);
		}
	}

	private List<IBioTMLAnnotation> getNERClues(IBioTMLCorpus biotmlCorpus, IBioTMLModel model) throws BioTMLException {
		List<IBioTMLAnnotation> clues = new ArrayList<>();
		List<IBioTMLDocument> documents = biotmlCorpus.getDocuments();
		Iterator<IBioTMLDocument> itDocuments = documents.iterator();
		while(itDocuments.hasNext() && !stop){
			List<IBioTMLDocument> documentsStep = getDocumentsInStep(itDocuments);
			annotator = new BioTMLMalletAnnotator(new BioTMLCorpus(documentsStep, new String()));
			IBioTMLCorpus anotatedCorpus = annotator.generateAnnotatedBioTMCorpus(model, configuration.getThreads());
			clues.addAll(anotatedCorpus.getAnnotations());
		}
		return clues;
	}

	private void executeAnnotation(IREProcessReport report, IBioTMLCorpus biotmlCorpus, List<String> submodelsFilename, long startimeannotation)throws BioTMLException, ANoteException {
		IBioTMLModelReader modelreader = new BioTMLModelReader();
		int counter = 0;
		List<IBioTMLDocument> documents = biotmlCorpus.getDocuments();
		int maxCounter = documents.size() * submodelsFilename.size();

		Iterator<String> itSubModelFilename = submodelsFilename.iterator();
		while(itSubModelFilename.hasNext() && !stop){
			String submodelFilename = itSubModelFilename.next();
			IBioTMLModel submodel = modelreader.loadModelFromGZFile(submodelFilename);
			counter = processDocumentsWithSubModel(report, biotmlCorpus, startimeannotation, counter, maxCounter, documents, submodel);
			submodel.cleanAlphabetMemory();
			submodel.cleanPipeMemory();
			submodel = null;
		}
		Runtime.getRuntime().gc();
	}

	private int processDocumentsWithSubModel(IREProcessReport report, IBioTMLCorpus biotmlCorpus, long startimeannotation, int counter, int maxCounter, List<IBioTMLDocument> documents, IBioTMLModel submodel) throws BioTMLException, ANoteException {
		Iterator<IBioTMLDocument> itDocuments = documents.iterator();
		while(itDocuments.hasNext() && !stop){
			List<IBioTMLDocument> documentsStep = getDocumentsInStep(itDocuments);
			List<IBioTMLAnnotation> reAnnotationsStep = getNERAnnots(biotmlCorpus, documentsStep);
			annotator = new BioTMLMalletAnnotator(new BioTMLCorpus(documentsStep, reAnnotationsStep, new String()));
			IBioTMLCorpus anotatedDocument = annotator.generateAnnotatedBioTMCorpus(submodel, configuration.getThreads());
			getConverter().convertBioTMLCorpusToAnote(anotatedDocument, report);
			annotator = null;
			counter = counter + documentsStep.size();
			increaseDocumentsInReport(report, documents, counter, documentsStep);
			memoryAndProgress(counter, maxCounter, startimeannotation);
		}
		return counter;
	}

	private void increaseDocumentsInReport(IREProcessReport report,
			List<IBioTMLDocument> documents, int counter,
			List<IBioTMLDocument> documentsStep) {
		if(counter<documents.size()){
			for(int i=0; i< documentsStep.size(); i++ ){
				report.incrementDocument();
			}
		}
	}

	private List<IBioTMLAnnotation> getNERAnnots(IBioTMLCorpus biotmlCorpus, List<IBioTMLDocument> documentsStep) {
		List<IBioTMLAnnotation> nerAnnots = new ArrayList<>();
		Iterator<IBioTMLDocument> itDocNERAnnotated = documentsStep.iterator();
		while(itDocNERAnnotated.hasNext() && !stop){
			IBioTMLDocument docWithNERAnnots = itDocNERAnnotated.next();
			nerAnnots.addAll(biotmlCorpus.getDocAnnotations(docWithNERAnnots.getID()));
		}
		return nerAnnots;
	}

	@Override
	public void stop() {
		this.stop =true;
		if(converter!= null){
			converter.stop();
		}
		if(annotator!=null){
			annotator.stopAnnotator();
		}
	}

	public BioTMLConverter getConverter(){
		return converter;
	}

	private List<IBioTMLDocument> getDocumentsInStep(
			Iterator<IBioTMLDocument> itDocuments) {
		List<IBioTMLDocument> documentsStep = new ArrayList<>();
		int step = 0;
		while(itDocuments.hasNext() && !stop && step< documentsStepSize){
			documentsStep.add(itDocuments.next());
			step++;
		}
		return documentsStep;
	}

	public void memoryAndProgress(int step, int total,long startime) {
		System.out.println((GlobalOptions.decimalformat.format((double)step/ (double) total * 100)) + " %...");
		Runtime.getRuntime().gc();
		System.out.println((Runtime.getRuntime().totalMemory()- Runtime.getRuntime().freeMemory())/(1024*1024) + " MB ");		
	}

}
