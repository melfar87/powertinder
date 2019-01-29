package com.wgcorp.powertinder.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.wgcorp.powertinder.Constant.DEFAULT_DATE_FORMAT;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Person {
    @JsonProperty("_id")
    private String id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("gender")
    private String gender;

    @JsonProperty("distance_mi")
    private int distanceMi;

    @JsonProperty("ping_time")
    private String pingTime;

    @JsonProperty("birth_date")
    private String birthDate;

    @JsonProperty("bio")
    private String bio;

    @JsonProperty("photos")
    private List<Photo> photos;

    public int getAge() {
        return Period.between(LocalDate.parse(birthDate, DateTimeFormatter.ofPattern(DEFAULT_DATE_FORMAT)), LocalDate.now()).getYears();
    }

}
