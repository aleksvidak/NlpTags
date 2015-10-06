package org.fon.tags.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONObject;

import com.google.gson.Gson;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;

import fon.tags.input.TextInput;
import fon.tags.nlp.KeywordsParser;
import fon.tags.nlp.KeyphrasesParser;

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

		String json = "";
		if (method.equalsIgnoreCase("keywords")) {
			KeywordsParser keywordsParser = new KeywordsParser();
			Gson gson = new Gson(); 
			json = gson.toJson(keywordsParser.toKeywordsNoStop(TextInput.readFile(filePath), number));
		}
		else if (method.equalsIgnoreCase("keyphrases")) {
			KeyphrasesParser keyphrasesParser = new KeyphrasesParser();
			Gson gson = new Gson(); 
			json = gson.toJson(keyphrasesParser.toKeyphrases(TextInput.readFile(filePath), number));
			
		}			
		return Response.status(200).entity(json).build();
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
