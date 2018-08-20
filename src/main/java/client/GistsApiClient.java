package client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import models.GetGistsDto;
import models.PostGistDto;
import models.UpdateGistDto;
import org.json.simple.parser.ParseException;
import org.junit.Assume;
import utils.TestPropertiesLoader;
import utils.Utils;

import java.io.IOException;

public class GistsApiClient {
	private String token = TestPropertiesLoader.getToken();
	private Utils utils = new Utils();

	static {
		RestAssured.baseURI = TestPropertiesLoader.getBaseUrl();
	}

//	public io.restassured.path.json.JsonPath getGists(int expectedStatusCode) {
//		JsonPath response = RestAssured.given()
//				.header("Authorization", "Bearer " + token)
//				.expect()
//				.statusCode(expectedStatusCode)
//				.when()
//				.get("/gists")
//				.then()
//				.body(JsonSchemaValidator.matchesJsonSchemaInClasspath("getGistSchema.json"))
//				.extract().jsonPath();
//
//		return response;
//	}

	public Response getGists() {
		return RestAssured.given()
				.header("Authorization", "Bearer " + token)
				.get("/gists");
	}

	public GetGistsDto.GistDetails[] getParsedGists() throws IOException {
		Response getGistsResponse = getGists();
		Assume.assumeTrue(getGistsResponse.getStatusCode() == 200);
		com.fasterxml.jackson.databind.ObjectMapper jasksonObjectMapper = new com.fasterxml.jackson.databind.ObjectMapper();
		return jasksonObjectMapper.readValue(getGistsResponse.getBody().print(), GetGistsDto.GistDetails[].class);
	}

//	public JsonPath postGist(String gist, int expectedStatusCode) throws IOException, ParseException {
//
//		String myGist = utils.readFromFile(gist);
//
//		JsonPath response = RestAssured.given()
//				.header("Authorization", "Bearer " + token)
//				.contentType("application/json")
//				.body(myGist)
//				.expect()
//				.statusCode(expectedStatusCode)
//				.when()
//				.post("/gists")
//				.then()
//				.body(JsonSchemaValidator.matchesJsonSchemaInClasspath("postGistSchema.json"))
//				.extract().jsonPath();
//
//		return response;
//	}

	public void postGist(PostGistDto.GistDetails gist) throws JsonProcessingException {
		ObjectMapper jacksonObjectMapper = new ObjectMapper();
		String bodyString = jacksonObjectMapper.writeValueAsString(gist);
		RestAssured.given()
				.header("Authorization", "Bearer " + token)
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
				.header("Authorization", "Bearer " + token)
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
