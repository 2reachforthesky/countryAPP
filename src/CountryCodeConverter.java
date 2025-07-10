package src;

import java.util.HashMap;
import java.util.Map;

public class CountryCodeConverter {
    private static final Map<String, String> countryNameToCode = new HashMap<>();
    static {
        countryNameToCode.put("japan", "JP");
        countryNameToCode.put("united states", "US");
        countryNameToCode.put("germany", "DE");
        countryNameToCode.put("france", "FR");
        // 必要に応じて追加...
    }

    public static String getCountryCode(String countryName) {
        if (countryName == null)
            return null;
        return countryNameToCode.get(countryName.toLowerCase());
    }

    public static void main(String[] args) {
        String input = "japan";
        String countryCode = getCountryCode(input);

        if (countryCode != null) {
            System.out.println("Country code: " + countryCode);
        } else {
            System.out.println("Country not found");
        }
    }
}
