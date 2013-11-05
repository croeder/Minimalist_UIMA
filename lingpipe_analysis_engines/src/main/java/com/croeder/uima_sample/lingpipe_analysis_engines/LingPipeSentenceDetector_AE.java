
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

