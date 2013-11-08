package com.croeder.uima_sample.collection_readers;

import org.apache.uima.UimaContext;
import org.apache.uima.jcas.JCas;
import org.apache.uima.util.Progress;
import org.apache.uima.util.ProgressImpl;

import org.uimafit.component.JCasCollectionReader_ImplBase;

import com.croeder.uima_sample.relational_document_index.DocumentProviderFactory;
import com.croeder.uima_sample.relational_document_index.DocumentProviderType;
import com.croeder.uima_sample.relational_document_index.DocumentProvider;


public class PmcDbCollectionReader extends JCasCollectionReader_ImplBase {

	int start=0;
	int end=10;
	int current=0;
	List<String> idList;

	DocumentProvider dp 
		= DocumentProviderFactory.getDocumentProvider(DocumentProviderType.Stub);

	@Override
	public void initialize(UimaContext context) 
	throws ResourceInitializationException {
		current=start;
		idList = dp.getIdRange(start, end);
	}

	@Override
	public void getNext(JCas jcas) {
		jcas.setDocumentText(text);	

		current++;

		SourceDocumentInformation srcDocInfo = new SourceDocumentInformation(jcas);
		srcDocInfo.setUri(dp.getDocumentPath(idList.get(current));
		srcDocInfo.setDocumentSize(23);
		srcDocInfo.addToIndexes();
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
