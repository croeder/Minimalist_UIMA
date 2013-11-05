// Copyright 2012, Chris Roeder 
package com.croeder.uima_sample;


import java.io.IOException;
import java.io.File;
import java.io.FileNotFoundException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import org.apache.uima.UIMAException;
import org.apache.uima.UIMAFramework;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.collection.CollectionReader;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.jcas.JCas;
import org.apache.uima.pear.util.FileUtil;
import org.apache.uima.resource.ExternalResourceDescription;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.apache.uima.resource.metadata.ResourceMetaData;
import org.apache.uima.tools.components.FileSystemCollectionReader;
import org.apache.uima.cas.impl.XmiCasSerializer;
import org.apache.uima.util.XMLSerializer;

import org.apache.uima.conceptMapper.ConceptMapper; 
import org.apache.uima.conceptMapper.DictTerm;
import org.apache.uima.conceptMapper.support.tokens.TokenFilter;
import org.apache.uima.conceptMapper.support.tokens.TokenNormalizer;
import org.apache.uima.conceptMapper.support.dictionaryResource.DictionaryResource_impl;

import org.uimafit.factory.AnalysisEngineFactory;
import org.uimafit.factory.CollectionReaderFactory;
import org.uimafit.factory.TypeSystemDescriptionFactory;
import org.uimafit.factory.JCasFactory;
import org.uimafit.factory.ResourceCreationSpecifierFactory;
import org.uimafit.pipeline.JCasIterable;
import org.uimafit.component.xwriter.CASDumpWriter;
import org.uimafit.component.xwriter.XWriter;

import uima.tt.TokenAnnotation;

import com.croeder.uima_sample.analysis_engines.LingPipeSentenceDetector_AE;
import com.croeder.uima_sample.analysis_engines.Debug_AE;

import org.xml.sax.SAXException;



public class ConceptMapperPipeline extends Pipeline  {

	private static Logger logger = Logger.getLogger(ConceptMapperPipeline.class);

	ConceptMapperPipeline(File dir) throws UIMAException, IOException {
		super(dir, new JCasExtractor());

        // SENTENCE DETECTOR 
        AnalysisEngineDescription sentenceDetectorDesc
           = LingPipeSentenceDetector_AE.createAnalysisEngineDescription(tsd);
		//engines.add(UIMAFramework.produceAnalysisEngine(sentenceDetectorDesc));
		engineDescs.add(sentenceDetectorDesc);


		// TOKENIZER from xml files ** THE PATH MUST BE A FILE SYSTEM PATH **
		Object[] config = new Object[0];
  		ResourceSpecifier tokenizerDesc 
			= ResourceCreationSpecifierFactory.createResourceCreationSpecifier(
				"target/classes/descriptors/analysis_engine/primitive/OffsetTokenizer.xml", config);
		engineDescs.add(tokenizerDesc);


		// CONCEPT MAPPER from xml files ** ...FILE SYSTEM PATH **
		// The dictionary is specified, in this case, in the xml file.
  		ResourceSpecifier conceptMapperDesc 
			= ResourceCreationSpecifierFactory.createResourceCreationSpecifier(
				"target/classes/descriptors/analysis_engine/primitive/ConceptMapperOffsetTokenizer.xml", config);
		engineDescs.add(conceptMapperDesc);


        AnalysisEngineDescription debugDesc = Debug_AE.createAnalysisEngineDescription(tsd);
        engineDescs.add(debugDesc);
	}


	protected static void usage() {
	 	System.out.println("mvn exec:java -Dinput=<input tree> ");
	}


	public static void main(String[] args) {

		if (args.length < 1) {
			usage();
			System.exit(1);
		}

		File inputDir = null;
		try {
			inputDir = new File(args[0]);
		} 
		catch(Exception x) {
			System.out.println("error:" + x);
			x.printStackTrace();
			usage();
			System.exit(2);
		}
		try {
			ConceptMapperPipeline pipeline = new ConceptMapperPipeline(inputDir);
	
			BasicConfigurator.configure();
	
			System.out.println("going with "
				+ " inputDir:" + inputDir);	
			Collection<JCasExtractor.Result> results = pipeline.runPipeline();

			for (JCasExtractor.Result result : results) {
				System.out.println(result.getName());
				for (String key : result.getKeys()) {
					System.out.println("    " + key + ", " + result.get(key));
				}
			}
		}
		catch(Exception x) {
			System.err.println(x);
			x.printStackTrace();
			System.exit(3);
		}
	}
}
