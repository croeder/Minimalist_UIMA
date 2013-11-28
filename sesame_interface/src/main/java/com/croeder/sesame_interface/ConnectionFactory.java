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

import java.util.Properties;
import java.util.HashMap;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.apache.log4j.Logger;

import org.openrdf.repository.base.RepositoryConnectionBase;
import org.openrdf.repository.RepositoryException;

import edu.ucdenver.ccp.nlp.util.SpacedProperties;

public class ConnectionFactory {

	public final static String  defaultPropertiesFilename = "/connection.properties";
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

		props = new SpacedProperties(propertiesFilename, propertiesPrefix);
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

