package com.example.harkkatyo;

import android.util.Log;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URL;

public class WeatherDataRetriever {
    private final String API_KEY = "b7770b753da7a9f17a1d9ea4e90719f7";
    private final String CONVERTER_BASE_URL = "https://api.openweathermap.org/geo/1.0/direct?q=%s&limit=5&appid=%s";
    private final String WEATHER_BASE_URL = "https://api.openweathermap.org/data/2.5/weather?lat=%s&lon=%s&lang=fi&units=metric&appid=%s";

    public WeatherData getWeatherData(String municipality) {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            JsonNode areas = objectMapper.readTree(new URL(String.format(CONVERTER_BASE_URL, municipality, API_KEY)));

            String latitude = areas.get(0).get("lat").asText();
            String longitude = areas.get(0).get("lon").asText();

            JsonNode weatherData = objectMapper.readTree(new URL(String.format(WEATHER_BASE_URL, latitude, longitude, API_KEY)));

            WeatherData wd = new WeatherData(
                    weatherData.get("name").asText(),
                    weatherData.get("weather").get(0).get("main").asText(),
                    weatherData.get("weather").get(0).get("description").asText(),
                    weatherData.get("main").get("temp").asText(),
                    weatherData.get("wind").get("speed").asText()
            );
            return wd;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public int getWeatherImageResource(String weatherMain) {
        switch (weatherMain) {
            case "Clear":
                return R.drawable.ic_sunny;
            case "Clouds":
                return R.drawable.ic_cloudy;
            case "Rain":
                return R.drawable.ic_rainy;
            case "Snow":
                return R.drawable.ic_snow;
            default:
                return R.drawable.ic_unknown_weather;
        }
    }
}
