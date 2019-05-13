package com.github.senyast4745.testModel;

import com.google.gson.annotations.SerializedName;

public class JsonSensorModel {

    @SerializedName("id")
    private long id;

    @SerializedName("coord1")
    private double coord1;

    @SerializedName("coord2")
    private double coord2;

    @SerializedName("temperature")
    private float temperature;

    @SerializedName("city")
    private String city;


    public long getId() {
        return id;
    }

    public double getCoord1() {
        return coord1;
    }

    public double getCoord2() {
        return coord2;
    }

    public float getTemperature() {
        return temperature;
    }

    public String getCity() {
        return city;
    }
}
