package src;

import java.util.InputMismatchException;
import java.util.Scanner;

public class main2 {

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
            System.out.println("\n--- その他のゲームとツール メニュー ---"); // サブメニューであることを明示
            System.out.println("1. 猫クイズゲームをプレイ");
            System.out.println("2. 今日のアドバイスを取得");
            System.out.println("0. メインメニューに戻る"); // 戻ることを明示
            System.out.print("実行したい機能の番号を入力してください: ");

            try {
                choice = scanner.nextInt(); // ユーザーの入力を読み込む
                scanner.nextLine(); // 改行文字を消費 (nextInt() の後に残る)

                switch (choice) {
                    case 1:
                        System.out.println("\n猫クイズゲームを起動します！");
                        CatFactGame.playCatFactGame(); // CatFactGameの関数を呼び出す
                        System.out.println("猫クイズゲームが終了しました。\n");
                        break;
                    case 2:
                        System.out.println("\nアドバイスを起動します！");
                        AdviceFetcher.fetchAndDisplayAdvice(); // AdviceFetcherの関数を呼び出す
                        System.out.println("アドバイスの表示が完了しました。\n");
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