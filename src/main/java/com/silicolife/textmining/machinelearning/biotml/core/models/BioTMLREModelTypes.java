package com.silicolife.textmining.machinelearning.biotml.core.models;

public enum BioTMLREModelTypes {
	alltokenscoocurrencewithtriggers{
		@Override
		public String toString() {
			return "Uses the coocurrence of all tokens from the sentence against token triggers. (token in sentence - token in trigger)";
		}
	},
	alltokenscoocurrencewithentities{
		@Override
		public String toString() {
			return "Uses the coocurrence of all tokens from the sentence against token annotations. (token in sentence - token in annotation)";
		}
	},
	annotationtokenscoocurrencewithtriggers{
		@Override
		public String toString() {
			return "Uses the coocurrence of annotation tokens from the sentence against token triggers. (token in annotation - token in trigger)";
		}
	},
	annotationtokenscoocureencewithentities{
		@Override
		public String toString() {
			return "Uses the coocurrence of annotation tokens from the sentence against token annotations. (token in annotation - token in annotation)";
		}
	},
/**	entitycluegenerateentity{
		@Override
		public String toString() {
			return "Relations with clues using all tokens generating entities with the relation";
		}
	},
	entityentiygenerateentity{
		@Override
		public String toString() {
			return "Relations entity-entity using all tokens generating entities with the relation";
		}
	},
	entityclueonlyannotationsgenerateentity{
		@Override
		public String toString() {
			return "Relations with clues using only annotation tokens generating entities with the relation";
		}
	},
	entityentiyonlyannotationsgenerateentity{
		@Override
		public String toString() {
			return "Relations entity-entity using only annotation tokens generating entities with the relation";
		}
	},*/
	events{
		@Override
		public String toString() {
			return "Generate a model based on events.";
		}
	};
	
	public static String getInfo(BioTMLREModelTypes type){
		if(type.equals(BioTMLREModelTypes.alltokenscoocurrencewithtriggers)){
			return "Each annotated clue is associated with all tokens present in the same sentence.\n"
					+ "The model will learn the association of all tokens to the annotated clues (if it belongs to a relation or not). \n"
					+ "Regarding the association token-clue, the model will be able to predict similar relations in text with NER annotations.";
		}
		if(type.equals(BioTMLREModelTypes.alltokenscoocurrencewithentities)){
			return "Each token annotation is associated with all tokens present in the same sentence.\n"
					+ "The model will learn the association of that token annotation with other tokens (if it belongs to a relation or not). \n"
					+ "Regarding the association token annotation-token, the model will be able to predict similar relations in text with NER annotations.";
		}
		if(type.equals(BioTMLREModelTypes.annotationtokenscoocurrencewithtriggers)){
			return "Each annotated clue is associated with tokens of each NER annotations present in the same sentence.\n"
					+ "The model will learn the association of that token annotations to the annotated clues (if it belongs to a relation or not). \n"
					+ "Regarding the association token annotation-clue, the model will be able to predict similar relations in text with NER annotations.";
		}
		if(type.equals(BioTMLREModelTypes.annotationtokenscoocureencewithentities)){
			return "Each token annotation is associated with annotation tokens present in the same sentence.\n"
					+ "The model will learn the association of that token annotation with annotation tokens (if it belongs to a relation or not). \n"
					+ "Regarding the association token annotation-token annotation, the model will be able to predict similar relations in text with NER annotations.";
		}
		if(type.equals(BioTMLREModelTypes.events)){
			return "Annotation events will be used as instances for model";
		}
		return "Without information.";
	}
	
	public static BioTMLREModelTypes stringValueOf(String toString){
		for(BioTMLREModelTypes type : BioTMLREModelTypes.values()){
			if(type.toString().equals(toString)){
				return type;
			}
		}
		return null;
	}
}
