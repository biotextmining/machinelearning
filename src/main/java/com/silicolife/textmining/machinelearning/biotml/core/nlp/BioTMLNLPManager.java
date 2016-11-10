package com.silicolife.textmining.machinelearning.biotml.core.nlp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.clapper.util.classutil.AbstractClassFilter;
import org.clapper.util.classutil.AndClassFilter;
import org.clapper.util.classutil.ClassFilter;
import org.clapper.util.classutil.ClassFinder;
import org.clapper.util.classutil.ClassInfo;
import org.clapper.util.classutil.ClassUtil;
import org.clapper.util.classutil.ClassUtilException;
import org.clapper.util.classutil.InterfaceOnlyClassFilter;
import org.clapper.util.classutil.NotClassFilter;
import org.clapper.util.classutil.SubclassClassFilter;

import com.silicolife.textmining.machinelearning.biotml.core.exception.BioTMLException;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLNLP;

public class BioTMLNLPManager {
	
	private Map<String,IBioTMLNLP> mapNLPidNLP;
	
	private static BioTMLNLPManager _instance;

	private BioTMLNLPManager() throws BioTMLException
	{
		mapNLPidNLP = new HashMap<>();
		loadNLPClasses();
	}

	/**
	 * Gives access to the OpenNLP instance
	 * @return OpenNLP instance.
	 * @throws BioTMLException 
	 */
	public static synchronized BioTMLNLPManager getInstance() throws BioTMLException {
		if (_instance == null) {
			BioTMLNLPManager.createInstance();
		}
		return _instance;
	}

	/**
	 * Creates the singleton instance.
	 * @throws BioTMLException 
	 */
	private static void createInstance() throws BioTMLException{

		if (_instance == null) {
			_instance = new BioTMLNLPManager();
		}
	}
	
	public Collection<IBioTMLNLP> getAllNLPAvailable()
	{
		return mapNLPidNLP.values();
	}
	
	public IBioTMLNLP getNLPById(String id)
	{
		return mapNLPidNLP.get(id);
	}

	private void loadNLPClasses() throws BioTMLException{
		ClassFinder finder = new ClassFinder();
		finder.addClassPath();
		ClassFilter filter =
	             new AndClassFilter
	                 // Must not be an interface
	                 (new NotClassFilter (new InterfaceOnlyClassFilter()),
	                 // Must implement the ClassFilter interface
	                 new SubclassClassFilter (IBioTMLNLP.class),
	                 // Must not be abstract
	                 new NotClassFilter (new AbstractClassFilter()));
		Collection<ClassInfo> foundClasses = new ArrayList<ClassInfo>();
        finder.findClasses(foundClasses, filter);
        for(ClassInfo classInfo  : foundClasses){
        	try {
        		IBioTMLNLP featureClass = (IBioTMLNLP) ClassUtil.instantiateClass(classInfo.getClassName());
        		registerNLPClass(featureClass);
			} catch (ClassUtilException exc) {
				throw new BioTMLException(10,exc);
			}
        }
	}
	
	/**
	 * 
	 * Method that makes the nlp module registration in feature manager to be used.
	 * 
	 * @param featureClass - Feature module generator to be registrated.
	 * @throws BioTMLException
	 */
	public void registerNLPClass(IBioTMLNLP nlpClass) throws BioTMLException{
		mapNLPidNLP.put(nlpClass.getID(), nlpClass);
	}
}
