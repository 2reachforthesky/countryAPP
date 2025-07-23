package src;

import java.util.InputMismatchException;
import java.util.Scanner;
// CatFactGameが使用するAPI関連のimportは不要です。
// CatFactGameクラス内でimportされます。

import src.main3.AnimeTriviaGame;
import src.main3.NasaApodFetcher;
import src.main3.NumbersApiFetcher;
import src.main3.OpenTriviaGeoEasy;
import src.main3.TodayInHistoryFetcher;

public class Main3 {

    // mainメソッドはテスト用として残すか、削除しても良い
    public static void main(String[] args) {
        // standalone で実行する場合のためにScannerを渡す例
        try (Scanner scanner = new Scanner(System.in)) {
            startProgramSelectionMenu(scanner);
        }
    }

    /**
     * ユーザーが猫クイズゲームまたは今日のアドバイスのいずれかを選択して実行できるメニューを開始します。
     * 渡されたScannerオブジェクトを使用し、このメソッド内でScannerは閉じません。
     *
     * @param scanner ユーザー入力のためのScannerオブジェクト
     */
    public static void startProgramSelectionMenu(Scanner scanner) { // Scannerを引数として受け取るように変更
        int choice = -1; // ユーザーの選択肢を初期化

        // 無限ループを防ぐため、サブメニューもループで囲む
        while (choice != 0) {
            System.out.println("\n--- その他のゲームとツール メニュー(2/2) ---"); // サブメニューであることを明示
            System.out.println("1.アニメトリビア ");
            System.out.println("2.NASAの今日の画像");
            System.out.println("3.数字にまつわるトリビア");
            System.out.println("4.地理クイズ（簡単）");
            System.out.println("5.今日の歴史上の出来事");
            System.out.println("0.戻る "); 
            System.out.print("実行したい機能の番号を入力してください: ");

            try {
                choice = scanner.nextInt(); // ユーザーの入力を読み込む
                scanner.nextLine(); // 改行文字を消費 (nextInt() の後に残る)

                switch (choice) {
                    case 1:
                        System.out.println("\nアニメトリビアゲームを起動します！");
                        AnimeTriviaGame.playAnimeTriviaGame(scanner); 
                        System.out.println("アニメトリビアゲームを終了しました\n");
                        break;
                    case 2:
                        System.out.println("\nNaSAの今日の画像を起動します！");
                        NasaApodFetcher.fetchAndDisplayApod();
                        System.out.println("NsSAの今日の画像を終了します\n");
                        break;
                    case 3:
                        System.out.println("\n 数字にまつわるトリビアを起動します！");
                        // AdviceFetcherクラスとそのメソッドが別途存在することを想定しています。
                        NumbersApiFetcher.fetchAndDisplayNumberTrivia(scanner); // NumbersApiFetcherの関数を呼び出す
                        System.out.println("数字にまつわるトリビアが終了しました\n");
                        break;
                    case 4:
                        System.out.println("\n地理クイズ（簡単）を起動します！");
                        OpenTriviaGeoEasy.playGeoTriviaQuiz(scanner); 
                        ; // CatFactGameの関数を呼び出す1
                        System.out.println("地理クイズ（簡単）が終了しました\n");
                        break;
                    case 5:
                        System.out.println("\n今日の歴史上の出来事を起動します！");
                        TodayInHistoryFetcher.displayTodayInHistory();
                        System.out.println("今日の歴史上の出来事を終了します\n");
                        break;
                    case 6:
                        System.out.println("\n戻る");
                        Main2.startProgramSelectionMenu(scanner);
                        System.out.println("\n");
                        break;
                    case 0:
                        System.out.println("メインメニューに戻ります。");
                        break;
                    default:
                        System.out.println("無効な入力です。1、2、または0を入力してください。");
                }
            } catch (InputMismatchException e) {
                System.out.println("無効な入力です。数字を入力してください。");
                scanner.nextLine(); // 不正な入力をクリア
                // ここでchoiceを-1に戻すなどして、ループが継続するようにする
                choice = -1;
            } catch (Exception e) {
                System.out.println("エラーが発生しました: " + e.getMessage());
                e.printStackTrace();
                // 例外発生時もループを継続させるか、終了させるかを考慮する
                choice = 0; // 例外が発生したらサブメニューを終了してメインに戻る
            }
        }
        // ここではscanner.close() を呼び出さない！
        // Scannerは呼び出し元 (Mainクラス) で閉じられるべき
    }
}