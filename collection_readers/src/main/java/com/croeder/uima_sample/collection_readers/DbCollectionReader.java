package com.croeder.uima_sample.collection_readers;


import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.jcas.JCas;
import org.apache.uima.util.Progress;
import org.apache.uima.util.ProgressImpl;
import org.apache.uima.resource.ResourceInitializationException;

import org.uimafit.component.JCasCollectionReader_ImplBase;
import org.uimafit.descriptor.ConfigurationParameter;
import org.uimafit.factory.ConfigurationParameterFactory;

import com.croeder.uima_sample.relational_document_index.DocumentProviderFactory;
import com.croeder.uima_sample.relational_document_index.DocumentProviderType;
import com.croeder.uima_sample.relational_document_index.DocumentProvider;

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
		current++;
		String path = dp.getDocumentPath(idList.get(current));
		String text = dp.getDocumentText(path);
		jcas.setDocumentText(text);	

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
