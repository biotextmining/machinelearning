package com.silicolife.textmining.ie.ner.biotml;

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
import com.silicolife.textmining.core.datastructures.report.processes.NERProcessReportImpl;
import com.silicolife.textmining.core.datastructures.utils.GenerateRandomId;
import com.silicolife.textmining.core.datastructures.utils.Utils;
import com.silicolife.textmining.core.datastructures.utils.conf.GlobalOptions;
import com.silicolife.textmining.core.interfaces.core.dataaccess.exception.ANoteException;
import com.silicolife.textmining.core.interfaces.core.document.corpus.ICorpus;
import com.silicolife.textmining.core.interfaces.core.report.processes.INERProcessReport;
import com.silicolife.textmining.core.interfaces.process.IProcessOrigin;
import com.silicolife.textmining.core.interfaces.process.IE.INERProcess;
import com.silicolife.textmining.ie.BioTMLConverter;
import com.silicolife.textmining.ie.ner.biotml.configuration.INERBioTMLAnnotatorConfiguration;
import com.silicolife.textmining.machinelearning.biotml.core.BioTMLConstants;
import com.silicolife.textmining.machinelearning.biotml.core.annotator.BioTMLMalletAnnotator;
import com.silicolife.textmining.machinelearning.biotml.core.corpora.BioTMLCorpus;
import com.silicolife.textmining.machinelearning.biotml.core.exception.BioTMLException;
import com.silicolife.textmining.machinelearning.biotml.core.features.BioTMLFeaturesManager;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLAnnotator;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLCorpus;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLDocument;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLModel;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLModelReader;
import com.silicolife.textmining.machinelearning.biotml.reader.BioTMLModelReader;

public class NERBioTMLTagger extends IEProcessImpl implements INERProcess{

	public static final String bioTMLTagger = "BioTML NER Tagger";
	public static final IProcessOrigin bioTMLOrigin= new ProcessOriginImpl(GenerateRandomId.generateID(),bioTMLTagger);
	private static int documentsStepSize = 10;// this int is responsible to load documents into memory... for best performance increase the number. However, memory usage will increase dramatically.
	private INERBioTMLAnnotatorConfiguration configuration;
	private boolean stop = false;
	private BioTMLConverter converter;
	private IBioTMLAnnotator annotator;

	public NERBioTMLTagger(INERBioTMLAnnotatorConfiguration configuration){
		super(configuration.getCorpus(), 
				NERBioTMLTagger.bioTMLTagger + " " +Utils.SimpleDataFormat.format(new Date()), 
				configuration.getNotes(),
				ProcessTypeImpl.getNERProcessType(),
				bioTMLOrigin, 
				gereateProperties(configuration));
		this.configuration = configuration;
		this.converter = new BioTMLConverter(this, configuration.getNLPSystem());
	}

	private static Properties gereateProperties(INERBioTMLAnnotatorConfiguration configuration){
		Properties prop = new Properties();
		prop.put("Model File", configuration.getModelPath());
		return prop;
	}

	public INERProcessReport executeCorpusNER(ICorpus corpus) throws ANoteException {
		try {
			InitConfiguration.getDataAccess().createIEProcess(this);
			long startime = GregorianCalendar.getInstance().getTimeInMillis();
			INERProcessReport report = new NERProcessReportImpl(LanguageProperties.getLanguageStream("pt.uminho.anote2.biotml.operation.report.title"), this);

			IBioTMLCorpus biotmlCorpus = getConverter().convertToBioTMLCorpus();

			IBioTMLModelReader modelreader = new BioTMLModelReader();
			List<String> submodelsFilename = new ArrayList<>();
			if(configuration.getModelPath().endsWith(".zip")){
				submodelsFilename = modelreader.loadSubmodelsToStringFromZipFile(configuration.getModelPath());
			}else if(configuration.getModelPath().endsWith(".gz")){
				submodelsFilename.add(configuration.getModelPath());
			}


			long startimeannotation = GregorianCalendar.getInstance().getTimeInMillis();
			List<IBioTMLDocument> documents = biotmlCorpus.getDocuments();
			int counter = 0;
			int maxCounter = documents.size() * submodelsFilename.size();

			Iterator<String> itSubModelFilename = submodelsFilename.iterator();
			while(itSubModelFilename.hasNext() && !stop){
				String submodelFilename = itSubModelFilename.next();
				IBioTMLModel submodel = modelreader.loadModelFromGZFile(submodelFilename);
				if(!configuration.getNERClasses().isEmpty() && !stop){
					if(submodel.getModelConfiguration().getIEType().equals(BioTMLConstants.ner.toString()) &&
							configuration.getNERClasses().contains(submodel.getModelConfiguration().getClassType())){
						counter = processDocumentsWithSubModel(report, startimeannotation, documents, counter, maxCounter, submodel);
					}

				}
				submodel.cleanAlphabetMemory();
				submodel.cleanPipeMemory();
			}
			Pipe.cleanAllPipesFromMemory();
			BioTMLFeaturesManager.getInstance().cleanMemoryFeaturesClass();
			Runtime.getRuntime().gc();

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

	private int processDocumentsWithSubModel(INERProcessReport report, long startimeannotation, List<IBioTMLDocument> documents, int counter, int maxCounter, IBioTMLModel submodel)
			throws BioTMLException, ANoteException {
		Iterator<IBioTMLDocument> itDocuments = documents.iterator();
		while(itDocuments.hasNext() && !stop){
			List<IBioTMLDocument> documentsStep = getDocumentsInStep(itDocuments);
			annotator = new BioTMLMalletAnnotator(new BioTMLCorpus(documentsStep, new String()));
			IBioTMLCorpus anotatedDocument = annotator.generateAnnotatedBioTMCorpus(submodel, configuration.getThreads());
			getConverter().convertBioTMLCorpusToAnote(anotatedDocument, report);
			annotator = null;
			counter = counter + documentsStep.size();
			increaseDocumentsInReport(report, documents, counter, documentsStep);
			memoryAndProgress(counter, maxCounter, startimeannotation);
		}
		return counter;
	}

	private void increaseDocumentsInReport(INERProcessReport report,
			List<IBioTMLDocument> documents, int counter,
			List<IBioTMLDocument> documentsStep) {
		if(counter<documents.size()){
			for(int i=0; i< documentsStep.size(); i++ ){
				report.incrementDocument();
			}
		}
	}


	private List<IBioTMLDocument> getDocumentsInStep(Iterator<IBioTMLDocument> itDocuments) {
		List<IBioTMLDocument> documentsStep = new ArrayList<>();
		int step = 0;
		while(itDocuments.hasNext() && !stop && step< documentsStepSize){
			documentsStep.add(itDocuments.next());
			step++;
		}
		return documentsStep;
	}

	@Override
	public void stop() {
		this.stop=true;
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

	public void memoryAndProgress(int step, int total,long startime) {
		System.out.println((GlobalOptions.decimalformat.format((double)step/ (double) total * 100)) + " %...");
		Runtime.getRuntime().gc();
		System.out.println((Runtime.getRuntime().totalMemory()- Runtime.getRuntime().freeMemory())/(1024*1024) + " MB ");		
	}

}
