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


import  static org.junit.Assert.assertEquals;
import  static org.junit.Assert.assertTrue;

import org.apache.log4j.Logger;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;



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
import org.openrdf.query.QueryLanguage;

import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;



public class ConnectionInstanceTest_Base {

	// TODO: abstract out
 	public final static String bibo  = "http://purl.org/ontology/bibo/";
    public final static String ro    = "http://www.obofoundry.org/ro/ro.owl#";
    public final static String iao   = "http://purl.obolibrary.org/obo/";
    public final static String ccp   = "http://compbio.ucdenver.edu/ccp/";
    public final static String rdf   = "http://www.w3.org/1999/02/22-rdf-syntax-ns/";
    public final static String medline = "http://www.nlm.nih.gov/bsd/medline/";
    public final static String pubmed = "http://www.ncbi.nlm.nih.gov/pubmed/";

    static final String prefixes  =
          "prefix bibo: <" + bibo  + ">\n"
        + "prefix ro: <" + ro    + ">\n"
        + "prefix iao: <" + iao   + ">\n"
        + "prefix ccp: <" + ccp + ">\n"
        + "prefix rdf: <" + rdf   + ">\n";

	QueryLanguage ql = QueryLanguage.SPARQL;

	private final int num_batches=5;
	private final int num_docs_per_batch=3;

	protected RepositoryConnection con;
	protected ValueFactory valueFactory;

	Logger logger = Logger.getLogger(ConnectionInstanceTest_Base.class);

	public void insertData() throws Exception  {
		URI uberBatchUri = valueFactory.createURI(ccp, "pmid_batch_set");
		URI hasPartUri = valueFactory.createURI(ro, "has_part");
		for (int i=0; i<num_batches; i++) {
			URI batchUri = valueFactory.createURI(ccp, "pmid_batch_" + i);
			Statement uberContainsStmt = new StatementImpl(uberBatchUri, hasPartUri, batchUri);
			con.add(uberContainsStmt);

			for (int j=0; j<num_docs_per_batch; j++) {
				URI medlineUri = valueFactory.createURI(medline, "TESTDOC_" + i + "_"  + j);
				Statement containsStmt = new StatementImpl(batchUri, hasPartUri, medlineUri);
				con.add(containsStmt);
			}
		}
	}

	public void deleteData() throws Exception {
        URI topUri = valueFactory.createURI(ccp, "pmid_batch_set");
        URI hasPartUri = valueFactory.createURI(ro, "has_part");
		for (int i=0 ; i<num_batches; i++) {
			URI batchUri = valueFactory.createURI(ccp, "pmid_batch_" + i);
        	Statement  topStmt = new StatementImpl(topUri, hasPartUri, batchUri);
        	//logger.info("TOP DELETE: " + topStmt.toString());
        	con.remove(topStmt);
		}

        for (int i=0; i<num_batches; i++) {
            URI batchUri = valueFactory.createURI(ccp, "pmid_batch_" + i);
			for (int j=0; j<num_docs_per_batch; j++) {
				URI medlineUri = valueFactory.createURI(medline, "TESTDOC_" + i + "_"  + j);
				Statement containsStmt = new StatementImpl(batchUri, hasPartUri, medlineUri);
            	con.remove(containsStmt);
			}
        }
    }

	@Test
	public void checkTop() throws Exception {
		String queryTop = prefixes 	+ "select  ?pmid WHERE "
									+ "{ ccp:pmid_batch_set ?p ?batch .}";
		//logger.info(queryTop);
		TupleQuery tq = null;
		TupleQueryResult result = null;
		try {
			tq = con.prepareTupleQuery(ql, queryTop);
			tq.setIncludeInferred(true);
			for (int batch_number=0; batch_number < num_batches; batch_number++) {
				URI batchUri = valueFactory.createURI(ccp, "pmid_batch_" + batch_number);
				URI hasPartUri = valueFactory.createURI(ro, "has_part");
				//logger.info("querying... " + batch_number);
				tq.setBinding("batch", batchUri);
				tq.setBinding("p", hasPartUri);
				result = tq.evaluate();
				assertTrue("batch: " + batch_number, result.hasNext());
			}
		}	
		finally {
			result.close();
		}
	}

	@Test
	public void query() throws Exception {
		String queryTop = prefixes 	+ "select  ?batch ?pmid WHERE "
									+ "{ ccp:pmid_batch_set ?p ?batch .\n"
								   	+ "  ?batch ?p2 ?pmid}";
		//logger.info(queryTop);
		TupleQuery tq = null;
		TupleQueryResult result = null;
		try {
			tq = con.prepareTupleQuery(ql, queryTop);
			tq.setIncludeInferred(true);
			for (int batch_number=0; batch_number < num_batches; batch_number++) {
				URI batchUri = valueFactory.createURI(ccp, "pmid_batch_" + batch_number);
				URI hasPartUri = valueFactory.createURI(ro, "has_part");
				//logger.info("querying... " + batch_number);
				tq.setBinding("batch", batchUri);
				tq.setBinding("p", hasPartUri);
				tq.setBinding("p2", hasPartUri);
				result = tq.evaluate();
				while (result.hasNext()) {
					//logger.info("next result:" + result.toString());
					BindingSet bs = (BindingSet) result.next();
					for (Binding b : bs) {
						//logger.info(b.getValue());
						//returnValues.add(b.getValue());
					}
				}
			}
		}	
		finally {
			result.close();
		}
	}
}
