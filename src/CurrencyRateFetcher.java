package src;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.json.JSONObject;

public class CurrencyRateFetcher {

    public static String getRate(String from, String to, String apiKey) {
        try {
            from = from.trim().toUpperCase();
            to = to.trim().toUpperCase();

            String apiUrl = buildApiUrl(from, to);
            String response = getApiResponse(apiUrl, apiKey);
            return parseAndFormat(response, from, to);

        } catch (Exception e) {
            return "エラー: 通貨レート取得例外 (" + e.getMessage() + ")";
        }
    }

    /** API URLを構築 */
    private static String buildApiUrl(String from, String to) {
        return "https://api.apilayer.com/exchangerates_data/convert?from=" + from + "&to=" + to + "&amount=1";
    }

    /** APIレスポンスを取得する */
    private static String getApiResponse(String apiUrl, String apiKey) throws Exception {
        URL url = new URI(apiUrl).toURL();
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("apikey", apiKey);

        int responseCode = conn.getResponseCode();
        if (responseCode != 200) {
            throw new RuntimeException("API呼び出し失敗 (HTTP " + responseCode + ")");
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null)
                sb.append(line);
            return sb.toString();
        }
    }

    /** レスポンスをパースして整形済み文字列を返す */
    private static String parseAndFormat(String jsonText, String from, String to) {
        JSONObject json = new JSONObject(jsonText);

        if (!json.has("result") || json.isNull("result")) {
            return "エラー: 通貨レートが取得できませんでした。";
        }

        double rate = json.getDouble("result");
        String date = json.optString("date", "日付不明");
        String formattedDate = formatDate(date);

        return String.format(
                "──────────── 通貨換算結果 ────────────%n"
                        + "・日付　　　: %s%n"
                        + "・通貨　　　: %s → %s%n"
                        + "・換算レート: 1 %s = %.4f %s%n"
                        + "──────────────────────────────────",
                formattedDate, from, to, from, rate, to);
    }

    /** 日付を「yyyy年MM月dd日」に整形する */
    private static String formatDate(String dateString) {
        try {
            LocalDate parsedDate = LocalDate.parse(dateString);
            return parsedDate.format(DateTimeFormatter.ofPattern("yyyy年MM月dd日"));
        } catch (Exception e) {
            return dateString; // パース失敗時はそのまま返す
        }
    }
}