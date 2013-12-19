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

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;

import java.io.IOException;

import org.apache.commons.lang3.tuple.ImmutablePair;


public class ElsevierArt5DocumentProviderTest {
	
	DocumentProvider da;
	
	@Before
	public void setup() {
		da = new ElsevierArt5DocumentProvider();
	}

	
	@Test 
	public void testMaxBatchIndex() {
		int maxIndex = da.getMaxBatchIndex();
		//assertEquals(999999, maxIndex);
	}

	@Test 
	public void testGetIdRange() {
		List<String> list = da.getIdRange(100);
		//assertEquals("100321", list.get(0));
		//assertEquals("822866",list.get(999));
	}

	@Test
	public void testGetDocumentPathAndId() {
		ImmutablePair<String, String> pair = da.getDocumentPathAndId("1");
		String path = pair.getLeft();
		String id = pair.getRight();
		//System.out.println("-------Elsevierxxxxxxx" + path + ", " + id);
	assertEquals("/net/amc-colfax/RAID1/data/fulltext/elsevier/untar/UNC00000000000709/09699961/v45i2/S0969996111003330/main.xml", path);
	assertEquals("S0969-9961(11)00333-0", id);
	}

	@Ignore
	@Test 
	public void getDocumentText() throws IOException {
		assertEquals(
		"",
		da.getDocumentText("/RAID1/data/fulltext/pmc/files/J_Cell_Biol/J_Cell_Biol_1976_Sep_1_70(3)_714-719.nxml"));
	}

}

