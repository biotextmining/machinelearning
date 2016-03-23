package com.silicolife.textmining.machinelearning.biotml.core.features.modules;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Pattern;

import com.silicolife.textmining.machinelearning.biotml.core.exception.BioTMLException;
import com.silicolife.textmining.machinelearning.biotml.core.features.datastructures.BioTMLAssociationProcess;
import com.silicolife.textmining.machinelearning.biotml.core.features.datastructures.BioTMLFeatureColumns;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLFeatureColumns;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLFeatureGenerator;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLFeatureGeneratorConfigurator;

/**
 * 
 * A class responsible for features generated by regular expressions.
 * 
 * @since 1.0.0
 * @author Ruben Rodrigues ({@code rrodrigues@silicolife.com})
 */

public class RegexMatchesFeatures implements IBioTMLFeatureGenerator{

	private Map<String, Pattern> listPatternsByUIDFeatures;
	private Set<String> uIDs;
	
	protected String chemicalElementsAbreviations = "Ac|Ag|Al|Am|Ar|As|At|Au|B|Ba|Be|Bh|Bi|Bk|Br|C|Ca|Cd|Ce|Cf|Cl|Cm|Co|Cr|Cs|Cu|Db|Ds|Dy|Er|Es|Eu|F|Fe|Fm|Fr|Ga|Gd|Ge|H|He|Hf|Hg|Ho|Hs|I|In|Ir|K|Kr|La|Li|Lr|Lu|Md|Mg|Mn|Mo|Mt|N|Na|Nb|Nd|Ne|Ni|No|Np|O|Os|P|Pa|Pb|Pd|Pm|Po|Pr|Pt|Pu|Ra|Rb|Re|Rf|Rh|Rn|Ru|S|Sb|Sc|Se|Sg|Si|Sm|Sn|Sr|Ta|Tb|Tc|Te|Th|Ti|Tl|Tm|U|Uub|Uuh|Uuo|Uup|Uuq|Uus|Uut|Uuu|V|W|Xe|Y|Yb|Zn|Zr";
	protected String chemicalElementsNames = "actinium|aluminum|americium|antimony|argon|arsenic|astatine|barium|berkelium|beryllium|bismuth|bohrium|boron|bromine|cadmium|calcium|californium|carbon|cerium|cesium|chlorine|chromium|cobalt|copper|curium|darmstadtium|dubnium|dysprosium|einsteinium|erbium|europium|fermium|fluorine|francium|gadolinium|gallium|germanium|gold|hafnium|hassium|helium|holmium|hydrogen|indium|iodine|iridium|iron|krypton|lanthanum|lawrencium|lead|lithium|lutetium|magnesium|manganese|meitnerium|mendelevium|mercury|molybdenum|neodymium|neon|neptunium|nickel|niobium|nitrogen|nobelium|osmium|oxygen|palladium|phosphorus|platinum|plutonium|polonium|potassium|praseodymium|promethium|protactinium|radium|radon|rhenium|rhodium|rubidium|ruthenium|rutherfordium|samarium|scandium|seaborgium|selenium|silicon|silver|sodium|strontium|sulfur|tantalum|technetium|tellurium|terbium|thallium|thorium|thulium|tin|titanium|tungsten|ununbium|ununhexium|ununoctium|ununpentium|ununquadium|ununseptium|ununtrium|ununium|uranium|vanadium|xenon|ytterbium|yttrium|zinc|zirconium";
	protected String aminoAcidNames = "alanine|arginine|asparagine|asparticacid|cysteine|glutamicacid|glutamine|glycine|histidine|isoleucine|leucine|lysine|methionine|phenylalanine|proline|serine|threonine|tryptophan|tyrosine|valine|selenocysteine|pyrrolysine";
	protected String aminoAcid3LetterAbreviations = "Ala|Arg|Asn|Asp|Cys|Glu|Gln|Gly|His|Ile|Leu|Lys|Met|Phe|Pro|Ser|Thr|Trp|Tyr|Val|Sec|Pyl|Asx|Glx|Xle|Xaa";
	
	/**
	 * 
	 * Initializes the insertion of features generated by regular expressions.
	 * 
	 */
	public  RegexMatchesFeatures(){
		this.uIDs = initUIDs();
		this.listPatternsByUIDFeatures = initListPatternsByUIDFeatures();
	}
	
	
	public Map<String, String> getUIDInfos() {
		Map<String, String> infoMap = new HashMap<>();
		infoMap.put("INITCAPS", "Boolean feature of regular expression used to verify if the token initializes with a upper case.");
		infoMap.put("ENDCAPS", "Boolean feature of regular expression used to verify if the token ends with a upper case.");
		infoMap.put("ALLCAPS", "Boolean feature of regular expression used to verify if the token is only upper case.");
		infoMap.put("NOCAPS", "Boolean feature of regular expression used to verify if the token is only lowercase case.");
		infoMap.put("MIXCAPS", "Boolean feature of regular expression used to verify if the token contains both upper and lower case.");
		infoMap.put("SYMBOLNUMCHAR", "Boolean feature of regular expression used to verify if the token contains symbols or numbers between letters.");
		infoMap.put("HYPHEN", "Boolean feature of regular expression used to verify if the token contains hyphens.");
		infoMap.put("BACKSLASH", "Boolean feature of regular expression used to verify if the token contains backslashs.");
		infoMap.put("OPENBRACKET", "Boolean feature of regular expression used to verify if the token contains open brackets.");
		infoMap.put("CLOSEBRACKET", "Boolean feature of regular expression used to verify if the token contains close brackets.");
		infoMap.put("COLON", "Boolean feature of regular expression used to verify if the token contains colons.");
		infoMap.put("SEMICOLON", "Boolean feature of regular expression used to verify if the token contains semi colons.");
		infoMap.put("PERCENT", "Boolean feature of regular expression used to verify if the token contains percent character.");
		infoMap.put("OPENPARENT", "Boolean feature of regular expression used to verify if the token contains open parentisis.");
		infoMap.put("CLOSEPARENT", "Boolean feature of regular expression used to verify if the token contains close parentisis.");
		infoMap.put("COMMA", "Boolean feature of regular expression used to verify if the token contains commas.");
		infoMap.put("DOT", "Boolean feature of regular expression used to verify if the token contains dots.");
		infoMap.put("APOSTROPHE", "Boolean feature of regular expression used to verify if the token contains apostrophes.");
		infoMap.put("QUOTATIONMARK", "Boolean feature of regular expression used to verify if the token contains quotation marks.");
		infoMap.put("ASTERISK", "Boolean feature of regular expression used to verify if the token contains asterisks.");
		infoMap.put("EQUAL", "Boolean feature of regular expression used to verify if the token contains equal characters.");
		infoMap.put("PLUS", "Boolean feature of regular expression used to verify if the token contains plus characters.");
		infoMap.put("ROMANNUM", "Boolean feature of regular expression used to verify if the token contains roman numbers.");
		infoMap.put("GREEKSYMB",  "Boolean feature of regular expression used to verify if the token contains greek symbols.");
		infoMap.put("MOLECULARFORMULAS", "Boolean feature of regular expression used to verify if is a possible molecular formula");
		infoMap.put("ELEMENTNAMES", "Boolean feature of regular expression used to verify if the token contains chemical element names");
		infoMap.put("AMINOACIDNAMES", "Boolean feature of regular expression used to verify if the token contains aminoacid names");
		infoMap.put("POSSIBLEIDENTIFIER", "Boolean feature of regular expression used to verify if the token is a chemical identifier.");
		infoMap.put("ANDOR", "Boolean feature of regular expression used to verify if the token is a 'or' or 'and'.");
		infoMap.put("POSSIBLEIUPAC", "Boolean feature of regular expression used to verify if the token is a chemical IUPAC.");
		return infoMap;
	}

	private Set<String> initUIDs() {
		Set<String> uids = new TreeSet<String>();
		uids.add("INITCAPS");
		uids.add("ENDCAPS");
		uids.add("ALLCAPS");
		uids.add("NOCAPS");
		uids.add("MIXCAPS");
		uids.add("SYMBOLNUMCHAR");
		uids.add("HYPHEN");
		uids.add("BACKSLASH");
		uids.add("OPENBRACKET");
		uids.add("CLOSEBRACKET");
		uids.add("COLON");
		uids.add("SEMICOLON");
		uids.add("PERCENT");
		uids.add("OPENPARENT");
		uids.add("CLOSEPARENT");
		uids.add("COMMA");
		uids.add("DOT");
		uids.add("APOSTROPHE");
		uids.add("QUOTATIONMARK");
		uids.add("ASTERISK");
		uids.add("EQUAL");
		uids.add("PLUS");
		uids.add("ROMANNUM");
		uids.add("GREEKSYMB");
		uids.add("MOLECULARFORMULAS");
		uids.add("ELEMENTNAMES");
		uids.add("AMINOACIDNAMES");
		uids.add("POSSIBLEIDENTIFIER");
		uids.add("ANDOR");
		uids.add("POSSIBLEIUPAC");
		return uids;
	}
	
	public Set<String> getRecomendedUIDs(){
		return getUIDs();
	}

	private Map<String, Pattern> initListPatternsByUIDFeatures(){
		Map<String, Pattern> listPatternsByUIDFeatures = new TreeMap<String, Pattern>();
		listPatternsByUIDFeatures.put("INITCAPS", Pattern.compile("[A-Z].*"));
		listPatternsByUIDFeatures.put("ENDCAPS", Pattern.compile(".*[A-Z]"));
		listPatternsByUIDFeatures.put("ALLCAPS", Pattern.compile("[A-Z]+"));
		listPatternsByUIDFeatures.put("NOCAPS", Pattern.compile("[a-z]+"));
		listPatternsByUIDFeatures.put("MIXCAPS", Pattern.compile("(?:.*[A-Z].*)(?:.*[a-z].*)|(?:.*[a-z].*)(?:.*[A-Z].*)"));
		listPatternsByUIDFeatures.put("SYMBOLNUMCHAR", Pattern.compile("[0-9a-zA-z]+[-%/\\[\\]:;()'\"*=+][0-9a-zA-z]+"));
		listPatternsByUIDFeatures.put("HYPHEN", Pattern.compile(".*[-].*"));
		listPatternsByUIDFeatures.put("BACKSLASH", Pattern.compile(".*[/].*"));
		listPatternsByUIDFeatures.put("OPENBRACKET", Pattern.compile(".*[\\[].*"));
		listPatternsByUIDFeatures.put("CLOSEBRACKET", Pattern.compile(".*[\\]].*"));
		listPatternsByUIDFeatures.put("COLON", Pattern.compile(".*[:].*"));
		listPatternsByUIDFeatures.put("SEMICOLON", Pattern.compile(".*[;].*"));
		listPatternsByUIDFeatures.put("PERCENT", Pattern.compile(".*[%].*"));
		listPatternsByUIDFeatures.put("OPENPARENT", Pattern.compile(".*[(].*"));
		listPatternsByUIDFeatures.put("CLOSEPARENT", Pattern.compile(".*[)].*"));
		listPatternsByUIDFeatures.put("COMMA", Pattern.compile(".*[,].*"));
		listPatternsByUIDFeatures.put("DOT", Pattern.compile(".*[\\.].*"));
		listPatternsByUIDFeatures.put("APOSTROPHE", Pattern.compile(".*['].*"));
		listPatternsByUIDFeatures.put("QUOTATIONMARK", Pattern.compile(".*[\"].*"));
		listPatternsByUIDFeatures.put("ASTERISK", Pattern.compile(".*[*].*"));
		listPatternsByUIDFeatures.put("EQUAL", Pattern.compile(".*[=].*"));
		listPatternsByUIDFeatures.put("PLUS", Pattern.compile(".*[+].*"));
		listPatternsByUIDFeatures.put("ROMANNUM", Pattern.compile("((?=[MDCLXVI])((M{0,3})((C[DM])|(D?C{0,3}))?((X[LC])|(L?XX{0,2})|L)?((I[VX])|(V?(II{0,2}))|V)?))"));
		listPatternsByUIDFeatures.put("GREEKSYMB", Pattern.compile("(alpha|beta|gamma|delta|epsilon|zeta|eta|theta|iota|kappa|lambda|mu|nu|xi|omicron|pi|rho|sigma|tau|upsilon|phi|chi|psi|omega)", Pattern.CASE_INSENSITIVE));
		listPatternsByUIDFeatures.put("MOLECULARFORMULAS", Pattern.compile("("+chemicalElementsAbreviations+"|[0-9()-=])+([+-]?)",Pattern.CASE_INSENSITIVE));
		listPatternsByUIDFeatures.put("ELEMENTNAMES", Pattern.compile(".*("+chemicalElementsNames+").*",Pattern.CASE_INSENSITIVE));
		listPatternsByUIDFeatures.put("AMINOACIDNAMES", Pattern.compile(".*("+aminoAcidNames+").*",Pattern.CASE_INSENSITIVE));
		listPatternsByUIDFeatures.put("POSSIBLEIDENTIFIER", Pattern.compile("[A-Z]{1,4}[1-9|\\-]+"));
		listPatternsByUIDFeatures.put("ANDOR", Pattern.compile("(and)|(or)",Pattern.CASE_INSENSITIVE));
		listPatternsByUIDFeatures.put("POSSIBLEIUPAC", Pattern.compile(".*(meth|eth|prop|but|pent|hex|hept|oct|non|dec|benz|oxy|cyano|amino|imido|formyl|halo|carbo|phenyl)+.*",Pattern.CASE_INSENSITIVE));
		return listPatternsByUIDFeatures;
	}

	public Set<String> getUIDs(){
		return uIDs;
	}

	private Map<String, Pattern> getListPatternsByUIDFeatures(){
		return listPatternsByUIDFeatures;
	}

	private String regexMatches(String token, String featureName, Pattern regex){

		String tokenTrimmed=token;
		if(tokenTrimmed.startsWith("("))
			tokenTrimmed = tokenTrimmed.substring(1);

		if(tokenTrimmed.endsWith(")") || tokenTrimmed.endsWith("."))
			tokenTrimmed = tokenTrimmed.substring(0, tokenTrimmed.length()-1);

		if (regex.matcher(token).matches())
			return featureName;

		if(tokenTrimmed.compareTo(token)!=0) {
			if (regex.matcher(tokenTrimmed).matches()) 
				return featureName;
		}

		return new String();
	}


	public IBioTMLFeatureColumns getFeatureColumns(List<String> tokensToProcess,
			IBioTMLFeatureGeneratorConfigurator configuration)
			throws BioTMLException {
		
		if(tokensToProcess.isEmpty()){
			throw new BioTMLException(27);
		}
		
		BioTMLAssociationProcess tokenAnnotProcess = new BioTMLAssociationProcess(tokensToProcess);
		List<String> tokens = tokenAnnotProcess.getTokens();
		IBioTMLFeatureColumns features = new BioTMLFeatureColumns(tokens, getUIDs(), configuration);

		for (int i = 0; i < tokens.size(); i++){
			String token = tokens.get(i);
			for(String uID : getUIDs()){
				if(configuration.hasFeatureUID(uID)){
					String result = regexMatches(token, uID, getListPatternsByUIDFeatures().get(uID));
					features.addTokenFeature(result, uID);
				}
			}		
		}

		features.updateTokenFeaturesUsingAssociationProcess(tokenAnnotProcess);

		return features;
	}

	public void cleanMemory(){
	}

}