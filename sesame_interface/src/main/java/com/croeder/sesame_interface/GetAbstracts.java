package com.croeder.sesame_interface;

import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;

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


public class GetAbstracts extends ServiceBase {

	static Logger logger = Logger.getLogger(GetAbstracts.class);

	URI hasPartUri;
	URI uberBatchUri;
	int batch_size=100;

	public GetAbstracts(String prefix) throws Exception {
		super(prefix);
		uberBatchUri = valueFactory.createURI(chris, "pmid_batch_set");
	 	hasPartUri = valueFactory.createURI(ro, "has_part");
	}

	public void close() throws RepositoryException{
		con.close();
	}

	public void createSets() throws Exception {
		createSets(0, 0, 1000);
	}

	protected void createSets(int limit, int offset, int  batch_size) throws Exception {
		this.batch_size = batch_size;
		String getPmidsQuery = prefixes 
			+ "select ?s "
			+ "{ ?s  bibo:pmid ?p . } order by ?s ";

		TupleQuery tq = con.prepareTupleQuery(sparql, getPmidsQuery);
		TupleQueryResult result = tq.evaluate();

		URI batchUri = null;
		int batch_number=0;
		int batch_count=0;
		int i=-1; 
		while (result.hasNext() && (limit==0 || batch_count < limit) ) {	
			i++;
			if (i >=  offset) {
				batch_count++;
				if (i % batch_size == 0) {
					batch_number = i / batch_size;
					batchUri = valueFactory.createURI(chris, "pmid_batch_" + batch_number);
					Statement uberContainsStmt = new StatementImpl(uberBatchUri, hasPartUri, batchUri);
					con.add(uberContainsStmt);
				}
	
				BindingSet bs = (BindingSet) result.next();
				Binding b = bs.iterator().next();
				Statement  itemStmt = new StatementImpl(batchUri, hasPartUri, b.getValue());
				con.add(itemStmt);
			}
		}
		logger.error("createSets: " + i);
	}

	public void deleteBatches() throws Exception {
		int batchCount=0;
		int docCount=0;	

		// get info from, then delete: each batch
		for (URI batchUri : getBatches()) {
			
			batchCount++;
			for (URI pmidUri : getPmidsBatch(batchUri)) {
				docCount++;
				Statement  hasPartStmt = new StatementImpl(batchUri, hasPartUri, pmidUri);
				con.remove(hasPartStmt);
			}	
			Statement uberContainsStmt = new StatementImpl(uberBatchUri, hasPartUri, batchUri);
			con.remove(uberContainsStmt);
		}
		logger.error("deleteBatches(): number deletes  batches:" + batchCount + " docs:" + docCount);
	}


	
	public List<URI> getBatches() {
		String queryString = prefixes + "SELECT DISTINCT  ?batch  WHERE "
							    + "{ chris:pmid_batch_set 	ro:has_part ?batch .}";
		List<URI> returnValues = new ArrayList<>();

		TupleQuery tq = null;
		TupleQueryResult result = null;
		try {
			tq = con.prepareTupleQuery(sparql, queryString);
			tq.setIncludeInferred(true);
			URI batchUri = valueFactory.createURI(chris, "pmid_batch_set");
			result = tq.evaluate();
			while (result.hasNext()) {
				BindingSet bs = (BindingSet) result.next();
				for (Binding b : bs) {
					returnValues.add((URI) b.getValue()); // XXX
				}
			}
			result.close();
		}
		catch (QueryEvaluationException | MalformedQueryException | RepositoryException  e) {
			logger.error("" +  tq.toString());
			logger.error(e);
			e.printStackTrace();
			throw new RuntimeException(e);
		}

		return returnValues;
	}

	public List<URI> getPmidsBatch(int batchNumber)  {
		URI batchUri = valueFactory.createURI(chris, "pmid_batch_" + batchNumber);
		return getPmidsBatch(batchUri);
	}

	public List<URI> getPmidsBatch(URI batchUri) {
		String queryString = prefixes + "SELECT  ?pmid  WHERE "
							    + "{ chris:pmid_batch_set 	ro:has_part ?batch ."
								+ " ?batch 					ro:has_part ?pmid .}";

		List<URI> returnValues = new ArrayList<>();
		int i=0; int j=0;
		TupleQuery tq = null;
		TupleQueryResult result = null;
		try {
			tq = con.prepareTupleQuery(sparql, queryString);
			tq.setIncludeInferred(false);
			tq.setBinding("batch", batchUri);
			result = tq.evaluate();
			while (result.hasNext()) {
				i++;
				BindingSet bs = (BindingSet) result.next();
				for (Binding b : bs) {
					j++;
					returnValues.add((URI) b.getValue());  //XXX
				}
			}
			result.close();
		}
		catch (QueryEvaluationException | MalformedQueryException | RepositoryException  e) {
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

	public String getAbstract(URI medlineUri) {
		String queryString = prefixes + "select  ?abstract  WHERE " 
			+ "{  ?medlineUri  iao:IAO_0000219 ?pmidUri . \n"
			+ " ?pmidUri dcterms:abstract  ?abstract .}";


		String abstractString=null;
		TupleQuery tq = null;
		TupleQueryResult result = null;
		try {
			tq = con.prepareTupleQuery(sparql, queryString);
			tq.setIncludeInferred(true);
			tq.setBinding("medlineUri", medlineUri);
			result = tq.evaluate();
			if (result.hasNext()) {
				BindingSet bs = (BindingSet) result.next();
				abstractString = bs.iterator().next().getValue().toString();
			}
			result.close();
		}
		catch (QueryEvaluationException | MalformedQueryException | RepositoryException  e) {
			logger.error("" +  tq.toString());
			logger.error(e);
			e.printStackTrace();
			throw new RuntimeException(e);
		}

		return abstractString;
	}

	public static void main(String args[]) {
		try {
			GetAbstracts ga = new GetAbstracts("conn.ag");

			ga.deleteBatches();

			{
				List<URI> list = ga.getPmidsBatch(0);
				HashSet<URI> set = new HashSet(list);
				logger.info("after delete, before craate batch 0 got " + list.size() + " abstracts, " + set.size() + " unique");
			}

			ga.createSets();
			
			List<URI> list = ga.getPmidsBatch(0);
			HashSet<URI> set = new HashSet(list);
			logger.info("batch 0 got " + list.size() + " abstracts, " + set.size() + " unique");

		}
		catch (Exception e) {
			logger.error(e);
			e.printStackTrace();
		}
	}


}
