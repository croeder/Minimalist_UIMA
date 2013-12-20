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


import static  org.junit.Assert.assertEquals;
import static  org.junit.Assert.assertTrue;
import static  org.junit.Assert.assertNotNull;
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


public class DbCollectionReaderTest {

	

	@Test
	public void testCollectionReaderMedline() throws Exception {
		String[] typeSystemDescriptions = { "edu.ucdenver.ccp.nlp.ts.TypeSystem" };
		TypeSystemDescription tsd 
			= TypeSystemDescriptionFactory.createTypeSystemDescription(typeSystemDescriptions); 
		CollectionReader cr = CollectionReaderFactory.createCollectionReader(
			DbCollectionReader.class, tsd,
			DbCollectionReader.PARAM_BATCH_NUMBER, 1,
			DbCollectionReader.PARAM_COLLECTION_TYPE, DocumentProviderType.Medline.toString());


		JCasIterable iter = new JCasIterable(cr);
		assertTrue(iter.hasNext());
		JCas jcas = iter.next();
		assertEquals(
			jcas.getDocumentText(),
			"In order to throw light on the problems related to the magnitude and the possibility of maintaining pressor response in the case of bilateral carotid occlusion (BCO), acute experiments were carried out on heparinized cats in chloralose-urethane narcosis and spontaneous respiration. The perfusion pressure in a hind leg autoperfused with a roller pump with a constant flow and the arterial blood pressure were recorded electromanometrically. A study was made of the changes taking place under the effect of BCO in the normal animal, in animals in a haemorrhagic state, after pharmacological alpha-adrenergic blockade, haemorrhage after alpha-adrenergic blockade, retransfusion of blood + alpha-adrenergic blocking agent and after local application of 0.01 papaverine. It was established that some of the factors determining the haemodynamic state of the organism, such as: blood volume, arterial pressure, vascular resistance, cardiac output, etc., are of great significance for the realization of the pressor response to BCO, but the haemodynamic state of the animal before the occlusion and the interactions between the abovementioned factors are decisive for the form, magnitude and maintenance of the pressor response in BCO.");
	}

	@Test
	public void testCollectionReaderMedlineBatch() throws Exception {
		String[] typeSystemDescriptions = { "edu.ucdenver.ccp.nlp.ts.TypeSystem" };
		TypeSystemDescription tsd 
			= TypeSystemDescriptionFactory.createTypeSystemDescription(typeSystemDescriptions); 
		CollectionReader cr = CollectionReaderFactory.createCollectionReader(
			DbCollectionReader.class, tsd,
			DbCollectionReader.PARAM_BATCH_NUMBER, 1,
			DbCollectionReader.PARAM_COLLECTION_TYPE, DocumentProviderType.Medline.toString());

		int count=0;
		JCasIterable iter = new JCasIterable(cr);
		while (iter.hasNext()) {
			JCas jcas = iter.next();
			count++;
		}
		assertEquals(1000, count);
	}

	@Test
	public void testCollectionReaderPMC() throws Exception {
		String[] typeSystemDescriptions = { "edu.ucdenver.ccp.nlp.ts.TypeSystem" };
		TypeSystemDescription tsd 
			= TypeSystemDescriptionFactory.createTypeSystemDescription(typeSystemDescriptions); 
		CollectionReader cr = CollectionReaderFactory.createCollectionReader(
			DbCollectionReader.class, tsd,
			DbCollectionReader.PARAM_BATCH_NUMBER, 1,
			DbCollectionReader.PARAM_COLLECTION_TYPE, DocumentProviderType.PMC.toString());


		JCasIterable iter = new JCasIterable(cr);
		assertTrue(iter.hasNext());
		JCas jcas = iter.next();
		assertEquals("<!DOCTYPE article PUBLIC \"-//NLM//DTD Journal Archiving and Interchange DTD v2.3 20070202//EN\" \"archivearticle.dtd\">\n<article xmlns:xlink=\"http://www.w3.org/1999/xlink\" article-type=\"research-article\"><?properties open_access?><front><journal-meta><journal-id journal-id-type=\"nlm-ta\">J Cell Biol</journal-id><journal-title>The Journal of Cell Biology</journal-title><issn pub-type=\"ppub\">0021-9525</issn><issn pub-type=\"epub\">1540-8140</issn><publisher><publisher-name>The Rockefeller University Press</publisher-name></publisher></journal-meta><article-meta><article-id pub-id-type=\"pmid\">182701</article-id><article-id pub-id-type=\"pmc\">2109846</article-id><article-id pub-id-type=\"publisher-id\">76260394</article-id><article-categories><subj-group subj-group-type=\"heading\"><subject>Articles</subject></subj-group></article-categories><title-group><article-title>Fibrils attached to the nuclear pore prevent egress of SV40 particles from the infected nucleus</article-title></title-group><pub-date pub-type=\"ppub\"><day>1</day><month>9</month><year>1976</year></pub-date><volume>70</volume><issue>3</issue><fpage>714</fpage><lpage>719</lpage><permissions></permissions><abstract><p>SV40 particles can apparently enter the nucleus intact. However, they do not leave the nucleus despite the high concentration present during the productive phase. We found structural evidence that SV40 virus is prevented from approaching the most likely site of exit, the nuclear pore complex. From these images, it is concluded that the fibrils attached to the nuclear pore complex prevent egress of SV40 particles from the infected nucleus.</p></abstract></article-meta></front></article>\n\n\n", 
		jcas.getDocumentText());
	}

}
