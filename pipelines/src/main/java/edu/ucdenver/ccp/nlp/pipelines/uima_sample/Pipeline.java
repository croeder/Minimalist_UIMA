package com.croeder.uima_sample;


import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import java.io.File;

import org.uimafit.factory.AnalysisEngineFactory;
import org.uimafit.factory.CollectionReaderFactory;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.cas.CAS;
import org.apache.uima.collection.CollectionReader;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.collection.base_cpm.BaseCollectionReader;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.Resource;
import org.apache.uima.resource.ResourceSpecifier;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.ResourceMetaData;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.apache.uima.util.CasCreationUtils;
import org.apache.uima.tools.components.FileSystemCollectionReader;

import org.uimafit.factory.AnalysisEngineFactory;
import org.uimafit.factory.CollectionReaderFactory;
import org.uimafit.factory.TypeSystemDescriptionFactory;
import org.uimafit.factory.ResourceCreationSpecifierFactory;
import org.uimafit.util.JCasUtil;
import org.uimafit.factory.JCasFactory;


import org.apache.log4j.Logger;

public class Pipeline {

	protected TypeSystemDescription tsd;
	protected JCas jcas;
	protected List<ResourceSpecifier> engineDescs;
	protected CollectionReader reader;
	protected String[] stopWordList = {};
	protected JCasExtractor extractor;

	static private  Logger logger = Logger.getLogger(Pipeline.class);
	protected static final String[] typeSystemStrs = {
       	"analysis_engine.primitive.DictTerm",
       	"com.croeder.uima_sample.ConceptMapperSupplementTypeSystem",
       	"com.croeder.uima_sample.TypeSystem",
       	"org.apache.uima.conceptMapper.support.tokenizer.TokenAnnotation",
       	"org.apache.uima.examples.SourceDocumentInformation"
    };


	public Pipeline(File inputDir, JCasExtractor extractor) 
		throws UIMAException, ResourceInitializationException {

		tsd= TypeSystemDescriptionFactory.createTypeSystemDescription(typeSystemStrs);

        reader = CollectionReaderFactory.createCollectionReader(
            FileSystemCollectionReader.class,
            tsd,
            FileSystemCollectionReader.PARAM_INPUTDIR,  inputDir,
            FileSystemCollectionReader.PARAM_ENCODING,  "UTF-8",
            FileSystemCollectionReader.PARAM_LANGUAGE,  "English",
            FileSystemCollectionReader.PARAM_XCAS,      "false",
            FileSystemCollectionReader.PARAM_LENIENT,   "true"
        );

		engineDescs = new ArrayList<ResourceSpecifier>();
        jcas = JCasFactory.createJCas(tsd);
		this.extractor = extractor;
	}


	public Collection<JCasExtractor.Result> runPipeline() throws UIMAException, IOException {
		Collection<JCasExtractor.Result> results =  new ArrayList<JCasExtractor.Result>();
		final AnalysisEngineDescription aaeDesc 
			= AnalysisEngineFactory.createAggregateDescription(
				engineDescs.toArray(new AnalysisEngineDescription[0]));
		final AnalysisEngine aae 
			= AnalysisEngineFactory.createAggregate(aaeDesc);
		
		try {
			int docCount = 0;
			while (reader.hasNext()) {
				reader.getNext(jcas.getCas());
				try {
					aae.process(jcas);
				}
				catch (Exception x) {
					String docId="unknown, count=" + docCount;
					logger.error("FILE FAILURE to process cas, moving to next.  doc count:" + docCount);
				}
				if (extractor != null) {
					Collection<JCasExtractor.Result> docResults = extractor.extract(jcas);
					results.addAll(docResults);
				}
				jcas.reset();
				docCount++;
			}
			
			aae.collectionProcessComplete();

			return results;
		}
		finally {
			aae.destroy();
			reader.close();
			reader.destroy();
		}
	}

}
