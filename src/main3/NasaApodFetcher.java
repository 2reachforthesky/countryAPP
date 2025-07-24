package src.main3;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.json.JSONObject;

public class NasaApodFetcher {

    // 翻訳メソッド（MyMemory APIを使用）
    // このメソッドはprivate staticとしてクラス内にカプセル化
    private static String translateToJapanese(String text) throws Exception {
        int maxLength = 500; // MyMemoryの最大長
        StringBuilder fullTranslation = new StringBuilder();

        for (int i = 0; i < text.length(); i += maxLength) {
            int end = Math.min(i + maxLength, text.length());
            String part = text.substring(i, end);
            String encodedText = URLEncoder.encode(part, StandardCharsets.UTF_8);
            String langpair = URLEncoder.encode("en|ja", StandardCharsets.UTF_8);
            String apiUrl = "https://api.mymemory.translated.net/get?q=" + encodedText + "&langpair=" + langpair;

            // new URI(apiUrl).toURL()で非推奨警告を回避
            URL url = new URI(apiUrl).toURL();
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

    /**
     * NASAの今日の宇宙画像（APOD）を取得し、その情報（タイトル、画像URL、説明の日本語訳）を表示します。
     * APIキーはコード内に直接記述されています。
     */
    public static void fetchAndDisplayApod() { // 関数化
        try {
            // NASAのAPOD APIを呼び出し
            String apiKey = "BRgMJDpDn6cMLfcaDZAtyaTr5tXO1hA0zVsX6iI8"; // ご自身のAPIキーに置き換えてください
            String apiUrl = "https://api.nasa.gov/planetary/apod?api_key=" + apiKey;

            HttpURLConnection conn = (HttpURLConnection) new URI(apiUrl).toURL().openConnection();
            conn.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8)); // UTF-8指定を追加
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
            System.out.println("NASA APODの取得または翻訳中にエラーが発生しました。");
            e.printStackTrace();
        }
    }

    // テスト用のmainメソッド (必要であれば残してください)
    public static void main(String[] args) {
        fetchAndDisplayApod();
    }
}