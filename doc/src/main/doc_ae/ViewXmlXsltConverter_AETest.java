/* Copyright (C) 2007-2010 Center for Computational Pharmacology, University of Colorado School of Medicine
 * 
 * This file is part of the CCP NLP library.
 * The CCP NLP library is free software: you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package edu.uchsc.ccp.uima.ae.util.converter.view;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.List;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.junit.Before;
import org.junit.Test;
import org.uimafit.factory.TypeSystemDescriptionFactory;

import edu.ucdenver.ccp.common.file.CharacterEncoding;
import edu.ucdenver.ccp.common.string.StringUtil;
import edu.uchsc.ccp.uima.test.DefaultUIMATestCase;
import edu.uchsc.ccp.uima.util.UIMA_Util;
import edu.uchsc.ccp.util.nlp.annotation.TextAnnotation;

public class ViewXmlXsltConverter_AETest extends DefaultUIMATestCase { 

	private static final String INPUT_FILE_NAME = 
		//"edu.uchsc.ccp.uima.ae.util.converter.view." +
		"ViewXMLXSLTConverter_AETest.txt";
	private static final String OUTPUT_FILE_NAME = 
		//"/edu.uchsc.ccp.uima.ae.util.converter.view." +
		"ViewXMLXSLTConverter_AETest_output.txt";
	
	String input;
	String expectedOutput;
	JCas rawView;
	JCas defaultView;
	
	String readStream(InputStream is) throws Exception {
		InputStreamReader isr = new InputStreamReader(is, "UTF8");
		StringBuffer sb = new StringBuffer();
		
		if (false) {
			BufferedReader br = new BufferedReader(isr);
			while (br.ready()) {
				String s = br.readLine();
				System.out.println("xxxxxx" + s);
				sb.append(s);
			}
		}
		else {
			char[] buff = new char[100];
			while (isr.ready()) {
				isr.read(buff);
				System.out.println("zzzzzz" + buff.toString());
				String s = new String(buff);
				if (s.contains("late-onset")) {
					int i=0;
					for (char c : buff) {
						System.out.println("" + i + ":" + c);
						i++;
					}
				}
				System.out.println("YYYYY:" + s);
				sb.append(s);
			}
		}
		
		
		
		return sb.toString();
	}
	
	@Before
	public void before() throws IOException {
		InputStream is = this.getClass().getResourceAsStream(INPUT_FILE_NAME);
		assertNotNull(is);
		input = StringUtil.convertStream(is, CharacterEncoding.UTF_8);
		
		InputStream is2 = this.getClass().getResourceAsStream(OUTPUT_FILE_NAME);
		assertNotNull(is2);
		expectedOutput = StringUtil.convertStream(is2, CharacterEncoding.UTF_8);
		/**
		try {
			expectedOutput = readStream(is2);
		}
		catch (Exception x) {
			System.err.println("WT ^%$$@#$@: " + x);
		}
		**/
		assert(expectedOutput.length() > 100);
		rawView.setDocumentText(input);
		UIMA_Util.setDocumentID(rawView, "testDocumentID");
		
			
	}

	private AnalysisEngine xsltAE;

	@Override
	protected void initJCas() throws Exception {
		rawView = jcas.createView(View.RAW.viewName());
		defaultView = jcas.getView(View.DEFAULT.viewName());
	}

	@Test
	public void testCopyRaw2Default() throws Exception {
		xsltAE = ViewXMLXSLTConverter_AE.createAnalysisEngine(tsd, 
			//View.RAW.name(), View.DEFAULT.name());
			View.RAW.toString(), View.DEFAULT.toString(),
			// would work also: View.RAW.viewName(), View.DEFAULT.viewName(),
			"edu/uchsc/ccp/uima/ae/util/converter/view/PMCOpenAccess.xsl");

		assertNull("Before, the text in the default view should not be set.", 
				jcas.getDocumentText());
		assertEquals("Before, the text in the raw view should be our input.",
				input, rawView.getDocumentText());
		
		xsltAE.process(jcas);
		
		assertEquals("After, the text in the raw view should be our input.",
			input, rawView.getDocumentText());

		System.err.println("expected-->" + expectedOutput + "<--");
		System.err.println("actual  -->" + defaultView.getDocumentText() + "<--");
		assertEquals(
			"After, default view should now contain the output.",
			expectedOutput, defaultView.getDocumentText());
	}
	
	/* getTextAnnotationsFromJCas calls one of the swapAnnotations functions
	 * which complains to System.err that the Document ID stuff is missing.
	 * It doesn't here, but over in CCPXMLViewConverter_AE it does.
	 * ...and the only way to check it is to look at the console.
	 * (until I get motivated and modify the code with an exception)
	 */
	@Test
	public void doesUimaUtilComplain() throws Exception {
		UIMA_Util util = new UIMA_Util();
		List<TextAnnotation> defaultAnnotationList 
			= util.getTextAnnotationsFromJCas(jcas);
		assertEquals(defaultAnnotationList.size(), 0);
		
		List<TextAnnotation> rawAnnotationList 
		= util.getTextAnnotationsFromJCas(rawView);
		assertEquals(rawAnnotationList.size(), 0);
	}
	

	@Test
	public void rawHasDocumentID() throws Exception {
		xsltAE = ViewXMLXSLTConverter_AE.createAnalysisEngine(tsd, 
				//View.RAW.name(), View.DEFAULT.name());
				View.RAW.toString(), View.DEFAULT.toString(),
				// would work also: View.RAW.viewName(), View.DEFAULT.viewName(),
			"PMCOpenAccess.xsl");

		xsltAE.process(jcas);
		String id = UIMA_Util.getDocumentID(rawView);
		assertTrue(!id.equals("-1"));
	}
	@Test
	public void defaultHasDocumentID() throws Exception {
		xsltAE = ViewXMLXSLTConverter_AE.createAnalysisEngine(tsd, 
				//View.RAW.name(), View.DEFAULT.name());
				View.RAW.toString(), View.DEFAULT.toString(),
				// would work also: View.RAW.viewName(), View.DEFAULT.viewName(),
			"PMCOpenAccess.xsl");

		xsltAE.process(jcas);
		String id = UIMA_Util.getDocumentID(defaultView);
		assertTrue(!id.equals("-1"));
	}
	
	@Test
	public void createXmlDescriptor() throws Exception {
		File descFile = new File("desc/ae/util/converter/view/ViewXMLXSLTConverter_AE.xml");
	    TypeSystemDescription tsd =
	           TypeSystemDescriptionFactory.createTypeSystemDescription(
	        		   "typeSystem.CCPTypeSystem");
		String xmlDesc = ViewXMLXSLTConverter_AE.createXmlDescriptor(tsd);
		PrintStream ps = new PrintStream(descFile);
		ps.print(xmlDesc);
		ps.close();
	}

}
