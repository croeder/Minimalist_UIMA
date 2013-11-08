package com.croeder.uima_sample.relational_document_index;

import java.util.List;

public interface DocumentProvider {

	List<String> getIdRange(int batchStart, int batchEnd);
	String getDocumentPath(String id);
	String getDocumentText(String path);
}

