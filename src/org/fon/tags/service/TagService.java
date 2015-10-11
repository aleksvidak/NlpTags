package org.fon.tags.service;

import java.io.IOException;
import java.io.InputStream;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;
import com.sun.jersey.multipart.FormDataParam;

import fon.tags.input.TextInput;
import fon.tags.nlp.KeyphrasesParser;
import fon.tags.nlp.KeywordsParser;

@Path("/v1/tag")
public class TagService {

	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	public Response tagFile(
			@FormDataParam("file") InputStream fileInputStream,
			@FormDataParam("method") String method,
			@FormDataParam("number") int number) throws IOException {

		String text = TextInput.getStringFromInputStream(fileInputStream);

		String json = "";
		if (method.equalsIgnoreCase("keywords")) {
			KeywordsParser keywordsParser = new KeywordsParser();
			Gson gson = new Gson();
			json = gson.toJson(keywordsParser.toKeywordsNoStop(text, number));
		} else if (method.equalsIgnoreCase("keyphrases")) {
			KeyphrasesParser keyphrasesParser = new KeyphrasesParser();
			Gson gson = new Gson();
			json = gson.toJson(keyphrasesParser.toKeyphrases(text, number));

		}
		return Response.status(200).entity(json).build();
	}

}
