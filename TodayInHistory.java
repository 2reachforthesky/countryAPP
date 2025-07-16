import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import java.time.LocalDate;

public class TodayInHistory {
    public static void main(String[] args) {
        // 今日の日付を取得
        LocalDate today = LocalDate.now();
        String month = String.valueOf(today.getMonthValue());
        String day = String.valueOf(today.getDayOfMonth());
        String url = "https://history.muffinlabs.com/date/" + month + "/" + day;
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                JSONObject json = new JSONObject(response.body());
                JSONObject data = json.getJSONObject("data");
                JSONArray events = data.getJSONArray("Events");
                System.out.println("📜 " + month + "/" + day + "の歴史上の出来事:");
                // 出来事（Events）を最大5件表示
                for (int i = 0; i < Math.min(events.length(), 5); i++) {
                    JSONObject event = events.getJSONObject(i);
                    String eventText = event.getString("text");
                    String eventJa = translateToJapanese(eventText);
                    System.out.println(" - [" + event.getString("year") + "] " + eventJa);
                }
            } else {
                System.out.println("❌ Error: HTTP Status " + response.statusCode());
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
