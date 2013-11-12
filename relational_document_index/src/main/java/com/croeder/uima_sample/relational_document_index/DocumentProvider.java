package com.croeder.uima_sample.relational_document_index;

import java.util.List;

public interface DocumentProvider {	
	int getMaxBatchIndex();
	List<String> getIdRange(int batchNumber);
	String getDocumentPath(String id);
	String getDocumentText(String path);
}

