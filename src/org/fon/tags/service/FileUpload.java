package org.fon.tags.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONObject;

import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;

import fon.tags.input.TextInput;
import fon.tags.nlp.Lemmatizer;
import fon.tags.nlp.LexParser;

@Path("/v1/lemmatize")
public class FileUpload {

	private static final String UPLOAD_LOCATION_FOLDER = "C:/Users/Aleksandar/Desktop/Upload_Files/";
	
	
	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response uploadFile(@FormDataParam("file") InputStream fileInputStream,
							   @FormDataParam("file") FormDataContentDisposition contentDispositionHeader,
							   @FormDataParam("method") String method,
							   @FormDataParam("number") int number) throws IOException {
		
		String filePath = UPLOAD_LOCATION_FOLDER + contentDispositionHeader.getFileName();
		saveFile(fileInputStream, filePath);
		//System.out.println(method);
		//String output = "File saved to server location : " + filePath;
		Object entity = null;
		
		if (method.equalsIgnoreCase("keywords")) {
			Lemmatizer lemmatizer = new Lemmatizer();
			entity= lemmatizer.lemmatizeNoStopWords(TextInput.readFile(filePath), number).toString();
			//return Response.status(200).entity(lemmatizer.lemmatizeNoStopWords(TextInput.readFile(filePath), number).toString()).build();
		}
		else if (method.equalsIgnoreCase("keyphrases")) {
			LexParser lex = new LexParser();
			entity = lex.lemmatizePhrases(TextInput.readFile(filePath), number).toString();
		}
			
		return Response.status(200).entity(entity).build();
	}

	private void saveFile(InputStream uploadedInputStream, String serverLocation) {

		try {
			OutputStream outputStream = new FileOutputStream(new File(serverLocation));
			int read = 0;
			byte[] bytes = new byte[1024];
			while ((read = uploadedInputStream.read(bytes)) != -1) {
				outputStream.write(bytes,0,read);
			}
			outputStream.flush();
			outputStream.close();
		} catch (Exception e) {
			
		}
	}
	
	
}
