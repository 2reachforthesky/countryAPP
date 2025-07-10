package src;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TriviaQuiz {
    public static void main(String[] args) throws Exception {
        runQuiz();
    }

    public static void runQuiz() throws Exception {
        String apiUrl = "https://opentdb.com/api.php?amount=1&category=22&type=multiple";

        // URIを経由してURLを作成（Java 20以降の推奨方法）
        URI uri = new URI(apiUrl);
        URL url = uri.toURL();

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        StringBuilder json = new StringBuilder();
        try (BufferedReader in = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = in.readLine()) != null) {
                json.append(line);
            }
        }

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

            List<String> options = new ArrayList<>();
            options.add(correct);

            Matcher optionMatcher = Pattern.compile("\"(.*?)\"").matcher(incorrectRaw);
            while (optionMatcher.find()) {
                options.add(htmlUnescape(optionMatcher.group(1)));
            }

            Collections.shuffle(options);

            System.out.println("地理クイズ");
            System.out.print("問題: ");
            MyMemoryTranslateExample.printTranslation(question, "en", "ja");
            for (int i = 0; i < options.size(); i++) {
                System.out.printf("  %d. ", i + 1);
                MyMemoryTranslateExample.printTranslation(options.get(i), "en", "ja");
            }

            System.out.print("あなたの答え（番号で入力）: ");
            try (Scanner scanner = new Scanner(System.in)) {
                int userChoice = scanner.nextInt();
                if (userChoice < 1 || userChoice > options.size()) {
                    System.out.println("無効な番号です。");
                    return;
                }
                String selectedAnswer = options.get(userChoice - 1);

                if (selectedAnswer.equals(correct)) {
                    MyMemoryTranslateExample.printTranslation("Correct!", "en", "ja");
                } else {
                    MyMemoryTranslateExample.printTranslation("Incorrect. The correct answer is: " + correct, "en",
                            "ja");
                }
            }
        } else {
            MyMemoryTranslateExample.printTranslation("Failed to get quiz.", "en", "ja");
        }
    }

    public static String htmlUnescape(String s) {
        return s.replace("&quot;", "\"")
                .replace("&#039;", "'")
                .replace("&amp;", "&")
                .replace("&lt;", "<")
                .replace("&gt;", ">");
    }
}