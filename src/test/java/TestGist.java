import client.GistsApiClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import models.FileDto;
import models.GistsDto;
import models.PostGistDto;
import org.apache.commons.lang3.StringUtils;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

import static io.restassured.RestAssured.given;

public class TestGist {
	private static GistsApiClient client = new GistsApiClient();

	@Test
	@DisplayName("Verify that it's possible to get list of available gists")
	public void testGetGists() throws IOException {
		HashMap<String, FileDto> inputFiles = generateFiles(3);
		PostGistDto.GistDetails postGistsRequest = PostGistDto.GistDetails.builder()
				.description("bla")
				.isPublic(true)
				.files(inputFiles)
				.build();

		client.postGist(postGistsRequest);
		GistsDto.GistDetails[] parsedGists = client.getParsedGists();

		for (GistsDto.GistDetails gist : parsedGists) {
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
		HashMap<String, FileDto> inputFiles = generateFiles(3);
		PostGistDto.GistDetails postGistsRequest = PostGistDto.GistDetails.builder()
				.description("bla")
				.isPublic(true)
				.files(inputFiles)
				.build();

		Response response = client.postGistWithResponse(postGistsRequest);
		Assert.assertEquals(201, response.getStatusCode());

		ObjectMapper objectMapper = new ObjectMapper();
		GistsDto.GistDetails postGistResponse = objectMapper.readValue(response.getBody().print(), GistsDto.GistDetails.class);

		HashMap<String, FileDto> responseFiles = postGistResponse.getFiles();

		assertFilesEquals(inputFiles, responseFiles);
	}

	@Test
	@DisplayName("Verify that it's possible to get gist by id")
	public void testGetGistById() throws IOException {
		HashMap<String, FileDto> inputFiles = generateFiles(3);
		PostGistDto.GistDetails postGistsRequest = PostGistDto.GistDetails.builder()
				.description("bla")
				.isPublic(true)
				.files(inputFiles)
				.build();

		Response postResponse = client.postGistWithResponse(postGistsRequest);
		Assert.assertEquals(201, postResponse.getStatusCode());

		ObjectMapper objectMapper = new ObjectMapper();
		GistsDto.GistDetails postGistResponse = objectMapper.readValue(postResponse.getBody().print(), GistsDto.GistDetails.class);

		Response getResponse = client.getGistById(postGistResponse.getId());
		GistsDto.GistDetails getGistResponse = objectMapper.readValue(getResponse.getBody().print(), GistsDto.GistDetails.class);

		HashMap<String, FileDto> responseFiles = getGistResponse.getFiles();

		Assert.assertEquals(200, getResponse.getStatusCode());
		assertFilesEquals(inputFiles, responseFiles);
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
		HashMap<String, FileDto> inputFiles = generateFiles(3);
		PostGistDto.GistDetails postGistsRequest = PostGistDto.GistDetails.builder()
				.description("bla")
				.isPublic(true)
				.files(inputFiles)
				.build();

		Response postResponse = client.postGistWithResponse(postGistsRequest);
		Assert.assertEquals(201, postResponse.getStatusCode());

		ObjectMapper objectMapper = new ObjectMapper();
		GistsDto.GistDetails postGistResponse = objectMapper.readValue(postResponse.getBody().print(), GistsDto.GistDetails.class);
		String id = postGistResponse.getId();

		String  key = inputFiles.keySet().toArray()[0].toString();
		inputFiles.put(key, null);

		PostGistDto.GistDetails updateGistsRequest = PostGistDto.GistDetails.builder()
				.description("bla")
				.isPublic(true)
				.files(inputFiles)
				.build();

		Response updateResponse = client.updateGist(updateGistsRequest, id);
		GistsDto.GistDetails updateGistResponse = objectMapper.readValue(updateResponse.getBody().print(), GistsDto.GistDetails.class);
		HashMap<String, FileDto> responseFiles = updateGistResponse.getFiles();

		assertFilesEquals(inputFiles, responseFiles);
	}

	@Test
	@DisplayName("TVerify that it's  possible to delete gist by id")
	public void testDeleteGists() throws IOException {
		HashMap<String, FileDto> inputFiles = generateFiles(3);
		PostGistDto.GistDetails postGistsRequest = PostGistDto.GistDetails.builder()
				.description("bla")
				.isPublic(true)
				.files(inputFiles)
				.build();

		Response postResponse = client.postGistWithResponse(postGistsRequest);
		Assert.assertEquals(201, postResponse.getStatusCode());
		ObjectMapper objectMapper = new ObjectMapper();
		GistsDto.GistDetails postGistResponse = objectMapper.readValue(postResponse.getBody().print(), GistsDto.GistDetails.class);

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

	@AfterClass
	public static void tearDown() throws IOException {
		GistsDto.GistDetails[] parsedGists = client.getParsedGists();
		Arrays.asList(parsedGists).forEach((GistsDto.GistDetails gistDetails) -> {
			client.deleteGist(gistDetails.getId());
		});
	}

	private HashMap<String, FileDto> generateFiles(int filesNumber) {
		HashMap<String, FileDto> inputFiles = new HashMap<>();
		for (int i = 0; i < filesNumber; i++) {
			inputFiles.put(UUID.randomUUID().toString(), new FileDto(UUID.randomUUID().toString()));
		}
		return inputFiles;
	}

	private void assertFilesEquals(HashMap<String, FileDto> expectedFiles, HashMap<String, FileDto> actualFiles) {
		actualFiles.forEach((String responseFilesName, FileDto responseFile) -> {
			Assert.assertTrue(expectedFiles.containsKey(responseFilesName));
			FileDto inputFile = expectedFiles.get(responseFilesName);
			Assert.assertEquals(inputFile.getContent(), responseFile.getContent());
		});
	}
}
