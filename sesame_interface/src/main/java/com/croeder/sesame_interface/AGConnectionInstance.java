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


import com.franz.agraph.repository.AGCatalog;
import com.franz.agraph.repository.AGRepository;
import com.franz.agraph.repository.AGServer;
import com.franz.agraph.repository.AGValueFactory;

import java.io.InputStream;
import java.io.FileInputStream;
import java.io.File;
import java.io.IOException;

import com.croeder.util.SpacedProperties;

import org.apache.log4j.Logger;

import org.openrdf.repository.base.RepositoryConnectionBase;
import org.openrdf.repository.RepositoryException;
import org.openrdf.model.impl.ValueFactoryBase;


public class AGConnectionInstance implements ConnectionInstance {

	private Logger logger = Logger.getLogger(AGConnectionInstance.class);

	private RepositoryConnectionBase conn;
	private AGServer server;
	private AGRepository repo;

	private String serverURI;
	private String username;
	private String password;
	private String repoName;

	public AGConnectionInstance(SpacedProperties properties) {
		readProperties(properties);
		//properties.dumpProperties();
		try {
			server = new AGServer(serverURI, username, password);
			AGCatalog catalog = server.getRootCatalog();
			repo = catalog.openRepository(repoName);
			repo.initialize();
			conn = repo.getConnection();
		}
		catch (NullPointerException | RepositoryException e) {
			logger.error("serverURI is:\"" + serverURI + "\" repo name is \"" + repoName + "\"");
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public RepositoryConnectionBase getConnection() throws RepositoryException{
		return repo.getConnection();
	}

	public ValueFactoryBase getValueFactory() {
		return new AGValueFactory(repo);
	}

	private void readProperties(SpacedProperties properties) {
        serverURI = properties.get("uri");
        username = properties.get("username");
        password = properties.get("password");
        repoName = properties.get("reponame");
	}

	public void close() {
		try {
			repo.close();
			server.close();
		}
		catch (RepositoryException e) {
			logger.error(e);
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
}

