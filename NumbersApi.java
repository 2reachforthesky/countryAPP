import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class NumbersApi {
    public static void main(String[] args) {
        // 取得したい数字や日付をここで指定
        String number = "42"; // 数字のトリビア
        // String date = "7/15"; // 日付のトリビア（月/日）

        // URLの組み立て（数字の場合）
        String url = "http://numbersapi.com/" + number;

        // 日付のトリビアを取得したい場合はこちらを使う
        // String url = "http://numbersapi.com/" + date + "/date";

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                String trivia = response.body();
                String triviaJa = translateToJapanese(trivia);
                System.out.println("Numbers APIのトリビア:");
                System.out.println(trivia);
                System.out.println("（日本語訳）:");
                System.out.println(triviaJa);
            } else {
                System.out.println("API呼び出しに失敗しました。ステータスコード: " + response.statusCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Google翻訳API（非公式）で英語→日本語
    private static String translateToJapanese(String text) {
        try {
            String encodedText = java.net.URLEncoder.encode(text, "UTF-8");
            String url = "https://translate.googleapis.com/translate_a/single?client=gtx&sl=en&tl=ja&dt=t&q="
                    + encodedText;
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            org.json.JSONArray transArray = new org.json.JSONArray(response.body());
            return transArray.getJSONArray(0).getJSONArray(0).getString(0);
        } catch (Exception e) {
            return "(翻訳失敗)";
        }
    }
}
