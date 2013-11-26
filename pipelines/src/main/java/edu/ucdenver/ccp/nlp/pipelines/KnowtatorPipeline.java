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
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.apache.uima.collection.CollectionReader;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.jcas.JCas;
import org.apache.uima.tools.components.FileSystemCollectionReader;

import org.apache.uima.examples.SourceDocumentInformation;

import org.apache.uima.conceptMapper.DictTerm;
import org.uimafit.factory.AnalysisEngineFactory;
import org.uimafit.factory.CollectionReaderFactory;
import org.uimafit.factory.TypeSystemDescriptionFactory;
import org.uimafit.pipeline.SimplePipeline;
import org.uimafit.pipeline.JCasIterable;

import edu.ucdenver.ccp.nlp.ae.Knowtator_AE;
import edu.ucdenver.ccp.nlp.ae.Protein_AE;
import edu.ucdenver.ccp.nlp.ae.Debug_AE;

public class KnowtatorPipeline extends BaseUimaFitPipeline  {

	private static Logger logger = Logger.getLogger(KnowtatorPipeline.class);


	KnowtatorPipeline(File inputDir) throws ResourceInitializationException {
		super(inputDir);

		aeDescList.add(
 			AnalysisEngineFactory.createPrimitiveDescription(Protein_AE.class));
		aeDescList.add(
 			AnalysisEngineFactory.createPrimitiveDescription(Knowtator_AE.class));
		aeDescList.add(
 			AnalysisEngineFactory.createPrimitiveDescription(Debug_AE.class));
    }

	public static void main(String[] args) {

		BasicConfigurator.configure();
		File inputDir = null;

		// get args
		if (args.length < 1) {
			usage();
			System.exit(1);
		}
		try {
			inputDir = new File(args[0]);
		
		} catch(Exception x) {
			System.out.println("error:" + x);
			x.printStackTrace();
			usage();
			System.exit(2);
		}


		// main part
		try {
			KnowtatorPipeline pipeline = new KnowtatorPipeline (inputDir);
			pipeline.go(inputDir);
		}
		catch(Exception x) {
			System.err.println(x);
			x.printStackTrace();
			System.exit(3);
		}
	}


}
