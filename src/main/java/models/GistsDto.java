package models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;

public class GistsDto {

	@AllArgsConstructor
	@NoArgsConstructor
	@Data
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class GistDetails {
		private String url;
		private String id;
		private HashMap<String, FileDto> files;
	}

}
