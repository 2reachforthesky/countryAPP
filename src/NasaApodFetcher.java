package src;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.json.JSONObject;

public class NasaApodFetcher {
    public static void main(String[] args) {
        try {
            // NASAのAPOD APIを呼び出し
            String apiKey = "BRgMJDpDn6cMLfcaDZAtyaTr5tXO1hA0zVsX6iI8";
            String apiUrl = "https://api.nasa.gov/planetary/apod?api_key=" + apiKey;

            HttpURLConnection conn = (HttpURLConnection) new URL(apiUrl).openConnection();
            conn.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder responseBuffer = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                responseBuffer.append(line);
            }
            reader.close();

            JSONObject json = new JSONObject(responseBuffer.toString());
            String title = json.getString("title");
            String imageUrl = json.getString("url");
            String explanation = json.getString("explanation");

            // 翻訳処理
            String translatedExplanation = translateToJapanese(explanation);

            // 表示
            System.out.println("=== 今日のNASA宇宙画像 ===");
            System.out.println("タイトル: " + title);
            System.out.println("画像URL: " + imageUrl);
            System.out.println("\n--- 英語の説明 ---");
            System.out.println(explanation);
            System.out.println("\n--- 日本語訳 ---");
            System.out.println(translatedExplanation);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 翻訳メソッド（MyMemory APIを使用）
    public static String translateToJapanese(String text) throws Exception {
        int maxLength = 500; // MyMemoryの最大長
        StringBuilder fullTranslation = new StringBuilder();

        for (int i = 0; i < text.length(); i += maxLength) {
            int end = Math.min(i + maxLength, text.length());
            String part = text.substring(i, end);
            String encodedText = URLEncoder.encode(part, StandardCharsets.UTF_8);
            String langpair = "en|ja";
            String apiUrl = "https://api.mymemory.translated.net/get?q=" + encodedText + "&langpair=" + langpair;

            // URLは URI 経由せず直接 HttpURLConnection に渡すことで安全に処理
            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder responseBuffer = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    responseBuffer.append(line);
                }

                JSONObject json = new JSONObject(responseBuffer.toString());
                JSONObject responseData = json.getJSONObject("responseData");
                String translatedText = responseData.getString("translatedText");
                fullTranslation.append(translatedText).append("\n");
            }
        }

        return fullTranslation.toString();
    }
}