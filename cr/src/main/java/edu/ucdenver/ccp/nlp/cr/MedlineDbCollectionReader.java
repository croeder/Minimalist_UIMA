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
package edu.ucdenver.ccp.nlp.cr;


import static java.lang.System.out;

import java.io.IOException;

import java.util.List;
import java.util.Collection;

import org.apache.uima.UimaContext;
import org.apache.uima.jcas.JCas;
import org.apache.uima.util.Progress;
import org.apache.uima.util.ProgressImpl;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.collection.CollectionReader;
import org.apache.uima.resource.metadata.TypeSystemDescription;

import org.uimafit.component.JCasCollectionReader_ImplBase;
import org.uimafit.descriptor.ConfigurationParameter;
import org.uimafit.factory.ConfigurationParameterFactory;
import org.uimafit.factory.CollectionReaderFactory;
import org.uimafit.util.JCasUtil;

import edu.ucdenver.ccp.nlp.doc.DocumentProviderFactory;
import edu.ucdenver.ccp.nlp.doc.DocumentProviderType;
import edu.ucdenver.ccp.nlp.doc.DocumentProvider;
import edu.ucdenver.ccp.nlp.doc.XsltConverter;
import edu.ucdenver.ccp.nlp.doc.CcpXmlParser;

import edu.ucdenver.ccp.nlp.ts.TextAnnotation;

import org.apache.log4j.Logger;

import org.xml.sax.SAXException;


/**
 * Pubmed Central Open Access Db Collection Reader
 **/
public class MedlineDbCollectionReader extends DbCollectionReader {

	static Logger logger = Logger.getLogger(PmcOaDbCollectionReader.class);

	@Override
	public void getNext(JCas jcas) 
	throws IOException, CollectionException {
		// get text
		String path = dp.getDocumentPath(idList.get(current));

		String plainText = dp.getDocumentText(path);

		// set text
		jcas.setDocumentText(plainText);	
		current++;

		// set SDI
		/**
		SourceDocumentInformation srcDocInfo = new SourceDocumentInformation(jcas);
		srcDocInfo.setUri(path);
		srcDocInfo.setDocumentSize(text.length);
		srcDocInfo.addToIndexes();
		**/
	}

	public static CollectionReader createCollectionReader(TypeSystemDescription tsd, int batch) 
	throws ResourceInitializationException  {
		CollectionReader cr = CollectionReaderFactory.createCollectionReader(
            MedlineDbCollectionReader.class, tsd,
            DbCollectionReader.PARAM_BATCH_NUMBER, batch,
            DbCollectionReader.PARAM_COLLECTION_TYPE, DocumentProviderType.Medline.toString());
		return cr;
	}

}
