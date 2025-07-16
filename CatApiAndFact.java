import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.Scanner;
import org.json.JSONArray;
import org.json.JSONObject;

public class CatApiAndFact {
    // TheCatAPIのAPIキー
    private static final String API_KEY = "live_GIpXv5KR9IIoLzy4V2lhtbzVYbGWYNv5NhLiXG7pHDfewg2cwZj7vI2Hx8SqMShu";

    public static void main(String[] args) {
        // 国名→猫種IDの対応表（一部例。必要に応じて追加可能）
        Map<String, String> countryToBreedId = Map.ofEntries(
                Map.entry("日本", "jbt"), // Japanese Bobtail
                Map.entry("アメリカ", "ame"), // American Shorthair
                Map.entry("イギリス", "bsh"), // British Shorthair
                Map.entry("ロシア", "rus"), // Russian Blue
                Map.entry("エジプト", "emau"), // Egyptian Mau
                Map.entry("トルコ", "tvan"), // Turkish Van
                Map.entry("タイ", "siam"), // Siamese
                Map.entry("ノルウェー", "nfo"), // Norwegian Forest Cat
                Map.entry("フランス", "cha"), // Chartreux
                Map.entry("カナダ", "sphy"), // Sphynx
                Map.entry("スコットランド", "sfol"), // Scottish Fold
                Map.entry("シンガポール", "sing") // Singapura
        );
        Scanner scanner = new Scanner(System.in);
        System.out.print("国名を入力してください（例: 日本, アメリカ, ロシア...）: ");
        String country = scanner.nextLine().trim();
        String breedId = countryToBreedId.get(country);

        try {
            HttpClient client = HttpClient.newHttpClient();

            // 1. 猫画像API
            String imageUrl = null;
            try {
                String catApiUrl;
                if (breedId != null) {
                    catApiUrl = "https://api.thecatapi.com/v1/images/search?breed_ids=" + breedId;
                } else {
                    catApiUrl = "https://api.thecatapi.com/v1/images/search";
                }
                HttpRequest catRequest = HttpRequest.newBuilder()
                        .uri(URI.create(catApiUrl))
                        .header("x-api-key", API_KEY)
                        .GET()
                        .build();
                HttpResponse<String> catResponse = client.send(catRequest, HttpResponse.BodyHandlers.ofString());
                JSONArray array = new JSONArray(catResponse.body());
                if (array.length() > 0) {
                    JSONObject catObject = array.getJSONObject(0);
                    imageUrl = catObject.getString("url");
                } else {
                    imageUrl = "(該当する猫画像が見つかりませんでした)";
                }
            } catch (Exception e) {
                System.out.println("猫画像の取得に失敗しました。\n" + e.getMessage());
                imageUrl = "(取得失敗)";
            }

            // 2. 猫雑学API
            String factJa = null;
            try {
                String factApiUrl = "https://catfact.ninja/fact";
                HttpRequest factRequest = HttpRequest.newBuilder()
                        .uri(URI.create(factApiUrl))
                        .GET()
                        .build();
                HttpResponse<String> factResponse = client.send(factRequest, HttpResponse.BodyHandlers.ofString());
                JSONObject json = new JSONObject(factResponse.body());
                String fact = json.getString("fact");
                // Google翻訳API（非公式）で日本語に翻訳
                String encodedFact = java.net.URLEncoder.encode(fact, "UTF-8");
                String transUrl = "https://translate.googleapis.com/translate_a/single?client=gtx&sl=en&tl=ja&dt=t&q="
                        + encodedFact;
                HttpRequest transRequest = HttpRequest.newBuilder()
                        .uri(URI.create(transUrl))
                        .GET()
                        .build();
                HttpResponse<String> transResponse = client.send(transRequest, HttpResponse.BodyHandlers.ofString());
                org.json.JSONArray transArray = new org.json.JSONArray(transResponse.body());
                factJa = transArray.getJSONArray(0).getJSONArray(0).getString(0);
            } catch (Exception e) {
                System.out.println("猫の雑学の取得に失敗しました。");
                factJa = "(取得失敗)";
            }

            // 結果表示
            System.out.println("\n=== 猫画像 & 猫の雑学 ===");
            System.out.println("猫画像URL: " + imageUrl);
            System.out.println("猫の雑学: " + factJa);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
