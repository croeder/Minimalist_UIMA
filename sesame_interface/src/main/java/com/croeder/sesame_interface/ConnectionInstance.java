package com.croeder.sesame_interface;

import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.model.ValueFactory;

public interface ConnectionInstance {
	public RepositoryConnection getConnection() throws RepositoryException;
	public ValueFactory getValueFactory();
}

