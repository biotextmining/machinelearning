package com.silicolife.textmining.machinelearning.biotml.core.corpora.otherdatastructures;

import java.util.ArrayList;
import java.util.List;

import com.silicolife.textmining.machinelearning.biotml.core.interfaces.IBioTMLEvent;

public class BioTMLEventsWithFeaturesAndLables {
	
	private List<IBioTMLEvent> events;
	private List<String> labels;
	private List<List<String>> eventFeatures;
	
	public BioTMLEventsWithFeaturesAndLables(){
		this.events = new ArrayList<>();
		this.labels = new ArrayList<>();
		this.eventFeatures = new ArrayList<>();
	}
	
}
