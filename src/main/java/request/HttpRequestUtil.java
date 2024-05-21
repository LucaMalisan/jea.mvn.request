package request;

import lombok.extern.java.Log;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.util.Map;

@Log
public class HttpRequestUtil {

    private HttpRequestUtil() {
    }

    public static String createHttpRequestAndGetResponse(String urlStr, String method, String data, Map<String, String> headers) {


        String base64AuthorizationHeader = "";

        if (headers.get(HeaderFields.AUTHORIZATION) != null) {
            base64AuthorizationHeader = HeaderFields.BASIC_PREFIX + Base64.getEncoder().encodeToString(headers.get(HeaderFields.AUTHORIZATION).getBytes());
        }

        try {
            URL url = new URL(urlStr);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            //header
            con.setRequestMethod(method);
            con.setRequestProperty(HeaderFields.AUTHORIZATION, base64AuthorizationHeader);

            for (Map.Entry<String, String> header : headers.entrySet()) {
                con.setRequestProperty(header.getKey(), header.getValue());
            }

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

    protected static String stringifyResponse(HttpURLConnection con) {
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
        } catch (FileNotFoundException e) {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getErrorStream()));
                String inputLine;
                StringBuilder content = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
                con.disconnect();
                log.severe(content.toString());
            } catch (IOException ex) {
                log.severe("Got an IOException: " + ex.getMessage());
            }
        } catch (IOException e) {
            log.severe("Got an IOException: " + e.getMessage());
        }
        return null;
    }
}
