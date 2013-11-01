package com.croeder.util;

import java.util.Properties;

import java.io.File;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.log4j.Logger;

/**
 * This class encapsulates java.util.Properties ,
 * reading the file and loading it, 
 * and some namespace trickery
 */
public class SpacedProperties {

	private Logger logger = Logger.getLogger(SpacedProperties.class);
	private Properties props;
	String namespace;	

	public SpacedProperties(File propertiesFile, String namespace) {
		props = new Properties();
		this.namespace = namespace;
		readProperties(propertiesFile);
		logger.info("read properties file: " + propertiesFile + " with namespace: " + namespace);
		//dumpProperties();
	}

	public String get(String name) {
		String x = namespace + "." + name;
		return (String)  props.get(x);
	}
	
	private void readProperties(File propertiesFile) {
        try {
            // TODO: do stream from classpath
            InputStream propsStream = new FileInputStream(propertiesFile);
            props = new Properties();
            props.load(propsStream);
			//dumpProperties();
        }
        catch (IOException e) {
            logger.error(e);
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

	public void dumpProperties() {
		logger.info("-- property values --");
		for (Object key : props.keySet()) {
			logger.info("\"" + key + "\", \"" + props.get(key) + "\"");
			logger.info(key.getClass().getName());
		}
	}
		
}


