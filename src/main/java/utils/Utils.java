package utils;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.net.URL;

public class Utils {

	public String readFromFile(String fileName) throws IOException, ParseException {
		URL url = getClass().getClassLoader().getResource(fileName);
		JSONParser parser = new JSONParser();
		Object obj = parser.parse(new FileReader(url.getPath()));
		JSONObject jsonObject = (JSONObject) obj;
		return jsonObject.toString();
	}

}
