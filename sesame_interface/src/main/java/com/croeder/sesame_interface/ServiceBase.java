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



// http://www.franz.com/agraph/support/documentation/v4/sparql-tutorial.html


public abstract class ServiceBase {

	public final static String bibo  = "http://purl.org/ontology/bibo/";
	public final static String ro    = "http://www.obofoundry.org/ro/ro.owl#";
	public final static String iao   = "http://purl.obolibrary.org/obo/";
	public final static String chris = "http://com.croeder/chris/";
	public final static String rdf   = "http://www.w3.org/1999/02/22-rdf-syntax-ns/";
	public final static String medline = "http://www.nlm.nih.gov/bsd/medline/";
	public final static String pubmed = "http://www.ncbi.nlm.nih.gov/pubmed/";

	static final String prefixes  = 
		  "prefix bibo: <" + bibo  + ">\n"
		+ "prefix ro: <" + ro    + ">\n"
		+ "prefix iao: <" + iao   + ">\n"
		+ "prefix chris: <" + chris + ">\n"
		+ "prefix rdf: <" + rdf   + ">\n";


	public QueryLanguage sparql = QueryLanguage.SPARQL;
	public final URI denotesUri;

	RepositoryConnection con;
	ValueFactory valueFactory;

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
