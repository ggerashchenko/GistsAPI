import client.GistsApiClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import models.GetGistsDto;
import models.PostGistDto;
import models.UpdateGistDto;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import static io.restassured.RestAssured.given;


public class TestGist {
	private GistsApiClient client = new GistsApiClient();

	@Test
	@DisplayName("Verify that it's possible to get list of available gists")
	public void testGetGists() throws IOException {
		GetGistsDto.GistDetails[] parsedGists = client.getParsedGists();

		for (GetGistsDto.GistDetails gist : parsedGists) {
			Assert.assertTrue(Objects.nonNull(gist.getId()));
			Assert.assertTrue(StringUtils.isNoneEmpty(gist.getId()));
		}
	}

	@Test
	@DisplayName("Verify that it's possible make request without authentication")
	public void testGetGistsNoAuthHeader() {
		given()
				.header("Authorization", "Bearer ")
				.contentType("application/json")
				.body("")
				.expect()
				.statusCode(401)
				.when()
				.post("/gists");
	}

	@Test
	@DisplayName("Verify that it's possible to post gists")
	public void testPostGist() throws IOException {
		HashMap<String, PostGistDto.FileContent> inputFiles = new HashMap<>();
		for (int i = 0; i < 3; i++) {
			inputFiles.put(UUID.randomUUID().toString(), new PostGistDto.FileContent(UUID.randomUUID().toString()));
		}

		PostGistDto.GistDetails postGistsRequest = PostGistDto.GistDetails.builder()
				.description("bla")
				.isPublic(true)
				.files(inputFiles)
				.build();

		Response response = client.postGistWithResponse(postGistsRequest);
		Assert.assertEquals(201, response.getStatusCode());

		ObjectMapper objectMapper = new ObjectMapper();
		PostGistDto.PostGistResponse postGistResponse = objectMapper.readValue(response.getBody().print(), PostGistDto.PostGistResponse.class);

		HashMap<String, PostGistDto.FileContent> responseFiles = postGistResponse.getFiles();

		responseFiles.forEach((String responseFilesName, PostGistDto.FileContent responseFile) -> {
			Assert.assertTrue(inputFiles.containsKey(responseFilesName));
			PostGistDto.FileContent inputFile = inputFiles.get(responseFilesName);
			Assert.assertEquals(inputFile.getContent(), responseFile.getContent());
		});
	}

//	@Test
//	@DisplayName("Test POST gist")
//	public void testPostGist() throws IOException, ParseException {
//
//		JsonPath postResp = client.postGist("postGist.json", 201);
//
//		Assert.assertThat(postResp.get().toString(), containsString("id"));
//		Assert.assertTrue(postResp.getMap("files").size() == 4);
//		Assert.assertTrue("Response does not have file",
//				postResp.getMap("files").containsKey("hello_world_ruby.txt"));
//		Assert.assertTrue("Response does not have file",
//				postResp.getMap("files").containsKey("hello_world.rb"));
//		Assert.assertTrue("Response does not have file",
//				postResp.getMap("files").containsKey("hello_world_python.txt"));
//		Assert.assertTrue("Response does not have file",
//				postResp.getMap("files").containsKey("hello_world_ruby.txt"));
//
////		JsonPath getResp = client.getGists();
////		String id = postResp.get("id").toString();
////		Assert.assertTrue(getResp.getList("id").contains(id));
//	}

	@Test
	@DisplayName("Verify that it's possible to get gist by id")
	public void testGetGistById() throws IOException {

		HashMap<String, PostGistDto.FileContent> inputFiles = new HashMap<>();
		for (int i = 0; i < 3; i++) {
			inputFiles.put(UUID.randomUUID().toString(), new PostGistDto.FileContent(UUID.randomUUID().toString()));
		}

		PostGistDto.GistDetails postGistsRequest = PostGistDto.GistDetails.builder()
				.description("bla")
				.isPublic(true)
				.files(inputFiles)
				.build();

		Response postResponse = client.postGistWithResponse(postGistsRequest);
		Assert.assertEquals(201, postResponse.getStatusCode());

		ObjectMapper objectMapper = new ObjectMapper();
		PostGistDto.PostGistResponse postGistResponse = objectMapper.readValue(postResponse.getBody().print(), PostGistDto.PostGistResponse.class);

		Response getResponse = client.getGistById(postGistResponse.getId());
		GetGistsDto.GetGistResponse getGistResponse = objectMapper.readValue(getResponse.getBody().print(), GetGistsDto.GetGistResponse.class);

		HashMap<String, GetGistsDto.FileContent> responseFiles = getGistResponse.getFiles();

		Assert.assertEquals(200, getResponse.getStatusCode());

		responseFiles.forEach((String responseFilesName, GetGistsDto.FileContent responseFile) -> {
			Assert.assertTrue(inputFiles.containsKey(responseFilesName));
			PostGistDto.FileContent inputFile = inputFiles.get(responseFilesName);
			Assert.assertEquals(inputFile.getContent(), responseFile.getContent());
		});
	}

	@Test
	@DisplayName("Verify that it's not possible to get gist by not existing id")
	public void testGetNotExistingGistById() {
		Response getResponse = client.getGistById("doesNotExist");
		Assert.assertEquals(404, getResponse.getStatusCode());
	}

	@Test
	@DisplayName("Verify that it's possible to update gist by id")
	public void testUpdateGist() throws IOException {
		HashMap<String, PostGistDto.FileContent> inputFiles = new HashMap<>();
		for (int i = 0; i < 3; i++) {
			inputFiles.put(UUID.randomUUID().toString(), new PostGistDto.FileContent(UUID.randomUUID().toString()));
		}

		HashMap<String, PostGistDto.FileContent> initialInput = (HashMap<String, PostGistDto.FileContent>)inputFiles;

		PostGistDto.GistDetails postGistsRequest = PostGistDto.GistDetails.builder()
				.description("bla")
				.isPublic(true)
				.files(inputFiles)
				.build();

		Response postResponse = client.postGistWithResponse(postGistsRequest);
		Assert.assertEquals(201, postResponse.getStatusCode());
		ObjectMapper objectMapper = new ObjectMapper();
		PostGistDto.PostGistResponse postGistResponse = objectMapper.readValue(postResponse.getBody().print(), PostGistDto.PostGistResponse.class);

		String id = postGistResponse.getId();

		String  key = inputFiles.keySet().toArray()[0].toString();
		inputFiles.remove(key);


		PostGistDto.GistDetails updateGistsRequest = UpdateGistDto.GistDetails.builder()
				.description("bla")
				.isPublic(true)
				.files(inputFiles)
				.build();

		Response updateResponse = client.updateGist(updateGistsRequest, id);
		UpdateGistDto.UpdateGistResponse updateGistResponse = objectMapper.readValue(updateResponse.getBody().print(), UpdateGistDto.UpdateGistResponse.class);

		HashMap<String, UpdateGistDto.FileContent> responseFiles = updateGistResponse.getFiles();


		responseFiles.forEach((String responseFilesName, UpdateGistDto.FileContent responseFile) -> {
			Assert.assertTrue(updatedFiles.containsKey(responseFilesName));
			UpdateGistDto.FileContent inputFile = updatedFiles.get(responseFilesName);
			Assert.assertEquals(inputFile.getContent(), responseFile.getContent());
		});


//		JsonPath postResp = client.postGist("postGist.json", 201);
//		Assert.assertTrue(postResp.getMap("files").size() == 4);
//		String id = postResp.get("id").toString();
//		JsonPath updateResp = client.updateGist(id, "updateGist.json", 201);
//		Assert.assertTrue(updateResp.getMap("files").size() == 2);
//		Assert.assertTrue("Response does not have file",
//				updateResp.getMap("files").containsKey("hello_world.py"));
//		Assert.assertTrue("Response does not have file",
//				updateResp.getMap("files").containsKey("hello_world.rb"));
//		Assert.assertFalse("Response does not have file",
//				updateResp.getMap("files").containsKey("hello_world_python.txt"));
//		Assert.assertFalse("Response does not have file",
//				updateResp.getMap("files").containsKey("hello_world_ruby.txt"));

	}

	@Test
	@DisplayName("TVerify that it's  possible to delete gist by id")
	public void testDeleteGists() throws IOException {
		HashMap<String, PostGistDto.FileContent> inputFiles = new HashMap<>();
		for (int i = 0; i < 3; i++) {
			inputFiles.put(UUID.randomUUID().toString(), new PostGistDto.FileContent(UUID.randomUUID().toString()));
		}

		PostGistDto.GistDetails postGistsRequest = PostGistDto.GistDetails.builder()
				.description("bla")
				.isPublic(true)
				.files(inputFiles)
				.build();

		Response postResponse = client.postGistWithResponse(postGistsRequest);
		Assert.assertEquals(201, postResponse.getStatusCode());
		ObjectMapper objectMapper = new ObjectMapper();
		PostGistDto.PostGistResponse postGistResponse = objectMapper.readValue(postResponse.getBody().print(), PostGistDto.PostGistResponse.class);

		String id = postGistResponse.getId();

		Response deleteResponse = client.deleteGist(id);

		Assert.assertEquals(204, deleteResponse.getStatusCode());
		Assert.assertTrue(deleteResponse.body().print().isEmpty());

		Response getGists = client.getGists();
		Assert.assertFalse(getGists.getBody().print().contains(id));
	}

	@Test
	@DisplayName("Verify that it's not possible to delete gist by not existing id")
	public void testDeleteNotExistingGists() {
		Response deleteResponse = client.deleteGist("doesNotExists");
		Assert.assertEquals(404, deleteResponse.getStatusCode());
	}
}
