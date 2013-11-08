package com.croeder.uima_sample.relational_document_index;

import static java.lang.System.out;

import java.util.List;
import java.util.ArrayList;


public class StubDocumentProvider implements DocumentProvider {

	public StubDocumentProvider() { }	

	public List<String> getIdRange(int batchStart, int batchEnd) {
		List<String> strings =  new ArrayList<>();
		for (int i=batchStart; i<batchEnd; i++) {
			strings.add("" + i);
		}
		return strings;	
	}

	public String getDocumentPath(String pmid) {
		return pmid;
	}


	public static void main(String args[]) {
        DocumentProvider da = new StubDocumentProvider();
        List<String> list = da.getIdRange(0,10);
        for (String i : list) {
            out.println("" + i);
        }

        String path = da.getDocumentPath(list.get(0));
        out.println(path);
    }
}
