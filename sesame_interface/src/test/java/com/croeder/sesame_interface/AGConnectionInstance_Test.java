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



public class AGConnectionInstance_Test extends ConnectionInstanceTest_Base {

	private final int num_batches=5;
	private final int num_docs_per_batch=3;
	private static Logger logger = Logger.getLogger(AGConnectionInstance_Test.class);

	@Before
	public void setup() throws Exception {
		ConnectionFactory factory = new ConnectionFactory("conn.ag");
		ConnectionInstance ci = factory.getConnection();
		con = ci.getConnection();
		valueFactory = ci.getValueFactory();

		insertData();
	}

	@After
	public void teardown() throws Exception {
		deleteData();
		con.close();
	}

}
