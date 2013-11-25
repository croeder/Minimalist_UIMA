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

import edu.uchsc.ccp.util.nlp.annotation.TextAnnotation;
import edu.uchsc.ccp.util.nlp.mention.ClassMention;
import edu.uchsc.ccp.util.nlp.mention.SlotMention;
import edu.uchsc.ccp.util.nlp.annotation.Span;
import edu.uchsc.ccp.util.nlp.annotation.InvalidSpanException;
import edu.uchsc.ccp.util.nlp.annotation.Annotator;
import edu.uchsc.ccp.util.nlp.annotation.AnnotationSet;


/*
 * See the Test class for an example of what an input doc. looks like.
 * 
 * Basic Strategy is to create a ClassMention named "Section", 
 * with a SlotMention named "Type" whose value is the the tag name.
 * An optional SlotMention named "Name" has a value for any names or titles.
 * The associated TextAnnotation has the span.
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
	private Stack<TextAnnotation> stack = new Stack<TextAnnotation>();
	private List<TextAnnotation> annotations = new ArrayList<TextAnnotation>();
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
		
//		Iterator<String> i = tagSet.iterator();
//		while (i.hasNext()) {
//			System.out.println(i.next());
//		}
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
	
	public List<TextAnnotation> getAnnotations() { return annotations; }

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
		
		public void startElement(String uri, String localName, String qName, 
				Attributes atts) throws SAXException {
			if (tagSet.contains(localName.toLowerCase())) {
				
				ClassMention cm = new ClassMention(CLASS_MENTION_NAME);
				Annotator annotator = new Annotator(
					Annotator.CCPXMLParser_ANNOTATOR_ID, 
					PARSER_NAME, PARSER_NAME, Annotator.UCDENVER_AFFILIATION);
				AnnotationSet aSet = new AnnotationSet(-1,"","");
				TextAnnotation ta = new TextAnnotation(0,0,"",annotator, aSet,
						-1,-1, docID, -1, cm);
				try {
					ta.setSpan(new Span(documentText.length(), 1000000));
				}
				catch (InvalidSpanException x) {
					System.err.println("CCPXMLParser" + x);
				}
				

				ta.setClassMention(cm);
				cm.addTextAnnotation(ta);
				SlotMention typeSlot = new SlotMention(TYPE_SLOT_NAME);
				typeSlot.setFirstSlotValue(localName);
				cm.addSlotMention(typeSlot);
				// kmv //System.out.println("attributes?");
				if (atts.getLength() > 0) {
					if (atts.getValue(NAME_ATTRIBUTE_NAME) != null) {
						SlotMention nameSlot = new SlotMention(NAME_SLOT_NAME);
						nameSlot.setFirstSlotValue(
									atts.getValue(0));
						if (atts.getValue(0).trim().length() > 0) {
							documentText.append(" " + atts.getValue(0) + " ");
							// kmv //System.out.println("Adding: \" " + atts.getValue(0) + " \"");
						}
						cm.addSlotMention(nameSlot);
					}
					if (atts.getValue(TITLE_ATTRIBUTE_NAME) != null) {
						SlotMention nameSlot = new SlotMention(TITLE_SLOT_NAME);
						nameSlot.setFirstSlotValue(
									atts.getValue(0));
						if (atts.getValue(0).trim().length() > 0) {
							documentText.append(" " + atts.getValue(0) + " ");
							// kmv //System.out.println("Adding: \" " + atts.getValue(0) + " \"");
						}
						cm.addSlotMention(nameSlot);
					}
				}
				// kmv //System.out.println("/attributes?");
				//System.out.print("pushing: " +ta.getClassMention().getMentionName() + " ");
				//ta.printAnnotationOnOneLine(System.out);
				stack.push(ta);
			}
		}

		public void endElement(String uri, String localName, String qName) 
		throws SAXException {
			if (!stack.isEmpty()) {
				TextAnnotation ta = stack.peek();
				if (ta != null) {
					String taType = (String) ta.getClassMention().getSlotMentionsByName(TYPE_SLOT_NAME).get(0).getSlotValues().get(0);
					if (taType.toLowerCase().equals(localName.toLowerCase())) {
						try {
							ta.getSpans().get(0).setSpanEnd(documentText.length());
						}
						catch (InvalidSpanException x) {
							System.err.println("invalid span:" + x + "\n" + localName);
						}
						annotations.add(ta);
						TextAnnotation wtf = stack.pop();
						//System.out.print("popping");
						//wtf.printAnnotationOnOneLine(System.out);
					}
					else {
						// kmv //System.out.println("unstacked tag:" + localName);
						// kmv //System.out.println("found " + taType + " instead");
					}
				}
				else {
					// kmv //System.out.println("unknown or null tag: " + localName);
				}
			}
			else {
				// kmv //System.out.println("empty stack: " + localName);
			}
		}


		public void characters(char[] ch, int start, int length) 
		throws SAXException {
			String s = new String(ch, start, length);	
			if (s.trim().length() > 0) {
				//documentText.append(s.trim() + " ");
				//System.out.println("ADDING: \"" + s.trim() + " \"");
				documentText.append(s);
				// kmv //System.out.println("ADDING: \"" + s + "\"");
			}
			if (documentText.toString().lastIndexOf("\n") > 60) {
				documentText.append("\n");
			}

		}

	}
}



