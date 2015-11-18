package org.fon.tags.service;

import java.io.IOException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;

import fon.tags.nlp.KeyphrasesParser;
import fon.tags.nlp.KeywordsParser;

@Path("/v1/tag")
public class TagService {

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response tagFile(
			@QueryParam("text") String text,
			@QueryParam("method") String method,
			@QueryParam("number") int number) throws IOException {

		//String text = TextInput.getStringFromInputStream(fileInputStream);
		String json = "";
		if (method.equalsIgnoreCase("stopwords")) {
			KeywordsParser keywordsParser = new KeywordsParser();
			Gson gson = new Gson();
			json = gson.toJson(keywordsParser.toKeywordsNoStop(text, number));
		} else if (method.equalsIgnoreCase("frequency")) {
			KeywordsParser keywordsParser = new KeywordsParser();
			Gson gson = new Gson();
			json = gson.toJson(keywordsParser.toKeywords(text, number));
		}
		else if (method.equalsIgnoreCase("keyphrases")) {
			KeyphrasesParser keyphrasesParser = new KeyphrasesParser();
			Gson gson = new Gson();
			json = gson.toJson(keyphrasesParser.toKeyphrases(text, number));

		}
		else {
			
		}
		return Response.status(200).entity(json).build();
	}

}
