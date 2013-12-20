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

import java.util.List;
import java.util.ArrayList;

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



public abstract class ServiceBase {

	public final static String bibo  = "http://purl.org/ontology/bibo/";
	public final static String ro    = "http://www.obofoundry.org/ro/ro.owl#";
	public final static String iao   = "http://purl.obolibrary.org/obo/";
	public final static String rdf   = "http://www.w3.org/1999/02/22-rdf-syntax-ns/";
	public final static String medline = "http://www.nlm.nih.gov/bsd/medline/";
	public final static String pubmed = "http://www.ncbi.nlm.nih.gov/pubmed/";
	public final static String ccp    = "http://compbio.ucdenver.edu/ccp/";

	public static final String prefixes  = 
		  "prefix bibo: <" + bibo  + ">\n"
		+ "prefix ro: <" + ro    + ">\n"
		+ "prefix iao: <" + iao   + ">\n"
		+ "prefix ccp: <" + ccp + ">\n"
		+ "prefix rdf: <" + rdf   + ">\n";


	public QueryLanguage sparql = QueryLanguage.SPARQL;
	public final URI denotesUri;

	protected RepositoryConnection con;
	protected ValueFactory valueFactory;

	public ServiceBase(String propertiesPrefix) throws Exception {
		ConnectionFactory factory = new ConnectionFactory(propertiesPrefix);
		ConnectionInstance ci = factory.getConnection();
		con = ci.getConnection();
		valueFactory = ci.getValueFactory();
		denotesUri = valueFactory.createURI(iao, "IAO0000219");
	}


	public void showQueryResults(String queryString) throws Exception {
		TupleQuery tq = con.prepareTupleQuery(sparql, queryString);
		tq.setIncludeInferred(true);
		TupleQueryResult result = tq.evaluate();

		System.out.println("== bindings == ");		
		for (String name : result.getBindingNames() ) {
			System.out.println("binding:" + name);
		}

		System.out.println("== results == ");		
		while (result.hasNext()) {
			BindingSet bs = (BindingSet) result.next();
			for (Binding b : bs) {
				System.out.println(b.getName() + ", " + b.getValue());
			}
		}
		
		result.close();
	}

}
