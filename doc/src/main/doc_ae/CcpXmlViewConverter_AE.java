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

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.uimafit.factory.AnalysisEngineFactory;
import org.uimafit.factory.TypeSystemDescriptionFactory;
import org.xml.sax.SAXException;

import edu.uchsc.ccp.util.nlp.parser.CCPXMLParser;
import edu.uchsc.ccp.util.nlp.annotation.TextAnnotation;
import edu.uchsc.ccp.uima.util.UIMA_Util;

import org.apache.log4j.Logger;

/*
 * reads the RAW view and outputs to the default view.
 * You will likely need to pipe from another's default view
 * to RAW suing SOFA mapping in the pipeline descriptor in CPE.
 * ...not sure how to do this in uimaFIT pipelines.
 */
public class CcpXmlViewConverter_AE extends ViewConverter_AE {
	
	CCPXMLParser p;
	static Logger logger=Logger.getLogger(CCPXMLViewConverter_AE.class);
	
	public static AnalysisEngine createAnalysisEngine(
			TypeSystemDescription tsd, 
			String sourceViewName,
			String destinationViewName) 
	throws ResourceInitializationException {
		return AnalysisEngineFactory.createPrimitive(
				CCPXMLViewConverter_AE.class, tsd, 
				PARAM_SOURCE_VIEW_NAME, sourceViewName, 
				PARAM_DESTINATION_VIEW_NAME, destinationViewName);
	}

	@Override
	protected void convertView(JCas sourceView, JCas destinationView)
			throws AnalysisEngineProcessException {
		try {
			p = new CCPXMLParser();
			String docID = UIMA_Util.getDocumentID(sourceView);
			String inString = sourceView.getDocumentText();
			logger.info("CCPXMLViewConverter_AE reading docID: \"" + docID + "\"");
			//logger.info("    reading doc: \"" + inString + "\"");

			String outString = p.parse(sourceView.getDocumentText(), docID);
				
			List<TextAnnotation> taList = p.getAnnotations();
			UIMA_Util util = new UIMA_Util();
			util.putTextAnnotationsIntoJCas(destinationView, taList);		
			UIMA_Util.setDocumentID(destinationView, docID);
			destinationView.setDocumentText(outString);
		} catch (IOException e) {
			e.printStackTrace();
			throw new AnalysisEngineProcessException(e);
		} catch (SAXException e) {
			logger.error("SAXException involving docID: \"" + UIMA_Util.getDocumentID(sourceView) + "\"");
			logger.error("SAXException involving doc: \"" + sourceView.getDocumentText() + "\"");
			e.printStackTrace();
			throw new AnalysisEngineProcessException(e);
		}
	}
	
	public static String createXmlDescriptor(TypeSystemDescription tsd) 
	throws ResourceInitializationException, SAXException, IOException {
		AnalysisEngineDescription aeDesc 
		= AnalysisEngineFactory.createPrimitiveDescription(
				CCPXMLViewConverter_AE.class, tsd, 
				PARAM_SOURCE_VIEW_NAME, View.XML.viewName(), 
				PARAM_DESTINATION_VIEW_NAME, View.DEFAULT.viewName());

		StringWriter sw = new StringWriter();
		aeDesc.toXML(sw);
		sw.close();
		return sw.toString();
	}
	
	public static void main(String[] args) {
		try {
	       TypeSystemDescription typeSystem =
	           TypeSystemDescriptionFactory.createTypeSystemDescription(
	        		   "typeSystem.CCPTypeSystem");
			String s = CCPXMLViewConverter_AE.createXmlDescriptor(typeSystem);
			System.out.println(s);
		}
		catch (Exception x) {
			;
		}
	}

}
