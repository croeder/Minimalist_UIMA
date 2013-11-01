package com.croeder.sesame_interface;

import java.io.InputStream;
import java.io.FileInputStream;
import java.io.File;
import java.io.IOException;

import java.util.Properties;
import java.util.HashMap;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.apache.log4j.Logger;

import org.openrdf.repository.base.RepositoryConnectionBase;
import org.openrdf.repository.RepositoryException;

import com.croeder.util.SpacedProperties;

public class ConnectionFactory {

	public final static String  defaultPropertiesFilename = "target/classes/connection.properties";
	public final static String VENDOR_PROPERTY = "vendor";

	private SpacedProperties props = null;


	private Logger logger = Logger.getLogger(ConnectionFactory.class);

	private String[] vendorNames = {"AG", "SAIL"};
	private String[] classNames  = {
		"com.croeder.sesame_interface.AGConnectionInstance",
		"com.croeder.sesame_interface.SAILConnectionInstance"
	};
	String vendor="AG";
	HashMap<String, String>  classMap;
	
	public ConnectionFactory(String propertiesPrefix) {
		this(defaultPropertiesFilename, propertiesPrefix);
	}

	public ConnectionFactory(String propertiesFilename, String propertiesPrefix) {

		props = new SpacedProperties(new File(propertiesFilename), propertiesPrefix);
		//props.dumpProperties();

		// build vendor name-->class map
		assert(vendorNames.length == classNames.length);
		classMap = new HashMap<String, String>();
		HashMap<String, String>  hash = new HashMap<String, String>();
		for (int i=0; i<vendorNames.length; i++) {
			classMap.put(vendorNames[i], classNames[i]);
		}

		vendor = props.get(VENDOR_PROPERTY);
	}

	public ConnectionInstance getConnection()  {
		if (!classMap.keySet().contains(vendor)) {
			String errMsg = "No value: " + vendor + ", in properties file for vendor property: " 
				+ props.get(VENDOR_PROPERTY) + " under key: " + VENDOR_PROPERTY + "."
				+ "Check  your properties file and properties prefix value.";
			logger.error(errMsg);
			throw new RuntimeException(errMsg);
		}
		ConnectionInstance conn = null;
		try {
			Class<?> clazz = Class.forName(classMap.get(vendor));
			Constructor<?> ctor = clazz.getConstructor(SpacedProperties.class);
			conn = (ConnectionInstance) ctor.newInstance(props);
		}
		catch (ClassNotFoundException | InstantiationException |  NoSuchMethodException 
			|  IllegalAccessException |  InvocationTargetException  e) {
			logger.error("error with vendor:" + vendor  + " props: " + props + " " + classMap.get(vendor));
			logger.error(e);
			e.printStackTrace();
			throw new RuntimeException(e);	
		}
		return conn;
	}
}

