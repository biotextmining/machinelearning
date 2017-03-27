package com.silicolife.textmining.machinelearning.biotml.core.mllibraries;

public enum BioTMLAlgorithm {
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
	},
	malletnaivebayes{
		@Override
		public String toString() {
			return "Naive Bayes using Mallet";
		}
	},
	malletdecisiontree{
		@Override
		public String toString() {
			return "Decision Tree using Mallet";
		}
	},
	malletmaxent{
		@Override
		public String toString() {
			return "MaxEnt using Mallet";
		}
	},
	malletc45{
		@Override
		public String toString() {
			return "C45 using Mallet";
		}
	};
	
	public static BioTMLAlgorithm stringValueOf(String toString){
		for(BioTMLAlgorithm algorithm : BioTMLAlgorithm.values()){
			if(algorithm.toString().equals(toString)){
				return algorithm;
			}
		}
		return null;
	}
}
