package com.croeder.uima_sample.web_service;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.media.multipart.MultiPartFeature;

import java.io.IOException;
import java.net.URI;



// see https://github.com/aruld/jersey2-multipart-sample/blob/master/src/main/java/com/aruld/jersey/multipart/App.java

/**
 * Main class.
 *
 */
public class Main {
    // Base URI the Grizzly HTTP server will listen on
    public static final String BASE_URI = "http://localhost:8080/myapp/";

    /**
     * Starts Grizzly HTTP server exposing JAX-RS resources defined in this application.
     * @return Grizzly HTTP server.
     */
    public static HttpServer startServer() {
        // create a resource config that scans for JAX-RS resources and providers
        // in com.croeder.uima_sample.web_service package
		// http://stackoverflow.com/questions/14288856/jersey-2-injection-source

        //final ResourceConfig rc = new ResourceConfig().packages("com.croeder.uima_sample.web_service");

		final ResourceConfig config = new ResourceConfig(UploadFileResource.class);
		config.register(MultiPartFeature.class);
        config.packages("com.croeder.uima_sample.web_service");

        // create and start a new instance of grizzly http server
        // exposing the Jersey application at BASE_URI
        return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), config);
    }

    /**
     * Main method.
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {

	
        final HttpServer server = startServer();
        System.out.println(String.format("Jersey app started with WADL available at "
                + "%sapplication.wadl\nHit enter to stop it...", BASE_URI));
        System.in.read();
        server.stop();
    }
}

