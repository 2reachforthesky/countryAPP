package src;

import java.util.InputMismatchException;
import java.util.Scanner;

import src.main2.AdviceFetcher;
import src.main2.AgePredictor;
import src.main2.CatFactGame;
import src.main2.CatFactGame2;
import src.main2.PokemonFetcher;

public class Main2 {

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
    public static void startProgramSelectionMenu(Scanner scanner) {
        int choice = -1; // ユーザーの選択肢を初期化

        // 無限ループを防ぐため、サブメニューもループで囲む
        while (choice != 0) {
            System.out.println("\n--- その他のゲームとツール メニュー(1/2) ---"); // サブメニューであることを明示
            System.out.println("1. 猫の雑学について");
            System.out.println("2. 猫クイズゲームをプレイ");
            System.out.println("3. 今日のアドバイスを取得");
            System.out.println("4. ポケモンランダム表示");
            System.out.println("5. 年齢予測");
            System.out.println("6. 次へ");
            System.out.println("0. メインメニューに戻る"); // 戻ることを明示
            System.out.print("実行したい機能の番号を入力してください: ");

            try {
                choice = scanner.nextInt(); // ユーザーの入力を読み込む
                scanner.nextLine(); // 改行文字を消費 (nextInt() の後に残る)

                switch (choice) {
                    case 1:
                        System.out.println("\n猫の雑学について起動します");
                        CatFactGame2.playCatFactGame(scanner); // CatFactGame2にscannerを渡す
                        System.out.println("猫の雑学についてが終了しました。\n");
                        break;
                    case 2:
                        System.out.println("\n猫クイズゲームを起動します！");
                        // *** ここが修正点です！ CatFactGame.playCatFactGame() に scanner を渡します ***
                        CatFactGame.playCatFactGame(scanner);
                        System.out.println("猫クイズゲームが終了しました。\n");
                        break;
                    case 3:
                        System.out.println("\nアドバイスを起動します！");
                        AdviceFetcher.fetchAndDisplayAdvice();
                        System.out.println("アドバイスの表示が完了しました。\n");
                        break;
                    case 4:
                        System.out.println("\nポケモンランダム表示を起動します！");
                        PokemonFetcher.fetchAndDisplayRandomPokemon();
                        System.out.println("ポケモンランダム表示が終了しました。\n");
                        break;
                    case 5:
                        System.out.println("\n年齢予測を起動します！");
                        AgePredictor.predictAge(scanner);
                        System.out.println("年齢予測を終了しました。\n");
                        break;
                    case 6:
                        System.out.println("\n 次へ");
                        Main3.startProgramSelectionMenu(scanner);
                        System.out.println("戻りました\n");
                        break;
                    case 0:
                        System.out.println("メインメニューに戻ります。");
                        break;
                    default:
                        System.out.println("無効な入力です。1〜6、または0を入力してください。");
                }
            } catch (InputMismatchException e) {
                System.out.println("無効な入力です。数字を入力してください。");
                scanner.nextLine(); // 不正な入力をクリア
                choice = -1; // ループが継続するようにする
            } catch (Exception e) {
                System.out.println("エラーが発生しました: " + e.getMessage());
                e.printStackTrace();
                choice = 0; // 例外が発生したらサブメニューを終了してメインに戻る
            }
        }
    }
}