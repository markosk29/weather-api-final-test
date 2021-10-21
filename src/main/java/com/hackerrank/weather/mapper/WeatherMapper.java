package com.hackerrank.weather.mapper;

import com.hackerrank.weather.entities.Weather;
import com.hackerrank.weather.model.WeatherInput;
import com.hackerrank.weather.output.WeatherJSON;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

public class WeatherMapper {

    public static Weather inputToEntity(WeatherInput weatherInput) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        return Weather.builder()
                .date(LocalDate.parse(weatherInput.getDate(), formatter))
                .lat(weatherInput.getLat())
                .lon(weatherInput.getLon())
                .city(weatherInput.getCity())
                .state(weatherInput.getState())
                .temperatures(weatherInput.getTemperatures().stream()
                        .map(BigDecimal::valueOf)
                        .collect(Collectors.toList()))
                .build();
    }

    public static WeatherJSON entityToJson(Weather weather) {
        return WeatherJSON.builder()
                .id(weather.getId())
                .date(weather.getDate().toString())
                .lat(weather.getLat())
                .lon(weather.getLon())
                .city(weather.getCity())
                .state(weather.getState())
                .temperatures(weather.getTemperatures().stream()
                        .map(BigDecimal::doubleValue)
                        .collect(Collectors.toList()))
                .build();
    }
}
