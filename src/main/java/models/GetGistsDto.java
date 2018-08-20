package models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;

public class GetGistsDto {

	@AllArgsConstructor
	@NoArgsConstructor
	@Data
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class GistDetails {
		private String url;
		private String id;
		private HashMap<String, FileContent> files;
	}

	@AllArgsConstructor
	@NoArgsConstructor
	@Data
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class FileContent {
		private String filename;
		private String content;
	}

	@AllArgsConstructor
	@NoArgsConstructor
	@Data
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class GetGistResponse {
		private String url;
		private String id;
		private HashMap<String, FileContent> files;
	}

}
