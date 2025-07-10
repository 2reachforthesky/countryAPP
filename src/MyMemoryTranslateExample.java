package src;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.net.URL;
import org.json.JSONObject;

public class MyMemoryTranslateExample {

    public static void main(String[] args) {
        String textToTranslate = "The United States of America (USA) is a large country located in North America. It is made up of 50 states and a federal district called Washington, D.C., which is the capital city. The USA is known for its diverse culture, economy, and geography. It has a population of over 330 million people, making it one of the most populous countries in the world.";
        printTranslation(textToTranslate, "en", "ja");
    }

    public static void printTranslation(String text, String sourceLang, String targetLang) {
        try {
            String translatedText = translate(text, sourceLang, targetLang);
            System.out.println(": " + translatedText);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String translate(String text, String sourceLang, String targetLang) throws Exception {
        // URLエンコード
        String encodedText = URLEncoder.encode(text, "UTF-8");
        String urlStr = "https://api.mymemory.translated.net/get?q=" + encodedText + "&langpair=" + sourceLang + "|"
                + targetLang;

        URL url = new URL(urlStr);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Accept", "application/json");

        int responseCode = con.getResponseCode();
        if (responseCode != 200) {
            throw new RuntimeException("HTTPエラーコード: " + responseCode);
        }

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = in.readLine()) != null) {
            response.append(line);
        }
        in.close();

        // JSON解析
        JSONObject jsonResponse = new JSONObject(response.toString());
        JSONObject responseData = jsonResponse.getJSONObject("responseData");
        String translatedText = responseData.getString("translatedText");

        return translatedText;
    }
}