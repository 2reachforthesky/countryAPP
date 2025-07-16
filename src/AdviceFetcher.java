package src;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URI; // URIをインポート (MyMemoryTranslateExampleに合わせて追加)

import org.json.JSONObject;

public class AdviceFetcher {

    public static void main(String[] args) {
        // fetchAndDisplayAdvice() メソッドを呼び出すだけ
        fetchAndDisplayAdvice();
    }

    /**
     * ランダムなアドバイスを取得し、日本語に翻訳して表示します。
     * APIへのHTTPリクエストを行い、レスポンスをJSONとしてパースします。
     * その後、MyMemoryTranslateExampleを使用して翻訳を行います。
     */
    public static void fetchAndDisplayAdvice() {
        try {
            String apiUrl = "https://api.adviceslip.com/advice";

            // HTTPリクエストの準備 (URIを介してURLを構築することで、より安全かつ推奨される方法に)
            URI uri = new URI(apiUrl);
            URL url = uri.toURL();

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            // レスポンスコード確認
            int status = conn.getResponseCode();
            if (status != 200) {
                System.out.println("エラーが発生しました: " + status);
                return;
            }

            // レスポンスを読み込む
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line);
            }
            in.close();

            // JSONをパースしてアドバイスを抽出
            JSONObject json = new JSONObject(response.toString());
            String advice = json.getJSONObject("slip").getString("advice");

            // 取得したアドバイスを日本語に翻訳
            // MyMemoryTranslateExample クラスが同じ src パッケージ内にあるか、
            // クラスパスに含まれていることを確認してください。
            String translatedAdvice = MyMemoryTranslateExample.translate(advice, "en", "ja");

            // 結果表示
            System.out.println(" 今日のアドバイス: " + translatedAdvice);

        } catch (Exception e) {
            System.out.println("アドバイスの取得または翻訳中にエラーが発生しました。");
            e.printStackTrace(); // デバッグ用にスタックトレースを表示
        }
    }
}