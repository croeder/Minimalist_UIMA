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
package com.croeder.sesame_interface;

import java.io.InputStream;
import java.io.FileInputStream;
import java.io.File;
import java.io.IOException;


import org.apache.log4j.Logger;

import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.Repository;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.repository.sail.SailRepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.model.ValueFactory;
import org.openrdf.sail.memory.MemoryStore;

import edu.ucdenver.ccp.nlp.util.SpacedProperties;

public class SAILConnectionInstance implements ConnectionInstance {

	private Logger logger = Logger.getLogger(AGConnectionInstance.class);
	private Repository repo;
	private RepositoryConnection conn;
	private String dataDir;

	public SAILConnectionInstance(SpacedProperties properties) {
		try {
			repo = new SailRepository( new MemoryStore(new File(properties.get("dataDir"))));
			repo.initialize();
			conn = repo.getConnection();
		}
		catch (RepositoryException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public RepositoryConnection getConnection() throws RepositoryException{
		return repo.getConnection();
	}

	public ValueFactory getValueFactory() {
		return repo.getValueFactory();
	}

	public void close() {
		try {
			conn.close();
		}
		catch (RepositoryException e) {
			logger.warn("error closing connection: " + e);
			throw new RuntimeException(e);
		}

		try {
			repo.shutDown();
		}
		catch (RepositoryException e) {
			logger.warn("error closing connection: " + e);
			throw new RuntimeException(e);
		}
	}	

}

