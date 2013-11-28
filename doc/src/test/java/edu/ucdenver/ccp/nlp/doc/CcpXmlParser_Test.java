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
package edu.ucdenver.ccp.nlp.doc;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.Before;
import java.util.List;


public class CcpXmlParser_Test {
	
	public static final String t1 =
		 "<doc>"
		+"<title name=\"optionalTitleName\">title-text</title>"
		+"<abstract name=\"AbstractName\">abstract-text</abstract>"
		+"<section name=\"FirstSectionName\">Section-Text"
		+"<subsection name=\"FirstSubsectionName\">"
		+"<paragraph>subsection-paragraph-text "
		+"<italics>italics-text</italics>"
		+"</paragraph>"
		+"</subsection>"
		+"<paragraph>section-paragraph"
		+"</paragraph>"
		+"</section>"
		+"<keyword>keyword</keyword>"
		+"<definition name=\"wordName\">definition-text</definition>"
		+"<figure name=\"figure_1\">figure-text</figure>"
		+"</doc>";
	
	public static final String[] stringSpans = {
		" optionalTitleName title-text ",
		"AbstractName abstract-text ",
		"FirstSectionName Section-Text ",
		"FirstSubsectionName ",
		"subsection-paragraph-text ",
		"italics-text",
		"section-paragraph",
		"keyword ",
		"wordName definition-text ",
		"figure_1 figure-text"
	};

	public static final int[][] spans =	 {
		{0,29},
		{29,56},
		{133,145},
		{145,162},
		{162,169},
		{169,194},
		{194,215}
	};
	
	@Test
	public void test() throws Exception {
		int i=0;
		CcpXmlParser parser = new CcpXmlParser();
		String text = parser.parse(t1, "fictiousDocID");
		List<CcpXmlParser.Annotation> list = parser.getAnnotations();
		System.out.println("---\\/-------------");
		for (CcpXmlParser.Annotation a : list) { 
			//System.out.println(a); 
			//System.out.println("\"" + text.substring(a.start, a.end) + "\""); 
			assertEquals(spans[i][0], a.start);
			assertEquals(spans[i][1], a.end);
			i++;
		}
		System.out.println("--^^---------------");
		StringBuffer expected = new StringBuffer();
		for (String s : stringSpans) {
			expected.append(s);
		}
		assertEquals(expected.toString(), text);
	}

}
