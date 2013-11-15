package edu.ucdenver.ccp.nlp.doc;

import java.util.List;

public interface DocumentProvider {	
	int getMaxBatchIndex();
	List<String> getIdRange(int batchNumber);
	String getDocumentPath(String id);
	String getDocumentText(String path);
}

