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
package edu.ucdenver.ccp.nlp.cr;


import static java.lang.System.out;

import java.io.IOException;

import java.util.List;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import org.apache.uima.UimaContext;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.cas.StringArray;
import org.apache.uima.resource.ResourceInitializationException;

import edu.ucdenver.ccp.nlp.doc.CcpXmlParser.Annotation;

import edu.ucdenver.ccp.nlp.ts.TextAnnotation;
import edu.ucdenver.ccp.nlp.ts.ClassMention;
import edu.ucdenver.ccp.nlp.ts.StringSlotMention;
import edu.ucdenver.ccp.nlp.ts.Span;
import edu.ucdenver.ccp.nlp.ts.Annotator;
import edu.ucdenver.ccp.nlp.ts.AnnotationSet;

/**
 * This class converts simple CcpXmlParser.Annotation objects into UIMA objects from the CCP Type System.
 */
public class CcpXmlAnnotationFactory {

		public static List<TextAnnotation> convert(JCas jcas, List<Annotation> inputAnnotations) {

			List<TextAnnotation> outputAnnotations = new ArrayList<>();
			for (Annotation a : inputAnnotations) {
				TextAnnotation ta = new TextAnnotation(jcas, a.start, a.end);

				// Spans
				Span s = new Span(jcas);
				s.setSpanStart(a.start);
				s.setSpanEnd(a.end);
				FSArray spanArray = new FSArray(jcas, 1);
				spanArray.set(0, s);
				ta.setSpans(spanArray);

				//  Annotator
				Annotator annotator = new Annotator(jcas);
				annotator.setFirstName("CcpXmlAnnotationFactory");
				annotator.setLastName("edu.ucdenver.ccp.nlp.cr");
				annotator.setAffiliation("http://compbio.ucdenver.edu");
				ta.setAnnotator(annotator);	

				// ClassMention
				ClassMention cm = new ClassMention(jcas);
				cm.setMentionName("Section");
				cm.setTextAnnotation(ta);
				ta.setClassMention(cm);

				FSArray slots = new FSArray(jcas, 2);
				cm.setSlotMentions(slots);
				{
					// Type Slot
					StringSlotMention sm = new StringSlotMention(jcas);	
					sm.setMentionName("TYPE");
					StringArray values = new StringArray(jcas, 1);
					values.set(0, a.type);
					sm.setSlotValues(values);	
					cm.setSlotMentions(0,sm);
				}

				if (a.name != null) {
					// Name Slot
					StringSlotMention sm = new StringSlotMention(jcas);	
					sm.setMentionName("NAME");
					StringArray values = new StringArray(jcas, 1);
					values.set(0, a.name);
					sm.setSlotValues(values);
					cm.setSlotMentions(1,sm);
				}

				ta.addToIndexes();

			}
			return outputAnnotations;
		}
}
