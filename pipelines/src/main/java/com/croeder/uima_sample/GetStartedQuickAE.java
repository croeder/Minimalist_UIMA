package com.croeder.uima_sample;

// from http://code.google.com/p/uimafit/wiki/GettingStarted

import org.apache.uima.jcas.JCas;
import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.analysis_engine.AnalysisEngine;


import org.uimafit.component.JCasAnnotator_ImplBase;
import org.uimafit.descriptor.ConfigurationParameter;
import org.uimafit.factory.AnalysisEngineFactory;
import org.uimafit.factory.JCasFactory;

import org.apache.log4j.Logger;
import org.apache.log4j.BasicConfigurator;


public class GetStartedQuickAE extends JCasAnnotator_ImplBase {

	Logger logger = Logger.getLogger(GetStartedQuickAE.class);
	
	public static final String PARAM_STRING = "stringParam";
	@ConfigurationParameter(name = PARAM_STRING)
	private String stringParam;
        
	@Override
	public void process(JCas jCas) 
	throws AnalysisEngineProcessException {
		System.out.println("Hello world!  Say 'hi' to " + stringParam);
		logger.info("...logging message");
	}


	public static void main(String args[]) 
	throws UIMAException, AnalysisEngineProcessException, ResourceInitializationException {
		BasicConfigurator.configure();

		JCas jCas = JCasFactory.createJCas();

		AnalysisEngine analysisEngine = 
    		AnalysisEngineFactory.createPrimitive(
				GetStartedQuickAE.class,
    			GetStartedQuickAE.PARAM_STRING, "uimaFIT");

		analysisEngine.process(jCas);
	}

}
