package com.croeder.uima_sample.relational_document_index;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.After;
import org.junit.Test;

import java.util.List;

public class MedlineDocumentProviderTest {
	
	DocumentProvider da;
	
	@Before
	public void setup() {
		da = new MedlineDocumentProvider();
	}

	@Test 
	public void testMaxBatchIndex() {
		int maxIndex = da.getMaxBatchIndex();
		assertEquals(8316, maxIndex);
	}

	@Test 
	public void testGetIdRange() {
		List<String> list = da.getIdRange(100);
		assertEquals("208335", list.get(0));
		assertEquals("210279",list.get(999));
	}

	@Test 
	public void getDocumentText() {
		assertEquals("A collaborative study was conducted in 12 laboratories to determine the effectiveness of a new method for maintaining vegetative cells of Clostridium perfringens in viable condition during storage and transport of food specimens to the laboratory. The collaborative results showed that treatment of brown gravy and roast beef samples with an equal amount by weight of sterile buffered glycerol-sodium chloride solution to give a final 10% glycerol concentration and storage with Dry Ice for 10 days at -56 degrees C resulted in plate counts of C. perfringens which were 2-4 log cycles higher with 2 different strains than counts with untreated specimens stored by the usual method at -20 degrees C. Plate counts obtained with the treated specimens stored with Dry Ice were less than 1 log cycle lower than counts made with identical specimens before freezing for storage and shipment to the collaborators. The results with treated specimens were also more uniform among the different laboratories. Because the new method for storage and shipment of food samples was so effective for maintaining viability of the organism, the official first action method for C. perfringens (46.B01) was changed to incorporate these procedures as part of the method.", da.getDocumentText("210152"));
	}

}

