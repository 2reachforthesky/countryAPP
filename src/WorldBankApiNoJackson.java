package src;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WorldBankApiNoJackson {
    public static void main(String[] args) throws Exception {
        if (args.length > 0) {
            worldBank(args[0]);
        } else {
            try (Scanner scanner = new Scanner(System.in)) {
                System.out.print("国コードを入力してください（例: jp, us, cn）: ");
                String countryCode = scanner.nextLine().trim().toLowerCase();
                worldBank(countryCode);
            }
        }
    }

    public static void worldBank(String countryCode) throws Exception {
        Map<String, String> indicators = Map.of(
                "NY.GDP.MKTP.CD", "国内総生産（GDP）［現在米ドル］",
                "SP.POP.TOTL", "人口",
                "SL.UEM.TOTL.ZS", "失業率（％）");

        for (Map.Entry<String, String> entry : indicators.entrySet()) {
            String indicatorCode = entry.getKey();
            String indicatorName = entry.getValue();

            System.out.println("指標名: " + indicatorName);
            String urlStr = String.format(
                    "https://api.worldbank.org/v2/country/%s/indicator/%s?format=json&per_page=5",
                    countryCode, indicatorCode);

            // URIを経由してURL作成（非推奨回避）
            URI uri = new URI(urlStr);
            URL url = uri.toURL();

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            StringBuilder response = new StringBuilder();
            try (BufferedReader in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = in.readLine()) != null) {
                    response.append(line);
                }
            }

            String json = response.toString();

            Pattern pattern = Pattern
                    .compile("\"date\"\\s*:\\s*\"(\\d{4})\".*?\"value\"\\s*:\\s*(null|[\\d\\.E\\+\\-]+)");
            Matcher matcher = pattern.matcher(json);

            int count = 0;
            while (matcher.find() && count < 5) {
                String year = matcher.group(1);
                String valueRaw = matcher.group(2);
                String value = valueRaw.equals("null") ? "データなし" : formatNumber(valueRaw);
                System.out.printf("  年度: %s  値: %s\n", year, value);
                count++;
            }
            System.out.println("---------------------------------------------------");
        }
    }

    private static String formatNumber(String numStr) {
        try {
            double num = Double.parseDouble(numStr);
            return String.format("%,.2f", num);
        } catch (NumberFormatException e) {
            return numStr;
        }
    }
}