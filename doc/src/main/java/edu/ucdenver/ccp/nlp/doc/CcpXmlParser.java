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

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Stack;
import java.util.Iterator;


import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;


/*
 * See the Test class for an example of what an input doc. looks like.
 * 
 * Basic Strategy is to create a ClassMention named "Section", 
 * with a SlotMention named "Type" whose value is the the tag name.
 * An optional SlotMention named "Name" has a value for any names or titles.
 * The associated TextAnnotation has the span.

  New strategy is  to use a local class to hold these attributes.
 */


public class CcpXmlParser {
	
	private static final String[] tags = {
			"DOC", "SECTION", "SUBSECTION", "PARAGRAPH", "KEYWORD", "DEFINITION", 
			"ABSTRACT", "FIGURE", "TITLE", "ITALICS"
	};

	static final String CLASS_MENTION_NAME="Section";
	static final String TYPE_SLOT_NAME="Type";
	static final String NAME_SLOT_NAME="Name";
	static final String TITLE_SLOT_NAME="Name";
	static final String NAME_ATTRIBUTE_NAME="name";
	static final String TITLE_ATTRIBUTE_NAME="NAME";
	static final String PARSER_NAME = "CCP XML Parser";
	
	private XMLReader parser;
	private HashSet<String> tagSet;
	private Stack<Annotation> stack = new Stack<Annotation>();
	private List<Annotation> annotations = new ArrayList<Annotation>();
	private StringBuffer documentText = new StringBuffer();
	private String docID;
	
	public CcpXmlParser() 
		throws IOException, SAXException {
		parser = XMLReaderFactory.createXMLReader(
				"org.apache.xerces.parsers.SAXParser");
		tagSet = new HashSet<String>();
		tagSet.addAll(Arrays.asList(tags));
		for (String t : tags) {
			tagSet.add(t.toLowerCase());
		}
	}

	/**
	 * Returns the parsed version of the XML string input
	 * 
	 * @param xml
	 * @return
	 * @throws IOException
	 * @throws SAXException
	 */
	public String parse(String xml, String docID) throws IOException, SAXException {
		return parse(new InputSource(new StringReader(xml)), docID);
	}
	
	public List<Annotation> getAnnotations() { return annotations; }

	/**
	 * Returns the parsed version of the input PMC NXML File
	 * 
	 * @param nxmlFile
	 * @return
	 * @throws IOException
	 * @throws SAXException
	 */
	public String parsePmcNxml(File nxmlFile, String docID) throws IOException, SAXException {
		return parse(new InputSource(new FileReader(nxmlFile)), docID);
	}

	/**
	 * General method for parsing PMC xml that takes an InputSource as input. This method should
	 * replace the deprecated parsePMCXML(String) method.
	 * 
	 * @param inputSource
	 * @return
	 * @throws IOException
	 * @throws SAXException
	 */
	public String parse(InputSource inputSource, String docID) 
	throws IOException, SAXException {
		this.docID = docID;
		PubMedCentralXMLContentHandler contentHandler 
			= new PubMedCentralXMLContentHandler();

		parser.setContentHandler(contentHandler);
		//parser.setEntityResolver(new PMCDTDClasspathResolver());
		parser.parse(inputSource);

		return documentText.toString();
	}


	class PubMedCentralXMLContentHandler implements ContentHandler {
		
		public void startDocument() throws SAXException {}
		public void endDocument() throws SAXException {}
		public void processingInstruction(String target, String data) 
			throws SAXException {}
		public void startPrefixMapping(String prefix, String uri) 
			throws SAXException {}
		public void endPrefixMapping(String prefix) throws SAXException {}
		public void setDocumentLocator(Locator locator) {}
		public void skippedEntity(String name) throws SAXException {}
		public void ignorableWhitespace(char[] ch, int start, int length) 
			throws SAXException {}
		
		public void startElement(String uri, String localName, String qName, Attributes atts) 
		throws SAXException {
			if (tagSet.contains(localName.toLowerCase())) {
				
				Annotation ta = new Annotation();
				ta.start=documentText.length();
				ta.end=1000000;
				

				ta.type=localName;
				if (atts.getLength() > 0) {
					if (atts.getValue(NAME_ATTRIBUTE_NAME) != null) {
						ta.name=atts.getValue(0);
						if (atts.getValue(0).trim().length() > 0) {
							documentText.append(" " + atts.getValue(0) + " ");
							//System.out.println("Adding: \" " + atts.getValue(0) + " \"");
						}
					}
					if (atts.getValue(TITLE_ATTRIBUTE_NAME) != null) {
						ta.name=atts.getValue(0);
						if (atts.getValue(0).trim().length() > 0) {
							documentText.append(" " + atts.getValue(0) + " ");
							//System.out.println("Adding: \" " + atts.getValue(0) + " \"");
						}
					}
				}
				//System.out.println("/attributes?");
				//System.out.print("pushing: " +ta.getClassMention().getMentionName() + " ");
				//ta.printAnnotationOnOneLine(System.out);
				stack.push(ta);
			}
		}

		public void endElement(String uri, String localName, String qName) 
		throws SAXException {
			if (!stack.isEmpty()) {
				Annotation ta = stack.peek();
				if (ta != null) {
					//String taType = (String) ta.getClassMention().getSlotMentionsByName(TYPE_SLOT_NAME).get(0).getSlotValues().get(0);
					String taType = ta.type;
					if (taType.toLowerCase().equals(localName.toLowerCase())) {
						ta.end=documentText.length();
						annotations.add(ta);
						//Annotation wtf = stack.pop();
						//System.out.print("popping");
						//wtf.printAnnotationOnOneLine(System.out);
					}
					else {
						//System.out.println("unstacked tag:" + localName);
						//System.out.println("found " + taType + " instead");
					}
				}
				else {
					//System.out.println("unknown or null tag: " + localName);
				}
			}
			else {
				//System.out.println("empty stack: " + localName);
			}
		}


		public void characters(char[] ch, int start, int length) 
		throws SAXException {
			String s = new String(ch, start, length);	
			if (s.trim().length() > 0) {
				//documentText.append(s.trim() + " ");
				//System.out.println("ADDING: \"" + s.trim() + " \"");
				documentText.append(s);
				//System.out.println("ADDING: \"" + s + "\"");
			}
			if (documentText.toString().lastIndexOf("\n") > 60) {
				documentText.append("\n");
			}

		}

	}

	public class Annotation {
		public String type;
		public String name;
		public int start;
		public int end;
		public String toString() {
			return name + ":" + type + ", " + start + ":" + end;
		}
	}
}


