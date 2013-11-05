package com.croeder.uima_sample.analysis_engines;

import java.io.IOException;

import org.junit.Test;
import org.junit.Assert;

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.UIMAException;
import org.apache.uima.resource.ResourceInitializationException;

import org.uimafit.pipeline.SimplePipeline;

import com.croeder.uima_sample.annotation.SentenceAnnotation;
import com.croeder.uima_sample.analysis_engines.LingPipeSentenceDetector_AE;

public class LingPipeSentenceDetectorAeTest  extends AnalysisEngineTest {

	final int sentenceSpans[][] = { {0, 34}, {35, 63}, {64, 106} };

	@Test
	public void test()
	throws ResourceInitializationException, UIMAException, IOException {
		AnalysisEngineDescription aed  
			=  LingPipeSentenceDetector_AE.createAnalysisEngineDescription(tsd);
		SimplePipeline.runPipeline(jCas, aed);

		FSIterator iter = jCas.getJFSIndexRepository().getAnnotationIndex(SentenceAnnotation.type).iterator();

		int num=0;
		while (iter.hasNext()) {
			SentenceAnnotation sentence = (SentenceAnnotation) iter.next();
			System.out.println("\"" + sentence.getCoveredText() + "\"");
			Assert.assertEquals(sentenceSpans[num][0] , sentence.getStart());
			Assert.assertEquals(sentenceSpans[num][1] , sentence.getEnd());
			num++;
		}
		Assert.assertEquals(3, num);

	}

	protected void addJcasData() 
	throws UIMAException, IOException {
		String documentText  
		    = "This is a proper English Sentence. Another sentence follows it. One more sentence makes it not too simple.";
			 //012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789
			 //000000000011111111112222222222333333333344444444445555555555666666666677777777778888888888999999999900000000001111111111

		jCas.setDocumentText(documentText);

		/*
		SentenceAnnotation  sa;
		sa = new SentenceAnnotation(jCas, 01,57); sa.addToIndexes();
		sa = new SentenceAnnotation(jCas, 58,118); sa.addToIndexes();
		*/
	}
}
