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

import com.croeder.util.SpacedProperties;

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

}

