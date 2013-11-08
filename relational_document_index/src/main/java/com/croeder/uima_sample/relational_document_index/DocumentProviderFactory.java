package com.croeder.uima_sample.relational_document_index;




public class DocumentProviderFactory {

	public static DocumentProvider getDocumentProvider(DocumentProviderType dpt) {
		DocumentProvider provider=null;
		switch (dpt) {
			case Stub:
				provider = new StubDocumentProvider();
				break;
			case PMC:
				provider = new PmcDocumentProvider();
				break;
			case Elsevier:
				break;
			case Medline:
				break;
		}
		return provider;
	}
}
