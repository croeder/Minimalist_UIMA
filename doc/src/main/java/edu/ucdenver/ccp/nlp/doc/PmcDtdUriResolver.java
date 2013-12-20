package edu.ucdenver.ccp.nlp.doc;

import static java.lang.System.out;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.transform.URIResolver;
import javax.xml.transform.TransformerException;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;

/**
 * This class is used to allow DTD's to be added to the classpath and then found accordingly.<
 * http://www.jezuk.co.uk/cgi-bin/view/jez?id=2260
 * 
 * The latest PMC DTD can be downloaded from: ftp://ftp.ncbi.nih.gov/pub/archive_dtd/archiving/
 * 
 * Download and unpack archive-interchange-dtd-3.0.zip for example. When unpacked there will be an
 * archiving/ directory. From that directory, create a jar file using the following command:
 * 
 * jar -cf pmc-dtd-3.0.jar *
 * 
 * @author croeder
 * 
 */
public class PmcDtdUriResolver implements URIResolver {

	static Logger logger = Logger.getLogger(PmcDtdUriResolver.class);

	public PmcDtdUriResolver() {
		logger.error("****PmcDtdUriResolver ctor****");
	}

	@Override
	public Source resolve(String href, String base)  
	throws TransformerException {
		logger.error("--------------->" + href + "   " + base);
		try {	
			
			String resource=href;
			if (href.length() > 11) {
				resource=href.substring(11);	
			}
			InputStream stream = getClass().getResourceAsStream(resource);
			return new StreamSource(stream, resource);
		}
		catch (Exception e) {
			logger.error("error in UriResolver:" + e);
			throw new TransformerException(e);
		}	
	}

}
