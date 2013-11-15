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

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.FSIndex;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JFSIndexRepository;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.cas.StringArray;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.cas.text.AnnotationIndex;


import org.uimafit.factory.AnalysisEngineFactory;
import org.uimafit.descriptor.SofaCapability;


import edu.ucdenver.ccp.nlp.ts.IdDictTerm;
import org.apache.uima.conceptMapper.support.tokenizer.TokenAnnotation;

import edu.ucdenver.ccp.nlp.ts.SentenceAnnotation;
import edu.ucdenver.ccp.nlp.ts.TextAnnotation;
import edu.ucdenver.ccp.nlp.ts.Annotator;
import edu.ucdenver.ccp.nlp.ts.ClassMention;
import edu.ucdenver.ccp.nlp.tsx.ClassMentionX;
import edu.ucdenver.ccp.nlp.ts.IntegerSlotMention;
import edu.ucdenver.ccp.nlp.ts.StringSlotMention;
import edu.ucdenver.ccp.nlp.ts.PrimitiveSlotMention;
import edu.ucdenver.ccp.nlp.ts.ComplexSlotMention;


/**
 * This is not an AnalysisEngine, though it reads a CAS and extracts
 * information from it, returning a Collection of Results.
 * Only deals with String slots for now..
 * 
 * The Simple Rest Server from the UIMA Sandbox does something similar.
 * The problem with AE's is that  you only have a reference to a descriptor,
 * not the actual AE. So, if you want to return data from the, you don't
 * have a handle to it to use to call a function on it, and there's 
 * no way through the descriptor. This extractor depends on 
 * a local pipeline execution engine that allows for CAS (JCAS) access.
 * Once you have that, you can pass a JCAS to an extractor and get
 * the data this whole exercise is about.
 *
 * Of course, an alternative is to write a file, and in many
 * batch oriented UIMA pipelines, we use a (deprecated) CAS Consumer
 * or an AE that acts as one to write a file from the CAS passed to it.
 * The point here is interactive programmatic access.
 */
public class  JCasExtractor {

	public JCasExtractor() { }

	static class Result {
		String name;
		Map<String, String> attributes;
		
		public Result(String name) {
			this.name = name;
			attributes = new TreeMap<String, String>();
		}

		public String getName() { return name; }

		public void add(String name, String value) {
			attributes.put(name, value);
		}
	
		public String get(String name) {
			return attributes.get(name);
		}
		public Set<String> getKeys() {
			return attributes.keySet();
		}
	}


	public Collection<Result> extract(JCas jcas) {

		List<Result> resultsList = new ArrayList<Result>();
		String doc = jcas.getDocumentText();
        FSIterator<Annotation> annotIter = jcas.getJFSIndexRepository().getAnnotationIndex().iterator();
        while (annotIter.hasNext()) {
	        Annotation annot = (Annotation) annotIter.next();
			if (annot instanceof IdDictTerm) {
				Result result = new Result("dictTerm for \"" + annot.getCoveredText() + "\"");

                IdDictTerm dt = (IdDictTerm) annot;
				result.add("id", dt.getId());						
				result.add("canonical", dt.getDictCanon());						
				// from ConceptMapper (returns null)
				//result.add("text", dt.getMatchedText());						

				result.add("begin", "" + annot.getBegin() );						
				result.add("end", "" + annot.getEnd());						
				// calculated, should match text
				result.add("covered", doc.substring(annot.getBegin(), annot.getEnd()));

				resultsList.add(result);
            }
		}

		return resultsList;
    }


}

