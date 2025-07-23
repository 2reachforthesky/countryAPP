package src.main2;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.Random;
import java.util.Scanner;

import org.json.JSONObject;

import src.MyMemoryTranslateExample;

public class CatFactGame {

    // mainメソッドは通常、スタンドアロン実行用。
    // 今回はmain2から呼ばれるため、このmainメソッドは削除またはコメントアウトしても良い。
    // ただし、単体テストなどで使う場合は残しておくと便利。
    public static void main(String[] args) {
        // スタンドアロン実行の場合、ここでScannerを生成して渡す
        try (Scanner mainScanner = new Scanner(System.in)) {
            playCatFactGame(mainScanner);
        }
    }

    /**
     * 猫クイズゲームを実行します。
     * ユーザーは猫の事実が本当か嘘かを判断し、スコアを獲得します。
     * 取得した事実はMyMemoryTranslateExampleを使用して日本語に翻訳されます。
     * 
     * @param scanner ユーザー入力のためのScannerオブジェクト
     */
    public static void playCatFactGame(Scanner scanner) { // ★ここに Scanner scanner を追加 ★
        // Scanner scanner = new Scanner(System.in); // ★この行を削除またはコメントアウト★
        int score = 0;

        System.out.println(" 猫クイズゲームへようこそ！正しいと思えば 't'、間違ってると思えば 'f' を入力してください。");
        System.out.println("ゲームスタート！\n");

        for (int i = 1; i <= 5; i++) {
            try {
                String fact = getCatFact();
                boolean isTrue = new Random().nextBoolean();
                String displayedFactEnglish = isTrue ? fact : fakeFact(fact);

                // ここで事実を日本語に翻訳
                String displayedFactJapanese = MyMemoryTranslateExample.translate(displayedFactEnglish, "en", "ja");

                System.out.println("Q" + i + ": 「" + displayedFactJapanese + "」");
                System.out.print("これは本当？ (t/f): ");
                String answer = scanner.nextLine().trim().toLowerCase();

                if ((isTrue && answer.equals("t")) || (!isTrue && answer.equals("f"))) {
                    System.out.println(" 〇正解！");
                    score++;
                } else {
                    System.out.println("☓ 不正解！");
                    System.out.println("正しいのは: " + (isTrue ? "True（事実）" : "False（うそ）"));
                    // 不正解の場合、元の事実も日本語で表示すると良いでしょう
                    System.out.println("元の事実: 「" + MyMemoryTranslateExample.translate(fact, "en", "ja") + "」");
                }
                System.out.println();

            } catch (Exception e) {
                System.out.println("⚠️ APIから事実を取得または翻訳できませんでした。スキップします。");
                e.printStackTrace(); // デバッグ用にスタックトレースを表示
            }
        }

        System.out.println(" ゲーム終了！あなたのスコアは " + score + " / 5 でした！");
        // scanner.close(); // ★この行を削除またはコメントアウト！★
        // 呼び出し元でScannerを閉じさせるため、ここでは閉じない。
    }

    private static String getCatFact() throws Exception {
        URI uri = new URI("https://catfact.ninja/fact");
        URL url = uri.toURL();

        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = in.readLine()) != null) {
            response.append(line);
        }
        in.close();

        JSONObject json = new JSONObject(response.toString());
        return json.getString("fact");
    }

    private static String fakeFact(String fact) {
        if (fact.contains("sleep")) {
            return fact.replaceAll("sleep", "dance");
        } else if (fact.contains("fur")) {
            return fact.replaceAll("fur", "feathers");
        } else if (fact.contains("climb")) {
            return fact.replaceAll("climb", "swim");
        }
        return fact + " (This is also about dogs.)";
    }
}