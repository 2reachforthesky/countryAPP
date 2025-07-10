package src;
public class demo {
    public static void main(String[] args) {
        try {
            // DictionaryAPIのgetWordInfoで意味を調べる
            DictionaryAPI.getWordInfo();
        
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

    