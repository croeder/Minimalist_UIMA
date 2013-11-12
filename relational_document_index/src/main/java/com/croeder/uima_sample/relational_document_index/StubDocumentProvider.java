package com.croeder.uima_sample.relational_document_index;

import static java.lang.System.out;

import java.util.List;
import java.util.ArrayList;


public class StubDocumentProvider implements DocumentProvider {

	public StubDocumentProvider() { }	

	public int getMaxBatchIndex() {
		return 1000;
	}

	public List<String> getIdRange(int batchNumber) {
		List<String> strings =  new ArrayList<>();
		for (int i=batchNumber * 1000; i<batchNumber * 1000 + 1000; i++) {
			strings.add("" + i);
		}
		return strings;	
	}

	public String getDocumentPath(String pmid) {
		return pmid;
	}

	public String getDocumentText(String pmid) {
		return "" + pmid;
	}


	public static void main(String args[]) {
        DocumentProvider da = new StubDocumentProvider();
        List<String> list = da.getIdRange(100);
        for (String i : list) {
            out.println("" + i);
        }

        String path = da.getDocumentPath(list.get(0));
        out.println(path);
    }
}
