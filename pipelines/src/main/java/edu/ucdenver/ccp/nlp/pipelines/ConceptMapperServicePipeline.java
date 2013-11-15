/*
 Copyright (c) 2013, Regents of the University of Colorado
 All rights reserved.

 Redistribution and use in source and binary forms, with or without modification,
 are permitted provided that the following conditions are met:

 * Redistributions of source code must retain the above copyright notice, this
    list of conditions and the following disclaimer.

 * Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 * Neither the name of the University of Colorado nor the names of its
    contributors may be used to endorse or promote products derived from this
    software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
 ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package edu.ucdenver.ccp.nlp.pipelines;


import java.io.IOException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

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

import edu.ucdenver.ccp.nlp.ae.Debug_AE;

import org.xml.sax.SAXException;


/**
 * modifies the base ConceptMapper pipeline so it doesn't read a
 * directory with a collection reader, rather it works with a 
 * document passed in as a string.
 * It passes in a bogus directory to the CR initialized in the
 * base class, then doesn't call runPipeline (which would call the CR)
 * and instead calls runServicePipeline on the string.
 * 
 * TODO: factor out the runPipeline method and separate creating the pipeline from running it
 * or feeding it input.
 */
public class ConceptMapperServicePipeline extends ConceptMapperPipeline  {

	private static Logger logger = Logger.getLogger(ConceptMapperServicePipeline.class);

	ConceptMapperServicePipeline() throws UIMAException, IOException {
		super(new File("/")); // must exist, but won't be used (TODO: that's gross)
	}
	
 	public Collection<JCasExtractor.Result> runServicePipeline(String document) 
	throws UIMAException, IOException {

        Collection<JCasExtractor.Result> results =  new ArrayList<JCasExtractor.Result>();
        final AnalysisEngineDescription aaeDesc
            = AnalysisEngineFactory.createAggregateDescription(
                engineDescs.toArray(new AnalysisEngineDescription[0]));
        final AnalysisEngine aae
            = AnalysisEngineFactory.createAggregate(aaeDesc);

		JCas jcas = JCasFactory.createJCas(tsd);
		jcas.setDocumentText(document);

        try {
        	aae.process(jcas);
            if (extractor != null) {
                Collection<JCasExtractor.Result> docResults = extractor.extract(jcas);
                results.addAll(docResults);
            }
            jcas.reset();
            aae.collectionProcessComplete();

            return results;
        }
        finally {
            aae.destroy();
            reader.close();
            reader.destroy();
        }
    }



	protected static void usage() {
	 	System.out.println("mvn exec:java  ");
	}


	public static void main(String[] args) {

		if (args.length < 1) {
			usage();
			System.exit(1);
		}

		File inputFile = null;
		try {
			inputFile = new File(args[0]);
		} 
		catch(Exception x) {
			System.out.println("error:" + x);
			x.printStackTrace();
			usage();
			System.exit(2);
		}
		try {
		
			StringBuilder docString = new StringBuilder();
            InputStreamReader isr = new InputStreamReader(new FileInputStream(inputFile), StandardCharsets.UTF_8);
			BufferedReader br = new BufferedReader(isr);
			while (br.ready()) {
				docString.append(br.readLine());
			}

			ConceptMapperServicePipeline pipeline = new ConceptMapperServicePipeline();
	
			BasicConfigurator.configure();
	
			System.out.println("going with " + " inputFile:" + inputFile);	
			Collection<JCasExtractor.Result> results = pipeline.runServicePipeline(docString.toString());

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
