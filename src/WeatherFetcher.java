package src;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.json.JSONObject;

public class WeatherFetcher {

    public static String getWeather(String city, String apiKey) throws Exception {
        String encodedCity = URLEncoder.encode(city, StandardCharsets.UTF_8.toString());
        String urlStr = "https://api.openweathermap.org/data/2.5/weather?q=" + encodedCity
                + "&appid=" + apiKey + "&units=metric&lang=ja";

        String response = fetchApiResponse(urlStr);
        JSONObject json = new JSONObject(response);
        return formatWeather(json);
    }

    /** APIからレスポンス文字列を取得 */
    private static String fetchApiResponse(String urlStr) throws Exception {
        URI uri = new URI(urlStr);
        HttpURLConnection conn = (HttpURLConnection) uri.toURL().openConnection();
        conn.setRequestMethod("GET");

        try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            StringBuilder content = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            return content.toString();
        } finally {
            conn.disconnect();
        }
    }

    /** JSONを整形した天気情報文字列に変換 */
    private static String formatWeather(JSONObject obj) {
        JSONObject weatherObj = obj.getJSONArray("weather").getJSONObject(0);
        JSONObject main = obj.getJSONObject("main");
        JSONObject wind = obj.getJSONObject("wind");

        String weatherMain = weatherObj.getString("main");
        String weatherDesc = weatherObj.getString("description");
        double temp = main.getDouble("temp");
        double feelsLike = main.getDouble("feels_like");
        int humidity = main.getInt("humidity");
        int pressure = main.getInt("pressure");
        int visibility = obj.has("visibility") ? obj.getInt("visibility") : -1;
        double windSpeed = wind.getDouble("speed");

        StringBuilder sb = new StringBuilder();
        sb.append(" 天気: ").append(weatherMain).append("（").append(weatherDesc).append("）\n");
        sb.append(" 気温: ").append(temp).append("℃（体感 ").append(feelsLike).append("℃）\n");
        sb.append("湿度: ").append(humidity).append("%\n");
        sb.append(" 気圧: ").append(pressure).append(" hPa\n");
        sb.append(" 視程: ");
        if (visibility >= 0) {
            sb.append(String.format("%.1f km", visibility / 1000.0));
        } else {
            sb.append("情報なし");
        }
        sb.append("\n");
        sb.append(" 風速: ").append(windSpeed).append(" m/s");

        return sb.toString();
    }
}