package clariah.fcs.mapping;

public class Conversions 
{
	static  ConversionTable UD2CHN;

	static ConversionTable UD2BaB;

	static ConversionTable UD2CGNSonar; 

	static
	{
		String[][] fieldMapping  = {{"xxword", "t"}};

		String[][] featureMapping = 
			{
					{"pos","ADJ", "pos", "AA"},
					{"pos","ADV", "pos", "ADV|AA"},
					{"pos", "INTJ", "pos", "INT"},

					{"pos", "NOUN", "pos", "NOU-C"},
					{"pos", "PROPN", "pos", "NOU-P"},
					
					{"pos", "VERB", "pos", "VRB"},

					{"pos", "ADP",  "pos", "ADP"},
					{"pos", "AUX", "pos", "VRB"},  // HM
				
					{"pos", "DET", "pos", "ART|PRN"}, // HM
					{"pos", "NUM", "pos", "NUM"},
					
					{"pos", "PRON", "pos", "PRN"}, // HM
					{"pos", "CCONJ", "pos", "CONJ", "type", "coor"}, // HM
					{"pos", "SCONJ", "pos", "CONJ", "type", "sub"}, // HM
					{"pos", "PUNCT", "pos", "RES"}, // HM hebben we niet
					{"pos", "SYM", "pos", "RES"},
					{"pos", "X", "pos", "RES"},

					
					// nominal features
					
					{"Number", "Plur", "number", "pl"},
					{"Number", "Sing", "number", "sg"},
					{"Gender", "Fem", "gender", "f"},
					{"Gender", "Masc", "gender", "m"},
					{"Gender", "Neut", "gender", "n"},
					{"Gender",  "Com", "gender", "f", "gender", "m"},  // HM, not implemented

					// adjective
					
					{"Degree", "Pos", "degree", "pos"},
					{"Degree", "Cmp", "degree", "comp"},
					{"Degree", "Sup", "degree", "sup"},
					
					// {"form"                "formal", "infl-e"}, Niet in universal dependencies: domdomdom!
					// {"Case", "Gen", NIET in CHN: domdomdom
					// verb
					// {"pos", "PART", "", ""}, // HM, particle hebben we niet
					
					{"Mood", "Ind", "finiteness", "fin"},
					{"Mood", "Imp", "finiteness", "fin"},
					{"Mood", "Sub", "finiteness", "fin"},
					
					{"VerbForm", "Fin", "finiteness", "fin"},
					{"VerbForm", "Inf", "finiteness", "inf"},
					{"VerbForm", "Part", "finiteness", "part"},
					
					{"Person", "1", "person", "1"},
					{"Person", "2", "person", "2"},
					{"Person", "3", "person", "3"},
					
					{"Tense", "Past", "tense", "past"},
					{"Tense", "Pres", "tense", "pres"},
					//{''tense", "pres", "tense", "pres"}
			};

		String[] grammarFeats = {"number", "tense", "mood", "type"};

		ConversionTable ct = new ConversionTable(fieldMapping, featureMapping);


		ct.useFeatureRegex = true;
		ct.posTagField = "pos";
		ct.grammaticalFeatures = grammarFeats;

		UD2CHN = ct;
	}



	static
	{
		String[][] fieldMapping1  = {{"xxword", "t"}};

		String[][] featureMapping1 = 
			{
					{"pos","ADJ", "pos", "ADJ"},
					{"pos","ADV", "pos", "BW"},
					{"pos", "INTJ", "pos", "TSW"},
					
					{"pos", "NOUN", "pos", "N", "feat", "soort"},
					{"pos", "PROPN", "pos", "N", "feat", "eigen"}, // spec(deeleigen nooit te vinden zo....
					
					{"pos", "VERB", "pos", "WW"},

					{"pos", "ADP",  "pos", "VZ"},
					{"pos", "AUX", "pos", "WW"},  // HM
					
					{"pos", "DET", "pos", "LID|VNW"}, // HM
					
					{"pos", "NUM", "pos", "TW"},
					//{"pos", "PART", "", ""}, // HM
					
					{"pos", "PRON", "pos", "VNW", "feat", "pron"}, // HM
					
					{"pos", "CCONJ", "pos", "VG", "feat", "neven"}, // HM
					{"pos", "SCONJ", "pos", "VG", "feat", "onder"}, // HM
					
					{"pos", "PUNCT", "pos", "LET"}, // HM 
					{"pos", "SYM", "pos", "SPEC"}, // opzoeken
					{"pos", "X", "pos", "SPEC"}
			};

		String[] grammarFeats1 = {"number", "tense", "mood", "type"};

		ConversionTable ct1 = new ConversionTable(fieldMapping1, featureMapping1);


		ct1.useFeatureRegex = true;
		ct1.includeFeatureNameInRegex = false;
		ct1.posTagField = "pos";
		ct1.grammaticalFeatures = grammarFeats1;

		UD2CGNSonar = ct1;
	}
}

