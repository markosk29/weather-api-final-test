package com.hackerrank.weather.entities;

import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "weather")
@Table(name = "weather")
public class Weather {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private int id;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "lat")
    private Float lat;

    @Column(name = "long")
    private Float lon;

    @Column(name = "city")
    private String city;

    @Column(name = "state")
    private String state;

    @ElementCollection
    @CollectionTable(name = "temperatures",
            joinColumns = {@JoinColumn(name = "weather_id", referencedColumnName = "id")})
    @Column(name = "temperature")
    private List<BigDecimal> temperatures;


}
