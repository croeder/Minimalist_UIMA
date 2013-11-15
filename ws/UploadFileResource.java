/*
 Copyright (c) 2012, Regents of the University of Colorado
 All rights reserved.

 Redistribution and use in source and binary forms, with or without modification,
 are permitted provided that the following conditions are met:

 * Redistributions of source code must retain the above copyright notice, this
    list of conditions and the following disclaimer.

 * Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 * Neither the name of the University of Colorado nor the names of its
    contributors may be used to endorse or promote products derived from this
    software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
 ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.BufferedReader;

import java.nio.file.Files;
//import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.charset.Charset;

import javax.servlet.http.HttpServletResponse;
//import javax.ws.rs.FormDataParam;
import javax.ws.rs.FormParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.ImmutablePair;

// pretty much a copy of : http://www.mkyong.com/webservices/jax-rs/file-upload-example-in-jersey/

@Path("file")
public class UploadFileResource {

	private final File baseDir = new File("/Users/roederc/storage"); // TODO property

/*
	@GET @Path("/get/{fileid}")
    @Produces(MediaType.TEXT_PLAIN)
	public String getFile(@PathParam("fileid") String fileid) {
System.out.println("GET " + fileid);
		StringBuilder builder = new StringBuilder();
		
		java.nio.file.Path filePath = Paths.get(new File(baseDir, fileid).getAbsolutePath());
		try {
			BufferedReader reader = Files.newBufferedReader( filePath, Charset.forName("UTF-8"));
			while (reader.ready()) {
				builder.append(reader.readLine());
			}
		} catch(Exception e) {}
System.out.println("GET" + builder.toString());
		return builder.toString();
	}
*/

// No path on a get?

	@GET 
    @Produces(MediaType.TEXT_PLAIN)
	public String getFile() {

System.out.println("GET " );
		String fileid = "data.txt";
		StringBuilder builder = new StringBuilder();
		
		java.nio.file.Path filePath = Paths.get(new File(baseDir, fileid).getAbsolutePath());
		try {
			BufferedReader reader = Files.newBufferedReader( filePath, Charset.forName("UTF-8"));
			while (reader.ready()) {
				builder.append(reader.readLine());
			}
		} catch(Exception e) {}
System.out.println("GET" + builder.toString());

		return builder.toString();
	}

/*
    @POST @Path("/upload")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadFile(
			@FormDataParam("file") InputStream uploadedInputStream,
			@FormDataParam("file") FormDataContentDisposition fileDetail) {

		System.out.println("POST UploadFileResource:"); 

		File uploadedFile = new File(baseDir, fileDetail.getFileName());
		Pair<Integer, String> statusPair = writeToFile(uploadedInputStream, uploadedFile);
		int returnCode=0;
		switch (statusPair.getLeft()) {
		case 0:
			returnCode=201;
			break;
		case -1:
			returnCode=500;
			break;
		case -2:
			returnCode=500;
		}

		return Response.status(returnCode).entity(statusPair.getRight()).build();
    }
*/
    @PUT 
	@Path("/put/{fileid}")
	@Consumes("application/octet-stream")
    public Response putFile(
			@PathParam("fileid") String fileId,
			InputStream uploadedInputStream) {

		System.out.println("PUT UploadFileResource:" + fileId); 

		File baseDir = new File("/Users/croeder/storage"); // TODO property
		File uploadedFile = new File(baseDir, fileId);
		Pair<Integer, String> statusPair = writeToFile(uploadedInputStream, uploadedFile);
		int httpStatus=0;
		switch (statusPair.getLeft()) {
		case 0:
			httpStatus=201;
			break;
		case -1:
			httpStatus=500;
			break;
		case -2:
			httpStatus=500;
			break;
		}
		return Response.status(httpStatus).entity(statusPair.getRight()).build();
    }


	private Pair<Integer, String> writeToFile(InputStream inputStream,
		File uploadedFile) {
	
		OutputStream out = null;
		try {
			out = new FileOutputStream(uploadedFile);
			int read = 0;
			byte[] bytes = new byte[1024];
			while ((read = inputStream.read(bytes)) != -1) {
				out.write(bytes, 0, read);
			}
		}
		catch (FileNotFoundException x) {
			return new ImmutablePair(-1, 
				"Error, coulnd't write to: "  
				+ uploadedFile.getAbsolutePath() 
				+ " " + x );
		}
		catch (IOException x) {
			return new ImmutablePair(-2, 
				"Error, unknown IOException:" + x);
		}
		finally {
			try {
				out.flush();
				out.close();
			}
			catch (IOException x) {
				// eat
			}
		}
		return new ImmutablePair(0, "success");
	}

}
