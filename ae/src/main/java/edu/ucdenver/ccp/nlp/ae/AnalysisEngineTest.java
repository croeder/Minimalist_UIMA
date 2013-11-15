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
package com.croeder.uima_sample.ae;

import java.io.IOException;

import org.apache.uima.UIMAException;
import org.apache.uima.cas.CASException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.metadata.TypeSystemDescription;

import org.junit.After;
import org.junit.Before;
import org.uimafit.factory.JCasFactory;
import org.uimafit.factory.TypeSystemDescriptionFactory;

import edu.ucdenver.ccp.nlp.ts.TokenAnnotation;
import edu.ucdenver.ccp.nlp.ts.SentenceAnnotation;

public class AnalysisEngineTest {


	protected TypeSystemDescription tsd;
	protected JCas jCas;

	protected void addJcasData() 
	throws UIMAException, IOException {
		String documentText  
			 //12345678901234567890123456789012345678901234567890
			 //00000000011111111112222222222333333333344444444445
		    = "Lorem ipsum dolor sit amet, consectetur adipiscing"
			+ " elit. Vivamus volutpat ut dui ut pretium. Praesent non quam massa. ";
			 //123456789012345678901234567890123456789012345678901234567890123456789
			 //555555555666666666677777777778888888888999999999900000000001111111111

		TokenAnnotation ta;
		ta = new TokenAnnotation(jCas, 1,6); ta.addToIndexes();  // Lorem
		ta = new TokenAnnotation(jCas, 7,12); ta.addToIndexes(); // ipsum
		ta = new TokenAnnotation(jCas, 13,18); ta.addToIndexes();// dolor
		ta = new TokenAnnotation(jCas, 19,22); ta.addToIndexes();// sit
		ta = new TokenAnnotation(jCas, 23,27); ta.addToIndexes();// amet
		ta = new TokenAnnotation(jCas, 27,28); ta.addToIndexes();// ,
		ta = new TokenAnnotation(jCas, 29,40); ta.addToIndexes();// consectetur
		ta = new TokenAnnotation(jCas, 41,51); ta.addToIndexes();// adipiscing
		ta = new TokenAnnotation(jCas, 52,56); ta.addToIndexes();// elit
		ta = new TokenAnnotation(jCas, 56,57); ta.addToIndexes();// .

		SentenceAnnotation  sa;
		sa = new SentenceAnnotation(jCas, 01,57); sa.addToIndexes();
		sa = new SentenceAnnotation(jCas, 58,118); sa.addToIndexes();
	}


	@Before
	public void setUp() throws UIMAException, IOException {
		tsd = getTypeSystem();
		jCas = JCasFactory.createJCas(tsd);
		addJcasData();
	}

	protected TypeSystemDescription getTypeSystem() {
		return TypeSystemDescriptionFactory.createTypeSystemDescription(
			"edu.ucdenver.ccp.nlp.TypeSystem");
	}

	@After
	public void tearDown() {
		if (jCas != null) {
			jCas.release();
		}
	}


}
