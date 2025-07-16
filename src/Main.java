package src;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in); // MainクラスでScannerを初期化
        int choice = -1;

        while (choice != 0) {
            printMainMenu(); // メインメニューを表示
            try {
                System.out.print("実行したい機能の番号を入力してください: ");
                choice = scanner.nextInt();
                scanner.nextLine(); // 改行文字を消費

                switch (choice) {
                    case 1:
                        executeCountryInfo(scanner);
                        break;
                    case 2:
                        executeWeatherInfo(scanner);
                        break;
                    case 3:
                        executeCurrencyRate(scanner);
                        break;
                    case 4: // 地理クイズの呼び出し
                        // **** ここが修正箇所 ****
                        executeTriviaQuiz(scanner); // executeTriviaQuiz に scanner を渡す
                        // **** 以前の Main.java のコードでは、executeTriviaQuiz() が引数を取らないと定義されていたため、
                        // **** ここでエラーが出ていました。executeTriviaQuiz も scanner を受け取る必要があります。
                        break;
                   case 5: // その他のゲームとツール (main2のメニューを呼び出す)
                    main2.startProgramSelectionMenu(scanner);
                    System.out.println("\n--- メインメニューに戻りました ---");
                        break;
                    case 0:
                        System.out.println("プログラムを終了します。");
                        break;
                    default:
                        System.out.println("無効な入力です。1〜5、または0を入力してください。");
                }
                System.out.println(); // 各機能実行後に空行
            } catch (InputMismatchException e) {
                System.out.println("無効な入力です。数字を入力してください。");
                scanner.nextLine(); // 不正な入力をクリア
            } catch (Exception e) {
                System.out.println("エラーが発生しました: " + e.getMessage());
                e.printStackTrace();
            }
        }
        scanner.close(); // Mainクラスで最後にScannerを閉じる
    }

    // メインメニュー表示用メソッド
    private static void printMainMenu() {
        System.out.println("--- メインメニュー ---");
        System.out.println("1. 国に関する詳細情報を取得（概要、言語、宗教、経済データを含む）");
        System.out.println("2. 天気情報を取得");
        System.out.println("3. 通貨レートを取得");
        System.out.println("4. 地理クイズをプレイ");
        System.out.println("5. その他のゲームとツール");
        System.out.println("0. 終了");
    }

    // 国情報、Wikipedia概要をまとめて実行するメソッド
    private static void executeCountryInfo(Scanner scanner) throws Exception {
        System.out.println("\n--- 国に関する詳細情報 ---");
        String country = prompt(scanner, "国名を入力してください（例：Japan）");

        // 基本情報
        printSection("基本情報");
        System.out.println(CountryInfoFetcher.getCountryInfo(country));

        // 言語と宗教
        printSection("言語と宗教");
        CountryInfoApp.fetchWikidataLanguageReligion(country);

        // 経済・詳細情報 (世界銀行データ)
        printSection("経済・詳細情報");
        String countryCode = CountryCodeConverter.getCountryCode(country);
        if (countryCode != null) {
            WorldBankApiNoJackson.worldBank(countryCode);
        } else {
            System.out.println("国コードが見つかりませんでした。詳細情報を取得できません。");
        }

        // Wikipedia概要
        printSection("Wikipedia概要");
        System.out.println(WikipediaFetcher.getSummary(country));
    }

    // 天気情報取得を実行するメソッド (新しく独立)
    private static void executeWeatherInfo(Scanner scanner) throws Exception {
        System.out.println("\n--- 天気情報取得 ---");
        String city = prompt(scanner, "都市名を入力してください（例：Tokyo）");
        String apiKeyWeather = System.getenv("OPENWEATHER_API_KEY");
        printSection("天気情報");
        if (apiKeyWeather == null || apiKeyWeather.isBlank()) {
            System.out.println("エラー: OPENWEATHER_API_KEY 環境変数が設定されていません。");
        } else {
            System.out.println(WeatherFetcher.getWeather(city, apiKeyWeather));
        }
    }

    // 通貨レート取得を実行するメソッド (変更なし)
    private static void executeCurrencyRate(Scanner scanner) throws Exception {
        System.out.println("\n--- 通貨レート取得 ---");
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

    // 地理クイズを実行するメソッド
    // **** ここも修正箇所: Scanner を引数として受け取るように変更 ****
    private static void executeTriviaQuiz(Scanner scanner) throws Exception {
        System.out.println("\n--- 地理クイズ ---");
        printSection("地理クイズ");
        TriviaQuiz.runQuiz(scanner); // ここで TriviaQuiz.runQuiz に scanner を渡す
    }

    // 入力用の共通メソッド (変更なし)
    private static String prompt(Scanner scanner, String message) {
        System.out.print(message + ": ");
        return scanner.nextLine().trim();
    }

    // 区切り線表示 (変更なし)
    private static void printSection(String title) {
        System.out.println("\n--- " + title + " ---");
    }
}