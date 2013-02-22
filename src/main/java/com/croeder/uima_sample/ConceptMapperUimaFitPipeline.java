// Copyright 2012, Chris Roeder 
package com.croeder.uima_sample;;



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
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.collection.CollectionReader;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.jcas.JCas;
import org.apache.uima.pear.util.FileUtil;
import org.apache.uima.resource.ExternalResourceDescription;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.apache.uima.tools.components.FileSystemCollectionReader;

import org.apache.uima.conceptMapper.ConceptMapper; 
import org.apache.uima.conceptMapper.DictTerm;
import org.apache.uima.conceptMapper.support.tokens.TokenFilter;
import org.apache.uima.conceptMapper.support.tokens.TokenNormalizer;
import org.apache.uima.conceptMapper.support.dictionaryResource.DictionaryResource_impl;

import org.uimafit.factory.AnalysisEngineFactory;
import org.uimafit.factory.CollectionReaderFactory;
import org.uimafit.factory.TypeSystemDescriptionFactory;
import org.uimafit.pipeline.SimplePipeline;
import org.uimafit.pipeline.JCasIterable;
import org.uimafit.component.xwriter.CASDumpWriter;

import uima.tt.TokenAnnotation;

//import org.cleartk.token.type.Sentence;
//import org.cleartk.syntax.opennlp.SentenceAnnotator;

import edu.ucdenver.ccp.nlp.wrapper.conceptmapper.ConceptMapperAggregateFactory;
import edu.ucdenver.ccp.nlp.wrapper.conceptmapper.ConceptMapperFactory;
import edu.ucdenver.ccp.nlp.wrapper.conceptmapper.tokenizer.OffsetTokenizerFactory;


// WTF?!!
import edu.ucdenver.ccp.nlp.ext.uima.types.Sentence;


import edu.ucdenver.ccp.nlp.core.uima.annotation.CCPTextAnnotation;
import edu.ucdenver.ccp.nlp.ext.uima.annotators.sentencedetectors.ExplicitSentenceCasInserter;
import edu.ucdenver.ccp.nlp.ext.uima.annotators.sentencedetectors.LingPipeSentenceDetector_AE;



public class ConceptMapperUimaFitPipeline  {

	private static Logger logger = Logger.getLogger(BaseUimaFitPipeline.class);

	protected static final String[] typeSystemStrs = {
 		"analysis_engine.primitive.DictTerm",

		// the train of type-system pain.....
		//"org.cleartk.token.type.Sentence",
		//"edu.ucdenver.ccp.nlp.ext.uima.types.Sentence",
		//"edu.ucdenver.ccp.nlp.ext.uima.annotation.syntax.CCPSentenceAnnotation",
		//"edu.ucdenver.ccp.nlp.ext.uima.annotation.syntax.TypeSystem",
		"edu.ucdenver.ccp.nlp.ext.uima.annotators.sentencedetectors.TypeSystem",
		"edu.ucdenver.ccp.nlp.core.uima.TypeSystem",

		"org.apache.uima.conceptMapper.support.tokenizer.TokenAnnotation",
		"org.apache.uima.examples.SourceDocumentInformation",

		"edu.ucdenver.ccp.nlp.wrapper.conceptmapper.OntologyTerm",
		"edu.ucdenver.ccp.nlp.wrapper.conceptmapper.TypeSystem",

		"uima.tt.TokenAnnotation"
	};

	protected TypeSystemDescription tsd;
  	private static final String PARAM_TOKENIZERDESCRIPTOR = "TokenizerDescriptorPath";


	ConceptMapperUimaFitPipeline() {
        tsd = TypeSystemDescriptionFactory.createTypeSystemDescription(typeSystemStrs);
    }


	protected List<AnalysisEngineDescription> getPipelineAeDescriptions(
		File dictionaryFile)
	throws UIMAException, IllegalArgumentException, IOException {

		List<AnalysisEngineDescription> descriptions = new ArrayList<AnalysisEngineDescription>();

		// OpenNLP Sentence Detector by way of ClearTK
		// not work because these sentences are derived from uima.tcas.Annotation
        // instead of org.apache.uima.jcas.tcas.Annotation...
		//descriptions.add(SentenceAnnotator.getDescription());


        // SENTENCE DETECTOR
        AnalysisEngineDescription sentenceDetectorDesc
           = LingPipeSentenceDetector_AE.createAnalysisEngineDescription(tsd, ExplicitSentenceCasInserter.class, true);
		descriptions.add(sentenceDetectorDesc);



		/**
		// Concept Mapper  -  (so far failed attempt at creating w/out the factory)
		AnalysisEngineDescription conceptMapperDescription = 
			AnalysisEngineFactory.createAnalysisEngineDescription(
				"analysis_engine.primitive.ConceptMapperOffsetTokenizer",
				ConceptMapper.PARAM_DICT_FILE,  dictionaryFile.getAbsolutePath(),
				// sentence annotation class TOKEN_TEXT_FEATURE_NAME

				ConceptMapper.PARAM_SEARCHSTRATEGY, "SkipAnyMatch",
				ConceptMapper.PARAM_ORDERINDEPENDENTLOOKUP,  true,
				"SpanFeatureStructure", 				Sentence.class,
                ConceptMapper.PARAM_TOKENANNOTATION,	TokenAnnotation.class.getName(),
				ConceptMapper.PARAM_FINDALLMATCHES, 	true,
				TokenNormalizer.PARAM_CASE_MATCH, 		"ignoreall",
				"TokenizerDescriptorPath", 				"target/classes/descriptors/analysis_engine/primitive/OffsetTokenizer.xml",

				// stopwordlist TokenFilter.PARAM_STOPWORDS

				// Output UIMA entities and meta data to describe their features
                ConceptMapper.PARAM_ANNOTATION_NAME,	DictTerm.class.getName(),
	 			ConceptMapper.PARAM_ATTRIBUTE_LIST, 	new String[] { "canonical" },	
				//ConceptMapper.PARAM_ATTRIBUTE_LIST, 	new String[] { "canonical", "id" },	
                //ConceptMapper.PARAM_FEATURE_LIST,		new String[] {"DictCanon", "ID"},
                ConceptMapper.PARAM_FEATURE_LIST,		new String[] {"DictCanon"},

                ConceptMapper.PARAM_MATCHEDFEATURE,		"matchedText",
                ConceptMapper.PARAM_ENCLOSINGSPAN,		"enclosingSpan",
				// tokenClassFeatuerName,tokenTextFeatureName, tokenTypeFeaturename
                ConceptMapper.PARAM_TOKENCLASSWRITEBACKFEATURENAMES, new String[0],
                TokenFilter.PARAM_EXCLUDEDTOKENCLASSES, new String[0],
                TokenFilter.PARAM_EXCLUDEDTOKENTYPES, 	new Integer[0],
                TokenFilter.PARAM_INCLUDEDTOKENCLASSES, new String[0],
                TokenFilter.PARAM_INCLUDEDTOKENTYPES, 	new Integer[0],
                "LanguageID", 							"en",

                DictionaryResource_impl.PARAM_DUMPDICT, false  //true 
			);

		ExternalResourceDescription[] externalResources 
			= conceptMapperDescription.getResourceManagerConfiguration().getExternalResources();
        ExternalResourceDescription dictionaryFileResourceDesc = externalResources[0];
        dictionaryFileResourceDesc.getResourceSpecifier().setAttributeValue(
			"fileUrl", dictionaryFile.getAbsolutePath());
        conceptMapperDescription.getResourceManagerConfiguration().setExternalResources(
                new ExternalResourceDescription[] { dictionaryFileResourceDesc });
		// DEBUG org.springframework.validation.DataBinder  - DataBinder requires binding of required fields 
		// [outFile,writeDocumentMetaData,rawFeaturePatterns,rawTypePatterns]
		descriptions.add(conceptMapperDescription);
		**/

		String[] stopwordList = {};
		AnalysisEngineDescription conceptMapperDescFromFactory = 
             ConceptMapperAggregateFactory.getOffsetTokenizerConceptMapperAggregateDescription(
                tsd,
                //dictionaryFile.getAbsolutePath(),
                dictionaryFile,
                ConceptMapperFactory.TokenNormalizerConfigParam.CaseMatchParamValue.CASE_IGNORE,// CASE_SENSITIVE,
                ConceptMapperFactory.SearchStrategyParamValue.SKIP_ANY_MATCH_ALLOW_OVERLAP,// CONTIGUOUS_MATCH,
                //Sentence.class, // spanFeatureStructureClass, //ExplicitSentenceCasInserter.SENTENCE_ANNOTATION_CLASS,
                ExplicitSentenceCasInserter.SENTENCE_ANNOTATION_CLASS,
                null,           // stemmerClass,
                stopwordList,
                true,           // orderIndependentLookup,
                true,           // findAllMatches,
                false           //replaceCommaWithAnd
            );
		descriptions.add(conceptMapperDescFromFactory);


		// CAS Dumper
        descriptions.add(AnalysisEngineFactory.createPrimitiveDescription(
            CASDumpWriter.class,
            CASDumpWriter.PARAM_OUTPUT_FILE, "cm_output.txt"));

		// Dict Term Reporter 
        descriptions.add(AnalysisEngineFactory.createPrimitiveDescription(
			DictTermReporter.class));

		return descriptions;
	}


	public void go(File dictionaryFile, File inputDir)
	throws UIMAException, ResourceInitializationException, FileNotFoundException, IOException {

		List<AnalysisEngineDescription> aeDescList
			= getPipelineAeDescriptions(dictionaryFile);

        CollectionReader cr = CollectionReaderFactory.createCollectionReader(
			FileSystemCollectionReader.class,
			tsd,
			FileSystemCollectionReader.PARAM_INPUTDIR,	inputDir,
			FileSystemCollectionReader.PARAM_ENCODING,	"UTF-8",
			FileSystemCollectionReader.PARAM_LANGUAGE, 	"English",
			FileSystemCollectionReader.PARAM_XCAS, 		"false",
			FileSystemCollectionReader.PARAM_LENIENT,	"true"
        );

		SimplePipeline.runPipeline(cr, aeDescList.toArray(new AnalysisEngineDescription[0]));
    }

	
	protected static void usage() {
	 	System.out.println("mvn exec:java -Dinput=<input tree> -Ddictionar=<dictionary file>");
		System.out.println("mvn exec:java -Dinput=/Users/roederc/data/fulltext/pmc/Yeast -Ddictionary=target/classes/dictionaries/cmDict-APO.xml");
	}


	public static void main(String[] args) {

		if (args.length < 2) {
			usage();
			System.exit(1);
		}

			File dictionaryFile  = null;
			File inputDir = null;
		try {
			inputDir = new File(args[0]);
			dictionaryFile  = new File(args[1]);;
		
		} catch(Exception x) {
			System.out.println("error:" + x);
			x.printStackTrace();
			usage();
			System.exit(2);
		}
		try {
			ConceptMapperUimaFitPipeline pipeline = new ConceptMapperUimaFitPipeline ();
	
			BasicConfigurator.configure();
	
			System.out.println("going with "
				+ " dictionaryFile:" + dictionaryFile
				+ " inputDir:" + inputDir);	
			pipeline.go(dictionaryFile,  inputDir);
		}
		catch(Exception x) {
			System.err.println(x);
			x.printStackTrace();
			System.exit(3);
		}
	}

}
