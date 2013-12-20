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
package com.croeder.uima_sample.analysis_engines;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.HashSet;


import org.apache.uima.UimaContext;
import org.apache.uima.jcas.JCas;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Level;

import org.uimafit.factory.AnalysisEngineFactory;
import org.uimafit.component.JCasAnnotator_ImplBase;
import org.uimafit.descriptor.ConfigurationParameter;
import org.uimafit.factory.ConfigurationParameterFactory;

import com.aliasi.chunk.Chunk;
import com.aliasi.chunk.Chunking;
import com.aliasi.sentences.MedlineSentenceModel;
import com.aliasi.sentences.SentenceChunker;
import com.aliasi.sentences.SentenceModel;
import com.aliasi.tokenizer.IndoEuropeanTokenizerFactory;
import com.aliasi.tokenizer.TokenizerFactory;

import com.croeder.uima_sample.annotation.SentenceAnnotation;
import com.croeder.uima_sample.annotation.AnnotationSet;
import com.croeder.uima_sample.annotation.Annotator;



public class LingPipeSentenceDetector_AE  extends JCasAnnotator_ImplBase {


	private final SentenceChunker sentenceChunker 
		= new SentenceChunker(
			new IndoEuropeanTokenizerFactory(),
			new MedlineSentenceModel());

	@Override
	public void initialize(UimaContext context) throws ResourceInitializationException {
		super.initialize(context);
	}

	@Override
	public void process(JCas jCas) throws AnalysisEngineProcessException {
		// TODO put 22 into an enum or properties file
		Annotator annotator = new Annotator(jCas);
		annotator.setAnnotatorID(22);
		annotator.setFirstName("UCDenver-CCP");
		annotator.setLastName("LingPipe");
		annotator.setAffiliation("Alias-i");
		String documentText = jCas.getDocumentText();

		// GET SENTENCES
		Chunking chunking = sentenceChunker.chunk(documentText.toCharArray(), 0, documentText.length());
		Set<Chunk> chunks = chunking.chunkSet();


		// CREATE ANNOTATIONS
		Collection<SentenceAnnotation> annotations = new ArrayList<SentenceAnnotation>();
		for (Chunk chunk : chunks) {
			int start = chunk.start();
			int end = chunk.end();

			SentenceAnnotation sa = new SentenceAnnotation(jCas, start, end);
			sa.setAnnotator(annotator);
			sa.addToIndexes();
		}

	}		

	public static AnalysisEngineDescription createAnalysisEngineDescription(TypeSystemDescription tsd)
	throws ResourceInitializationException {
		return AnalysisEngineFactory
				.createPrimitiveDescription(LingPipeSentenceDetector_AE.class, tsd);
	}
}

