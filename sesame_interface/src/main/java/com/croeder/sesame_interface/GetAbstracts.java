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


public class GetAbstracts {

	public final static String bibo  = "http://purl.org/ontology/bibo/";
	public final static String ro    = "http://www.obofoundry.org/ro/ro.owl#";
	public final static String iao   = "http://purl.obolibrary.org/obo/";
	public final static String chris = "http://com.croeder/chris/";
	public final static String rdf   = "http://www.w3.org/1999/02/22-rdf-syntax-ns/";
	public final static String medline = "http://www.nlm.nih.gov/bsd/medline/";
	public final static String pubmed = "http://www.ncbi.nlm.nih.gov/pubmed/";

	static final String basicQuery = "select ?s ?p ?o {?s ?p ?o} LIMIT 10";
	static final String prefixes  = 
		  "prefix bibo: <" + bibo  + ">\n"
		+ "prefix ro: <" + ro    + ">\n"
		+ "prefix iao: <" + iao   + ">\n"
		+ "prefix chris: <" + chris + ">\n"
		+ "prefix rdf: <" + rdf   + ">\n";
	static final String getPmidsQuery = prefixes 
		+ "select ?s "
		+ "{ ?s  bibo:pmid ?p . } order by ?s ";
		//+ "{ ?s  bibo:pmid ?p . } order by ?s LIMIT 1000";
	static final String abstractsQuery = prefixes 
		+ "select ?s ?o2 "
		+ "{ ?s  bibo:pmid \"23025167\"@en . \n"
		+ "  ?s  iao:IAO_0000219 ?s2 . \n"
		+ "  ?s2 dcterms:abstract ?o2 } limit 100";


	public final URI denotesUri;

	static Logger logger = Logger.getLogger(GetAbstracts.class);
	RepositoryConnection con;
	ValueFactory valueFactory;
	QueryLanguage ql = QueryLanguage.SPARQL;

	public GetAbstracts() throws Exception {
		ConnectionFactory factory = new ConnectionFactory("conn.ag");
		ConnectionInstance ci = factory.getConnection("AG");
		con = ci.getConnection();
		valueFactory = ci.getValueFactory();
		denotesUri = valueFactory.createURI(iao, "IAO0000219");
	}


	public void showQueryResults(String queryString) throws Exception {
		TupleQuery tq = con.prepareTupleQuery(ql, queryString);
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


		//URI uberBatchUri = valueFactory.createURI(chris, "pmid_batch_set");
		//Statement uberBatchStmt = new StatementImpl(uberBatchUri,  RDF.TYPE, RDF.BAG);
		//URI counterUri = valueFactory.createURI(RDF.NS.getPrefix(), "_" + i);
		//Statement  itemStmt = new StatementImpl(batchUri, counterUri, v);

	public void createSets() throws Exception {
		final int batch_size = 100;

		TupleQuery tq = con.prepareTupleQuery(ql, getPmidsQuery);
		TupleQueryResult result = tq.evaluate();

		URI hasPartUri = valueFactory.createURI(ro, "has_part");
		// uberBatch type bag
		URI uberBatchUri = valueFactory.createURI(chris, "pmid_batch_set");
		// assign no type

		URI batchUri = null;
		int batch_number=0;
		int i=-1;
		while (result.hasNext()  ) {	
			i++;
			if (i % batch_size == 0) {
				batch_number = i / batch_size;

				// batch  type bag
				batchUri = valueFactory.createURI(chris, "pmid_batch_" + batch_number);
				// assign no type`

				// uberBatch contains batch
				Statement uberContainsStmt = new StatementImpl(uberBatchUri, hasPartUri, batchUri);
				logger.info("UBER CONTAINS: " + uberContainsStmt.toString());
				con.add(uberContainsStmt);
			}

			// batch contains pmid item
			BindingSet bs = (BindingSet) result.next();
			Binding b = bs.iterator().next();
			Statement  itemStmt = new StatementImpl(batchUri, hasPartUri, b.getValue());
			logger.info("BATCH " + batch_number + "CONTAINS: " + itemStmt.toString());
			con.add(itemStmt);
		}
	}

	public void runQueries() throws Exception {
	}

	public void showSets() throws Exception {
			String queryTop = prefixes + "select ?p ?o WHERE { chris:pmid_batch_set ?p ?o .}";
			logger.info(queryTop);
			showQueryResults(queryTop);

			//for (int i=0; i<10; i++) {
			//	String query = prefixes + "select  ?o WHERE { chris:pmid_batch_" + i + " ro:has_part ?o .}";
			//	logger.info(query);
			//	query(query);

			String query = prefixes + "select  ?batch ?pmid  WHERE { chris:pmid_batch_set ro:has_part ?batch ."
									+                              " ?batch ro:has_part ?pmid .}";
			logger.info(query);
			showQueryResults(query);

			// produces other junk:
			//String query = prefixes +  "select ?o { chris:pmid_batch_1 ?p   ?o}";
			// don't work:
			//String query = prefixes +  "select ?o { chris:pmid_batch_1 rdfs:member   ?o}";
			//String query = prefixes +  "select ?p ?o { chris:pmid_batch_1 ?p   ?o. FILTER (strstarts(str(?prop), str(rdf:_)))}";
	}

	public void deleteSets() throws Exception {
		URI hasPartUri = valueFactory.createURI(ro, "has_part");
		URI topUri = valueFactory.createURI(chris, "pmid_batch_set");
		Statement  topStmt = new StatementImpl(topUri, hasPartUri, null);
		logger.info("TOP DELETE: " + topStmt.toString());
		con.remove(topStmt);

		for (int i=0; i<10; i++) {
			URI batchUri = valueFactory.createURI(chris, "pmid_batch_" + i);
			Statement  hasPartStmt = new StatementImpl(batchUri, hasPartUri, null);
			logger.info("BATCH DELETE: " + hasPartStmt.toString());
			con.remove(hasPartStmt);
		}
	}

	public List<Value> getPmidsBatch(int batchNumber)  {
		String queryString = prefixes + "SELECT  ?pmid  WHERE "
							    + "{ chris:pmid_batch_set 	ro:has_part ?batch ."
								+ " ?batch 					ro:has_part ?pmid .}";

		List<Value> returnValues = new ArrayList<>();

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
		catch (QueryEvaluationException e) {
			logger.error("" +  tq.toString());
			logger.error(e);
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		catch (MalformedQueryException e) {
			logger.error("" +  tq.toString());
			logger.error(e);
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		catch (RepositoryException e) {
			logger.error("" +  tq.toString());
			logger.error(e);
			e.printStackTrace();
			throw new RuntimeException(e);
		}

		return returnValues;
	}


	public String getAbstract(String pmid) {
		URI medlineUri = valueFactory.createURI(medline, pmid);
		return getAbstract(medlineUri);
	}

	public String getAbstract(Value medlineUri) {
		String queryString = prefixes + "select  ?abstract  WHERE " 
			+ "{  ?medlineUri  iao:IAO_0000219 ?pmidUri . \n"
			+ " ?pmidUri dcterms:abstract  ?abstract .}";


		String abstractString=null;
		TupleQuery tq = null;
		TupleQueryResult result = null;
		try {
			tq = con.prepareTupleQuery(ql, queryString);
			tq.setIncludeInferred(true);
			tq.setBinding("medlineUri", medlineUri);
			result = tq.evaluate();
			if (result.hasNext()) {
				BindingSet bs = (BindingSet) result.next();
				abstractString = bs.iterator().next().getValue().toString();
			}
			result.close();
		}
		catch (QueryEvaluationException e) {
			logger.error("" +  tq.toString());
			logger.error(e);
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		catch (MalformedQueryException e) {
			logger.error("" +  tq.toString());
			logger.error(e);
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		catch (RepositoryException e) {
			logger.error("" +  tq.toString());
			logger.error(e);
			e.printStackTrace();
			throw new RuntimeException(e);
		}

		return abstractString;
	}

	public void testIntegrateShort() {
		System.out.println(getAbstract("23025167"));
		System.out.println(getAbstract(valueFactory.createURI(pubmed, "23025167")));
	}	
	public void testIntegrate() {
			List<Value> pmids = getPmidsBatch(1);
			for (Value v : pmids) {
				logger.info("VALUE is:" + v.toString());
				System.out.println(getAbstract(v));
			}
	}


	public static void main(String args[]) {
		try {
			GetAbstracts ga = new GetAbstracts();
			//ga.query(basicQuery);
			//ga.query(abstractsQuery);

			//ga.deleteSets();
			//ga.createSets();
			//ga.showSets();

			//ga.getPmidsBatch(1);
			//ga.getPmidsBatch(1);
			ga.testIntegrateShort();
			ga.testIntegrate();
		}
		catch (Exception e) {
			logger.error(e);
			e.printStackTrace();
		}
	}


}
