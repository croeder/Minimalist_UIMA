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
package com.croeder.sesame_interface;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import org.junit.Rule;

import java.io.File;
import java.util.List;
import java.util.List;

import org.apache.log4j.Logger;

import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.Statement;;
import org.openrdf.model.Literal;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.vocabulary.RDF;

import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.query.BindingSet;
import org.openrdf.query.Binding;
import org.openrdf.query.Update;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;

import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;


public class GetAbstracts_Test {
	Logger logger = Logger.getLogger(GetAbstracts_Test.class);
	GetAbstracts ga;
	int offset=15000000;
	int limit=10;
	int batchSize=10;

	@Before
	public void setup() throws Exception { 
		//ga = new GetAbstracts("conn.sail");
		ga = new GetAbstracts("conn.ag");


		ga.deleteBatches();

		ga.createSets(limit, offset, batchSize);
	}

	@After
	public void teardown() throws Exception {
		ga.deleteBatches();
		ga.close();
	}

	@Test
	public void test_getPmidsBatch_1() {
		List<URI> list = ga.getPmidsBatch(offset/batchSize);  

		logger.info("test 1 batch size " + list.size());
		logger.info(list.get(0));
		logger.info(ga.getAbstract(list.get(0)));

		assertEquals("the batches should be full-size", batchSize, list.size());
	}

	@Test
	public void test_getAbstract_1() {
		String a = ga.getAbstract("bogus");
		assertEquals(null, a);
	}

	@Test
	public void test_getAbstract_2() { 
		String a = ga.getAbstract("PMID_21490105");
		assertEquals("\"To assess the awareness and acceptability of colorectal cancer (CRC) screening in noncompliant Singaporeans and to determine if their barriers can be overcome by education. A questionnaire developed from thematic analysis of open-ended interviews with 72 subjects was administered to 580 residents in a local high-rise housing estate. Participants aware of CRC screening were assessed for barriers and acceptability of CRC screening. All participants were subsequently educated about CRC screening and reassessed for barriers and acceptance. Those keen for fecal occult blood testing (FOBT) were offered FOBT kits and followed up. CRC screening awareness was poor. Having no symptoms was the most common barrier. More barriers to FOBT than to colonoscopy were reduced with education. After education, acceptability toward FOBT increased but rejection rates rose even higher. FOBT is probably Singapore's most acceptable screening modality. Education is limited by barriers, which need to be overcome by alternative measures.\"@en", a);
	}


}
