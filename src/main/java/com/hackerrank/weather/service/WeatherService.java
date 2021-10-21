package com.hackerrank.weather.service;

import com.hackerrank.weather.entities.Weather;
import com.hackerrank.weather.mapper.WeatherMapper;
import com.hackerrank.weather.model.WeatherInput;
import com.hackerrank.weather.output.WeatherJSON;
import com.hackerrank.weather.repository.WeatherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.data.jpa.domain.Specification.*;

@Service
public class WeatherService {

    private final WeatherRepository weatherRepository;

    @Autowired
    public WeatherService(WeatherRepository weatherRepository) {
        this.weatherRepository = weatherRepository;
    }

    public WeatherJSON addWeather(WeatherInput weatherInput) {
        Weather weather = WeatherMapper.inputToEntity(weatherInput);

        weatherRepository.save(weather);

        return WeatherMapper.entityToJson(weather);
    }

    public WeatherJSON getWeather(Integer id) {
        Weather weather = weatherRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Weather entity not found!"));

        return WeatherMapper.entityToJson(weather);
    }

    public List<WeatherJSON> getWeatherInfo(String dateString, String city, String sort) {
       //return weatherRepository.findAllCustom(date, city, sort);

        List<Weather> weatherList;

        if (dateString != null && city != null) {
            LocalDate date = LocalDate.parse(dateString);
            city = city.toLowerCase();
            List<String> cityList = Arrays.stream(city.split(",")).map(String::trim).collect(Collectors.toList());

            weatherList = weatherRepository.findAll(where(dateCondition(date).and(cityCondition(cityList))));
        } else if (dateString != null) {
            LocalDate date = LocalDate.parse(dateString);

            weatherList = weatherRepository.findAll(dateCondition(date));
        } else if (city != null) {
            city = city.toLowerCase();
            List<String> cityList = Arrays.stream(city.split(",")).map(String::trim).collect(Collectors.toList());

            weatherList = weatherRepository.findAll(cityCondition(cityList));
        } else {
            weatherList = weatherRepository.findAll();
        }

        if ("date".equals(sort)) {
            weatherList.sort(Comparator.comparing(Weather::getDate).thenComparingInt(Weather::getId));
        } else if ("-date".equals(sort)) {
            weatherList.sort(Comparator.comparing(Weather::getDate).reversed().thenComparingInt(Weather::getId));
        } else {
            weatherList.sort(Comparator.comparingInt(Weather::getId));
        }

        return weatherList.stream().map(WeatherMapper::entityToJson).collect(Collectors.toList());
    }

    private Specification<Weather> dateCondition(LocalDate date) {
        return (weatherRoot, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(weatherRoot.get("date"), date);
    }

    private Specification<Weather> cityCondition(List<String> cityList) {
        return (weatherRoot, criteriaQuery, criteriaBuilder) -> criteriaBuilder.lower(weatherRoot.get("city")).in(cityList);
    }
}
