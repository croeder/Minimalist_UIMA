package com.croeder.uima_sample.relational_document_index;

import static java.lang.System.out;

import com.croeder.relational_document_index.orm.Pmc;

import javax.persistence.Persistence;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import java.util.List;

import com.google.common.collect.Lists;
import com.google.common.base.Functions;


public class PmcDocumentProvider implements DocumentProvider {
	EntityManager em;

	public PmcDocumentProvider() {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory(
			"com.croeder.relational_document_index");
		em = emf.createEntityManager();
	}	

	public List<String> getIdRange(int batchStart, int batchEnd) {
		em.getTransaction().begin(); // needed for query?
		Query q = em.createQuery("SELECT pmid FROM Pmc x  WHERE seq >= :start AND seq < :end");
		q.setParameter("start", batchStart).setParameter("end", batchEnd);
		List<Integer> docIds =  (List<Integer>) q.getResultList();
		List<String> strings = Lists.transform(docIds, Functions.toStringFunction());
		return strings;	
	}

	public String getDocumentPath(String pmid) {
		Query q = em.createQuery("SELECT x from Pmc x  WHERE :pmid = pmid");
		int pmidInt = Integer.valueOf(pmid);
		q.setParameter("pmid", pmidInt);
		///q.setParameter("pmid", pmid);
		Pmc doc =  (Pmc) q.getSingleResult();
		return doc.getPath();
	}

	public String getDocumentText(String path) {
		// wget the path and return?
		// TODO
		return "";
	}

	public static void main(String args[]) {
		DocumentProvider da = new PmcDocumentProvider();
		List<String> list = da.getIdRange(0,10);
		for (String i : list) {
			out.println("" + i);
		}

		String path = da.getDocumentPath(list.get(0));
		out.println(path);
	}

}
