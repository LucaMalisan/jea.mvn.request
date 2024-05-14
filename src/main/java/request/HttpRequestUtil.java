package request;

import header.HeaderFields;
import lombok.extern.java.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;

@Log
public class HttpRequestUtil {

    private HttpRequestUtil() {
    }

    public static String createHttpRequestAndGetResponse(String urlStr, String method, String authorization, String data) {

        String base64AuthorizationHeader = HeaderFields.BASIC_PREFIX + Base64.getEncoder().encodeToString(authorization.getBytes());

        try {
            URL url = new URL(urlStr);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            //header
            con.setRequestMethod(method);
            con.setRequestProperty(HeaderFields.AUTHORIZATION, base64AuthorizationHeader);

            //payload
            if (data != null && !data.isEmpty()) {
                createBody(con, data);
            }

            //open connection
            con.setConnectTimeout(5000);
            con.setReadTimeout(5000);

            return stringifyResponse(con);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Sets the payload
     */

    protected static void createBody(HttpURLConnection con, String data) throws IOException {
        con.setDoOutput(true);
        OutputStream os = con.getOutputStream();
        os.write(data.getBytes());
        os.flush();
        os.close();
    }

    /**
     * Reads the response and stringifies it
     */

    protected static String stringifyResponse(HttpURLConnection con) throws IOException {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            con.disconnect();

            if (con.getResponseCode() >= 300) {
                log.severe("Request returned the following status: " + con.getResponseMessage());
            }

            return content.toString();
        }
    }
}
