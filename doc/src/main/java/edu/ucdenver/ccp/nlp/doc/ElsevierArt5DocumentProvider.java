/*
 Copyright (c) 2013, Regents of the University of Colorado
 All rights reserved.

 Redistribution and use in source and binary forms, with or without modification,
 are permitted provided that the following conditions are met:

 * Redistributions of source code must retain the above copyright notice, this
    list of conditions and the following disclaimer.

 * Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 * Neither the name of the University of Colorado nor the names of its
    contributors may be used to endorse or promote products derived from this
    software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
 ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package edu.ucdenver.ccp.nlp.doc;


import static java.lang.System.out;

import edu.ucdenver.ccp.nlp.doc.orm.ElsevierArt5Record;

import javax.persistence.Persistence;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import java.util.List;
import java.io.File;
import java.io.IOException;

import com.google.common.collect.Lists;
import com.google.common.base.Functions;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;

import org.apache.log4j.Logger;

/**
 * serves up elsevier documents. Ids are Pii, paths are on amc-colfax
 */
public class ElsevierArt5DocumentProvider implements DocumentProvider {

	Logger logger = Logger.getLogger(ElsevierArt5DocumentProvider.class);
	EntityManager em;

	public ElsevierArt5DocumentProvider() {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory(
			"edu.ucdenver.ccp.nlp.doc");
		em = emf.createEntityManager();
	}

    public int getMaxBatchIndex() {
        Query q = em.createQuery("SELECT max(doc_id) FROM  ElsevierArt5Batch ");
        List<Integer> docIds =  (List<Integer>) q.getResultList();

        return docIds.get(0);
    }

    public List<String> getIdRange(int batchNumber) {
        Query q = em.createQuery("SELECT doc_id FROM ElsevierArt5Batch WHERE batch_id = :batchId ");
        q.setParameter("batchId", batchNumber);
        List<Integer> docIds =  (List<Integer>) q.getResultList();
        List<String> strings = Lists.transform(docIds, Functions.toStringFunction());
        return strings;
    }

	public ImmutablePair<String,String> getDocumentPathAndId(String docId) {
		Query q = em.createQuery("SELECT rec from ElsevierArt5Record as rec  WHERE id = :docId");
		int docIdInt = Integer.valueOf(docId);
		q.setParameter("docId", docIdInt);
		ElsevierArt5Record record = (ElsevierArt5Record) q.getSingleResult();
		return  new ImmutablePair<String, String>(record.getPath(), record.getPii());
	}

	public String getDocumentText(String path) 
	throws IOException {

		// TODO: make a property for this, deal with the possibility of different paths needing different prefixes
		///path = "/net/amc-colfax/" + path;

		
		// wget the path and return?
		// TODO
		String fileString =  FileUtils.readFileToString(new File(path), "UTF-8");
		//logger.debug("docProvider PATH is: " + path + "\nFILE IS:\"" + fileString + "\"");
		return fileString;
	}

}
