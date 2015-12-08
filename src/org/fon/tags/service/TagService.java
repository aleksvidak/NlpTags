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
			@QueryParam("filter") String filter,
			@QueryParam("number") int number) throws IOException {

		//String text = TextInput.getStringFromInputStream(fileInputStream);
		String json = "";
		int status = 0;
		if (method.equalsIgnoreCase("keywords")) {
			if (filter.equalsIgnoreCase("stopwords")) {
				KeywordsParser keywordsParser = new KeywordsParser();
				Gson gson = new Gson();
				json = gson.toJson(keywordsParser.toKeywordsNoStop(text, number));
				status = 1;
			} else if (filter.equalsIgnoreCase("frequency")) {
				KeywordsParser keywordsParser = new KeywordsParser();
				Gson gson = new Gson();
				json = gson.toJson(keywordsParser.toKeywords(text, number));
				status = 1;
			}
		}
		else if (method.equalsIgnoreCase("keyphrases")) {
			KeyphrasesParser keyphrasesParser = new KeyphrasesParser();
			Gson gson = new Gson();
			json = gson.toJson(keyphrasesParser.toKeyphrases(text, number));
			status = 1;
		}
		else {
			status = 0;
		}
		
		if (status==1) {
			return Response.status(200).entity(json).build();
		}
		else {
			return Response.status(400).build();
		}
	}

}
