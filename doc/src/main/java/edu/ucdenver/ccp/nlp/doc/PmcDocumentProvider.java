package edu.ucdenver.ccp.nlp.doc;

import static java.lang.System.out;

import edu.ucdenver.ccp.nlp.doc.orm.Pmc;

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
			"edu.ucdenver.ccp.nlp.doc");
		em = emf.createEntityManager();
	}

    public int getMaxBatchIndex() {
        Query q = em.createQuery("SELECT max(id) FROM  PmcBatch ");
        List<Integer> docIds =  (List<Integer>) q.getResultList();

        return docIds.get(0);
    }

    public List<String> getIdRange(int batchNumber) {
        Query q = em.createQuery("SELECT pmid FROM PmcBatch WHERE id = :id ");
        q.setParameter("id", batchNumber);
        List<Integer> docIds =  (List<Integer>) q.getResultList();
        List<String> strings = Lists.transform(docIds, Functions.toStringFunction());
        return strings;
    }


	public String getDocumentPath(String pmid) {
		Query q = em.createQuery("SELECT x from Pmc x  WHERE :pmid = pmid");
		int pmidInt = Integer.valueOf(pmid);
		q.setParameter("pmid", pmidInt);
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

		int maxIndex = da.getMaxBatchIndex();
		out.println("Max batch index is: " + maxIndex);

		List<String> list = da.getIdRange(100);
		for (String i : list) {
			out.println("" + i);
		}

		String path = da.getDocumentPath(list.get(0));
		out.println(path);
	}



}
