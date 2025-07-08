package src;

import org.json.JSONArray;
import org.json.JSONObject;

public class CountryInfoParser {

    /** JSON文字列から主要な国情報を抜き出し、整形して返す */
    public static String parse(String json) {
        JSONObject country = extractFirstCountry(json);

        String name = extractCommonName(country);
        String capital = extractCapital(country);
        int population = extractPopulation(country);

        return String.format("国名: %s%n首都: %s%n人口: %,d人", name, capital, population);
    }

    /** JSON配列から最初の国オブジェクトを取り出す */
    private static JSONObject extractFirstCountry(String json) {
        JSONArray arr = new JSONArray(json);
        return arr.getJSONObject(0);
    }

    /** 共通国名（例: Japan）を取得 */
    private static String extractCommonName(JSONObject country) {
        return country.getJSONObject("name").getString("common");
    }

    /** 首都名を取得 */
    private static String extractCapital(JSONObject country) {
        JSONArray capitals = country.optJSONArray("capital");
        return (capitals != null && capitals.length() > 0) ? capitals.getString(0) : "不明";
    }

    /** 人口を取得 */
    private static int extractPopulation(JSONObject country) {
        return country.optInt("population", -1);
    }
}