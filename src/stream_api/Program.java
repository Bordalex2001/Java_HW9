package stream_api;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Program {
	public static String fetchJsonFromUrl(String urlStr) throws Exception {
		StringBuilder result = new StringBuilder();
		@SuppressWarnings("deprecation")
		URL url = new URL(urlStr);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");

		try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
			String line;
			while ((line = reader.readLine()) != null) {
				result.append(line);
			}
		}

		return result.toString();
	}

	public static List<JsonObject> filterByCondition(JsonArray jsonArr, String key, String value) {
		List<JsonObject> filteredObjs = new ArrayList<>();

		for (JsonElement elem : jsonArr) {
			JsonObject obj = elem.getAsJsonObject();
			if (obj.has(key) && obj.get(key).getAsString().equals(value)) {
				filteredObjs.add(obj);
			}
		}

		return filteredObjs;
	}
	
	public static String formatDate(String dateValue) {
		try {
			DateTimeFormatter inputDateFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy");
			LocalDate date = LocalDate.parse(dateValue, inputDateFormat);
			DateTimeFormatter outputDateFormat = DateTimeFormatter.ofPattern("yyyyMMdd");
			return date.format(outputDateFormat);
		}
		catch (Exception ex) {
			System.err.println("Невірний формат дати: " + ex.getMessage());
			return null;
		}
	}

	public static void main(String[] args) {
		String dateKey = "exchangedate";
		String dateValue = "28.05.2024";
		
		String formattedDate = formatDate(dateValue);
		String urlStr = String.format("https://bank.gov.ua/NBUStatService/v1/statdirectory/exchange?date=%s&json", formattedDate);

		try {
			String jsonResponse = fetchJsonFromUrl(urlStr);

			JsonArray jsonArr = JsonParser.parseString(jsonResponse).getAsJsonArray();

			List<JsonObject> filteredObjs = filterByCondition(jsonArr, dateKey, dateValue);

			System.out.printf("Курс валют за %s:", dateValue);
			System.out.println();
			for (JsonObject obj : filteredObjs) {
				System.out.println(obj);
			}
		} catch (Exception ex) {
			System.err.println("Помилка завантаження даних: " + ex.getMessage());
		}
	}
}