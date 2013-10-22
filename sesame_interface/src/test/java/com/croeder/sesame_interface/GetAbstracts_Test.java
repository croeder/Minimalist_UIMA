package com.croeder.starter;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;

import java.io.File;

public class MyClassTest {
	MyClass myClass= new MyClass();

	@Before
	public void setup() { 
		//myClass.preliminaryStuff();
	}

	@Test
	public void test_1() {
		String s = myClass.doStuff(new File("test.txt"));
		assertEquals("the returned string should be from the file", "This is a file at the top level.", s);
	}

	@After
	public void teardown() {
		//myClass.close();
	}
}
