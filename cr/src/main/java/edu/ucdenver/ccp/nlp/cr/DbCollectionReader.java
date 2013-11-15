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


import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.jcas.JCas;
import org.apache.uima.util.Progress;
import org.apache.uima.util.ProgressImpl;
import org.apache.uima.resource.ResourceInitializationException;

import org.uimafit.component.JCasCollectionReader_ImplBase;
import org.uimafit.descriptor.ConfigurationParameter;
import org.uimafit.factory.ConfigurationParameterFactory;

import edu.ucdenver.ccp.nlp.doc.DocumentProviderFactory;
import edu.ucdenver.ccp.nlp.doc.DocumentProviderType;
import edu.ucdenver.ccp.nlp.doc.DocumentProvider;

import org.apache.log4j.Logger;

public class DbCollectionReader extends JCasCollectionReader_ImplBase {

	static Logger logger = Logger.getLogger(DbCollectionReader.class);

	public static final String PARAM_BATCH_NUMBER 
		= ConfigurationParameterFactory.createConfigurationParameterName(
			DbCollectionReader.class, "batchNumber");
	@ConfigurationParameter(mandatory=true, description="number of batch of 1000 articles out of those available.")
	int batchNumber;

	public static final String PARAM_COLLECTION_TYPE
		= ConfigurationParameterFactory.createConfigurationParameterName(
			DbCollectionReader.class, "collectionTypeString");
	@ConfigurationParameter(mandatory=true, description="name of the collection to use. see DocumentProviderType.")
	String collectionTypeString;

	int end=1000; // TODO, this constant is EVEYRWHERE
	int current=0;
	List<String> idList;
	DocumentProvider dp ;


	@Override
	public void initialize(UimaContext context) 
	throws ResourceInitializationException {
		try {
			DocumentProviderType collectionType = DocumentProviderType.valueOf(collectionTypeString);
			dp = DocumentProviderFactory.getDocumentProvider(collectionType);
			if (dp == null) {
				logger.error("WTFW????????????????" + collectionType);
			}
			idList = dp.getIdRange(batchNumber);
		}
		catch (Exception e ) {
			logger.error(e);
			e.printStackTrace();
			throw new ResourceInitializationException(e);
		}
	}

	@Override
	public void getNext(JCas jcas) {
		String path = dp.getDocumentPath(idList.get(current));
		String text = dp.getDocumentText(path);
		jcas.setDocumentText(text);	
		current++;

		/*
		SourceDocumentInformation srcDocInfo = new SourceDocumentInformation(jcas);
		srcDocInfo.setUri(path);
		srcDocInfo.setDocumentSize(text.length);
		srcDocInfo.addToIndexes();
		*/
	}

	@Override
	public boolean hasNext() {
		return current < end;
	}	

	@Override
	public Progress[] getProgress() {
		int completed=0;
		int total=0;
		Progress[] progArray = new Progress[1];
		progArray[0] = new ProgressImpl(completed, total, "article");
		return progArray;
	}
}
