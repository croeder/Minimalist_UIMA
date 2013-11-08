package com.croeder.uima_sample.relational_document_index;

import static java.lang.System.out;

import com.croeder.relational_document_index.orm.Section;

import javax.persistence.Persistence;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import java.util.List;

import com.google.common.collect.Lists;
import com.google.common.base.Functions;


public class MedlineDocumentProvider implements DocumentProvider {
	EntityManager em;

	public MedlineDocumentProvider() {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory(
			"com.croeder.relational_document_index");
		em = emf.createEntityManager();
	}	

	public List<String> getIdRange(int batchStart, int batchEnd) {
		em.getTransaction().begin(); // needed for query?
		Query q = em.createQuery("SELECT content FROM Section x  WHERE name = 'Abstract' ");
		q.setParameter("start", batchStart).setParameter("end", batchEnd);
		List<Integer> docIds =  (List<Integer>) q.getResultList();
		List<String> strings = Lists.transform(docIds, Functions.toStringFunction());
		return strings;	
	}

	// returns a string to be fed to getDocumentText, the id in this case
	public String getDocumentPath(String pmid) {
		return pmid;
	}

	public String getDocumentText(String pmid) {
		em.getTransaction().begin(); // needed for query?
		Query q = em.createQuery("SELECT content FROM Section x  WHERE pmid = :pmid and name = 'Abstract'");
		q.setParameter("pmid", pmid);
		List<String> textList = (List<String>) q.getResultList();
		return textList.get(0);
	}

	public static void main(String args[]) {
		DocumentProvider da = new MedlineDocumentProvider();
		List<String> list = da.getIdRange(0,10);
		for (String i : list) {
			out.println("" + i);
		}

		String path = da.getDocumentPath(list.get(0));
		out.println(path);
	}

}
