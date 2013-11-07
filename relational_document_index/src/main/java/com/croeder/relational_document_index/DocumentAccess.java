package com.croeder.relational_document_index;

import static java.lang.System.out;

import com.croeder.relational_document_index.orm.Pmc;

import javax.persistence.Persistence;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import java.util.List;


public class DocumentAccess {
	EntityManager em;

	public DocumentAccess() {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory(
			"com.croeder.relational_document_index");
		em = emf.createEntityManager();
	}	

	public List<Integer> getPmidRange(int batchStart, int batchEnd) {
		em.getTransaction().begin(); // needed for query?
		//Query q = em.createQuery("SELECT x.pmid from Pmc x  WHERE seq >= :start and seq < :end");
		Query q = em.createQuery("SELECT pmid FROM Pmc x  WHERE seq >= :start AND seq < :end");
		q.setParameter("start", batchStart).setParameter("end", batchEnd);
		List<Integer> docs =  (List<Integer>) q.getResultList();
		return docs;	
	}

	public String getPmidPmcPath(int pmid) {
		Query q = em.createQuery("SELECT x from Pmc x  WHERE :pmid = pmid");
		q.setParameter("pmid", pmid);
		Pmc doc =  (Pmc) q.getSingleResult();
		return doc.getPath();
	}


	public static void main(String args[]) {
		DocumentAccess da = new DocumentAccess();
		List<Integer> list = da.getPmidRange(0,10);
		for (Integer i : list) {
			out.println("" + i);
		}

		String path = da.getPmidPmcPath(list.get(0));
		out.println(path);
	}

}
