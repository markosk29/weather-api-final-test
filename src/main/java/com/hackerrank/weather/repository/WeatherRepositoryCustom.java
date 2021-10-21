package com.hackerrank.weather.repository;

import com.hackerrank.weather.output.WeatherJSON;

import java.util.List;

public interface WeatherRepositoryCustom {
    List<WeatherJSON> findAllCustom(String date, String city, String sort);
}
