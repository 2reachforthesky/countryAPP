import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.HashMap;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONObject;

public class RandomPokemon {

    // ポケモンの最大ID（必要に応じて更新してください）
    private static final int MAX_POKEMON_ID = 1010;

    // タイプ英日対応表（公式準拠）
    private static final Map<String, String> TYPE_JA_MAP = Map.ofEntries(
            Map.entry("normal", "ノーマル"),
            Map.entry("fire", "ほのお"),
            Map.entry("water", "みず"),
            Map.entry("electric", "でんき"),
            Map.entry("grass", "くさ"),
            Map.entry("ice", "こおり"),
            Map.entry("fighting", "かくとう"),
            Map.entry("poison", "どく"),
            Map.entry("ground", "じめん"),
            Map.entry("flying", "ひこう"),
            Map.entry("psychic", "エスパー"),
            Map.entry("bug", "むし"),
            Map.entry("rock", "いわ"),
            Map.entry("ghost", "ゴースト"),
            Map.entry("dragon", "ドラゴン"),
            Map.entry("dark", "あく"),
            Map.entry("steel", "はがね"),
            Map.entry("fairy", "フェアリー"));

    public static void main(String[] args) {
        int randomId = getRandomPokemonId();
        System.out.println("ランダムなポケモンID: " + randomId);

        String url = "https://pokeapi.co/api/v2/pokemon/" + randomId;
        String speciesUrl = "https://pokeapi.co/api/v2/pokemon-species/" + randomId;

        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JSONObject json = new JSONObject(response.body());
                String name = json.getString("name");
                int id = json.getInt("id");

                // 公式日本語名取得
                String nameJa = null;
                try {
                    HttpRequest speciesRequest = HttpRequest.newBuilder()
                            .uri(URI.create(speciesUrl))
                            .GET()
                            .build();
                    HttpResponse<String> speciesResponse = client.send(speciesRequest,
                            HttpResponse.BodyHandlers.ofString());
                    if (speciesResponse.statusCode() == 200) {
                        JSONObject speciesJson = new JSONObject(speciesResponse.body());
                        JSONArray namesArray = speciesJson.getJSONArray("names");
                        for (int i = 0; i < namesArray.length(); i++) {
                            JSONObject nameObj = namesArray.getJSONObject(i);
                            if ("ja".equals(nameObj.getJSONObject("language").getString("name"))) {
                                nameJa = nameObj.getString("name");
                                break;
                            }
                        }
                    }
                } catch (Exception e) {
                    // 取得失敗時はnullのまま
                }
                if (nameJa == null) {
                    nameJa = translateToJapanese(name);
                }

                System.out.println("==============================");
                System.out.println("ID: " + id);
                System.out.println("英語名: " + name);
                System.out.println("日本語名: " + nameJa);
                System.out.println("タイプ:");
                JSONArray types = json.getJSONArray("types");
                for (int i = 0; i < types.length(); i++) {
                    JSONObject typeInfo = types.getJSONObject(i).getJSONObject("type");
                    String typeEn = typeInfo.getString("name");
                    String typeJa = TYPE_JA_MAP.getOrDefault(typeEn, translateToJapanese(typeEn));
                    System.out.println("- " + typeEn + "（" + typeJa + "）");
                }
                System.out.println("==============================");
            } else {
                System.out.println("エラー: HTTPステータス " + response.statusCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 1からMAX_POKEMON_IDまでのランダムな整数を返す
    private static int getRandomPokemonId() {
        Random random = new Random();
        return random.nextInt(MAX_POKEMON_ID) + 1;
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
