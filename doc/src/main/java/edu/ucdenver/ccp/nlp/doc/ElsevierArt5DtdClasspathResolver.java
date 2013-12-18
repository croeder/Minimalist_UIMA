package edu.ucdenver.ccp.nlp.doc;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import org.apache.log4j.Logger;

/**
 * This class is used to allow DTD's to be added to the classpath and then found accordingly.<br>
 * Code snippet from: http://www.theserverside.com/discussions/thread.tss?thread_id=24895 <br>
 * Reference: http://www.ibm.com/developerworks/library/x-tipent.html
 * 
 * The latest Elsevier  DTDs can be downloaded from: 
 * http://www.elsevier.com/author-schemas/elsevier-xml-dtds-and-transport-schemas#DTD-5.0.2
 * 
 * @author roederc
 * 
 */
public class ElsevierArt5DtdClasspathResolver implements EntityResolver {

	Logger logger = Logger.getLogger(ElsevierArt5DtdClasspathResolver.class);


	@Override
	public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {

		//logger.debug("CLASSPATH:" + System.getProperty("java.class.path"));
	
		logger.debug("TRYING TO RESOLVE ENTITY: publicID: \"" + publicId + "\"   systemID: \"" + systemId + "\"") ;
		String parts[] = {
			"html",
			"mathml",
			"iso8879",
			"iso9573-13",
			"xmlchars",
            "mathml2-mod-ES.dtd",
			"mathml2-qname-1.mod",
			"ESextra.ent",
			"common110_1",
			"common115",
			"common120"
		};

		boolean found=false;
		for (String p : parts) {
			if (systemId.contains(p)) {
				systemId = systemId.substring(systemId.lastIndexOf(p) - 1);
				logger.debug("found entity in list: publicID: \"" + publicId + "\"   systemID: \"" + systemId + "\"") ;
				found = true;
				break;
			}
		}

		if (!found) {
			systemId = systemId.substring(systemId.lastIndexOf(File.separatorChar));
		}


		InputStream stream = getClass().getResourceAsStream(systemId);
		if (stream == null) {
			logger.warn("**** not RESOLVING ENTITY (null stream from systemId): publicID: \"" + publicId + "\"   systemID: \"" + systemId + "\"");
			return null;
		} else {
			logger.debug("RESOLVED ENTITY: publicID: \"" + publicId + "\"   systemID: \"" + systemId + "\"") ;
			return new InputSource(stream);
		}
	}

}
