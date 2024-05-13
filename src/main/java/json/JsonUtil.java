package json;

import jakarta.json.*;

import java.io.StringReader;

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
}
