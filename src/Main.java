package src;

import java.util.Scanner;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class Main {
    public static void main(String[] args) throws Exception {
        try (Scanner scanner = new Scanner(System.in)) {

            // 国情報取得
            String country = prompt(scanner, "国名を入力してください（例：Japan）");
            printSection("国情報");
            System.out.println(CountryInfoFetcher.getCountryInfo(country));

            // Wikipedia概要
            printSection("Wikipedia概要");
            System.out.println(WikipediaFetcher.getSummary(country));

            // 天気情報
            String city = prompt(scanner, "都市名を入力してください（例：Tokyo）");
            String apiKeyWeather = System.getenv("OPENWEATHER_API_KEY");
            printSection("天気情報");
            System.out.println(WeatherFetcher.getWeather(city, apiKeyWeather));

            // 通貨レート
            String from = prompt(scanner, "通貨コード（例：USD）");
            String to = prompt(scanner, "→ 対象通貨（例：JPY）");
            String apiKeyCurrency = System.getenv("APILAYER_API_KEY");

            printSection("通貨レート");
            if (apiKeyCurrency == null || apiKeyCurrency.isBlank()) {
                System.out.println("エラー: APILAYER_API_KEY 環境変数が設定されていません。");
            } else {
                String rateLine = CurrencyRateFetcher.getRate(from, to, apiKeyCurrency);
                String today = LocalDate.now(ZoneId.of("Asia/Tokyo"))
                        .format(DateTimeFormatter.ofPattern("yyyy年MM月dd日"));
                System.out.printf(" 通貨換算結果（%s 時点）\n\n%s\n", today, rateLine);
            }
        }
    }

    // 入力用の共通メソッド
    private static String prompt(Scanner scanner, String message) {
        System.out.print(message + ": ");
        return scanner.nextLine().trim();
    }

    // 区切り線表示
    private static void printSection(String title) {
        System.out.println("\n--- " + title + " ---");
    }
}
