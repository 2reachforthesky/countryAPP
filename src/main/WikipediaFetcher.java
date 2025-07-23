package src.main;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.json.JSONObject;

public class WikipediaFetcher {
    public static String getSummary(String title) throws Exception {
        // タイトルをURLエンコード（日本語・空白対応）
        String encodedTitle = URLEncoder.encode(title, StandardCharsets.UTF_8.toString());
        String apiURL = "https://ja.wikipedia.org/api/rest_v1/page/summary/" + encodedTitle;

        URI uri = new URI(apiURL);
        URL url = uri.toURL();

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        int responseCode = conn.getResponseCode();
        if (responseCode != 200) {
            return "概要情報が取得できませんでした。（HTTPエラーコード: " + responseCode + "）";
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
            JSONObject json = new JSONObject(result.toString());
            if (json.has("extract")) {
                return json.getString("extract");
            } else {
                return "概要情報が取得できませんでした。";
            }
        }
    }
}