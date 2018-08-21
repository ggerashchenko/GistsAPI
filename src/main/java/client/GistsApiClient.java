package client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import models.GistsDto;
import models.PostGistDto;
import org.junit.Assume;
import utils.TestPropertiesLoader;

import java.io.IOException;

public class GistsApiClient {
	static {
		RestAssured.baseURI = TestPropertiesLoader.getBaseUrl();
	}

	private String token = TestPropertiesLoader.getToken();

	public Response getGists() {
		return RestAssured.given()
				.header("Authorization", "Bearer " + token)
				.get("/gists");
	}

	public GistsDto.GistDetails[] getParsedGists() throws IOException {
		Response getGistsResponse = getGists();
		Assume.assumeTrue(getGistsResponse.getStatusCode() == 200);
		com.fasterxml.jackson.databind.ObjectMapper jasksonObjectMapper = new com.fasterxml.jackson.databind.ObjectMapper();
		return jasksonObjectMapper.readValue(getGistsResponse.getBody().print(), GistsDto.GistDetails[].class);
	}

	public void postGist(PostGistDto.GistDetails gist) throws JsonProcessingException {
		ObjectMapper jacksonObjectMapper = new ObjectMapper();
		String bodyString = jacksonObjectMapper.writeValueAsString(gist);
		RestAssured.given()
				.header("Authorization", "Bearer " + System.getProperty("token"))
				.contentType("application/json")
				.body(bodyString)
				.expect()
				.statusCode(201)
				.when()
				.post("/gists");
	}

	public Response postGistWithResponse(PostGistDto.GistDetails gist) throws JsonProcessingException {
		ObjectMapper jacksonObjectMapper = new ObjectMapper();
		String bodyString = jacksonObjectMapper.writeValueAsString(gist);
		return RestAssured.given()
				.header("Authorization", "Bearer " + System.getProperty("token"))
				.contentType("application/json")
				.body(bodyString)
				.when()
				.post("/gists");
	}

	public Response getGistById(String gistId){
		Response response = RestAssured.given()
				.header("Authorization", "Bearer " + token)
				.contentType("application/json")
				.get("/gists/"+gistId);

		return response;
	}

	public Response updateGist(PostGistDto.GistDetails gist, String gistId) throws IOException {
		ObjectMapper jacksonObjectMapper = new ObjectMapper();
		String bodyString = jacksonObjectMapper.writeValueAsString(gist);

		Response response = RestAssured.given()
				.header("Authorization", "Bearer " + token)
				.contentType("application/json")
				.body(bodyString)
				.patch("/gists/"+gistId);

		return response;
	}

	public Response deleteGist(String gistId){
		Response response = RestAssured.given()
			.header("Authorization", "Bearer " + token)
			.contentType("application/json")
			.delete("/gists/"+gistId);
		return response;
	}
}
