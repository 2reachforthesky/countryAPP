package src;

import java.net.URI;
import java.net.URL;
import java.net.HttpURLConnection;
import java.io.InputStreamReader;
import java.io.BufferedReader;

import org.json.JSONArray;
import org.json.JSONObject;

public class CountryInfoFetcher {

    /* ======= Public API ======= */
    /** ユーザー呼び出し口。国名を渡すと整形済み情報を返す */
    public static String getCountryInfo(String country) throws Exception {
        // 1) URL生成 → 2) HTTP GET → 3) JSONパース → 4) 整形
        String url = buildRequestUrl(country);
        String rawJson = fetchJson(url);
        JSONObject firstCountry = extractFirstCountry(rawJson);
        return formatCountryInfo(firstCountry);
    }

    /* ======= Step 1. URL 組み立て ======= */
    private static String buildRequestUrl(String country) throws Exception {
        // REST Countries API は国名に空白を含む場合でも URI エンコード不要
        return "https://restcountries.com/v3.1/name/" + country.trim();
    }

    /* ======= Step 2. HTTP GET でJSON取得 ======= */
    private static String fetchJson(String urlStr) throws Exception {
        URL url = new URI(urlStr).toURL();
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null)
                sb.append(line);
            return sb.toString();
        }
    }

    /* ======= Step 3. 先頭の国オブジェクトを取り出す ======= */
    private static JSONObject extractFirstCountry(String json) {
        JSONArray arr = new JSONArray(json);
        return arr.getJSONObject(0); // 最初の一致のみ使用
    }

    /* ======= Step 4. 出力整形 ======= */
    private static String formatCountryInfo(JSONObject obj) {
        // 共通情報
        String commonName = obj.getJSONObject("name").getString("common");
        String officialName = obj.getJSONObject("name").getString("official");
        String capital = obj.getJSONArray("capital").getString(0);
        String region = obj.getString("region");
        String subregion = obj.getString("subregion");
        int population = obj.getInt("population");

        // 通貨 (最初の1件だけ)
        String[] cur = parseFirstCurrency(obj.getJSONObject("currencies"));
        String currencyName = cur[0];
        String currencySymbol = cur[1];

        // 整形して返却
        return String.format(
                "国名: %s (%s)%n首都: %s%n地域: %s / %s%n通貨: %s (%s)%n人口: %,d人",
                commonName, officialName, capital, region, subregion,
                currencyName, currencySymbol, population);
    }

    /* ======= 補助: 通貨名・記号を最初の1件だけ取り出す ======= */
    private static String[] parseFirstCurrency(JSONObject currencies) {
        for (String code : currencies.keySet()) {
            JSONObject cur = currencies.getJSONObject(code);
            return new String[] { cur.getString("name"), cur.getString("symbol") };
        }
        return new String[] { "不明", "-" }; // 万一通貨情報が無い場合
    }
}