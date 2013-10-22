package com.croeder.sesame_interface;


static import org.junit.Assert.assertEquals;

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

import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;



public class SAILConnectionInstance_Test {

	@Before
	public void setup() {
		ConnectionFactory factory = new ConnectionFactory("conn.sail");
		ConnectionInstance ci = factory.getConnection("SAIL");
		con = ci.getConnection();
		valueFactory = ci.getValueFactory();

		insertData();
	}

	@After
	public void teardown() {
		con.close();
	}


	public void insertData()  {
		URI uberBatchUri = valueFactory.createURI(chris, "pmid_batch_set");
		URI hasPartUri = valueFactory.createURI(ro, "has_part");
		batchUri = valueFactory.createURI(chris, "pmid_batch_" + batch_number);
		Statement uberContainsStmt = new StatementImpl(uberBatchUri, hasPartUri, batchUri);
		con.add(uberContainsStmt);
	}


	@Test
	public void query() {
			String queryTop = prefixes + "select ?p ?o WHERE { chris:pmid_batch_set ?p ?o .}";
			logger.info(queryTop);
		TupleQuery tq = null;
		TupleQueryResult result = null;
		try {
			tq = con.prepareTupleQuery(ql, queryString);
			tq.setIncludeInferred(true);
			URI batchUri = valueFactory.createURI(chris, "pmid_batch_" + batchNumber);
			tq.setBinding("batch", batchUri);
			result = tq.evaluate();
			while (result.hasNext()) {
				BindingSet bs = (BindingSet) result.next();
				for (Binding b : bs) {
					returnValues.add(b.getValue());
				}
			}
			result.close();
	}

}
