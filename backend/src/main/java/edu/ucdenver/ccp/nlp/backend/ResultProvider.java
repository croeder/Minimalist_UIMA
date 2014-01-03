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
package edu.ucdenver.ccp.nlp.backend;


import static java.lang.System.out;

import edu.ucdenver.ccp.nlp.backend.orm.PrimitiveResult;
import edu.ucdenver.ccp.nlp.backend.orm.CompoundResult;

import javax.persistence.Persistence;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;

import java.util.List;
import java.io.File;
import java.io.IOException;

import com.google.common.collect.Lists;
import com.google.common.base.Functions;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;

public class ResultProvider {
	EntityManager em;

	public ResultProvider() {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory(
			"edu.ucdenver.ccp.nlp.backend");
		em = emf.createEntityManager();
	}

    public List<CompoundResult> getCompoundResults() {
        Query q = em.createQuery("SELECT x  FROM  CompoundResult x ");
        return  (List<CompoundResult>) q.getResultList();
    }

	public void insertCompoundResult(CompoundResult cr) {
		EntityTransaction et = em.getTransaction();
		et.begin();
		em.persist(cr.getAgent());
		em.persist(cr.getTheme());
		em.persist(cr.getLocation());
		em.persist(cr);
		et.commit();
	}
}
