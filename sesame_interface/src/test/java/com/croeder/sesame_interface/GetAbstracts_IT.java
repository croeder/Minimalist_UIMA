package com.croeder.sesame_interface;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import com.carrotsearch.junitbenchmarks.BenchmarkOptions;

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


public class GetAbstracts_IT {
	Logger logger = Logger.getLogger(GetAbstracts_Test.class);
	GetAbstracts ga;
	int offset=0;
	int limit=30000000;
	int batchSize=1000;

	@Before
	public void setup() throws Exception { 
		ga = new GetAbstracts("conn.sail");
		//ga = new GetAbstracts("conn.ag");

		ga.deleteBatches();

		ga.createSets(limit, offset, batchSize);
	}

	@After
	public void teardown() throws Exception {
		ga.deleteBatches();
		ga.close();
	}

	@BenchmarkOptions(benchmarkRounds=1, warmupRounds=0)
	@Test
	public void timePullAbstracts() {
		List<URI> list = ga.getPmidsBatch(10);  
		for (URI uri : list) {
			ga.getAbstract(uri);
		}
	}


}
