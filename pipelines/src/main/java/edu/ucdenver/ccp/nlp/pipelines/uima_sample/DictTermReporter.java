// Copyright 2012, Chris Roeder 
package com.croeder.uima_sample;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.apache.log4j.BasicConfigurator;

import org.apache.uima.jcas.JCas;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.analysis_engine.AnalysisEngine;

//import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.uimafit.component.JCasAnnotator_ImplBase;
import org.uimafit.descriptor.ConfigurationParameter;
import org.uimafit.factory.AnalysisEngineFactory;
import org.uimafit.factory.JCasFactory;
import org.uimafit.util.JCasUtil;

import org.apache.uima.conceptMapper.DictTerm;
import org.apache.uima.examples.SourceDocumentInformation;

/**
 * Example annotator  just reports what Proteins it finds in the CAS.
 */
public class DictTermReporter extends JCasAnnotator_ImplBase {

	Logger logger = Logger.getLogger(GetStartedQuickAE.class);

	public void process(JCas jCas) {

		for (SourceDocumentInformation doc : JCasUtil.select(jCas, SourceDocumentInformation.class)) {
			System.out.println("" + doc.getDocumentSize() + doc.getUri());
        }

		for (DictTerm term : JCasUtil.select(jCas, DictTerm.class)) {
			System.out.println(term.getMatchedText() + ", " + term.getDictCanon());
        }
	
	}

}
