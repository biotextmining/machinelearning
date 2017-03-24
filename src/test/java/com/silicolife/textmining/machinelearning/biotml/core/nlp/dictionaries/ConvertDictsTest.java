package com.silicolife.textmining.machinelearning.biotml.core.nlp.dictionaries;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;


public class ConvertDictsTest {

	@Test
	public void convertChemicalDictionary() throws IOException {
		Set<String> terms = new HashSet<String>();
		BufferedReader br = new BufferedReader(new FileReader("src/test/resources/elements.csv"));
		String line;
		while((line = br.readLine())!=null){
			if(line.startsWith("\"")){
				line = line.substring(1);
			}
			if(line.endsWith("\"")){
				line = line.substring(0,line.length()-1);
			}
			terms.add(line);
		}
		br.close();
		BufferedReader br2 = new BufferedReader(new FileReader("src/test/resources/synonyms.csv"));
		String line2;
		while((line2 = br2.readLine())!=null){
			if(line2.startsWith("\"")){
				line2 = line2.substring(1);
			}
			if(line2.endsWith("\"")){
				line2 = line2.substring(0,line2.length()-1);
			}
			terms.add(line2);
		}
		br2.close();
		
		String[] tosave = terms.toArray(new String[0]);
		Arrays.sort(tosave);
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("src/main/resources/biotmldatalists/chemical.list"));
		oos.writeObject(tosave);
		oos.close();
	}
	
	@Test
	public void convertMostFrequentGoogleTokens() throws IOException{
		//data obtained in http://norvig.com/mayzner.html at 22/05/2015 by http://norvig.com/google-books-common-words.txt url.
		//English Version 20120701 of google books processed by Peter Norvig. 
		//Norvig discarded any entry that used a character other than the 26 letters A-Z and any word with fewer than 100,000 mentions.
		//The google-books-common-words.txt contains 97565 tokens ordered by frequency.
		//To create a frequency list of most frequent words we use only 10k of frequent words.
		Set<String> terms = new HashSet<String>();
		BufferedReader br = new BufferedReader(new FileReader("src/test/resources/google-books-common-words.txt"));
		String line;
		int number = 0;
		int stopnumber = 10000;
		while((line = br.readLine())!=null && number<stopnumber){
			line = line.replaceAll("\\s|[0-9]", "").toLowerCase();
			terms.add(line);
			number++;
		}
		br.close();
		String[] tosave = terms.toArray(new String[0]);
		Arrays.sort(tosave);
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("src/main/resources/biotmldatalists/frequent.list"));
		oos.writeObject(tosave);
		oos.close();
	}
	
	@Test
	public void convertListClues() throws IOException{
		Set<String> terms = new HashSet<String>();
		BufferedReader br = new BufferedReader(new FileReader("src/test/resources/clues.txt"));
		String line;
		while((line = br.readLine())!=null){
			String[] tokens = line.split("\\s");
			for(String token : tokens){
				if(!token.isEmpty()){
					terms.add(token);
				}
			}
		}
		br.close();
		String[] tosave = terms.toArray(new String[0]);
		Arrays.sort(tosave);
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("src/main/resources/biotmldatalists/clues.list"));
		oos.writeObject(tosave);
		oos.close();
	}
	
	@Test
	public void convertListPositiveWords() throws IOException{
		Set<String> terms = new HashSet<String>();
		BufferedReader br = new BufferedReader(new FileReader("src/test/resources/positivewords.txt"));
		String line;
		while((line = br.readLine())!=null){
			String[] tokens = line.split("\\s");
			for(String token : tokens){
				if(!token.isEmpty()){
					terms.add(token);
				}
			}
		}
		br.close();
		String[] tosave = terms.toArray(new String[0]);
		Arrays.sort(tosave);
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("src/main/resources/biotmldatalists/positivewords.list"));
		oos.writeObject(tosave);
		oos.close();
	}
	
	@Test
	public void convertListNegativeWords() throws IOException{
		Set<String> terms = new HashSet<String>();
		BufferedReader br = new BufferedReader(new FileReader("src/test/resources/negativewords.txt"));
		String line;
		while((line = br.readLine())!=null){
			String[] tokens = line.split("\\s");
			for(String token : tokens){
				if(!token.isEmpty()){
					terms.add(token);
				}
			}
		}
		br.close();
		String[] tosave = terms.toArray(new String[0]);
		Arrays.sort(tosave);
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("src/main/resources/biotmldatalists/negativewords.list"));
		oos.writeObject(tosave);
		oos.close();
	}

}
