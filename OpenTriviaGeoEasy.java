import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Collections;

public class OpenTriviaGeoEasy {
    public static void main(String[] args) {
        // ジャンル22＝Geography、難易度easy、問題数1
        String url = "https://opentdb.com/api.php?amount=1&category=22&difficulty=easy";
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                JSONObject json = new JSONObject(response.body());
                int responseCode = json.getInt("response_code");
                if (responseCode != 0) {
                    System.out.println("API returned error code: " + responseCode);
                    return;
                }
                JSONArray results = json.getJSONArray("results");
                JSONObject questionData = results.getJSONObject(0);
                String question = decodeHtml(questionData.getString("question"));
                String correctAnswer = decodeHtml(questionData.getString("correct_answer"));
                JSONArray incorrectAnswers = questionData.getJSONArray("incorrect_answers");
                // 日本語翻訳
                String questionJa = translateToJapanese(question);
                String correctAnswerJa = translateToJapanese(correctAnswer);
                ArrayList<String> choices = new ArrayList<>();
                choices.add(correctAnswerJa);
                for (int i = 0; i < incorrectAnswers.length(); i++) {
                    String incorrect = decodeHtml(incorrectAnswers.getString(i));
                    String incorrectJa = translateToJapanese(incorrect);
                    choices.add(incorrectJa);
                }
                // 選択肢をシャッフル
                Collections.shuffle(choices);
                System.out.println("問題: " + questionJa);
                System.out.println("選択肢:");
                for (int i = 0; i < choices.size(); i++) {
                    System.out.printf("%d. %s\n", i + 1, choices.get(i));
                }
                // 回答受付
                try (Scanner scanner = new Scanner(System.in)) {
                    int userChoice = 0;
                    while (true) {
                        System.out.print("あなたの答えを番号で入力してください（1～" + choices.size() + "）: ");
                        String input = scanner.nextLine().trim();
                        try {
                            userChoice = Integer.parseInt(input);
                            if (userChoice >= 1 && userChoice <= choices.size()) {
                                break;
                            } else {
                                System.out.println("1から" + choices.size() + "の番号を入力してください。");
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("数字で入力してください。");
                        }
                    }
                    String userAnswer = choices.get(userChoice - 1);
                    if (userAnswer.equals(correctAnswerJa)) {
                        System.out.println("✅ 正解です！");
                    } else {
                        System.out.println("❌ 不正解です。正解は: " + correctAnswerJa);
                    }
                }
            } else {
                System.out.println("HTTP Error: " + response.statusCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Open Trivia DBはHTMLエスケープされているためデコード
    private static String decodeHtml(String text) {
        return text.replace("&quot;", "\"")
                .replace("&#039;", "'")
                .replace("&amp;", "&")
                .replace("&lt;", "<")
                .replace("&gt;", ">")
                .replace("&eacute;", "é")
                .replace("&ouml;", "ö")
                .replace("&uuml;", "ü");
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
