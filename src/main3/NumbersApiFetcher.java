package src.main3;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.InputMismatchException; // InputMismatchException をインポート
import java.util.Scanner; // Scanner をインポート

import org.json.JSONArray; // org.json.JSONArray をインポート

public class NumbersApiFetcher {

    /**
     * Numbers APIから指定された数字のトリビアを取得し、表示します。
     * 翻訳機能も含まれます。
     *
     * @param scanner ユーザー入力のためのScannerオブジェクト
     */
    public static void fetchAndDisplayNumberTrivia(Scanner scanner) {
        System.out.println("\n--- 数字のトリビア ---");
        System.out.print("トリビアを知りたい数字を入力してください: ");
        int number;

        try {
            number = scanner.nextInt();
            scanner.nextLine(); // 改行文字を消費
        } catch (InputMismatchException e) {
            System.out.println("無効な入力です。数字を入力してください。");
            scanner.nextLine(); // 不正な入力をクリア
            return;
        }

        // URLの組み立て
        String url = "http://numbersapi.com/" + number;

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                String trivia = response.body();
                String triviaJa = translateToJapanese(trivia); // Google翻訳APIは非公式のため、動作保証はありません

                System.out.println("\nNumbers APIのトリビア:");
                System.out.println(trivia);
                System.out.println("（日本語訳）:");
                System.out.println(triviaJa);
            } else {
                System.out.println("API呼び出しに失敗しました。ステータスコード: " + response.statusCode());
            }
        } catch (Exception e) {
            System.out.println("エラーが発生しました。Numbers APIからのデータ取得中に問題がありました。");
            e.printStackTrace();
        }
    }

    /**
     * Google翻訳API（非公式）を使用して英語のテキストを日本語に翻訳します。
     * このAPIは非公式であり、将来的に利用できなくなる可能性があります。
     *
     * @param text 翻訳する英語のテキスト
     * @return 翻訳された日本語のテキスト、または翻訳失敗時には "(翻訳失敗)"
     */
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

            // レスポンスがJSON配列であることを確認
            JSONArray transArray = new JSONArray(response.body());
            // 翻訳されたテキストは通常、[0][0][0] に含まれる
            return transArray.getJSONArray(0).getJSONArray(0).getString(0);
        } catch (Exception e) {
            System.err.println("翻訳中にエラーが発生しました: " + e.getMessage());
            return "(翻訳失敗)";
        }
    }

    // テスト用のmainメソッド
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            fetchAndDisplayNumberTrivia(scanner);
        }
    }
}