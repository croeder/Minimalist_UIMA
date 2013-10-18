package com.croeder.sesame_interface;

import org.openrdf.repository.base.RepositoryConnectionBase;
import org.openrdf.repository.RepositoryException;
import org.openrdf.model.impl.ValueFactoryBase;

public interface ConnectionInstance {
	public RepositoryConnectionBase getConnection() throws RepositoryException;
	public ValueFactoryBase getValueFactory();
}

