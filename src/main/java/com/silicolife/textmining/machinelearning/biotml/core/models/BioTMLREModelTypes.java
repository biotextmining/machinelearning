package com.silicolife.textmining.machinelearning.biotml.core.models;

public enum BioTMLREModelTypes {
	allclasseswithclues{
		@Override
		public String toString() {
			return "Relations with clues using all tokens";
		}
	},
	entityentiy{
		@Override
		public String toString() {
			return "Relations entity-entity using all tokens";
		}
	},
	allclasseswithcluesonlyannotations{
		@Override
		public String toString() {
			return "Relations with clues using only annotation tokens";
		}
	},
	entityentiyonlyannotations{
		@Override
		public String toString() {
			return "Relations entity-entity using only annotation tokens";
		}
	};
	
	public static String getInfo(BioTMLREModelTypes type){
		if(type.equals(BioTMLREModelTypes.allclasseswithclues)){
			return "Each annotated clue is associated with all tokens present in the same sentence.\n"
					+ "The model will learn the association of all tokens to the annotated clues (if it belongs to a relation or not). \n"
					+ "Regarding the association token-clue, the model will be able to predict similar relations in text with NER annotations.";
		}
		if(type.equals(BioTMLREModelTypes.entityentiy)){
			return "Each token annotation is associated with all tokens present in the same sentence.\n"
					+ "The model will learn the association of that token annotation with other tokens (if it belongs to a relation or not). \n"
					+ "Regarding the association token annotation-token, the model will be able to predict similar relations in text with NER annotations.";
		}
		if(type.equals(BioTMLREModelTypes.allclasseswithcluesonlyannotations)){
			return "Each annotated clue is associated with tokens of each NER annotations present in the same sentence.\n"
					+ "The model will learn the association of that token annotations to the annotated clues (if it belongs to a relation or not). \n"
					+ "Regarding the association token annotation-clue, the model will be able to predict similar relations in text with NER annotations.";
		}
		if(type.equals(BioTMLREModelTypes.entityentiyonlyannotations)){
			return "Each token annotation is associated with annotation tokens present in the same sentence.\n"
					+ "The model will learn the association of that token annotation with annotation tokens (if it belongs to a relation or not). \n"
					+ "Regarding the association token annotation-token annotation, the model will be able to predict similar relations in text with NER annotations.";
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
