package src.main3;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import java.time.LocalDate; // LocalDate をインポート

public class TodayInHistoryFetcher { // クラス名を TodayInHistory から TodayInHistoryFetcher に変更

    /**
     * 今日の日付の歴史上の出来事（最大5件）を取得し、日本語に翻訳して表示します。
     * History.muffinlabs.com APIを使用します。
     */
    public static void displayTodayInHistory() { // 関数化
        // 今日の日付を取得
        LocalDate today = LocalDate.now();
        String month = String.valueOf(today.getMonthValue());
        String day = String.valueOf(today.getDayOfMonth());
        String url = "https://history.muffinlabs.com/date/" + month + "/" + day;

        System.out.println("\n--- 今日の歴史上の出来事 ---");

        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JSONObject json = new JSONObject(response.body());
                // APIレスポンスの構造を確認し、"data"が存在するかチェック
                if (json.has("data")) {
                    JSONObject data = json.getJSONObject("data");
                    if (data.has("Events")) {
                        JSONArray events = data.getJSONArray("Events");

                        System.out.println("📜 " + month + "月" + day + "日の歴史:"); // 日本語表記に修正

                        // 出来事（Events）を最大5件表示
                        for (int i = 0; i < Math.min(events.length(), 5); i++) {
                            JSONObject event = events.getJSONObject(i);
                            String eventText = event.getString("text");
                            String eventJa = translateToJapanese(eventText); // Google翻訳APIは非公式のため、動作保証はありません
                            System.out.println(" - [" + event.getString("year") + "] " + eventJa);
                        }
                    } else {
                        System.out.println("この日の歴史的出来事が見つかりませんでした。");
                    }
                } else {
                    System.out.println("APIレスポンスに'data'フィールドがありませんでした。");
                }
            } else {
                System.out.println("❌ エラー: HTTPステータス " + response.statusCode());
                System.out.println("歴史データにアクセスできませんでした。");
            }
        } catch (Exception e) {
            System.out.println("歴史データを取得または処理中にエラーが発生しました。");
            e.printStackTrace();
        }
    }

    /**
     * Google翻訳API（非公式）を使用して英語のテキストを日本語に翻訳します。
     * このAPIは非公式であり、将来的に利用できなくなる可能性があります。
     *
     * @param text 翻訳する英語のテキスト
     * @return 翻訳された日本語のテキスト、または翻訳失敗時には "(翻訳失敗)"
     */
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
            // レスポンスがJSON配列であることを確認
            JSONArray transArray = new JSONArray(response.body());
            // 翻訳されたテキストは通常、[0][0][0] に含まれる
            return transArray.getJSONArray(0).getJSONArray(0).getString(0);
        } catch (Exception e) {
            System.err.println("翻訳中にエラーが発生しました: " + e.getMessage());
            return "(翻訳失敗)";
        }
    }

    // テスト用のmainメソッド (必要であれば残してください)
    public static void main(String[] args) {
        displayTodayInHistory();
    }
}