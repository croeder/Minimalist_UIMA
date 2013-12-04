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
 * The latest PMC DTD can be downloaded from: ftp://ftp.ncbi.nih.gov/pub/archive_dtd/archiving/
 * 
 * Download and unpack archive-interchange-dtd-3.0.zip for example. When unpacked there will be an
 * archiving/ directory. From that directory, create a jar file using the following command:
 * 
 * jar -cf pmc-dtd-3.0.jar *
 * 
 * @author Bill Baumgartner
 * 
 */
public class PmcDtdClasspathResolver implements EntityResolver {

	Logger logger = Logger.getLogger(PmcDtdClasspathResolver.class);


	@Override
	public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
	
		if (systemId.contains("mathml")) {
			systemId = systemId.substring(systemId.lastIndexOf("mathml") - 1);
		} else if (systemId.contains("iso8879")) {
			systemId = systemId.substring(systemId.lastIndexOf("iso8879") - 1);
		} else if (systemId.contains("iso9573-13")) {
			systemId = systemId.substring(systemId.lastIndexOf("iso9573-13") - 1);
		} else if (systemId.contains("xmlchars")) {
			systemId = systemId.substring(systemId.lastIndexOf("xmlchars") - 1);
		} else {
			systemId = systemId.substring(systemId.lastIndexOf(File.separatorChar));
		}


		logger.debug("RESOLVING ENTITY: publicID: \"" + publicId + "\"   systemID: \"" + systemId + "\"") ;

		InputStream stream = getClass().getResourceAsStream(systemId);
		if (stream == null) {
			logger.warn("not RESOLVING ENTITY (null stream from systemId): publicID: \"" + publicId + "\"   systemID: \"" + systemId + "\"");
			return null;
		} else {
			return new InputSource(stream);
		}
	}

}
