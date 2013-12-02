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
package edu.ucdenver.ccp.nlp.doc;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.After;
import org.junit.Test;

import java.util.List;

import java.io.IOException;

public class PmcDocumentProviderTest {
	
	DocumentProvider da;
	
	@Before
	public void setup() {
		da = new PmcDocumentProvider();
	}

	@Test 
	public void testMaxBatchIndex() {
		int maxIndex = da.getMaxBatchIndex();
		assertEquals(628, maxIndex);
	}

	@Test 
	public void testGetIdRange() {
		List<String> list = da.getIdRange(100);
		assertEquals("16857050", list.get(0));
		assertEquals("16907971",list.get(999));
	}

	@Test 
	public void getDocumentText() throws IOException {
		assertEquals(
		"<!DOCTYPE article PUBLIC \"-//NLM//DTD Journal Archiving and Interchange DTD v2.3 20070202//EN\" \"archivearticle.dtd\">\n<article xmlns:xlink=\"http://www.w3.org/1999/xlink\" article-type=\"research-article\"><?properties open_access?><front><journal-meta><journal-id journal-id-type=\"nlm-ta\">J Cell Biol</journal-id><journal-title>The Journal of Cell Biology</journal-title><issn pub-type=\"ppub\">0021-9525</issn><issn pub-type=\"epub\">1540-8140</issn><publisher><publisher-name>The Rockefeller University Press</publisher-name></publisher></journal-meta><article-meta><article-id pub-id-type=\"pmid\">182701</article-id><article-id pub-id-type=\"pmc\">2109846</article-id><article-id pub-id-type=\"publisher-id\">76260394</article-id><article-categories><subj-group subj-group-type=\"heading\"><subject>Articles</subject></subj-group></article-categories><title-group><article-title>Fibrils attached to the nuclear pore prevent egress of SV40 particles from the infected nucleus</article-title></title-group><pub-date pub-type=\"ppub\"><day>1</day><month>9</month><year>1976</year></pub-date><volume>70</volume><issue>3</issue><fpage>714</fpage><lpage>719</lpage><permissions></permissions><abstract><p>SV40 particles can apparently enter the nucleus intact. However, they do not leave the nucleus despite the high concentration present during the productive phase. We found structural evidence that SV40 virus is prevented from approaching the most likely site of exit, the nuclear pore complex. From these images, it is concluded that the fibrils attached to the nuclear pore complex prevent egress of SV40 particles from the infected nucleus.</p></abstract></article-meta></front></article>\n\n\n", 
		da.getDocumentText("/RAID1/data/fulltext/pmc/files/J_Cell_Biol/J_Cell_Biol_1976_Sep_1_70(3)_714-719.nxml"));
	}

}

