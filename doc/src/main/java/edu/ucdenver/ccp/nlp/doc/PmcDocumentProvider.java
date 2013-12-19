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

import edu.ucdenver.ccp.nlp.doc.orm.Pmc;

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

/**
 * serves up pmc paths by pmid. Paths are on RAID1 (amc-colfax)
 */
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

	public ImmutablePair<String,String> getDocumentPathAndId(String pmid) {
		Query q = em.createQuery("SELECT rec from Pmc as rec   WHERE :pmid = pmid");
		int pmidInt = Integer.valueOf(pmid);
		q.setParameter("pmid", pmidInt);
		Pmc record = (Pmc) q.getSingleResult();
		return new ImmutablePair<String, String>(record.getPath(), "" + record.getPmid());
	}

	public String getDocumentText(String path) 
	throws IOException {

		// TODO: make a property for this, deal with the possibility of different paths needing different prefixes
		path = "/net/amc-colfax/" + path;

		
		// wget the path and return?
		// TODO
		return FileUtils.readFileToString(new File(path), "UTF-8");
	}

}
