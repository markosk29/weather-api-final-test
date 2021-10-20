package com.hackerrank.weather.repository;

import com.hackerrank.weather.entities.Weather;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface WeatherRepository extends JpaRepository<Weather, Integer> {
}
