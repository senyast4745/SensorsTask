package com.github.senyast4745.controller;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.github.senyast4745.model.Sensor;

@JsonPropertyOrder({ "coord1", "coord2", "temp", "city" })
class SensorForm {

    private double coord1;
    private double coord2;
    private float temp;
    private String city;

    @JsonGetter("coord1")
    double getCoord1() {
        return coord1;
    }

    @JsonGetter("coord2")
    double getCoord2() {
        return coord2;
    }

    @JsonGetter("temp")
    float getTemp() {
        return temp;
    }

    @JsonGetter("city")
    String getCityName() {
        return city;
    }

    @JsonCreator
    SensorForm(@JsonProperty("coord1") double coord1,@JsonProperty("coord2") double coord2,
               @JsonProperty("temp") float temp,@JsonProperty("city") String city) {
        super();
        this.coord1 = coord1;
        this.coord2 = coord2;
        this.temp = temp;
        this.city = city;
    }

}
