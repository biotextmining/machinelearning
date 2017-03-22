package com.silicolife.textmining.machinelearning.biotml.core.nlp.nlp4j;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.silicolife.textmining.machinelearning.biotml.core.exception.BioTMLException;
import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLSentence;

public class TestNLP4JSingleton {

	public String document(){
		return "Background\n"+
		"The occurrence of subsequent neoplasms has direct impact on the quantity and quality of life in cancer survivors. We have expanded our analysis of these events in the Childhood Cancer Survivor Study (CCSS) to better understand the occurrence of these events as the survivor population ages.\n"+
		"Methods\n"+
		"The incidence of and risk for subsequent neoplasms occurring 5 years or more after the childhood cancer diagnosis were determined among 14 359 5-year survivors in the CCSS who were treated from 1970 through 1986 and who were at a median age of 30 years (range = 5–56 years) for this analysis. At 30 years after childhood cancer diagnosis, we calculated cumulative incidence at 30 years of subsequent neoplasms and calculated standardized incidence ratios (SIRs), excess absolute risks (EARs) for invasive second malignant neoplasms, and relative risks for subsequent neoplasms by use of multivariable Poisson regression.\n"+
		"Results\n"+
		"Among 14 359 5-year survivors, 1402 subsequently developed 2703 neoplasms. Cumulative incidence at 30 years after the childhood cancer diagnosis was 20.5% (95% confidence interval [CI] = 19.1% to 21.8%) for all subsequent neoplasms, 7.9% (95% CI = 7.2% to 8.5%) for second malignant neoplasms (excluding nonmelanoma skin cancer), 9.1% (95% CI = 8.1% to 10.1%) for nonmelanoma skin cancer, and 3.1% (95% CI = 2.5% to 3.8%) for meningioma. Excess risk was evident for all primary diagnoses (EAR = 2.6 per 1000 person-years, 95% CI = 2.4 to 2.9 per 1000 person-years; SIR = 6.0, 95% CI = 5.5 to 6.4), with the highest being for Hodgkin lymphoma (SIR = 8.7, 95% CI = 7.7 to 9.8) and Ewing sarcoma (SIR = 8.5, 95% CI = 6.2 to 11.7). In the Poisson multivariable analysis, female sex, older age at diagnosis, earlier treatment era, diagnosis of Hodgkin lymphoma, and treatment with radiation therapy were associated with increased risk of subsequent neoplasm.\n"+
		"Conclusions"+
		"As childhood cancer survivors progress through adulthood, risk of subsequent neoplasms increases. Patients surviving Hodgkin lymphoma are at greatest risk. There is no evidence of risk reduction with increasing duration of follow-up.";
	}
	
	public String document2(){
		return "The xpto is not essential gene. The xpto isn't essential gene. The xpto is non-essential gene.";
	}
	
	@Test
	public void test() throws BioTMLException {
		
		List<IBioTMLSentence> sentences = BioTMLNLP4J.getInstance().getSentences(document2());
		List<String> tokens = sentences.get(2).getTokenStrings();
		
		System.out.println(tokens);
		
		List<String> lemmas = BioTMLNLP4J.getInstance().processLemma(tokens);
		System.out.println(Arrays.asList(lemmas));
		
		List<String> pos = BioTMLNLP4J.getInstance().processPos(tokens);
		System.out.println(Arrays.asList(pos));
//		NLPNode[] nodes = BioTMLNLP4J.getInstance().processDependency(tokens.toArray(new String[0]));
//		System.out.println(nodes);
//		BioTMLNLP4J.getInstance().clearModelsInMemory();
	}

}
