package com.silicolife.textmining.machinelearning.biotml.core.features.modules;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.silicolife.textmining.machinelearning.biotml.core.corpora.BioTMLOffsetsPairImpl;
import com.silicolife.textmining.machinelearning.biotml.core.exception.BioTMLException;
import com.silicolife.textmining.machinelearning.biotml.core.features.datastructures.BioTMLFeatureColumns;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLEntity;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLAssociation;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLFeatureColumns;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLFeatureGenerator;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLFeatureGeneratorConfigurator;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLOffsetsPair;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLToken;
import com.silicolife.textmining.machinelearning.biotml.core.nlp.nlp4j.BioTMLNLP4J;

import edu.emory.mathcs.nlp.component.template.feature.Field;
import edu.emory.mathcs.nlp.component.template.node.NLPNode;

/**
 * 
 * A class responsible for features from NLP4J.
 * 
 * @since 1.0.0
 * @author Ruben Rodrigues ({@code rrodrigues@silicolife.com})
 */
public class NLP4JFeatures implements IBioTMLFeatureGenerator{

	private String[] keywordsEssentiality = {"cell", "growth", "viability", "protein", "gene", "familiy", 
			"proliferation", "biosynthesis", "mitochondria", "ribossome", "life"};
	
	/**
	 * 
	 * Initializes the insertion of features from ClearNLP.
	 * 
	 */
	public  NLP4JFeatures(){
	}

	public Set<String> getNERFeatureIds() {
		Set<String> uids = new TreeSet<String>();
		uids.add("NLP4JLEMMA");
		uids.add("NLP4JPOS");
		uids.add("NLP4JDEPENDECY");//to be corrected for RE
		//		uids.add("CLEARNLPSRL");
		uids.add("CONJUCTNLP4JLEMMA");
		uids.add("CONJUCTNLP4JPOS");
		uids.add("WINDOWNLP4JLEMMA");
		uids.add("WINDOWNLP4JPOS");
		return uids;
	}

	public Set<String> getRecomendedNERFeatureIds(){
		Set<String> uids = new TreeSet<String>();
		uids.add("NLP4JLEMMA");
		uids.add("NLP4JPOS");
		uids.add("CONJUCTNLP4JLEMMA");
		return uids;
	}

	@Override
	public Map<String, String> getNERFeatureIdsInfos() {
		Map<String, String> infoMap = new HashMap<>();
		infoMap.put("NLP4JLEMMA", "The NLP4J lemmatization system is used to create a feature that stores the lemma of each token.");
		infoMap.put("NLP4JPOS", "The NLP4J part-of-speech system is used to create a feature that stores the POS of each token.");
		infoMap.put("NLP4JDEPENDECY", "The NLP4J dependency parsing system is used to create a feature that stores the dependecy label of each token.");
		infoMap.put("CONJUCTNLP4JLEMMA", "An adaptation of conjunctions from mallet is used to create conjunctions for NLP4J lemmatization features.");
		infoMap.put("CONJUCTNLP4JPOS", "An adaptation of conjunctions from mallet is used to create conjunctions for NLP4J part-of-speech features.");
		infoMap.put("WINDOWNLP4JLEMMA", "An adaptation of windows from mallet is used to create a 'Sliding window' for NLP4J lemmatization features.");
		infoMap.put("WINDOWNLP4JPOS", "An adaptation of windows from mallet is used to create a 'Sliding window' for NLP4J part-of-speech features.");
		return infoMap;
	}
	
	@Override
	public Set<String> getREFeatureIds() {
		Set<String> uids = new TreeSet<String>();
		uids.add("NLP4JLEMMA");
		uids.add("NLP4JPOS");
		uids.add("NLP4JBETWEENCONTAINSNOT");
		uids.add("NLP4JBETWEENCONTAINSFOR");
		uids.add("NLP4JBETWEENCONTAINSBY");
		uids.add("NLP4JBETWEENCONTAINSFROM");
		uids.add("NLP4JLEMMASAFTERFOR");
		uids.add("NLP4JLEMMASAFTERROLE");
		uids.add("NLP4JLEMMASAFTERIN");
		uids.add("NLP4JBETWEENVERB");
		uids.add("NLP4JOUTSIDEVERB");
		uids.add("NLP4JLASTVERBBEFOREANNOTS");
		uids.add("NLP4JDEPENDECY");
		uids.add("NLP4JPOSREPRESENTATIVE");
		uids.add("NLP4JDEPENDECYREPRESENTATIVE");
		uids.add("NLP4JDEPENDECYDISTANCE");
		uids.add("NLP4JGETKEYWORDSAFTERFOR");
		uids.add("NLP4JGETKEYWORDSBETWEENANNOTS");
		return uids;
	}

	@Override
	public Map<String, String> getREFeatureIdsInfos() {
		Map<String, String> infoMap = new HashMap<>();
		infoMap.put("NLP4JLEMMA", "The NLP4J lemmatization system is used to create a feature that associates lemmas for event annotations.");
		infoMap.put("NLP4JPOS", "The NLP4J part-of-speech system is used to create a feature that associates POS for event annotations.");
		infoMap.put("NLP4JBETWEENCONTAINSNOT", "Verifies if there is a 'not' lemma between event annotations.");
		infoMap.put("NLP4JBETWEENCONTAINSFOR", "Verifies if there is a 'for' lemma between event annotations.");
		infoMap.put("NLP4JBETWEENCONTAINSBY", "Verifies if there is a 'by' lemma between event annotations.");
		infoMap.put("NLP4JBETWEENCONTAINSFROM", "Verifies if there is a 'from' lemma between event annotations.");
		infoMap.put("NLP4JBETWEENVERB", "Gives the lemma verbs between annotation events.");
		infoMap.put("NLP4JLEMMASAFTERFOR", "Gives the lemma tokens after a 'for' lemma.");
		infoMap.put("NLP4JLEMMASAFTERROLE", "Gives the lemma tokens after a 'role' lemma.");
		infoMap.put("NLP4JLEMMASAFTERIN", "Gives the lemma tokens after a 'in' lemma.");
		infoMap.put("NLP4JOUTSIDEVERB", "Gives the lemma verbs ouside annotation events.");
		infoMap.put("NLP4JDEPENDECY", "The NLP4J dependency parsing system is used to create a feature that associates the dependecy label for event annotations.");
		infoMap.put("NLP4JPOSREPRESENTATIVE", "The NLP4J POS parsing system is used to create a feature that associates the POS label representative of each annotation in event.");
		infoMap.put("NLP4JDEPENDECYREPRESENTATIVE", "The NLP4J dependency parsing system is used to create a feature that associates the dependecy label representative of each annotation in event.");
		infoMap.put("NLP4JDEPENDECYDISTANCE", "The NLP4J dependency parsing system is used to create a feature that calculates the distance of annotations in dependency tree.");
		infoMap.put("NLP4JGETKEYWORDSAFTERFOR", "");
		infoMap.put("NLP4JGETKEYWORDSBETWEENANNOTS", "");
		return infoMap;
	}

	@Override
	public Set<String> getRecomendedREFeatureIds() {
		
		return new TreeSet<String>();
	}

	public IBioTMLFeatureColumns<IBioTMLToken> getFeatureColumns(List<IBioTMLToken> tokens, IBioTMLFeatureGeneratorConfigurator configuration) throws BioTMLException {

		if(tokens.isEmpty()){
			throw new BioTMLException(27);
		}

		IBioTMLFeatureColumns<IBioTMLToken> features = new BioTMLFeatureColumns<>(tokens, getNERFeatureIds(), configuration);
		
		Collections.sort(tokens);
		
		List<String> tokenStrings = new ArrayList<>();
		for(IBioTMLToken token:tokens)
			tokenStrings.add(token.getToken());
		
		if(configuration.hasFeatureUID("NLP4JLEMMA")){
			List<String> lemmas = BioTMLNLP4J.getInstance().processLemma(tokenStrings);
			for(String lemma : lemmas)
				features.addBioTMLObjectFeature("NLP4JLEMMA=" + lemma, "NLP4JLEMMA");
		}

		if(configuration.hasFeatureUID("NLP4JPOS")){
			List<String> poss = BioTMLNLP4J.getInstance().processPos(tokenStrings);
			for(String pos : poss)
				features.addBioTMLObjectFeature("NLP4JPOS=" + pos, "NLP4JPOS");
		}


		if(configuration.hasFeatureUID("NLP4JSRL")){
			//			features.addTokenFeature("Somthing", "NLP4JSRL");//TODO
		}

		if(configuration.hasFeatureUID("CONJUCTNLP4JLEMMA")){
			List<String> lemmas = new ArrayList<>();
			if(configuration.hasFeatureUID("NLP4JLEMMA"))
				lemmas = features.getFeatureColumByUID("NLP4JLEMMA");
			else
				lemmas = BioTMLNLP4J.getInstance().processLemma(tokenStrings);
			OffsetConjunctions conjuctions = new OffsetConjunctions(lemmas,  new int[][]{{-1, 0}, {-2, -1}, {0, 1}, {-1, 1}, {-3, -1}});
			features.updateBioTMLObjectFeatures(conjuctions.generateFeatures(), "CONJUCTNLP4JLEMMA");
			features.setUIDhasMultiFeatureColumn("CONJUCTNLP4JLEMMA");
		}

		if(configuration.hasFeatureUID("CONJUCTNLP4JPOS")){
			List<String> pos = new ArrayList<>();
			if(configuration.hasFeatureUID("NLP4JPOS"))
				pos = features.getFeatureColumByUID("NLP4JPOS");
			else
				pos = BioTMLNLP4J.getInstance().processPos(tokenStrings);
			OffsetConjunctions conjuctions = new OffsetConjunctions(pos,  new int[][]{{-1, 0}, {-2, -1}, {0, 1}, {-1, 1}, {-3, -1}});
			features.updateBioTMLObjectFeatures(conjuctions.generateFeatures(), "CONJUCTNLP4JPOS");
			features.setUIDhasMultiFeatureColumn("CONJUCTNLP4JPOS");
		}

		if(configuration.hasFeatureUID("WINDOWNLP4JLEMMA")){
			List<String> lemmas = new ArrayList<>();
			if(configuration.hasFeatureUID("NLP4JLEMMA"))
				lemmas = features.getFeatureColumByUID("NLP4JLEMMA");
			else
				lemmas = BioTMLNLP4J.getInstance().processLemma(tokenStrings);
			WindowFeatures windows = new WindowFeatures("WINDOW_LEMMA=", lemmas, -3, 3);
			features.updateBioTMLObjectFeatures(windows.generateFeatures(), "WINDOWNLP4JLEMMA");
			features.setUIDhasMultiFeatureColumn("WINDOWNLP4JLEMMA");
		}

		if(configuration.hasFeatureUID("WINDOWNLP4JPOS")){
			List<String> pos = new ArrayList<>();
			if(configuration.hasFeatureUID("NLP4JPOS"))
				pos = features.getFeatureColumByUID("NLP4JPOS");
			else
				pos = BioTMLNLP4J.getInstance().processPos(tokenStrings);
			WindowFeatures windows = new WindowFeatures("WINDOW_POS=", pos, -3, 3);
			features.updateBioTMLObjectFeatures(windows.generateFeatures(), "WINDOWNLP4JPOS");
			features.setUIDhasMultiFeatureColumn("WINDOWNLP4JPOS");
		}

		return features;
	}

	public void cleanMemory() {
		BioTMLNLP4J.getInstance().clearModelsInMemory();
	}

	@SuppressWarnings("rawtypes")
	@Override
	public IBioTMLFeatureColumns<IBioTMLAssociation> getEventFeatureColumns(List<IBioTMLToken> tokens, List<IBioTMLAssociation> associations,
			IBioTMLFeatureGeneratorConfigurator configuration) throws BioTMLException {

		IBioTMLFeatureColumns<IBioTMLAssociation> features = new BioTMLFeatureColumns<>(associations, getREFeatureIds(), configuration);
		
		
		List<String> tokenStrings = new ArrayList<>();
		for(IBioTMLToken token:tokens)
			tokenStrings.add(token.getToken());
		
		List<String> lemmas = BioTMLNLP4J.getInstance().processLemma(tokenStrings);
		List<String> poss = BioTMLNLP4J.getInstance().processPos(tokenStrings);
		NLPNode[] dependencyNodes = BioTMLNLP4J.getInstance().processDependency(tokenStrings);
		List<String> dependencyLabels = new ArrayList<>();
		for(int i=1; i< dependencyNodes.length; i++)
			dependencyLabels.add(dependencyNodes[i].getDependencyLabel());
		
		for(IBioTMLAssociation association : associations){
			if(association.getEntryOne() instanceof IBioTMLEntity && association.getEntryTwo() instanceof IBioTMLEntity){
				IBioTMLEntity annotationOne = (IBioTMLEntity) association.getEntryOne();
				IBioTMLEntity annotationTwo = (IBioTMLEntity) association.getEntryTwo();
				
				if(configuration.hasFeatureUID("NLP4JLEMMA"))
					features.addBioTMLObjectFeature("NLP4JLEMMA="+addAssociatedFeature(annotationOne, annotationTwo, tokens, lemmas), "NLP4JLEMMA");
				
				if(configuration.hasFeatureUID("NLP4JPOS"))
					features.addBioTMLObjectFeature("NLP4JPOS="+addAssociatedFeature(annotationOne, annotationTwo, tokens, poss), "NLP4JPOS");
				
				if(configuration.hasFeatureUID("NLP4JBETWEENCONTAINSNOT")){
					Boolean containsNot = containsFeature(annotationOne, annotationTwo, tokens, lemmas, "not");
					features.addBioTMLObjectFeature("NLP4JBETWEENCONTAINSNOT="+String.valueOf(containsNot), "NLP4JBETWEENCONTAINSNOT");
				}
				
				if(configuration.hasFeatureUID("NLP4JBETWEENCONTAINSFOR")){
					Boolean containsNot = containsFeature(annotationOne, annotationTwo, tokens, lemmas, "for");
					features.addBioTMLObjectFeature("NLP4JBETWEENCONTAINSFOR="+String.valueOf(containsNot), "NLP4JBETWEENCONTAINSFOR");
				}
				
				if(configuration.hasFeatureUID("NLP4JBETWEENCONTAINSBY")){
					Boolean containsBy = containsFeature(annotationOne, annotationTwo, tokens, lemmas, "by");
					features.addBioTMLObjectFeature("NLP4JBETWEENCONTAINSBY="+String.valueOf(containsBy), "NLP4JBETWEENCONTAINSBY");
				}
				if(configuration.hasFeatureUID("NLP4JBETWEENCONTAINSFROM")){
					Boolean containsfrom = containsFeature(annotationOne, annotationTwo, tokens, lemmas, "from");
					features.addBioTMLObjectFeature("NLP4JBETWEENCONTAINSFROM="+String.valueOf(containsfrom), "NLP4JBETWEENCONTAINSFROM");
				}
				
				if(configuration.hasFeatureUID("NLP4JBETWEENVERB"))
					features.addBioTMLObjectFeature("NLP4JBETWEENVERB="+verbBetween(annotationOne, annotationTwo, tokens, poss, lemmas), "NLP4JBETWEENVERB");
				
				if(configuration.hasFeatureUID("NLP4JOUTSIDEVERB"))
					features.addBioTMLObjectFeature("NLP4JOUTSIDEVERB="+verbOutside(annotationOne, annotationTwo, tokens, poss, lemmas), "NLP4JOUTSIDEVERB");
				
				if(configuration.hasFeatureUID("NLP4JLASTVERBBEFOREANNOTS"))
					features.addBioTMLObjectFeature("NLP4JLASTVERBBEFOREANNOTS="+lastverbBeforeAnnotations(annotationOne, annotationTwo, tokens, poss, lemmas), "NLP4JLASTVERBBEFOREANNOTS");
				
				if(configuration.hasFeatureUID("NLP4JLEMMASAFTERFOR"))
					features.addBioTMLObjectFeature("NLP4JLEMMASAFTERFOR="+getTokensAfterLastAnnotationsAndTokenLemma(annotationOne, annotationTwo, tokens, lemmas, "for"),"NLP4JLEMMASAFTERFOR");
				
				if(configuration.hasFeatureUID("NLP4JLEMMASAFTERROLE"))
					features.addBioTMLObjectFeature("NLP4JLEMMASAFTERROLE="+getTokensAfterLastAnnotationsAndTokenLemma(annotationOne, annotationTwo, tokens, lemmas, "role"),"NLP4JLEMMASAFTERROLE");
				
				if(configuration.hasFeatureUID("NLP4JLEMMASAFTERIN"))
					features.addBioTMLObjectFeature("NLP4JLEMMASAFTERIN="+getTokensAfterLastAnnotationsAndTokenLemma(annotationOne, annotationTwo, tokens, lemmas, "in"),"NLP4JLEMMASAFTERIN");
				
				if(configuration.hasFeatureUID("NLP4JDEPENDECY"))
					features.addBioTMLObjectFeature("NLP4JDEPENDECY="+addAssociatedFeature(annotationOne, annotationTwo, tokens, dependencyLabels), "NLP4JDEPENDECY");
				
				if(configuration.hasFeatureUID("NLP4JPOSREPRESENTATIVE"))
					features.addBioTMLObjectFeature("NLP4JPOSREPRESENTATIVE="+associationFeaturesUsingLCATokensAnnotation(annotationOne, annotationTwo, tokens, dependencyNodes, "NLP4JPOSREPRESENTATIVE"), "NLP4JPOSREPRESENTATIVE");
				
				if(configuration.hasFeatureUID("NLP4JDEPENDECYREPRESENTATIVE"))
					features.addBioTMLObjectFeature("NLP4JDEPENDECYREPRESENTATIVE="+associationFeaturesUsingLCATokensAnnotation(annotationOne, annotationTwo, tokens, dependencyNodes, "NLP4JDEPENDECYREPRESENTATIVE"), "NLP4JDEPENDECYREPRESENTATIVE");
				
				if(configuration.hasFeatureUID("NLP4JDEPENDECYDISTANCE")){
					NLPNode annotationOneNode = getCommonAnnotationsTokensNLPNode(annotationOne, dependencyNodes, tokens);
					NLPNode annotationTwoNode = getCommonAnnotationsTokensNLPNode(annotationTwo, dependencyNodes, tokens);
					String distancePath = annotationOneNode.getPath(annotationTwoNode, Field.distance);
					features.addBioTMLObjectFeature("NLP4JDEPENDECYDISTANCE="+distancePath, "NLP4JDEPENDECYDISTANCE");
				}
					
				
				if(configuration.hasFeatureUID("NLP4JGETKEYWORDSAFTERFOR")){
					String result = new String();
					List<String> listLemmas = getLemmasAfterLemaPoint(annotationOne, annotationTwo, tokens, lemmas, "for");
					for(String keyword : keywordsEssentiality){
						if(listLemmas.contains(keyword)){
							if(!result.isEmpty())
								result = result + "\t";
							result = result + "NLP4JGETKEYWORDSAFTERFOR="+keyword;
						}
					}
					features.addBioTMLObjectFeature(result, "NLP4JGETKEYWORDSAFTERFOR");
				}
				
				if(configuration.hasFeatureUID("NLP4JGETKEYWORDSBETWEENANNOTS")){
					String result = new String();
					List<String> listLemmas = getLemmasBetweenAnnotations(annotationOne, annotationTwo, tokens, lemmas);
					for(String keyword : keywordsEssentiality){
						if(listLemmas.contains(keyword)){
							if(!result.isEmpty())
								result = result + "\t";
							result = result + "NLP4JGETKEYWORDSBETWEENANNOTS="+keyword;
						}
					}
					features.addBioTMLObjectFeature(result, "NLP4JGETKEYWORDSBETWEENANNOTS");
				}
			}else if(association.getEntryOne() instanceof IBioTMLEntity && association.getEntryTwo() instanceof IBioTMLAssociation){
				//TODO
			}else if(association.getEntryOne() instanceof IBioTMLAssociation && association.getEntryTwo() instanceof IBioTMLEntity){
				//TODO
			}else if(association.getEntryOne() instanceof IBioTMLAssociation && association.getEntryTwo() instanceof IBioTMLAssociation){
				//TODO
			}
		}
		
		return features;
	}
	
	private String addAssociatedFeature(IBioTMLEntity annotationOne, IBioTMLEntity annotationTwo, List<IBioTMLToken> tokens, List<String> features) {
		String result = new String();
		for(int i=0; i<tokens.size(); i++){
			if(annotationOne.getAnnotationOffsets().offsetsOverlap(tokens.get(i).getTokenOffsetsPair())){
				if(!result.isEmpty())
					result = result +"__&__";
				result = result+ features.get(i);
			}
			if(annotationTwo.getAnnotationOffsets().offsetsOverlap(tokens.get(i).getTokenOffsetsPair())){
				if(!result.isEmpty())
					result = result +"__&__";
				result = result+ features.get(i);
			}
		}
		return result;
	}
	
	private Boolean containsFeature(IBioTMLEntity annotationOne, IBioTMLEntity annotationTwo, List<IBioTMLToken> tokens, List<String> lemmas, String contains){
		IBioTMLOffsetsPair offsetspair = null;
		if(annotationOne.compareTo(annotationTwo)<0){
			offsetspair = new BioTMLOffsetsPairImpl(annotationOne.getStartOffset(), annotationTwo.getEndOffset());
		}else{
			offsetspair = new BioTMLOffsetsPairImpl(annotationTwo.getStartOffset(), annotationOne.getEndOffset());
		}
		for(int i=0; i<tokens.size(); i++){
			if(offsetspair.offsetsOverlap(tokens.get(i).getTokenOffsetsPair()) && lemmas.get(i).equals(contains)){
				return true;
			}
		}
		return false;

	}
	
	private String verbBetween(IBioTMLEntity annotationOne, IBioTMLEntity annotationTwo, List<IBioTMLToken> tokens, List<String> pos, List<String> lemmas){
		String verbString = new String();
		IBioTMLOffsetsPair offsetspair = null;
		if(annotationOne.compareTo(annotationTwo)<0)
			offsetspair = new BioTMLOffsetsPairImpl(annotationOne.getStartOffset(), annotationTwo.getEndOffset());
		else
			offsetspair = new BioTMLOffsetsPairImpl(annotationTwo.getStartOffset(), annotationOne.getEndOffset());
		
		for(int i=0; i<tokens.size(); i++){
			if(offsetspair.offsetsOverlap(tokens.get(i).getTokenOffsetsPair()) && pos.get(i).startsWith("VB")){
				if(!verbString.isEmpty())
					verbString = verbString + " ";
				verbString = verbString + lemmas.get(i);
			}
		}
		return verbString;

	}
	
	private String lastverbBeforeAnnotations(IBioTMLEntity annotationOne, IBioTMLEntity annotationTwo, List<IBioTMLToken> tokens, List<String> pos, List<String> lemmas){
		String verbString = new String();
		IBioTMLOffsetsPair offsetspair = null;
		if(annotationOne.compareTo(annotationTwo)<0)
			offsetspair = new BioTMLOffsetsPairImpl(annotationOne.getStartOffset(), annotationTwo.getEndOffset());
		else
			offsetspair = new BioTMLOffsetsPairImpl(annotationTwo.getStartOffset(), annotationOne.getEndOffset());
		
		IBioTMLToken verbToken = null;
		
		for(int i=0; i<tokens.size(); i++){
			IBioTMLOffsetsPair tokenoffsetpair = tokens.get(i).getTokenOffsetsPair();
			if(!offsetspair.offsetsOverlap(tokenoffsetpair) 
					&& pos.get(i).startsWith("VB")
					&& tokenoffsetpair.compareTo(offsetspair)<0){
				if(verbToken == null || verbToken != null && verbToken.getTokenOffsetsPair().compareTo(tokenoffsetpair)>0)
					verbToken = tokens.get(i);
			}
		}
		
		if(verbToken != null)
			verbString = verbToken.getToken();
		return verbString;

	}
	
	private String verbOutside(IBioTMLEntity annotationOne, IBioTMLEntity annotationTwo, List<IBioTMLToken> tokens, List<String> pos, List<String> lemmas){
		String verbString = new String();
		IBioTMLOffsetsPair offsetspair = null;
		if(annotationOne.compareTo(annotationTwo)<0)
			offsetspair = new BioTMLOffsetsPairImpl(annotationOne.getStartOffset(), annotationTwo.getEndOffset());
		else
			offsetspair = new BioTMLOffsetsPairImpl(annotationTwo.getStartOffset(), annotationOne.getEndOffset());
		
		for(int i=0; i<tokens.size(); i++){
			if(!offsetspair.offsetsOverlap(tokens.get(i).getTokenOffsetsPair()) && pos.get(i).startsWith("VB")){
				if(!verbString.isEmpty())
					verbString = verbString + " ";
				verbString = verbString + lemmas.get(i);
			}
		}
		return verbString;

	}
	
	private String associationFeaturesUsingLCATokensAnnotation(IBioTMLEntity annotationOne, IBioTMLEntity annotationTwo, List<IBioTMLToken> tokens, NLPNode[] dependencyNodes, String featureType ){
		String result = new String();
		NLPNode commonNLPNodeAnnotationOne = getCommonAnnotationsTokensNLPNode(annotationOne, dependencyNodes, tokens);
		NLPNode commonNLPNodeAnnotationTwo = getCommonAnnotationsTokensNLPNode(annotationTwo, dependencyNodes, tokens);
		if(featureType.equals("NLP4JPOSREPRESENTATIVE"))
			result = commonNLPNodeAnnotationOne.getPartOfSpeechTag() + "__&&__" + commonNLPNodeAnnotationTwo.getPartOfSpeechTag();
		else if(featureType.equals("NLP4JDEPENDECYREPRESENTATIVE"))
			result = commonNLPNodeAnnotationOne.getDependencyLabel() + "__&&__" + commonNLPNodeAnnotationTwo.getDependencyLabel();
		return result;
	}
	
	private NLPNode getCommonAnnotationsTokensNLPNode(IBioTMLEntity annotation, NLPNode[] dependencyNodes, List<IBioTMLToken> tokens){
		Set<NLPNode> annotationNodes = new HashSet<>();
		for(int i=0; i<tokens.size(); i++){
			if(annotation.getAnnotationOffsets().offsetsOverlap(tokens.get(i).getTokenOffsetsPair()))
				annotationNodes.add(dependencyNodes[i+1]);
		}
		return findLowestCommonAcenstor(annotationNodes);
	}
	
	private NLPNode findLowestCommonAcenstor(Set<NLPNode> nodes){
		NLPNode ascenstor = null;
		Iterator<NLPNode> itnodes = nodes.iterator();
		while(itnodes.hasNext()){
			NLPNode node = itnodes.next();
			if(ascenstor == null)
				ascenstor = node;
			else
				ascenstor = ascenstor.getLowestCommonAncestor(node);
		}
		return ascenstor;
	}
	
	private String getTokensAfterLastAnnotationsAndTokenLemma(IBioTMLEntity annotationOne, IBioTMLEntity annotationTwo, 
			List<IBioTMLToken> tokens, List<String> lemmas, String lemmatofind){
		String result = new String();
		List<String> resultList = getLemmasAfterLemaPoint(annotationOne, annotationTwo, tokens, lemmas, lemmatofind);
		for(String res : resultList){
			if(!result.isEmpty())
				result = result + "__&&__";
			result = result + res;
		}
		return result;
	}

	private List<String> getLemmasAfterLemaPoint(IBioTMLEntity annotationOne, IBioTMLEntity annotationTwo,
			List<IBioTMLToken> tokens, List<String> lemmas, String lemmatofind) {
		List<String> resultList = new ArrayList<>();
		IBioTMLOffsetsPair offsetspair = null;
		if(annotationOne.compareTo(annotationTwo)<0)
			offsetspair = new BioTMLOffsetsPairImpl(annotationOne.getStartOffset(), annotationTwo.getEndOffset());
		else
			offsetspair = new BioTMLOffsetsPairImpl(annotationTwo.getStartOffset(), annotationOne.getEndOffset());
		
		String[] finisherLemmas = {".", ",", "because", "that", "who", "which", "by", "for", "from", "but"};
		List<String> finisherLemmasList = Arrays.asList(finisherLemmas);
		
		boolean found = false;
		boolean foundForFinisher = false;
		for(int i=0; i<tokens.size(); i++){
			if(offsetspair.compareTo(tokens.get(i).getTokenOffsetsPair())<0){			
				if(found && !foundForFinisher){
					if(finisherLemmasList.contains(lemmas.get(i)))
						foundForFinisher = true;
					else
						resultList.add(lemmas.get(i));
				}
				if(lemmas.get(i).equals(lemmatofind))
					found = true;
			}
		}
		return resultList;
	}
	
	private List<String> getLemmasBetweenAnnotations(IBioTMLEntity annotationOne, IBioTMLEntity annotationTwo,
			List<IBioTMLToken> tokens, List<String> lemmas){
		List<String> resultList = new ArrayList<>();
		IBioTMLOffsetsPair offsetspair = null;
		if(annotationOne.compareTo(annotationTwo)<0)
			offsetspair = new BioTMLOffsetsPairImpl(annotationOne.getStartOffset(), annotationTwo.getEndOffset());
		else
			offsetspair = new BioTMLOffsetsPairImpl(annotationTwo.getStartOffset(), annotationOne.getEndOffset());
		
		for(int i=0; i<tokens.size(); i++){
			if(offsetspair.offsetsOverlap(tokens.get(i).getTokenOffsetsPair())){
				resultList.add(lemmas.get(i));
			}
		}
		return resultList;
	}

}