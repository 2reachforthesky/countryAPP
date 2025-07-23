package src.main2;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Scanner; // Scanner をここで import
import org.json.JSONObject;

public class AgePredictor {

    /**
     * ユーザーが入力した名前（ローマ字）に基づいて、Agify APIを使って予測年齢を表示します。
     * 日本に限定して予測を行います。
     *
     * @param scanner ユーザー入力のためのScannerオブジェクト
     */
    public static void predictAge(Scanner scanner) {
        try {
            // ユーザーから名前を入力
            System.out.print("名前を入力してください（ローマ字）: ");
            String name = scanner.nextLine().trim(); // Scanner は呼び出し元から渡されたものを使用

            // 入力が空なら終了
            if (name.isEmpty()) {
                System.out.println("名前が入力されていません。年齢予測を終了します。");
                return;
            }

            // APIリクエストのURL（日本限定）
            String url = "https://api.agify.io?name=" + name + "&country_id=JP";

            // HTTPクライアントの作成とリクエスト
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(url))
                    .GET()
                    .build();

            // レスポンス取得
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // JSONパース
            JSONObject json = new JSONObject(response.body());

            // 結果を表示
            System.out.println("\n==============================");
            System.out.println("   【Agify 年齢予測結果】");
            System.out.println("------------------------------");
            System.out.println("名前（ローマ字）: " + json.getString("name"));
            if (!json.isNull("age")) {
                System.out.println("予測年齢（日本）: " + json.getInt("age") + "歳");
            } else {
                System.out.println("予測年齢（日本）: データなし");
            }
            System.out.println("データ件数: " + json.getInt("count"));
            System.out.println("==============================\n");

        } catch (Exception e) {
            System.out.println("エラーが発生しました:");
            e.printStackTrace();
        }
        // ここでは scanner.close() を呼び出さない！
        // Scanner は main2.java の main メソッドで開かれ、そこで閉じられるべきです。
    }
}