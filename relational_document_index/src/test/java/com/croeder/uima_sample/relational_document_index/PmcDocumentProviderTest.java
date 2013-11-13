package com.croeder.uima_sample.relational_document_index;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.After;
import org.junit.Test;

import java.util.List;

public class PmcDocumentProviderTest {
	
	DocumentProvider da;
	
	@Before
	public void setup() {
		da = new PmcDocumentProvider();
	}

	@Test 
	public void testMaxBatchIndex() {
		int maxIndex = da.getMaxBatchIndex();
		assertEquals(628, maxIndex);
	}

	@Test 
	public void testGetIdRange() {
		List<String> list = da.getIdRange(100);
		assertEquals("16857050", list.get(0));
		assertEquals("16907971",list.get(999));
	}

	@Test 
	public void getDocumentText() {
		assertEquals("", da.getDocumentText("210152"));
	}

}

