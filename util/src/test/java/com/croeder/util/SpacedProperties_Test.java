package com.croeder.util;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.apache.log4j.Logger;
import org.apache.log4j.BasicConfigurator;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;


public class SpacedProperties_Test {
	SpacedProperties sProps = null;

	@Before
	public void setup() { 
		BasicConfigurator.configure();
		sProps = new SpacedProperties(new File("target/classes/connection.properties"), "conn.ag");
	}

	@Test
	public void test_1() {	
		//conn.ag.reponame, medline-dev
		assertEquals("medline-dev", sProps.get("reponame"));

		//conn.ag.vendor, AG
		assertEquals("AG", sProps.get("vendor"));

		//conn.ag.username, roederc
		assertEquals("roederc", sProps.get("username"));

		//conn.ag.uri, http://192.168.167.128:10035
		assertEquals("http://192.168.167.128:10035", sProps.get("uri"));

		//conn.ag.password, Anschutz09
		assertEquals("PASSWORD", sProps.get("password"));
	}

	@After
	public void teardown() {
	}
}
