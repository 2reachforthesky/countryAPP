import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Scanner;
import org.json.JSONObject;

public class AgifyTerminal {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        try {
            // ユーザーから名前を入力
            System.out.print("名前を入力してください（ローマ字）: ");
            String name = scanner.nextLine().trim();
            // 入力が空なら終了
            if (name.isEmpty()) {
                System.out.println("名前が入力されていません。終了します。");
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
        } finally {
            scanner.close();
        }
    }
}
