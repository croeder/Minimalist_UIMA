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
import static  org.junit.Assert.assertEquals;
import static  org.junit.Assert.assertTrue;
import static  org.junit.Assert.assertNotNull;

import java.util.Collection;

import org.junit.Ignore;
import org.junit.Test;

import org.apache.uima.UIMAException;
import org.apache.uima.collection.CollectionReader;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.TypeSystemDescription;

import org.uimafit.factory.CollectionReaderFactory;
import org.uimafit.pipeline.JCasIterable;
import org.uimafit.util.JCasUtil;
import org.uimafit.factory.AnalysisEngineFactory;
import org.uimafit.factory.CollectionReaderFactory;
import org.uimafit.factory.TypeSystemDescriptionFactory;

import edu.ucdenver.ccp.nlp.doc.DocumentProviderType;
import edu.ucdenver.ccp.nlp.ts.TextAnnotation;
import edu.ucdenver.ccp.nlp.ts.ClassMention;
import edu.ucdenver.ccp.nlp.ts.SlotMention;
import edu.ucdenver.ccp.nlp.ts.StringSlotMention;

public class ElsevierArt5DbCollectionReaderTest {


	@Test
	public void testCollectionReaderElsevierArt5() throws Exception {
		String[] typeSystemDescriptions = { "edu.ucdenver.ccp.nlp.ts.TypeSystem" };
		TypeSystemDescription tsd 
			= TypeSystemDescriptionFactory.createTypeSystemDescription(typeSystemDescriptions); 
		CollectionReader cr = ElsevierArt5DbCollectionReader.createCollectionReader(tsd, 1);

		JCasIterable iter = new JCasIterable(cr);
		assertTrue(iter.hasNext());
		JCas jcas = iter.next();
		assertEquals("E-Poster abstractCardiac Imaging (E-poster 90-98)",
			jcas.getDocumentText());


		int i=0;
        Collection<TextAnnotation> textAnnos = JCasUtil.select(jcas, TextAnnotation.class);
		for (TextAnnotation ta : textAnnos) {

			ClassMention cm = ta.getClassMention();
			if (cm.getSlotMentions() != null) {
				StringSlotMention sm = (StringSlotMention) cm.getSlotMentions(0);
				//out.println("xxxxxxxxxxxxxxxxx" + ta.getCoveredText() + ", " + cm.getMentionName() + ", " + sm.getMentionName() + ", " + sm.getSlotValues(0) );
			}
			else {
					//out.println("xxxxxxxxxxxxxxxxx" + ta.getCoveredText() + ", " + cm.getMentionName() );
			}
			if (i==0) {
				//null:TITLE, 0:97
				assertEquals("Section", cm.getMentionName());
				assertEquals(17, ta.getBegin());
				assertEquals(49, ta.getEnd());
			}
			else if (i==1) {
				//null:PARAGRAPH, 97:541
				assertEquals("Section", cm.getMentionName());
				assertEquals(49, ta.getBegin());
				assertEquals(49, ta.getEnd());
			}
			i++;
		}
	}

}
