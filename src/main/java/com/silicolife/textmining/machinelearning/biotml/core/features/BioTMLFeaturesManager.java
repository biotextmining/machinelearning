package com.silicolife.textmining.machinelearning.biotml.core.features;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLFeatureGenerator;

/**
 * 
 * BioTMLFeaturesManager singleton to access features modules.
 * 
 * @since 1.0.0
 * @author Ruben Rodrigues ({@code rrodrigues@silicolife.com})
 */
public class BioTMLFeaturesManager {
	
	private static BioTMLFeaturesManager _instance;
	private Map<String, IBioTMLFeatureGenerator> nerFeaturesClassMap;
	private Set<String> nerRecomendedFeatures;
	private Map<String, IBioTMLFeatureGenerator> reFeaturesClassMap;
	private Set<String> reRecomendedFeatures;
	
	private BioTMLFeaturesManager() throws BioTMLException{
		this.nerFeaturesClassMap = new HashMap<String, IBioTMLFeatureGenerator>();
		this.nerRecomendedFeatures = new HashSet<String>();
		this.reFeaturesClassMap = new HashMap<String, IBioTMLFeatureGenerator>();
		this.reRecomendedFeatures = new HashSet<String>();
		loadFeatureModulesClasses();
	}
	
	/**
	 * 
	 * Gives access to the features manager instance.
	 * 
	 * @return Instance of features manager singleton.
	 * @throws BioTMLException 
	 */
	public static synchronized BioTMLFeaturesManager getInstance() throws BioTMLException{
		if (_instance == null) {
			BioTMLFeaturesManager.createInstance();
		}
		return _instance;
	}

	/**
	 * 
	 * Method that returns a map of feature name and the feature generator module that processes that feature.
	 * 
	 * @return Map of features name and features generators.
	 */
	public Map<String, IBioTMLFeatureGenerator> getNERFeaturesClassMap(){
		return nerFeaturesClassMap;
	}
	
	public Set<String> getNERRecomendedFeatures(){
		return nerRecomendedFeatures;
	}
	
	public Map<String, IBioTMLFeatureGenerator> getREFeaturesClassMap() {
		return reFeaturesClassMap;
	}

	public Set<String> getRERecomendedFeatures() {
		return reRecomendedFeatures;
	}

	/**
	 * 
	 * Method that makes the feature module generator registration in feature manager to be used.
	 * 
	 * @param featureClass - Feature module generator to be registrated.
	 * @throws BioTMLException
	 */
	public void registerFeaturesClass(IBioTMLFeatureGenerator featureClass) throws BioTMLException{
		getNERRecomendedFeatures().addAll(featureClass.getRecomendedNERFeatureIds());
		for(String uid : featureClass.getNERFeatureIds())
		{
			if(!getNERFeaturesClassMap().containsKey(uid)){
				getNERFeaturesClassMap().put(uid, featureClass);
			}
		}
		
		getRERecomendedFeatures().addAll(featureClass.getRecomendedREFeatureIds());
		for(String uid : featureClass.getREFeatureIds()){
			if(!getREFeaturesClassMap().containsKey(uid)){
				getREFeaturesClassMap().put(uid, featureClass);
			}
		}
	}
	
	/**
	 * Method that cleans the allocated memory of all modules. 
	 * (e.g. if the module loads a model file into the memory, that process removes that model from memory).
	 * 
	 * @throws BioTMLException
	 */
	public void cleanMemoryFeaturesClass() throws BioTMLException{
		for(String classUID : getNERFeaturesClassMap().keySet())
		{
			getNERClass(classUID).cleanMemory();
		}
	}

	/**
	 * 
	 * Method that gets the module feature generator that processes a feature.
	 * 
	 * @param classUID - Feature name.
	 * @return Feature module generator that processes that feature.
	 * @throws BioTMLException
	 */
	public IBioTMLFeatureGenerator getNERClass(String classUID) throws BioTMLException{
		if(getNERFeaturesClassMap().containsKey(classUID)){
			return getNERFeaturesClassMap().get(classUID);
		}
		throw new BioTMLException(9);
	}
	
	/**
	 * 
	 * Method that gets the module feature generator that processes a feature.
	 * 
	 * @param classUID - Feature name.
	 * @return Feature module generator that processes that feature.
	 * @throws BioTMLException
	 */
	public IBioTMLFeatureGenerator getREClass(String classUID) throws BioTMLException{
		if(getREFeaturesClassMap().containsKey(classUID)){
			return getREFeaturesClassMap().get(classUID);
		}
		throw new BioTMLException(9);
	}
	
	/**
	 * Creates the singleton instance.
	 * 
	 * @throws BioTMLException 
	 */
	private static void createInstance() throws BioTMLException{

		if (_instance == null) {
			_instance = new BioTMLFeaturesManager();
		}
	}
	
	private void loadFeatureModulesClasses() throws BioTMLException{
		ClassFinder finder = new ClassFinder();
		finder.addClassPath();
		ClassFilter filter =
	             new AndClassFilter
	                 // Must not be an interface
	                 (new NotClassFilter (new InterfaceOnlyClassFilter()),
	                 // Must implement the ClassFilter interface
	                 new SubclassClassFilter (IBioTMLFeatureGenerator.class),
	                 // Must not be abstract
	                 new NotClassFilter (new AbstractClassFilter()));
		Collection<ClassInfo> foundClasses = new ArrayList<ClassInfo>();
        finder.findClasses(foundClasses, filter);
        for(ClassInfo classInfo  : foundClasses){
        	try {
        		IBioTMLFeatureGenerator featureClass = (IBioTMLFeatureGenerator) ClassUtil.instantiateClass(classInfo.getClassName());
        		registerFeaturesClass(featureClass);
			} catch (ClassUtilException exc) {
				throw new BioTMLException(10,exc);
			}
        }
	}

}