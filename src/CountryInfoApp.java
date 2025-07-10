package src;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

import org.json.JSONArray;
import org.json.JSONObject;

public class CountryInfoApp {
    public static void main(String[] args) {
        // ここで国名を直接指定してください（日本語）
        // 例: "日本", "アメリカ合衆国", "ハンガリー"

        fetchWikipediaSummary();
    }

    private static void fetchWikipediaSummary() {
        try {
            String countryName = "イギリス";
            String encodedName = URLEncoder.encode(countryName, StandardCharsets.UTF_8);
            String url = "https://ja.wikipedia.org/api/rest_v1/page/summary/" + encodedName;

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .header("User-Agent", "Java WikipediaCultureApp/1.0")
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                System.err.println("Wikipedia API エラー: HTTP " + response.statusCode());
                return;
            }

            JSONObject json = new JSONObject(response.body());

            String title = json.optString("title", "不明");
            String extract = json.optString("extract", "概要が見つかりませんでした。");

            System.out.println("\n=== " + title + " の文化に関する概要 ===");
            System.out.println(extract);
            fetchWikidataLanguageReligion(countryName);

        } catch (Exception e) {
            System.err.println("Wikipedia概要取得中にエラーが発生しました。");
            e.printStackTrace();

        }
    }

    private static void fetchWikidataLanguageReligion(String countryName) {
        try {
            String sparql = "SELECT ?languageLabel ?religionLabel WHERE {\n" +
                    "  ?country wdt:P31 wd:Q6256;  # 国\n" +
                    "           rdfs:label \"" + countryName + "\"@ja.\n" +
                    "  OPTIONAL { ?country wdt:P37 ?language. }\n" +
                    "  OPTIONAL { ?country wdt:P140 ?religion. }\n" +
                    "  SERVICE wikibase:label { bd:serviceParam wikibase:language \"ja\". }\n" +
                    "}";

            String url = "https://query.wikidata.org/sparql?query=" +
                    URLEncoder.encode(sparql, StandardCharsets.UTF_8) + "&format=json";

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .header("User-Agent", "Java WikidataClient/1.0")
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                System.err.println("Wikidata API エラー: HTTP " + response.statusCode());
                return;
            }

            JSONObject json = new JSONObject(response.body());
            JSONArray results = json.getJSONObject("results").getJSONArray("bindings");

            if (results.length() == 0) {
                System.out.println("\nWikidataで言語・宗教情報が見つかりませんでした。");
                return;
            }

            System.out.println("\n=== " + countryName + " の言語と宗教 ===");
            for (int i = 0; i < results.length(); i++) {
                JSONObject entry = results.getJSONObject(i);

                String language = entry.has("languageLabel")
                        ? entry.getJSONObject("languageLabel").getString("value")
                        : "不明";

                String religion = entry.has("religionLabel")
                        ? entry.getJSONObject("religionLabel").getString("value")
                        : "不明";

                System.out.printf("言語: %s, 宗教: %s%n", language, religion);
            }

        } catch (Exception e) {
            System.err.println("Wikidata取得中にエラーが発生しました。");
            e.printStackTrace();
        }
    }
}
