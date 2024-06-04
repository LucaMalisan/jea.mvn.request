package json;

import jakarta.json.*;

import java.io.StringReader;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class JsonUtil {

    public static JsonArray getJsonArray(String content, String key) {
        JsonReader reader = Json.createReader(new StringReader(content));
        JsonObject jsonObject = reader.readObject();
        JsonObject embedded = jsonObject.getJsonObject("_embedded");

        return embedded.getJsonArray(key);
    }

    public static String jsonValueToString(JsonValue value, String property) {
        return value.asJsonObject().get(property).toString().replace("\"", "");
    }

    public static Map<String, String> jsonStringToMap(String jsonStr) {
        return Arrays.stream(jsonStr.split(","))
                .map(e -> e.replaceAll("[{}\"]", ""))
                .map(e -> e.split(":"))
                .collect(Collectors.toMap(e -> e[0].trim(), e -> e[1].trim()));
    }
}
