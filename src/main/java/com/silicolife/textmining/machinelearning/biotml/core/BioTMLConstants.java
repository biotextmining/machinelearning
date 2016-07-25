package com.silicolife.textmining.machinelearning.biotml.core;

public enum BioTMLConstants {
	b{
		@Override
        public String toString() {
            return "B";
        }
	},
	i{
		@Override
        public String toString() {
            return "I";
        }
	},
	o{
		@Override
        public String toString() {
            return "O";
        }
	},
	ner{
		@Override
		public String toString() {
			return "NER";
		}
	},
	re{
		@Override
		public String toString() {
			return "RE";
		}
	},
	clue{
		@Override
		public String toString() {
			return "clue";
		}
	},
	isAnnotation{
		@Override
		public String toString() {
			return "Token belongs to a annotation";
		}
	},
	isNotAnnotation{
		@Override
		public String toString() {
			return "Token doesn't belong to a annotation";
		}
	}
}
