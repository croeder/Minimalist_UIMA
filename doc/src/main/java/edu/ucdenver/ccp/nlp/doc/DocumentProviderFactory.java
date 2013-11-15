package edu.ucdenver.ccp.nlp.doc;

import org.apache.log4j.Logger;


public class DocumentProviderFactory {
	static Logger logger = Logger.getLogger(DocumentProviderFactory.class);

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
				provider = new MedlineDocumentProvider();
				break;
			default:
				logger.error("unrecognized provider type:" + dpt + " returning null");
				break;
		}
		return provider;
	}
}
