package src;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import org.json.JSONObject;

public class MyMemoryTranslateExample {

    public static void printTranslation(String text, String sourceLang, String targetLang) {
        try {
            String translatedText = translate(text, sourceLang, targetLang);
            System.out.println(translatedText);
        } catch (Exception e) {
            System.out.println("翻訳中にエラーが発生しました。");
            e.printStackTrace();
        }
    }

    public static String translate(String text, String sourceLang, String targetLang) throws Exception {
        // textをURLエンコード
        String encodedText = URLEncoder.encode(text, StandardCharsets.UTF_8);
        // langpairの区切り文字 | を %7C にエンコード
        String langpair = sourceLang + "|" + targetLang;
        String encodedLangpair = URLEncoder.encode(langpair, StandardCharsets.UTF_8);

        String urlStr = "https://api.mymemory.translated.net/get?q=" + encodedText + "&langpair=" + encodedLangpair;

        // URIを作成（Java20非推奨警告回避）
        URI uri = new URI(urlStr);
        URL url = uri.toURL();

        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Accept", "application/json");

        int responseCode = con.getResponseCode();
        if (responseCode != 200) {
            throw new RuntimeException("HTTPエラーコード: " + responseCode);
        }

        try (BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line);
            }

            JSONObject jsonResponse = new JSONObject(response.toString());
            JSONObject responseData = jsonResponse.getJSONObject("responseData");
            return responseData.getString("translatedText");
        }
    }
}