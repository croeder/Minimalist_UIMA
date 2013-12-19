/*
Copyright (c) 2012, Regents of the University of Colorado
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

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.URIResolver;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.EntityResolver;

import org.apache.xml.resolver.tools.CatalogResolver;

import org.apache.log4j.Logger;


public class XsltConverter {

	static Logger logger  = Logger.getLogger(XsltConverter.class);

	EntityResolver er;

	// http://codingwithpassion.blogspot.com/2011/03/saxon-xslt-java-example.htmlsax
	// http://stackoverflow.com/questions/2968190/how-to-select-saxon-transformerfactory-in-java
	static final String transformerFactoryPropertyName = "javax.xml.transform.TransformerFactory";

	public XsltConverter() {
		er = new PmcDtdClasspathResolver();
	}

	public XsltConverter(EntityResolver er) {
		this.er = er;
	}
	
	public static void main(String args[]) {
		if (args.length < 2) {
			usage();
		}
		else {
			XsltConverter me = new XsltConverter(); 
			String xmlFilename = args[0];
			String xsltFilename = args[1];
			String input=readFile(xmlFilename);
			try {
				String output=me.convert(input,xsltFilename);
				logger.debug(output);
			}
			catch(Exception x) {
				logger.error("XsltConverter error with file::\"" + xsltFilename + "\"");
				logger.error("XsltConverter failed:" + x);
			}
		}
		
	}
	
	public static void usage() {
		System.out.println(
			"java XSLTConverter <xmlFilename> <xsltFilename>");
	}
	
	public static String readFile(String filename) {
		StringBuffer sb = new StringBuffer();
		try {
			InputStream is = XsltConverter.class.getResourceAsStream(filename);
			java.io.InputStreamReader isr = new java.io.InputStreamReader(is, "UTF-8");
			java.io.BufferedReader br = new java.io.BufferedReader(isr);
			
			while (br.ready()) {	
				String s = br.readLine();
				sb.append(s);
			}
			return sb.toString();
		}
		catch (Exception x) {
			logger.error("XSTLConverter.readFile() failed on file:\"" + filename + "\" " + x);
			x.printStackTrace();
			throw new RuntimeException(x);
		}
	}

	public String convert(String input, String xsltFileName)  {
		InputStream xslStream =null;
		String retval="";
		try {

			xslStream = this.getClass().getResourceAsStream(xsltFileName);
			if (xslStream == null) {
				logger.error("XlstConverter.convert(): couldn't read the xslt file: " + xsltFileName);
			}
			else {
				// get an xslt Source
				Source xsltSource = new javax.xml.transform.stream.StreamSource(xslStream);

				// Get a transformer
				//TransformerFactory transFact = TransformerFactory.newInstance();
				TransformerFactory transFact = SAXTransformerFactory.newInstance();
				Transformer trans = transFact.newTransformer(xsltSource);
				//http://docs.oracle.com/javase/6/docs/api/index.html?javax/xml/transform/URIResolver.html
				// If an application wants to set the ErrorHandler or EntityResolver for an XMLReader used 
				// during a transformation, it should use a URIResolver to return the SAXSource which 
				// provides (with getXMLReader) a reference to the XMLReader.

				// get a source to the input xml
				StringReader sr = new StringReader(input);
				InputSource xmlSource = new InputSource(sr);
	

					// input
					//SAXSource xmlSaxSource = new SAXSource(xmlSource);

					SAXParserFactory spf = SAXParserFactory.newInstance();
					spf.setValidating(false);
					spf.setNamespaceAware(true);
					SAXParser parser = spf.newSAXParser();
					XMLReader reader = parser.getXMLReader();

					/**********/


					reader.setEntityResolver(er);
					SAXSource xmlSaxSource = new SAXSource(reader,xmlSource);
					//xmlSaxSource.setXMLReader(reader);

					// output
					StringWriter sw = new StringWriter();
					Result result = new StreamResult(sw);

					// transform
					try {
						trans.transform(xmlSaxSource, result);
					}
					catch (Exception e) {
						logger.error("error transforming " + xmlSaxSource);
						throw new RuntimeException(e);
					}
					retval = sw.toString();
					sw.close();
			}
		}
		catch (Exception x) { 
			logger.error("XSLTConverter.convert() failed:" + x);
			x.printStackTrace();
			throw new RuntimeException(x);
		}

		try { if (xslStream != null) {xslStream.close();} }
		catch (IOException x) { }

		// UGLY HACK TODO
		if (retval.indexOf("<doc>") == -1) {
			retval = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "<DOC>" + retval.substring(38) + "</DOC>";
		}

		return retval;
	
	}

}
