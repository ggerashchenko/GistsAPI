package models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;

public class PostGistDto {

	@AllArgsConstructor
	@NoArgsConstructor
	@Data
	@JsonIgnoreProperties(ignoreUnknown = true)
	@Builder
	public static class GistDetails {
		private String description;
		@JsonProperty("public")
		private boolean isPublic;
		private HashMap<String, FileDto> files;
	}
}



