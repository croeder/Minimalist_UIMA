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
import org.uimafit.factory.ExternalResourceFactory;
import org.uimafit.factory.JCasFactory;
import org.uimafit.factory.ResourceCreationSpecifierFactory;
import org.uimafit.factory.TypeSystemDescriptionFactory;
import org.uimafit.pipeline.JCasIterable;

import opennlp.uima.util.UimaUtil;
import opennlp.uima.sentdetect.SentenceDetector;
import opennlp.uima.sentdetect.SentenceModelResourceImpl;
import opennlp.uima.tokenize.Tokenizer;
import opennlp.uima.tokenize.TokenizerModelResourceImpl;

import uima.tt.TokenAnnotation;

import edu.ucdenver.ccp.nlp.ts.SentenceAnnotation;
import edu.ucdenver.ccp.nlp.cr.MedlineDbCollectionReader;
import edu.ucdenver.ccp.nlp.cr.PmcOaDbCollectionReader;
import edu.ucdenver.ccp.nlp.cr.ElsevierArt5DbCollectionReader;
import edu.ucdenver.ccp.nlp.ae.Debug_AE;
import edu.ucdenver.ccp.nlp.ae.opendmap.OpenDMAP_AE;
import edu.ucdenver.ccp.nlp.ae.ConceptMapper2CCPTypeSystemConverter_AE;
import edu.ucdenver.ccp.nlp.ae.ClassMentionRemovalFilter_AE;

import edu.ucdenver.ccp.nlp.ae.MapNameToIDSlot_AE;

import org.xml.sax.SAXException;



public class DMapPipeline extends BaseUimaFitPipeline  {
	private static Logger logger = Logger.getLogger(ConceptMapperPipeline.class);

    final String sentenceModelUrl = "http://opennlp.sourceforge.net/models-1.5/en-sent.bin";
    final String tokenizerModelUrl = "http://opennlp.sourceforge.net/models-1.5/en-token.bin";

    String sentenceSpanName = "edu.ucdenver.ccp.nlp.ts.Sentence";

	DMapPipeline(int batchNum) 
	throws UIMAException, IOException {

        //cr = MedlineDbCollectionReader.createCollectionReader(tsd,1);
        //cr = PmcOaDbCollectionReader.createCollectionReader(tsd,1);
        cr = ElsevierArt5DbCollectionReader.createCollectionReader(tsd, batchNum);


        // SENTENCE DETECTOR 
		AnalysisEngineDescription sentenceDesc = AnalysisEngineFactory.createPrimitiveDescription(
        	SentenceDetector.class, UimaUtil.SENTENCE_TYPE_PARAMETER, SentenceAnnotation.class.getName());
        ExternalResourceFactory.createDependencyAndBind(sentenceDesc,
                UimaUtil.MODEL_PARAMETER, SentenceModelResourceImpl.class,
                sentenceModelUrl);
        aeDescList.add(sentenceDesc);
        //AnalysisEngineDescription sentenceDetectorDesc
        //   = LingPipeSentenceDetector_AE.createAnalysisEngineDescription(tsd);


		// TOKENIZER from xml files ** TODO:  THE PATH MUST BE A FILE SYSTEM PATH **
		Object[] config = new Object[0];
  		ResourceSpecifier tokenizerDesc 
			= ResourceCreationSpecifierFactory.createResourceCreationSpecifier(
				"target/classes/descriptors/analysis_engine/primitive/OffsetTokenizer.xml", config);
		aeDescList.add(tokenizerDesc);


		// CONCEPT MAPPER from xml files ** ...FILE SYSTEM PATH **
		// The dictionary is specified, in this case, in the xml file.
  		ResourceSpecifier conceptMapperDesc2 
			= ResourceCreationSpecifierFactory.createResourceCreationSpecifier(
				"target/classes/descriptors/analysis_engine/primitive/ConceptMapperOffsetTokenizer-PR.xml", config);
		aeDescList.add(conceptMapperDesc2);

		// CONCEPT MAPPER from xml files ** ...FILE SYSTEM PATH **
		// The dictionary is specified, in this case, in the xml file.
  		ResourceSpecifier conceptMapperDesc 
			= ResourceCreationSpecifierFactory.createResourceCreationSpecifier(
				"target/classes/descriptors/analysis_engine/primitive/ConceptMapperOffsetTokenizer-CL.xml", config);
		aeDescList.add(conceptMapperDesc);


  		// TS CONVERTER
        AnalysisEngineDescription converterDesc
            = ConceptMapper2CCPTypeSystemConverter_AE.createAnalysisEngineDescription(tsd);
        aeDescList.add(converterDesc);


        ////////// GeneTUKit AKA  bioMedNer - NEEDS SOURCE VIEW NAME
       // System.out.println("-- NER --");
       // AnalysisEngineDescription gtiDesc = GeneTUKit_AE.createAnalysisEngineDescription(tsd);
       // engineDescs.add(gtiDesc);

        // MENTION NAME MAPPER: CL:00000011 --> "cell_type", slot ID with value "CL:000000011"
		// maps output of concept mapper (class mention named CL:00000xxx) to 
		// something for dmap: class mention named "cell_type" with a slot named ID with value CL:0000xxx
        AnalysisEngineDescription clMapperDesc = MapNameToIDSlot_AE.createAnalysisEngineDescription(tsd, "cell_type", "CL:[0-9]+");
        aeDescList.add(clMapperDesc);

        // MENTION NAME MAPPER: PR:00000011 --> "normalized_gene"
        AnalysisEngineDescription prMapperDesc = MapNameToIDSlot_AE.createAnalysisEngineDescription(tsd, "normalized_gene", "PR:[0-9]+");
        aeDescList.add(prMapperDesc);

        // DMAP
        String contextFileName = "target/classes/tissue_specific_proteins_context.xml";
        String configFileName = "target/classes/tissue_specific_proteins_config.xml";
        AnalysisEngineDescription dmapDesc = OpenDMAP_AE.createAnalysisEngineDescription(tsd, true, true,
            contextFileName, configFileName, sentenceSpanName, false, true);
        aeDescList.add(dmapDesc);
/***
        // CLASS MENTION FILTER
        AnalysisEngineDescription tokenRemovalDesc
            = ClassMentionRemovalFilter_AE.createAnalysisEngineDescription(
                tsd, new String[] { "token", "sentence" });
        aeDescList.add(tokenRemovalDesc);
**/
        AnalysisEngineDescription debugDesc = Debug_AE.createAnalysisEngineDescription(tsd);
        aeDescList.add(debugDesc);
	}

	protected static void usage() {
	 	System.out.println("mvn exec:java -Dbatch=<batch_num> ");
	}


	public static void main(String[] args) {
		BasicConfigurator.configure();

		if (args.length < 1) {
			usage();
			System.exit(1);
		}

		int batchNumber=0;
		try {
			batchNumber = Integer.parseInt(args[0]);
		} 
		catch(Exception x) {
			System.out.println("error:" + x);
			x.printStackTrace();
			usage();
			System.exit(2);
		}

		try {
			DMapPipeline pipeline = new DMapPipeline(batchNumber);
			//Collection<JCasExtractor.Result> results = pipeline.go();
			pipeline.go();
		}
		catch(Exception x) {
			System.err.println(x);
			x.printStackTrace();
			System.exit(3);
		}
	}
}
