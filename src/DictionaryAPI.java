package src;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URI;
import java.net.URISyntaxException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Scanner;
import org.json.JSONArray;
import org.json.JSONObject;

public class DictionaryAPI {
    public static void main(String[] args) {
        getWordInfo();
    }

    public static void getWordInfo() {
        System.out.print("調べたい単語を入力してください: ");

        try (Scanner scanner = new Scanner(System.in)) {
            String word = scanner.nextLine().trim();
            String apiUrl = "https://api.dictionaryapi.dev/api/v2/entries/en/" + word;

            // 推奨される方法: URIを使ってURLを作成
            URI uri = new URI(apiUrl);
            URL url = uri.toURL();

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                StringBuilder response = new StringBuilder();
                String inputLine;

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }

                JSONArray jsonArray = new JSONArray(response.toString());
                JSONObject wordData = jsonArray.getJSONObject(0);
                JSONArray meanings = wordData.getJSONArray("meanings");
                JSONObject meaning = meanings.getJSONObject(0);
                JSONArray definitions = meaning.getJSONArray("definitions");
                JSONObject definition = definitions.getJSONObject(0);

                System.out.println("単語: " + word);
                String meaningEn = definition.getString("definition");
                System.out.println("意味: " + meaningEn);

                // 翻訳処理（定義済みの別クラスが必要）
                MyMemoryTranslateExample.printTranslation(meaningEn, "en", "ja");

            } catch (Exception e) {
                System.out.println("意味の取得中にエラーが発生しました。");
                e.printStackTrace();
            }
        } catch (URISyntaxException e) {
            System.out.println("URLの構文が不正です。");
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("接続エラーまたは入力エラーが発生しました。");
            e.printStackTrace();
        }
    }
}