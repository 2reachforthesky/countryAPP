package src.main;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONObject;

public class CountryInfoApp {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter country name in English (e.g., Germany): ");
        String countryName = scanner.nextLine().trim();
        scanner.close();

        fetchWikidataLanguageReligion(countryName);
    }

    public static void fetchWikidataLanguageReligion(String countryName) {
        // 入力を1文字目だけ大文字、残り小文字に変換（例: germany → Germany）
        if (countryName != null && !countryName.isEmpty()) {
            countryName = countryName.substring(0, 1).toUpperCase() + countryName.substring(1).toLowerCase();
        }
        try {
            String sparql = "SELECT ?languageLabel ?religionLabel WHERE {\n" +
                    "  ?country wdt:P31 wd:Q6256;  # 国\n" +
                    "           rdfs:label \"" + countryName + "\"@en.\n" +
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
