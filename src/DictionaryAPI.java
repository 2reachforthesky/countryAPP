package src;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import org.json.JSONArray;
import org.json.JSONObject;

public class DictionaryAPI {
    public static void main(String[] args) {
        getWordInfo();
    }

    public static void getWordInfo() {
        System.out.print("調べたい単語を入力してください: ");
        java.util.Scanner scanner = new java.util.Scanner(System.in);
        String word = scanner.nextLine();
        String apiUrl = "https://api.dictionaryapi.dev/api/v2/entries/en/" + word;

        try {
            // URLオブジェクトの作成
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // レスポンスを取得
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // JSONレスポンスの処理
            JSONArray jsonArray = new JSONArray(response.toString());
            JSONObject wordData = jsonArray.getJSONObject(0); // 単語データ（最初のエントリ）
            JSONArray meanings = wordData.getJSONArray("meanings"); // 意味の配列
            JSONObject meaning = meanings.getJSONObject(0); // 最初の意味
            JSONArray definitions = meaning.getJSONArray("definitions"); // 定義の配列
            JSONObject definition = definitions.getJSONObject(0); // 最初の定義

            // 結果を表示
            System.out.println("単語: " + word);
            String meaningEn = definition.getString("definition");
            System.out.println("意味: " + meaningEn);

            // 翻訳
            MyMemoryTranslateExample.printTranslation(meaningEn, "en", "ja");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
