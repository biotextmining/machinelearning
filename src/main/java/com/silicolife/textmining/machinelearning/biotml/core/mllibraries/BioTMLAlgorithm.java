package com.silicolife.textmining.machinelearning.biotml.core.mllibraries;

public enum BioTMLAlgorithms {
	malletcrf{
		@Override
        public String toString() {
            return "CRF using Mallet";
        }
	},
	mallethmm{
		@Override
		public String toString() {
			return "HMM using Mallet";
		}
	},
	malletsvm{
		@Override
		public String toString() {
			return "SVM using LibSVM and Mallet";
		}
	};
	
	public static BioTMLAlgorithms stringValueOf(String toString){
		for(BioTMLAlgorithms algorithm : BioTMLAlgorithms.values()){
			if(algorithm.toString().equals(toString)){
				return algorithm;
			}
		}
		return null;
	}
}
