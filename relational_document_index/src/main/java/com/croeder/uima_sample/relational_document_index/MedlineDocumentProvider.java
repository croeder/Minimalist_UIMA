package com.croeder.uima_sample.relational_document_index;

import static java.lang.System.out;

import com.croeder.relational_document_index.orm.Medline;

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

	public int getMaxBatchIndex() {	
		Query q = em.createQuery("SELECT max(id) FROM  MedlineBatch ");
		List<Integer> docIds =  (List<Integer>) q.getResultList();

		return docIds.get(0);
	}

	public List<String> getIdRange(int batchNumber) {
		Query q = em.createQuery("SELECT pmid FROM MedlineBatch WHERE id = :id ");
		q.setParameter("id", batchNumber);
		List<Integer> docIds =  (List<Integer>) q.getResultList();
		List<String> strings = Lists.transform(docIds, Functions.toStringFunction());
		return strings;	
	}

	public String getDocumentPath(String pmid) {
		return pmid;
	}

	public String getDocumentText(String pmid) {
		String text=null;
		try {
			Integer pmidInt = Integer.parseInt(pmid);
			Query q = em.createQuery("SELECT content FROM Medline x  WHERE pmid = :pmid and name = 'Abstract'");
			q.setParameter("pmid", pmidInt);
			List<String> textList = (List<String>) q.getResultList();
			text = textList.get(0);
		}
		catch (Exception e) {
			System.out.println("error getting text for pmid=" + pmid);
			System.out.println(e);
			e.printStackTrace();
		}
		return text;
	}

	public static void main(String args[]) {
		DocumentProvider da = new MedlineDocumentProvider();

		int maxIndex = da.getMaxBatchIndex();
		out.println("Max batch index is: " + maxIndex);

		List<String> list = da.getIdRange(100);
		for (String i : list) {
			out.println("pmid:" + i);
			out.println(da.getDocumentText(i));
		}
	}

}
