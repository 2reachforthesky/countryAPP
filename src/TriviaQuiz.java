package src;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TriviaQuiz {
    public static void main(String[] args) throws Exception {
        runQuiz();
    }

    public static void runQuiz() throws Exception {
        // API URL（地理カテゴリで1問取得、選択式）
        String apiUrl = "https://opentdb.com/api.php?amount=1&category=22&type=multiple";

        // HTTP GETリクエスト
        URL url = new URL(apiUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        StringBuilder json = new StringBuilder();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            String line;
            while ((line = in.readLine()) != null) {
                json.append(line);
            }
        }

        // 問題を抽出（正規表現で抽出）
        Pattern questionPattern = Pattern.compile("\"question\":\"(.*?)\"");
        Pattern correctPattern = Pattern.compile("\"correct_answer\":\"(.*?)\"");
        Pattern incorrectPattern = Pattern.compile("\"incorrect_answers\":\\[(.*?)\\]");

        Matcher questionMatcher = questionPattern.matcher(json);
        Matcher correctMatcher = correctPattern.matcher(json);
        Matcher incorrectMatcher = incorrectPattern.matcher(json);

        if (questionMatcher.find() && correctMatcher.find() && incorrectMatcher.find()) {
            String question = htmlUnescape(questionMatcher.group(1));
            String correct = htmlUnescape(correctMatcher.group(1));
            String incorrectRaw = incorrectMatcher.group(1);

            // 不正解選択肢を配列に
            List<String> options = new ArrayList<>();
            options.add(correct);

            Matcher optionMatcher = Pattern.compile("\"(.*?)\"").matcher(incorrectRaw);
            while (optionMatcher.find()) {
                options.add(htmlUnescape(optionMatcher.group(1)));
            }

            // シャッフルしてランダム配置
            Collections.shuffle(options);

            // 表示
            System.out.println("地理クイズ");
            System.out.print("問題: ");
            MyMemoryTranslateExample.printTranslation(question, "en", "ja");
            for (int i = 0; i < options.size(); i++) {
                System.out.printf("  %d. ", i + 1);
                MyMemoryTranslateExample.printTranslation(options.get(i), "en", "ja");
            }

            // ユーザーの回答を受け取る
            System.out.print("あなたの答え（番号で入力）: ");
            Scanner scanner = new Scanner(System.in);
            int userChoice = scanner.nextInt();
            String selectedAnswer = options.get(userChoice - 1);

            if (selectedAnswer.equals(correct)) {
                MyMemoryTranslateExample.printTranslation("Correct!", "en", "ja");
            } else {
                MyMemoryTranslateExample.printTranslation("Incorrect. The correct answer is: " + correct, "en", "ja");
            }
        } else {
            MyMemoryTranslateExample.printTranslation("Failed to get quiz.", "en", "ja");
        }
    }

    // HTMLエスケープを処理する補助メソッド
    public static String htmlUnescape(String s) {
        return s.replace("&quot;", "\"")
                .replace("&#039;", "'")
                .replace("&amp;", "&")
                .replace("&lt;", "<")
                .replace("&gt;", ">");
    }
}
