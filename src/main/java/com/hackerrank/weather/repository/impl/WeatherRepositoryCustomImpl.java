package com.hackerrank.weather.repository.impl;

import com.hackerrank.weather.entities.Weather;
import com.hackerrank.weather.output.WeatherJSON;
import com.hackerrank.weather.repository.WeatherRepositoryCustom;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WeatherRepositoryCustomImpl implements WeatherRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<WeatherJSON> findAllCustom(String dateString, String city, String sort) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<WeatherJSON> criteriaQuery = criteriaBuilder.createQuery(WeatherJSON.class);
        final Root<Weather> weatherRoot = criteriaQuery.from(Weather.class);

        List<Order> orderList = new ArrayList<>();

        if ("date".equals(sort)) {
            orderList.add(criteriaBuilder.asc(weatherRoot.get("date")));
        } else if ("-date".equals(sort)) {
            orderList.add(criteriaBuilder.desc(weatherRoot.get("date")));
        }

        orderList.add(criteriaBuilder.asc(weatherRoot.get("id")));

        criteriaQuery.select(criteriaBuilder.construct(
                WeatherJSON.class,
                weatherRoot.get("id"),
                weatherRoot.get("date"),
                weatherRoot.get("lat"),
                weatherRoot.get("lon"),
                weatherRoot.get("city"),
                weatherRoot.get("state"),
                weatherRoot.joinList("temperatures", JoinType.INNER)))
                .orderBy(orderList);

        List<Predicate> predicates = getFilterPredicates(dateString, city, criteriaBuilder, weatherRoot);
        if (!predicates.isEmpty()) {
            criteriaQuery.where(predicates.toArray(new Predicate[]{}));
        }

        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    private List<Predicate> getFilterPredicates(String dateString, String city, CriteriaBuilder builder, Root<Weather> weatherRoot) {
        Predicate dateCondition = null;
        Predicate cityCondition = null;

        if (dateString != null) {
            LocalDate date = LocalDate.parse(dateString);

            dateCondition = builder.equal(weatherRoot.get("date"), date);
        }

        if (city != null) {
            city = city.toLowerCase();
            List<String> cityList = Arrays.stream(city.split(",")).map(String::trim).collect(Collectors.toList());

            cityCondition = builder.lower(weatherRoot.get("city")).in(cityList);
        }

        return Stream.of(dateCondition, cityCondition)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
