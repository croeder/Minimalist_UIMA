package com.croeder.uima_sample.web_service;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.FormParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Context;

@Path("conceptmapper")
public class ConceptMapperResource {

    /**
     * Method handling HTTP GET requests. The returned object will be sent
     * to the client as "text/plain" media type.
     *
     * @return String that will be returned as a text/plain response.
     */
    @POST
	//@Consumes(MediaType.TEXT_PLAIN) // {"text/plain"})
    @Produces(MediaType.TEXT_PLAIN)
    public void doPost(@FormParam("text") String text
		// , @Context HttpServletResponse servletResponse
	) throws IOException {

		System.out.println("POST ConceptMapper:" + text); 

		//servletResponse.sendRedirect("../create_todo.html");
    }

	@GET
	public String doGet() {
		System.out.println("GET ConceptMapper: (no param)"); 
		return "Got it (no param)";
	}

	@GET
	@Path("conceptmapper/{ontology}")
	public String doGet(@PathParam("ontology") String ontologyName) {
		System.out.println("GET ConceptMapper, param" + ontologyName ); 
		return "Got it" + ontologyName;
	}

}
