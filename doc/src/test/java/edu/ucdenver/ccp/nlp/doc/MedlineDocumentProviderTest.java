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
package edu.ucdenver.ccp.nlp.doc;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.After;
import org.junit.Test;

import java.util.List;
import java.io.IOException;

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
	public void getDocumentText() 
	throws IOException {
		assertEquals("A collaborative study was conducted in 12 laboratories to determine the effectiveness of a new method for maintaining vegetative cells of Clostridium perfringens in viable condition during storage and transport of food specimens to the laboratory. The collaborative results showed that treatment of brown gravy and roast beef samples with an equal amount by weight of sterile buffered glycerol-sodium chloride solution to give a final 10% glycerol concentration and storage with Dry Ice for 10 days at -56 degrees C resulted in plate counts of C. perfringens which were 2-4 log cycles higher with 2 different strains than counts with untreated specimens stored by the usual method at -20 degrees C. Plate counts obtained with the treated specimens stored with Dry Ice were less than 1 log cycle lower than counts made with identical specimens before freezing for storage and shipment to the collaborators. The results with treated specimens were also more uniform among the different laboratories. Because the new method for storage and shipment of food samples was so effective for maintaining viability of the organism, the official first action method for C. perfringens (46.B01) was changed to incorporate these procedures as part of the method.", da.getDocumentText("210152"));
	}

}

