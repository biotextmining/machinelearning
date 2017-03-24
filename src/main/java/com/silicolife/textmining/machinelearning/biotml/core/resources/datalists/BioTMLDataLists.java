package com.silicolife.textmining.machinelearning.biotml.core.resources.datalists;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.Arrays;

import com.silicolife.textmining.machinelearning.biotml.core.nlp.opennlp.BioTMLOpenNLP;


/**
 * 
 * Singleton class to load BioTMLDataLists.
 * 
 * @since 1.0.0
 * @author Ruben Rodrigues ({@code rrodrigues@silicolife.com})
 */

public class BioTMLDataLists {
	
	private final static String datachemicalList = "biotmldatalists/chemical.list";
	private final static String datafrequentList = "biotmldatalists/frequent.list";
	private final static String datacluesList = "biotmldatalists/clues.list";
	private final static String dataPositiveWordsList = "biotmldatalists/positivewords.list";
	private final static String dataNegativeWordsList = "biotmldatalists/negativewords.list";
	
	private String[] chemicalList;
	private String[] frequentList;
	private String[] cluesList;
	private String[] positiveWordsList;
	private String[] negativeWordsList;
	
	private static BioTMLDataLists _instance;
	
	private BioTMLDataLists(){
	}
	
	/**
	 * Gives access to the BioTMLDictionaries instance
	 * @return OpenNLP instance.
	 */
	public static synchronized BioTMLDataLists getInstance() {
		if (_instance == null) {
			BioTMLDataLists.createInstance();
		}
		return _instance;
	}
	
	/**
	 * Creates the singleton instance.
	 */
	private static void createInstance(){

		if (_instance == null) {
			_instance = new BioTMLDataLists();
		}
	}
	
	private synchronized void initChemicalList() throws IOException, ClassNotFoundException{
		if(getChemicalList()==null){
			InputStream list = BioTMLOpenNLP.class.getClassLoader().getResourceAsStream(datachemicalList);
			ObjectInputStream ois = new ObjectInputStream(list);
			chemicalList =(String[]) ois.readObject();
			ois.close();
		}
	}
	
	private synchronized void initFrequentList() throws IOException, ClassNotFoundException{
		if(getFrequentList()==null){
			InputStream list = BioTMLOpenNLP.class.getClassLoader().getResourceAsStream(datafrequentList);
			ObjectInputStream ois = new ObjectInputStream(list);
			frequentList =(String[]) ois.readObject();
			ois.close();
		}
	}
	
	private synchronized void initCluesList() throws IOException, ClassNotFoundException{
		if(getFrequentList()==null){
			InputStream list = BioTMLOpenNLP.class.getClassLoader().getResourceAsStream(datacluesList);
			ObjectInputStream ois = new ObjectInputStream(list);
			cluesList =(String[]) ois.readObject();
			ois.close();
		}
	}
	
	private synchronized void initPositiveWordsList() throws IOException, ClassNotFoundException{
		if(getFrequentList()==null){
			InputStream list = BioTMLOpenNLP.class.getClassLoader().getResourceAsStream(dataPositiveWordsList);
			ObjectInputStream ois = new ObjectInputStream(list);
			positiveWordsList =(String[]) ois.readObject();
			ois.close();
		}
	}
	
	private synchronized void initNegativeWordsList() throws IOException, ClassNotFoundException{
		if(getFrequentList()==null){
			InputStream list = BioTMLOpenNLP.class.getClassLoader().getResourceAsStream(dataNegativeWordsList);
			ObjectInputStream ois = new ObjectInputStream(list);
			negativeWordsList =(String[]) ois.readObject();
			ois.close();
		}
	}
	
	private String[] getChemicalList(){
		return chemicalList;
	}
	
	private String[] getFrequentList(){
		return frequentList;
	}
	
	private String[] getCluesList(){
		return cluesList;
	}
	
	private String[] getPositiveWordsList(){
		return positiveWordsList;
	}
	
	private String[] getNegativeWordsList(){
		return negativeWordsList;
	}
	
	public boolean findStringInChemicalList(String text) throws ClassNotFoundException, IOException{
		if(getChemicalList() == null){
			initChemicalList();
		}
		return executeStringBinarySearch(getChemicalList(), text);
	}
	
	public boolean findStringInFrequentList(String text) throws ClassNotFoundException, IOException{
		if(getFrequentList() == null){
			initFrequentList();
		}
		return executeStringBinarySearch(getFrequentList(), text);
	}
	
	public boolean findStringInClueList(String text) throws ClassNotFoundException, IOException{
		if(getCluesList() == null){
			initCluesList();
		}
		return executeStringBinarySearch(getCluesList(), text);
	}
	
	public boolean findStringInPositiveWordsList(String text) throws ClassNotFoundException, IOException{
		if(getPositiveWordsList() == null){
			initPositiveWordsList();
		}
		return executeStringBinarySearch(getPositiveWordsList(), text);
	}
	
	public boolean findStringInNegativeWordsList(String text) throws ClassNotFoundException, IOException{
		if(getNegativeWordsList() == null){
			initNegativeWordsList();
		}
		return executeStringBinarySearch(getNegativeWordsList(), text);
	}
	
//	private String exectueStringOrSubstringBinarySearch(String[] sortedListofString, String stringToFind){
//		int result = Arrays.binarySearch(sortedListofString, stringToFind);
//		if(result >=0){
//			//found exact stringToFind in sortedListofString
//			return sortedListofString[result];
//		} else if (-result-1 < sortedListofString.length){
//			//Verify if the stringToFind starts with one entry in sortedListofString that is major than stringToFind.
//			//If so, we can say that is a substring present in sortedListofString.
//			if(sortedListofString[-result-1].startsWith(stringToFind)){
//				return sortedListofString[-result-1];
//			}
//		}
//		return new String();
//	}
	
	private boolean executeStringBinarySearch(String[] sortedListofString, String stringToFind){
		if(Arrays.binarySearch(sortedListofString, stringToFind) >=0){
			return true;
		}
		return false;
	}

}
